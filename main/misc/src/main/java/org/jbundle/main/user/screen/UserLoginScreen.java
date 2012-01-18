/**
 * @(#)UserLoginScreen.
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
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.user.db.*;
import org.jbundle.base.field.convert.encode.*;
import org.jbundle.base.screen.control.servlet.*;

/**
 *  UserLoginScreen - User sign in screen.
 */
public class UserLoginScreen extends Screen
{
    /**
     * Default constructor.
     */
    public UserLoginScreen()
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
    public UserLoginScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return "User sign in screen";
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        String strUserName = this.getProperty(DBParams.USER_NAME);
        this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kuser).setString(strUserName);
        
        String strMessage = "Login required";
        if (this.getTask() != null)
            if (this.getTask().getApplication() != null)
        {
            BaseApplication application = (BaseApplication)this.getTask().getApplication();
            strMessage = application.getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strMessage);
            this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kStatusLine).setString(strMessage);
        }
        
        super.addListeners();
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
     * Everyone can access this screen.
     */
    public int checkSecurity()
    {
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        ToolScreen screen = new ToolScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
        new SCannedBox(screen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), screen, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.RESET);
        new SCannedBox(screen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), screen, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.LOGIN);
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        String strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(UserEntryScreen.CREATE_NEW_USER);
        String strCommand = Utility.addURLParam(null, DBParams.SCREEN, UserEntryScreen.class.getName());
        new SCannedBox(screen.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.DONT_SET_ANCHOR), screen, null, ScreenConstants.DEFAULT_DISPLAY, null, strDesc, MenuConstants.FORM, strCommand, MenuConstants.FORM + "Tip");
        return screen;
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        Converter fieldConverter = this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kStatusLine);
        new SStaticText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, fieldConverter, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
        this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kuser).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        Converter converter = new HashSHAConverter(this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kpassword));
        converter = new FieldLengthConverter(converter, 20);
        converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.ksaveuser).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        SCannedBox loginButton = new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.LOGIN);
        loginButton.setRequestFocusEnabled(true);
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        String strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(UserEntryScreen.CREATE_NEW_USER);
        String strCommand = Utility.addURLParam(null, DBParams.SCREEN, UserEntryScreen.class.getName());
        new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.DONT_SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, strDesc, MenuConstants.FORM, strCommand, MenuConstants.FORM + "Tip");
        this.setDefaultButton(loginButton);
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
        if (MenuConstants.LOGIN.equalsIgnoreCase(strCommand))
        {
            Task task = this.getTask();
            if (task != null)
            {
                String strUserName = this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kuser).toString();
                String strPassword = this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kpassword).toString();
        
                String strOldUserID = this.getProperty(DBParams.USER_ID);
                int iErrorCode = task.getApplication().login(task, strUserName, strPassword, this.getProperty(DBParams.DOMAIN));
                if (iErrorCode == DBConstants.NORMAL_RETURN)
                {
                    if (this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.ksaveuser).getState() == true)
                        if (((Application)task.getApplication()).getMuffinManager() != null)
                            if (((Application)task.getApplication()).getMuffinManager().isServiceAvailable())
                                if (this.getProperty(DBParams.USER_ID) != null)
                                    if (!DBConstants.ANON_USER_ID.equalsIgnoreCase(this.getProperty(DBParams.USER_ID)))
                                        ((Application)task.getApplication()).getMuffinManager().setMuffin(DBParams.USER_ID, this.getProperty(DBParams.USER_ID));
                    if ((strOldUserID == null) || (!strOldUserID.equalsIgnoreCase(this.getProperty(DBParams.USER_ID))))
                    {   // If user changes, clear history and go home
                        return this.handleCommand(MenuConstants.HOME, sourceSField, iCommandOptions);
                    }
                    else
                        return this.handleCommand(DBConstants.BACK, sourceSField, iCommandOptions);
                }
                else
                {   // Error - Get and display the error
                    String strError = task.getLastError(iErrorCode);
                    this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kStatusLine).setString(strError);
                }
            }
            // May want to add some code here to login if this is used in thick client
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
     * BaseScreen screen = super;    // Process params from previous screen
     * 
     * String strCommand = this.getProperty(DBParams.COMMAND);
     * BaseApplication application = (BaseApplication)this.getTask().getApplication();
     * String strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(MenuConstants.LOGIN);
     * if (strDesc.equals(strCommand))
     * {
     *     String strUserName = this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kuser).toString();
     *     String strPassword = this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kpassword).toString();
     *     Task task = this.getTask();
     * 
     *     int iErrorCode = task.getApplication().login(task, strUserName, strPassword);
     *     if (iErrorCode == DBConstants.NORMAL_RETURN)
     *     {
     *         if (this.getTask().getStatusText(DBConstants.WARNING_MESSAGE) == null)
     *         {   // Normal return = logged in, go to main menu.
     *             this.free();
     *             return null;    // This will cause the main menu to display
     *         }
     *     }
     *     else
     *     {   // Error - Get and display the error
     *         String strError = task.getLastError(iErrorCode);
     *         this.getTask().setStatusText(strError, DBConstants.WARNING_MESSAGE);
     *         this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kpassword).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
     *     }
     * }
     * strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(UserEntryScreen.CREATE_NEW_USER);
     * if (strDesc.equals(strCommand))
     * {
     *     screen.free();
     *     screen = new UserEntryScreen(null, null, screenParent, null, 0, null);
     *     screen.setProperty(DBParams.SCREEN, UserEntryScreen.class.getName());
     * }
     * return screen;.
     */
    public BaseScreen doServletCommand(BasePanel screenParent)
    {
        BaseScreen screen = super.doServletCommand(screenParent);    // Process params from previous screen
        
        String strCommand = this.getProperty(DBParams.COMMAND);
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        String strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(MenuConstants.LOGIN);
        if (strDesc.equals(strCommand))
        {
            if (this.getTask().getStatusText(DBConstants.WARNING_MESSAGE) == null)
            {   // Normal return = logged in, go to main menu.
                this.free();
                return null;    // This will cause the main menu to display
            }
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
    /**
     * Get this screen's hidden params.
     * @return This screens hidden params.
    .
     */
    public Map<String, Object> getHiddenParams()
    {
        Map<String, Object> mapParams = super.getHiddenParams();
        if (this.getTask() instanceof ServletTask)
            mapParams = ((ServletTask)this.getTask()).getRequestProperties(((ServletTask)this.getTask()).getServletRequest(), false);
        mapParams.remove(DBParams.USER_NAME);
        mapParams.remove(DBParams.USER_ID);
        return mapParams;
    }

}
