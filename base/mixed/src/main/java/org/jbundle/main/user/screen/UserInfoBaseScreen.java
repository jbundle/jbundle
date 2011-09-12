/**
 * @(#)UserInfoBaseScreen.
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
 *  UserInfoBaseScreen - Base screen for all User Info screens.
 */
public class UserInfoBaseScreen extends Screen
{
    /**
     * Default constructor.
     */
    public UserInfoBaseScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties Addition properties to pass to the screen.
     */
    public UserInfoBaseScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Get the screen display title.
     */
    public String getTitle()
    {
        return "Base screen for all User Info screens";
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new UserInfo(this);
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new UserControl(this);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new UserScreenRecord(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.addMainKeyBehavior();
        this.getMainRecord().getField(UserInfo.kUserGroupID).addListener(new InitFieldHandler(this.getRecord(UserControl.kUserControlFile).getField(UserControl.kAnonUserGroupID)));
        ((UserInfo)this.getMainRecord()).addPropertyListeners();
    }
    /**
     * ReadCurrentUser Method.
     */
    public boolean readCurrentUser()
    {
        String strUserName = this.getProperty(DBParams.USER_NAME);
        String strUserID = this.getProperty(DBParams.USER_ID);
        boolean bUserFound = false;
        if ((this.getMainRecord().getEditMode() == DBConstants.EDIT_CURRENT) || (this.getMainRecord().getEditMode() == DBConstants.EDIT_IN_PROGRESS))
        {
            bUserFound = true;
        }
        else
        {
            if ((strUserID != null) && (strUserID.length() > 0))
            {
                bUserFound = ((UserInfo)this.getMainRecord()).getUserInfo(strUserID, false);
                if (bUserFound)
                    if (this.getMainRecord().getField(UserInfo.kReadOnlyRecord).getState() == true)
                        bUserFound = false; // Can't change anonymous
            }
            if (!bUserFound)
                if ((strUserName != null) && (strUserName.length() > 0))
            {
                bUserFound = ((UserInfo)this.getMainRecord()).getUserInfo(strUserName, false);
                if (bUserFound)
                    if (this.getMainRecord().getField(UserInfo.kReadOnlyRecord).getState() == true)
                        bUserFound = false; // Can't change anonymous
            }
        }
        if (!bUserFound)
        {
            try {
                this.getMainRecord().addNew();
            } catch (DBException e) {
                e.printStackTrace();
            }
            this.getMainRecord().getField(UserInfo.kUserName).setString(strUserName);
            this.getMainRecord().getField(UserInfo.kUserName).setModified(false);   // Don't force a write
        }
        else
        {
            // TODO What do I do if this user exists?
        }
        
        return bUserFound;
    }
    /**
     * AddAutoLoginHandler Method.
     */
    public void addAutoLoginHandler()
    {
        this.getMainRecord().addListener(new FileListener(null)
        {
            public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
            {   // Return an error to stop the change
                int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
                if ((iChangeType == DBConstants.AFTER_ADD_TYPE) || (iChangeType == DBConstants.AFTER_UPDATE_TYPE))
                    if (iErrorCode == DBConstants.NORMAL_RETURN)
                {
                    Record recUserInfo = this.getOwner();
                    Task task = recUserInfo.getTask();
                    String strUserName = recUserInfo.getField(UserInfo.kID).toString();
                    if ((strUserName == null) || (strUserName.length() == 0))
                        strUserName = recUserInfo.getLastModified(DBConstants.BOOKMARK_HANDLE).toString();
                    String strPassword = recUserInfo.getField(UserInfo.kPassword).toString();
                    iErrorCode = task.getApplication().login(task, strUserName, strPassword, task.getProperty(DBParams.DOMAIN));   // Always okay
                }
                return iErrorCode;
            }
        });
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kUserName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
