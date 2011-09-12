/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)AppletScreen.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Point;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.DatabaseException;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.PasswordField;
import org.jbundle.base.field.StringField;
import org.jbundle.base.field.convert.FieldLengthConverter;
import org.jbundle.base.field.convert.encode.HashSHAConverter;
import org.jbundle.base.field.event.FieldListener;
import org.jbundle.base.screen.model.util.MenuToolbar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.screen.view.ScreenFieldView;
import org.jbundle.base.screen.view.ViewFactory;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.UserProperties;
import org.jbundle.base.util.Utility;
import org.jbundle.main.user.screen.UserEntryScreen;
import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.thread.RecordOwnerCollection;
import org.jbundle.thin.base.thread.TaskScheduler;
import org.jbundle.thin.base.util.Application;


/**
 * BasePanel - This is the base for any data display screen.
 * <p>NOTE: You should probably split this class into two; one for Containers and
 * the overriding class as a FileContainer. (Frames and Applets would override ContainerScreen,
 * Screen and GridScreen would override FileScreen)
 * <pre>
 *  Notes on Keyboard BaseListener:
 *      Accelerator keys:
 *  Ctrl+Enter - Newline
 *  Shift+Enter - New Record
 *  Enter (x) - New Record
 *  Enter (keyboard) / (Multiline) - Newline
 *  Enter (keyboard) / (Singleline) - Tab to next field
 *  Ctrl+plus(+) - New Record
 *  Ctrl+minus(-) - Delete Record
 *  Esc+Esc - Refresh Record
 *  Ctrl+Page Down - Next Record
 *  Ctrl+Page Up - Previous Record
 *  Tab - Next field
 *  Shift+Tab - Previous field
 *  Ctrl+Tab - Tab in text
 * </pre>
 */
