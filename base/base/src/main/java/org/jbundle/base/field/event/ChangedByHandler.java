/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)ChangedByHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.IntegerField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.util.BaseApplication;

/**
 * If this field is changed, update the "Changed by" Field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ChangedByHandler extends FieldListener
{
    /**
     * The field sequence of the "changed by" field.
     */
    String mainFilesField = null;

    /**
     * Constructor.
     */
    public ChangedByHandler()
    {
        super();
    }
    /**
     * Constructor.
     * param mainFilesField The field sequence of the "changed by" field in this field's record.
     */
    public ChangedByHandler(String mainFilesField)
    {
        this();
        this.init(null, mainFilesField);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * param mainFilesField The field sequence of the "changed by" field in this field's record.
     */
    public void init(BaseField field, String mainFilesField)
    {
        super.init(field);
        this.mainFilesField = mainFilesField;
        m_bReadMove = false;  // Don't move on read!
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (this.getOwner() != null)
            this.syncBehaviorToRecord(this.getOwner().getRecord().getField(mainFilesField));  // Init now
    }
    /**
     * Set this cloned listener to the same state at this listener.
     * @param field The field this new listener will be added to.
     * @param listener The new listener to sync to this.
     * @param bInitCalled Has the init method been called?
     * @return True if I called init.
     */
    public boolean syncClonedListener(BaseField field, FieldListener listener, boolean bInitCalled)
    {
        bInitCalled = super.syncClonedListener(field, listener, bInitCalled);
        ((ChangedByHandler)listener).setMainFilesFieldSeq(mainFilesField);
        return bInitCalled;
    }
    /**
     * Set the main file's field seq.
     * @param iMainFilesFieldSeq
     */
    public void setMainFilesFieldSeq(String iMainFilesFieldSeq )
    {
        mainFilesField = iMainFilesFieldSeq;
    }
    /**
     * The Field has Changed.
     * Change the changed by user field.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    { // Read a valid record
        if (this.getOwner() != null)
        {
            BaseField thisField = this.getOwner().getRecord().getField(mainFilesField);
            if (this.getOwner().getRecord().getRecordOwner() != null)
                if (this.getOwner().getRecord().getRecordOwner().getTask() != null)
                    if (this.getOwner().getRecord().getRecordOwner().getTask().getApplication() != null)
            {
                String userId = "-1";
                if (this.getOwner().getRecord().getRecordOwner() != null)
                    if (((BaseApplication)this.getOwner().getRecord().getRecordOwner().getTask().getApplication()) != null)
                    if (((BaseApplication)this.getOwner().getRecord().getRecordOwner().getTask().getApplication()).getUserID() != null)
                    if (((BaseApplication)this.getOwner().getRecord().getRecordOwner().getTask().getApplication()).getUserID().length() > 0)
                        userId = ((BaseApplication)this.getOwner().getRecord().getRecordOwner().getTask().getApplication()).getUserID();     // File written or updated, set the user name
                int iErrorCode = thisField.setString(userId, bDisplayOption, DBConstants.SCREEN_MOVE);
                if (iMoveMode == DBConstants.INIT_MOVE)
                    thisField.setModified(false);   // Don't make this record modified just because I set this field.
                return iErrorCode;
            }
        }
        return DBConstants.ERROR_RETURN;    // Can't set a field that is not long!
    }
}
