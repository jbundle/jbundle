/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CopyLastHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.message.record.RecordMessageHeader;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.util.Application;


/**
 * Wait until this field changes to return (or timeout).
 * <code>
 * WaitForFieldChangeHandler listener = new WaitForFieldChangeHandler(iTimeoutMS);
 * recTour.getField(Tour.kTourStatusID).addListener(listener);
 * recTour.refreshToCurrent(DBConstants.AFTER_UPDATE_TYPE, false); // Start with the most recent version
 * int iErrorCode = listener.waitForChange();
 * if (iErrorCode == WaitForFieldChangeHandler.TIMEOUT_ERROR)
 * </code>
 * Note: You should do a refresh to current on the record to make sure you have the current value before you start.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class WaitForFieldChangeHandler extends FieldListener
{
    public static final int TIMEOUT_ERROR = -1;

    private boolean m_bWaiting = false;        
    protected long m_lTimeOut = 0;
    protected WaitForFieldChangeMessageListener m_messageListener = null;

    /**
     * Constructor.
     */
    public WaitForFieldChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param owner The basefield owner of this listener (usually null and set on setOwner()).
     */
    public WaitForFieldChangeHandler(long lTimeOut)
    {
        this();
        this.init(lTimeOut);
    }
    /**
     * Constructor.
     * @param owner The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(long lTimeOut)
    {
        super.init(null);
        m_lTimeOut = lTimeOut;
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        if (owner == null)
        {
            if (m_messageListener != null)
            {
                m_messageListener.free();
                m_messageListener = null;
            }
        }
        super.setOwner(owner);
        if (owner != null)
        {
            Record record = this.getOwner().getRecord();
            BaseMessageManager messageManager = ((Application)record.getTask().getApplication()).getMessageManager();
            if (messageManager != null)
            {
                BaseMessageFilter messageFilter = new BaseMessageFilter(MessageConstants.TRX_RETURN_QUEUE, MessageConstants.INTERNET_QUEUE, this, null);
                messageManager.addMessageFilter(messageFilter);
                m_messageListener = new  WaitForFieldChangeMessageListener(messageFilter, this);
                record.setupRecordListener(m_messageListener, false, false);   // I need to listen for record changes
            }
        }            
    }
    /**
     * The Field has Changed.
     * Don't need to call inherited.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        if (m_bWaiting)
        {
            synchronized (this)
            {
                m_bWaiting = false;
                this.notify();
            }
        }
        return super.fieldChanged(bDisplayOption, iMoveMode);
    }
    /**
     * Set the timeout value in ms.
     * @param iTimeOut
     */
    public void setTimeout(long lTimeOut)
    {
        m_lTimeOut = lTimeOut;
    }
    /**
     * Wait for this field to change.
     * @return Normal return if changed, TIMEOUT if timeout.
     */
    public int waitForChange()
    {
        if (m_lTimeOut <= 0)
            return TIMEOUT_ERROR;   // Timed out                
        m_bWaiting = false;        
        synchronized(this)
        {
            long lStartTime = System.currentTimeMillis();
            try {
                m_bWaiting = true;
                this.wait(m_lTimeOut);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            m_lTimeOut = m_lTimeOut - (System.currentTimeMillis() - lStartTime);
        }
        if (m_bWaiting == false)
            return DBConstants.NORMAL_RETURN;   // Was interrupted by a field change
        m_bWaiting = false;        
        return TIMEOUT_ERROR;   // Timed out
    }
        /**
         * Handle record message changes.
         * @author don
         */
        class WaitForFieldChangeMessageListener extends BaseMessageListener
        {
            protected WaitForFieldChangeHandler m_listener = null;
            
            /**
             * Constructor.
             */
            public WaitForFieldChangeMessageListener()
            {
                super();
            }
            /**
             * Constructor.
             */
            public WaitForFieldChangeMessageListener(BaseMessageFilter messageFilter, WaitForFieldChangeHandler listener)
            {
                this();
                this.init(null, messageFilter, listener);
            }
            /**
             * Constructor.
             */
            public void init(BaseMessageReceiver messageReceiver, BaseMessageFilter messageFilter, WaitForFieldChangeHandler listener)
            {
                this.init(messageReceiver, messageFilter);
                m_listener = listener;
            }
            /**
             * Handle the record changed message.
             */
           public int handleMessage(BaseMessage message)
            {
                RecordMessageHeader messageHeader = (RecordMessageHeader)message.getMessageHeader();
                Record record = m_listener.getOwner().getRecord();
                if (messageHeader.isRecordMatch(record))    // Double-check to make sure this is the right message
                    record.refreshToCurrent(DBConstants.AFTER_UPDATE_TYPE, false);  // This will cause a field changed event if it changed.
                return super.handleMessage(message); // Override this to process change
            }
        }
}
