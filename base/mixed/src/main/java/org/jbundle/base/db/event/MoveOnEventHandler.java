package org.jbundle.base.db.event;

/**
 * @(#)MoveOnValidHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.field.event.FieldRemoveBOnCloseHandler;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * On Valid or new record, move source field or string to destination.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MoveOnEventHandler extends FileListener
{
    /**
     * Destination field.
     */
    protected BaseField m_fldDest = null;
    /**
     * Source field.
     */
    protected BaseField m_fldSource = null;
    /**
     * If true move and disable the destination field, if false don't move.
     */
    protected Converter m_convCheckMark = null;
    /**
     * If there is a converter check mark, enable/disable the field?
     */
    protected boolean m_bDisableOnMove = true;
    /**
     * Source string.
     */
    protected String m_strSource = null;
    /**
     * If true, move on new record.
     */
    protected boolean m_bMoveOnNew = false;
    /**
     * If true, move on valid record.
     */
    protected boolean m_bMoveOnValid = true;
    /**
     * If true, move on valid record.
     */
    protected boolean m_bMoveOnSelect = false;
    /**
     * If true, move on valid record.
     */
    protected boolean m_bMoveOnAdd = false;
    /**
     * If true, move on valid record.
     */
    protected boolean m_bMoveOnUpdate = false;
    /**
     * If true, don't move a null source.
     */
    protected boolean m_bDontMoveNullSource = false;

    /**
     * Constructor.
     */
    public MoveOnEventHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param pfldDest tour.field.BaseField The destination field.
     * @param fldSource The source field.
     * @param pCheckMark If is field if false, don't move the data.
     * @param bMoveOnNew If true, move on new also.
     * @param bMoveOnValid If true, move on valid also.
     */
    public MoveOnEventHandler(BaseField fldDest, BaseField fldSource, Converter convCheckMark, boolean bMoveOnNew, boolean bMoveOnValid, boolean bMoveOnSelect, boolean bMoveOnAdd, boolean bMoveOnUpdate, String strSource, boolean bDontMoveNullSource)
    {
        this();
        this.init(null, fldDest, fldSource, convCheckMark, bMoveOnNew, bMoveOnValid, bMoveOnSelect, bMoveOnAdd, bMoveOnUpdate, strSource, bDontMoveNullSource);
    }
    /**
     * Constructor.
     * @param pfldDest tour.field.BaseField The destination field.
     * @param fldSource The source field.
     * @param pCheckMark If is field if false, don't move the data.
     * @param bMoveOnNew If true, move on new also.
     * @param bMoveOnValid If true, move on valid also.
     */
    public void init(Record record, BaseField fldDest, BaseField fldSource, Converter convCheckMark, boolean bMoveOnNew, boolean bMoveOnValid, boolean bMoveOnSelect, boolean bMoveOnAdd, boolean bMoveOnUpdate, String strSource, boolean bDontMoveNullSource)
    {
        m_fldDest = fldDest;
        m_fldSource = fldSource;
        m_convCheckMark = convCheckMark;
        m_strSource = strSource;
        m_bMoveOnNew = bMoveOnNew;
        m_bMoveOnValid = bMoveOnValid;
        m_bMoveOnSelect = bMoveOnSelect;
        m_bMoveOnAdd = bMoveOnAdd;
        m_bMoveOnUpdate = bMoveOnUpdate;
        m_bDontMoveNullSource = bDontMoveNullSource;
        super.init(record);
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (this.getOwner() == null)
            return;
        if (m_fldDest.getRecord() != this.getOwner())  // If field is not in this file, remember to remove it
            m_fldDest.addListener(new FieldRemoveBOnCloseHandler(this));
        if (m_fldSource != null) if (m_fldSource.getRecord() != this.getOwner()) if (m_fldSource.getRecord() != m_fldDest.getRecord())
            m_fldSource.addListener(new FieldRemoveBOnCloseHandler(this));
        if ((this.getOwner().getEditMode() == DBConstants.EDIT_CURRENT) || (this.getOwner().getEditMode() == DBConstants.EDIT_IN_PROGRESS))
            if (m_bMoveOnValid)
                this.moveTheData(DBConstants.DISPLAY, DBConstants.INIT_MOVE);  // Do trigger a record change.
        if (this.getOwner().getEditMode() == DBConstants.EDIT_ADD)
            if (m_bMoveOnNew)
                this.moveTheData(DBConstants.DISPLAY, DBConstants.INIT_MOVE);  // Do trigger a record change.
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        super.doNewRecord(bDisplayOption);
        if (m_bMoveOnNew)
        {
        	boolean bOldModified = false;
            if (m_fldDest != null)
            	bOldModified = m_fldDest.isModified();
            int iMoveType = DBConstants.INIT_MOVE;  // Typically, Don't trigger a record change.
            if (m_bMoveOnValid)
                if (m_fldDest != null)
                    if (m_fldDest instanceof ReferenceField)
                        if (((ReferenceField)m_fldDest).getReferenceRecord() != null)
                        	if (((ReferenceField)m_fldDest).getReferenceRecord().getCounterField() == m_fldSource)
                        		if ((m_fldDest.getRecord().getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (m_fldDest.getRecord().getEditMode() == DBConstants.EDIT_CURRENT))
                        			iMoveType = DBConstants.SCREEN_MOVE;   // Special case - clearing a secondary field = YES - modified
            this.moveTheData(bDisplayOption, iMoveType);
            if (iMoveType == DBConstants.INIT_MOVE)
                if (m_fldDest != null)
                    if (bOldModified == false)
                        m_fldDest.setModified(false);
        }
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {   // Copy the key field to the master file and BYPASS the BEHAVIORS
        super.doValidRecord(bDisplayOption);
        if (m_bMoveOnValid)
        {
            int iMoveType = DBConstants.SCREEN_MOVE;  // Do trigger a record change.
            this.moveTheData(bDisplayOption, iMoveType);
        }
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * If this file is selected (opened) move the field.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);      // Initialize the record
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        if (((iChangeType == DBConstants.SELECT_TYPE) && (m_bMoveOnSelect))
            || ((iChangeType == DBConstants.AFTER_ADD_TYPE) && (m_bMoveOnAdd))
            || ((iChangeType == DBConstants.AFTER_UPDATE_TYPE) && (m_bMoveOnUpdate)))
                this.moveTheData(bDisplayOption, DBConstants.SCREEN_MOVE);  // Do trigger a record change.
        return iErrorCode;
    }
    /**
     * Actually move the data.
     * @param bDisplayOption If true, display any changes.
     */
    public void moveTheData(boolean bDisplayOption, int iMoveType)
    {
        if (m_convCheckMark != null) if (m_bDisableOnMove)
            m_fldDest.setEnabled(!m_convCheckMark.getState());
        if ((m_convCheckMark == null)
            || (m_convCheckMark.getState()))
        {
            if ((this.getSourceField() != null)
                && ((!this.getSourceField().isNull()) || (!m_bDontMoveNullSource)))
                m_fldDest.moveFieldToThis(this.getSourceField(), bDisplayOption, iMoveType);  // Move dependent field to here
            else if (m_strSource != null)
                m_fldDest.setString(m_strSource, bDisplayOption, iMoveType);   // Move dependent field to here
            else if (m_fldDest instanceof ReferenceField)
                ((ReferenceField)m_fldDest).setReference(this.getOwner(), bDisplayOption, iMoveType);
        }
        else
        {
            if (bDisplayOption)
                m_fldDest.displayField();      // Redisplay based on this check mark
        }
    }
    /**
     * Get the destination field.
     * @return The destination field.
     */
    public BaseField getDestField()
    {
        return m_fldDest;
    }
    /**
     * Get the destination field.
     * @return The destination field.
     */
    public BaseField getSourceField()
    {
        return m_fldSource;
    }
    /**
     * Disable on move.
     * @param bDisableOnMove Disable if converter on move?
     */
    public void setDisableOnMove(boolean bDisableOnMove)
    {
        m_bDisableOnMove = bDisableOnMove;
    }
}
