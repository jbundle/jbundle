/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.yml;

/**
 * @(#)YScreenField.java   0.00 12-Feb-97 Don Corley
 * HTML and XML Shared code.
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.html.AppletHtmlScreen;
import org.jbundle.base.screen.view.ScreenFieldViewAdapter;
import org.jbundle.base.screen.view.zml.ZScreenField;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.model.main.user.db.UserInfoModel;
import org.jbundle.model.screen.ScreenComponent;


/**
 * The base view for XML and HTML components.
 * All shared *ml handling should go here.
 * Note: Some of these methods are just utility methods since
 * the entire class tree cannot be shared between HTML and XML.
 */
public abstract class YScreenField extends ZScreenField
{
    /**
     * Constructor.
     */
    public YScreenField()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public YScreenField(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Display the applet html screen.
     * @exception DBException File exception.
     */
    public void printAppletHtmlScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
    	reg = ((BaseApplication)this.getTask().getApplication()).getResources("HtmlApplet", false);
    	
        Map<String,Object> propApplet = new Hashtable<String,Object>();

        Task task = this.getTask();
        Map<String,Object> properties = null;
        if (task instanceof ServletTask)
            properties = ((ServletTask)task).getRequestProperties(((ServletTask)task).getServletRequest(), true);
        else
            properties = task.getProperties();
        
        properties = ((AppletHtmlScreen)this.getScreenField()).getAppletProperties(propApplet, properties);

        out.println("<center>");
        
        char DEFAULT = ' ';
        char ch = ScreenFieldViewAdapter.getFirstToUpper(this.getProperty(DBParams.JAVA), DEFAULT);

        if (ch == DEFAULT)
            if (this.getTask() instanceof ServletTask)
        { // Default - 'P'lug-in/'A'pplet depends on browser
            ch = 'P'; // by Default - use plug-in
            HttpServletRequest req = ((ServletTask)this.getTask()).getServletRequest();
            Enumeration<?> e = req.getHeaderNames();
            while (e.hasMoreElements())
            {
                String name = (String)e.nextElement();
                String value = req.getHeader(name);
                if ((name != null) && (name.equalsIgnoreCase("User-Agent")) && (value != null))
                { // This is what I'm looking for... the browser type
                    value = value.toUpperCase();
                    if (value.indexOf("MOZILLA/5") != -1)
                        ch = UserInfoModel.WEBSTART.charAt(0); // Browser 5.x... Use plug-in (yeah!)
                    if (value.indexOf("MOZILLA/4") != -1)
                        ch = UserInfoModel.WEBSTART.charAt(0); // Browser 4.x... must use plug-in
                    if (value.indexOf("MSIE") != -1)
                        ch = UserInfoModel.WEBSTART.charAt(0); // Microsoft... must use plug-in
                    if (value.indexOf("WEBKIT") != -1)
                        ch = UserInfoModel.WEBSTART.charAt(0); // Chrome/Safari... must use plug-in
                    if (value.indexOf("CHROME") != -1)
                        ch = UserInfoModel.WEBSTART.charAt(0); // Chrome... must use plug-in
                    if (value.indexOf("SAFARI") != -1)
                        ch = UserInfoModel.WEBSTART.charAt(0); // Safari... must use plug-in
                    break;
                }
            }
        }

        if (ch != UserInfoModel.PLUG_IN.charAt(0))
        { // Not the plug-in, use jnlp applet tags
        	String strWebStartResourceName = this.getProperty("webStart");
        	if (strWebStartResourceName == null)
        		strWebStartResourceName = "webStart";
            String strApplet = reg.getString(strWebStartResourceName);
            String strJnlpURL = reg.getString(strWebStartResourceName + "Jnlp");
            
            if ((strApplet == null) || (strApplet.length() == 0))
            	strApplet = WEB_START_DEFAULT;
            StringBuilder sb = new StringBuilder(strApplet);
            
            StringBuffer sbParams = new StringBuffer();
            for (Map.Entry<String,Object> entry : properties.entrySet())
            {
                String strKey = (String)entry.getKey();
                Object objValue = entry.getValue();
                String strValue = null;
                if (objValue != null)
                    strValue = objValue.toString();
                if (strValue != null)
//x                    if (strValue.length() > 0)
                        sbParams.append(strKey + ":\"" + strValue + "\",\n");
            }
            if (propApplet.get(HtmlConstants.ARCHIVE) != null)
            	sbParams.append("archive:\"" + propApplet.get(HtmlConstants.ARCHIVE) + "\", \n");
            if (propApplet.get(HtmlConstants.NAME) != null)
                if (sbParams.indexOf("name:") == -1)
                	sbParams.append("name:\"" + propApplet.get(HtmlConstants.NAME) + "\", \n");
            if (propApplet.get(HtmlConstants.ID) != null)
            	sbParams.append("id:\"" + propApplet.get(HtmlConstants.ID) + "\", \n");
            if (sbParams.indexOf("hash:") == -1)
            	sbParams.append("hash:location.hash,\n");
            if (sbParams.indexOf("draggable:") == -1)
            	sbParams.append("draggable:true,");
            Utility.replace(sb, "{other}", sbParams.toString());

            strJnlpURL = Utility.encodeXML(this.getJnlpURL(strJnlpURL, propApplet, properties));
            Utility.replace(sb, "{jnlpURL}", strJnlpURL);

            Utility.replaceResources(sb, reg, propApplet, null);

            out.println(sb.toString());
        }
        else
        {
            out.println("<OBJECT classid=\"clsid:8AD9C840-044E-11D1-B3E9-00805F499D93\"");
            out.println(" width=\"" + propApplet.get(HtmlConstants.WIDTH) + "\"");
            out.println(" height=\"" + propApplet.get(HtmlConstants.HEIGHT) + "\"");
            if (propApplet.get(HtmlConstants.NAME) != null)
                out.println(" name=\"" + propApplet.get(HtmlConstants.NAME) + "\"");
            out.println(" codebase=\"http://java.sun.com/update/1.5.0/jinstall-1_6_0-windows-i586.cab#Version=1,6,0,0\">");
            if (propApplet.get(HtmlConstants.ARCHIVE) != null)
                out.println("<param name=\"ARCHIVE\" value=\"" + propApplet.get(HtmlConstants.ARCHIVE) + "\">");
            out.println("<param name=\"CODE\" value=\"" + propApplet.get(DBParams.APPLET) +  "\">");
            out.println("<param name=\"CODEBASE\" value=\"" + propApplet.get(HtmlConstants.CODEBASE) + "\">");
            
            for (Map.Entry<String,Object> entry : properties.entrySet())
            {
                String strKey = (String)entry.getKey();
                Object objValue = entry.getValue();
                String strValue = null;
                if (objValue != null)
                    strValue = objValue.toString();
                if (strValue != null)
                    if (strValue.length() > 0)
                        out.println("<param name=\"" + strKey + "\" value=\"" + strValue + "\">");
            }
    
            out.println("<param name=\"type\" value=\"application/x-java-applet;version=1.6.0\">");
            out.println("<COMMENT>");
            out.println("<EMBED type=\"application/x-java-applet;version=1.6\"");

            if (propApplet.get(HtmlConstants.ARCHIVE) != null)
                out.println(" java_ARCHIVE=\"" + propApplet.get(HtmlConstants.ARCHIVE) + "\"");
            out.println(" java_CODE=\"" + propApplet.get(DBParams.APPLET) + "\"");
            out.println(" width=\"" + propApplet.get(HtmlConstants.WIDTH) + "\"");
            out.println(" height=\"" + propApplet.get(HtmlConstants.HEIGHT) + "\"");
            if (propApplet.get(HtmlConstants.NAME) != null)
                out.println(" name=\"" + propApplet.get(HtmlConstants.NAME) + "\"");
            out.println(" java_CODEBASE=\"" + propApplet.get(HtmlConstants.CODEBASE) + "\"");
            
            for (Map.Entry<String,Object> entry : properties.entrySet())
            {
                String strKey = (String)entry.getKey();
                Object objValue = entry.getValue();
                String strValue = null;
                if (objValue != null)
                    strValue = objValue.toString();
                if (strValue != null)
                    if (strValue.length() > 0)
                        out.println(" " + strKey + " =\"" + strValue + "\"");
            }
    
            out.println(" pluginspage=\"http://java.sun.com/javase/downloads/ea.jsp\"><NOEMBED></COMMENT>");
            out.println(" alt=\"Your browser understands the &lt;APPLET&gt; tag but is not running the applet, for some reason.\"");
            out.println(" Your browser is completely ignoring the &lt;APPLET&gt; tag!");
            out.println("</NOEMBED></EMBED>");
            out.println("</OBJECT>");
        }
        out.println("</center>");
    }
}
