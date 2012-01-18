/**
 * @(#)UserPasswordHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;

/**
 *  UserPasswordHandler - Verify and move the password to the UserInfo record.
 */
public class UserPasswordHandler extends FileListener
{
    protected boolean m_bCheckOldPassword = false;
    /**
     * Default constructor.
     */
    public UserPasswordHandler()
    {
        super();
    }
    /**
     * UserPasswordHandler Method.
     */
    public UserPasswordHandler(boolean bCheckOldPassword)
    {
        this();
        this.init(bCheckOldPassword);
    }
    /**
     * Initialize class fields.
     */
    public void init(boolean bCheckOldPassword)
    {
        m_bCheckOldPassword = bCheckOldPassword;
        super.init(null);
    }
    /**
     * Set the object that owns this listener.
     * @owner The object that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
        {
            Record recUserInfo = this.getOwner();
            RecordOwner recordOwner = recUserInfo.getRecordOwner();
            Record recUserScreenRecord = recordOwner.getScreenRecord();
            // This will flag the password as changed:
            recUserScreenRecord.getField(UserScreenRecord.kNewPassword1).addListener(new ChangeOnChangeHandler(recUserInfo.getField(UserInfo.kPassword), true));
            recUserScreenRecord.getField(UserScreenRecord.kNewPassword2).addListener(new ChangeOnChangeHandler(recUserInfo.getField(UserInfo.kPassword), true));
        }
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        Record recUserInfo = this.getOwner();
        RecordOwner recordOwner = recUserInfo.getRecordOwner();
        Record recUserScreenRecord = recordOwner.getScreenRecord();
        recUserScreenRecord.getField(UserScreenRecord.kCurrentPassword).setData(null);
        recUserScreenRecord.getField(UserScreenRecord.kNewPassword1).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
        recUserScreenRecord.getField(UserScreenRecord.kNewPassword2).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
        recUserScreenRecord.getField(UserScreenRecord.kCurrentPassword).setModified(false);
        recUserScreenRecord.getField(UserScreenRecord.kNewPassword1).setModified(false);
        recUserScreenRecord.getField(UserScreenRecord.kNewPassword2).setModified(false);
        
        recUserScreenRecord.getField(UserScreenRecord.kStatusLine).setString(DBConstants.BLANK);
        super.doNewRecord(bDisplayOption);
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        Record recUserInfo = this.getOwner();
        RecordOwner recordOwner = recUserInfo.getRecordOwner();
        Record recUserScreenRecord = recordOwner.getScreenRecord();
        recUserScreenRecord.getField(UserScreenRecord.kCurrentPassword).setModified(false);
        recUserScreenRecord.getField(UserScreenRecord.kCurrentPassword).setData(null);
        
        recUserScreenRecord.getField(UserScreenRecord.kNewPassword1).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
        recUserScreenRecord.getField(UserScreenRecord.kNewPassword2).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
        recUserScreenRecord.getField(UserScreenRecord.kNewPassword1).setModified(false);
        recUserScreenRecord.getField(UserScreenRecord.kNewPassword2).setModified(false);
        
        recUserScreenRecord.getField(UserScreenRecord.kStatusLine).setString(DBConstants.BLANK);
        super.doValidRecord(bDisplayOption);
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
        if ((iChangeType == DBConstants.ADD_TYPE) || (iChangeType == DBConstants.UPDATE_TYPE))
        {
            Record recUserInfo = this.getOwner();
            RecordOwner recordOwner = recUserInfo.getRecordOwner();
            Record recUserScreenRecord = recordOwner.getScreenRecord();
            Task task = recordOwner.getTask();
        
            if (m_bCheckOldPassword)
            {
                if (iChangeType == DBConstants.ADD_TYPE)
                    return task.setLastError(task.getString("Can't add a new account on this screen."));
                if (!recUserInfo.getField(UserInfo.kPassword).equals(recUserScreenRecord.getField(UserScreenRecord.kCurrentPassword)))
                {
                    return task.setLastError(task.getString("Error, current password was incorrect."));
                }
            }
            
            if ((recUserScreenRecord.getField(UserScreenRecord.kNewPassword1).isModified())
                || (recUserScreenRecord.getField(UserScreenRecord.kNewPassword2).isModified()))
            {
                if (recUserScreenRecord.getField(UserScreenRecord.kNewPassword1).equals(recUserScreenRecord.getField(UserScreenRecord.kNewPassword2)))
                {
                    recUserInfo.getField(UserInfo.kPassword).moveFieldToThis(recUserScreenRecord.getField(UserScreenRecord.kNewPassword1));
                }
                else
                {
                    return task.setLastError(task.getString("Error, new passwords are not equal."));
                }
            }
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }

}
