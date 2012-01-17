/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)HtmlScreen.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.StringField;
import org.jbundle.base.screen.model.html.AppletHtmlScreen;
import org.jbundle.base.screen.model.html.DataAccessScreen;
import org.jbundle.base.screen.model.report.BaseParserScreen;
import org.jbundle.base.screen.model.report.HelpScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.MainApplication;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.main.user.db.UserInfoModel;
import org.jbundle.model.util.Constant;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.util.Application;


/**
 * TopScreen - The top screen directly below the task.
 * <p/>Contains the main screen, toolbars, status bar(s), etc.
 */
public class TopScreen extends BasePanel
{
    /*
     * The task that this screen belongs to.
     * For an AppletScreen, this is a special flag used when this program is started as an applet.
     * Because the applet object already exists (and the default constructor called)
     * This tells the View that I won't be needing a control, just use this one (just
     * don't forget to call the init(xxx) method to finish setting it up.
     * <p />NOTE: This object is guaranteed to be an SApplet, but I can't access
     * the view package from here, so remember to cast it.
     */
    protected RecordOwnerParent m_recordOwnerParent = null;   // My task/owner
    /**
     * Base for reports.
     */
    public static final String SERVLET_BASE = "base.screen.model.report.";

    public static final String HTML_TYPE = "html";
    public static final String XML_TYPE = "xml";
    public static final String SWING_TYPE = "swing";
    public static final String DOJO_TYPE = "dojo";
    
