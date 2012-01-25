/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.jaxm;
/*
 * MessageReceivingServlet.
 */



import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.messaging.JAXMServlet;
import javax.xml.messaging.ReqRespListener;
import javax.xml.soap.SOAPMessage;

import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.SoapTrxMessageIn;
import org.jbundle.base.message.trx.transport.soap.SOAPMessageTransport;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.Utility;
import org.jbundle.model.App;
import org.jbundle.model.Task;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageStatusModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.TreeMessage;


/**
 * Servlet that receives SOAP messages.
 */
public class MessageReceivingServlet extends JAXMServlet
    implements /*SyncListener,*/ BasicServlet, ReqRespListener
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
        ServletTask.initServlet(this, BasicServlet.SERVLET_TYPE.MESSAGE);
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
    /*
    * This is the application code for handling the message.. Once the
    * message is received the application can retrieve the soap part, the
    * attachment part if there are any, or any other information from the
    * message.
    * @param message The incoming message to process.
    */
    public SOAPMessage onMessage(SOAPMessage message)
    {
        Utility.getLogger().info("onMessage called in receiving servlet");
        SOAPMessageTransport soapMessageTransport = null;
        ServletTask servletTask = null;
        BaseMessage msgReplyInternal = null;
        Map<String,Object> properties = m_initProperties;
        try {
        	if (message.getMimeHeaders() != null)
        	{
        		String[] rgstrTargetHost = message.getMimeHeaders().getHeader("host");
        		if (rgstrTargetHost != null)
        			if (rgstrTargetHost.length >= 1)
        			{
        		        properties = new HashMap<String,Object>();
        		        if (m_initProperties != null)
        		        	properties.putAll(m_initProperties);
        				properties.put(DBParams.DOMAIN, rgstrTargetHost[0]);
        				
        			}
        	}
            servletTask = new ServletTask(this, BasicServlet.SERVLET_TYPE.MESSAGE);
            servletTask.setProperties(properties);
            Environment env = null;
            if (servletTask.getApplication() != null)
                env = ((BaseApplication)servletTask.getApplication()).getEnvironment();
            if (env == null)
                env = Environment.getEnvironment(null);
            App app = env.getMessageApplication(true, properties);
            servletTask.setApplication(app);

            soapMessageTransport = new SOAPMessageTransport(servletTask);
            
            BaseMessage messageIn = new TreeMessage(null, null);
            new SoapTrxMessageIn(messageIn, message);

            msgReplyInternal = soapMessageTransport.processIncomingMessage(messageIn, null);
            Utility.getLogger().info("msgReplyInternal: " + msgReplyInternal);

            int iErrorCode = soapMessageTransport.convertToExternal(msgReplyInternal, null);
            Utility.getLogger().info("externalMessageReply: " + msgReplyInternal);
            SOAPMessage msg = null;//fac.createMessage();

            if (iErrorCode == DBConstants.NORMAL_RETURN)
            {
//?             Document doc = ((JAXBTrxMessageOut)msgReplyInternal.getExternalMessage()).getScratchDocument(); 
                msg = soapMessageTransport.setSOAPBody(msg, msgReplyInternal);
                String strTrxID = (String)msgReplyInternal.getMessageHeader().get(TrxMessageHeader.LOG_TRX_ID);
                soapMessageTransport.logMessage(strTrxID, msgReplyInternal, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.SENTOK, null, null);    // Sent (no reply required)
            }

            return msg;
        } catch (Throwable ex)  {
            ex.printStackTrace();
            String strError = "Error in processing or replying to a message";
            Utility.getLogger().warning(strError);
            if ((msgReplyInternal != null) && (soapMessageTransport != null))
            {
                String strTrxID = (String)msgReplyInternal.getMessageHeader().get(TrxMessageHeader.LOG_TRX_ID);
                soapMessageTransport.logMessage(strTrxID, msgReplyInternal, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.ERROR, strError, null);
            }
            return null;
        } finally   {
            if (soapMessageTransport != null)
            {
                soapMessageTransport.free();
                soapMessageTransport = null;
            }
            if (servletTask != null)
                servletTask.free();
        }
    }
    /**
     * Do any of the initial servlet stuff.
     * @param servletTask The calling servlet task.
     */
    public void initServletSession(Task servletTask)
    {
    }
    /**
     * Set the content type for this type of servlet.
     * (From the BasicServlet interface).
     * @param The http response to set.
     */
    public void setContentType(HttpServletResponse res)
    {
        // Not used for a JAXM servlet.
    }
    /**
     * Get the output stream.
     * (From the BasicServlet interface).
     * @param The http response to set.
     * @return The output stream.
     */
    public PrintWriter getOutputStream(HttpServletResponse res)
        throws IOException
    {
        return null;    // Not used for a JAXM servlet.
    }
    /**
     * Get the main screen (with the correct view factory!).
     * (From the BasicServlet interface).
     * @param parent The record owner parent.
     * @param recordMain The main record.
     * @param properties The properties for this screen.
     * @return The top screen.
     */
    public ComponentParent createTopScreen(Task task, Map<String,Object> properties)
    {
        return null;    // Not used for a JAXM servlet.
    }
    /**
     * Get the physical path for this internet path.
     * (From the BasicServlet interface).
     * @param request The http request.
     * @param strFileName The file name to find in this context.
     * @return The physical path to this file.
     */
    public String getRealPath(HttpServletRequest request, String strFilename)
    {
        return null;    // Not used for a JAXM servlet.
    }
}
