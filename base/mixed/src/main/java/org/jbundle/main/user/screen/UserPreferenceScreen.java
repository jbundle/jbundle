/**
 *  @(#)UserPreferenceScreen.
 *  Copyright © 2010 tourapp.com. All rights reserved.
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
 *  UserPreferenceScreen - User preferences screen.
 */
public class UserPreferenceScreen extends UserInfoBaseScreen
{
    /**
     * Default constructor.
     */
    public UserPreferenceScreen()
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
    public UserPreferenceScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return "User preferences screen";
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        this.readCurrentUser();
        super.addListeners();
        this.addAutoLoginHandler();
        this.getMainRecord().addListener(new UpdatePreferencesHandler(null));
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        int iLevel = Constants.LOGIN_USER;
        try {
            iLevel = Integer.parseInt(this.getProperty(Params.SECURITY_LEVEL));
        } catch (NumberFormatException ex) {
        }
        
        int iAccessAllowed = DBConstants.NORMAL_RETURN;
        
        if (iLevel == Constants.LOGIN_USER)
            if (!DBConstants.ANON_USER_ID.equalsIgnoreCase(this.getProperty(DBParams.USER_ID)))
        {
            if (this.getProperty(DBParams.USER_NAME) != null)
                iAccessAllowed = DBConstants.AUTHENTICATION_REQUIRED;   // If you have an account, you need to sign into it.
        }
        
        return iAccessAllowed;
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        return new MaintToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC)
        {
            public void setupMiddleSFields()
            {
                new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.SUBMIT);
                new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.RESET);
                String strDesc = UserPasswordChange.CHANGE_PASSWORD;
                String strCommand = Utility.addURLParam(null, DBParams.SCREEN, UserPasswordChange.class.getName());
                if (this.getProperty(DBParams.USER_NAME) == null)
                {
                    strDesc = UserEntryScreen.CREATE_NEW_USER;
                    strCommand = Utility.addURLParam(null, DBParams.SCREEN, UserEntryScreen.class.getName());
                }
                if (this.getTask() != null)
                    if (this.getTask().getApplication() != null)
                        strDesc = this.getTask().getApplication().getResources(ResourceConstants.MAIN_RESOURCE, true).getString(strDesc);
                new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON, ScreenConstants.DONT_SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, strDesc, MenuConstants.FORM, strCommand, MenuConstants.FORM + "Tip");
            }
        };
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kFirstName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kLastName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kLanguage).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kJava).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kNavMenus).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kHelpPage).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.ksaveuser).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.SUBMIT);
        String strDesc = UserPasswordChange.CHANGE_PASSWORD;
        String strCommand = Utility.addURLParam(null, DBParams.SCREEN, UserPasswordChange.class.getName());
        if (this.getProperty(DBParams.USER_NAME) == null)
        {
            strDesc = UserEntryScreen.CREATE_NEW_USER;
            strCommand = Utility.addURLParam(null, DBParams.SCREEN, UserEntryScreen.class.getName());
        }
        if (this.getTask() != null)
            if (this.getTask().getApplication() != null)
                strDesc = this.getTask().getApplication().getResources(ResourceConstants.MAIN_RESOURCE, true).getString(strDesc);
        new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.DONT_SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, strDesc, MenuConstants.FORM, strCommand, MenuConstants.FORM + "Tip");
        //this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kLogos).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        //this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kTrailers).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        //this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kHome).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        //this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kMenu).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        if (UserPasswordChange.CHANGE_PASSWORD.equalsIgnoreCase(strCommand))
        {
            ScreenLocation itsLocation = null;
            BasePanel parentScreen = this.getParentScreen();
            if ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_SAME_WINDOW)  // Use same window
                itsLocation = this.getScreenLocation();
            else
                parentScreen = Screen.makeWindow(this.getTask().getApplication());
            new UserPasswordChange(null, itsLocation, parentScreen, null, 0, null);
            return true;
        }
        else if (UserEntryScreen.CREATE_NEW_USER.equalsIgnoreCase(strCommand))
        {
            ScreenLocation itsLocation = null;
            BasePanel parentScreen = this.getParentScreen();
            if ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_SAME_WINDOW)  // Use same window
                itsLocation = this.getScreenLocation();
            else
                parentScreen = Screen.makeWindow(this.getTask().getApplication());
            new UserEntryScreen(null, itsLocation, parentScreen, null, 0, null);
            // Add here todo (don)
            return true;
        }
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }
    /**
     * Do the special HTML command.
     * This gives the screen a chance to change screens for special HTML commands.
     * You have a chance to change two things:
     * 1. The information display line (this will display on the next screen... ie., submit was successful)
     * 2. The error display line (if there was an error)
     * @return this or the new screen to display.
     */
    public BaseScreen doServletCommand(BasePanel screenParent)
    {
        BaseScreen screen = super.doServletCommand(screenParent);    // Process params from previous screen
        
        String strCommand = this.getProperty(DBParams.COMMAND);
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        if (MenuConstants.SUBMIT.equals(strCommand))
        {
            this.free();
            return null;    // This will cause the main menu to display
        }
        String strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(UserPasswordChange.CHANGE_PASSWORD);
        if (strDesc.equals(strCommand))
        {
            screen.free();
            screen = new UserPasswordChange(null, null, screenParent, null, 0, null);
            screen.setProperty(DBParams.SCREEN, UserPasswordChange.class.getName());
        }
        strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(UserEntryScreen.CREATE_NEW_USER);
        if (strDesc.equals(strCommand))
        {
            screen.free();
            screen = new UserEntryScreen(null, null, screenParent, null, 0, null);
            screen.setProperty(DBParams.SCREEN, UserEntryScreen.class.getName());
        }
        return screen;
    }

}
