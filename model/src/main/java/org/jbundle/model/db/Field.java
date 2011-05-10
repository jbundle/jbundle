package org.jbundle.model.db;

/**
 * @(#)FieldInfo.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */




/**
 * FieldInfo - This is the base class for all fields.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface Field extends Convert
{
    /**
     * Initialize this field.
     * After setting up the input params, initField() is called.
     * @param record The parent record. This field is added to the record's field list.
     * @param strName The field name.
     * @param iDataLength The maximum length (DEFAULT_FIELD_LENGTH if default).
     * @param strDesc The string description (pass null to use the description in the resource file).
     * @param objDefault The default (initial) field value in string or native format.
     */
    public void init(Rec record, String strName, int iDataLength, String strDesc, Object objDefault);
    /**
     * Get the component at this position.
     * @return The component at this position or null.
     */
    public Object getComponent(int iPosition);
    /**
     * Compare field to this and return < > or = (-,+,0).
     * @return compare value.
     */
    public int compareTo(Field field);
    /**
     * Compare field to this and return < > or = (-,+,0).
     * @return compare value.
     */
    public int compareTo(Object dataCompare);
    /**
     * Display this field.
     * Go through the sFieldList and setText for JTextComponents and setControlValue for
     * FieldComponents.
     * @see org.jbundle.model.db.FieldComponent
     */
    public void displayField();
    /**
     * Set the field description.
     * @param strFieldDesc The field desc.
     */
    public void setFieldDesc(String strFieldDesc);
    /**
     * Get the long field description tip.
     * Look in the resource for the long description, if not found, return the field desc.
     * @return The field tip.
     */
    public String getFieldTip();
    /**
     * Get this field's Record.
     * @return The record.
     */
    public Rec getRecord();
    /**
     * Get this field's name.
     * @param bAddQuotes All quotes to the name.
     * @param bInclude the FileName (ie., filename.fieldname).
     * @return The field name.
     */
    public String getFieldName(boolean bAddQuotes, boolean bIncludeFileName);
    /**
     * Set this field's name.
     * @param sString The field name.
     */
    public void setFieldName(String sString);
    /**
     * Set this field back to the original value.
     * @param bDisplayOption If true, display the data.
     * @return The error code.
     */
    public int initField(boolean bDisplayOption);
    /**
     * Is this field null?
     * @return true if the data is null or the current length is 0.
     */
    public boolean isNull();
    /**
     * Set this field's Record.
     * WARNING - You must add this field to the record manually.
     * @param record The record to set.
     */
    public void setRecord(Rec record);
    /**
     * Get the class of the m_data field.
     * @return the java class of the raw data.
     */
    public Class<?> getDataClass();
    /**
     * Set the class of the m_data field.
     * @param classData the java class of the raw data.
     */
    public void setDataClass(Class<?> classData);
    /**
     * Is this field active?
     * Used primarily for sql selects.
     * @return true if selected.
     */
    public boolean isSelected();
    /**
     * Is this a virtual field?
     * A virtual field does not actually carry data, it simply reflects another field (usually in a different format).
     * @return true if virtual.
     */
    public boolean isVirtual();
    /**
     * Set the default value.
     * @param objDefault The value to set (must be in string or native format).
     */
    public void setDefault(Object objDefault);
    /**
     * Get the default value.
     * @return The default value.
     */
    public Object getDefault();
    /**
     * Get the number of fractional digits.
     */
    public void setScale(int iScale);
    /**
     * Does this field prefer to be hidden on displays?
     */
    public boolean isHidden();
    /**
     * Set whether this field prefers to be hidden on displays.
     */
    public void setHidden(Boolean bHidden);
    /**
     * Get the dirty flag.
     * @return True is this field has been changes since the last init.
     */
    public boolean isModified();
    /**
     * Set the dirty flag.
     * @param flag If true, this field is intrepreted as modified.
     */
    public void setModified(boolean flag);
}