public class BasePanel extends ScreenField
    implements PropertyOwner, RecordOwnerParent
{
    /**
     * All the screen fields in this BasePanel.
     */
    protected Vector<ScreenField> m_SFieldList = null;       // Screen fields in this window (for display window only)
    /**
     * Key to the registration database (If open).
     */    
    protected PropertyOwner m_registration = null;
    /**
     *  History of sub-screens contained in this Applet Screen (for the 'back' command).
     */
    protected Vector<String> m_vHistory = null;
    /**
     * Children record owners.
     */
    protected RecordOwnerCollection m_recordOwnerCollection = null;

    /**
     * Constructor.
     */
    public BasePanel()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public BasePanel(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
    	this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null);
    }
    /**
     * Open the files and setup the screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        if (itsLocation == null) if (parentScreen != null)
            itsLocation = parentScreen.getNextLocation(ScreenConstants.BELOW_LAST_DESC, ScreenConstants.DONT_SET_ANCHOR);
        
        m_SFieldList = new Vector<ScreenField>();

        m_vHistory = null;
        if (properties != null)
        {
            for (String key : properties.keySet())
            {
        		this.setProperty(key, (String)properties.get(key));
        	}
        }

        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
        m_bIsFocusTarget = false; // Generally, can't tab to a panel

        int iErrorCode = this.checkSecurity();
        if ((iErrorCode != DBConstants.NORMAL_RETURN) && (iErrorCode != Constants.READ_ACCESS))
        {
            this.displaySecurityWarning(this.checkSecurity());
            return;
        }
        
        this.getScreenFieldView().addScreenLayout();            // Set up the screen layout
        this.setupSFields();                // Add the sub-views (usually overidden by the screen in use)
        this.addToolbars();
        this.makeSubScreen();
        this.addScreenMenus();
        this.addListeners();        // Add the screen behaviors
        if (iErrorCode == Constants.READ_ACCESS)
            this.setEditing(false);
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl If false, set up a read-only control.
     * @return The new view.
     */
    public ScreenFieldView setupScreenFieldView(boolean bEditableControl)
    {
        int iErrorCode = this.checkSecurity();
        if ((iErrorCode == DBConstants.NORMAL_RETURN) || (iErrorCode == Constants.READ_ACCESS))
            return super.setupScreenFieldView(bEditableControl);
        else
            return this.getViewFactory().setupScreenFieldView(this, Screen.class, bEditableControl);
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        App application = null;
        if (this.getTask() != null)
            application = this.getTask().getApplication();
        int iErrorCode = DBConstants.NORMAL_RETURN; 
        if (application != null)
            iErrorCode = application.checkSecurity(this.getClass().getName());
        return iErrorCode;
    }
    /**
     * Display the correct security warning (access denied on the login screen).
     * @param iErrorCode
     */
    public void displaySecurityWarning(int iErrorCode)
    {
        String strDisplay = this.getTask().getApplication().getSecurityErrorText(iErrorCode);
        if (iErrorCode == DBConstants.ACCESS_DENIED)
        {
            m_iDisplayFieldDesc = ScreenConstants.SECURITY_MODE;    // Make sure this screen is not cached in TopScreen
            this.getScreenFieldView().addScreenLayout();            // Set up the screen layout

            BaseApplication application = (BaseApplication)this.getTask().getApplication();
            String strMessage = application.getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strDisplay);
            new SStaticString(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, strMessage);
            new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_INPUT_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.BACK);
            new MenuToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
            this.resizeToContent(strDisplay);
        }
        if ((iErrorCode == DBConstants.LOGIN_REQUIRED) || (iErrorCode == DBConstants.AUTHENTICATION_REQUIRED))
        {
            m_iDisplayFieldDesc = ScreenConstants.SECURITY_MODE;    // Make sure this screen is not cached in TopScreen
            this.getScreenFieldView().addScreenLayout();            // Set up the screen layout

            BaseApplication application = (BaseApplication)this.getTask().getApplication();
            String strMessage = application.getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strDisplay);
            new SStaticString(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, strMessage);
            
            Record record = null;
            String strName = DBParams.USER_NAME;
            int iDataLength =  DBConstants.DEFAULT_FIELD_LENGTH;
            String strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(DBParams.USER_NAME);
            Object strDefault = application.getUserName();
            Converter converter = new StringField(record, strName, iDataLength, strDesc, strDefault);
            converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_INPUT_LOCATION, ScreenConstants.SET_ANCHOR), this, ScreenConstants.DISPLAY_DESC);

            strName = DBParams.PASSWORD;
            iDataLength =  DBConstants.DEFAULT_FIELD_LENGTH;
            strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(DBParams.PASSWORD);
            strDefault = null;
            iDataLength = 80;
            converter = new PasswordField(record, strName, iDataLength, strDesc, strDefault);
            converter = new HashSHAConverter(converter);
            converter = new FieldLengthConverter(converter, 20);
            converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_INPUT_LOCATION, ScreenConstants.SET_ANCHOR), this, ScreenConstants.DISPLAY_DESC);
            SCannedBox loginBox = new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_INPUT_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.LOGIN);
            loginBox.setRequestFocusEnabled(true);
            strDesc = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString("Create new account");
            String strCommand = Utility.addURLParam(null, DBParams.SCREEN, UserEntryScreen.class.getName());
            new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.DONT_SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, strDesc, MenuConstants.FORM, strCommand, MenuConstants.FORM + DBConstants.TIP);
            this.setDefaultButton(loginBox);
            ((BaseField)converter.getField()).addListener(new FieldListener(null)
            {
                public void setOwner(ListenerOwner owner)
                {
                    if (owner == null)
                        setDefaultButton(null);
                    super.setOwner(owner);
                }
            });

            for (int i = this.getSFieldCount(); i >= 0; i--)
            {
                if (this.getSField(i) instanceof ToolScreen)
                    this.getSField(i).free();
            }
            new MenuToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
            this.resizeToContent(strDisplay);
        }
    }
    /**
     * Free.
     */
    public void free()
    {
    	if (m_recordOwnerCollection != null)
    		m_recordOwnerCollection.free();
    	m_recordOwnerCollection = null;
        if (m_registration != null)
            ((UserProperties)m_registration).free();
        m_registration = null;
        this.freeAllSFields(true);
        m_SFieldList = null;

        super.free();
    }
    /**
     * Override to add screen behaviors.
     */
    public void addListeners()
    {
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {   // Override this to add (call this) or replace (don't call) this default toolbar
        return null;
    }
    /**
     * Add the toolbars that belong with this screen.
     * Override this to add buttons to the middle of this default toolbar.
     * @param toolScreen The toolbar to add controls to.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
//      new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, MenuConstants.POST, MenuConstants.POST, MenuConstants.POST, null);
    }
    /**
     * Move the control's value to the field.
     * @return An error code.
     */
    public int controlToField()
    {
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Move the field's value to the control.
     * NOTE: Not used for screens.
     */
    public void fieldToControl()
    {
    }
    /**
     * Display a dialog/status box with this error.
     * @param iErrorCode The error code.
     */
    public void displayError(int iErrorCode)
    {
        if (iErrorCode < -2)
        {   // Error codes < -2 are usually user defined (and available at 'LastError')
            Task task = this.getTask();//getAppletScreen().getScreenFieldView().getControl();
            if (task != null)
            {
                String strError = task.getLastError(iErrorCode);
                if ((strError != null) && (strError.length() > 0))
                {
                    this.displayError(strError);
                    return;
                }
            }
        }
        DBException e = new DBException(iErrorCode);
        this.displayError(e);
    }
    /**
     * Display a dialog/status box with this error.
     * @param strError The error text.
     */
    public void displayError(String strError)
    {
        this.displayError(strError, DBConstants.WARNING_MESSAGE);
    }
    /**
     * Display a dialog/status box with this error.
     * @param ex The error exception.
     */
    public void displayError(DBException ex)
    {
        int iWarningLevel = DBConstants.WARNING_MESSAGE;
        String strMessage = DBConstants.BLANK;
        if (ex instanceof DatabaseException)
        {
            iWarningLevel = ((DatabaseException)ex).getWarningLevel();
            strMessage = ((DatabaseException)ex).getMessage(this.getTask());
        }
        else
            strMessage = ex.getMessage();
        this.displayError(strMessage, iWarningLevel);
    }
    /**
     * Display a dialog/status box with this error.
     * @param strError The error text.
     * @param The level of the error (debug/warning/error/etc).
     */
    public void displayError(String strError, int iWarningLevel)
    {
        if ((strError == null) || (strError.length() == 0))
            return;     // No error
        Task task = this.getTask();
        if (task != null)
            task.setStatusText(strError, iWarningLevel);
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iUseSameWindow If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean handleCommand(String strCommand, ScreenField sourceSField, int iUseSameWindow)
    {
        boolean bHandled = this.doCommand(strCommand, sourceSField, iUseSameWindow);   // Do I handle it?

        if (bHandled == false)
        {   // Not handled by this screen, try child windows
            for (int iFieldSeq = 0; iFieldSeq < this.getSFieldCount(); iFieldSeq++)
            {   // See if any of my children want to handle this command
                ScreenField sField = this.getSField(iFieldSeq);
                if (sField != sourceSField)   // Don't call the child that passed this up
                {
                    bHandled = sField.handleCommand(strCommand, this, iUseSameWindow);  // Send to children (make sure they don't call me)
                    if (bHandled)
                        return bHandled;
                }
            }
        }
        if (bHandled == false)
            bHandled = super.handleCommand(strCommand, sourceSField, iUseSameWindow); // This will send the command to my parent
        return bHandled;
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        boolean bFlag = super.doCommand(strCommand, sourceSField, iCommandOptions); // This will send the command to my view
        if (bFlag)
            return bFlag;   // Handled by my view
    
        int iIndex = strCommand.indexOf('=');
        if (iIndex != -1)
        	if (this instanceof BaseScreen)
        {   // only BaseScreens send commands
            if ((strCommand.indexOf(DBParams.TASK + '=') != -1) || (strCommand.indexOf(DBParams.APPLET + '=') != -1))
            { // Asking to start a job
                Task task = this.getTask();
                if (task == null)
                    task = BaseApplet.getSharedInstance();  // ??
                TaskScheduler js = ((Application)task.getApplication()).getTaskScheduler();
                js.addTask(strCommand);
                return true;
            }
            BasePanel parentScreen = this.getParentScreen();
            ScreenLocation itsLocation = null;
            if ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_SAME_WINDOW)	// Use same window
                itsLocation = this.getScreenLocation();
            else
                parentScreen = Screen.makeWindow(this.getTask().getApplication());

            Task task = parentScreen.getTask();//getAppletScreen().getScreenFieldView().getControl();
            if ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_SAME_WINDOW)	// Use same window
                this.free();        // Remove this screen
            task.setProperties(null);
            Map<String,Object> properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, strCommand);        // Set these properties
            task.setProperties(properties);
            BaseScreen.makeScreenFromParams(task, itsLocation, parentScreen, iCommandOptions, properties);
            bFlag = true; // Handled
        }
        if (strCommand.equalsIgnoreCase(MenuConstants.RESET))
            bFlag = this.onRefresh();
        if (strCommand.equalsIgnoreCase(MenuConstants.DELETE))
            bFlag = this.onDelete();
        if (strCommand.equalsIgnoreCase(MenuConstants.BACK))
            bFlag = this.onBack();
        if (strCommand.equalsIgnoreCase(MenuConstants.CUT))
            bFlag = true; // Ignore this command (handled by CutActionHandler)
        if (strCommand.equalsIgnoreCase(MenuConstants.COPY))
            bFlag = true; // Ignore this command 
        if (strCommand.equalsIgnoreCase(MenuConstants.PASTE))
            bFlag = true; // Ignore this command 
                // Commands before this line do not validate the current field
        if (bFlag == false)     // Not yet handled
            if (strCommand.length() > 0)    // Valid command
                if (!(strCommand.equalsIgnoreCase(MenuConstants.UNDO)))   // Don't select first on undo
            this.selectField(null, DBConstants.SELECT_FIRST_FIELD);   // Validate current field
                // Commands after this line do validate the current field
        if (strCommand.equalsIgnoreCase(MenuConstants.FIRST))
            bFlag = this.onMove(DBConstants.FIRST_RECORD);
        if (strCommand.equalsIgnoreCase(MenuConstants.PREVIOUS))
            bFlag = this.onMove(DBConstants.PREVIOUS_RECORD);
        if (strCommand.equalsIgnoreCase(MenuConstants.NEXT))
            bFlag = this.onMove(DBConstants.NEXT_RECORD);
        if (strCommand.equalsIgnoreCase(MenuConstants.LAST))
            bFlag = this.onMove(DBConstants.LAST_RECORD);
        if (strCommand.equalsIgnoreCase(MenuConstants.SUBMIT))
            bFlag = this.onAdd();
        if (strCommand.equalsIgnoreCase(MenuConstants.LOOKUP))
            return (this.onForm(null, (((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_SAME_WINDOW) ? ScreenConstants.DISPLAY_MODE : ScreenConstants.SELECT_MODE), true, iCommandOptions, null) != null);
        if (strCommand.equalsIgnoreCase(MenuConstants.FORM))
            bFlag = (this.onForm(null, ScreenConstants.MAINT_MODE, false, iCommandOptions, null) != null);
        if (strCommand.equalsIgnoreCase(MenuConstants.FORMLINK))
            bFlag = (this.onForm(null, ScreenConstants.MAINT_MODE, true, iCommandOptions, null) != null);
        if (strCommand.equalsIgnoreCase(MenuConstants.LOOKUPCLONE))
            return (this.onForm(null, ScreenConstants.SELECT_MODE, true, ScreenConstants.USE_NEW_WINDOW, null) != null);
        if (strCommand.equalsIgnoreCase(MenuConstants.FORMCLONE))
            bFlag = (this.onForm(null, ScreenConstants.MAINT_MODE, true, ScreenConstants.USE_NEW_WINDOW, null) != null);
        if (strCommand.equalsIgnoreCase(MenuConstants.FORMDETAIL))
            bFlag = (this.onForm(null, ScreenConstants.DETAIL_MODE, true, iCommandOptions, null) != null);
        if (strCommand.equalsIgnoreCase(MenuConstants.POST))
            bFlag = (this.onForm(null, ScreenConstants.POST_MODE, true, iCommandOptions, null) != null);
        if (strCommand.equalsIgnoreCase(MenuConstants.HELP))
            bFlag = this.onHelp(iCommandOptions);
        if (strCommand.equalsIgnoreCase(MenuConstants.HOME))
            bFlag = this.onHome();
        if (strCommand.equalsIgnoreCase(MenuConstants.CLONE))
            bFlag = this.onClone();
        if (strCommand.equalsIgnoreCase(MenuConstants.NEW_WINDOW))
            bFlag = this.onNewWindow();
        if (strCommand.equalsIgnoreCase(MenuConstants.DISPLAY))
            bFlag = this.onDisplay();
        if (strCommand.equalsIgnoreCase(MenuConstants.LOGIN))
            bFlag = this.onLogin();
        if (strCommand.equalsIgnoreCase(MenuConstants.LOGON))
            bFlag = this.onLogon();
        if (strCommand.equalsIgnoreCase(MenuConstants.LOGOUT))
            bFlag = this.onLogout();
        if (strCommand.equalsIgnoreCase(MenuConstants.CHANGE_PASSWORD))
            bFlag = this.onChangePassword();
        if (strCommand.equalsIgnoreCase(MenuConstants.SETTINGS))
            bFlag = this.onChangeSettings();
        return bFlag;
    }
    /**
     * This field changed, if this is the main record, lock it!
     * @param field The field that changed.
     */
    public void fieldChanged(BaseField field)
    {
        Record record = this.getMainRecord();
        if (field != null)
            if (record != null)
            if (field.getRecord() == record)
        {
            int iErrorCode= record.handleRecordChange(field, DBConstants.FIELD_CHANGED_TYPE, DBConstants.DONT_DISPLAY);   // Tell table that I'm getting changed (if not locked)
            if (iErrorCode != DBConstants.NORMAL_RETURN)
                this.displayError(iErrorCode);
        }
    }
    /**
     * Free all the child sFields.
     * @param bIncludeToolScreens If true, also free the toolScreens.
     */
    public void freeAllSFields(boolean bIncludeToolScreens)
    {
        int iToolScreens = 0;
        while (this.getSFieldCount() > iToolScreens)
        {   // First, get rid of all child screens.
            ScreenField sField = this.getSField(iToolScreens);
            if ((!bIncludeToolScreens) && (sField instanceof ToolScreen))
                iToolScreens++;
            else
                sField.free();
        }
    }
    /**
     * Do I include a description for sField?
     * Generally yes, except for on GridScreens.
     * @param sField field to check for description includes.
     */
    public boolean getDisplayFieldDesc(ScreenField sField)
    {
        if (sField == this)
            return false; // Panels generally do not have descriptions
        if (sField instanceof SButtonBox)
            if (((SButtonBox)sField).getImageButtonName() != null)
                return false;    // Buttons don't need their field descriptions (if there is a icon) in base screens
        return true;    // But the controls in them generally do.
    }
    /**
     * Is editing enabled?
     * @return true if enabled.
     */
    public boolean getEditing()
    {
        return this.isEnabled();
    }
    /**
     * Get the main record for this screen.
     * @return The main record.
     */
    public Record getMainRecord()
    {
        if (m_screenParent != null)
            return m_screenParent.getMainRecord();  // Look thru the parent window now
        else
            return null;
    }
    /**
     * Setup a ScreenLocation object that contains this Row and Column.
     * @param iColumn The column of this control.
     * @param iRow The row for this control.
     * @param setNewAnchor Set anchor?
     * @return The new screen location constant.
     */
    public ScreenLocation getNextLocation(int iColumn, int iRow, short setNewAnchor)
    {
        return new ScreenLocation(iRow, iColumn, setNewAnchor);
    }
    /**
     * Setup a ScreenLocation object that contains this Row and Column.
     * @param columnRow The position of the next location (row and column).
     * @param setNewAnchor Set anchor?
     * @return The new screen location constant.
     */
    public ScreenLocation getNextLocation(Point columnRow, short setNewAnchor)
    {
        return this.getNextLocation(columnRow.x + 1, columnRow.y + 1, setNewAnchor);
    }
    /**
     * Code this position and Anchor to add it to the LayoutManager.
     * @param position The position of the next location (see the position constants).
     * @param setNewAnchor Set anchor?
     * @return The new screen location constant.
     */
    public ScreenLocation getNextLocation(short position, short setNewAnchor)
    {
        return new ScreenLocation(position, setNewAnchor);
    }
    /**
     * Lookup this record in this screen.
     * @param The file name of the record to find.
     * @param The record (or null if not found).
     */
    public Record getRecord(String strFileName)
    {
        if (m_screenParent != null)
            return m_screenParent.getRecord(strFileName); // Look thru the parent window now
        else
            return null;
    }
    /**
     * Get base panel.
     * @return The base panel.
     */
    public BasePanel getBasePanel()
    {
        return (BasePanel)this;
    } // Get first parent up from here (or this if BasePanel!)
    /**
     * Get the top level screen.
     * @return The top level screen.
     */
    public BasePanel getRootScreen()
    {
        if (this.getParentScreen() != null)
            return super.getRootScreen();
        else
            return (BasePanel)this;    // This is the top level screen!
    }
    /**
     * Get the top applet screen.
     * @return The applet screen.
     */
    public AppletScreen getAppletScreen()
    { // The root window is the ChildScreen
        if (this.getParentScreen() != null)
            return getParentScreen().getAppletScreen();
        else
            return null;    // This is the top level screen!
    }
    /**
     * Add the menus that belong with this screen (override).
     */
    public void addScreenMenus()
    {
        // Override to add menu's to this screen
    }
    /**
     * Get the screen record.
     * @return The screen record.
     */
    public Record getScreenRecord()
    {
        if (m_screenParent != null)
            return m_screenParent.getScreenRecord();    // Look thru the parent window now
        else
            return null;
    }
    /**
     * Get the screen type.
     * @return the screen type.
     */
    public int getScreenType()
    {
        return m_iDisplayFieldDesc & ScreenConstants.SCREEN_TYPE_MASK;
    }
    /**
     * Overidden to supply the title for this screen.
     * @return the screen title.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    { // This is almost always overidden!
        return Constants.BLANK;
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        if (this.getParentScreen() != null)
            return this.getParentScreen().getTask();
        return null;    // Never
    }
    /**
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        return null;
    }
    /**
     * Make a screen window to put a screen in.
     * <br/>NOTE: This method returns the AppletScreen NOT the FrameScreen,
     * because the AppletScreen is where you add your controls!
     */
    public static BasePanel makeWindow(App application)
    {
        FrameScreen frameScreen = new FrameScreen(null, null, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
        AppletScreen appletScreen = new AppletScreen(null, frameScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
        
        appletScreen.setupDefaultTask((Application)application);
        
        return appletScreen;
    }
    /**
     * Process the "Add" toolbar command.
     * @return  true    If command was handled
     */
    public boolean onAdd()
    {
        Record record = this.getMainRecord();
        if (record == null)
            return false;

        try {
            if (record.isModified(false))
            {
                if (record.getEditMode() == Constants.EDIT_IN_PROGRESS)
                    record.set();
                else if (record.getEditMode() == Constants.EDIT_ADD)
                    record.add();
            }
            record.addNew();
            this.clearStatusText();
        } catch(DBException ex) {
            this.displayError(ex);
            return false;
        }
        return true;
    }
    /**
     * Process the "Delete" toolbar command.
     * @return  true    If command was handled
     */
    public boolean onDelete()
    {
        Record record = this.getMainRecord();
        if (record == null)
            return false;

        if (record.getEditMode() == Constants.EDIT_NONE)
            return true;
        if (record.getEditMode() == Constants.EDIT_ADD)
        {   // If they're adding, can't delete nothing!
            this.onRefresh();
            return true;
        }
        try {
            if (record.getEditMode() != Constants.EDIT_IN_PROGRESS)
                record.edit();
            record.remove();
            record.addNew();
            this.clearStatusText();
        } catch(DBException ex) {
            this.displayError(ex);
            return false;
        }
        return true;
    }
    /**
     * Process the "Help" toolbar command.
     * @param bUseSameWindow Use the same window?
     * @retrun true if successful.
     */
    public boolean onHelp(int iOptions)
    {
        String strPrevAction = this.getScreenURL();
        if ((strPrevAction == null) || (strPrevAction.length() == 0))
            return false;   // Can't handle help if I'm not the target screen.
        // Note: The following is a hack to make the demo screen stop displaying after the first help is pressed
        if (this.getProperty("displayInitialHelp") != null)
        	if ((this.getProperty("helplanguage") != null) && (this.getProperty("helplanguage").length() > 0))
        		if (this.getTask() != null)
        			if (this.getTask().getApplication() != null)
    		this.getTask().getApplication().setProperty("helplanguage", this.getTask().getApplication().getLanguage(true));
        strPrevAction = Utility.fixDisplayURL(strPrevAction, true, true, true, this);

        BaseApplet applet = null;
        if (this.getAppletScreen() != null)
        	if (this.getAppletScreen().getScreenFieldView() != null)
        		applet = (BaseApplet)this.getAppletScreen().getScreenFieldView().getControl();
        if (applet != null)
        	iOptions = applet.getHelpPageOptions(iOptions);
        if ((MenuConstants.HELP_WEB_OPTION & iOptions) == MenuConstants.HELP_WEB_OPTION)
            return this.getScreenFieldView().showDocument(strPrevAction, iOptions & 1);   // Not standalone
        else
        	return this.getAppletScreen().showHelpScreen(strPrevAction, iOptions);
    }
    /**
     * Create a data entry screen with this main record.
     * (null means use this screen's main record)
     * @param recordMain The main record for the new form.
     * @param iDocMode The document type of the new form.
     * @param bReadCurrentRecord Sync the new screen with my current record?
     * @param bUseSameWindow Use the same window?
     * @return true if successful.
     */
    public BasePanel onForm(Record recordMain, int iDocMode, boolean bReadCurrentRecord, int iCommandOptions, Map<String,Object> properties)
    {
        boolean bLinkGridToQuery = false;
        if ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_NEW_WINDOW)
            bLinkGridToQuery = true;
        return this.onForm(recordMain, iDocMode, bReadCurrentRecord, iCommandOptions, bLinkGridToQuery, properties);
    }
    /**
     * Create a data entry screen with this main record.
     * (null means use this screen's main record)
     * @param recordMain The main record for the new form.
     * @param iDocMode The document type of the new form.
     * @param bReadCurrentRecord Sync the new screen with my current record?
     * @param iCommandOptions Use the same window?
     * @return the screen if successful, null if not.
     */
    public BasePanel onForm(Record recordMain, int iDocMode, boolean bReadCurrentRecord, int iCommandOptions, boolean bLinkGridToQuery, Map<String,Object> properties)
    {
        if (recordMain == null)
            recordMain = this.getMainRecord();
        if (recordMain != null)
        {
            ScreenLocation itsLocation = null;
            BasePanel parentScreen = this.getParentScreen();
            if ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_SAME_WINDOW)	// Use same window
                itsLocation = this.getScreenLocation();
            else
                parentScreen = Screen.makeWindow(this.getTask().getApplication());
            boolean bUseBaseTable = true;
            boolean bCloneThisQuery = true;
            if ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_SAME_WINDOW)	// Use same window
            {
                parentScreen.popHistory(1, false);
                parentScreen.pushHistory(this.getScreenURL(), false);  // Update the history to my current state.
            }
            if (bCloneThisQuery)
            {
                try {
                    Object bookmark = null;
                    if ((recordMain.getEditMode() == DBConstants.EDIT_CURRENT) || (recordMain.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                        bookmark = recordMain.getHandle(DBConstants.BOOKMARK_HANDLE);
                    this.finalizeThisScreen();  // Validate current control, update record, get ready to close screen.
                    if (bookmark == null)
                    {
                        if ((recordMain.getEditMode() == DBConstants.EDIT_ADD) && (recordMain.isModified()))
                        {
                            recordMain.writeAndRefresh();
                            parentScreen.popHistory(1, false);
                            parentScreen.pushHistory(this.getScreenURL(), false);  // Update the history to my current state.
                        }
                        else
                            recordMain.addNew();
                    }
                    else if ((!bookmark.equals(recordMain.getHandle(DBConstants.BOOKMARK_HANDLE)))
                            || ((recordMain.getEditMode() == DBConstants.EDIT_NONE) || (recordMain.getEditMode() == DBConstants.EDIT_ADD)))
                        recordMain.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
            BasePanel screen = recordMain.makeScreen(itsLocation, parentScreen, iDocMode | iCommandOptions, bCloneThisQuery, bReadCurrentRecord, bUseBaseTable, bLinkGridToQuery, properties);
            return screen;    // Success
        }
        return null; // Command not processed
    }
    /**
     * Process the "Move" Commands.
     * @param nIDMoveCommand The move command (first/last/next/prev).
     * @return true if successful.
     */
    public boolean onMove(int nIDMoveCommand)
    {
        Record record = this.getMainRecord();
        if (record == null)
            return false;
        try
        {
            if (record.isModified(false))
            {
                if (record.getEditMode() == Constants.EDIT_IN_PROGRESS)
                    record.set();
                else if (record.getEditMode() == Constants.EDIT_ADD)
                    record.add();
            }
            if ((nIDMoveCommand == DBConstants.NEXT_RECORD) && (!record.hasNext()))
                this.onAdd();   // Next record, enter "Add" mode
            else if ((nIDMoveCommand == DBConstants.PREVIOUS_RECORD) && (!record.hasPrevious()))
                this.onMove(DBConstants.FIRST_RECORD);      // Can't move before the first record
            else
                record.move(nIDMoveCommand);
            record.isModified(false);
            this.clearStatusText();
        } catch(DBException ex) {
            this.displayError(ex);
            return false;
        }
        return true;    // Command processed
    }
    /**
     * Process the Refresh Command.
     * @return true if successful.
     */
    public boolean onRefresh()
    {
        Record record = this.getMainRecord();
        if (record == null)
            return false;

        try
        {
            if (record.getEditMode() == Constants.EDIT_IN_PROGRESS)
            {   // If they're adding, can't delete nothing!
                if (record.isRefreshedRecord())
                    return this.onDelete();
                Object bookmark = record.getHandle(DBConstants.BOOKMARK_HANDLE);
                record.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
            }
            if (record.getEditMode() == Constants.EDIT_ADD)
                record.addNew();
            this.clearStatusText();
        } catch (DBException ex) {
            this.displayError(ex);
            return false;
        }
        return true;
    }
    /**
     * Is this the user's main menu, Override if you can determine the answer.
     * @return true If this is the main menu.
     */
    public boolean isMainMenu()
    {
        return false;
    }
    /**
     * Get the command string that will restore this screen.
     * Override in Screen, GridScreen, MenuScreen.
     * @return The screen URL of this screen.
     */
    public String getScreenURL()
    {
        return Constants.BLANK;
    }
    /**
     * Get the command string that will restore this screen.
     * Override in Screen, GridScreen, MenuScreen.
     * @param strOldURL The URL to add this param to.
     * @param strParam The new param.
     * @param strData The new data for this param.
     * @return The new URL.
     * @see Utility.addURLParam.
     */
    public String addURLParam(String strOldURL, String strParam, String strData)
    {
        return Utility.addURLParam(strOldURL, strParam, strData);
    }
    /**
     * Process the "Back" toolbar command.
     * @return true if successful.
     */
    public boolean onBack()
    {
        BasePanel parent = this.getParentScreen();
        if (parent == null)
            return false;
        parent.popHistory(1, false);        // Pop command for this screen
        String strLastCommand = parent.popHistory(1, false);
        if (strLastCommand != null)		// I don't back up into the browser history if the user hit the java back button.
        {	// If that wasn't the first screen, redo pop and update the browser this time
        	parent.pushHistory(strLastCommand, false);
        	strLastCommand = parent.popHistory(1, true);
        }
        if ((strLastCommand == null) || (strLastCommand.length() == 0))
        {
            if (parent.getParentScreen() != null)
                if (!(parent.getParentScreen() instanceof FrameScreen))
                    return parent.onBack();
            // ++Add++ No more to pop, display home screen (if push-down gets messed up)
            parent.pushHistory(this.getScreenURL(), false);    // This is the first screen in the stack
        }
        else
            this.handleCommand(strLastCommand, this, ScreenConstants.USE_SAME_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER);     // Process the last command
        return true;    // Handled
    }
    /**
     * "Clone" this screen.
     * @return true if successful.
     */
    public boolean onClone()
    {
        String strLastCommand = this.getScreenURL();
        if ((strLastCommand == null) || (strLastCommand.length() == 0))
            return false; // Can't process this
        else
            this.handleCommand(strLastCommand, this, ScreenConstants.USE_NEW_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER);        // Process the last command in a new window
        return true;    // Handled
    }
    /**
     * "Clone" this screen.
     * @return true if successful.
     */
    public boolean onNewWindow()
    {
        String strLastCommand = Utility.addURLParam(null, DBParams.MENU, DBConstants.BLANK);  //"?menu="; // Blank command = home
        this.handleCommand(strLastCommand, this, ScreenConstants.USE_NEW_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER);        // Process the last ?menu= command in a new window
        return true;    // Handled
    }
    /**
     * Process the "Home" command.
     * @return true if successful.
     */
    public boolean onHome()
    {
        if (!(this instanceof BaseScreen))
            return false;   // This can only be called from the main screen
        BasePanel screenParent = this.getParentScreen();
        
		int count = 0;
		while (screenParent.popHistory(1, false) != null)
		{	// Pop all the history.
			count++;
		}
		count--;	// Want to move back to the first one
		if (count > 0)
			screenParent.popHistory(count, true);		// Dump all browser history
        String strLastCommand = Utility.addURLParam(null, DBParams.MENU, DBConstants.BLANK);  //"?menu="; // Blank command = home
        this.handleCommand(strLastCommand, this, ScreenConstants.USE_SAME_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER);     // Process the last command in this window
        return true;    // Handled
    }
    /**
     * Process the "Display" toolbar command.
     * @return true if successful.
     */
    public boolean onDisplay()
    {
        BasePanel parent = this.getParentScreen();
        if (parent == null)
            return false;
        return true;    // Handled
    }
    /**
     * Process the "Login" toolbar command.
     * @return true if successful.
     */
    public boolean onLogin()
    {
        return false;    // Handled in overriding class
    }
    /**
     * Display the "Logon" screen.
     * @return true if successful.
     */
    public boolean onLogon()
    {
        return false;    // Handled in overriding class
    }
    /**
     * Process the "Login" toolbar command.
     * @return true if successful.
     */
    public boolean onLogout()
    {
        return false;    // Handled in overriding class
    }
    /**
     * Process the "Change password" toolbar command.
     * @return true if successful.
     */
    public boolean onChangePassword()
    {
        return false;    // Handled in overriding class
    }
    /**
     * Process the "Settings" toolbar command.
     * @return true if successful.
     */
    public boolean onChangeSettings()
    {
        return false;    // Handled in overriding class
    }
    /**
     * Push this command onto the history stack.
     * @param strHistory The history command to push onto the stack.
     */
    public void pushHistory(String strHistory)
    {
        this.pushHistory(strHistory, true);
    }
    /**
     * Push this command onto the history stack.
     * @param strHistory The history command to push onto the stack.
     */
    public void pushHistory(String strHistory, boolean bPushToBrowser)
    {
        if (m_vHistory == null)
            m_vHistory = new Vector<String>();
        m_vHistory.addElement(strHistory);
        String strHelp = Utility.fixDisplayURL(strHistory, true, true, true, this);
        if (this.getAppletScreen() != null)
        	if (this.getAppletScreen().getScreenFieldView() != null)
        		this.getAppletScreen().getScreenFieldView().showDocument(strHelp, MenuConstants.HELP_WINDOW_CHANGE);
        if (bPushToBrowser)
        	if (this.getAppletScreen() != null) 
            	if (this.getAppletScreen().getTask() instanceof BaseApplet)
                	if (((BaseApplet)this.getAppletScreen().getTask()).getBrowserManager() != null)    	// Let browser know about the new screen
        				((BaseApplet)this.getAppletScreen().getTask()).getBrowserManager().pushBrowserHistory(strHistory, this.getTitle());    	// Let browser know about the new screen
    }
    /**
     * Pop a command off the history stack.
     * @return The command at the top of the history stack or null if empty.
     */
    public String popHistory()
    {
        return this.popHistory(1, true);
    }
    /**
     * Pop this command off the history stack.
     * NOTE: Do not use this method in most cases, use the method in BaseApplet.
     * @return The history command on top of the stack.
     */
    public String popHistory(int quanityToPop, boolean bPushToBrowser)
    {
        String strHistory = null;
        for (int i = 0; i < quanityToPop; i++)
        {
        	strHistory = null;
	        if (m_vHistory != null) if (m_vHistory.size() > 0)
	            strHistory = (String)m_vHistory.remove(m_vHistory.size() - 1);
        }
        if (bPushToBrowser)
        	if (this.getAppletScreen() != null) 
            	if (this.getAppletScreen().getTask() instanceof BaseApplet)
                	if (((BaseApplet)this.getAppletScreen().getTask()).getBrowserManager() != null)    	// Let browser know about the new screen
        				((BaseApplet)this.getAppletScreen().getTask()).getBrowserManager().popBrowserHistory(quanityToPop, strHistory != null, this.getTitle());    	// Let browser know about the new screen
        return strHistory;
    }
    /**
     * Add this screen field to the sfield list.
     * @param sField The screen field to add.
     */
    public void addSField(ScreenField sField)
    {   // Add to the screen field list
        m_SFieldList.addElement(sField);
    }
    /**
     * Add this screen field to the sfield list in this location.
     * @param sField The screen field to add.
     * @param iIndex The location to add this screen field.
     */
    public void addSField(ScreenField sField, int iIndex)
    {   // Add to the screen field list
        m_SFieldList.insertElementAt(sField, iIndex);
    }
    /**
     * Remove this screen field from this screen.
     * @param sField The screen field to add.
     * @return true if successful (if found).
     */
    public boolean removeSField(ScreenField sField)
    {   // This call is used when a SField is deleted
        boolean bFlag = false;
        if (m_SFieldList.contains(sField))
        {   // This should only be called for Imaged GridScreens (Child windows would be deleted by now if Component)
            bFlag = m_SFieldList.removeElement(sField);
            if (sField.getScreenFieldView() != null)
                if (sField.getScreenFieldView().getControl() != null) // If it isn't being delete x now, do it
                    sField.free();
        }
        return bFlag;
    }
    /**
     * Number of Screen Fields in this screen.
     * @return screen field count.
     */
    public int getSFieldCount()
    {
        return m_SFieldList.size();
    }
    /**
     * Get the SField at this index.
     * @param index location of the screen field.
     * @return The screen field at this location.
     */
    public ScreenField getSField(int index)
    {       // If this screen cant accept a select BaseTable, find the one that can
        if ((index-DBConstants.MAIN_FIELD >= m_SFieldList.size()) || (index < DBConstants.MAIN_FIELD))
            return null;
        try   {
            return (ScreenField)m_SFieldList.elementAt(index-DBConstants.MAIN_FIELD);
        } catch (ArrayIndexOutOfBoundsException e)  {
        }
        return null;    // Not found
    }
    /**
     * Resurvey the child control(s) and resize frame.
     * @param strTitle The title of the new screen.
     */
    public void resizeToContent(String strTitle)
    {
        this.getScreenFieldView().resizeToContent(strTitle);
    }
    /**
     * This is a special method that runs some code when this screen is opened as a task.
     */
    public void run()
    {
        int iSFieldCount = this.getSFieldCount(); // Keep out of loop because of chance of free() during run()
        for (int iFieldSeq = 0; iFieldSeq < iSFieldCount; iFieldSeq++)
        {   // See if any of my children want to handle this command
            ScreenField sField = this.getSField(iFieldSeq);
            if (sField instanceof BasePanel)    // Run every basepanel
            {
                ((BasePanel)sField).run();
            }
        }
    }
    /**
     * Move the focus to the next logical field.
     * @param sfCurrent The currently selected screen field.
     * @param iSelectField The screen field to select (next/prev/first/last).
     */
    public void selectField(ScreenField sfCurrent, int iSelectField)
    {
        ScreenField sField = this.getComponentAfter(sfCurrent, iSelectField);
        if (sField != null)
            sField.requestFocus();
    }
    /**
     * Returns the Component that should receive the focus after aComponent.
     * @param sfCurrent Currently focused control.
     * @param iSelectField Type of field to select next (first, next, prev, last).
     * @return Control to select next.
     */
    public ScreenField getComponentAfter(ScreenField sfCurrent, int iSelectField)
    {
        ScreenField m_ChildFocused = (ScreenField)sfCurrent;
        if (m_ChildFocused == null)
            iSelectField = DBConstants.SELECT_FIRST_FIELD;
        ScreenField sField = null;
        ScreenField sPrevScreenField = null;
        ScreenField sNextScreenField = null;
        ScreenField sTargetScreenField = null;
        ScreenField sLastFocusTarget = null;
        ScreenField sFirstFocusTarget = null;
// Now survey the control to see which one is next
        for (int iSField = 0; iSField < this.getSFieldCount(); iSField++)
        {   // This should only be called for Imaged GridScreens (Child windows would be deleted by now if Component)
            sField = this.getSField(iSField);
            if (sField.isEnabled()) if (sField.isFocusTarget())
            {
                if (sFirstFocusTarget == null)
                    sFirstFocusTarget = sField;
                sLastFocusTarget = sField;
                if (sTargetScreenField != null) if (sNextScreenField == null)
                    sNextScreenField = sField;
                if (sField == m_ChildFocused)
                    sTargetScreenField = sField;
                if (sTargetScreenField == null)
                    sPrevScreenField = sField;
                if (sPrevScreenField != null) if (sTargetScreenField != null) if (sNextScreenField != null) 
                    break;      // All targets found
            }
            if (sField == m_ChildFocused)
                sTargetScreenField = sField;    // In case focus on a control that doesn't accept focus
        }
        if (sPrevScreenField == null)
            sPrevScreenField = sLastFocusTarget;  // Last field
        if (sNextScreenField == null)
            sNextScreenField = sFirstFocusTarget; // First field
        if (iSelectField == DBConstants.SELECT_PREV_FIELD)
            sNextScreenField = sPrevScreenField;  // Tab backwards
        if (iSelectField == DBConstants.SELECT_FIRST_FIELD)
            sNextScreenField = sFirstFocusTarget; // First field
        sField = sNextScreenField;
        return sField;
    }
    /**
     * Enable or disable all fields. ** Get rid of this method  (replaced by setEnabled) **
     * @param bEditing If true, enable editing for this field.
     */
    public void setEditing(boolean bEditing)
    {
        this.setEnabled(bEditing);
    }
    /**
     * Enable or disable all fields.
     * @param bEditing If true, enable this field.
     */
    public void setEnabled(boolean bEditing)
    {
        super.setEnabled(bEditing);
        for (int iFieldSeq = 0; iFieldSeq < this.getSFieldCount(); iFieldSeq++)
        {
            ScreenField sField = this.getSField(iFieldSeq);
            if (!(sField instanceof ToolScreen))
                sField.setEnabled(bEditing);
        }
    }
    /**
     * Find the sub-screen that uses this grid query and set for selection.
     * When you select a new record here, you read the same record in the SelectQuery.
     * (Override to do something).
     * @param selectTable The record which is synced on record change.
     * @param bUpdateOnSelect Do I update the current record if a selection occurs.
     * @return True if successful.
     */
    public boolean setSelectQuery(Record selectTable, boolean bUpdateOnSelect)
    {       // If this screen can't accept a select BaseTable, find the one that can
        for (Enumeration<ScreenField> e = m_SFieldList.elements() ; e.hasMoreElements() ;)
        {   // This should only be called for Imaged GridScreens (Child windows would be deleted by now if Component)
            ScreenField sField = e.nextElement();
            if (sField.setSelectQuery(selectTable, bUpdateOnSelect))
                return true;
        }
        return false;
    }
    /**
     * Set the default button for this basepanel.
     * @param The button to default to on return.
     */
    public void setDefaultButton(SBaseButton button)
    {
        this.getScreenFieldView().setDefaultButton(button.getScreenFieldView());
    }
    /**
     * Override to add screen fields.
     */
    public void setupSFields()
    {
        // Override this method!
    }
   /**
     * Set the properties.
     * @param strProperties The properties to set.
     */
    public void setProperties(Map<String, Object> properties)
    {
        if (this.getTask() != null)
            this.getTask().setProperties(properties);
    }
    /**
     * Get the properties.
     * @return A <b>copy</b> of the properties in the propertyowner.
     */
    public Map<String, Object> getProperties()
    {
        if (this.getTask() != null)
            return this.getTask().getProperties();
        return null;
    }
    /**
     * Get this property.
     * @param strProperty The property key to find.
     * @param The property.
     */
    public String getProperty(String strProperty)
    {
        if (this.getTask() != null)
            return this.getTask().getProperty(strProperty);   // Try app
        return null;
    }
    /**
     * Set this property.
     * @param strProperty The property key.
     * @param strValue The property value.
     */
    public void setProperty(String strProperty, String strValue)
    {
        if (this.getTask() != null)
            this.getTask().setProperty(strProperty, strValue);
    }
    /**
     * Retrieve/Create a user properties record with this lookup key.
     * @param strPropertyCode The key I'm looking up.
     * @return The UserProperties for this registration key.
     */
    public PropertyOwner retrieveUserProperties(String strRegistrationKey)
    {
        if (this.getTask() != null)
            return this.getTask().retrieveUserProperties(strRegistrationKey);
        return null;    // Not found
    }
    /**
     * Get the key that holds all the user defaults for this screen.
     *  Format:   \Software\tourgeek.com\(This screen name)\(The screen's main file)
     *  ie., \Software\tourgeek.com\GridScreen\AmAffil.
     * @return The property owner.
     */
    public PropertyOwner retrieveUserProperties()
    {
        if (m_registration == null)
        {
            BasePanel parent = this.getParentScreen();
            if (parent == null)
                return null;
            if (!(parent instanceof TopScreen))
                return parent.retrieveUserProperties();     // Register with the highest screen below the AppletScreen
            String strKey = DBConstants.BLANK, strFileName = DBConstants.BLANK;
            String strScreen = this.getClass().getName().toString();
            strKey += strScreen + MenuConstants.REGSEPARATOR; // Screen
            Record record = this.getMainRecord();
            if (record == null)
                record = this.getScreenRecord();
            if (record != null)
                strFileName = record.getTableNames(false);  // File name of the main file
            else
                strFileName = "No Main Query";
            if (strFileName.length() == 0)
                strFileName = "No Query Name";
            strKey += strFileName;  // Main file Key
            m_registration = this.retrieveUserProperties(strKey);
        }
        return m_registration;
    }
    /**
     * Tell report module to print the report detail.
     * <p>Note: This does not maintain an independent variable, I fake that the screen fields
     * have been modified, because this signals the report module to print.
     * @param bPrintReport If true, set print report.
     */
    public void setPrintReport(boolean bPrintReport)
    {
        if (bPrintReport)
            if (this.getScreenRecord() != null)   // This tells the RecordHtmlScreen to display the data
                this.getScreenRecord().getField(DBConstants.MAIN_FIELD).setModified(true);  // Flag screen record, so toolbar knows
    }
    /**
     * Is this report in data entry mode or print report mode?
     * <p>Note: This does not maintain an independent variable, I fake that the screen fields
     * have been modified, because this signals the report module to print.
     * @return True if this is a report.
     */
    public boolean isPrintReport()
    {
        boolean bPrintReport = false;
//        if ((this.getScreenRecord() == null)    // This tells the RecordHtmlScreen to display the data
  //          || (this.getScreenRecord().getField(DBConstants.MAIN_FIELD).isModified()))  // Flag screen record, so toolbar knows
    //            bPrintReport = true;
        if ((HtmlConstants.DISPLAY.equalsIgnoreCase(this.getProperty(HtmlConstants.FORMS)))
            || (HtmlConstants.BOTH.equalsIgnoreCase(this.getProperty(HtmlConstants.FORMS))))
                bPrintReport = true;
        if (this.getProperty(DBParams.XML) != null)
            bPrintReport = true;    // Always a report screen
        return bPrintReport;
    }
    /**
     * Get the path to the target servlet.
     * @param strServletParam The servlet type (html or xml)
     * @return the servlet path.
     */
    public String getServletPath(String strServletParam)
    {
        String strServletName = null;
        if (strServletParam == null)
            strServletParam = Params.SERVLET;
        if (this.getTask() != null)
            strServletName = this.getTask().getProperty(strServletParam);
        if ((strServletName == null) || (strServletName.length() == 0))
        {
            strServletName = Constants.DEFAULT_SERVLET;
//?            if (this.getTask() instanceof RemoteRecordOwner)
//?            	strServletName = strServletName + "xsl";	// Special case - if task is a session, servlet should be tourappxsl
            if (DBParams.XHTMLSERVLET.equalsIgnoreCase(strServletParam))
                strServletName = Constants.DEFAULT_XHTML_SERVLET;
        }
        return strServletName;
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public ViewFactory getViewFactory()
    {
        if (m_viewFactory != null)
            return m_viewFactory;
        m_viewFactory = super.getViewFactory();
        if (m_viewFactory != null)
            return m_viewFactory;
        return m_viewFactory = ViewFactory.getViewFactory(TopScreen.SWING_TYPE, 'V');
    }
    protected ViewFactory m_viewFactory = null;
    /**
     * Just before the view prints out the screen.
     * This is a good time to adjust the variables or screen fields before printing.
     * (Called from the view in the printScreen method).
     */
    public void prePrintReport()
    {
    }
    /**
     * Move the HTML input to the screen record fields.
     * @param strSuffix Only move fields with the suffix.
     * @return true if one was moved.
     * @exception DBException File exception.
     */
    public int setSFieldToProperty(String strSuffix, boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;

        for (int iIndex = 0; iIndex < this.getSFieldCount(); iIndex++)
        {
            ScreenField sField = this.getSField(iIndex);
            int iErrorCode2 = sField.setSFieldToProperty(strSuffix, bDisplayOption, iMoveMode);
            if (iErrorCode2 != DBConstants.NORMAL_RETURN)
                iErrorCode = iErrorCode2;
        }
        return iErrorCode;
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        boolean bFieldsFound = false;
        int iNumCols = this.getSFieldCount();
        for (int iIndex = 0; iIndex < iNumCols; iIndex++)
        {
            ScreenField sField = this.getSField(iIndex);
            boolean bPrintControl = this.isPrintableControl(sField, iPrintOptions);
            if (this.isToolbar())
                if (sField.getConverter() == null)
                    bPrintControl = false;
            if (this instanceof BaseGridScreen)
                if (iIndex < ((BaseGridScreen)this).getNavCount())
                    bPrintControl = true;   // Move to isPrintable.
            if (bPrintControl)
            {
                if (!bFieldsFound)
                    this.printDataStartForm(out, iPrintOptions);    // First time
                this.printDataStartField(out, iPrintOptions);
                this.printScreenFieldData(sField, out, iPrintOptions);
                this.printDataEndField(out, iPrintOptions);
                bFieldsFound = true;
            }
        }
        if (bFieldsFound)
            this.printDataEndForm(out, iPrintOptions);
        return bFieldsFound;
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions)
    {
        boolean bFieldsFound = super.printControl(out, iPrintOptions);
        if (!bFieldsFound)
        {
            int iNumCols = this.getSFieldCount();
            for (int iIndex = 0; iIndex < iNumCols; iIndex++)
            {
                ScreenField sField = this.getSField(iIndex);
                boolean bPrintControl = this.isPrintableControl(sField, iPrintOptions);
                if (this.isToolbar())
                    if (sField.getConverter() == null)
                        bPrintControl = false;
                if (this instanceof BaseGridScreen)
                    if (iIndex < ((BaseGridScreen)this).getNavCount())
                        bPrintControl = true;   // Move to isPrintable.
                if (bPrintControl)
                {
                    if (!bFieldsFound)
                        this.printControlStartForm(out, iPrintOptions);    // First time
                    this.printControlStartField(out, iPrintOptions);
                    sField.printControl(out, iPrintOptions);
                    this.printControlEndField(out, iPrintOptions);
                    bFieldsFound = true;
                }
            }
            if (bFieldsFound)
                this.printControlEndForm(out, iPrintOptions);
        }
        return bFieldsFound;
    }
    /**
     * Display this sub-control in html input format?
     * @param iPrintOptions The view specific print options.
     * @return True if this sub-control is printable.
     */
    public boolean isPrintableControl(ScreenField sField, int iPrintOptions)
    {
        // Override this to break
        if ((sField == null) || (sField == this))
        {       // Asking about this control
            return false;    // Base screens are not printed as a sub-screen.
        }
        return super.isPrintableControl(sField, iPrintOptions);
    }
    /**
     * Clear the status text message.
     */
    public void clearStatusText()
    {
        if (this.getTask() != null)
            this.getTask().setStatusText(DBConstants.BLANK);        
    }
    /**
     * Display this screen's hidden params.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public Map<String, Object> getHiddenParams()
    {
        Map<String, Object> mapParams = new Hashtable<String, Object>();
        return mapParams;
    }
    /**
     * Add this record owner to my list.
     * @param recordOwner The record owner to add
     */
    public boolean addRecordOwner(RecordOwnerParent recordOwner)
    {
    	if (recordOwner instanceof BasePanel)
    		return false;		// Panels have their own owner hierarchy - Don't need to manage them
    	if (m_recordOwnerCollection == null)
    		m_recordOwnerCollection = new RecordOwnerCollection(this);
    	return m_recordOwnerCollection.addRecordOwner(recordOwner);
    }
    /**
     * Remove this record owner to my list.
     * @param recordOwner The record owner to remove.
     */
    public boolean removeRecordOwner(RecordOwnerParent recordOwner)
    {
    	if (m_recordOwnerCollection != null)
    		return m_recordOwnerCollection.removeRecordOwner(recordOwner);
    	return false;
    }
}
