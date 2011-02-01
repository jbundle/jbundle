/**
 *  @(#)BookingMessageData.
 *  Copyright © 2006 tourapp.com. All rights reserved.
 */
package org.jbundle.test.manual.test.message;

import org.jbundle.app.test.vet.db.Cat;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.message.MessageDataParent;
import org.jbundle.thin.base.message.MessageRecordDesc;


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
    public int putRawRecordData(FieldList record)
    {
        int iErrorCode = super.putRawRecordData(record);
        for (int iFieldSeq = Cat.kName; iFieldSeq <= Cat.kWeight; iFieldSeq++)
        {
            this.putRawFieldData(record.getField(iFieldSeq));
        }
        return iErrorCode;
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int getRawRecordData(FieldList record)
    {
        for (int iFieldSeq = Cat.kName; iFieldSeq <= Cat.kWeight; iFieldSeq++)
        {
            this.getRawFieldData(record.getField(iFieldSeq));
        }
        return super.getRawRecordData(record);
    }
}
