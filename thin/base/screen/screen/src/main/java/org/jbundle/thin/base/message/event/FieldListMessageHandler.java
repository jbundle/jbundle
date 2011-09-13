/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message.event;

import javax.swing.SwingUtilities;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MessageConstants;


/**
 * This is a generalized listener that processes incoming messages for a JBaseScreen.
 */
public class FieldListMessageHandler extends BaseMessageListener
{
    /**
     * The target JBaseScreen.
     */
    protected transient FieldList m_record = null;

    /**
     * Constructor.
     */
    public FieldListMessageHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param screen The target JBaseScreen.
     */
    public FieldListMessageHandler(FieldList record)
    {
        this();
        this.init(record, null, null);
    }
    /**
     * Constructor.
     * @param screen The target JBaseScreen.
     */
    public FieldListMessageHandler(FieldList record, BaseMessageFilter messageFilter)
    {
        this();
        this.init(record, null, messageFilter);
    }
    /**
     * Constructor.
     * @param screen The target JBaseScreen.
     */
    public FieldListMessageHandler(FieldList record, BaseMessageReceiver messageReceiver)
    {
        this();
        this.init(record, messageReceiver, null);
    }
    /**
     * Constructor.
     * @param screen The target JBaseScreen.
     */
    public void init(FieldList record, BaseMessageReceiver messageReceiver, BaseMessageFilter messageFilter)
    {
        super.init(messageReceiver, messageFilter);
        m_record = record;
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Handle this message.
     * Basically, if I get a message that the current record changed, I re-read the record.
     * @param The message to handle.
     * @return An error code.
     */
    public int handleMessage(BaseMessage message)
    {
            try   {
                int iMessageType = Integer.parseInt((String)message.get(MessageConstants.MESSAGE_TYPE_PARAM));
                if ((iMessageType == Constants.AFTER_UPDATE_TYPE)
                        || (iMessageType == Constants.CACHE_UPDATE_TYPE))
                    { // Record updated - be very careful, you are running in an independent thread.
                        SwingUtilities.invokeLater(new UpdateScreenRecord(iMessageType));
                    }
            } catch (NumberFormatException ex)  {
                // Ignore
            }
        return super.handleMessage(message);
    }
    /**
     * A message has been received to update the model at this row.
     * NOTE: DO NOT call this method directly; it is guaranteed to be in the awt thread and IS NOT general thread safe.
     * This method is public so you can override it and take action when an event arrives.
     * @param iMessageType
     */
    public void updateScreen(int iMessageType)
    {
        FieldList record = m_record;
        if (record.getEditMode() == Constants.EDIT_CURRENT)
        {
            try   {
                Object bookmark = record.getField(0).getData();
                FieldTable table = record.getTable();
                if (bookmark != null)
                    if (bookmark instanceof Integer)
                {
                    if (table instanceof org.jbundle.thin.base.db.client.RemoteFieldTable)  // Always
                    {
                        org.jbundle.thin.base.db.client.CachedRemoteTable remoteTable = (org.jbundle.thin.base.db.client.CachedRemoteTable)((org.jbundle.thin.base.db.client.RemoteFieldTable)table).getRemoteTableType(org.jbundle.thin.base.db.client.CachedRemoteTable.class);
                        if (remoteTable != null)
                            remoteTable.setCache(bookmark, null);   // Clear this cache entry
                    }
                    record.refreshToCurrent(iMessageType, false);
                }
            } catch (Exception ex)  {
                ex.printStackTrace();
            }
        }        
    }
    /**
     * Update the screen record in the swing thread.
     */
    class UpdateScreenRecord extends Object
        implements Runnable
    {
        private int m_iMessageType = -1;
        
        /**
         * Constructor.
         */
        public UpdateScreenRecord(int iMessageType)
        {
            super();
            m_iMessageType = iMessageType;
        }
        /**
         * Run it!
         */
        public void run()
        {
            updateScreen(m_iMessageType);
        }
    }
}
