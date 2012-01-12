/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

/**
 * @(#)SubFileFilter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileRemoveBOnCloseHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.field.event.FieldReSelectHandler;
import org.jbundle.base.field.event.FieldRemoveBOnCloseHandler;
import org.jbundle.base.field.event.InitIfSubFieldHandler;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * A SubFileFilter sets up a sub-file query that includes records for this
 * main record (or main fields).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SubFileFilter extends DependentFileFilter
{
    /**
     * The main record for this query.
     */
    protected Record m_recordMain = null;
    /**
     * The field that the first key field will be set to (null if record is specified).
     */
    protected BaseField m_fldMainFile = null;
    protected BaseField m_fldMainFile2 = null;
    protected BaseField m_fldMainFile3 = null;
    /**
     * If true, this will filter if the target field(s) are null (usually a empty query set).
     */
    protected boolean m_bSetFilterIfNull = false;
    /**
     * If true, this class will refresh the last record if the record is not current.
     */
    protected boolean m_bRefreshLastIfNotCurrent = false;
    /**
     * The key order.
     */
    protected int m_iKeyOrder = -1;
    /**
     * 
     */
    protected boolean m_bAddNewHeaderOnAdd = true;

    /**
     * Constructor.
     */
    public SubFileFilter()
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        super();
    }
    /**
     * Constructor.
     * @param fldMainFile First field in the key fields.
     * @param fldMainFile2 Second field in the key fields.
     * @param fldMainFile3 Third field in the key fields.
     * @param iFieldSeq The First field sequence of the key.
     * @param iFieldSeq2 The Second field sequence of the key (-1 for none).
     * @param iFieldSeq3 The Third field sequence of the key (-1 for none).
     */
    public SubFileFilter(BaseField fldMainFile, int iFieldSeq, BaseField fldMainFile2, int iFieldSeq2, BaseField fldMainFile3, int iFieldSeq3)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, null, -1, fldMainFile, iFieldSeq, fldMainFile2, iFieldSeq2, fldMainFile3, iFieldSeq3, false, false, true);
    }
    /**
     * Constructor.
     * @param fldMainFile First field in the key fields.
     * @param fldMainFile2 Second field in the key fields.
     * @param fldMainFile3 Third field in the key fields.
     * @param iFieldSeq The First field sequence of the key.
     * @param iFieldSeq2 The Second field sequence of the key (-1 for none).
     * @param iFieldSeq3 The Third field sequence of the key (-1 for none).
     */
    public SubFileFilter(BaseField fldMainFile, int iFieldSeq, BaseField fldMainFile2, int iFieldSeq2, BaseField fldMainFile3, int iFieldSeq3, boolean bSetFilterIfNull)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, null, -1, fldMainFile, iFieldSeq, fldMainFile2, iFieldSeq2, fldMainFile3, iFieldSeq3, bSetFilterIfNull, false, true);
    }
    /**
     * Constructor for Records with properly set-up reference fields.
     * If a key area's main key is a reference to this record, then this class sets up
     * a sub-query to this listener's owner (a record).
     * @param recordMain The main record to create a sub-query for.
     */
    public SubFileFilter(Record recordMain)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, recordMain, -1, null, -1, null, -1, null, -1, false, false, true);
    }
    /**
     * Constructor for Records with properly set-up reference fields.
     * If a key area's main key is a reference to this record, then this class sets up
     * a sub-query to this listener's owner (a record).
     * @param recordMain The main record to create a sub-query for.
     * @param bRefreshLastIfNotCurrent If true, this class will refresh the last record if the record is not current.
     * @param bRefreshLastIfNotCurrent (Typically used for remote sessions where the remote method does an add before the detail can add).
     */
    public SubFileFilter(Record recordMain, boolean bSetFilterIfNull, boolean bAddNewHeaderOnAdd)
    {   // For this to work right, the key field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, recordMain, -1, null, -1, null, -1, null, -1, bSetFilterIfNull, false, bAddNewHeaderOnAdd);
    }
    /**
     * Constructor for Records with properly set-up reference fields.
     * If a key area's main key is a reference to this record, then this class sets up
     * a sub-query to this listener's owner (a record).
     * @param recordMain The main record to create a sub-query for.
     * @param bRefreshLastIfNotCurrent If true, this class will refresh the last record if the record is not current.
     * @param bRefreshLastIfNotCurrent (Typically used for remote sessions where the remote method does an add before the detail can add).
     */
    public SubFileFilter(Record recordMain, boolean bSetFilterIfNull)
    {   // For this to work right, the key field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, recordMain, -1, null, -1, null, -1, null, -1, bSetFilterIfNull, false, true);
    }
    /**
     * Constructor for Records with properly set-up reference fields.
     * If a key area's main key is a reference to this record, then this class sets up
     * a sub-query to this listener's owner (a record).
     * @param recordMain The main record to create a sub-query for.
     * @param bRefreshLastIfNotCurrent If true, this class will refresh the last record if the record is not current.
     * @param bRefreshLastIfNotCurrent (Typically used for remote sessions where the remote method does an add before the detail can add).
     */
    public SubFileFilter(int iKeyOrder, BaseField fldMainFile)
    {   // For this to work right, the key field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, null, iKeyOrder, fldMainFile, -1, null, -1, null, -1, false, false, true);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recordMain The main record to create a sub-query for.
     * @param fldMainFile First field in the key fields.
     * @param fldMainFile2 Second field in the key fields.
     * @param fldMainFile3 Third field in the key fields.
     * @param iFieldSeq The First field sequence of the key.
     * @param iFieldSeq2 The Second field sequence of the key (-1 for none).
     * @param iFieldSeq3 The Third field sequence of the key (-1 for none).
     * @param bSetFilterIfNull If true, this will filter if the target field(s) are null (usually a empty query set) (defaults to true [no filter = all records]).
     * @param bRefreshLastIfNotCurrent If true, this class will refresh the last record if the record is not current.
     * @param bRefreshLastIfNotCurrent (Typically used for remote sessions where the remote method does an add before the detail can add).
     */
    public void init(Record record, Record recordMain, int iKeyOrder, BaseField fldMainFile, int iFieldSeq, BaseField fldMainFile2, int iFieldSeq2, BaseField fldMainFile3, int iFieldSeq3, boolean bSetFilterIfNull, boolean bRefreshLastIfNotCurrent, boolean bAddNewHeaderOnAdd)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        super.init(record, iFieldSeq, null, iFieldSeq2, null, iFieldSeq3, null);

        m_recordMain = recordMain;
        
        m_iKeyOrder = iKeyOrder;
        
        m_fldMainFile = fldMainFile;
        m_fldMainFile2 = fldMainFile2;
        m_fldMainFile3 = fldMainFile3;

        m_bSetFilterIfNull = bSetFilterIfNull;
        m_bRefreshLastIfNotCurrent = bRefreshLastIfNotCurrent;
        m_bAddNewHeaderOnAdd = bAddNewHeaderOnAdd;

        if (fldMainFile != null)
            fldMainFile.addListener(new FieldRemoveBOnCloseHandler(this));  // Remove this if you close the file first
        else if (recordMain != null)
            recordMain.addListener(new FileRemoveBOnCloseHandler(this));
    }
    /**
     * Set the record that owns this listener.
     * If a record is passed in, this method makes sure the correct key area is set on this record.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner == null)
            return;
        if (m_recordMain != null)
        {   // If they are using the main record, then make sure the key area is correct.
            ReferenceField fieldThisRecord = this.getOwner().getReferenceField(m_recordMain);
            if (fieldThisRecord != null)
                if (fieldThisRecord.getReferenceRecord(null, false) == null)
                    fieldThisRecord.setReferenceRecord(m_recordMain);   // Make sure these are linked
            this.getOwner().setKeyArea(fieldThisRecord);
            // Listen carefully:
            // If the main record belongs to another screen and the screen is closed,
            // do not change the sub-field.
            if (fieldThisRecord != null)
                if (this.getOwner().getRecordOwner() != null)
                    if (m_recordMain.getRecordOwner() != null)
                        if (this.getOwner().getRecordOwner() != m_recordMain.getRecordOwner())
                            fieldThisRecord.addListener(new InitIfSubFieldHandler(null));
        }
        else if ((m_iKeyOrder != -1) && (m_fldThisFile == null))
        {
        	this.getOwner().setKeyArea(m_iKeyOrder);
        	if (m_fldMainFile != null)
        		m_fldThisFile = this.getOwner().getKeyArea().getField(0);
        	if (m_fldMainFile2 != null)
        		m_fldThisFile2 = this.getOwner().getKeyArea().getField(1);
        	if (m_fldMainFile3 != null)
        		m_fldThisFile3 = this.getOwner().getKeyArea().getField(2);
        }
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        SubFileFilter newBehavior = null;
        if (m_fldMainFile != null)
            newBehavior = new SubFileFilter(m_fldMainFile, m_iThisFileFieldSeq, m_fldMainFile2, m_iThisFileFieldSeq2, m_fldMainFile3, m_iThisFileFieldSeq3);
        else if (m_recordMain != null)
            newBehavior = new SubFileFilter(m_recordMain);
        return newBehavior;
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
        int iErrorCode = DBConstants.NORMAL_RETURN;
        switch (iChangeType)
        {
            case DBConstants.LOCK_TYPE:
                if (this.getMainRecord() != null)
                    iErrorCode = this.getMainRecord().handleRecordChange(this.getMainFileKeyField(false), DBConstants.FIELD_CHANGED_TYPE, bDisplayOption); // Tell the main file that I changed (so it can lock/whatever)
                break;
//            case DBConstants.AFTER_REFRESH_TYPE:
            case DBConstants.UPDATE_TYPE:   // If refresh is set, it is possible that first add is UPDATE_TYPE
                if (!this.getOwner().isRefreshedRecord())
                    break;  // No first add
            case DBConstants.ADD_TYPE:
                boolean bOldSelect = this.enableReselect(false);
                if (this.getMainRecord() != null)
                {
                    if (this.getMainRecord().getEditMode() == DBConstants.EDIT_ADD)
                    {
                        if (!m_bAddNewHeaderOnAdd)
                            return this.getOwner().getTask().setLastError("Can't add detail without a header record");
                        m_bMainRecordChanged = true;
                    }
                    iErrorCode = this.getMainRecord().handleRecordChange(this.getMainFileKeyField(false), DBConstants.FIELD_CHANGED_TYPE, bDisplayOption); // Tell the main file that I changed (so it can lock/whatever)
                    iErrorCode = DBConstants.NORMAL_RETURN; // Ignore error on lock main!
                }
                this.enableReselect(bOldSelect);
                break;
            case DBConstants.AFTER_ADD_TYPE:
// Note: This code is not necessary, since the newly added record is now a member of the new header record a refresh is not necessary!
//                if (m_bMainRecordChanged)
//                    iErrorCode = this.getMainFileKeyField(true).handleFieldChanged(bDisplayOption, DBConstants.SCREEN_MOVE); // Tell the main file that I changed (so it can lock/whatever)
                break;
            default:
                m_bMainRecordChanged = false;
                break;
        }
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        return super.doRecordChange(field, iChangeType, bDisplayOption);   // Initialize the record
    }
    
    protected boolean m_bMainRecordChanged = false;
    
    public boolean enableReselect(boolean bEnable)
    {
        boolean bOldReselect = false;
        if (this.getMainFileKeyField(true) != null)
            if (this.getMainFileKeyField(true).getListener(FieldReSelectHandler.class) != null)
                bOldReselect = this.getMainFileKeyField(true).getListener(FieldReSelectHandler.class).setEnabledListener(bEnable);
        return bOldReselect;
    }
    public BaseField getMainFileKeyField(boolean bReturnCounterField)
    {
        if (m_fldMainFile != null)
            return m_fldMainFile;
        if (m_recordMain != null)
            if (bReturnCounterField)
                return (BaseField)m_recordMain.getCounterField();
        return null;
    }
    /**
     * Setup the target key field.
     * Sets the main key field(s) to the current main record/field fields.
     * @oaram bDisplayOption If true, display changes.
     * @param boolSetModified - If not null, set this field's modified flag to this value
     * @return false If this key was set to all nulls.
     */
    public boolean setMainKey(boolean bDisplayOption, Boolean boolSetModified, boolean bSetIfModified)
    {
        super.setMainKey(bDisplayOption, boolSetModified, bSetIfModified);
        boolean bNonNulls = false;  // Default to yes, all keys are null.
        if (m_fldMainFile != null)
            if ((m_bSetFilterIfNull) || (!m_fldMainFile.isNull()))
        {
            if ((bSetIfModified) || (!m_fldThisFile.isModified()) || (m_fldThisFile.isNull()))
                m_fldThisFile.moveFieldToThis(m_fldMainFile, bDisplayOption, DBConstants.READ_MOVE);
            if (boolSetModified != null)
                m_fldThisFile.setModified(boolSetModified.booleanValue());
            if (!m_fldThisFile.isNull())
                bNonNulls = true;   // Non null.
        }
        if (m_fldMainFile2 != null)
            if ((m_bSetFilterIfNull) || (!m_fldMainFile2.isNull()))
        {
            if ((bSetIfModified) || (!m_fldThisFile2.isModified()) || (m_fldThisFile2.isNull()))
                m_fldThisFile2.moveFieldToThis(m_fldMainFile2, bDisplayOption, DBConstants.READ_MOVE);
            if (boolSetModified != null)
                m_fldThisFile2.setModified(boolSetModified.booleanValue());
            if (!m_fldThisFile2.isNull())
                bNonNulls = true;   // Non null.
        }
        if (m_fldMainFile3 != null)
            if ((m_bSetFilterIfNull) || (!m_fldMainFile3.isNull()))
        {
            if ((bSetIfModified) || (!m_fldThisFile3.isModified()) || (m_fldThisFile3.isNull()))
                m_fldThisFile3.moveFieldToThis(m_fldMainFile3, bDisplayOption, DBConstants.READ_MOVE);
            if (boolSetModified != null)
                m_fldThisFile3.setModified(boolSetModified.booleanValue());
            if (!m_fldThisFile3.isNull())
                bNonNulls = true;   // Non null.
        }
        if (m_recordMain != null)
        {
            ReferenceField fieldThisRecord = this.getOwner().getReferenceField(m_recordMain);
            if (m_bRefreshLastIfNotCurrent)
                if (m_recordMain.getEditMode() == DBConstants.EDIT_NONE)
            {   // If there is not a current record refresh the last record.
                try {
                    Object bookmark = m_recordMain.getLastModified(DBConstants.DATA_SOURCE_HANDLE);
                    if (bookmark != null)
                        m_recordMain.setHandle(bookmark, DBConstants.DATA_SOURCE_HANDLE);
                } catch (DBException ex)    {
                    ex.printStackTrace();
                }
            }
            if ((m_bSetFilterIfNull) || (!m_recordMain.getCounterField().isNull()))
            {
                if ((bSetIfModified) || (!fieldThisRecord.isModified()) || (fieldThisRecord.isNull()))
                    fieldThisRecord.setReference(m_recordMain, bDisplayOption, DBConstants.READ_MOVE);    // Set the booking number in pax file
                if (boolSetModified != null)
                    fieldThisRecord.setModified(boolSetModified.booleanValue());
            }
            if (!fieldThisRecord.isNull())
                bNonNulls = true;   // Non null.
        }
        return bNonNulls;
    }
    /**
     * Get the main record for this sub file.
     */
    public Record getMainRecord()
    {
        if (m_fldMainFile != null)
            return m_fldMainFile.getRecord();
        return m_recordMain;
    }
    /**
     * 
     */
    public boolean setFilterIfNull(boolean bSetFilterIfNull)
    {
        boolean bOldValue = m_bSetFilterIfNull;
        m_bSetFilterIfNull = bSetFilterIfNull;
        return bOldValue;
    }
    /**
     * 
     * @param bAddNewHeaderOnAdd
     */
    public void setAddNewHeaderOnAdd(boolean bAddNewHeaderOnAdd)
    {
        m_bAddNewHeaderOnAdd = bAddNewHeaderOnAdd;
    }
    /**
     * Get the foreign field that references this record.
     * There can be more than one, so supply an index until you get a null.
     * @param iCount The index of the reference to retrieve
     * @return The referenced field
     */
    public BaseField getReferencedField(int iIndex)
    {
        if (m_recordMain != null)
            return m_recordMain.getKeyArea().getField(iIndex);
        if (iIndex == 0)
            return m_fldMainFile;
        if (iIndex == 1)
            return m_fldMainFile2;
        if (iIndex == 2)
            return m_fldMainFile3;
        return null;
    }
}
