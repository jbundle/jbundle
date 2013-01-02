/**
 * @(#)BaseRegistrationScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.demo;

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
import org.jbundle.main.user.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.main.user.db.*;
import org.jbundle.thin.base.db.buff.*;
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.thread.*;
import java.net.*;
import java.io.*;
import org.jbundle.app.program.script.scan.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.app.program.demo.message.*;
import java.text.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.base.message.remote.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.model.message.*;

/**
 *  BaseRegistrationScreen - .
 */
public class BaseRegistrationScreen extends UserEntryScreen
{
    public static final String SITE_NAME_TEMPLATE_PARAM = "siteNameTemplate";
    public static final String SITE_TEMPLATE_CODE_PARAM = "siteTemplate";
    public static final String MAIN_USER_PARAM = "mainUser";
    public static final String DEFAULT_DOMAIN = "tourgeek.com";
    public static final String SITE_TEMPLATE_CODE = "template." + DEFAULT_DOMAIN;
    public static final String DEFAULT_ARCHIVE_FOLDER = "org/jbundle/res/data/test_data";
    public static final String CURRENT_TEST_DATA_DIR = "current_test_data";
    public static final String HOME_MENU_CODE = "demostart";
    public static final String DEFAULT_SITE_HOME_CODE = "demoSite";
    public static final String ADMIN_HOME_MENU_CODE = "adminHomeMenu";
    public static final String DEFAULT_ADMIN_HOME_MENU = "app";
    protected boolean waiting = false;
    protected String strMessage = null;
    protected Integer m_intRegistryID = null;
    /**
     * Default constructor.
     */
    public BaseRegistrationScreen()
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
    public BaseRegistrationScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new Menus(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.getMainRecord().addListener(new FileListener(null)
        {
            public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
            {   // Return an error to stop the change
                if (iChangeType == DBConstants.AFTER_ADD_TYPE)
                {
                    strMessage = DBConstants.BLANK;
                    afterAdd();
                    if ((strMessage != null) && (strMessage.toUpperCase().startsWith("ERROR")))
                        return getTask().setLastError(strMessage);
                }
                return super.doRecordChange(field, iChangeType, bDisplayOption);
            }
        });
        
        MessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
        if (messageManager != null)
        {
            Object source = this;
            BaseMessageFilter filter = new BaseMessageFilter(MessageConstants.TRX_RETURN_QUEUE, MessageConstants.INTERNET_QUEUE, source, null);
            filter.addMessageListener(this);
            messageManager.addMessageFilter(filter);
            m_intRegistryID = filter.getRegistryID();
        }
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
        boolean bSuccess = super.doCommand(strCommand, sourceSField, iCommandOptions);
        if (MenuConstants.SUBMIT.equalsIgnoreCase(strCommand))
            if (bSuccess)
        {
        //this.setupUserInfo();   // Success, set up their databases
        }
        return bSuccess;
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
        //  if (screen == null)
        //      this.setupUserInfo();   // Success, set up their databases
        }
        
