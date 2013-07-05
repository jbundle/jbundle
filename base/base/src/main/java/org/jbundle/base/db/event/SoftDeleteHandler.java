/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)NoDeleteHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Vector;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Flag this record as deleted instead of physically deleting it.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SoftDeleteHandler extends FileListener
{
    protected BaseField m_fldDeleteFlag = null;
    
    protected boolean m_bFilterThisRecord = true;
    
    /**
     * Constructor.
     */
    public SoftDeleteHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public SoftDeleteHandler(BaseField fldDeleteFlag)
    {
        this();
        this.init(null, fldDeleteFlag);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(BaseField fldDeleteFlag)
    {
        this.init(null, fldDeleteFlag);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(Record record, BaseField fldDeleteFlag)
    {
        m_fldDeleteFlag = fldDeleteFlag;
        m_bFilterThisRecord = true;
        super.init(record);
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        m_fldDeleteFlag = null;
        
        super.free();
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
     * Should I display soft-deleted records?
     */
    public void filterThisRecord(boolean bFilterThisRecord)
    {
        m_bFilterThisRecord = bFilterThisRecord;
    }
    /**
     * Set up/do the local criteria.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @return True if you should not skip this record (does a check on the local data).
     */
    public boolean doLocalCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {
        boolean bDontSkip = super.doLocalCriteria(strbFilter, bIncludeFileName, vParamList);
        if (bDontSkip == true)
        {
            if (m_bFilterThisRecord)
                bDontSkip = !this.isRecordSoftDeleted();    // If set, skip it!
        }
        return bDontSkip; // Don't skip (no criteria)
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
        if (iChangeType == DBConstants.DELETE_TYPE)
            if ((this.getOwner().getEditMode() & DBConstants.EDIT_ADD) != DBConstants.EDIT_ADD)
                if (this.isSoftDeleteThisRecord())
        {
            int iErrorCode = this.softDeleteThisRecord();
            
            if (iErrorCode != DBConstants.NORMAL_RETURN)
                return iErrorCode;
            
            try {
                this.getOwner().set();      // Update the record
            } catch (DBException ex) {
                return ex.getErrorCode();
            }
            
            return iErrorCode;
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);   // Initialize the record
    }
    /**
     * Soft delete this record.
     * Set the deleted flag.
     * Override this for different handling
     */
    public int softDeleteThisRecord()
    {
        return m_fldDeleteFlag.setState(true); // Delete
    }
    /**
     * Is this record soft deleted?
     * Override this to decide whether to soft delete or physically delete the record.
     */
    public boolean isRecordSoftDeleted()
    {
        if (m_fldDeleteFlag == null)
            return false;
        if (this.getOwner().isRefreshedRecord())
            return false;   // By default, if this is a brand new record, just delete it.
        return m_fldDeleteFlag.getState();
    }
    /**
     * Soft delete this record?
     * Override this to decide whether to soft delete or physically delete the record.
     */
    public boolean isSoftDeleteThisRecord()
    {
        if (m_fldDeleteFlag == null)
            return false;
        return true;
    }
}