    /**
     * Constructor.
     */
    public TopScreen()
    {
        super();
        m_recordOwnerParent = null;  // Init here instead of in init, because a method may want to set it between.
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public TopScreen(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize the RecordOwner.
     * This initializer is required by the RecordOwner interface.
     * @param record The main record for this screen.
     * @param parent The parent screen (task).
     * @param properties The properties object.
     */
    public void init(RecordOwnerParent parent, FieldList recordMain, Object properties)
    {
        if (!(parent instanceof BasePanel))
        {   // The parent is an SApplet (but the view packages are not accessible from here)
            m_recordOwnerParent = parent;
            if (m_recordOwnerParent != null)
            	m_recordOwnerParent.addRecordOwner(this);
            
            parent = null;
        }
        this.init(null, (BasePanel)parent, null, ScreenConstants.DEFAULT_DISPLAY);
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
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Free the resources.
     */
    public void free()
    {
        super.free();
        if (m_recordOwnerParent != null)
        	m_recordOwnerParent.removeRecordOwner(this);
        m_recordOwnerParent = null;
    }
    /**
     * Add the toolbars.
     * @return null as a top screen shouldn't add a toolbar.
     */
    public ToolScreen addToolbars()
    {   // Obviously, an applet shouldn't add a toolbar!
        return null;
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        if (m_recordOwnerParent == null)
            return null;
        return m_recordOwnerParent.getTask();
    }
    /**
     * Special call due to static applet creation when stand-alone.
     * @param task The task to set.
     */
    public void setTask(Task task)
    {
        if ((m_recordOwnerParent == null) || (m_recordOwnerParent instanceof Task))
            m_recordOwnerParent = task;
    }
    /**
     * Don't set up any screen fields (initially) for an applet.
     */   
    public void setupSFields()
    {
        // Don't setup any screen fields (as the default!)
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        return DBConstants.NORMAL_RETURN;   // This is never a main screen
    }
    /**
     * From the parameters, get the screen.
     */
    public BaseScreen getScreen(BaseScreen screen, PropertyOwner propertyOwner)
    {
        if (propertyOwner == null)
            propertyOwner = this;
        // fetch the parameters
        String strAppletScreen = propertyOwner.getProperty(DBParams.APPLET);   // Applet page
        String strMenu = propertyOwner.getProperty(DBParams.MENU); // Menu page
        String strRecord = propertyOwner.getProperty(DBParams.RECORD); // Display record
        if (strRecord == null)
            strRecord = Constants.BLANK;
        String strScreen = propertyOwner.getProperty(DBParams.SCREEN); // Display screen
        if (strScreen == null)
            strScreen = Constants.BLANK;
        String strPreference = propertyOwner.getProperty(DBParams.PREFERENCES);  // Changing parameters
        String strHelp = propertyOwner.getProperty(DBParams.HELP);       // Menu page
        String strXml = propertyOwner.getProperty(DBParams.XML);             // Html page
        String strImage = propertyOwner.getProperty(DBParams.DATATYPE);              // Image from DB

        if (strPreference != null)
            this.changeParameters();
        if (strAppletScreen != null)
            if (strHelp == null)
        {
            String strJavaLaunch = propertyOwner.getProperty(DBParams.JAVA);
            String strBrowser = this.getProperty(DBParams.BROWSER);
            String strOS = this.getProperty(DBParams.OS);
            char chJavaLaunch = this.getDefaultJava(strBrowser, strOS);    // Web start = default
            if ((strJavaLaunch != null) && (strJavaLaunch.length() > 0))
                chJavaLaunch = Character.toUpperCase(strJavaLaunch.charAt(0));
            if (chJavaLaunch == 'W')
            {
                this.setProperty(DBParams.DATATYPE, "Jnlp");
                strScreen = DataAccessScreen.class.getName();
            }
            else
                strScreen = AppletHtmlScreen.class.getName();    // Applet HTML form
        }
        if (strMenu != null) if (strScreen.length() == 0)
            strScreen = MenuScreen.class.getName();
        if (strXml != null)
            strScreen = BaseParserScreen.class.getName();
        if (strHelp != null)
            if ((strMenu == null) && (strXml == null))
                if (!strHelp.equalsIgnoreCase("add"))
        {
            if (strHelp.length() == 0)
                strHelp = HelpScreen.class.getName();
            if (strHelp.indexOf('.') == -1)
                strHelp = "." + SERVLET_BASE + strHelp;
            if (strHelp.indexOf('.') == 0)
                strHelp = DBConstants.ROOT_PACKAGE + strHelp.substring(1);
            strScreen = strHelp;
        }
        if (strImage != null)
            strScreen = DataAccessScreen.class.getName();

        boolean bUseOldScreen = false;
        if (screen != null)
            if ((strScreen != null) && (strScreen.length() > 0))
        {
            if (strScreen.indexOf('.') == 0)
                strScreen = Constants.ROOT_PACKAGE + strScreen.substring(1);
            if (strScreen.equals(screen.getClass().getName()))
                bUseOldScreen = true;
        }
        if (screen != null)
        {
            if ((strRecord != null) && (strRecord.length() > 0))
                if (screen.getMainRecord() != null)
                    if ((strRecord.equalsIgnoreCase(screen.getMainRecord().getRecordName()))
                        || (strRecord.equalsIgnoreCase(screen.getMainRecord().getClass().getName())))
            {
                int iOldMode = ScreenConstants.MAINT_MODE;
                if (screen instanceof BaseGridScreen)
                    iOldMode = ScreenConstants.DISPLAY_MODE;
                int HELP_MODE = ScreenConstants.LAST_MODE + 1;
                if (screen instanceof HelpScreen)
                    iOldMode = HELP_MODE;
                int iNewMode = ScreenConstants.MAINT_MODE;
                if ((propertyOwner.getProperty(DBParams.COMMAND) == null)
                    || ((MenuConstants.LOOKUP.equalsIgnoreCase(this.getProperty(DBParams.COMMAND)))
                        || (MenuConstants.GRID.equalsIgnoreCase(this.getProperty(DBParams.COMMAND)))
                        || (HtmlConstants.DISPLAY.equalsIgnoreCase(this.getProperty(HtmlConstants.FORMS)))))
                            iNewMode = ScreenConstants.DISPLAY_MODE;
                if (strHelp != null)
                    iNewMode = HELP_MODE;
                if (iNewMode == iOldMode)
                    bUseOldScreen = true;
            }
        }
        if (screen != null)
	        if ((screen.getDisplayFieldDesc() & ScreenConstants.SECURITY_MODE) != 0)
    	        bUseOldScreen = false;
        if (bUseOldScreen)
            screen.clearCachedData();
        if (screen != null)
        {
        	boolean bIsApplet = false;
        	if ((strAppletScreen != null) && (strHelp == null))
        		bIsApplet = true;
            if (this.getProperty(DBParams.MENU) != null)
                if (!bIsApplet)
                    if (MenuScreen.class.getName().equals(screen.getClass().getName()))
            {
                bUseOldScreen = true;
                screen.setProperty(DBParams.MENU, this.getProperty(DBParams.MENU));
            }
        }
        if (!bUseOldScreen)
            if (screen != null)
        {
            Map<String,Object> properties = null;
            if (propertyOwner != null)
                properties = propertyOwner.getProperties();    // This will get cleared when I free the screen.
            screen.free();
            screen = null;  // Don't use old screen.
            if (propertyOwner != null)
                propertyOwner.setProperties(properties);
        }

                // First, see if they want to see a screen
        BaseApplet applet = null;
        if (this.getTask() instanceof BaseApplet)
        	applet = (BaseApplet)this.getTask();
        Object oldCursor = null;
        if (applet != null)
        	oldCursor = applet.setStatus(Constant.WAIT, applet, null);
        if (screen == null)
            if (strScreen.length() > 0)
                screen = BaseScreen.makeNewScreen(strScreen, null, this, 0, null, null, true);
                // Now, see if they want to open a file and create the default screen
        if (screen == null) if (strRecord.length() > 0)
        {
            String strScreenType = propertyOwner.getProperty(DBParams.COMMAND);
            int iDocType = ScreenConstants.DISPLAY_MODE;
            if (this.getProperty(DBParams.COMMAND) != null)
                iDocType = ScreenConstants.MAINT_MODE;
            RecordOwner recordOwner = Utility.getRecordOwner(this);
            Record record = Record.makeRecordFromClassName(strRecord, recordOwner);
            if (record != null)
                if (recordOwner != null)
                    recordOwner.removeRecord(record);   // Will be added to screen
            if (strScreenType != null)
            {
                iDocType = 0;
                boolean bNumeric = true;
                for (int i = 0; i < strScreenType.length(); i++)
                {
                    if (!Character.isDigit(strScreenType.charAt(i)))
                        bNumeric = false;
                }
                if (bNumeric)
                {
                    try   {
                        iDocType = Integer.parseInt(strScreenType);
                    } catch (NumberFormatException ex)  {
                        iDocType = 0;
                    }
                }
                if (iDocType == 0)
                    if (record != null)
                        iDocType = record.commandToDocType(strScreenType);
            }
            if (record != null)
                screen = (BaseScreen)record.makeScreen(null, this, iDocType, null);
        }
        if (screen == null)
        {   // Default Display Form
            strScreen = MenuScreen.class.getName();
            screen = BaseScreen.makeNewScreen(strScreen, null, this, 0, null, null, true);
        }
        if (applet != null)
            applet.setStatus(0, applet, oldCursor);
        return screen;
    }
    /**
     * Get best way to launch java
     * @param strBrowser
     * @param strOS
     * @return
     */
    public char getDefaultJava(String strBrowser, String strOS)
    {
        char chJavaLaunch = 'A';    // Applet = default
        if ("safari".equalsIgnoreCase(strBrowser))
            chJavaLaunch = 'W';    // Web start
//        if ("linux".equalsIgnoreCase(strOS))
//            chJavaLaunch = 'W';    // Web start
        return chJavaLaunch;
    }
    /**
     * 
     * @param strClassResource
     * @return
     */
    public BaseScreen checkSecurity(BaseScreen screen, BasePanel parentScreen)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (screen != null)
            iErrorCode = screen.checkSecurity();
        if (iErrorCode == Constants.READ_ACCESS)
        {
            screen.setEditing(false);
            screen.setAppending(false);
            iErrorCode = DBConstants.NORMAL_RETURN;
        }
        if (iErrorCode == DBConstants.NORMAL_RETURN)
            return screen;    // Good, access allowed
        else
        {
            if (screen != null)
                screen.free();
            return this.getSecurityScreen(iErrorCode, parentScreen);  // Create and return the login or error screen
        }
    }
    /**
     * Display the correct security warning (access denied or the login screen).
     * @param iErrorCode
     */
    public BaseScreen getSecurityScreen(int iErrorCode, BasePanel parentScreen)
    {
        BaseScreen screen = null;
        if (iErrorCode == DBConstants.ACCESS_DENIED)
        {
            screen = new BaseScreen(null, null, parentScreen, null, 0, null);
            String strDisplay = this.getTask().getApplication().getSecurityErrorText(iErrorCode);
            BaseApplication application = (BaseApplication)this.getTask().getApplication();
            String strMessage = application.getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strDisplay);
            BaseField fldFake = new StringField(null, DBConstants.BLANK, 128, DBConstants.BLANK, null);
            fldFake.setString(strMessage);
            new SStaticText(screen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), screen, fldFake, ScreenConstants.DEFAULT_DISPLAY);
        }
        else if ((iErrorCode == DBConstants.LOGIN_REQUIRED) || (iErrorCode == DBConstants.AUTHENTICATION_REQUIRED))
        {
            Record record = Record.makeRecordFromClassName(UserInfoModel.THICK_CLASS, null);
            ScreenLocation itsLocation = this.getScreenLocation();
            int docMode = record.commandToDocType(UserInfoModel.LOGIN_SCREEN);
            Map<String,Object> properties = null;
            screen = (BaseScreen)record.makeScreen(itsLocation, parentScreen, docMode, properties);
        }
        else if (iErrorCode == DBConstants.CREATE_USER_REQUIRED)
        {
            Record record = Record.makeRecordFromClassName(UserInfoModel.THICK_CLASS, null);
            ScreenLocation itsLocation = this.getScreenLocation();
            int docMode = record.commandToDocType(UserInfoModel.ENTRY_SCREEN);
            Map<String,Object> properties = null;
            screen = (BaseScreen)record.makeScreen(itsLocation, parentScreen, docMode, properties);
        }
        return screen;
    }
    /**
     * Change the session parameters.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void changeParameters()
    {
        //Get the session object
        String string = null;
        String strPreferences = this.getProperty(DBParams.PREFERENCES);   // Changing parameters
        boolean bSetDefault = true;
        if ((strPreferences == null) || (strPreferences.length() == 0))
            bSetDefault = false;    // Set using a URL (vs set using a form)

        Application application = (BaseApplication)this.getTask().getApplication();

        string = this.getProperty(DBParams.FRAMES);
        if (bSetDefault)
            if (string == null)
                string = DBConstants.NO;
        if (string != null)
            application.setProperty(DBParams.FRAMES, string);

        string = this.getProperty(DBParams.MENUBARS);
        if (bSetDefault)
            if (string == null)
                string = UserInfoModel.YES;
        if (string != null)
            application.setProperty(DBParams.MENUBARS, string);

        string = this.getProperty(DBParams.NAVMENUS);
        if (bSetDefault)
            if (string == null)
                string = UserInfoModel.NO_ICONS;
        if (string != null)
            application.setProperty(DBParams.NAVMENUS, string);

        string = this.getProperty(DBParams.JAVA);
        if (bSetDefault)
            if (string == null)
                string = UserInfoModel.DEFAULT;        // Changing parameters
        if (string != null)
            application.setProperty(DBParams.JAVA, string);

        string = this.getProperty(DBParams.BANNERS);
        if (bSetDefault)
            if (string == null)
                string = DBConstants.NO;
        if (string != null)
            application.setProperty(DBParams.BANNERS, string);

        string = this.getProperty(DBParams.LOGOS);
        if (bSetDefault)
            if (string == null)
                string = UserInfoModel.HOME_PAGE_ONLY;
        if (string != null)
            application.setProperty(DBParams.LOGOS, string);

        string = this.getProperty(DBParams.TRAILERS);
        if (bSetDefault)
            if (string == null)
                string = UserInfoModel.HOME_PAGE_ONLY;
        if (string != null)
            application.setProperty(DBParams.TRAILERS, string);

        string = this.getProperty(DBConstants.SUB_SYSTEM_LN_SUFFIX);
        if (string != null)
            application.setProperty(DBConstants.SUB_SYSTEM_LN_SUFFIX, string);

        string = this.getProperty(DBParams.LANGUAGE);
        if (bSetDefault)
            if (string == null)
                string = UserInfoModel.DEFAULT; /** en (english)*/;
        if (string != null)
            application.setLanguage(string);
        
        if (application instanceof MainApplication)
        {   // Always
            ((MainApplication)application).readUserInfo(true, true);   // Flush the new properties
        }
    }
}
