/*
 *  @(#)BookingMessageData.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.message;

import org.jbundle.app.test.vet.db.Cat;
import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.message.MessageDataParent;
import org.jbundle.thin.base.message.MessageRecordDesc;
import org.jbundle.model.db.*;


/**
 *  BookingMessageData - .
 */
public class CatsMessageRecordDesc extends MessageRecordDesc
{
    /**
     * Default constructor.
     */
    public CatsMessageRecordDesc()
    {
        super();
    }
    /**
     * BookingMessageData Method.
     */
    public CatsMessageRecordDesc(MessageDataParent messageDataParent, String strKey)
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
    public int handlePutRawRecordData(Rec record)
    {
        Vet vet = (Vet)record;
        Cat cat = new Cat(vet.getRecordOwner());
        cat.addListener(new SubFileFilter(vet));
        try {
            while (cat.hasNext())
            {
                cat.next();
                record = cat;
                super.handlePutRawRecordData(record);
                for (int iFieldSeq = Cat.kName; iFieldSeq <= Cat.kWeight; iFieldSeq++)
                {
                    this.putRawFieldData(record.getField(iFieldSeq));
                }
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
        cat.free();
        return Constants.NORMAL_RETURN;
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int getRawRecordData(Rec record)
    {
        int iErrorCode = Constants.NORMAL_RETURN;
        Cat cat = new Cat(null);
        try {
            cat.addNew();
            iErrorCode = super.getRawRecordData(cat);
            for (int iFieldSeq = Cat.kName; iFieldSeq <= Cat.kWeight; iFieldSeq++)
            {
                this.getRawFieldData(cat.getField(iFieldSeq));
            }
            cat.add();
        } catch (DBException ex) {
            ex.printStackTrace();
        }
        cat.free();
        return iErrorCode;
    }
}
