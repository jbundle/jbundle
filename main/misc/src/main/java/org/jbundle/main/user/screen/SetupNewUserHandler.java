/**
 * @(#)SetupNewUserHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.screen;

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
import org.jbundle.main.user.db.*;

/**
 *  SetupNewUserHandler - Setup a new user by replicating the user template and the registration that comes with it..
 */
public class SetupNewUserHandler extends FileListener
{
    protected UserControl userControl = null;
    /**
     * Default constructor.
     */
    public SetupNewUserHandler()
    {
        super();
    }
    /**
     * SetupNewUserHandler Method.
     */
    public SetupNewUserHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        userControl = null;
        super.init(record);
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        UserInfo userTemplate = this.getUserTemplate();
        if (userTemplate != null)
        {
            Record userInfo = this.getOwner();
            boolean[] fileListenerStates = userInfo.setEnableListeners(false);
            Object[] fieldListenerStates = userInfo.setEnableFieldListeners(false);
            userInfo.moveFields(userTemplate, null, bDisplayOption, DBConstants.INIT_MOVE, false, false, false);
            userInfo.getField(UserInfo.kID).initField(bDisplayOption);
            userInfo.getField(UserInfo.kFirstName).initField(bDisplayOption);
            userInfo.getField(UserInfo.kLastName).initField(bDisplayOption);
            userInfo.getField(UserInfo.kUserName).initField(bDisplayOption);
            userInfo.getField(UserInfo.kPassword).initField(bDisplayOption);
            userInfo.getField(UserInfo.kID).setModified(false);
            userInfo.getField(UserInfo.kFirstName).setModified(false);
            userInfo.getField(UserInfo.kLastName).setModified(false);
            userInfo.getField(UserInfo.kUserName).setModified(false);
            userInfo.getField(UserInfo.kPassword).setModified(false);
            userInfo.setEnableListeners(fileListenerStates);
            userInfo.setEnableFieldListeners(fieldListenerStates);
        }
        super.doNewRecord(bDisplayOption);
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
        if (iChangeType == DBConstants.AFTER_ADD_TYPE)
        {
            UserInfo userTemplate = this.getUserTemplate();
            if (userTemplate != null)
            {
                Record userInfo = this.getOwner();
                Object bookmark = userInfo.getLastModified(DBConstants.BOOKMARK_HANDLE);
                RecordOwner recordOwner = Utility.getRecordOwner(this.getOwner());
                UserRegistration userRegistration = new UserRegistration(recordOwner);
                UserRegistration newUserRegistration = new UserRegistration(recordOwner);
                userRegistration.addListener(new SubFileFilter(userTemplate));
                try {
                    while (userRegistration.hasNext())
                    {
                        userRegistration.next();
                        newUserRegistration.addNew();
                        newUserRegistration.moveFields(userRegistration, null, bDisplayOption, DBConstants.INIT_MOVE, true, false, false);
                        newUserRegistration.getField(UserRegistration.kID).initField(bDisplayOption);
                        newUserRegistration.getField(UserRegistration.kUserID).setData(bookmark);
                        newUserRegistration.add();
                    }
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }
    /**
     * GetUserTemplate Method.
     */
    public UserInfo getUserTemplate()
    {
        if (userControl == null)
            userControl = new UserControl(Utility.getRecordOwner(this.getOwner()));
        if (userControl != null)
            if ((userControl.getEditMode() == DBConstants.EDIT_CURRENT) || (userControl.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
        {
            UserInfo userInfo = (UserInfo)((ReferenceField)userControl.getField(UserControl.kTemplateUserID)).getReference();
            if (userInfo != null)
                if ((userInfo.getEditMode() == DBConstants.EDIT_CURRENT) || (userInfo.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    return userInfo;
        }
        return null;
    }

}
