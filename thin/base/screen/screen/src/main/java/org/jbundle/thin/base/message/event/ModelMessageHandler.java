/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message.event;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.client.CachedRemoteTable;
import org.jbundle.thin.base.db.client.RemoteFieldTable;
import org.jbundle.thin.base.db.client.SyncRemoteTable;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.screen.AbstractThinTableModel;


/**
 * This is a special listener that waits for updates for the table model.
 * The (remote) message sender, only needs to supply the start and end index params to update.
 */
public class ModelMessageHandler extends BaseMessageListener
{
    /**
     * The param for a start index.
     */
    public static final String START_INDEX_PARAM = "startIndex";
    /**
     * The param for an end index.
     */
    public static final String END_INDEX_PARAM = "endIndex";
    /**
     * The target table model.
     */
    protected AbstractTableModel m_model = null;

    /**
     * Constructor.
     */
    public ModelMessageHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The target table model.
     */
    public ModelMessageHandler(AbstractTableModel model)
    {
        this();
        this.init(null, model);
    }
    /**
     * Constructor.
     * @param messageReceiver The message receiver that this listener is added to.
     * @param model The target table model.
     */
    public ModelMessageHandler(BaseMessageReceiver messageReceiver, AbstractTableModel model)
    {
        this();
        this.init(messageReceiver, model);
    }
    /**
     * Constructor.
     * @param messageReceiver The message receiver that this listener is added to.
     * @param model The target table model.
     */
    public void init(BaseMessageReceiver messageReceiver, AbstractTableModel model)
    {
        super.init(messageReceiver, null);
        m_model = model;
    }
    /**
     * Free this handler.
     */
    public void free()
    {
        m_model = null;
        super.free();
    }
    /**
     * Handle this message.
     * Update the model depending of the message.
     * @param message The message to handle.
     */
    public int handleMessage(BaseMessage message)
    {
        Map<String,Object> properties = new HashMap<String,Object>();
        String strMessageType =  (String)message.get(MessageConstants.MESSAGE_TYPE_PARAM);
        String strStartIndex = (String)message.get(START_INDEX_PARAM);
        String strEndIndex = (String)message.get(END_INDEX_PARAM);
        if (strMessageType != null)
            properties.put(MessageConstants.MESSAGE_TYPE_PARAM, strMessageType);
        if (strStartIndex != null)
            properties.put(START_INDEX_PARAM, strStartIndex);
        if (strEndIndex != null)
            properties.put(END_INDEX_PARAM, strEndIndex);
        Map<String,Object> propertiesHdr = null;
        if (message.getMessageHeader() != null)
            propertiesHdr = message.getMessageHeader().getProperties();
        if (propertiesHdr != null)
            properties.putAll(propertiesHdr); // Merge them to simplify access
        if (properties != null)
        {
            strMessageType =  (String)properties.get(MessageConstants.MESSAGE_TYPE_PARAM);
            try {
                int iMessageType = Integer.parseInt(strMessageType);
                if ((Constants.AFTER_ADD_TYPE == iMessageType)
                    || (Constants.AFTER_DELETE_TYPE == iMessageType)
                    || (Constants.AFTER_UPDATE_TYPE == iMessageType))
                {
                    SwingUtilities.invokeLater(new UpdateGridTable(properties));
                }
            } catch (NumberFormatException ex) {
                // Ignore
            }
        }
        return super.handleMessage(message);
    }
    /**
     * A message has been received to update the model at this row.
     * NOTE: DO NOT call this method directly; it is guaranteed to be in the awt thread and IS NOT general thread safe.
     * This method is public so you can override it and take action when an event arrives.
     * @param iMessageType
     * @param iStartIndex
     * @param iEndIndex
     */
    public void updateModel(int iMessageType, int iStartIndex, int iEndIndex)
    {
        FieldTable table = null;
        if (m_model instanceof AbstractThinTableModel)
        {
            if (((AbstractThinTableModel)m_model).getCurrentRow() == iStartIndex)
                ((AbstractThinTableModel)m_model).makeRowCurrent(-1, false);    // Invalidate the current row (and any changes)
            table = ((AbstractThinTableModel)m_model).getFieldTable();
            if (table instanceof RemoteFieldTable)
            {
                CachedRemoteTable remoteTable = (CachedRemoteTable)((RemoteFieldTable)table).getRemoteTableType(CachedRemoteTable.class);
                SyncRemoteTable syncRemoteTable = (SyncRemoteTable)((RemoteFieldTable)table).getRemoteTableType(SyncRemoteTable.class);
                if ((remoteTable != null) && (syncRemoteTable != null))
                {
                    Object objStartIndex = new Integer(iStartIndex);
                    synchronized (syncRemoteTable.getSyncObject())
                    {
                        remoteTable.setCache(objStartIndex, null);   // Clear this cache entry
                    }
                }
            }
            if (Constants.AFTER_ADD_TYPE == iMessageType)
            {
                if (((AbstractThinTableModel)m_model).getRowCount() == iStartIndex) // It is possible for this to be called twice (timing issue since in swing thread)
                    ((AbstractThinTableModel)m_model).bumpTableSize(+1, false); // I call row inserted in a few lines
                else
                    iMessageType = Constants.AFTER_UPDATE_TYPE;
            }
        }
        if (Constants.AFTER_UPDATE_TYPE == iMessageType)
            m_model.fireTableRowsUpdated(iStartIndex, iEndIndex);
        else if (Constants.AFTER_DELETE_TYPE == iMessageType)
            m_model.fireTableRowsUpdated(iStartIndex, iEndIndex); // Do not delete - leave a blank row in the table
        else if (Constants.AFTER_ADD_TYPE == iMessageType)
            m_model.fireTableRowsInserted(iStartIndex, iEndIndex);
    }
    /**
     * Update the screen record in the swing thread.
     */
    class UpdateGridTable extends Object
        implements Runnable
    {
        Map<String,Object> m_properties;
        
        /**
         * Constructor.
         */
        public UpdateGridTable(Map<String,Object> properties)
        {
            super();
            m_properties = properties;
        }
        /**
         * Run it!
         */
        public void run()
        {
            int iMessageType = 0;
            try {
                iMessageType = Integer.parseInt((String)m_properties.get(MessageConstants.MESSAGE_TYPE_PARAM));
            } catch (NumberFormatException ex) {
                // Ignore
            }
            String strStartIndex = (String)m_properties.get(START_INDEX_PARAM);
            String strEndIndex = (String)m_properties.get(END_INDEX_PARAM);
             // Tell the model that this record has changed... refresh the record.
            if ((strStartIndex != null)
                || (strEndIndex != null))
            {
                int iStartIndex = -1;
                try   {
                    iStartIndex = Integer.parseInt(strStartIndex);
                } catch (NumberFormatException ex)  {
                    iStartIndex = -1;
                }
                int iEndIndex = -1;
                try   {
                    iEndIndex = Integer.parseInt(strEndIndex);
                } catch (NumberFormatException ex)  {
                    iEndIndex = -1;
                }
                if (iEndIndex == -1)
                    iEndIndex = iStartIndex;
                if (iStartIndex == -1)
                    iStartIndex = iEndIndex;

                if (iStartIndex != -1)
                {
                    updateModel(iMessageType, iStartIndex, iEndIndex);
                }
            }
        }
    }
}
