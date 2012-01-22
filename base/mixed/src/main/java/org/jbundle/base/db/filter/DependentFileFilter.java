/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

/**
 * @(#)DependentFileFilter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * A DependentFileFilter is the base for filters that pass fields or strings that
 * are the initial or end values of a record query.
 * This class makes sure to call the setMainKey method when setInitialKey or setEndKey
 * are called.
 * @version 1.0.0
 * @author    Don Corley
 */
public class DependentFileFilter extends FileFilter
{
    /**
     * A temporary holder for the field sequence until the field can be stored in setOwner().
     */
    protected int m_iThisFileFieldSeq = -1;
    protected int m_iThisFileFieldSeq2 = -1;
    protected int m_iThisFileFieldSeq3 = -1;
    /**
     * The field dependent on the first field of this key area.
     */
    protected BaseField m_fldThisFile = null;
    protected BaseField m_fldThisFile2 = null;
    protected BaseField m_fldThisFile3 = null;
    /**
     * Set the initial key.
     */
    protected boolean m_bInitialKey = true;
    /**
     * Set the end key.
     */
    protected boolean m_bEndKey = true;

    /**
     * DependentFileFilter.
     */
    public DependentFileFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param iFieldSeq The First field sequence of the key.
     * @param iFieldSeq2 The Second field sequence of the key (-1 for none).
     * @param iFieldSeq3 The Third field sequence of the key (-1 for none).
     */
    public DependentFileFilter(int iFieldSeq, int iFieldSeq2, int iFieldSeq3)
    {
        this();
        this.init(null, iFieldSeq, null, iFieldSeq2, null, iFieldSeq3, null);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iFieldSeq The First field sequence of the key.
     * @param fldThisFile TODO
     * @param iFieldSeq2 The Second field sequence of the key (-1 for none).
     * @param fldThisFile2 TODO
     * @param iFieldSeq3 The Third field sequence of the key (-1 for none).
     * @param fldThisFile3 TODO
     */
    public void init(Record record, int iFieldSeq, BaseField fldThisFile, int iFieldSeq2, BaseField fldThisFile2, int iFieldSeq3, BaseField fldThisFile3)
    {
        super.init(record);
        m_iThisFileFieldSeq = iFieldSeq;
        m_fldThisFile = fldThisFile;
        m_iThisFileFieldSeq2 = iFieldSeq2;
        m_fldThisFile2 = fldThisFile2;
        m_iThisFileFieldSeq3 = iFieldSeq3;
        m_fldThisFile3 = fldThisFile3;
        m_bInitialKey = true;
        m_bEndKey = true;
    }
    /**
     * Set the record that owns this listener.
     * This method looks up up all the fields in the record.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner == null)
            return;
        if (m_fldThisFile == null)
            if (m_iThisFileFieldSeq != -1)
                m_fldThisFile = this.getOwner().getField(m_iThisFileFieldSeq);
        if (m_fldThisFile2 == null)
            if (m_iThisFileFieldSeq2 != -1)
                m_fldThisFile2 = this.getOwner().getField(m_iThisFileFieldSeq2);
        if (m_fldThisFile2 == null)
            if (m_iThisFileFieldSeq3 != -1)
                m_fldThisFile3 = this.getOwner().getField(m_iThisFileFieldSeq3);
//?        if (m_fldThisFile != null)
//?            m_fldThisFile.saveEnableListeners(false);     // Don't let behaviors mess with my values.
//?        if (m_fldThisFile2 != null)
//?            m_fldThisFile2.saveEnableListeners(false);    // Note: This is because of a conflict with.
//?        if (m_fldThisFile3 != null)
//?            m_fldThisFile3.saveEnableListeners(false);    // InitOnceFieldHandler, the value is only set once.
//x        this.setMainKey(true, null);    // Initialize the keys
    }
    /**
     * Setup the initial key position in this record... Save it!
     */
    public void doInitialKey()
    {
        if (m_bInitialKey)
            this.setMainKey(DBConstants.DONT_DISPLAY, Boolean.TRUE, true);    // Set up the key (mark all fields as changed)
        super.doInitialKey();   // This is the starting key, set the initial position
    }
    /**
     * Setup the end key position in this record... Save it!
     */
    public void doEndKey()
    {
        if (m_bEndKey)
            this.setMainKey(DBConstants.DONT_DISPLAY, Boolean.TRUE, true);    // Set up the key (mark all fields as changed)
        super.doEndKey();
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        super.doNewRecord(bDisplayOption);              // Initialize the record
        this.setMainKey(bDisplayOption, Boolean.FALSE, true);   // Set up the key (DO NOT mark all fields as changed)
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * Before an add, set the key back to the original value.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);      // Initialize the record
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        boolean bSetIfModified = true;
        switch (iChangeType)
        {
            case DBConstants.UPDATE_TYPE:   // This should not be necessary (but I do it anyway).
                if (!this.getOwner().isRefreshedRecord())
                    break;  // No first add
                bSetIfModified = false;
            case DBConstants.ADD_TYPE:
                boolean bNonNulls = this.setMainKey(bDisplayOption, null, bSetIfModified);              // Set up the key (keys are marked as changed if they change!)
                if ((bNonNulls == false) && ((this.getOwner().getMasterSlave() & RecordOwner.SLAVE) == 0))  // Don't return for a server!
                {
                    if (this.getOwner().getTask() != null)
                        return this.getOwner().getTask().setLastError("Main key cannot be null");
                    return DBConstants.ERROR_RETURN;    // Key can't be null!
                }
                break;
        }
        return iErrorCode;      // Initialize the record
    }
    /**
     * If true, end key position is valid.
     * @param flag If true, end key position is valid.
     */
    public void setEndKey(boolean flag)
    {
        m_bEndKey = flag;
    }
    /**
     * If true, start key position is valid.
     * @param flag If true, start key position is valid.
     */
    public void setInitialKey(boolean flag)
    {
        m_bInitialKey = flag;
    }
    /**
     * Setup the target key field.
     * Disable/Enable the key fields from responding to actions.
     * Override this method to set the actual data.
     * @param bDisplayOption If true, display changes.
     * @param boolSetModified - If not null, set this field's modified flag to this value.
     * @param bSetIfModified Set if modified.
     * @return false If this key was set to all nulls.
     */
    public boolean setMainKey(boolean bDisplayOption, Boolean boolSetModified, boolean bSetIfModified)
    {
//        if (m_fldThisFile != null)
  //          m_fldThisFile.setEnabled(false);    // Disable this field from input
    //    if (m_fldThisFile2 != null)
      //      m_fldThisFile2.setEnabled(false);   // Disable this field from input
        //if (m_fldThisFile3 != null)
          //  m_fldThisFile3.setEnabled(false);   // Disable this field from input
        return true;
    }
}