        return screen;    // By default, don't do anything
    }
    /**
     * Set up the new site after the user information is added.
     */
    public void afterAdd()
    {
        this.setupUserInfo();
    }
    /**
     * SetupUserInfo Method.
     */
    public void setupUserInfo()
    {
        MessageProcessInfo recMessageProcessInfo = null;
        try {
            Record recUser = this.getMainRecord();
            Object bookmark = recUser.getLastModified(DBConstants.BOOKMARK_HANDLE);
            recUser = recUser.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
            if ((recUser == null) ||
                (recUser.getEditMode() == DBConstants.EDIT_NONE) || (recUser.getEditMode() == DBConstants.EDIT_ADD))
                    return;     // Never
        
            Map<String, Object> properties = this.getSiteProperties();
            RunRemoteProcessMessageData createSiteRequest = this.setupUserProperties(properties);
        
            recMessageProcessInfo = new MessageProcessInfo(this);
            recMessageProcessInfo.setKeyArea(MessageProcessInfo.CODE_KEY);
            recMessageProcessInfo.getField(MessageProcessInfo.CODE).setString("RunRemoteProcessRQ");
            if (recMessageProcessInfo.seek(null))
            {
                TrxMessageHeader trxMessageHeader = recMessageProcessInfo.createProcessMessageHeader(null, MessageTransport.XML);
                if (trxMessageHeader == null)
                    return;
                trxMessageHeader.put(TrxMessageHeader.DESTINATION_PARAM, properties.get(TrxMessageHeader.DESTINATION_PARAM));
                trxMessageHeader.put(MessageTransport.MANUAL_RESPONSE_PARAM, DBConstants.TRUE);    // For testing, allow manual requests
                if (m_intRegistryID != null)
                    trxMessageHeader.put(TrxMessageHeader.REGISTRY_ID, m_intRegistryID);    // The return Queue ID
                BaseMessage message = createSiteRequest.getMessage();
                message.setMessageHeader(trxMessageHeader);
                if (!MessageTransport.DIRECT.equalsIgnoreCase((String)trxMessageHeader.get(MessageTransport.SEND_MESSAGE_BY_PARAM)))
                {
                    MessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
                    if (messageManager != null)
                        messageManager.sendMessage(message);
                }
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            recMessageProcessInfo.free();
        }
            
        synchronized (this)
        {
           try {
               waiting = true;
               this.wait(40000);
           } catch (InterruptedException ex) {
               ex.printStackTrace();
           }
           if (waiting)
               strMessage = "Error: Timeout on message call";
        }
    }
    /**
     * GetSiteProperties Method.
     */
    public Map<String, Object> getSiteProperties()
    {
        Map<String, Object> properties = new HashMap<String, Object>();
        
        Record recUser = this.getMainRecord();
        
        String sitePrefix = ((PropertiesField)recUser.getField(UserInfo.PROPERTIES)).getProperty(MenusMessageData.SITE_PREFIX);
        if (sitePrefix == null)
        {
            sitePrefix = recUser.getField(UserInfo.USER_NAME).toString();
            if (sitePrefix.indexOf('@') != -1)
                sitePrefix = sitePrefix.substring(0, sitePrefix.indexOf('@'));
            sitePrefix = sitePrefix.toLowerCase();
            sitePrefix = sitePrefix.replace('.', '-');
        }
        properties.put(MenusMessageData.SITE_PREFIX, sitePrefix);
        
        String siteTemplate = this.getProperty(SITE_TEMPLATE_CODE_PARAM);
        if (siteTemplate == null)
            siteTemplate = SITE_TEMPLATE_CODE;
        properties.put(SITE_TEMPLATE_CODE_PARAM, siteTemplate);
        
        String homeCode = this.getProperty(MenusMessageData.SITE_HOME_MENU);
        if (homeCode == null)
            homeCode = HOME_MENU_CODE;
        properties.put(MenusMessageData.SITE_HOME_MENU, homeCode);
        
        String siteHomeCode = this.getProperty(MenusMessageData.SITE_TEMPLATE_MENU);
        if (siteHomeCode == null)
            siteHomeCode = DEFAULT_SITE_HOME_CODE;
        properties.put(MenusMessageData.SITE_TEMPLATE_MENU, siteHomeCode);
        
        String processClassName = this.getProperty(RunRemoteProcessMessageData.PROCESS_CLASS_NAME);
        if (processClassName == null)
            processClassName = BaseSetupSiteProcess.class.getName();
        properties.put(RunRemoteProcessMessageData.PROCESS_CLASS_NAME, processClassName);
        
        String destination = this.getProperty(TrxMessageHeader.DESTINATION_PARAM);
        if (destination == null)
            destination = "http://www.tourgeek.com:8181/xmlws";
        properties.put(TrxMessageHeader.DESTINATION_PARAM, destination);        
        
        String userHomeMenu = this.getProperty(ADMIN_HOME_MENU_CODE);
        if (userHomeMenu == null)
            userHomeMenu = DEFAULT_ADMIN_HOME_MENU;
        properties.put(ADMIN_HOME_MENU_CODE, userHomeMenu);
        
        return properties;
    }
    /**
     * SetupUserProperties Method.
     */
    public RunRemoteProcessMessageData setupUserProperties(Map<String, Object> properties)
    {
        try {
            Record recUser = this.getMainRecord();
            
            String processClassName = (String)properties.get(RunRemoteProcessMessageData.PROCESS_CLASS_NAME);
            
            // Step 1 - Get the web site prefix
            String sitePrefix = (String)properties.get(MenusMessageData.SITE_PREFIX);
            String fullSitePrefix = sitePrefix;
            String strDomain = (String)properties.get(DBParams.DOMAIN);
            if (strDomain == null)
            {
                strDomain = this.getProperty(DBParams.DOMAIN);
                if ((strDomain == null) || (strDomain.length() == 0))
                    strDomain = DEFAULT_DOMAIN;
                if (strDomain.indexOf('.') != strDomain.lastIndexOf('.'))
                    strDomain = strDomain.substring(strDomain.indexOf('.') + 1);
            }
            String homeMenu = (String)properties.get(MenusMessageData.SITE_HOME_MENU);
        
            // Next, Create a main site menu for this new domain.
            Record recMenus = this.getRecord(Menus.MENUS_FILE);
            recMenus.addNew();
            String siteTemplate = (String)properties.get(SITE_TEMPLATE_CODE_PARAM);
            recMenus.getField(Menus.CODE).setString(siteTemplate);
            int iOldOrder = recMenus.getDefaultOrder();
            recMenus.setKeyArea(Menus.CODE_KEY);
            BaseBuffer buffer = null;
            if (recMenus.seek(null))
            {
                buffer = new VectorBuffer(null);
                buffer.fieldsToBuffer(recMenus);
            }
            else
                return null;        // Error - Site template does not exist
            recMenus.setKeyArea(iOldOrder);
            
            recMenus.addNew();
            if (buffer != null)     // Always
                buffer.bufferToFields(recMenus, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
            
            recMenus.getField(Menus.SEQUENCE).setValue(100);
            String siteNameTemplate = (String)properties.get(SITE_NAME_TEMPLATE_PARAM);
            if (siteNameTemplate == null)
                siteNameTemplate = "{0}''s Demo site";
            String siteName = MessageFormat.format(siteNameTemplate, sitePrefix.toUpperCase().substring(0,1) + sitePrefix.substring(1));
            recMenus.getField(Menus.NAME).setString(siteName);
            ((PropertiesField)recMenus.getField(Menus.PARAMS)).setProperty(MAIN_USER_PARAM, recUser.getField(UserInfo.USER_NAME).toString());
            String templateArchivePath = ((PropertiesField)recMenus.getField(Menus.PARAMS)).getProperty(DBConstants.USER_ARCHIVE_FOLDER);
            if ((templateArchivePath == null) || (templateArchivePath.length() == 0))
                templateArchivePath = Utility.addToPath(DEFAULT_ARCHIVE_FOLDER, CURRENT_TEST_DATA_DIR);
            
            int iErrorCode = DBConstants.DUPLICATE_KEY;
            int iCounter = 0;
            while (iErrorCode == DBConstants.DUPLICATE_KEY)
            {
                try {
                    fullSitePrefix = sitePrefix;
                    if (iCounter > 0)
                        fullSitePrefix += Integer.toString(iCounter);
        
                    recMenus.getField(Menus.CODE).setString(fullSitePrefix + '.' + strDomain);
        
                    recMenus.add();
                    iErrorCode = DBConstants.NORMAL_RETURN;
                } catch (DBException ex) {
                    iErrorCode = ex.getErrorCode();
                    if (iErrorCode != DBConstants.DUPLICATE_KEY)
                    {
                        ex.printStackTrace();
                        return null;     // Error
                    }
                    iCounter++;
                    if (iCounter >= 100)
                        break;
                }
            }
            // Now we have this user's domain: sitePrefix
            
            // Update the user's account to show they own this new domain
            if (iErrorCode == DBConstants.NORMAL_RETURN)
            {
                iOldOrder = recUser.getDefaultOrder();
                recUser.setKeyArea(UserInfo.ID_KEY);
                recUser.edit();
                ((PropertiesField)recUser.getField(UserInfo.PROPERTIES)).setProperty(MenusMessageData.DOMAIN_NAME, recMenus.getField(Menus.CODE).toString());
                ((PropertiesField)recUser.getField(UserInfo.PROPERTIES)).setProperty(DBParams.HOME, homeMenu);
                recUser.set();
                recUser.setKeyArea(iOldOrder);
            }
            
            TreeMessage message = new TreeMessage(null, null);
            RunRemoteProcessMessageData runRemoteProcessRequest = new RunRemoteProcessMessageData(message, null);
        
            CreateSiteMessageData createSiteRequest = new CreateSiteMessageData(runRemoteProcessRequest, null);
            runRemoteProcessRequest.addMessageDataDesc(createSiteRequest);
            MessageRecordDesc userInfoMessageData = (MessageRecordDesc)createSiteRequest.getMessageDataDesc(UserInfo.USER_INFO_FILE);
            MessageRecordDesc menusMessageData = (MessageRecordDesc)createSiteRequest.getMessageDataDesc(Menus.MENUS_FILE);
            
            String user = this.getProperty(DBParams.USER_NAME);
            String auth = this.getProperty(DBParams.AUTH_TOKEN);
            runRemoteProcessRequest.put(RunRemoteProcessMessageData.PROCESS_CLASS_NAME, processClassName);
            runRemoteProcessRequest.put(DBParams.USER_NAME, user);
            runRemoteProcessRequest.put(DBParams.AUTH_TOKEN, auth);
            
            userInfoMessageData.putRawRecordData(recUser);  // User, password
        
            menusMessageData.put(MenusMessageData.DOMAIN_NAME, fullSitePrefix + '.' + strDomain);
            menusMessageData.put(MenusMessageData.SITE_PREFIX, fullSitePrefix);
            menusMessageData.put(MenusMessageData.SITE_NAME, siteName);
            menusMessageData.put(MenusMessageData.XSL_TEMPLATE_PATH, templateArchivePath);
            menusMessageData.put(MenusMessageData.SITE_TEMPLATE_MENU, siteTemplate);
            menusMessageData.put(MenusMessageData.SITE_HOME_MENU, homeMenu);
        
            return runRemoteProcessRequest;
            
        } catch (DBException ex) {
            return null;
        }
    }
    /**
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: Be very careful as this code is running in an independent thread
     * (synchronize to the task before calling record calls).
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(BaseMessage message)
    {
        if (message != null)
            if (message.getMessageHeader().getRegistryIDMatch() != null)    // My private message
        {
            strMessage = (String)message.get(StandardMessageResponseData.MESSAGE);
            synchronized (this)
            {
                waiting = false;
                this.notify();
            }
        }
        return super.handleMessage(message);
    }
    /**
     * DisplayError Method.
     */
    public void displayError(DBException ex)
    {
        if ((ex instanceof DatabaseException) && (ex.getErrorCode() == Constants.DUPLICATE_KEY))
            this.displayError("Account already exists, sign-in using this user name", DBConstants.WARNING_MESSAGE);
        else
            super.displayError(ex);
    }

}
