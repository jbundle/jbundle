/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)MergeConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.RemoveConverterOnCloseHandler;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Converter;


/**
 * MergeConverter - Special converter to resolve the field EVERY time.
 * Used for mergeTables where the target table is always changing.
 * Usually, you will use the MultipleTableFieldConverter... This
 * converter is much more powerful, allowing you to specifing the recordname
 * and field name/sequence for each column (see addMergeColumn).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MergeConverter extends FieldConverter
{
    /**
     * The merge record.
     */
    protected Record m_MergeRecord = null;
    /**
     * The default field sequence.
     */
    protected int m_FieldSeq = -1;
    /**
     * The name of the record to get this field from (optional).
     */
    protected String m_strLinkedRecord = null;
    /**
     * An array to hold the added merge information.
     */
    protected Vector<InfoList> m_vArray = null;

    /**
     * Constructor.
     */
    public MergeConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The merge record for this column.
     */
    public MergeConverter(Record record)
    {
        this();
        this.init(record, null, -1);
    }
    /**
     * Constructor.
     * @param record The merge record for this column.
     * @param iFieldSeq The field sequence of the field for this column.
     */
    public MergeConverter(Record record, int iFieldSeq)
    {
        this();
        this.init(record, null, iFieldSeq);
    }
    /**
     * Constructor.
     * @param record The merge record for this column.
     * @param strLinkedRecord The name of the record this field sequence is in.
     * @param iFieldSeq The field sequence of the field for this column.
     */
    public MergeConverter(Record record, String strLinkedRecord, int iFieldSeq)
    {
        this();
        this.init(record, strLinkedRecord, iFieldSeq);
    }
    /**
     * Initialize this object.
     * @param record The merge record for this column.
     * @param strLinkedRecord The name of the record this field sequence is in.
     * @param iFieldSeq The field sequence of the field for this column.
     */
    public void init(Record record, String strLinkedRecord, int iFieldSeq)
    {
        super.init(null);
        m_MergeRecord = record;
        m_strLinkedRecord = strLinkedRecord;
        m_FieldSeq = iFieldSeq;
        if (record != null)
            record.addListener(new RemoveConverterOnCloseHandler(this));    // Because this is a converter (not a fieldConverter)
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        m_MergeRecord = null;
        m_strLinkedRecord = null;
        m_FieldSeq = -1;
        if (m_vArray != null)
        {
            while (m_vArray.size() > 0)
            {
                ((InfoList)m_vArray.elementAt(0)).free();
                m_vArray.removeElementAt(0);
            }
        }
        m_vArray = null;
        super.free();
    }
    /**
     * Get the maximum length of this field.
     * @return The maxlength of the current next converter on the chain.
     */
    public int getMaxLength() 
    {
        return this.getNextConverter().getMaxLength();
    }
    /**
     * Retrieve (in string format) from this field.
     * @return The current string of the current next converter.
     */
    public String getString() 
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().getString();
        else
            return super.getString();
    }
    /**
     * Convert and move string to this field.
     * Set the current string of the current next converter..
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String strString, boolean bDisplayOption, int iMoveMode)               // init this field override for other value
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().setString(strString, bDisplayOption, iMoveMode);
        else
            return super.setString(strString, bDisplayOption, iMoveMode);
    }
    /**
     * Get the field description.
     * @return The field description string of the current next converter.
     */
    public String getFieldDesc() 
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().getFieldDesc();
        else
            return super.getFieldDesc();
    }
    /**
     * Set up the default control for this field.
     * Adds the default screen control for the current converter, and makes me it's converter.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert convert, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        ScreenComponent sField = null;
        Converter converter = this.getNextConverter();
        if (converter != null)
            sField = converter.setupDefaultView(itsLocation, targetScreen, convert, iDisplayFieldDesc, properties);
        else
            sField = super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
        if (sField != null)
        {   // Get rid of any and all links to/from field/converter
            converter.removeComponent(sField);      // Have the field add me to its list for display
            sField.setConverter(this);
        }
        return sField;
    }
    /**
     * Get the Current converter.
     * @return The current converter depending on the current table in the merge table.
     */
    public Converter getNextConverter()
    {
        BaseTable currentTable = m_MergeRecord.getTable().getCurrentTable();
        if (currentTable == null)
            return null;
        if (m_FieldSeq != -1)
        {
            if (m_strLinkedRecord == null)
                return currentTable.getRecord().getField(m_FieldSeq); // Get the current field
            else
                return currentTable.getRecord().getField(m_strLinkedRecord, m_FieldSeq);    // Get the current field
        }
        else
        {
            for (int index = 0; index < m_vArray.size(); index++)
            {
                InfoList infoList = (InfoList)m_vArray.elementAt(index);
                Record pQueryTable = currentTable.getRecord().getRecord(infoList.m_strFileName);
                if (pQueryTable != null)
                {
                    String strLinkFileName = infoList.m_strLinkFileName;
                    int iFieldSeq = infoList.m_iFieldSeq;
                    Converter field = infoList.m_converter;
                    if (field != null)
                        return field;
                    if (strLinkFileName != null)
                        return currentTable.getRecord().getField(strLinkFileName, iFieldSeq);
                    return pQueryTable.getField(iFieldSeq);
                }
            }
        }
        return null;
    }
    /**
     * Add a file/converter pair.
     * @param strFileName The target record name.
     * @param converter The converter to return if this record is current.
     */
    public void addMergeField(String strFileName, Converter converter)
    {
        this.checkArray();
        m_vArray.addElement(new InfoList(strFileName, null, -1, converter));
    }
    /**
     * Add a file/converter pair.
     * @param strFileName The target record name.
     * @param iFieldSeq The field sequence of the field in thie record to return.
     */
    public void addMergeField(String strFileName, int iFieldSeq)
    {
        this.checkArray();
        m_vArray.addElement(new InfoList(strFileName, null, iFieldSeq, null));
    }
    /*
     * Add a file/converter pair.
     * @param strFileName The target record name.
     * @param strLinkFileName The File name to use if the target file name is the current file.
     * @param iFieldSeq The field sequence of the field in thie record to return.
     */
    public void addMergeField(String strFileName, String strLinkFileName, int iFieldSeq)
    {
        this.checkArray();
        m_vArray.addElement(new InfoList(strFileName, strLinkFileName, iFieldSeq, null));
    }
    /**
     * Set up the information array if it doesn't already exist.
     */
    public void checkArray()
    {
        if (m_vArray == null)
            m_vArray = new Vector<InfoList>();
    }
    /**
     * Holder for the extended merge information.
     */
    class InfoList extends Object
    {
        /*
         * The target record name.
         */
        public String m_strFileName;
        /*
         * The File name to use if the target file name is the current file.
         */
        public String m_strLinkFileName;
        /*
         * The field sequence of the field in thie record to return.
         */
        public int m_iFieldSeq;
        /*
         * The converter to return when the target file matches (optional).
         */
        public Converter m_converter;
        /*
         * @param strFileName The target record name.
         * @param strLinkFileName The File name to use if the target file name is the current file.
         * @param iFieldSeq The field sequence of the field in thie record to return.
         * @param converter The converter to return when the target file matches (optional).
         */
        public InfoList(String strFileName, String strLinkFileName, int iFieldSeq, Converter converter)
        {
            m_strFileName = strFileName;
            m_strLinkFileName = strLinkFileName;
            m_iFieldSeq = iFieldSeq;
            m_converter = converter;
        }
        /**
         * Free the fields in this object.
         */
        public void free()
        {
            m_strFileName = null;
            m_strLinkFileName = null;
            m_iFieldSeq = -1;
            m_converter = null;
        }
    }
}
