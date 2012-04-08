/*
 *  @(#)BookingMessageData.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.message;

import org.jbundle.app.test.vet.db.Cat;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.message.MessageRecordDesc;
import org.jbundle.model.db.*;
import org.jbundle.model.message.MessageDataParent;


/**
 *  BookingMessageData - .
 */
public class CatMessageRecordDesc extends MessageRecordDesc
{
    /**
     * Default constructor.
     */
    public CatMessageRecordDesc()
    {
        super();
    }
    /**
     * BookingMessageData Method.
     */
    public CatMessageRecordDesc(MessageDataParent messageDataParent, String strKey)
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
        this.addMessageDataDesc(new VetMessageRecordDesc(this, "vet"));
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     */
    public int putRawRecordData(Rec record)
    {
        int iErrorCode = super.putRawRecordData(record);
        this.putRawFieldData(record.getField(Cat.NAME));
        this.putRawFieldData(record.getField(Cat.COLOR));
        this.putRawFieldData(record.getField(Cat.WEIGHT));
        return iErrorCode;
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int getRawRecordData(Rec record)
    {
        this.getRawFieldData(record.getField(Cat.NAME));
        this.getRawFieldData(record.getField(Cat.COLOR));
        this.getRawFieldData(record.getField(Cat.WEIGHT));
        return super.getRawRecordData(record);
    }
}
