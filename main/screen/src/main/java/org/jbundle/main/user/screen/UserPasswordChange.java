/**
 * @(#)UserPasswordChange.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.screen;

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
 *  UserPasswordChange - .
 */
public class UserPasswordChange extends UserInfoBaseScreen
{
    public static final String CHANGE_PASSWORD = "Change password";
    /**
     * Default constructor.
     */
    public UserPasswordChange()
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
    public UserPasswordChange(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        this.getRecord(UserInfo.USER_INFO_FILE).getField(UserInfo.USER_NAME).setEnabled(false);
        this.readCurrentUser();
        
        super.addListeners();
        
        this.getMainRecord().addListener(new UserPasswordHandler(true));
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        ToolScreen screen = new ToolScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
        new SCannedBox(screen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), screen, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.SUBMIT);
        new SCannedBox(screen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), screen, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.RESET);
        String strDesc = "Create account";
        new SCannedBox(screen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), screen, null, ScreenConstants.DEFAULT_DISPLAY, null, strDesc, MenuConstants.FORM, MenuConstants.FORM, MenuConstants.FORM + "Tip");
        return screen;
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
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        super.setupSFields();
        Converter converter = new HashSHAConverter(this.getScreenRecord().getField(UserScreenRecord.CURRENT_PASSWORD));
        converter = new FieldLengthConverter(converter, 20);
        converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        converter = new HashSHAConverter(this.getScreenRecord().getField(UserScreenRecord.NEW_PASSWORD_1));
        converter = new FieldLengthConverter(converter, 20, 6);
        converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        converter = new HashSHAConverter(this.getScreenRecord().getField(UserScreenRecord.NEW_PASSWORD_2));
        converter = new FieldLengthConverter(converter, 20, 6);
        converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        SCannedBox submitButton = new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.SUBMIT);
        submitButton.setRequestFocusEnabled(true);
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
        boolean bFlag = super.doCommand(strCommand, sourceSField, iCommandOptions);
        if (MenuConstants.SUBMIT.equalsIgnoreCase(strCommand))
            if (bFlag)
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
                this.getScreenRecord().getField(UserScreenRecord.CURRENT_PASSWORD).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
                this.getScreenRecord().getField(UserScreenRecord.NEW_PASSWORD_1).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
                this.getScreenRecord().getField(UserScreenRecord.NEW_PASSWORD_2).setData(null, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
            }
        }
        
        return screen;    // By default, don't do anything
    }

}
