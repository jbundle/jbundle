/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)DateUpdatedHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.model.DBConstants;

/**
 * If this listener's owner field changes, move the current date to the target field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DateUpdatedHandler extends FieldListener
{
    /**
     * The field to move the current date to.
     */
    String m_iMainFilesFieldSeq = null;
    /**
     * If true, moves the current time (defaults to false).
     */
    protected boolean m_bMoveCurrentTime = false;

    /**
     * Constructor.
     */
    public DateUpdatedHandler()
    {
        super();
    }
    /**
     * Constructor to move the current date to this field on change.
     * @param iMainFilesField The field to move the current date to on change.
     */
    public DateUpdatedHandler(String iMainFilesField)
    {
        this();
        this.init(null, iMainFilesField, false);
    }
    /**
     * Constructor (optionally move the current time).
     * @param iMainFilesField The field to move the current date to on change.
     * @param bMoveCurrentTime If true, moves the current time (rather than the current date).
     */
    public DateUpdatedHandler(String iMainFilesField, boolean bMoveCurrentTime)
    {
        this();
        this.init(null, iMainFilesField, bMoveCurrentTime);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field, String iMainFilesField, boolean bMoveCurrentTime)
    {
        super.init(field);
        m_iMainFilesFieldSeq = iMainFilesField;
        m_bMoveCurrentTime = bMoveCurrentTime;
        
        m_bReadMove = false;  // Don't move on read!
    }
    /**
     * Set this cloned listener to the same state at this listener.
     * @param field The field this new listener will be added to.
     * @param The new listener to sync to this.
     * @param Has the init method been called?
     * @return True if I called init.
     */
    public boolean syncClonedListener(BaseField field, FieldListener listener, boolean bInitCalled)
    {
        if (!bInitCalled)
            ((DateUpdatedHandler)listener).init(null, m_iMainFilesFieldSeq, m_bMoveCurrentTime);
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * The Field has Changed.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    { // Read a valid record
        if (this.getOwner().getRecord().getField(m_iMainFilesFieldSeq) instanceof DateTimeField)
        {
            DateTimeField thisField = ((DateTimeField)this.getOwner().getRecord().getField(m_iMainFilesFieldSeq));
            double dDate = 0;
            if (m_bMoveCurrentTime)
                dDate = DateField.currentTime();
            else
                dDate = DateField.todaysDate();
            int iErrorCode = thisField.setValue(dDate, bDisplayOption, iMoveMode);        // File written or updated, set the update date
            if (iMoveMode == DBConstants.INIT_MOVE)
                thisField.setModified(false);   // Don't make this record modified just because I set this field.
            return iErrorCode;
        }
        return DBConstants.NORMAL_RETURN;
    }
}
