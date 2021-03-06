/*
 *  @(#)BookingMessageData.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.message;

import org.jbundle.app.test.vet.db.Cat;
import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.message.MessageRecordDesc;
import org.jbundle.model.db.*;
import org.jbundle.model.message.MessageDataParent;


/**
 *  BookingMessageData - .
 */
public class VetMessageRecordDesc extends MessageRecordDesc
{
    /**
     * Default constructor.
     */
    public VetMessageRecordDesc()
    {
        super();
    }
    /**
     * BookingMessageData Method.
     */
    public VetMessageRecordDesc(MessageDataParent messageDataParent, String strKey)
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
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     */
    public int putRawRecordData(Rec record)
    {
        int iErrorCode = super.putRawRecordData(record);
        FieldList vet = ((ReferenceField)record.getField(Cat.VET)).getReference();
        this.putRawFieldData(vet.getField(Vet.NAME));
        return iErrorCode;
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int getRawRecordData(Rec record)
    {
        Vet vet = new Vet(null);
        try {
            vet.addNew();
            this.getRawFieldData(vet.getField(Vet.NAME));
            vet.add();
            Object bookmark = vet.getLastModified(DBConstants.BOOKMARK_HANDLE);
            record.getField(Cat.VET).setData(bookmark);
        } catch (DBException ex) {
            ex.printStackTrace();
        }
        return super.getRawRecordData(record);
    }

}
