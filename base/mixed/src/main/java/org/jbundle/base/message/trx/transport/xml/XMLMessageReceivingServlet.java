/*
 * MessageReceivingServlet.

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.XmlTrxMessageIn;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.screen.control.servlet.BaseHttpTask;
import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.base.screen.control.servlet.BasicServlet.SERVLET_TYPE;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.control.servlet.xml.XMLServlet;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.Utility;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageStatusModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.TreeMessage;
import org.jbundle.thin.base.util.Application;


/**
 * Servlet that receives XML messages.
 */
public class XMLMessageReceivingServlet extends XMLServlet
    implements BasicServlet
//public class MessageReceivingServlet extends javax.servlet.http.HttpServlet
//    implements BasicServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * Initialize the servlet.
     * @param servletConfig The servlet configuration.
     */
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
        ServletTask.initServlet(this, BasicServlet.SERVLET_TYPE.XML);
        Enumeration<?> paramNames = this.getInitParameterNames();
        while (paramNames.hasMoreElements())
        {
            String strProperty = (String)paramNames.nextElement();
            String strValue = this.getInitParameter(strProperty);
            m_initProperties.put(strProperty, strValue);
        }
    }
    protected Map<String,Object> m_initProperties = new HashMap<String,Object>();
    /**
     * Destroy this Servlet and any active applications.
     * This is only called when all users are done using this Servlet.
     */
    public void destroy()
    {
        super.destroy();
        ServletTask.destroyServlet();
    }
    /**
     * Process an HTML get or post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void doProcess(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        Utility.getLogger().info("doProcess called in xmlws servlet");
        XMLMessageTransport xmlMessageTransport = null;
        ServletTask servletTask = null;
        BaseMessage msgReplyInternal = null;
        try {
            Map<String,Object> properties = m_initProperties;
        	String strDomain = BaseHttpTask.getParam(req, DBParams.DOMAIN);
        	if (strDomain != null)
        	{
		        properties = new HashMap<String,Object>();
		        if (m_initProperties != null)
		        	properties.putAll(m_initProperties);
				properties.put(DBParams.DOMAIN, strDomain);
        	}
            servletTask = new ServletTask(this, BasicServlet.SERVLET_TYPE.XML);
            servletTask.setProperties(properties);
            Environment env = null;
            if (servletTask.getApplication() != null)
                env = ((BaseApplication)servletTask.getApplication()).getEnvironment();
            if (env == null)
                env = Environment.getEnvironment(null);
            Application app = env.getMessageApplication(true, properties);
            servletTask.setApplication(app);

            xmlMessageTransport = new XMLMessageTransport(servletTask);
            InputStream inStream = req.getInputStream();	// .getReader();
            Reader in = new InputStreamReader(inStream);
            String message = Utility.transferURLStream(null, null, in, null);
            try   {
                message = URLDecoder.decode(message, DBConstants.URL_ENCODING);
            } catch (java.io.UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            //xMap<String,Object> properties = PropertiesField.stringToProperties(message);
            if (message.startsWith("xml="))	// Always
                message = message.substring(4);
            Map<String, Object> propMessage = null;
            if (message.length() == 0)
            {
            	Enumeration<?> names = req.getParameterNames();
            	while (names.hasMoreElements())
            	{
            		String key = names.nextElement().toString();
            		if ("xml".equalsIgnoreCase(key))
            			message = (String)req.getParameter(key);
            		else
            		{
            			if (propMessage == null)
            				propMessage = new HashMap<String, Object>();
            			propMessage.put(key, req.getParameter(key));	// RESTful params
            		}
            	}
            }

            BaseMessage messageIn = new TreeMessage(null, null);
            new XmlTrxMessageIn(messageIn, message);
            if (propMessage != null)
            {
            	for (String key : propMessage.keySet())
            	{
            		messageIn.put(key, propMessage.get(key));
            	}
            }

            msgReplyInternal = xmlMessageTransport.processIncomingMessage(messageIn, null);
            Utility.getLogger().info("msgReplyInternal: " + msgReplyInternal);

            int iErrorCode = xmlMessageTransport.convertToExternal(msgReplyInternal, null);
            Utility.getLogger().info("externalMessageReply: " + msgReplyInternal);

            if (iErrorCode == DBConstants.NORMAL_RETURN)
            {
            	if (msgReplyInternal.getExternalMessage() != null)
            	{	// Always
	                String strReply = msgReplyInternal.getExternalMessage().toString();
    	            res.getWriter().print(strReply);
        	        String strTrxID = (String)msgReplyInternal.getMessageHeader().get(TrxMessageHeader.LOG_TRX_ID);
            	    xmlMessageTransport.logMessage(strTrxID, msgReplyInternal, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.SENTOK, null, null);    // Sent (no reply required)
            	}
            }
        } catch (Throwable ex)  {
            ex.printStackTrace();
            String strError = "Error in processing or replying to a message";
            Utility.getLogger().warning(strError);
            if ((msgReplyInternal != null) && (xmlMessageTransport != null))
            {
                String strTrxID = (String)msgReplyInternal.getMessageHeader().get(TrxMessageHeader.LOG_TRX_ID);
                xmlMessageTransport.logMessage(strTrxID, msgReplyInternal, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.ERROR, strError, null);
            }
            return; // Error
        } finally   {
            if (xmlMessageTransport != null)
            {
                xmlMessageTransport.free();
                xmlMessageTransport = null;
            }
            if (servletTask != null)
                servletTask.free();
        }
    }
}
