/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy.transport;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.base.util.Utility;
import org.jbundle.base.util.XMLTags;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.util.base64.Base64;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * EncodedProxyTask The servlet to handle proxy messages (tunneled through http).
 */
public class AjaxProxyTask extends ProxyTask
{

    /**
     * Constructor.
     */
    public AjaxProxyTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public AjaxProxyTask(BasicServlet servlet, SERVLET_TYPE servletType)
    {
        this();
        this.init(servlet, servletType);
    }
    /**
     * Constructor.
     */
    public void init(BasicServlet servlet, SERVLET_TYPE servletType)
    {
        super.init(servlet, servletType);
    }
    /**
     * Utility to convert a map to a JSON object.
     * @param map
     * @return
     */
    public static JSONObject mapToJsonObject(Map<String, Object> map)
    {
        JSONObject jsonObj = new JSONObject();
        
        try {
            for (String key : map.keySet())
            {
                Object value = map.get(key);
                if (value instanceof Map)
                    jsonObj.put(key, AjaxProxyTask.mapToJsonObject((Map)value));
                else
                    jsonObj.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return jsonObj;
    }
    /**
     * Utility to convert a map to a JSON object.
     * @param mapDBParentProperties
     * @return
     */
    public static Map<String, Object> jsonObjectToMap(JSONObject jsonObj)
    {
        Map<String, Object> map = new Hashtable<String, Object>();
        
        Iterator<?> iterator = jsonObj.keys();
        while (iterator.hasNext())
        {
            try {
                String key = (String)iterator.next();
                String value = jsonObj.getString(key);  // TODO what if this is a JSONObject?
                map.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    /**
     * Convert the json Object to a string that can be eval(ed) in javascript.
     * @param jsonObj
     * @return
     */
    public static String jsonObjectToReturnString(JSONObject jsonObj)
    {
        String strReturn = jsonObj.toString();
        
        if (strReturn != null)
            if (!strReturn.startsWith("("))
                strReturn = "(" + strReturn + ")";
        
        return strReturn;
    }
    /**
     * Get the next (String) param.
     * Typically this is overidden in the concrete implementation.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public String getNextStringParam(InputStream in, String strName, Map<String, Object> properties)
    {
        String strValue = super.getNextStringParam(in, strName, properties);
        return strValue;
    }
    /**
     * Sent/send this return string.
     * @param out The return output stream.
     * @param strReturn The string to return.
     */
    public void setErrorReturn(PrintWriter out, RemoteException ex)
        throws RemoteException
    {
        out.println(Utility.startTag(XMLTags.STATUS_TEXT));
        String strMessage = ex.getLocalizedMessage();
        if (Utility.isNumeric(strMessage))
        {
            try {
                int iErrorCode = Integer.parseInt(strMessage);
                out.println(Utility.startTag(XMLTags.ERROR_CODE) + strMessage + Utility.endTag(XMLTags.ERROR_CODE));
                if (this.getTask() != null)
                    if (this.getTask().getApplication() != null)
                        strMessage = this.getTask().getApplication().getSecurityErrorText(iErrorCode);
            } catch (NumberFormatException ex2) {
                // Ignore
            }
        }
        out.println(Utility.startTag(XMLTags.TEXT) + strMessage + Utility.endTag(XMLTags.TEXT));
        out.println(Utility.startTag(XMLTags.ERROR) + "error" + Utility.endTag(XMLTags.ERROR));
        out.println(Utility.endTag(XMLTags.STATUS_TEXT));
        return;
    }
    /**
     * Sent/send this return string.
     * @param out The return output stream.
     * @param strReturn The string to return.
     */
    public void setReturnObject(PrintWriter out, Object objReturn)
    {
        String strReturn = null;
        if (objReturn instanceof Map)
        {
            Map<String, Object> map = (Map)objReturn;

            JSONObject jsonObj = AjaxProxyTask.mapToJsonObject(map);

            strReturn = AjaxProxyTask.jsonObjectToReturnString(jsonObj);
        }
        else if (objReturn instanceof BaseMessage)
        {
            BaseMessage message = (BaseMessage)objReturn;
            String strMessage = (String)message.get("message");
            BaseMessageHeader messageHeader = message.getMessageHeader();
            String strQueueName = messageHeader.getQueueName();
            String strQueueType = messageHeader.getQueueType();
            Integer intRegID = messageHeader.getRegistryIDMatch();
            JSONObject jsonObj = new JSONObject();
            
            try {
                jsonObj.put("message", strMessage);
                jsonObj.put(MessageConstants.QUEUE_NAME, strQueueName);
                jsonObj.put(MessageConstants.QUEUE_TYPE, strQueueType);
                if (intRegID != null)
                    jsonObj.put("id", intRegID.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            strReturn = AjaxProxyTask.jsonObjectToReturnString(jsonObj);
        }
        else if (objReturn instanceof BaseMessageFilter)
        {
            Integer intFilterID = ((BaseMessageFilter)objReturn).getFilterID();
            if (intFilterID != null)  // If registry ID is null.
                strReturn = intFilterID.toString();
            Integer intRegistryFilterID = ((BaseMessageFilter)objReturn).getRegistryID();
            if (intRegistryFilterID != null)  // Always
                strReturn = intRegistryFilterID.toString();
        }
        else if (objReturn instanceof RemoteException)
        {
            try {
                this.setErrorReturn(out, (RemoteException)objReturn);
                return;
            } catch (RemoteException ex) {
                // Never
            }
        }
        else
        {
//?        String strReturn = org.jbundle.thin.base.remote.proxy.transport.BaseTransport.convertObjectToString(objReturn);
//?        strReturn = org.jbundle.thin.base.remote.proxy.transport.Base64Encoder.encode(strReturn);
            if (objReturn != null)
                strReturn = objReturn.toString();
        }
        this.setReturnString(out, strReturn);
    }
    /**
     * Get the next (String) param.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public Object getNextObjectParam(InputStream in, String strName, Map<String, Object> properties)
    {
        String strParam = this.getNextStringParam(in, strName, properties);
        if (MESSAGE.equalsIgnoreCase(strName))
        {
            BaseMessageHeader messageHeader = new BaseMessageHeader("chat", "intranet", null, null);
            Map<String,Object> map = new Hashtable<String,Object>();
            map.put("message", strParam);
            BaseMessage message = new MapMessage(messageHeader, map);
            return message;
        }
        else if (FILTER.equals(strName))
        {
            BaseMessageFilter messageFilter = new BaseMessageFilter("chat", "intranet", null, null);
            Integer intID = new Integer(strParam);
            messageFilter.setFilterID(intID);
            return messageFilter;
        }
        else if (PROPERTIES.equals(strName))
        {
            if (strParam == null)
                return null;
            try {
                JSONObject jsonObj = new JSONObject(strParam);
                Map<String, Object> map = AjaxProxyTask.jsonObjectToMap(jsonObj);
                return map;
            } catch (JSONException e) {
                if (strParam != null)
                    if (strParam.startsWith("?"))
                {
                    Map<String, Object> map = new Hashtable<String, Object>();
                    Util.parseArgs(map, strParam);
                    return map;
                }
            }
        }

        strParam = Base64.decode(strParam);
        return org.jbundle.thin.base.remote.proxy.transport.BaseTransport.convertStringToObject(strParam);
    }
    /**
     * Process an HTML get or post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void doProcess(BasicServlet servlet, HttpServletRequest req, HttpServletResponse res, PrintWriter out) 
        throws ServletException, IOException
    {
        super.doProcess(servlet, req, res, out);
    }
}
