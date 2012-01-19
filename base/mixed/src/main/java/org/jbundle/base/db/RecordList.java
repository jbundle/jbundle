/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * QueryBase - Query description class.
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Enumeration;
import java.util.Vector;

import org.jbundle.base.util.DBConstants;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;


/**
 * RecordList is a helper class for RecordOwners.
 * It handles most of the list and organization functions required of a recordowner.
 * Note: Since RecordList is a Vector, it holds the records directly.
 */
public class RecordList extends Vector<Rec>
{
	private static final long serialVersionUID = 1L;

	/**
     * The screen record.
     */
    protected Record m_ScreenRecord = null;     // Screen Fields

    /**
     * Constructor.
     */
    public RecordList()
    {
        super();
    }
    /**
     * Constructor.
     * @param object Currently undefined, but you should pass the RecordList owner.
     */
    public RecordList(Object object)
    {
        this();
        this.init(object);
    }
    /**
     * Initialize the class.
     * @param object Currently undefined, but you should pass the RecordList owner.
     */
    public void init(Object object)
    {
        m_ScreenRecord = null;
    }
    /**
     * Free this object (calls free(null)).
     */
    public void free()
    {
        this.free(null);
    }
    /**
     * Free all the records in this list.
     * @param recordOwner Free records that have this recordowner (if null, free all records).
     */
    public void free(RecordOwner recordOwner)
    {
        if (m_ScreenRecord != null)
            if ((recordOwner == null) || (m_ScreenRecord.getRecordOwner() == recordOwner))
        {
            m_ScreenRecord.free();  // Delete the screen fields
            m_ScreenRecord = null;
        }

        while (this.size() > 0)
        {
            Record record = (Record)this.elementAt(0);
            if ((recordOwner == null) || (record.getRecordOwner() == recordOwner))
                record.free();
            else
                this.removeRecord(record);  // Remove the query from this list
        } 
        this.removeAllElements();
    }
    /**
     * Add this record to this list.
     * @param record The record to add.
     */
    public void addRecord(FieldList record)
    {
        this.addRecord(record, false);
    }
    /**
     * Add this record to this list.
     * @param record The record to add.
     * @param bMainQuery If this is the main record.
     */
    public void addRecord(Rec record, boolean bMainQuery)
    {
        if (record == null)
            return;
        if (this.contains(record))
        {     // Don't add this twice.
            if (!bMainQuery)
                return;
            this.removeRecord(record);  // Change order
        }
        if (bMainQuery)
            this.insertElementAt(record, 0);
        else
            this.addElement(record);
    }
    /**
     * Remove this record from this list.
     * @param record The record to remove.
     * @return true if successful.
     */
    public boolean removeRecord(Rec record)
    {
        return this.remove(record);
    }
    /**
     * Lookup this record for this recordowner.
     * @param The record's name (if null, return the screenrecord, if blank, return the main record).
     * @return The record with this name (or null if not found).
     */
    public Record getRecord(String strFileName)
    {
        if (strFileName == null)
            return this.getScreenRecord();
        if (strFileName.length() == 0)
            return this.getMainRecord();
        for (Enumeration<Rec> e = this.elements() ; e.hasMoreElements() ;)
        {   // This should only be called for Imaged GridScreens (Child windows would be deleted by now if Component)
            Record record = (Record)e.nextElement();
            record = record.getRecord(strFileName);
            if (record != null)
                return record;
        }
        return null;    // Not found
    }
    /**
     * Get the main record for this list.
     * @return The main record (or null if none).
     */
    public Record getMainRecord()
    {
        Record record = null;
        if (this.size() > 0)
        {
            record = (Record)this.firstElement();
            if (record != null) if (record == this.getScreenRecord())
            {
                record = null;
                if (this.size() >= 2)
                    record = (Record)this.elementAt(1);
            }
        }
        return record;
    }
    /**
     * Get the record as this position in the record list.
     * @param i Index
     * @return The record at this location (or null).
     */
    public Record getRecordAt(int i) 
    {
        i -= DBConstants.MAIN_FIELD;    // Zero based index
        try   {
            return (Record)this.elementAt(i);
        } catch (ArrayIndexOutOfBoundsException e)  {
        }
        return null;    // Not found
    }
    /**
     * Number of records in this recordlist.
     * @return The record count.
     */
    public int getRecordCount()
    {
        return this.size();
    }
    /**
     * Get the screen record.
     * @return The screen record.
     */
    public Record getScreenRecord()
    {
        return m_ScreenRecord;
    }
    /**
     * Set the screen record.
     * @param record The screen record.
     */
    public void setScreenRecord(Record record)
    {
        m_ScreenRecord = record;
    }
}
