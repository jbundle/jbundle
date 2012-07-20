/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)ReadSecondaryHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileRemoveBOnCloseHandler;
import org.jbundle.base.db.event.MoveOnValidHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * Read a secondary file when this key changes.
 * This class has a utility method to add MoveOnValid methods to the
 * target record. Then fields can be automatically copied to the main
 * record, copied back and more.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ReadSecondaryHandler extends FieldListener
{
    /**
     * The secondary record that this field triggers a read to.
     */
    protected Record m_record = null;
    /**
     * The key area in the secondary record to read from.
     */
    protected String keyAreaName = null;
    /**
     * The main key field in the secondary record.
     */
    protected BaseField m_KeyField = null;
    /**
     * Close the record when this behavior is removed?
     */
    protected boolean m_bCloseOnFree = true;
    /**
     * Update the secondary record before reading (if it has changed)?
     */
    protected boolean m_bUpdateRecord = true;
    /**
     * If true, a null field value will trigger a new record; if false a key not found error.
     */
    protected boolean m_bAllowNull = true;
    /**
     * Have move on valid behaviors been added?
     */
    protected boolean m_bMoveBehavior = false;

    /**
     * Constructor.
     */
    public ReadSecondaryHandler()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ReadSecondaryHandler(Record record)
    {
        this();
        this.init(null, record, null, true, false, true);
    }
    /**
     * Constructor.
     * @param record The secondary record to read.
     * @param iQueryKeyArea The key area to read from.
     */
    public ReadSecondaryHandler(Record record, String keyAreaName)
    {
        this();
        this.init(null, record, keyAreaName, true, false, true);
    }
    /**
     * Constructor.
     * @param record The secondary record to read.
     * @param iQueryKeyArea The key area to read from.
     * @param bCloseOnFree Close the record when this behavior is removed?
     * @param bUpdateRecord Update the secondary record before reading (if it has changed)?
     * @param bAllowNull If true, a null field value will trigger a new record; if false a key not found error.
     */
    public ReadSecondaryHandler(Record record, String keyAreaName, boolean bCloseOnFree, boolean bUpdateRecord, boolean bAllowNull)
    {
        this();
        this.init(null, record, keyAreaName, bCloseOnFree, bUpdateRecord, bAllowNull);
    }
    /**
     * Initialize this listener.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param record The secondary record that this field triggers a read to.
     * @param iQueryKeyArea The key area in the secondary record to read from.
     * @param bCloseOnFree Close the record when this behavior is removed?
     * @param bUpdateRecord Update the secondary record before reading (if it has changed)?
     * @param bAllowNull If true, a null field value will trigger a new record; if false a key not found error.
     */
    public void init(BaseField field, Record record, String keyAreaName, boolean bCloseOnFree, boolean bUpdateRecord, boolean bAllowNull)
    {
        super.init(field);
        m_record = record;
        this.keyAreaName = keyAreaName;
        m_KeyField = null;
        m_bCloseOnFree = bCloseOnFree;
        m_bUpdateRecord = bUpdateRecord;
        m_bAllowNull = bAllowNull;

        m_bMoveBehavior = false;
        m_record.addListener(new FileRemoveBOnCloseHandler(this));  // Remove this if you close the file first
        if (m_bUpdateRecord)
        {
            if ((m_record.getOpenMode() & DBConstants.LOCK_TYPE_MASK) == 0)  // If there is no lock strategy or type, set one.
                if (m_record.getTask() != null)
                    m_record.setOpenMode(m_record.getOpenMode() | m_record.getTask().getDefaultLockType(m_record.getDatabaseType()));
        }
        else
            m_record.setOpenMode(DBConstants.OPEN_READ_ONLY);       // Dont Lock the record if any changes (Also caches records).
    }
    /**
     * Creates a new object of the same class as this object.
     * @param field The field to add the new cloned behavior to.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone(BaseField field) throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException(); // For now
    }
    /**
     * Set the field that owns this listener.
     * This method adds the methods that allow a record to read itself if the main key field is changed.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
        {
            MoveOnValidHandler moveBehavior = null;
            m_record.setOpenMode(m_record.getOpenMode() | DBConstants.OPEN_CACHE_RECORDS);    // Cache recently used records.
            
            m_bMoveBehavior = false;    // This flag is set automatically if the Valid record listener has to be used
            
            ReadSecondaryHandler listenerDup = (ReadSecondaryHandler)this.getOwner().getListener(this.getClass());
            while ((listenerDup != this) && (listenerDup != null))
            {
                if (listenerDup.getRecord() == this.getRecord())
                    if (listenerDup.getActualKeyArea() == this.getActualKeyArea())
                {
                    listenerDup.setRecord(null);
                    this.getOwner().removeListener(listenerDup, true); // Make sure there is only one
                    break;
                }
                listenerDup = (ReadSecondaryHandler)listenerDup.getListener(this.getClass());
            }
            
            this.fieldChanged(DBConstants.DISPLAY, DBConstants.READ_MOVE);    // SCREEN_MOVE says this is coming from here

            if (owner instanceof ReferenceField)
            {
                if (keyAreaName != null)
                    m_KeyField = m_record.getKeyArea(keyAreaName).getField(DBConstants.MAIN_KEY_FIELD);   // Handle field
                moveBehavior = new MoveOnValidHandler(((BaseField)owner), m_KeyField, null, true, true);        // Its okay to sync the key with references
                m_KeyField = null;
            } 
            else
            {
                MainReadOnlyHandler listener = new MainReadOnlyHandler(keyAreaName);
                m_KeyField = m_record.getKeyArea(keyAreaName).getField(DBConstants.MAIN_KEY_FIELD);
                m_KeyField.addListener(listener); //    Make sure this field has the BaseListener to read it's file
                    // Make sure you move the key field to this field!!
                moveBehavior = new MoveOnValidHandler(((BaseField)owner), m_KeyField, null, false, true);
            } 
            m_record.addListener(moveBehavior);
        }
        else
        {
            if (m_bCloseOnFree) if (this.getDependentListener() != null) // If close and file is still open
            {
                this.setDependentListener(null);    // If case you want to delete me!
                if (m_record != null)
                    m_record.free();  // File is still open, and my listener is still there, close it!
            }
            m_record = null;
            m_KeyField = null;
        }
    }
    /**
     * Add a field source and dest.
     * @param fldDest The destination field.
     * @param fldSource The source field.
     * @param bMoveToDependent If true adds a MoveOnValidHandler to the secondary record.
     * @param bMoveBackOnChange If true, adds a CopyFieldHandler to the destination field (moves to the source).
     * @param convCheckMark Check mark to check before moving.
     * @param convBackconvCheckMark Check mark to check before moving back.
     */
    public MoveOnValidHandler addFieldPair(BaseField fldDest, BaseField fldSource, boolean bMoveToDependent, boolean bMoveBackOnChange, Converter convCheckMark, Converter convBackconvCheckMark)
    {   // BaseField will return iSourceFieldSeq if m_OwnerField is 'Y'es
        MoveOnValidHandler moveBehavior = null;
        if (convCheckMark != null) if (convCheckMark.getField() != null)
        {
            CheckMoveHandler listener = new CheckMoveHandler(fldDest, fldSource);
            ((BaseField)convCheckMark.getField()).addListener(listener);        // Whenever changed, this.FieldChanged is called
        }
        if (bMoveToDependent)
        {
            m_bMoveBehavior = true;
            moveBehavior = new MoveOnValidHandler(fldDest, fldSource, convCheckMark, true, true);
            m_record.addListener(moveBehavior);
        }
        if (bMoveBackOnChange)
        {
            CopyFieldHandler listener = new CopyFieldHandler(fldSource, convBackconvCheckMark);
            fldDest.addListener(listener);      // Add this listener to the field
        }
        return moveBehavior;
    }
    /**
     * This method is specifically for making sure a handle is moved to this field on valid.
     *  The field must be a ReferenceField.
     * @param iFieldSeq int On valid, move to this field.
     */
    public MoveOnValidHandler addFieldSeqPair(int iFieldSeq)
    {
        m_bMoveBehavior = true;
        MoveOnValidHandler moveBehavior = new MoveOnValidHandler(this.getOwner().getRecord().getField(iFieldSeq));
        m_record.addListener(moveBehavior);
        return moveBehavior;
    }
    /**
     * Add a destination/source field pair (on valid record, move dest to source).
     * @param iDestFieldSeq The destination field.
     * @param iSourceFieldSeq The source field.
     * @param bMoveToDependent If true adds a MoveOnValidHandler to the secondary record.
     * @param bMoveBackOnChange If true, adds a CopyFieldHandler to the destination field (moves to the source).
     */
    public MoveOnValidHandler addFieldSeqPair(int iDestFieldSeq, int iSourceFieldSeq, boolean bMoveToDependent, boolean bMoveBackOnChange)
    {   // BaseField will return iSourceFieldSeq if m_OwnerField is 'Y'es
        return this.addFieldPair(this.getOwner().getRecord().getField(iDestFieldSeq), m_record.getField(iSourceFieldSeq), bMoveToDependent, bMoveBackOnChange, null, null);
    }
    /**
     * Add the set of fields that will move on a valid record.
     * @param iDestFieldSeq The destination field.
     * @param iSourceFieldSeq The source field.
     * @param bMoveToDependent If true adds a MoveOnValidHandler to the secondary record.
     * @param bMoveBackOnChange If true, adds a CopyFieldHandler to the destination field (moves to the source).
     * @param convCheckMark Check mark to check before moving.
     * @param convBackconvCheckMark Check mark to check before moving back.
     */
    public MoveOnValidHandler addFieldSeqPair(int iDestFieldSeq, int iSourceFieldSeq, boolean bMoveToDependent, boolean bMoveBackOnChange, Converter convCheckMark, Converter convBackconvCheckMark)
    {   // BaseField will return iSourceFieldSeq if m_OwnerField is 'Y'es
        return this.addFieldPair(this.getOwner().getRecord().getField(iDestFieldSeq), m_record.getField(iSourceFieldSeq),
                bMoveToDependent, bMoveBackOnChange, convCheckMark, convBackconvCheckMark);
    }
    /**
     * This method is specifically for making sure a handle is moved to this field on valid.
     *  The field must be a ReferenceField.
     * @param iFieldSeq int On valid, move to this field.
     */
    public MoveOnValidHandler addFieldSeqPair(String iFieldSeq)
    {
        m_bMoveBehavior = true;
        MoveOnValidHandler moveBehavior = new MoveOnValidHandler(this.getOwner().getRecord().getField(iFieldSeq));
        m_record.addListener(moveBehavior);
        return moveBehavior;
    }
    /**
     * Add a destination/source field pair (on valid record, move dest to source).
     * @param iDestFieldSeq The destination field.
     * @param iSourceFieldSeq The source field.
     * @param bMoveToDependent If true adds a MoveOnValidHandler to the secondary record.
     * @param bMoveBackOnChange If true, adds a CopyFieldHandler to the destination field (moves to the source).
     */
    public MoveOnValidHandler addFieldSeqPair(String iDestFieldSeq, String iSourceFieldSeq, boolean bMoveToDependent, boolean bMoveBackOnChange)
    {   // BaseField will return iSourceFieldSeq if m_OwnerField is 'Y'es
        return this.addFieldPair(this.getOwner().getRecord().getField(iDestFieldSeq), m_record.getField(iSourceFieldSeq), bMoveToDependent, bMoveBackOnChange, null, null);
    }
    /**
     * Add the set of fields that will move on a valid record.
     * @param iDestFieldSeq The destination field.
     * @param iSourceFieldSeq The source field.
     * @param bMoveToDependent If true adds a MoveOnValidHandler to the secondary record.
     * @param bMoveBackOnChange If true, adds a CopyFieldHandler to the destination field (moves to the source).
     * @param convCheckMark Check mark to check before moving.
     * @param convBackconvCheckMark Check mark to check before moving back.
     */
    public MoveOnValidHandler addFieldSeqPair(String iDestFieldSeq, String iSourceFieldSeq, boolean bMoveToDependent, boolean bMoveBackOnChange, Converter convCheckMark, Converter convBackconvCheckMark)
    {   // BaseField will return iSourceFieldSeq if m_OwnerField is 'Y'es
        return this.addFieldPair(this.getOwner().getRecord().getField(iDestFieldSeq), m_record.getField(iSourceFieldSeq),
                bMoveToDependent, bMoveBackOnChange, convCheckMark, convBackconvCheckMark);
    }
    /**
     * The Field has Changed.
     * Read the secondary file.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * Key field changed, read the new secondary record.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (m_bUpdateRecord)
            if (m_record.isModified())
        {
            try
            {
                if (m_record.getEditMode() == Constants.EDIT_IN_PROGRESS)
                    m_record.set();
                else if (m_record.getEditMode() == Constants.EDIT_ADD)
                    m_record.add();
            }
            catch( DBException ex )
            {
                return ex.getErrorCode();
            }
        }
        if (m_bMoveBehavior) if (m_KeyField != null)
            if ((iMoveMode == DBConstants.INIT_MOVE) || (iMoveMode == DBConstants.READ_MOVE))
                m_KeyField.setModified(true);   // Force re-read and GetValid/GetNew listener to run
        if (m_KeyField == null)
        {
            int iHandleType = DBConstants.BOOKMARK_HANDLE;
            iErrorCode = DBConstants.NORMAL_RETURN;
            try
            {
                Object handle = this.getOwner().getData();
                if ((handle == null)
                    || ((this.getOwner() instanceof ReferenceField) && (((Integer)handle).intValue() == 0)))
                {
                    if ((m_bAllowNull) || (iMoveMode != DBConstants.SCREEN_MOVE))
                        m_record.handleNewRecord(DBConstants.DISPLAY);      //? Display Fields (Should leave record in an indeterminate state!)
                    else
                        iErrorCode = DBConstants.KEY_NOT_FOUND;   // Can't have a null value
                }
                else
                {
                    if (m_record.setHandle(handle, iHandleType) == null) // SCREEN_MOVE says this is coming from here
                        iErrorCode = DBConstants.KEY_NOT_FOUND;
                    else // Do all the field behaviors for the secondary record //x if (iMoveMode != DBConstants.READ_MOVE)
                    {
                        for (int i = 0; i < m_record.getFieldCount(); i++)
                        {
                            m_record.getField(i).handleFieldChanged(bDisplayOption, DBConstants.READ_MOVE);        // Make sure all fields of the secondary record get this change notification
                        }
                    }
                }
            } catch (DBException ex)    {
                iErrorCode = ex.getErrorCode();
            }
        }
        else
            iErrorCode = m_KeyField.moveFieldToThis(this.getOwner(), DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);   // SCREEN_MOVE says this is coming from here
        return iErrorCode;
    }
    /**
     * Get the linked record.
     */
    public Record getRecord()
    {
        return m_record;
    }
    /**
     * Set the linked record.
     */
    public void setRecord(Record record)
    {
        m_record = record;
    }
    /**
     * Get the key area
     * @return
     */
    public String getActualKeyArea()
    {
        if (keyAreaName != null)
            return keyAreaName;
        return Record.ID_KEY;    // Main key area
    }
}
