/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * Base Reference to an object in a table.<p>
 * Note: This field will only save references to BaseTable records.
 * If you want the capability to read a reference, use ReferenceField.
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.db.event.ClearFieldReferenceOnCloseHandler;
import org.jbundle.base.db.event.FreeOnFreeHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;
import org.jbundle.base.model.RecordOwner;

import java.util.Map;

public class RecordReferenceField extends ObjectField
{
	private static final long serialVersionUID = 1L;

	/**
     * Typical description field.
     */
    protected String m_iDescription = null;
    /**
     * Record this field refers to.
     */
    protected Record m_recordReference = null;

    /**
     * ReferenceField constructor comment.
     */
    public RecordReferenceField()
    {
        super();
    }
    /**
     * ReferenceField constructor comment.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public RecordReferenceField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize this object.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        m_iDescription = null;    // Typical description field
        m_recordReference = null;                                       // Record description of this Table
    }
    /**
     * Get the record that this field references.
     * This is the default method, so if a record by this name doesn't belong to this screen, add this one!
     * @return tour.db.Record
     */
    public Record getReferenceRecord()
    {
        Record recordReference = this.getReferenceRecord(null);
        if (recordReference != null)
        	if (recordReference.getRecordOwner() == null)	// Only add it if it doesn't belong to any other screen.
        {
            RecordOwner recordOwner = null;
            if (this.getRecord() != null)
                recordOwner = Record.findRecordOwner(this.getRecord());
            if (recordOwner != null)
                recordOwner.addRecord(recordReference, false);
            if (this.getRecord() != null)
            {   // Make sure record is freed when this record is freed.
                FreeOnFreeHandler listener = (FreeOnFreeHandler)this.getRecord().getListener(FreeOnFreeHandler.class);
                while (listener != null)
                {
                    if (listener.getDependentObject() == recordReference)
                        break;  // listener is already there
                    listener = (FreeOnFreeHandler)listener.getListener(FreeOnFreeHandler.class);
                }
                if (listener == null)
                    this.getRecord().addListener(new FreeOnFreeHandler(recordReference));
            }
        }
        return recordReference;
    }
    /**
     * Get the record that this field references.
     * @param recordOwner The recordowner to initialize the reference record to if it doesn't already exist.
     * @return tour.db.Record The record this field references.
     */
    public Record getReferenceRecord(RecordOwner recordOwner)
    {
        return this.getReferenceRecord(recordOwner, true);
    }
    /**
     * Get the record that this field references.
     * @param screen The recordowner to initialize the reference record to if it doesn't already exist.
     * @param bCreateIfNotFound Create the record reference if it isn't set up yet.
     * @return tour.db.Record The record this field references.
     */
    public Record getReferenceRecord(RecordOwner screen, boolean bCreateIfNotFound)
    {
        if ((m_recordReference == null)
            && (bCreateIfNotFound))
        {
            RecordOwner recordOwner = screen;
            if (recordOwner == null)
                if (this.getRecord() != null)
                    recordOwner = Record.findRecordOwner(this.getRecord());
            this.setReferenceRecord(this.makeReferenceRecord(recordOwner));
            if (m_recordReference != null)  // getReferenceRecord() would cause an endless loop
            {
                m_recordReference.setOpenMode(m_recordReference.getOpenMode() | DBConstants.OPEN_READ_ONLY);    // By default a reference record can't be changed
                if (screen == null)
                    if (recordOwner != null)   // The caller wanted to have a record without a recordowner.
                    {
                        recordOwner.removeRecord(m_recordReference);    // Set it is not on the recordowner's list
                        if (this.getRecord() != null)
                        {   // Make sure record is freed when this record is freed.
                            FreeOnFreeHandler listener = (FreeOnFreeHandler)this.getRecord().getListener(FreeOnFreeHandler.class);
                            while (listener != null)
                            {
                                if (listener.getDependentObject() == m_recordReference)
                                    break;  // listener is already there
                                listener = (FreeOnFreeHandler)listener.getListener(FreeOnFreeHandler.class);
                            }
                            if (listener == null)
                                this.getRecord().addListener(new FreeOnFreeHandler(m_recordReference));
                        }
                    }
            }
        }
        else if ((m_recordReference != null)
            && (m_recordReference.getRecordOwner() == null)
                && (screen != null))
        {
            screen.addRecord(m_recordReference, false);
            m_recordReference.setRecordOwner(screen);   // Now you have a record owner.
        }
        return m_recordReference;
    }
    /**
     * Make the record that this field references.
     * Typically, you override this method to set the referenced class if it doesn't exist.
     *<p><pre>
     * For Example:
     *  return new RecordName(screen);
     * </pre>
     * @return tour.db.Record
     */
    public final Record makeReferenceRecord()
    {
        if (m_recordReference != null)
            return m_recordReference;       // It already exists
        RecordOwner recordOwner = null;
        if (this.getRecord() != null)
            recordOwner = Record.findRecordOwner(this.getRecord());    // This way the record has access to the correct environment.
        this.setReferenceRecord(this.makeReferenceRecord(recordOwner));
        return m_recordReference;
    }
    /**
     * Make the record that this field references.
     * Typically, you override this method to set the referenced class if it doesn't exist.
     *<p><pre>
     * For Example:
     *  return new RecordName(screen);
     * </pre>
     * @param recordOwner The recordowner.
     * @return tour.db.Record
     */
    public Record makeReferenceRecord(RecordOwner recordOwner)
    {
        return m_recordReference; // Return null
    }
    /**
     * Set the record that this field references.
     * @param record The record to set as the record reference.
     */
    public void setReferenceRecord(Record record)
    {
        if (m_recordReference != record)
        {
            if (m_recordReference != null)
            { // Take behaviors off old references
               // You only want to disconnect the referenced record (not free it).
                if (this.getRecord() != null)
                {
                    FreeOnFreeHandler freeListener = (FreeOnFreeHandler)this.getRecord().getListener(FreeOnFreeHandler.class);
                    while (freeListener != null)
                    {
                        if (freeListener.getDependentObject() == m_recordReference)
                        {
                            freeListener.setDependentObject(null);
                            this.getRecord().removeListener(freeListener, true);
                            break;
                        }
                        freeListener = (FreeOnFreeHandler)freeListener.getListener(FreeOnFreeHandler.class);
                    }
                }

                ClearFieldReferenceOnCloseHandler listener = (ClearFieldReferenceOnCloseHandler)m_recordReference.getListener(ClearFieldReferenceOnCloseHandler.class.getName());
                while (listener != null)
                {
                    if (listener.getField() == this)
                    {
                        Record recordTemp = m_recordReference;
                        m_recordReference = null; // Cancel echo
                        recordTemp.removeListener(listener, true);
                        listener = null;    // Got it!
                    }
                    else
                        listener = (ClearFieldReferenceOnCloseHandler)listener.getListener(ClearFieldReferenceOnCloseHandler.class.getName());
                }
            }
            if (record != null)
            { // Make sure reference is cleared when record is discarded
                record.addListener(new ClearFieldReferenceOnCloseHandler(this));
            }
            m_recordReference = record;
        }
    }
    /**
     * Get the field sequence of the record description field.
     * @return
     */
    public String getDefaultDisplayFieldSeq()
    {
        if (m_iDescription == null)
        	m_iDescription = this.getReferenceRecord().getDefaultDisplayFieldName();
        return m_iDescription;
    }
}
