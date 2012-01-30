/**
 * @(#)UserEntryScreen.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.user.db.*;
import org.jbundle.base.field.convert.encode.*;

/**
 *  UserEntryScreen - Create new user account.
 */
public class UserEntryScreen extends UserInfoBaseScreen
{
    public static final String CREATE_NEW_USER = "Create new account";
    /**
     * Default constructor.
     */
    public UserEntryScreen()
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
    public UserEntryScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return "Create new user account";
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        String strMessage = "Create new user account";
        String strTerms = this.getProperty("terms");    // Terms resource EY
        if (strTerms == null)
            strTerms = "terms";
        if (this.getTask() != null)
            if (this.getTask().getApplication() != null)
        {
            BaseApplication application = (BaseApplication)this.getTask().getApplication();
            strMessage = application.getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strMessage);
        
            strTerms = application.getResources(ResourceConstants.DEFAULT_RESOURCE, true).getString(strTerms);
        }
        this.getScreenRecord().getField(UserScreenRecord.STATUS_LINE).setString(strMessage);
        this.getScreenRecord().getField(UserScreenRecord.TERMS).setString(strTerms);
        
        //x this.readCurrentUser();
        
        super.addListeners();
        
        FieldListener listener = this.getMainRecord().getField(UserInfo.USER_NAME).getListener(MainFieldHandler.class);
        if (listener != null)
            this.getMainRecord().getField(UserInfo.USER_NAME).removeListener(listener, true);   // Don't read current accounts
        
        this.getMainRecord().addListener(new UserPasswordHandler(false));
        
        this.addAutoLoginHandler();
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
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
        return new MaintToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null)
        {
            public void setupMiddleSFields()
            {
                String strSubmitText = MenuConstants.SUBMIT;
                if (this.getTask() != null)
                    if (this.getTask().getApplication() != null)
                        strSubmitText = this.getTask().getApplication().getResources(ResourceConstants.MAIN_RESOURCE, true).getString(strSubmitText);
                new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, strSubmitText, MenuConstants.SUBMIT, MenuConstants.SUBMIT, null);
                new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.RESET);
            }
        };
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        super.setupSFields();
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kFirstName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kLastName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.addOtherSFields();   // Add other screen fields here
        Converter converter = new HashSHAConverter(this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kNewPassword1));
        converter = new FieldLengthConverter(converter, 20, 6);
        converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        converter = new HashSHAConverter(this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kNewPassword2));
        converter = new FieldLengthConverter(converter, 20, 6);
        converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.ksaveuser).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        String strSubmitText = MenuConstants.SUBMIT;
        if (this.getTask() != null)
            if (this.getTask().getApplication() != null)
                strSubmitText = this.getTask().getApplication().getResources(ResourceConstants.MAIN_RESOURCE, true).getString(strSubmitText);
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, strSubmitText, MenuConstants.SUBMIT, MenuConstants.SUBMIT, null);
        this.getRecord(UserScreenRecord.kUserScreenRecordFile).getField(UserScreenRecord.kTerms).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * AddOtherSFields Method.
     */
    public void addOtherSFields()
    {
        // override this to add other screen fields
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
        boolean bLogin = false;
        if (MenuConstants.SUBMIT.equalsIgnoreCase(strCommand))
            if (this.getMainRecord().isModified())
                bLogin = true;
        boolean bFlag = super.doCommand(strCommand, sourceSField, iCommandOptions);
        if (MenuConstants.SUBMIT.equalsIgnoreCase(strCommand))
            if (bFlag)
                if (bLogin)
            return super.doCommand(MenuConstants.HOME, sourceSField, iCommandOptions);
        return bFlag;
    }
    /**
     * Do the special HTML command.
     * This gives the screen a chance to change screens for special HTML commands.
     * You have a chance to change two things:
     * 1. The information display line (this will display on the next screen... ie., submit was successful)
     * 2. The error display line (if there was an error)
     * @return this or the new screen to display.
     */
    public ScreenModel doServletCommand(ScreenModel screenParent)
    {
        String strCommand = this.getProperty(DBParams.COMMAND);
        if (strCommand != null)
            if (this.getTask() != null)
                if (this.getTask().getApplication() != null)
                    if (strCommand.equalsIgnoreCase(this.getTask().getApplication().getResources(ResourceConstants.MAIN_RESOURCE, true).getString(MenuConstants.SUBMIT)))
                        this.setProperty(DBParams.COMMAND, MenuConstants.SUBMIT);
        
        ScreenModel screen = super.doServletCommand(screenParent);    // Process params from previous screen
        
        if (MenuConstants.SUBMIT.equalsIgnoreCase(this.getProperty(DBParams.COMMAND)))
        {
            if (this.getTask().getStatusText(DBConstants.WARNING_MESSAGE) == null)
            {   // Normal return = logged in, go to main menu.
                this.free();
                return null;    // This will cause the main menu to display
            }
            else
            {
                this.getScreenRecord().getField(UserScreenRecord.NEW_PASSWORD_1).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
                this.getScreenRecord().getField(UserScreenRecord.NEW_PASSWORD_2).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
            }
        }
        
        return screen;    // By default, don't do anything
    }

}
