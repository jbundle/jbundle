/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.record;

/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
import java.io.Serializable;

import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.message.session.SessionMessageHeader;


/** 
 * A Record message is a message explaining the changes to this record.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class RecordMessage extends MapMessage
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * Creates new RecordMessage.
     */
    public RecordMessage()
    {
        super();
    }
    /**
     * Constructor.
     */
    public RecordMessage(BaseMessageHeader messageHeader)
    {
        this();
        this.init(messageHeader);
    }
    /**
     * Constructor.
     */
    public void init(BaseMessageHeader messageHeader)
    {
        super.init(messageHeader, null);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the raw data for this (xpath) key.
     * @param strParam  The xpath key.
     * @return The raw data at this location in the message.
     */
    public Object get(String strParam)
    {
        Object objValue = super.get(strParam);
        if (objValue == null)
            if (MessageConstants.MESSAGE_TYPE_PARAM.equals(strParam))
                objValue = Integer.toString(((RecordMessageHeader)this.getMessageHeader()).getRecordMessageType());
        return objValue;
    }
    /**
     * If you are sending a thick message to a thin client, convert it first.
     * Since BaseMessage is already, so conversion is necessary... return this message.
     * @return this message.
     */
    public BaseMessage convertToThinMessage()
    {
        int iChangeType = ((RecordMessageHeader)this.getMessageHeader()).getRecordMessageType();
        // See if this record is currently displayed or buffered, if so, refresh and display.

        Object data = this.getData();
        BaseMessage messageTableUpdate = null;
        // NOTE: The only way I will send this message to the client is if the ModelMessageHandler.START_INDEX_PARAM has been added to this message by the TableSession
//        if (properties.get(ModelMessageHandler.START_INDEX_PARAM) != null)
        {
            BaseMessageHeader messageHeader = new SessionMessageHeader(this.getMessageHeader().getQueueName(), this.getMessageHeader().getQueueType(), null, this);
            messageTableUpdate = new MapMessage(messageHeader, data);
            messageTableUpdate.put(MessageConstants.MESSAGE_TYPE_PARAM, Integer.toString(iChangeType));
        }
        return messageTableUpdate;
    }
}
