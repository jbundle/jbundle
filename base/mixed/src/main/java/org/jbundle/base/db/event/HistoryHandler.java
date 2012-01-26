/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)HistoryHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Calendar;
import java.util.Date;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.VirtualRecord;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Maintain a history file (create a record each time this record changes).
 * Typically, when you set up a history file, you use the target record as
 * the base and make the following changes:
 * <br/>- Change the counter field to an Integer field, as a counter is not normally used in a history file.
 * <br/>- Add a DateTimeField to the end of the record.
 * <br/>- Create a key usually of the counter and datetime field for access.
 * <p/>Typically you add the historyhandler in the record's addlisteners() method.
 * <p/>Note: The history handler runs only in the server space.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class HistoryHandler extends FreeOnFreeHandler
{
    /**
     * Location of the mod date in the history record.
     */
    protected int m_iHistoryDateSeq = -1;
    /**
     * Name of the history class.
     */
    protected String m_strRecHistoryClass = null;
    /**
    * The source history date field in this record.
    */
   protected BaseField m_fldSourceHistoryDate = null;
    /**
     * The source history date field sequence in this record.
     */
    protected int m_iSourceDateSeq = -1;

    /**
     * Constructor.
     */
    public HistoryHandler()
    {
        super();
    }
    /**
     * Constructor assumes the history field is the last one and the date is the current time and close on free.
     * @param recHistory The history record.
     */
    public HistoryHandler(Record recHistory)
    {
        this();
        this.init(null, recHistory, -1, null, true, null, -1);   // Warning, you must manually free this record
    }
    /**
     * Constructor.
     * @param recHistory The history record.
     * @param iHistoryDate The last changed date in the history record.
     * @param fldSourceHistoryDate Where to get the date changed (if null, use current time).
     * @param bConfirmOnChange If true, ask the user if the changes are okay before writing them.
     * @param bCloseOnFree Close the history file when this record is freed (default true).
     */
    public HistoryHandler(Record recHistory, int iHistoryDate, BaseField fldSourceHistoryDate, boolean bCloseOnFree)
    {
        this();
        this.init(null, recHistory, iHistoryDate, fldSourceHistoryDate, bCloseOnFree, null, -1);
    }
    /**
     * Constructor.
     * @param recHistory The history record.
     * @param iHistoryDate The last changed date in the history record.
     * @param fldSourceHistoryDate Where to get the date changed (if null, use current time).
     * @param bConfirmOnChange If true, ask the user if the changes are okay before writing them.
     * @param bCloseOnFree Close the history file when this record is freed (default true).
     */
    public HistoryHandler(String strRecHistoryClass, int iHistoryDate, int iSourceHistoryDateSeq)
    {
        this();
        this.init(null, null, iHistoryDate, null, false, strRecHistoryClass, iSourceHistoryDateSeq);    // Free on Free
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recHistory The history record.
     * @param iHistoryDate The last changed date in the history record.
     * @param fldSourceHistoryDate Where to get the date changed (if null, use current time).
     * @param bConfirmOnChange If true, ask the user if the changes are okay before writing them.
     * @param bCloseOnFree Close the history file when this record is freed (default true).
     */
    public void init(Record record, Record recHistory, int iHistoryDateSeq, BaseField fldSourceHistoryDate, boolean bCloseOnFree, String strRecHistoryClass, int iSourceHistoryDateSeq)
    {
        if (iHistoryDateSeq == -1)
            iHistoryDateSeq = recHistory.getFieldCount() - 1;    // Last field
        m_iHistoryDateSeq = iHistoryDateSeq;
        m_fldSourceHistoryDate = fldSourceHistoryDate;
        m_strRecHistoryClass = strRecHistoryClass;
        m_iSourceDateSeq = iSourceHistoryDateSeq;

        super.init(record, null, recHistory, bCloseOnFree);
        if (m_recDependent != null)
        {
            if (m_recDependent.getListener(RecordChangedHandler.class) != null)
                m_recDependent.removeListener(m_recDependent.getListener(RecordChangedHandler.class), true);  // I replace this listener
        }                    

        this.setMasterSlaveFlag(FileListener.RUN_IN_SLAVE);   // This runs on the slave (if there is a slave)
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
    }
    /**
     * Get the destination (history) record.
     * @return
     */
    public Record getHistoryRecord()
    {
        if (m_recDependent == null)
        {
            if (m_strRecHistoryClass != null)
            {
                m_recDependent = Record.makeRecordFromClassName(m_strRecHistoryClass, Record.findRecordOwner(this.getOwner()));
                if (m_recDependent != null)
                {
                    m_bCloseOnFree = true;
                    m_recDependent.addListener(new FileRemoveBOnCloseHandler(this));    // Being careful
                    if (m_recDependent.getListener(RecordChangedHandler.class) != null)
                        m_recDependent.removeListener(this.getOwner().getListener(RecordChangedHandler.class), true);  // I replace this listener
                }                    
            }
        }
        return m_recDependent;
    }
    /**
     * Move the date field (or the current time) to the target date field.
     */
    public void moveDateToTarget()
    {
        DateTimeField fldHistoryDateTarget = (DateTimeField)this.getHistoryRecord().getField(m_iHistoryDateSeq);
        if ((this.getHistorySourceDate() != null) && (!this.getHistorySourceDate().isNull()))
            fldHistoryDateTarget.moveFieldToThis(this.getHistorySourceDate());
        else
            fldHistoryDateTarget.setDateTime(new Date(), DBConstants.DISPLAY, DBConstants.SCREEN_MOVE); // Need seconds
    }
    /**
     * Get the date source from this record
     */
    public BaseField getHistorySourceDate()
    {
        if (m_fldSourceHistoryDate == null)
        {
            if (m_iSourceDateSeq != -1)
                m_fldSourceHistoryDate = this.getOwner().getField(m_iSourceDateSeq);
            else if (this.getHistoryRecord() instanceof VirtualRecord)
                m_fldSourceHistoryDate = this.getOwner().getField(VirtualRecord.kLastChanged);
        }
        return m_fldSourceHistoryDate;
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        int iErrorCode = DBConstants.NORMAL_RETURN;
        switch (iChangeType)
        {
            case DBConstants.LOCK_TYPE:
                try   {
                    this.getHistoryRecord().addNew();
                } catch (DBException ex)    {
                    ex.printStackTrace(); // Never
                }
                int iLastField = this.getOwner().getFieldCount() + DBConstants.MAIN_FIELD - 1;
                for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq <= iLastField; iFieldSeq++)
                {   // Move all the current fields to the history table
                    BaseField fldDest = this.getHistoryRecord().getField(iFieldSeq);
                    BaseField fldSource = this.getOwner().getField(iFieldSeq);
                    if ((fldSource == null)
                        || (!fldDest.getFieldName().equals(fldSource.getFieldName())))
                            fldSource = this.getHistoryRecord().getField(fldDest.getFieldName());    // Being very careful
                    if (fldSource != null)
                        fldDest.moveFieldToThis(fldSource);
                }
                break;
            case DBConstants.AFTER_UPDATE_TYPE:
            case DBConstants.AFTER_DELETE_TYPE:
//x                if (this.getOwner().isModified())
                {
                    this.moveDateToTarget();
                    iErrorCode = DBConstants.ERROR_RETURN;
                    while (iErrorCode != DBConstants.NORMAL_RETURN)
                    {
                        try {
                            if (this.getHistoryRecord().getEditMode() == DBConstants.EDIT_ADD)  // It is possible that a nested set called this already
                                if (this.getOwner().isModified(true))
                                    this.getHistoryRecord().add();
                            iErrorCode = DBConstants.NORMAL_RETURN; // Good
                        } catch(DBException ex) {
                            iErrorCode = ex.getErrorCode();
                            if ((iErrorCode != DBConstants.DUPLICATE_KEY) && (iErrorCode != DBConstants.DUPLICATE_COUNTER))
                            {
                                ex.printStackTrace();
                                break;
                            }
                            else
                            {
                                DateTimeField fieldTarget = (DateTimeField)this.getHistoryRecord().getField(m_iHistoryDateSeq);
                                Date dateBefore = this.bumpTime(fieldTarget);
                                if ((fieldTarget.getDateTime() == null)
                                    || (fieldTarget.getDateTime().equals(dateBefore)))
                                        break;  // Should never happen - being careful.
                                // Try again
                            }
                        }
                    }
                }
                break;
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);        // Initialize the record
    }
    /**
     * Bump time field by a second.
     * @param fieldTarget
     * @return
     */
    public Date bumpTime(DateTimeField fieldTarget)
    {
        Date dateBefore = fieldTarget.getDateTime();
        Calendar calTarget = fieldTarget.getCalendar();
        calTarget.add(Calendar.SECOND, 1);
        fieldTarget.setCalendar(calTarget, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
        return dateBefore;
    }
}
