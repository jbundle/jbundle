/**
 * @(#)UserFileHandler.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.main.db.*;

/**
 *  UserFileHandler - Only read the record for the current user (similar to Control File Behavior).
 */
public class UserFileHandler extends FileListener
{
    /**
     * Default constructor.
     */
    public UserFileHandler()
    {
        super();
    }
    /**
     * UserFileHandler Method.
     */
    public UserFileHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        super.init(record);
    }
    /**
     * Set the object that owns this listener.
     * @owner The object that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner == null)
            return;
        try   {     // Open this table and move to the control record
            if (!this.getOwner().isOpen())
                this.getOwner().open();     // Open the control file
            if (this.getOwner().getEditMode() == Constants.EDIT_NONE)
                this.doNewRecord(true);     // Read the control file
        } catch (DBException e)   {
            return;
        }
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        super.doNewRecord(bDisplayOption);
        if (this.getOwner().isOpen())   // Don't do first time!
        {
            String strUserID = ((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()).getUserID();
            int iUserID = -1;
            if ((strUserID != null) && (strUserID.length() > 0))
                iUserID = Integer.parseInt(strUserID);
            this.getOwner().getField(UserInfo.ID).setValue(iUserID);
            String strOldKey = this.getOwner().getKeyName();
            try   {
                this.getOwner().setKeyArea(UserInfo.ID_KEY);
                if (!this.getOwner().seek("="))
                {
                    this.getOwner().addNew(); // Make a new one
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            } finally   {
                this.getOwner().setKeyArea(strOldKey);
            }
        }
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * ADD_TYPE - Before a write.
     * UPDATE_TYPE - Before an update.
     * DELETE_TYPE - Before a delete.
     * AFTER_UPDATE_TYPE - After a write or update.
     * LOCK_TYPE - Before a lock.
     * SELECT_TYPE - After a select.
     * DESELECT_TYPE - After a deselect.
     * MOVE_NEXT_TYPE - After a move.
     * AFTER_REQUERY_TYPE - Record opened.
     * SELECT_EOF_TYPE - EOF Hit.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
        if (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
        {   // Update this user's preferences
            MainApplication app = (MainApplication)this.getOwner().getRecordOwner().getTask().getApplication();
            app.readUserInfo(false, false);
        }
        return iErrorCode;
    }

}
