/*
 *  @(#)BookingMessageData.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.message;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.message.MessageRecordDesc;
import org.jbundle.model.db.*;
import org.jbundle.model.message.MessageDataParent;

/**
 *  BookingMessageData - .
 */
public class CatListMessageRecordDesc extends MessageRecordDesc
{
    /**
     * Default constructor.
     */
    public CatListMessageRecordDesc()
    {
        super();
    }
    /**
     * BookingMessageData Method.
     */
    public CatListMessageRecordDesc(MessageDataParent messageDataParent, String strKey)
    {
        this();
        this.init(messageDataParent, strKey);
    }
    /**
     * Initialize class fields.
     */
    public void init(MessageDataParent messageDataParent, String strKey)
    {
        super.init(messageDataParent, strKey);
    }
    /**
     * 
     */
    public void setupMessageDataDesc()
    {
        super.setupMessageDataDesc();
        CatsMessageRecordDesc messageDataDesc = new CatsMessageRecordDesc(this, "cat");
        messageDataDesc.setNodeType(CatsMessageRecordDesc.NON_UNIQUE_NODE);
        this.addMessageDataDesc(messageDataDesc);
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     */
    public int putRawRecordData(Rec record)
    {
        return super.putRawRecordData(record);
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int getRawRecordData(Rec record)
    {
        int iErrorCode = Constants.NORMAL_RETURN;
        iErrorCode = super.getRawRecordData(record);
        return iErrorCode;
    }

}
