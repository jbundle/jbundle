/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * @(#)KeyArea.java   1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.KeyAreaInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;


/**
 * KeyArea - Definition of this key area
 * Be very careful when using this implementation, because the internal
 * data representation is much different than the inherited KeyAreaInfo (in the thin model).
 * In the thin model the m_vKeyFieldList is used to save the actual fields,
 * where in the thick model this field is used to store KeyField(s).
 * An KeyArea describes a particular key area (fields and order)
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class KeyArea extends KeyAreaInfo
{
	private static final long serialVersionUID = 1L;

	/**
     * Some implementations require this value.
     */
    protected int m_iKeyActualLength = 0;       // Actual key length
    /**
     * Some implementations require this value.
     */
    protected int m_iKeyByteLength = 0;         // Key length (0-64 bytes) (Rounded up to the nearest word)

    /**
     * Constructor.
     */
    public KeyArea()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param iKeyDup The type of key (UNIQUE/NOT_UNIQUE/SECONDARY).
     * @param strKeyName The name of this key (default to first fieldnameKEY).
     */
    public KeyArea(FieldList record, int iKeyDup, String strKeyName)
    {
        this();
        this.init(record, iKeyDup, strKeyName);
    }
    /**
     * Initialize the class.
     * @param record The parent record.
     * @param iKeyDup The type of key (UNIQUE/NOT_UNIQUE/SECONDARY).
     * @param strKeyName The name of this key (default to first fieldnameKEY).
     */
    public void init(Rec record, int iKeyDup, String strKeyName)
    {
        super.init(record, iKeyDup, strKeyName);
        m_iKeyActualLength = 0;     // Actual Byte length
        m_iKeyByteLength = 0;   // Key length (0-64 bytes) (including trailer bytes)
    }
    /**
     * Release the Objects in this KeyArea.
     */
    public void free()
    {
        for (int i = m_vKeyFieldList.size() - 1 ; i >= 0 ;i--)
        {
            KeyField keyField = (KeyField)m_vKeyFieldList.elementAt(i);
            keyField.free();
        }
        super.free();
    }
    /**
     * Add this KeyField to this Key Area.
     * Note: Don't call this directly, it is called from KeyField.
     * @param iFieldSeq The field to add.
     * @param bKeyArea The order (ascending/descending).
     */
    public void addKeyField(KeyField keyField)
    { // Get the field with this seq
        m_vKeyFieldList.addElement(keyField);           // Set this key field
        if (m_strKeyName == null)
            m_strKeyName = keyField.getField(DBConstants.FILE_KEY_AREA).getFieldName(false, false);   //x + "Key";  // Default key name
    }
    /**
     * Remove this KeyField to this Key Area.
     * Note: Don't call this directly, it is called from KeyField.
     * @param keyField The keyfield to remove.
     * @return true of successful.
     */
    public boolean removeKeyField(KeyField keyField)
    { // Get the field with this seq
        return m_vKeyFieldList.removeElement(keyField);             // Set this key field
    }
    /**
     * Add this field to this Key Area.
     * @param iFieldSeq The field to add.
     * @param bKeyArea The order (ascending/descending).
     */
    public void addKeyField(Field field, boolean bKeyOrder)
    { // Get the field with this seq
        new KeyField(this, (BaseField)field, bKeyOrder);    // init the key field seq
    }
    /**
     * Setup the SQL Key Filter.
     * @param seekSign The seek sign.
     * @param iAreaDesc The key area to select.
     * @param bAddOnlyMods Add only the keys which have modified?
     * @param bIncludeFileName Include the file name in the string?
     * @param bUseCurrentValues Use current values?
     * @param vParamList The parameter list.
     * @param bForceUniqueKey If params must be unique, if they aren't, add the unique key to the end.
     * @param bIncludeTempFields  If true, include any temporary key fields that have been added to the end if this keyarea
     * @return The select string.
     */
    public String addSelectParams(String seekSign, int iAreaDesc, boolean bAddOnlyMods, boolean bIncludeFileName, boolean bUseCurrentValues, Vector<BaseField> vParamList, boolean bForceUniqueKey, boolean bIncludeTempFields)
    {
        if (iAreaDesc == DBConstants.START_SELECT_KEY)
            if (bAddOnlyMods)
                if (bForceUniqueKey)
                    if (">=".equals(seekSign))
                        if (this.getRecord().getCounterField() != this.getField(0))
                            if (this.getKeyField(this.getKeyFields(bForceUniqueKey, false) - 1, bForceUniqueKey).getField(iAreaDesc).isModified())
                                return this.addGreaterThanParams(seekSign, iAreaDesc, bAddOnlyMods, bIncludeFileName, bUseCurrentValues, vParamList, bForceUniqueKey);
        String strFilter = DBConstants.BLANK;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, bIncludeTempFields);
        if (bAddOnlyMods)
            iKeyFieldCount = this.lastModified(iAreaDesc, bForceUniqueKey) + 1;
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField pParamField = keyField.getField(iAreaDesc);
            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
            if (">=".equals(seekSign))
                if (pParamField.isNull())
                    break;  // Can't say >=? if null.
            if (strFilter.length() > 0)
                strFilter += " AND ";
            String strCompare = null;
            strFilter += field.getFieldName(true, bIncludeFileName);
            String strSign = seekSign;
            if (bUseCurrentValues == false)
            {
                strCompare = "?";
                strFilter += strSign + strCompare;
                if (vParamList != null)
                    vParamList.add(pParamField);
            }
            else
                strFilter += pParamField.getSQLFilter(strSign, strCompare, true);
        }
        return strFilter;
    }
    /**
     * Setup the SQL Key Filter.
     * This is the special filter that is used to get the next records in a
     * unique sequential list. Specifically, to position a grid correctly when
     * the original query is no longer valid. This uses logic to set up the WHERE clause as:
     * <pre>
     * ((ROW0 > ?) OR
     * ((ROW0 > ?) AND (ROW1 > ?)) OR
     * ((ROW0 >= ?) AND (ROW1 >= ?) AND (ROW2 >= ?)))   -- ROW2 is the counter field.
     * </pre>
     * @param seekSign The seek sign.
     * @param iAreaDesc The key area to select.
     * @param bAddOnlyMods Add only the keys which have modified?
     * @param bIncludeFileName Include the file name in the string?
     * @param bUseCurrentValues Use current values?
     * @param vParamList The parameter list.
     * @param bForceUniqueKey If params must be unique, if they aren't, add the unique key to the end.
     * @return The select string.
     */
    public String addGreaterThanParams(String seekSign, int iAreaDesc, boolean bAddOnlyMods, boolean bIncludeFileName, boolean bUseCurrentValues, Vector<BaseField> vParamList, boolean bForceUniqueKey)
    {
        String strFilter = DBConstants.BLANK;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        strFilter += "(";
        for (int iCount = DBConstants.MAIN_KEY_FIELD; iCount < iKeyFieldCount; iCount++)
        {
            if (strFilter.length() > 1)
                strFilter += " OR ";
            strFilter += "(";
            for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq <= iCount; iKeyFieldSeq++)
            {
                KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
                BaseField pParamField = keyField.getField(iAreaDesc);
                BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
                if (iKeyFieldSeq > DBConstants.MAIN_KEY_FIELD)
                    strFilter += " AND ";
                String strCompare = null;
                strFilter += field.getFieldName(true, bIncludeFileName);
                String strSign = seekSign;
                if (iCount < iKeyFieldCount - 1)
                    strSign = seekSign.substring(0, 1);
                if (bUseCurrentValues == false)
                {
                    strCompare = "?";
                    strFilter += strSign + strCompare;
                    if (vParamList != null)
                        vParamList.add(pParamField);
                }
                else
                    strFilter += pParamField.getSQLFilter(strSign, strCompare, true);
            }
            strFilter += ")";
        }
        strFilter += ")";
        return strFilter;
    }
    /**
     * Setup the SQL Sort String.
     * @param bIncludeFileName If true, include the filename with the fieldname in the string.
     * @param bForceUniqueKey If params must be unique, if they aren't, add the unique key to the end.
     * @return The SQL sort string.
     */
    public String addSortParams(boolean bIncludeFileName, boolean bForceUniqueKey)
    {
        String strSort = DBConstants.BLANK;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
            if (strSort.length() > 0)
                strSort += ",";
            strSort += field.getFieldName(true, bIncludeFileName);
            if (keyField.getKeyOrder() == DBConstants.DESCENDING)
                strSort += " DESC";     // Descending
        }
        if (strSort.length() > 0)
            strSort = " ORDER BY " + strSort;
        return strSort;
    }
    /**
     * Compare these two keys and return the compare result.
     * @param iAreaDesc The temp key area to compare.
     * @return The compare result (see compareTo method).
     */
    public int compareKeys(int iAreaDesc)
    {
        int iCompareValue = 0;
        boolean bForceUniqueKey = false;    // This method is only used by the physical table which do their own unique processing.
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField fldCurrent = keyField.getField(DBConstants.FILE_KEY_AREA);
            BaseField fldTemp = keyField.getField(iAreaDesc);
            iCompareValue = fldCurrent.compareTo(fldTemp);
            if (this.getKeyOrder(iKeyFieldSeq) == DBConstants.DESCENDING)
                iCompareValue = -iCompareValue;
            if (iCompareValue != 0)
                break;
        }
        return iCompareValue;
    }
    /**
     * Get the Field in this KeyField.
     * This is just a convenience to get the field from the KeyField.
     * @param iKeyFieldSeq The position of this field in the key area.
     * @return The field.
     */
    public BaseField getField(int iKeyFieldSeq)
    {
        if (this.getKeyField(iKeyFieldSeq) == null)
            return null;
        return this.getKeyField(iKeyFieldSeq).getField(DBConstants.FILE_KEY_AREA);
    }
    /**
     * Get this Key Field.
     * @param iKeyFieldSeq The position of this keyfield in the key area.
     * @return The key field.
     */
    public KeyField getKeyField(int iKeyFieldSeq)
    {
        if (iKeyFieldSeq >= m_vKeyFieldList.size())
            return null;
        return (KeyField)m_vKeyFieldList.elementAt(iKeyFieldSeq);
    }
    /**
     * Only used in overridden classes.
     * @return The key length.
     */
    public int getKeyLength()
    {
        return m_iKeyByteLength;
    }       // Get key length
    /**
     * Get the key order for this key field (Ascending/Descending).
     * @param iKeyFieldSeq The field to check.
     * @return true if ascending order.
     */
    public boolean getKeyOrder(int iKeyFieldSeq)
    {
        return this.getKeyField(iKeyFieldSeq).getKeyOrder();
    }
    /**
     * Get the seq of this key area.
     * Get rid of this method.
     * @return The sequence of the keyarea.
     */
    public int getKeySeq()
    {   // This is a dumb way to do this
        int iLastIndexArea = m_record.getKeyAreaCount() - 1 + DBConstants.MAIN_KEY_AREA;
        for (int nIndex = DBConstants.MAIN_KEY_AREA; nIndex <= iLastIndexArea; nIndex++)
        {
            KeyAreaInfo index = m_record.getKeyArea(nIndex);
            if (index == null)
                return -1;  // Not found
            if (this == index)
                return nIndex;      // Zero based . MAIN_FIELD based
        }
        return -1;  // Not found
    }
    /**
     * Get the parent record.
     * (Same as getFieldList with the return cast to Record).
     * @return The parent record.
     */
    public Record getRecord()
    {
        return (Record)super.getRecord();
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * This is overidden to move the physical data type.
     * @param statement The SQL prepared statement.
     * @param iParamColumn  Starting param column
     * @param iAreaDesc The key field area to get the values from.
     * @param bAddOnlyMods Add only modified fields?
     * @return  Next param column.
     * @exception SQLException From SQL calls.
     */
    public int getSQLFromField(PreparedStatement statement, int iType, int iParamColumn, int iAreaDesc, boolean bAddOnlyMods, boolean bIncludeTempFields) throws SQLException
    {
        boolean bForceUniqueKey = false;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, bIncludeTempFields);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField fieldParam = keyField.getField(iAreaDesc);
            if (bAddOnlyMods) if (!fieldParam.isModified())
                continue;   // Skip this one
            fieldParam.getSQLFromField(statement, iType, iParamColumn++);
        }
        return iParamColumn;
    }
    /**
     * Get the table.
     * @return The table for this key area's parent record.
     */
    public BaseTable getTable()
    {
        return ((Record)m_record).getTable();
    }
    /**
     * Any of these key fields modified?
     * @param iAreaDesc The key field area to get the values from.
     * @return true if any have been modified.
     */
    public boolean isModified(int iAreaDesc)
    {   // Set up the end key
        return (this.lastModified(iAreaDesc, true) != -1);
    }
    /**
     * Any of these key fields modified?
     * @param iAreaDesc The key field area to get the values from.
     * @param iStartKeyFieldSeq The starting key field to check (from here on).
     * @return true if any have been modified.
     */
    public int lastModified(int iAreaDesc, boolean bForceUniqueKey)
    {   // Set up the end key
        int iLastKeyField = this.getKeyFields(bForceUniqueKey, false) - 1;
        for (int iKeyFieldSeq = iLastKeyField; iKeyFieldSeq >= DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq--)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            if (keyField.getField(iAreaDesc).isModified())
                return iKeyFieldSeq;
        }
        return -1;
    }
    /**
     * Take this BOOKMARK handle and back it into the fields.
     * @param bookmark The bookmark handle.
     * @param iAreaDesc The area to move it into.
     */
    public void reverseBookmark(Object bookmark, int iAreaDesc)
    {
        if (this.getKeyFields() == 1)   // Special case - single unique key;
        {
            BaseField field = this.getField(iAreaDesc);
            boolean[] rgbEnabled = field.setEnableListeners(false);
            field.setData(bookmark, DBConstants.DONT_DISPLAY, DBConstants.INIT_MOVE);
            field.setEnableListeners(rgbEnabled);
        }
        else
        {
            BaseBuffer buffer = (BaseBuffer)bookmark;
            if (buffer != null)
                buffer.resetPosition();
            this.reverseKeyBuffer(buffer, iAreaDesc);
        }
    }
    /**
     * Create a BOOKMARK handle for this key area.
     * @return bookmark The bookmark handle.
     * @param iAreaDesc The area to move it into.
     */
    public Object setupBookmark(int iAreaDesc)
    {
        if (this.getKeyFields() == 1)   // Special case - single unique key;
            return this.getField(iAreaDesc).getData();
        BaseBuffer buffer = new org.jbundle.thin.base.db.buff.str.StrBuffer(null);
        this.setupKeyBuffer(buffer, iAreaDesc);
        return buffer;
    }
    /**
     * Move the key area to the record.
     * <pre>
     * Remember to do the following: (before calling this method!)
     *      if (bufferSource != null)
     *      bufferSource.resetPosition();
     * </pre>
     * @param destBuffer A BaseBuffer to fill with data (ignore if null).
     * @param iAreaDesc The (optional) temporary area to copy the current fields to.
     */
    public void reverseKeyBuffer(BaseBuffer bufferSource, int iAreaDesc)        // Move these keys back to the record
    {
        boolean bForceUniqueKey = true;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
            BaseField paramField = keyField.getField(iAreaDesc);
            if (iAreaDesc != DBConstants.FILE_KEY_AREA)
                field.moveFieldToThis(paramField, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);   // Copy the value
            if (bufferSource != null)
            {
                bufferSource.getNextField(field, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);  // Read move ignores most behaviors
            }
        }
    }
    /**
     * Set up the key area indicated.
     * Remember to clear the destBuffer first.
     * @param destBuffer A BaseBuffer to fill with data (ignore if null).
     * @param iAreaDesc The (optional) temporary area to copy the current fields to.
     */
    public void setupKeyBuffer(BaseBuffer destBuffer, int iAreaDesc)
    {
        this.setupKeyBuffer(destBuffer, iAreaDesc, true);
    }
    /**
     * Set up the key area indicated.
     * Remember to clear the destBuffer first.
     * @param destBuffer A BaseBuffer to fill with data (ignore if null).
     * @param iAreaDesc The (optional) temporary area to copy the current fields to.
     * @param bMoveToField If true, move the param field to the field (default).
     */
    public void setupKeyBuffer(BaseBuffer destBuffer, int iAreaDesc, boolean bMoveToField)
    {
        boolean bForceUniqueKey = true;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
            BaseField paramField = keyField.getField(iAreaDesc);
            if (bMoveToField)
                if (iAreaDesc != DBConstants.FILE_KEY_AREA)   // Don't move this they are the same field
            {
                paramField.moveFieldToThis(field, DBConstants.DONT_DISPLAY, DBConstants.INIT_MOVE);   // opy the value
                boolean bIsModified = field.isModified();
                paramField.setModified(bIsModified);
            }
            if (destBuffer != null)
            {   // Copy to buffer also?
                destBuffer.addNextField(paramField);
            }
        }
        if (destBuffer != null)
            destBuffer.finishBuffer();
    }
    /**
     * Initialize the Key Fields.
     * @param iAreaDesc The (optional) temporary area to copy the current fields to.
     * @see BaseField.zeroKeyFields(int).
     */
    public void zeroKeyFields(int iAreaDesc)
    {   // Set up the initial key
        boolean bForceUniqueKey = true;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField thisField = keyField.getField(DBConstants.FILE_KEY_AREA);
            int limitDesc = iAreaDesc;
            if (keyField.getKeyOrder() == DBConstants.DESCENDING)
            {   // If Descending, set to opposite
                if (iAreaDesc == DBConstants.START_SELECT_KEY)
                    limitDesc = DBConstants.END_SELECT_KEY;
                else
                    limitDesc = DBConstants.START_SELECT_KEY;
            }
            thisField.setToLimit(limitDesc);    // Set this field to largest/smallest value possible
            thisField.setModified(false);   // So you can sense a change
            // Match the param fields to the start/end value.
            keyField.getField(iAreaDesc).moveFieldToThis(thisField, false, DBConstants.INIT_MOVE);
            keyField.getField(iAreaDesc).setModified(false);
        }
    }
    /**
     * Get the adjusted field count.
     * Add one key field (the counter field if this isn't a unique key area and you want to force a unique key).
     * @param bForceUniqueKey If params must be unique, if they aren't, add the unique key to the end.
     * @param bIncludeTempFields If true, include any temporary key fields that have been added to the end if this keyarea
     * @return The Key field count.
     */
    public int getKeyFields(boolean bForceUniqueKey, boolean bIncludeTempFields)
    {
        int iKeyFieldCount = super.getKeyFields(bForceUniqueKey, bIncludeTempFields);
        if (!bIncludeTempFields)
        {
            while (iKeyFieldCount > 0)
            {
                if (this.getKeyField(iKeyFieldCount - 1).isTemporary())
                    iKeyFieldCount--;
                else
                    break;
            }
        }
        if (bForceUniqueKey)
            if (this.getUniqueKeyCode() != DBConstants.UNIQUE)
                if (this.getRecord().getCounterField() == this.getRecord().getKeyArea(0).getField(0))
                    iKeyFieldCount++;
        return iKeyFieldCount;
    }
    /**
     * Get this key field taking into account whether the key must be unique.
     * Add one key field (the counter field if this isn't a unique key area and you want to force a unique key).
     * @param bForceUniqueKey If params must be unique, if they aren't, add the unique key to the end.
     * @return The Key field.
     */
    public KeyField getKeyField(int iKeyFieldSeq, boolean bForceUniqueKey)
    {
        KeyField keyField = null;
        if (iKeyFieldSeq != this.getKeyFields(false, true))
            keyField = this.getKeyField(iKeyFieldSeq);
        else
            keyField = this.getRecord().getKeyArea(0).getKeyField(0);  // Special - key must be unique
        return keyField;
    }
    /**
     * Get the field's modified status
     * @param bNonKeyOnly If we are talking about non current key fields only.
     * @return true if any fields have changed.
     */
    public boolean[] getModified()
    {
        boolean bForceUniqueKey = true;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        boolean[] rgbModified = new boolean[iKeyFieldCount];
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
            rgbModified[iKeyFieldSeq] = field.isModified();
        }
        return rgbModified;
    }
    /**
     * Restore the field's modified status to this?
     * @param bNonKeyOnly If we are talking about non current key fields only.
     * @return true if any fields have changed.
     */
    public void setModified(boolean[] rgbModified)
    {
        boolean bForceUniqueKey = true;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
            if (iKeyFieldSeq < rgbModified.length)
                field.setModified(rgbModified[iKeyFieldSeq]);
        }
    }
    /**
     * Get the field's modified status
     * @param bNonKeyOnly If we are talking about non current key fields only.
     * @return true if any fields have changed.
     */
    public boolean[] setNullable(boolean bNullable)
    {
        boolean bForceUniqueKey = true;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        boolean[] rgbNullable = new boolean[iKeyFieldCount];
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
            rgbNullable[iKeyFieldSeq] = field.isNullable();
            field.setNullable(bNullable);
        }
        return rgbNullable;
    }
    /**
     * Restore the field's modified status to this?
     * @param bNonKeyOnly If we are talking about non current key fields only.
     * @return true if any fields have changed.
     */
    public void setNullable(boolean[] rgbNullable)
    {
        boolean bForceUniqueKey = true;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
            if (iKeyFieldSeq < rgbNullable.length)
                field.setNullable(rgbNullable[iKeyFieldSeq]);
        }
    }
    /**
     * Order the keys ascending or descending?
     */
    public void setKeyOrder(boolean bKeyOrder)
    {
        super.setKeyOrder(bKeyOrder);
        boolean bForceUniqueKey = true;
        int iKeyFieldCount = this.getKeyFields(bForceUniqueKey, false);
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++)
        {
            KeyField keyField = this.getKeyField(iKeyFieldSeq, bForceUniqueKey);
            keyField.setKeyOrder(bKeyOrder);
        }
    }
    /**
     * If any of the non-counter fields are null, return true.
     * @param iAreaDesc
     * @return
     */
    public boolean isNull(int iAreaDesc, boolean bIncludeTempFields)
    {
        for (int i = 0; i < this.getKeyFields(false, bIncludeTempFields); i++)
        {
            if (this.getField(i) != this.getRecord().getCounterField())
                if (this.getKeyField(i).getField(iAreaDesc).isNull())
                    return true;    // Is null
        }
        return false;   // is not null
    }
}
