/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)MultipleTableFieldConverter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Iterator;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.RemoveConverterOnCloseHandler;
import org.jbundle.base.db.shared.MultiTable;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.thin.base.db.Converter;


/**
 * Special converter for merge records. The converter returns the field
 * of the current record in a merge table.
 *  (Used for Multiple Tables where the target table is always changing).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MultipleTableFieldConverter extends MultipleFieldConverter
{
    /**
     * The merge record.
     */
    protected Record m_recMerge = null;
    /**
     * The sequence of this field in the target records.
     */
    protected int m_iFieldSeq = -1;
    /**
     * The sequence of this field in the secondary record.
     */
    protected int m_iSecondaryFieldSeq = -1;
    /**
     * The sequence of this field in the target records.
     */
    protected String fieldName = null;
    /**
     * The sequence of this field in the secondary record.
     */
    protected String secondaryFieldName = null;

    /**
     * Constructor.
     */
    public MultipleTableFieldConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The merge record.
     * @param iFieldSeq The sequence of the field in the target records.
     */
    public MultipleTableFieldConverter(Record record, int iFieldSeq)
    {
        this();
        this.init(record, iFieldSeq, null, -1, null);
    }
    /**
     * Constructor.
     * @param record The merge record.
     * @param iFieldSeq The sequence of the field in the target records.
     */
    public MultipleTableFieldConverter(Record record, String fieldName)
    {
        this();
        this.init(record, -1, fieldName, -1, null);
    }
    /**
     * Constructor.
     * @param record The merge record.
     * @param iFieldSeq The sequence of the field in the target records.
     */
    public MultipleTableFieldConverter(Record record, int iFieldSeq, int iSecondaryFieldSeq)
    {
        this();
        this.init(record, iFieldSeq, null, iSecondaryFieldSeq, null);
    }
    /**
     * Constructor.
     * @param record The merge record.
     * @param iFieldSeq The sequence of the field in the target records.
     */
    public MultipleTableFieldConverter(Record record, String fieldName, String secondaryFieldName)
    {
        this();
        this.init(record, -1, fieldName, -1, secondaryFieldName);
    }
    /**
     * Init.
     * @param record The merge record.
     * @param iFieldSeq The sequence of the field in the target records.
     */
    public void init(Record record, int iFieldSeq, String fieldName, int iSecondaryFieldSeq, String secondaryFieldName)
    {
        Converter converter = null;
        m_recMerge = record;
        m_iFieldSeq = iFieldSeq;
        m_iSecondaryFieldSeq = iSecondaryFieldSeq;
        this.fieldName = fieldName;
        this.secondaryFieldName = secondaryFieldName;
        if (record != null)
            converter = this.getTargetField(null);
        super.init(converter, null);
        if (record != null)
            record.addListener(new RemoveConverterOnCloseHandler(this));    // Because this is a converter (not a fieldConverter)
        if (m_recMerge != null)
            if (m_recMerge.getTable() instanceof MultiTable)
        {   // Add all the fields in the sub-records
            MultiTable multiTable = (MultiTable)m_recMerge.getTable();
            Iterator<BaseTable> iterator = multiTable.getTables();
            while (iterator.hasNext())
            {
                BaseTable table = (BaseTable)iterator.next();
                Converter field = this.getTargetField(table.getRecord());
                this.addConverterToPass(field);     // Add it, and
            }
        }
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        m_recMerge = null;
        m_iFieldSeq = -1;
        m_iSecondaryFieldSeq = -1;
        this.fieldName = null;
        this.secondaryFieldName = null;
        super.free();
    }
    /**
     * Should I pass the alternate field (or the main field)?
     * @return index (-1)= next converter, 0 - n = List of converters
     */
    public int getIndexOfConverterToPass(boolean bSetData)
    {
        Converter field = this.getTargetField(null);
        if (m_converterNext == field)
            return -1;  // -1 is the code for the base field.
        int iIndex = 0;
        for ( ; ; iIndex++)
        {   // Is this one already on my list?
            Converter converter = this.getConverterToPass(iIndex);
            if (converter == null)
                break;      // End of list
            if (converter == field)
                return iIndex;      // Found
        }
        if (field == null)
            return -1;  // Never
        this.addConverterToPass(field);     // Add it, and
        return iIndex;  // Return the index to the new converter.
    } // Override this
    /**
     * Get the target field in this record.
     */
    public Converter getTargetField(Record record)
    {
        if (record == null)
        {
            BaseTable currentTable = m_recMerge.getTable().getCurrentTable();
            if (currentTable == null)
                currentTable = m_recMerge.getTable();    // First time only
            record = currentTable.getRecord();
        }
        Converter field = null;
        if (fieldName != null)
            field = record.getField(fieldName);  // Get the current field
        else
            field = record.getField(m_iFieldSeq);  // Get the current field
        if ((m_iSecondaryFieldSeq != -1) || (secondaryFieldName != null))
            if (field instanceof ReferenceField)
            	if (((ReferenceField)field).getRecord() != null)
                	if (((ReferenceField)field).getRecord().getTable() != null)		// Make sure this isn't being called in free()
        {   // Special case - field in the secondary file
            Record recordSecond = ((ReferenceField)field).getReferenceRecord();
            if (recordSecond != null)
            {
                if (secondaryFieldName != null)
                    field = recordSecond.getField(secondaryFieldName);
                else
                    field = recordSecond.getField(m_iSecondaryFieldSeq);
            }
        }
        return field;
    }
}
