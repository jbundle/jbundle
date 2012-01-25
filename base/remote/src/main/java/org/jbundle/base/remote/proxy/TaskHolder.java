/*
 * TaskHolder.java
 *
 * Created on November 16, 2002, 8:53 PM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.base.remote.db.TaskSession;
import org.jbundle.base.remote.proxy.transport.ProxyTask;
import org.jbundle.base.util.Utility;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteReceiveQueue;
import org.jbundle.thin.base.remote.RemoteSendQueue;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.remote.proxy.ProxyConstants;
import org.jbundle.thin.base.util.Application;


/**
 *
 * @author  don
 */
public class TaskHolder extends BaseSessionHolder
    implements ProxyConstants
{
    /**
     * The servlet task that called me.
     */
    protected ProxyTask m_proxyTask = null;

    /**
     * Creates a new instance of TaskHolder
     */
    public TaskHolder()
    {
        super();
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public TaskHolder(ProxyTask proxyTask, RemoteTask remoteTask)
    {
        this();
        this.init(proxyTask, remoteTask);
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public void init(ProxyTask proxyTask, RemoteTask remoteTask)
    {
        super.init(null, remoteTask);    // TaskHolder doesn't have a parent (BaseHolder).
        m_proxyTask = proxyTask;
    }
    /**
     * Free the resources for this holder.
     */
    public void free()
    {
        super.free();
        if (m_proxyTask != null)
            m_proxyTask.remove(this);
        m_proxyTask = null;
    }
    /**
     * Handle the command send from my client peer.
     * @param in The (optional) Inputstream to get the params from.
     * @param out The stream to write the results.
     */
    public void doProcess(InputStream in, PrintWriter out, Map<String, Object> properties)
        throws RemoteException
    {
        String strCommand = this.getProperty(REMOTE_COMMAND, properties);
        if (MAKE_REMOTE_TABLE.equals(strCommand))
        {
            String strRecordClassName = this.getNextStringParam(in, NAME, properties);
            String strTableSessionClassName = this.getNextStringParam(in, SESSION_CLASS_NAME, properties);
            Map<String,Object> propIn = this.getNextPropertiesParam(in, PROPERTIES, properties);
            Map<String,Object> propDatabase = this.getNextPropertiesParam(in, PROPERTIES_DB, properties);
            if (propIn != null)
                properties.putAll(propIn);
            RemoteTable remoteTable = ((RemoteTask)m_remoteObject).makeRemoteTable(strRecordClassName, strTableSessionClassName, properties, propDatabase);
            String strTableID = this.add(new TableHolder(this, remoteTable));
            this.setReturnString(out, strTableID);
        }
        else if (CREATE_REMOTE_TASK.equals(strCommand))
        {   // Note: I am not the parent, the proxyTask (the application task) is.
            Map<String,Object> propIn = this.getNextPropertiesParam(in, PROPERTIES, properties);
            if (propIn != null)
                properties.putAll(propIn);
            Application app = ((TaskSession)this.getRemoteObject()).getApplication();
            RemoteTask remoteTask = m_proxyTask.getNewRemoteTask(app, properties);  // Parent is MY APPLICATION not ME.
            String strUniqueID = m_proxyTask.add(new TaskHolder(m_proxyTask, remoteTask));  // Parent is Parent Proxy not ME.
            out.write(strUniqueID);
        }
        else if (CREATE_REMOTE_RECEIVE_QUEUE.equals(strCommand))
        {
            String strQueueName = this.getNextStringParam(in, MessageConstants.QUEUE_NAME, properties);
            String strQueueType = this.getNextStringParam(in, MessageConstants.QUEUE_TYPE, properties);
            RemoteReceiveQueue remoteReceiveQueue = ((RemoteTask)m_remoteObject).createRemoteReceiveQueue(strQueueName, strQueueType);
            String strID = this.find(remoteReceiveQueue);
            if (strID == null)
                strID = this.add(new ReceiveQueueHolder(this, remoteReceiveQueue));
            this.setReturnString(out, strID);
        }
        else if (CREATE_REMOTE_SEND_QUEUE.equals(strCommand))
        {
            String strQueueName = this.getNextStringParam(in, MessageConstants.QUEUE_NAME, properties);
            String strQueueType = this.getNextStringParam(in, MessageConstants.QUEUE_TYPE, properties);
            RemoteSendQueue remoteSendQueue = ((RemoteTask)m_remoteObject).createRemoteSendQueue(strQueueName, strQueueType);
            String strID = this.find(remoteSendQueue);
            if (strID == null)
                strID = this.add(new SendQueueHolder(this, remoteSendQueue));
            this.setReturnString(out, strID);
        }
        else if (SET_REMOTE_MESSAGE_TASK.equals(strCommand))
        {
            String strTaskID = this.getNextStringParam(in, ID, properties);
            BaseHolder sessionHolder = this.getSessionFromPath(strTaskID);
            if (sessionHolder instanceof TaskHolder)
            {       // Always
                RemoteTask remoteTask = (RemoteTask)sessionHolder.getRemoteObject();
                ((RemoteTask)m_remoteObject).setRemoteMessageTask(remoteTask);
            }
            else
                Utility.getLogger().warning("Remote session not found");
        }
        else if (LOGIN.equals(strCommand))
        {
            String strUserName = this.getNextStringParam(in, Params.USER_NAME, properties);
            String strPassword = this.getNextStringParam(in, Params.PASSWORD, properties);
            String strDomain = this.getNextStringParam(in, Params.DOMAIN, properties);
            Map<String,Object> mapLoginInfo = ((RemoteTask)m_remoteObject).login(strUserName, strPassword, strDomain);
            // First, see if this is in my list already?
            this.setReturnObject(out, mapLoginInfo);
        }
        else
            super.doProcess(in, out, properties);
    }
    /**
     * Get the servlet's property.
     * For Ajax proxies, the top level proxy is shared among sessions. since it is not unique, don't return property. 
     */
    public String getProperty(String strKey, Map<String, Object> properties)
    {
        String strProperty = super.getProperty(strKey, properties);
        if (strProperty == null)
            if (!m_proxyTask.isShared())
                strProperty = m_proxyTask.getProperty(strKey);
        return strProperty;
    }
    /**
     * Get the next (String) param.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public String getNextStringParam(InputStream in, String strName, Map<String, Object> properties)
    {
        return m_proxyTask.getNextStringParam(in, strName, properties);
    }
    /**
     * Get the next (String) param.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public boolean getNextBooleanParam(InputStream in, String strName, Map<String, Object> properties)
    {
        return m_proxyTask.getNextBooleanParam(in, strName, properties);
    }
    /**
     * Get the next (String) param.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public int getNextIntParam(InputStream in, String strName, Map<String, Object> properties)
    {
        return m_proxyTask.getNextIntParam(in, strName, properties);
    }
    /**
     * Get the next (String) param.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public Map<String, Object> getNextPropertiesParam(InputStream in, String strName, Map<String, Object> properties)
    {
        return m_proxyTask.getNextPropertiesParam(in, strName, properties);
    }
    /**
     * Get the next (String) param.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public Object getNextObjectParam(InputStream in, String strName, Map<String, Object> properties)
    {
        return m_proxyTask.getNextObjectParam(in, strName, properties);
    }
    /**
     * Sent/send this return string.
     * @param out The return output stream.
     * @param strReturn The string to return.
     */
    public void setReturnString(PrintWriter out, String strReturn)
    {
        m_proxyTask.setReturnString(out, strReturn);
    }
    /**
     * Sent/send this return string.
     * @param out The return output stream.
     * @param strReturn The string to return.
     */
    public void setReturnObject(PrintWriter out, Object objReturn)
    {
        m_proxyTask.setReturnObject(out, objReturn);
    }
    /**
     * Get the session with this path (This method must be called from the ProxyTask).
     * @param out The return output stream.
     * @param strReturn The string to return.
     */
    public BaseHolder getSessionFromPath(String strSessionPathID)
    {
        return m_proxyTask.getSessionFromPath(strSessionPathID);
    }
}
