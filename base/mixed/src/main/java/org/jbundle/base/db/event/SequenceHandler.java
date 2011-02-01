package org.jbundle.base.db.event;

/**
 * @(#)SequenceHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Automatic sequence number.
 * This is typically used where you are entering detail record which should be
 * ordered by a sequence number. The source field then would be a field in
 * the main (parent) record which contains the next sequence number. Besides
 * Setting this field to the next sequence number, this class sets the master
 * (source) field to the largest (or next) sequence number.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SequenceHandler extends MoveOnValidHandler
{

    /**
     * Constructor.
     */
    public SequenceHandler()
    {
        super();
    }
    /**
     * This Constructor moves the source field to the dest field on valid.
     * @param pfldDest tour.field.BaseField The destination field.
     * @param fldSource The source field.
     */
    public SequenceHandler(BaseField fldDest, BaseField fldSource)
    {
        this();
        this.init(null, fldDest, fldSource);
    }
    /**
     * This Constructor moves the source field to the dest field on valid.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param pfldDest tour.field.BaseField The destination field.
     * @param fldSource The source field.
     */
    public void init(Record record, BaseField fldDest, BaseField fldSource)
    {
        super.init(record, fldDest, fldSource, null, true, false, false, false, false, null, false);
    }
    /**
     * Clone Method.
     */
    public Object clone()
    {
        return new SequenceHandler(m_fldDest, m_fldSource);
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
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);      // Initialize the record (Move the header record data down)
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        if ((iChangeType == DBConstants.AFTER_ADD_TYPE) || (iChangeType == DBConstants.AFTER_UPDATE_TYPE))
        {
            double dControlValue = m_fldSource.getValue();
            double dFieldValue = m_fldDest.getValue();
            if (dControlValue == dFieldValue)
                iErrorCode = m_fldSource.setValue(dFieldValue + this.getBumpValue(), bDisplayOption, DBConstants.SCREEN_MOVE);
            else if (dControlValue < dFieldValue)
                iErrorCode = m_fldSource.moveFieldToThis(m_fldDest, bDisplayOption, DBConstants.SCREEN_MOVE);
        }
        return iErrorCode;
    }
    /**
     * Get the sequence increment value.
     * @return The sequence increment (default is 1).
     */
    public double getBumpValue()
    { // Read a valid record
        return 1; // Default implementation only counts records!
    }
}
