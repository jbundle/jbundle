/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)NoDeleteHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ResourceConstants;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Can't add or change this record unless this field is valid.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ValidateFieldHandler extends FileListener
{
    /**
     *
     */
    protected int m_iFieldSeq = -1;
    protected String fieldName = null;
    /**
     *
     */
    protected String m_strCompare = null;
    /**
     *
     */
    protected boolean m_bValidIfMatch = true;
    
    /**
     * Constructor.
     */
    public ValidateFieldHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public ValidateFieldHandler(int iFieldSeq, String strCompare, boolean bValidIfMatch)
    {
        this();
        this.init(null, iFieldSeq, null, strCompare, bValidIfMatch);
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public ValidateFieldHandler(String fieldName, String strCompare, boolean bValidIfMatch)
    {
        this();
        this.init(null, -1, fieldName, strCompare, bValidIfMatch);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(Record record, int iFieldSeq, String fieldName, String strCompare, boolean bValidIfMatch)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        m_iFieldSeq = iFieldSeq;
        this.fieldName = fieldName;
        m_strCompare = strCompare;
        m_bValidIfMatch = bValidIfMatch;
        super.init(record);
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null;        // DO NOT clone this behavior as it is almost always explicitly added in record.addListeners().
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * On an add or lock, makes sure the main key field is set to the current main target field
     * so it will be a child of the current main record.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * Before an add, set the key back to the original value.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        if ((iChangeType == DBConstants.ADD_TYPE)
            || (iChangeType == DBConstants.UPDATE_TYPE))
        {
            BaseField fld = null;
            if (fieldName != null)
                fld = this.getOwner().getField(fieldName);
            else
                fld = this.getOwner().getField(m_iFieldSeq);
            boolean bValid = this.checkValidField(fld);
            if (!bValid)
            {
                String strError = "can't equal";
                if (m_bValidIfMatch)
                    strError = "must equal";
                org.jbundle.model.Task task = null;
                if (this.getOwner().getRecordOwner() != null)
                    task = this.getOwner().getRecordOwner().getTask();
                if (task != null)
                    strError = ((BaseApplication)task.getApplication()).getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strError);
                strError = fld.getFieldDesc() + ' ' + strError + ' ' + m_strCompare;
                if (task != null)
                    return this.getOwner().getRecordOwner().getTask().setLastError(strError);
                return DBConstants.ERROR_RETURN;
            }
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);   // Initialize the record
    }
    /**
     *
     */
    public boolean checkValidField(BaseField field)
    {
        boolean bMatch = false;
        String strValue = field.toString();
        if ((strValue == m_strCompare)
            || ((strValue == null) && (m_strCompare.equalsIgnoreCase(DBConstants.BLANK)))
            || ((DBConstants.BLANK.equals(strValue)) && (m_strCompare == null))
            || ((strValue != null) && (strValue.equalsIgnoreCase(m_strCompare))))
                bMatch = true;
        if (!m_bValidIfMatch)
            bMatch = !bMatch;
        return bMatch;    // Valid
    }
}
