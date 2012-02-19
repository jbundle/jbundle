/*
 * TaskHolder.java
 *
 * Created on November 16, 2002, 8:53 PM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTable;


/**
 *
 * @author  don
 */
public class TableHolder extends SessionHolder
{

    /**
     * Creates a new instance of TaskHolder
     */
    public TableHolder()
    {
        super();
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public TableHolder(BaseHolder parentHolder, RemoteTable remoteObject)
    {
        this();
        this.init(parentHolder, remoteObject);
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public void init(BaseHolder parentHolder, RemoteDatabase remoteObject)
    {
        super.init(parentHolder, remoteObject);    // TaskHolder doesn't have a parent (BaseHolder).
    }
    /**
     * Free the resources for this holder.
     */
    public void free()
    {
        super.free();
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
        try {
            if (OPEN.equals(strCommand))
            {
                String strKeyArea = this.getNextStringParam(in, KEY, properties);
                int iOpenMode = this.getNextIntParam(in, MODE, properties);
                boolean bDirection = this.getNextBooleanParam(in, DIRECTION, properties);
                String strFields = this.getNextStringParam(in, FIELDS, properties);
                Object objInitialKey = this.getNextObjectParam(in, INITIAL_KEY, properties);
                Object objEndKey = this.getNextObjectParam(in, END_KEY, properties);
                byte[] byBehaviorData = (byte[])this.getNextObjectParam(in, BEHAVIOR_DATA, properties);
                ((RemoteTable)m_remoteObject).open(strKeyArea, iOpenMode, bDirection, strFields, objInitialKey, objEndKey, byBehaviorData);
            }
            else if (ADD.equals(strCommand))
            {
                Object objData = this.getNextObjectParam(in, DATA, properties);
                int iOpenMode = this.getNextIntParam(in, MODE, properties);
                Object objReturn = ((RemoteTable)m_remoteObject).add(objData, iOpenMode);
                this.setReturnObject(out, objReturn);
            }
            else if (EDIT.equals(strCommand))
            {
                int iOpenMode = this.getNextIntParam(in, MODE, properties);
                int iErrorCode = ((RemoteTable)m_remoteObject).edit(iOpenMode);
                this.setReturnObject(out, new Integer(iErrorCode));
            }
            else if (SET.equals(strCommand))
            {
                Object objData = this.getNextObjectParam(in, DATA, properties);
                int iOpenMode = this.getNextIntParam(in, MODE, properties);
                ((RemoteTable)m_remoteObject).set(objData, iOpenMode);
            }
            else if (REMOVE.equals(strCommand))
            {
                Object objData = this.getNextObjectParam(in, DATA, properties);
                int iOpenMode = this.getNextIntParam(in, MODE, properties);
                ((RemoteTable)m_remoteObject).remove(objData, iOpenMode);
            }
            else if (DO_MOVE.equals(strCommand))
            {
                int iRelPosition = this.getNextIntParam(in, POSITION, properties);
                int iCount = this.getNextIntParam(in, COUNT, properties);
                Object objReturn = ((RemoteTable)m_remoteObject).doMove(iRelPosition, iCount);
                this.setReturnObject(out, objReturn);
            }
            else if (SEEK.equals(strCommand))
            {
                String strSeekSign = this.getNextStringParam(in, SIGN, properties);
                int iOpenMode = this.getNextIntParam(in, MODE, properties);
                String strKeyArea = this.getNextStringParam(in, KEY, properties);
                String strFields = this.getNextStringParam(in, FIELDS, properties);
                Object objKeyData = this.getNextObjectParam(in, KEY_DATA, properties);
                Object objReturn = ((RemoteTable)m_remoteObject).seek(strSeekSign, iOpenMode, strKeyArea, strFields, objKeyData);
                this.setReturnObject(out, objReturn);
            }
            else if (DO_SET_HANDLE.equals(strCommand))
            {
                Object bookmark = this.getNextObjectParam(in, BOOKMARK, properties);
                int iOpenMode = this.getNextIntParam(in, MODE, properties);
                String strFields = this.getNextStringParam(in, FIELDS, properties);
                int iHandleType = this.getNextIntParam(in, TYPE, properties);
                Object objReturn = ((RemoteTable)m_remoteObject).doSetHandle(bookmark, iOpenMode, strFields, iHandleType);
                this.setReturnObject(out, objReturn);
            }
            else if (GET_LAST_MODIFIED.equals(strCommand))
            {
                int iHandleType = this.getNextIntParam(in, TYPE, properties);
                Object objReturn = ((RemoteTable)m_remoteObject).getLastModified(iHandleType);
                this.setReturnObject(out, objReturn);
            }
            else if (GET.equals(strCommand))
            {
                int iRowIndex = this.getNextIntParam(in, INDEX, properties);
                int iRowCount = this.getNextIntParam(in, COUNT, properties);
                Object objReturn = ((RemoteTable)m_remoteObject).get(iRowIndex, iRowCount);
                this.setReturnObject(out, objReturn);
            }
            else if (SET_REMOTE_PROPERTY.equals(strCommand))
            {
                String strProperty = this.getNextStringParam(in, KEY, properties);
                String strValue = this.getNextStringParam(in, VALUE, properties);
                ((RemoteTable)m_remoteObject).setRemoteProperty(strProperty, strValue);
            }
            else if (MAKE_FIELD_LIST.equals(strCommand))
            {
                String strFieldsToInclude = this.getNextStringParam(in, FIELDS, properties);
                Object objReturn = ((RemoteTable)m_remoteObject).makeFieldList(strFieldsToInclude);
                this.setReturnObject(out, objReturn);
            }
            else if (GET_REMOTE_DATABASE.equals(strCommand))
            {
                Map<String,Object> propIn = this.getNextPropertiesParam(in, PROPERTIES, properties);
                if (propIn != null)
                    properties.putAll(propIn);
                RemoteDatabase remoteDatabase = ((RemoteTable)m_remoteObject).getRemoteDatabase(properties);
                // First, see if this is in my list already?
                String strDBID = this.find(remoteDatabase);
                if (strDBID == null)
                {
                    strDBID = this.add(new DatabaseHolder((TaskHolder)this.getParentHolder(), remoteDatabase));
                }
                this.setReturnString(out, strDBID);
            }
            else
                super.doProcess(in, out, properties);
        } catch (DBException ex)    {
            this.setReturnObject(out, ex);
        } catch (Exception ex)    {
            this.setReturnObject(out, ex);
        }
    }
}
