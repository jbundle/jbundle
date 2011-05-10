package org.jbundle.thin.base.db;

/**
 * @(#)KeyArea.java   1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.Serializable;
import java.util.Vector;

import org.jbundle.model.db.Field;
import org.jbundle.model.db.Key;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.buff.BaseBuffer;


/**
 * A KeyArea describes a particular key area (fields and order).
 * KeyArea - Definition of this key area for the thin implementation.
 * Be very careful when using this implementation, because the internal
 * data representation is much different than a KeyArea (in the thick model).
 * In this (thin) model the m_vKeyFieldList is used to save the actual fields,
 * where in the thick model this field is used to store KeyField(s).
 * Also, since there is not a KeyField to store temporary data, there is a
 * tempDataArea to store the field values (see compareKeys() and setupKeyBuffer()).
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class KeyAreaInfo extends Object
    implements Key, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * The parent fieldlist.
     */
    protected FieldList m_record = null;              // File of this key area
    /**
     * The key type (UNIQUE/NOT_UNIQUE/SECONDARY).
     */
    protected int m_iKeyDup = Constants.UNIQUE;
    /**
     * The key field list.
     * Note: In thin, these are fields, in thick these are KeyFields.
     */
    protected Vector<Object> m_vKeyFieldList = null;
    /**
     * The Key Name.
     */
    protected String m_strKeyName = null;
    /**
     * The m_rgobjTempData is a replacement for the temporary field area's for the thin model.
     * If setupKeyBuffer() is called, this array will contain the current data for this key
     * area's fields.
     * @see setupKeyBuffer.
     */
    protected Object[] m_rgobjTempData = null;
    /**
     * Order the keys ascending or descending?
     * NOTE: Only used in Thin.
     */
    protected boolean m_bKeyOrder = Constants.ASCENDING;

    /**
     * Constructor.
     */
    public KeyAreaInfo()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param iKeyDup The type of key (UNIQUE/NOT_UNIQUE/SECONDARY).
     * @param strKeyName The name of this key (default to first fieldnameKEY).
     */
    public KeyAreaInfo(FieldList record, int iKeyDup, String strKeyName)
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
        m_vKeyFieldList = new Vector<Object>();
        m_record = (FieldList)record;
        m_iKeyDup = iKeyDup;
        m_strKeyName = strKeyName;
        if (m_record != null)
            m_record.addKeyArea(this);
        if (strKeyName != null)
            m_strKeyName = strKeyName;
        else
        {
            if (m_record != null) if (m_record.getKeyAreaCount() == 1)
                m_strKeyName = Constants.PRIMARY_KEY; // Default Primary key name
        }
    }
    /**
     * Release the Objects in this KeyArea.
     */
    public void free()
    {
        m_record.removeKeyArea(this);
        m_record = null;

        m_vKeyFieldList.removeAllElements();
        m_vKeyFieldList = null;
        m_strKeyName = null;            // Key Name
    }
    /**
     * Add this field to this Key Area.
     * @param iFieldSeq The field to add.
     * @param bKeyArea The order (ascending/descending).
     */
    public void addKeyField(int iFieldSeq, boolean bKeyOrder)
    {
        this.addKeyField(m_record.getField(iFieldSeq), bKeyOrder);
    }
    /**
     * Add this field to this Key Area.
     * @param strFieldName The field to add.
     * @param bKeyArea The order (ascending/descending).
     */
    public void addKeyField(String strFieldName, boolean bKeyOrder)
    {
        this.addKeyField(m_record.getField(strFieldName), bKeyOrder);
    }
    /**
     * Add this field to this Key Area.
     * @param field The field to add.
     * @param bKeyArea The order (ascending/descending).
     */
    public void addKeyField(Field field, boolean bKeyOrder)
    { // Get the field with this seq
        if (m_vKeyFieldList.size() == 0)
            m_bKeyOrder = bKeyOrder;
        m_vKeyFieldList.add(field);   // init the key field seq
    }
    /**
     * Get the Field in this KeyField.
     * @param iKeyFieldSeq The position of this field in the key area.
     * @return The field.
     */
    public FieldInfo getField(int iKeyFieldSeq)
    {
        if (iKeyFieldSeq >= m_vKeyFieldList.size())
            return null;
        return (FieldInfo)m_vKeyFieldList.elementAt(iKeyFieldSeq);
    }
    /**
     * Key area count.
     * @return key field count.
     */
    public int getKeyFields()
    {
        return this.getKeyFields(false, false);
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
        return m_vKeyFieldList.size();
    }
    /**
     * Get the key name.
     * @return The key name for this key area.
     */
    public String getKeyName()
    {
        return m_strKeyName;
    }
    /**
     * Get the key order for this key field (Ascending/Descending).
     * Note: This is not implemented for thin.
     * @param iKeyFieldSeq The field to check.
     * @return true if ascending order.
     */
    public boolean getKeyOrder(int keyFieldSeq)
    {
        return true;
    }
    /**
     * Get the parent record.
     * @return The parent record.
     */
    public FieldList getRecord()
    {
        return m_record;
    }
    /**
     * Is this a unique key?
     * @return The unique key code (Unique/Not unique/secondary).
     */
    public int getUniqueKeyCode()
    {
        return m_iKeyDup;
    }
    /**
     * Move the key area to the record.
     * @param destBuffer A BaseBuffer to fill with data (ignored for thin).
     * @param iAreaDesc The (optional) temporary area to copy the current fields to ().
     */
    public void reverseKeyBuffer(BaseBuffer bufferSource, int iAreaDesc)        // Move these keys back to the record
    {
        int iKeyFields = this.getKeyFields();
        for (int iKeyFieldSeq = Constants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFields + Constants.MAIN_KEY_FIELD; iKeyFieldSeq++)
        {
            this.getField(iKeyFieldSeq).setData(m_rgobjTempData != null ? m_rgobjTempData[iKeyFieldSeq] : null);
        }
    }
    /**
     * Set up the key area indicated.
     * <br />Note: The thin implementation is completely different from the thick implementation
     * here, the areadesc is ignored and the thin data data area is set up instead.
     * @param destBuffer (Always ignored for the thin implementation).
     * @param iAreaDesc (Always ignored for the thin implementation).
     */
    public void setupKeyBuffer(BaseBuffer destBuffer, int iAreaDesc)
    {
        int iKeyFields = this.getKeyFields();
        m_rgobjTempData = new Object[iKeyFields];
        for (int iKeyFieldSeq = Constants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFields + Constants.MAIN_KEY_FIELD; iKeyFieldSeq++)
        {
            m_rgobjTempData[iKeyFieldSeq] = this.getField(iKeyFieldSeq).getData();
        }
    }
    /**
     * Initialize the Key Fields.
     * @param iAreaDesc The (optional) temporary area to copy the current fields to.
     * @see BaseField.zeroKeyFields(int).
     */
    public void zeroKeyFields(int iAreaDesc)
    {   // Set up the initial key
        m_rgobjTempData = null;
    }
    /**
     * Compare these two keys and return the compare result.
     * <p />Note: The thin implementation is completely different from the thick implementation
     * here, the areadesc is ignored and the thin data data area is compared an returned.
     * @param iAreaDesc (Always ignored for the thin implementation).
     * @return The compare result (see compareTo method).
     */
    public int compareKeys(int iAreaDesc)
    {
        int iCompareValue = 0;
        int iKeyFields = this.getKeyFields() + Constants.MAIN_KEY_FIELD;
        for (int iKeyFieldSeq = Constants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFields; iKeyFieldSeq++)
        {
            FieldInfo fldCurrent = this.getField(iKeyFieldSeq);
            Object fldTemp = m_rgobjTempData != null ? m_rgobjTempData[iKeyFieldSeq] : null;
            iCompareValue = fldCurrent.compareTo(fldTemp);
            if (iCompareValue != 0)
                break;
        }
        return iCompareValue;
    }
    /**
     * Order the keys ascending or descending?
     */
    public void setKeyOrder(boolean bKeyOrder)
    {
        m_bKeyOrder = bKeyOrder;
    }
    /**
     * Order the keys ascending or descending?
     * NOTE: Only used in Thin.
     */
    public boolean getKeyOrder()
    {
        return m_bKeyOrder;
    }
}
