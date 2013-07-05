/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)QueryConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.IntegerField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.field.StringField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Tie a query into a screen field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class QueryConverter extends FieldConverter
{
    /**
     *
     */
    protected GridTable m_gridTable = null;
    /**
     * The target record.
     */
    protected Record m_record = null;
    /**
     *
     */
    protected int m_iFieldSeq = -1;
    protected String displayFieldName = null;
    /**
     *
     */
    protected boolean m_bIncludeBlankOption = false;
    /**
     * The type of handle to save.
     */
    protected int m_iHandleType = DBConstants.BOOKMARK_HANDLE;

    /**
     * Constructor.
     */
    public QueryConverter()
    {
        super();
    }
    /**
     * This method was created by a SmartGuide.
     * @param converter The field that controls this grid query (should be a reference field).
     * @param record tour.db.Record The record to control (note, this converter adds a GridTable to the record's table).
     * @param iDisplayFieldSeq int The description field sequence in the grid record.
     * @param bIncludeBlankOption boolean Include a blank line in the popup?
     */
    public QueryConverter(Converter converter, Record record, int iDisplayFieldSeq, boolean bIncludeBlankOption)
    {
        this();
        this.init(converter, record, iDisplayFieldSeq, null, bIncludeBlankOption);
    }
    /**
     * This method was created by a SmartGuide.
     * @param converter The field that controls this grid query (should be a reference field).
     * @param record tour.db.Record The record to control (note, this converter adds a GridTable to the record's table).
     * @param iDisplayFieldSeq int The description field sequence in the grid record.
     * @param bIncludeBlankOption boolean Include a blank line in the popup?
     */
    public QueryConverter(Converter converter, Record record, String displayFieldName, boolean bIncludeBlankOption)
    {
        this();
        this.init(converter, record, -1, displayFieldName, bIncludeBlankOption);
    }
    /**
     * Initialize this converter.
     * @param converter The field that controls this grid query (should be a reference record).
     * @param record tour.db.Record The record to control (note, this converter adds a GridTable to the record's table).
     * @param iDisplayFieldSeq int The description field sequence in the grid record.
     * @param bIncludeBlankOption boolean Include a blank line in the popup?
     */
    public void init(Converter converter, Record record, int iDisplayFieldSeq, String displayFieldName, boolean bIncludeBlankOption)
    {
        super.init(converter);
        if (converter == null)
            converter = record.getField(displayFieldName);
        if (converter == null)
            converter = record.getField(iDisplayFieldSeq);
        m_iHandleType = DBConstants.BOOKMARK_HANDLE;
        if (record.getKeyArea().getKeyName().equals(DBConstants.PRIMARY_KEY))
            record.setKeyArea(record.getDefaultScreenKeyArea());
        record.setOpenMode(DBConstants.OPEN_READ_ONLY);   // Can't change a popup!
        m_record = record;
        if (record.getTable() instanceof GridTable)
            m_gridTable = (GridTable)record.getTable();     // Never
        else
            m_gridTable = new GridTable(null, record);
        m_bIncludeBlankOption = bIncludeBlankOption;
        m_iFieldSeq = iDisplayFieldSeq;
        this.displayFieldName = displayFieldName;
        try   {
            m_gridTable.open();
        } catch (DBException e)   {
            m_gridTable = null;
        }
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        if (m_gridTable != null)
        {
            m_gridTable.close();
            m_gridTable.free();     // If this file doesn't have a sceen, close it
            m_gridTable = null;
        }
    }
    /**
     * Convert the current field value to an index.
     * @return The zero-based index of the location of this field (-1 = none).
     */
    public int convertFieldToIndex()
    {
        int index = -1;
        FieldInfo field = this.getField();
        if (field != null)
        {
            if (!field.isNull())
            {
                boolean bFound = false;
                Object bookmark = null;
                try   {
                    if (field instanceof ReferenceField)    // Always
                        if (((ReferenceField)field).getReferenceRecord() == m_record)
                            bookmark = ((ReferenceField)field).getReference().getHandle(DBConstants.BOOKMARK_HANDLE);
                } catch (DBException ex)    {
                    bookmark = null;
                }
                if (bookmark == null)
                    if (field instanceof IntegerField)
                        bookmark = field.getData();
                Object bookmarkRecord = null;
                try   {
                    bookmarkRecord = m_record.getTable().getHandle(m_iHandleType);
                } catch (DBException ex)    {
                } 

                if ((m_record.getEditMode() == Constants.EDIT_IN_PROGRESS) ||
                    (m_record.getEditMode() == Constants.EDIT_CURRENT))
                        if (bookmarkRecord != null)
                            if (bookmarkRecord.equals(bookmark))
                                bFound = true;  // Don't re-read the record... It is current!
                if (bFound == false)
                    if (bookmark != null)
                {   // First, try to look up the field (a bookmark) in the grid table.
                    index = m_gridTable.bookmarkToIndex(bookmark, DBConstants.BOOKMARK_HANDLE);   // Find the index of this record in the list
                    if (index != -1)
                    {
                        if (m_bIncludeBlankOption)
                            index++;
                        return index;
                    }

                    try   {
                        bFound = (m_record.getTable().setHandle(bookmark, m_iHandleType) != null);
                    } catch (DBException e)   {
                        bFound = false;
                    }
                } 
                if (bFound)
                {
                    index = m_gridTable.bookmarkToIndex(bookmark, DBConstants.BOOKMARK_HANDLE);   // Find the index of this record in the list
                    if (index != -1)
                    {
                        try {
                            m_gridTable.get(index); // Make sure I'm actually pointing to this record (fast - should be cached)
                        } catch (DBException e) {
                            e.printStackTrace();    // Never
                        } 
                    }
                    if (m_bIncludeBlankOption)
                        index++;
                    if (index != -1)
                        return index;
                }
            }
        }
        this.moveToIndex(-1);  // Move off any valid record
        if (m_bIncludeBlankOption)
            if (field != null)
                if (field.isNull())
                    return 0; // Null = Select the "Blank" line
        return -1;
    }
    /**
     * Convert this index value to a display string.
     * @param index The index of the display string to retrieve.
     * @return The display string.
     */
    public String convertIndexToDisStr(int index)
    {
        int iErrorCode = this.moveToIndex(index);
        if (iErrorCode == DBConstants.NORMAL_RETURN)
        {
            if (displayFieldName != null)
                return m_record.getField(displayFieldName).getString();
            else
                return m_record.getField(m_iFieldSeq).getString();
        }
        else
            return Constants.BLANK;
    }
    /**
     * Convert the display's index to the field value and move to field.
     * @param index The index to convert an set this field to.
     * @param bDisplayOption If true, display the change in the converters.
     * @param iMoveMove The type of move.
     */
    public int convertIndexToField(int index, boolean bDisplayOption, int iMoveMode)
    {   // User selected an item... Read it in!
        int iErrorCode = this.moveToIndex(index);
        if (iErrorCode == DBConstants.NORMAL_RETURN)
            m_record.handleRecordChange(null, DBConstants.SELECT_TYPE, bDisplayOption);   // Record selected!!!
        // These next lines causes the FieldBehaviors with SCREEN_MOVE to be executed!!!
        FieldInfo field = this.getField();
        if (iErrorCode == DBConstants.NORMAL_RETURN) if (field != null)
        {
            if (field instanceof ReferenceField)
                ((ReferenceField)field).setReference(m_record, bDisplayOption, iMoveMode);
            else if (field instanceof StringField)
            {
                {
                    if (displayFieldName != null)
                        return field.setString(m_record.getField(displayFieldName).toString());
                    else
                        return field.setString(m_record.getField(m_iFieldSeq).toString());
                }
            }
        }
        return iErrorCode;
    }
    /**
     * Retrieve (in string format) from this field.
     * @return The display field of the grid record.
     */
    public String getString() 
    {
        FieldInfo field = this.getField();
        if (field instanceof StringField)
            return field.toString();
        if (displayFieldName != null)
            return m_record.getField(displayFieldName).getString();  // Return the desc string
        else
            return m_record.getField(m_iFieldSeq).getString();  // Return the desc string
    }
    /**
     * Move the grid record to this location in the query (taking blank/no blank into consideration).
     * @param index The position to move to.
     * @return An error code (or NORMAL_RETURN).
     */
    public int moveToIndex(int index)
    {
        int index2 = index;
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (m_bIncludeBlankOption)
        {
            if (index == 0)
                iErrorCode = DBConstants.ERROR_RETURN;
            index2--; // Offset by one
        }
        if (index == -1)
            iErrorCode = DBConstants.ERROR_RETURN;
        if (iErrorCode == DBConstants.NORMAL_RETURN)
        {
            if (index2 != -1)
            {
                try
                {
                    Record record = (Record)m_gridTable.get(index2);    //?, DONT_DISPLAY);   // DON'T Display, or you will be caught in a loop
                    if (record == null)
                        iErrorCode = DBConstants.END_OF_FILE;
                }
                catch( DBException e )
                {
                    iErrorCode = e.getErrorCode();
                }
            }
            else
                iErrorCode = DBConstants.ERROR_RETURN;  // Do an addNew
        }
        else        // This is usually selecting index 0 or -1!
        {
            m_record.initRecord(DBConstants.DISPLAY);  // Clear the fields!
            iErrorCode = DBConstants.NORMAL_RETURN;   // Clear fields = record 0
        }
        return iErrorCode;
    }
    /**
     * Get the record for this QueryConverter.
     * @return The record.
     */
    public Record getTargetRecord()
    {
        return m_record;
    }
}
