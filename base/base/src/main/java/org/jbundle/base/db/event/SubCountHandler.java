/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)CSubCountBehavior.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.event.FieldRemoveBOnCloseHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Listener for maintaining a count of sub-file records in the main file.
 * NOTE: This class also implements the old FieldSubCountHandler class, using a different constructor.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SubCountHandler extends FileListener
{
    /**
     * The header field this handler counts to.
     */
    protected BaseField m_fldMain = null;
    /**
     * The original value of the field so I can adjust the count on update or delete.
     */
    protected double m_dOldValue = 0;
    /**
     * Should I recount the file on requery?
     * ie., The total will not be reset to 0 on a new query.
     */
    protected boolean m_bRecountOnSelect = true;
    /**
     * If I hit an EOF, should I double-check the total.
     * ie., The visual total is NOT updated until EOF is hit.
     */
    protected boolean m_bVerifyOnEOF = true;
    /**
     * Reset the counter on a control break?
     */
    protected boolean m_bResetOnBreak = false;
    /**
     * Variable used to keep track of the total.
     */
    protected double m_dTotalToVerify = 0;
    /**
     * Has the EOF been hit?
     */
    protected boolean m_bEOFHit = true;
    /**
     * The target field to receive the count.
     */
    protected String fsToCount = null;
    /**
     * The (optional) field break.
     */
    protected BaseField m_fldBreak = null;
    /**
     * Constant to mark the first time.
     */
    public static final Object FIRST_TIME = new Object();
    /**
     * Last value of the fldBreak (to see if there is a break).
     */
    protected Object m_objLastBreakValue = FIRST_TIME;

    /**
     * Constructor.
     */
    public SubCountHandler()    // Init this field override for other value
    {
        super();
    }
    /**
     * Constructor for counting the sub-records in this file.
     * @param fieldMain The field to receive the count.
     * @param bVerifyOnEOF Verify the total on End of File (true default).
     * @param bRecountOnSelect Recount the total each time a file select is called (False default).
     */
    public SubCountHandler(BaseField fieldMain, boolean bRecountOnSelect, boolean bVerifyOnEOF)
    {
        this();
        this.init(null, null, null, fieldMain, null, bRecountOnSelect, bVerifyOnEOF, false);
    }
    /**
     * Constructor for counting the value of a field in this record.
     * @param fieldMain The field to receive the count.
     * @param ifsToCount The field in this record to add up.
     * @param bVerifyOnEOF Verify the total on End of File (true default).
     * @param bRecountOnSelect Recount the total each time a file select is called (False default).
     */
    public SubCountHandler(BaseField fieldMain, String fsToCount, boolean bRecountOnSelect, boolean bVerifyOnEOF)   // Init this field override for other value
    {
        this();
        this.init(null, null, null, fieldMain, fsToCount, bRecountOnSelect, bVerifyOnEOF, false);
    }
    /**
     * Constructor for counting the value of a field in this record.
     * @param fieldMain The field to receive the count.
     * @param ifsToCount The field in this record to add up.
     * @param bVerifyOnEOF Verify the total on End of File (true default).
     * @param bRecountOnSelect Recount the total each time a file select is called (False default).
     * @param bResetOnBreak Reset the counter on a control break?
     */
    public SubCountHandler(BaseField fieldMain, String fsToCount, boolean bRecountOnSelect, boolean bVerifyOnEOF, boolean bResetOnBreak)   // Init this field override for other value
    {
        this();
        this.init(null, null, null, fieldMain, fsToCount, bRecountOnSelect, bVerifyOnEOF, bResetOnBreak);
    }
    /**
     * Constructor for counting the value of a field in this record.
     * @param fieldMain The field to receive the count.
     * @param ifsToCount The field in this record to add up.
     * @param bVerifyOnEOF Verify the total on End of File (true default).
     * @param bRecountOnSelect Recount the total each time a file select is called (False default).
     * @param bResetOnBreak Reset the counter on a control break?
     */
    public void init(Record record, Record recordMain, String iMainFilesField, BaseField fieldMain, String fsToCount, boolean bRecountOnSelect, boolean bVerifyOnEOF, boolean bResetOnBreak)    // Init this field override for other value
    {
        super.init(record);
        this.fsToCount = fsToCount;
        if (fieldMain != null)
            m_fldMain = fieldMain;
        else if (recordMain != null)
            m_fldMain = recordMain.getField(iMainFilesField);
        if (m_fldMain != null)
            if ((m_fldMain.getRecord() == null) || (m_fldMain.getRecord().getEditMode() == DBConstants.EDIT_NONE) || (m_fldMain.getRecord().getEditMode() == DBConstants.EDIT_ADD))
                this.resetCount(); // Set in main file's field if the record is not current.
        m_dOldValue = 0;
        m_bRecountOnSelect = bRecountOnSelect;
        m_bVerifyOnEOF = bVerifyOnEOF;
        m_bResetOnBreak = bResetOnBreak;
        m_dTotalToVerify = 0;
        m_bEOFHit = true;	// In case this is a maint screen (grid screens start by requery/set this to false)
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null) if (m_fldMain != null) if (m_fldMain.getRecord() != owner)
            m_fldMain.addListener(new FieldRemoveBOnCloseHandler(this));
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone()
    {   // The listener is cloned along with any cloned files
        SubCountHandler handler = new SubCountHandler();
        handler.init(null, null, null, m_fldMain, fsToCount, m_bRecountOnSelect, m_bVerifyOnEOF, m_bResetOnBreak);    // Init this field override for other value
        return handler;
    }
    /**
     * Called when a valid record is read from the table/query.
     * Grab the old value of the field in case there is a change.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        super.doNewRecord(bDisplayOption);
        m_dOldValue = this.getFieldValue();      // Old value (in case of change)
    }
    /**
     * Called when a valid record is read from the table/query.
     * Grab the old value of the field in case there is a change.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        super.doValidRecord(bDisplayOption);
        m_dOldValue = this.getFieldValue();      // Old value (in case of change)
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * Check for add, write, or delete and adjust the total.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        double dMainValue, dCurrentValue;
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);   // Initialize the record
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        switch (iChangeType)
        {
            case DBConstants.CONTROL_BREAK_TYPE:
                if (!m_bResetOnBreak)
                    break;
                if (m_fldBreak != null)
                {
                    if (m_objLastBreakValue != FIRST_TIME)
                        if ((m_fldBreak.getData() == m_objLastBreakValue)
                            || ((m_fldBreak.getData() != null) && (m_fldBreak.getData().equals(m_objLastBreakValue))))
                                break;  // Break value is the same, don't break.
                    m_objLastBreakValue = m_fldBreak.getData();
                }
                iErrorCode = this.resetCount(); // Set in main file's field
                break;
            case DBConstants.AFTER_REQUERY_TYPE:
                m_bEOFHit = false;
                m_dTotalToVerify = 0;
                if ((m_bRecountOnSelect) || (m_bResetOnBreak))
                {
                    boolean bResetCount = true;
                    if (m_bVerifyOnEOF)    // Only have to reset field on requery if I don't verify on eof
                        if (m_fldMain != null)
                            if (m_fldMain.getRecord() != null) 
                                if ((m_fldMain.getRecord().getEditMode() == DBConstants.EDIT_CURRENT) || (m_fldMain.getRecord().getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                                bResetCount = false;    // Don't reset a field on a current record (since that would cause the record to be re-written for no reason AND may trigger unnecessary changes)
                    if (bResetCount)
                        iErrorCode = this.resetCount(); // Set in main file's field
                    if (m_fldBreak != null)
                        m_objLastBreakValue = FIRST_TIME;   // Reset to first time
                }
                break;
            case DBConstants.SELECT_EOF_TYPE:
                m_bEOFHit = true;
                if (m_bVerifyOnEOF)
                    iErrorCode = this.setCount(m_dTotalToVerify, false, DBConstants.INIT_MOVE);   // Set in main file's field
                break;
            case DBConstants.MOVE_NEXT_TYPE:
                dMainValue = this.getCount();    // Set in main file's field
                dCurrentValue = this.getFieldValue();            // New(current) value
                dMainValue += dCurrentValue;
                m_dTotalToVerify += dCurrentValue;
                if (m_fldBreak != null)
                    if (m_objLastBreakValue == FIRST_TIME)
                        m_objLastBreakValue = m_fldBreak.getData(); // First value = first value to compare
                if ((m_bVerifyOnEOF) && (!m_bResetOnBreak))   // Don't total until the end unless this is a break counter
                    break;  // Don't update the total until I hit EOF
                iErrorCode = this.setCount(dMainValue, true, DBConstants.INIT_MOVE); // Set in main file's field
                break;
            case DBConstants.AFTER_REFRESH_TYPE:    // New blank record created
                m_dOldValue = this.getFieldValue();      // Old value (in case of change)
                break;
            case DBConstants.AFTER_ADD_TYPE:
                dMainValue = this.getCount();       // Set in main file's field
                dCurrentValue = this.getFieldValue(); // New(current) value
                m_dOldValue = dCurrentValue;        // In case record is set to refresh on write (so an update is coming)
                dMainValue += dCurrentValue;
                m_dTotalToVerify += dCurrentValue;
                if ((m_bVerifyOnEOF == false) || (m_bEOFHit))
                    iErrorCode = this.setCount(dMainValue, false, DBConstants.SCREEN_MOVE); // Set in main file's field
                break;
            case DBConstants.AFTER_UPDATE_TYPE:
                dMainValue = this.getCount();    // Set in main file's field
                dCurrentValue = this.getFieldValue();            // New(current) value
                dMainValue += dCurrentValue - m_dOldValue;         // New(current) value
                m_dTotalToVerify += dCurrentValue - m_dOldValue;       // New(current) value
                if ((m_bVerifyOnEOF == false) || (m_bEOFHit))
                    iErrorCode = this.setCount(dMainValue, false, DBConstants.SCREEN_MOVE); // Set in main file's field
                break;
            case DBConstants.AFTER_DELETE_TYPE:
                dMainValue = this.getCount();    // Set in main file's field
                dMainValue -= m_dOldValue;            // New(current) value
                m_dTotalToVerify -= m_dOldValue;
                if ((m_bVerifyOnEOF == false) || (m_bEOFHit))
                    iErrorCode = this.setCount(dMainValue, false, DBConstants.SCREEN_MOVE); // Set in main file's field
                break;
        }
        return iErrorCode;
    }
    /**
     * Get the value to add (Overidden from SubCountHandler).
     * If there was a field specified, return the value, otherwise just return a count of 1.
     * @return The field value.
     */
    public double getFieldValue()
    { // Read a valid record
        if (fsToCount != null)
            return this.getOwner().getField(fsToCount).getValue();    // Default implementation only counts records!
        else
            return 1; // Count records.
    }
    /**
     * Set the field to check for control break.
     * @param fldBreak The field that contains the break value.
     */
    public void setBreakField(BaseField fldBreak)
    {
        m_fldBreak = fldBreak;
    }
    /**
     * Reset the field count.
     */
    public int resetCount()
    {
        return this.setCount(0, true, DBConstants.INIT_MOVE); // Set in main file's field if the record is not current.
    }
    /**
     * Reset the field count.
     * @param bDisableListeners Disable the field listeners (used for grid count verification)
     * @param iMoveMode Move mode.
     */
    public int setCount(double dFieldCount, boolean bDisableListeners, int iMoveMode)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (m_fldMain != null)
        {
            boolean[] rgbEnabled = null;
            if (bDisableListeners)
                rgbEnabled = m_fldMain.setEnableListeners(false);
            int iOriginalValue = (int)m_fldMain.getValue();
            boolean bOriginalModified = m_fldMain.isModified();
            int iOldOpenMode = m_fldMain.getRecord().setOpenMode(m_fldMain.getRecord().getOpenMode() & ~DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY); // don't trigger refresh and write
            iErrorCode = m_fldMain.setValue(dFieldCount, true, iMoveMode); // Set in main file's field if the record is not current
            m_fldMain.getRecord().setOpenMode(iOldOpenMode);
            if (iOriginalValue == (int)m_fldMain.getValue())
                if (bOriginalModified == false)
                    m_fldMain.setModified(bOriginalModified);   // Make sure this didn't change if change was just null to 0.
            if (rgbEnabled != null)
                m_fldMain.setEnableListeners(rgbEnabled);
        }
        return iErrorCode;
    }
    /**
     * Get the current field count.
     */
    public double getCount()
    {
        double dMainValue = 0;
        if (m_fldMain != null)
            dMainValue = m_fldMain.getValue();    // Set in main file's field
        return dMainValue;
    }
    /**
     * Return the current cached total
     */
    public double getTotalToVerify()
    {
        return m_dTotalToVerify;
    }
}
