/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)MoveOnChangeHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;


/**
 * When this field is changed, write or update and (optionally) refresh the record that
 * the field belongs to.
 * Typically used when a field change must be written to disk.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class WriteOnChangeHandler extends FieldListener
{
    /**
     * Refresh the record after write.
     */
    protected boolean m_bRefreshAfterWrite = true;

    /**
     * Constructor.
     */
    public WriteOnChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param bRefreshAfterWrite Refresh the record after write.
     */
    public WriteOnChangeHandler(boolean bRefreshAfterWrite)
    {
        this();
        this.init(null, bRefreshAfterWrite);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param bRefreshAfterWrite Refresh the record after write.
     */
    public void init(BaseField field, boolean bRefreshAfterWrite)
    {
        super.init(field);
        this.setRespondsToMode(DBConstants.INIT_MOVE, false);
        this.setRespondsToMode(DBConstants.READ_MOVE, false); // By default, only respond to screen moves
        m_bRefreshAfterWrite = bRefreshAfterWrite;
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
            ((WriteOnChangeHandler)listener).init(null, m_bRefreshAfterWrite);
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * The Field has Changed.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        Record record = this.getOwner().getRecord();
        if (record != null)
        {
            try   {
                Object varBookmark = null;
                if (record.getEditMode() == DBConstants.EDIT_IN_PROGRESS)
                { // Update the record
                    if (m_bRefreshAfterWrite)
                        varBookmark = record.getHandle(DBConstants.DATA_SOURCE_HANDLE);
                    record.set();
                }
                else if (record.getEditMode() == DBConstants.EDIT_ADD)
                {
                    record.add();
                    if (m_bRefreshAfterWrite)
                        varBookmark = record.getLastModified(DBConstants.DATA_SOURCE_HANDLE);
                }
                if (varBookmark != null)
                {
                    record.setHandle(varBookmark, DBConstants.DATA_SOURCE_HANDLE);
                    record.edit();
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
                iErrorCode = ex.getErrorCode();
            }
        }
        return iErrorCode;
    }
}
