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
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordList;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.screen.model.util.MaintToolbar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.DatabaseCollection;
import org.jbundle.base.util.DatabaseOwner;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.main.msg.db.base.ContactType;
import org.jbundle.main.user.screen.UserLoginScreen;
import org.jbundle.main.user.screen.UserPasswordChange;
import org.jbundle.main.user.screen.UserPreferenceScreen;
import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Constant;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.MessageListenerFilterList;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.util.ThinMenuConstants;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * This is the base for any parent screen.
 */
public class BaseScreen extends BasePanel
    implements RecordOwner
{

    /**
     * Contains all the open records for this screen.
     */
    protected RecordList m_vRecordList = null;  // Files in use for this window
    /*
     * This screen is dependent on this file (if that file closes, this screen is destroyed)
     */
    protected Record m_recDependent = null;
    /*
     * Is Appending allowed?
     */
    protected boolean m_bAppending = true;
    /**
     * The list of databases. (Only used for transaction support).
     */
    private DatabaseCollection m_databaseCollection = null;   // List of database lists.
    /**
     * Keep track of any filters that have me as a listener.
     */
    protected MessageListenerFilterList m_messageFilterList = null;

    /**
     * Constructor.
     */
    public BaseScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     */
    public BaseScreen(Record record)
    {
        this();
        ScreenLocation itsLocation = null;
        BasePanel parentScreen = null;
        Converter fieldConverter = null;
        int iDisplayFieldDesc = ScreenConstants.DONT_DISPLAY_DESC;
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null);
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public BaseScreen(Record mainRecord, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(mainRecord, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * This is the new constructor list.
     * This initializer is required by the RecordOwner interface.
     * @param record The main record for this screen.
     * @param parent The parent screen.
     * @param properties The properties object.
     * @param location (property) The location of this component within the parent.
     * @param display (property) Do I display the field desc?
     */
    public void init(RecordOwnerParent parent, FieldList record, Object properties)
    {
        ScreenLocation itsLocation = null;
        Converter fieldConverter = null;
        int iDisplayFieldDesc = ScreenConstants.DEFAULT_DISPLAY;
        if (properties instanceof Properties)
        {
            String strItsLocation = ((Properties)properties).getProperty("location");
            String strDisplayFieldDesc =  ((Properties)properties).getProperty("display");
            try   {
                iDisplayFieldDesc = Integer.parseInt(strDisplayFieldDesc);
                if (strItsLocation != null)
                    itsLocation = new ScreenLocation(Short.parseShort(strItsLocation), ScreenConstants.ANCHOR_DEFAULT);
            } catch (Exception ex) {
            }
        }
        else
            properties = null;
        this.init((Record)record, itsLocation, (BasePanel)parent, fieldConverter, iDisplayFieldDesc, (Map)properties);
    }
    /**
     * Open the files and setup the screen.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record mainRecord, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        m_vRecordList = new RecordList(null);
        m_recDependent = null;
        m_screenParent = parentScreen;  // I know this is done later in ScreenField.init, but parentScreen may be needed by the new records
        if (m_vRecordList.size() == 0)  // Was the main file passed in?
        {
            boolean bQueryInTable = false;
            if (mainRecord == null)
                mainRecord = this.openMainRecord(); // No, open it!
            // Now look through the list and see if this query was added, it not, add it!
            for (Enumeration<FieldList> e = m_vRecordList.elements() ; e.hasMoreElements() ;)
            {   // This should only be called for Imaged GridScreens (Child windows would be deleted by now if Component)
                Record cQueryInTable = (Record)e.nextElement();
                if (cQueryInTable == mainRecord)
                    bQueryInTable = true;
            }
            if (mainRecord != null)
                if (!bQueryInTable)
                    this.addRecord(mainRecord, false);
        }
        mainRecord = this.getMainRecord();
        if (mainRecord != null)
            if (mainRecord.getKeyArea() != null)
                if (mainRecord.getKeyArea().getKeyName().equals(DBConstants.PRIMARY_KEY))
                    mainRecord.setKeyArea(mainRecord.getDefaultScreenKeyArea());
        this.openOtherRecords();    // Open the other files
        this.setScreenRecord(this.addScreenRecord());
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        this.syncHeaderToMain();    // Read in the current (optional) Header record.

        boolean bPushToBrowser = (iDisplayFieldDesc & ScreenConstants.DONT_PUSH_TO_BROSWER) == ScreenConstants.PUSH_TO_BROSWER;
        if (parentScreen != null)
            parentScreen.pushHistory(this.getScreenURL(), bPushToBrowser);  // Push this screen onto history stack
    }
    /**
     * Free.
     */
    public void free()
    {
        if (m_messageFilterList != null)
            m_messageFilterList.free();
        m_messageFilterList = null;
        
        Record record = this.getMainRecord();

        if (record != null)
        {
            try
            {
                if (record.isModified(false))
                    if (record.getRecordOwner() == this)
                    {
                        if (record.getEditMode() == Constants.EDIT_IN_PROGRESS)
                            record.set();
                        else if (record.getEditMode() == Constants.EDIT_ADD)
                            record.add();
                    }
            } catch(DBException ex) {
                this.displayError(ex);
            }
        }
        if (m_recDependent != null)
            m_recDependent.removeDependentScreen(this);  // This screen is gone, so query doesn't have to destroy me on close

        if (this.getTask() != null)
            this.getTask().setProperties(null);     // Since the task holds all of my properties

        super.free();

        m_vRecordList.free(this); // Free all records belonging to this screen, remove all others from list
        m_vRecordList = null;

        if (m_databaseCollection != null)
            m_databaseCollection.free();
        m_databaseCollection = null;
    }
    /**
     * Clear any cached data from this screen.
     * So this screen can be reused.
     */
    public void clearCachedData()
    {
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        if ((this.getClass().getName().startsWith(BASE_CLASS)) && (this.getMainRecord() != null))
        {
            App application = null;
            if (this.getTask() != null)
                application = this.getTask().getApplication();
            int iErrorCode = DBConstants.NORMAL_RETURN; 
            if (application != null)
                iErrorCode = application.checkSecurity(this.getMainRecord().getClass().getName());    // Check access to main record
            return iErrorCode;
        }
        else
            return super.checkSecurity();   // Custom screen - check security
    }
    /**
     * Special security check for screens that only allow access to users that are contacts (such as vendors, profiles, employees).
     * If this contact type and ID match the user contact and ID, allow access, otherwise deny access.
     * NOTE: Special case if this user is not a contact or is a different type of contact (such as an administrator), allow access.
     * @param strContactType
     * @param strContactID
     * @return
     */
    public int checkContactSecurity(String strContactType, String strContactID)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        
        if (Utility.isNumeric(strContactType))
        {
            ContactType recContactType = new ContactType(this);
            strContactType = recContactType.getContactTypeFromID(strContactType);
            recContactType.free();
        }

        String strUserContactType = this.getProperty(TrxMessageHeader.CONTACT_TYPE);
        String strUserContactID = this.getProperty(TrxMessageHeader.CONTACT_ID);
        
        if ((strUserContactType == null) || (strUserContactType.length() == 0) || (strUserContactType.equalsIgnoreCase(strContactType)))
            iErrorCode = DBConstants.NORMAL_RETURN; // No contact or different contact, okay
        else if ((strUserContactID != null) && (strUserContactID.equalsIgnoreCase(strContactID)))
            iErrorCode = DBConstants.NORMAL_RETURN; // Matching contact, okay
        else
            iErrorCode = DBConstants.ACCESS_DENIED; // Non-matching contact, NO
        
        return iErrorCode;
    }
    public static final String BASE_CLASS = DBConstants.ROOT_PACKAGE + "base.";
    /**
     *  Override this to add behaviors.
     */
    public void addListeners()
    {   // If there is a header record and there is a bookmark associated with it, make it current!
        super.addListeners();
        if (this.getMainRecord() != null)
            this.getMainRecord().addScreenListeners(this);      // Add the basic listeners to this record that belong on screen(s).
        if (this.getMainRecord() != null)
            if (DBConstants.TRUE.equalsIgnoreCase(this.getMainRecord().getTable().getDatabase().getProperty(DBConstants.READ_ONLY_DB)))
                this.setEditing(false);     // Don't allow changes to read-only tables
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
        if (strCommand.indexOf('=') == -1)
            if (this.getMainRecord() != null)
        {
            int iDocType = this.getMainRecord().commandToDocType(strCommand);
            if (iDocType > ScreenConstants.LAST_MODE)
            {       // Display the user defined screen.
                boolean bSuccess = (this.onForm(null, iDocType, true, iCommandOptions, null) != null);
                if (bSuccess)
                    return bSuccess;    // Return if successful.
            }
        }
        return super.doCommand(strCommand, sourceSField, iCommandOptions); // This will send the command to my view
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
        this.getTask().setLastError(null);  // If there is an error, set it here!
        this.getTask().setStatusText(null); // If there is a completion message, set it here!
        try {
//          ----------------
            Record record = this.getMainRecord();
            if (this instanceof GridScreen)
                record = ((GridScreen)this).getHeaderRecord();
            if (record != null)
            {
                String strParamValue = this.getProperty(DBConstants.STRING_OBJECT_ID_HANDLE);
                if (strParamValue != null)
                {
                    try   {
                        strParamValue = URLDecoder.decode(strParamValue, DBConstants.URL_ENCODING);
                    } catch (Exception ex)  {
                        // Ignore
                    }
                    try   {
                        record = record.setHandle(strParamValue, DBConstants.OBJECT_ID_HANDLE);   // Set to last position
                        if (record == null)
                        {
                            this.getMainRecord().addNew();
                        }
                        else
                        {
                            record.edit();
                        }
                    } catch (DBException e)   {
                    }
                }
            }
//        ----------------        
            /*int iDefaultParamsFound =*/ this.getScreenFieldView().moveControlInput(DBConstants.BLANK);
            boolean bSuccess = this.getScreenFieldView().processServletCommand();
            if (bSuccess)
            {
                String strMoveValue = this.getProperty(DBParams.COMMAND);      // Display record
                if (strMoveValue == null)
                    strMoveValue = Constants.BLANK;
                this.getTask().setStatusText("Success on previous " + strMoveValue + " operation.", DBConstants.INFORMATION);
//?             if (iDefaultParamsFound != DBConstants.NORMAL_RETURN)
//?                 ??
            }
            else
            {
                // ?
            }
        } catch (DBException ex) {
            ex.printStackTrace();
            this.getTask().setLastError(ex.getMessage());
        }            
        return this;    // By default, don't do anything
    }
    /**
     *  Get the command string to restore screen.
     * @return The URL of this screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        try   {
            if ((this.getHeaderRecord() != null)
                && (this.getHeaderRecord() != this.getMainRecord())
                && ((this.getHeaderRecord().getEditMode() == Constants.EDIT_IN_PROGRESS) || (this.getHeaderRecord().getEditMode() == Constants.EDIT_CURRENT)))
                    strURL = this.addURLParam(strURL, DBParams.HEADER_OBJECT_ID, this.getHeaderRecord().getHandle(DBConstants.BOOKMARK_HANDLE).toString());
//x            else if (this.getProperty(DBParams.HEADEROBJECTID) != null)
//x                    strURL = this.addURLParam(strURL, DBParams.HEADER_OBJECT_ID, this.getProperty(DBParams.HEADER_OBJECT_ID));
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        Map<String,Object> properties = this.getTask().getApplication().getProperties();
        if (properties != null)
        {   // Add any database properties to the url, so the created tables use this alternate db.
        	for (String key : properties.keySet())
        	{
        		if (key.endsWith(BaseDatabase.DBSHARED_PARAM_SUFFIX))
                    if (strURL.indexOf(key) == -1)
                        strURL = Utility.addURLParam(strURL, key, properties.get(key).toString());
        		if (key.endsWith(BaseDatabase.DBUSER_PARAM_SUFFIX))
                    if (strURL.indexOf(key) == -1)
                        strURL = Utility.addURLParam(strURL, key, properties.get(key).toString());
        	}
        }
        return strURL;
    }
    /**
     *  If there is a header record, return it, otherwise, return the main record.
     *  The header record is the (optional) main record on gridscreens and is sometimes used
     *  to enter data in a sub-record when a header is required.
     * @return The header record.
     */
    public Record getHeaderRecord()
    {
        return this.getMainRecord();
    }
    /**
     * Read the current file in the header record given the current detail record.
     */
    public void syncHeaderToMain()
    {
        if (this.getHeaderRecord() != null)
            this.syncRecordToMainField(null, this.getHeaderRecord(), DBParams.HEADER_OBJECT_ID);
    }
    /**
     * RestoreScreenParam Method.
     * This is a convenience method to restore screen fields to their property values.
     * @param iFieldSeq The screen field sequence to restore.
     */
    public void restoreScreenParam(int iFieldSeq)
    {
        if (this.getScreenRecord() != null)
            if (this.getScreenRecord().getField(iFieldSeq) != null)
                if (this.getScreenRecord().getField(iFieldSeq).getFieldName() != null)
        {
            String strFieldName = this.getScreenRecord().getField(iFieldSeq).getFieldName();
            if (this.getProperty(strFieldName) != null)
                this.getScreenRecord().getField(iFieldSeq).setString((String)this.getProperty(strFieldName));
        }
    }
    /**
     * Sync the contact type record to the main value.
     * @oaram fldMain The field in the main record to sync the recHeader to (if null, tries to figure it out from the recHeader).
     * @param recHeader The header record (if null, will try to make it from the fldMain).
     * @param The (optional) bookmark param for this recHeader.
     */
    public void syncRecordToMainField(ReferenceField fldMain, Record recHeader, String strBookmarkParam)
    {
        if (recHeader == null)
            if (fldMain != null)
                recHeader = fldMain.getReferenceRecord(this);
        if (recHeader == null)
            return; // Can't do it.
        if ((recHeader == null)
            || (this.getMainRecord() == recHeader)
                || (ScreenConstants.HTML_SCREEN_TYPE.equalsIgnoreCase(this.getViewFactory().getViewSubpackage())))
                    return;
        String strBookmark = this.getProperty(strBookmarkParam);
        boolean bSuccess = false;
        try   {
            if ((strBookmark != null) && (strBookmark.length() > 0))
            {
                Object objBookmark = null;
                if (recHeader.getCounterField() instanceof CounterField)
                {
                    try {
                        int iBookmark = Integer.parseInt(strBookmark);
                        if (iBookmark > 0)
                            objBookmark = new Integer(iBookmark);
                    } catch (NumberFormatException ex)  {
                    }
                }
                Record record = null;
                if (objBookmark != null)
                    record = recHeader.setHandle(objBookmark, DBConstants.BOOKMARK_HANDLE);        // Initial menu
                if (record != null)
                    bSuccess = true;
            }
        } catch (DBException ex)    {
            // Ignore errors
        }
        if (!bSuccess)
            if ((recHeader.getEditMode() != Constants.EDIT_IN_PROGRESS) && (recHeader.getEditMode() != Constants.EDIT_CURRENT))
        {
            if (fldMain == null)
                if (recHeader != null)
                    fldMain = this.getMainRecord().getReferenceField(recHeader);
            if (fldMain != null)
                if (recHeader != null)
                    if (fldMain.getReferenceRecord(null, false) != recHeader)
                        fldMain.setReferenceRecord(recHeader);
            if ((this.getMainRecord().getEditMode() != Constants.EDIT_NONE) && (this.getMainRecord().getEditMode() != Constants.EDIT_ADD))
            {   // Wow... If there isn't a valid header, but I have a detail record, read the header record.
                if (fldMain != null)
                    fldMain.getReference();   // This should read the header record.
            }
            else if (((recHeader.getEditMode() == Constants.EDIT_ADD) || (recHeader.getEditMode() == Constants.EDIT_NONE)) && (strBookmark == null))
            {   // You can't have a new record as the header, you must write/refresh so your secondary file can reference the header record.
            	if (recHeader.isModified())
            	{
	                try {
	                    recHeader.add();
	                    Object bookmark = recHeader.getLastModified(DBConstants.BOOKMARK_HANDLE);
	                    recHeader.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                        recHeader.edit();
	                } catch (DBException ex)    {
	                    ex.printStackTrace();
	                }
            	}
            	else
            	{
                    if (fldMain != null)
                    	fldMain.getReference();   // This should read the header record.
            	}
            }
        }
        // Update the header record if it changes.
        recHeader.addListener(new org.jbundle.base.db.event.UpdateOnCloseHandler(null));
    }
    /**
     * Add this record to this screen.
     * @param record The record to add.
     * @param bMainQuery If this is the main record.
     */
    public void addRecord(FieldList record, boolean bMainQuery)
    {
        if (record == null)
            return;
        if (((Record)record).getRecordOwner() != null)      // Screen already here?
            if (((Record)record).getRecordOwner() != this)      // Screen already here?
                ((Record)record).getRecordOwner().removeRecord(record);      // Already belongs to another owner, remove it!
        m_vRecordList.addRecord(record, bMainQuery);
        if (((Record)record).getRecordOwner() == null)      // Screen already here?
            ((Record)record).setRecordOwner(this);    // This is the file for this screen
    }
    /**
     * Remove this record from this screen.
     * @param record The record to remove.
     * @return true if successful.
     */
    public boolean removeRecord(FieldList record)
    {
        if (m_vRecordList == null)
            return false;
        boolean bFlag = m_vRecordList.removeRecord(record);
        if (((Record)record).getRecordOwner() == this)
            ((Record)record).setRecordOwner(null);
        return bFlag;
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {   // Override this to add (call this) or replace (don't call) this default toolbar
        return new MaintToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        return super.getTask();
    }
    /**
     * Get the main record for this screen.
     * @return The main record for this screen.
     */
    public Record getMainRecord()
    {
        Record record = m_vRecordList.getMainRecord();
        if (record == null)
            record = super.getMainRecord();   // Look thru the parent window now
        return record;
    }
    /**
     * Lookup this record for this recordowner.
     * @param The record's name.
     * @return The record with this name (or null if not found).
     */
    public Record getRecord(String strFileName)
    {
        if (m_vRecordList == null)
            return null;
        Record record = m_vRecordList.getRecord(strFileName);
        if (record != null)
            return record;
        return super.getRecord(strFileName);    // Look thru the parent window now
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return null;
    }
    /**
     * Get the screen record.
     * @return The screen record.
     */
    public Record getScreenRecord()
    {
        Record record = m_vRecordList.getScreenRecord();
        if (record == null)
            record = super.getScreenRecord(); // Look thru the parent window now
        return record;
    }
    /**
     * Set the screen record.
     * @param screenRecord The screen record.
     */
    public void setScreenRecord(Record record)
    {
        m_vRecordList.setScreenRecord(record);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return null;
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
    }
    /**
     * When this query closes, this screen should close also.
     * @param record The record that is dependent on this screen.
     */
    public void setDependentQuery(Record record)
    {
        m_recDependent = record;
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
        int iErrorCode = this.getScreenFieldView().handleMessage(message); // Have the view handle this message.
        return iErrorCode; // Override this to process change
    }
    /**
     * Process the "Login" toolbar command.
     * @return true if successful.
     */
    public boolean onLogin()
    {
        SStaticString sfStaticField = null;
        String strUserName = null;
        String strPassword = null;
        for (int i = 0; i < this.getSFieldCount(); i++)
        {
            ScreenField sField = this.getSField(i);
            if (sfStaticField == null)
                if (sField instanceof SStaticString)
                    sfStaticField = (SStaticString)sField;
            if (sField.getConverter() != null)
                if (DBParams.USER_NAME.equalsIgnoreCase(sField.getConverter().getFieldName()))
                    strUserName = sField.getConverter().getField().toString();
            if (sField.getConverter() != null)
                if (DBParams.PASSWORD.equalsIgnoreCase(sField.getConverter().getFieldName()))
                    strPassword = sField.getConverter().getField().toString();
        }
        if (strUserName == null)
            return false;
        
        App application = this.getTask().getApplication();
        int iErrorCode = application.login(this.getTask(), strUserName, strPassword, this.getProperty(DBParams.DOMAIN));
        
        if (iErrorCode == DBConstants.NORMAL_RETURN)
        {
            String strLastCommand = null;
            BasePanel parent = this.getParentScreen();
            int iUseSameWindow = ScreenConstants.USE_SAME_WINDOW | ScreenConstants.PUSH_TO_BROSWER;
            if (parent != null)
            {
                strLastCommand = parent.popHistory(1, false);
                iUseSameWindow = ScreenConstants.USE_SAME_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER;
            }
            else
                strLastCommand = this.getScreenURL();
            
            this.handleCommand(strLastCommand, this, iUseSameWindow);     // Process the last command
        }
        else
        {
            String strError = this.getTask().getLastError(iErrorCode);
            if (strError != null)
                if (sfStaticField != null)
                    sfStaticField.setString(strError);
            try {
                Thread.sleep(3000);     // Wait 3 seconds if bad password
            } catch (InterruptedException ex) {
                ex.printStackTrace();   // Never
            }
        }

        return (iErrorCode == DBConstants.NORMAL_RETURN);    // Handled
    }
    /**
     * Display the "Logon" screen.
     * @return true if successful.
     */
    public boolean onLogon()
    {
        BasePanel parentScreen = this.getParentScreen();
        ScreenLocation itsLocation = this.getScreenLocation();
        parentScreen.popHistory(1, false);
        parentScreen.pushHistory(this.getScreenURL(), false);  // Update the history to my current state.
        this.finalizeThisScreen();  // Validate current control, update record, get ready to close screen.
        Converter fieldConverter = null;
        int iDisplayFieldDesc = 0;
        Map<String,Object> properties = null;
        this.free();
        new UserLoginScreen(null, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        return true;
    }
    /**
     * Process the "Login" toolbar command.
     * @return true if successful.
     */
    public boolean onLogout()
    {
        App application = this.getTask().getApplication();
        int iErrorCode = application.login(this.getTask(), null, null, this.getProperty(DBParams.DOMAIN));
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return false;
        String strMenu = this.getProperty(DBParams.HOME);
        if (strMenu == null)
            strMenu = this.getTask().getProperty(DBParams.MENU);
        if (strMenu != null)
        {
            BasePanel screenParent = this.getParentScreen();
			int count = 0;
			while (screenParent.popHistory(1, false) != null)
			{
				count++;
			}
			count--;	// Want to move back to the first one
			if (count > 0)
				popHistory(count, true);		// Dump all browser history
            Map<String,Object> properties = this.getProperties();
            this.finalizeThisScreen();  // Validate current control, update record, get ready to close screen.
            if (!(this instanceof MenuScreen))
            {
                this.free();
                new MenuScreen(null, null, screenParent, null, ScreenConstants.MAINT_MODE, properties);
            }
            else
                this.doCommand("?" +  DBParams.MENU + '=' + strMenu, this, ScreenConstants.USE_SAME_WINDOW | DBConstants.PUSH_TO_BROSWER);
        }
        return true;   // Should always be successful        
    }
    /**
     * Process the "Login" toolbar command.
     * @return true if successful.
     */
    public boolean onChangePassword()
    {
        BasePanel screenParent = this.getParentScreen();
        this.finalizeThisScreen();  // Validate current control, update record, get ready to close screen.
        Map<String,Object> properties = null;
        this.free();
        new UserPasswordChange(null, null, screenParent, null, ScreenConstants.MAINT_MODE, properties);
        return true;   // Should always be successful
    }
    /**
     * Process the "Settings" toolbar command.
     * @return true if successful.
     */
    public boolean onChangeSettings()
    {
        BasePanel screenParent = this.getParentScreen();
        this.finalizeThisScreen();  // Validate current control, update record, get ready to close screen.
        Map<String,Object> properties = null;
        this.free();
        new UserPreferenceScreen(null, null, screenParent, null, ScreenConstants.MAINT_MODE, properties);
        return true;   // Should always be successful
    }
    /**
     * Add this message filter to my list.
     */
    public void addListenerMessageFilter(BaseMessageFilter messageFilter)
    {
        if (m_messageFilterList == null)
            m_messageFilterList = new MessageListenerFilterList(this);
        m_messageFilterList.addMessageFilter(messageFilter);
    }
    /**
     * Have the message listener remove this filter from its list.
     */
    public void removeListenerMessageFilter(BaseMessageFilter messageFilter)
    {
        if (m_messageFilterList != null)
            m_messageFilterList.removeMessageFilter(messageFilter);
    }
    /**
     * Have the message listener remove this filter from its list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to remove.
     */
    public BaseMessageFilter getListenerMessageFilter(int iIndex)
    {
        if (m_messageFilterList != null)
            return m_messageFilterList.getMessageFilter(iIndex);
        return null;
    }
    /**
     * Is this listener going to send its messages to a thin client?
     * @return true if yes.
     */
    public boolean isThinListener()
    {
        return false;
    }
    /**
     * Allow appends to this grid?
     * @param bAppending If true, allow appending to the record.
     */
    public void setAppending(boolean bAppending)
    {
        m_bAppending = bAppending;
    }
    /**
     * Allow appends to this grid?
     * @return true if appends are allowed.
     */
    public boolean getAppending()
    {
        return m_bAppending;
    }
    /**
     * Get the navigation button count.
     * @return The nav button count.
     */
    public int getNavCount()
    {
        return 0;
    }
    /**
     * Make a screen window and put the screen with this class name into it.
     * @param strScreenClass The class of the new screen.
     * @param itsLocation The location of the new screen.
     * @param screenParent The parent of the new screen.
     * @param iDisplayFieldDesc Display the field desc?
     * @param initScreen TODO
     * @return The new screen.
     */
    public static BaseScreen makeNewScreen(String strScreenClass, ScreenLocation itsLocation, BasePanel screenParent, int iDisplayFieldDesc, Map<String, Object> properties, boolean initScreen)
    {
        BaseScreen screen = (BaseScreen)ClassServiceUtility.getClassService().makeObjectFromClassName(strScreenClass);
        if (screen != null)
        {
            BaseApplet applet = null;
            if (screenParent.getTask() instanceof BaseApplet)
            	applet = (BaseApplet)screenParent.getTask();
            if (initScreen)
            {
                Object oldCursor = null;
                if (applet != null)
                	oldCursor = applet.setStatus(Constant.WAIT, applet, null);
                screen.init(null, itsLocation, screenParent, null, iDisplayFieldDesc, properties);
                if (applet != null)
                    applet.setStatus(0, applet, oldCursor);
            }
        }
        return screen;
    }
    /**
     * Make a screen window and put the screen with this class name into it.
     * @param itsLocation The location of the new screen.
     * @param screenParent The parent of the new screen.
     * @param strRecord The class of the record to create.
     * @param iDocMode The type of screen to create for this record.
     * @return The new screen.
     */
    public static BaseScreen makeScreenFromRecord(ScreenLocation itsLocation, BasePanel screenParent, String strRecord, int iDocMode, Map<String, Object> properties)
    {
        BaseScreen screen = null;
        Record record = null;
        if (strRecord != null)
        {
            RecordOwner recordOwner = Utility.getRecordOwner(screenParent);
            record = Record.makeRecordFromClassName(strRecord, recordOwner);
            if (record != null)
                if (recordOwner != null)
                    recordOwner.removeRecord(record);
        }
        if (record != null)
        {
            BaseApplet applet = null;
            if (screenParent.getTask() instanceof BaseApplet)
            	applet = (BaseApplet)screenParent.getTask();
            Object oldCursor = null;
            if (applet != null)
            	oldCursor = applet.setStatus(Constant.WAIT, applet, null);
            screen = record.makeScreen(itsLocation, screenParent, iDocMode, properties);
            if (applet != null)
                applet.setStatus(0, applet, oldCursor);
        }
        return screen;
    }
    /**
     * Make a screen window and put the screen with this class name into it.
     * @param itsLocation The location of the new screen.
     * @param screenParent The parent of the new screen.
     * @param strRecord The class of the record to create.
     * @param iDocMode The type of screen to create for this record.
     * @return The new screen.
     */
    public static BaseScreen makeScreenFromRecord(ScreenLocation itsLocation, BasePanel screenParent, String strRecord, String strDocMode, Map<String, Object> properties)
    {
        BaseScreen screen = null;
        Record record = null;
        RecordOwner recordOwner = Utility.getRecordOwner(screenParent);
        if (strRecord != null)
            record = Record.makeRecordFromClassName(strRecord, recordOwner);
        if (record != null)
        {
            if (recordOwner != null)
                recordOwner.removeRecord(record);    // Should be set to the new screen.
            int iDocMode = record.commandToDocType(strDocMode);
            BaseApplet applet = null;
            if (screenParent.getTask() instanceof BaseApplet)
            	applet = (BaseApplet)screenParent.getTask();
            Object oldCursor = null;
            if (applet != null)
            	oldCursor = applet.setStatus(Constant.WAIT, applet, null);
            screen = record.makeScreen(itsLocation, screenParent, iDocMode, properties);
            if (applet != null)
                applet.setStatus(0, applet, oldCursor);
        }
        return screen;
    }
    /**
     * Make a screen window from the current params.
     * @param task The parent to get the SCREEN, SCREENTYPE, and RECORD params from.
     * @param itsLocation The location of the new screen.
     * @param screenParent The parent of the new screen.
     * @param iDocType The document display params
     * @return The new screen.
     */
    public static BaseScreen makeScreenFromParams(Task task, ScreenLocation itsLocation, BasePanel screenParent, int iDocType, Map<String, Object> properties)
    {
    	iDocType = iDocType & ~(ScreenConstants.DISPLAY_MASK | ScreenConstants.SCREEN_TYPE_MASK);	// Don't need the screen types
        BaseScreen screen = null;
// First, see if they want to see a screen
        String strScreen = task.getProperty(DBParams.SCREEN);
        if (strScreen != null)
            screen = BaseScreen.makeNewScreen(strScreen, itsLocation, screenParent, iDocType | ScreenConstants.DEFAULT_DISPLAY, properties, true);
// Now, see if they want to open a file and create the default screen
        if (screen == null)
        {
            String strRecord = task.getProperty(DBParams.RECORD);
            if (strRecord != null)
            {
                String strScreenType = task.getProperty(DBParams.COMMAND);
                iDocType = iDocType | ScreenConstants.DISPLAY_MODE | ScreenConstants.DEFAULT_DISPLAY;
                if ((strScreenType != null) && (strScreenType.length() > 0))
                    screen = BaseScreen.makeScreenFromRecord(itsLocation, screenParent, strRecord, strScreenType, properties);
                else
                    screen = BaseScreen.makeScreenFromRecord(itsLocation, screenParent, strRecord, iDocType, properties);
            }
        }
        if (screen == null)
        {
            String strRecord = task.getProperty(ThinMenuConstants.FORM.toLowerCase());
            if (strRecord != null)
                screen = BaseScreen.makeScreenFromRecord(itsLocation, screenParent, strRecord, iDocType | ScreenConstants.MAINT_MODE | ScreenConstants.DEFAULT_DISPLAY, properties);
        }
        if (screen == null)
        {
            String strMenu = task.getProperty(DBParams.MENU);
            if (strMenu != null)
            {
                screen = new MenuScreen(null, null, screenParent, null, iDocType | ScreenConstants.MAINT_MODE, properties);
            }
        }
        if (screen == null)
        {   // If no params were passed in, display the default screen (menu).
            screen = new MenuScreen(null, null, screenParent, null, iDocType | ScreenConstants.MAINT_MODE, properties);
        }
        return screen;
    }
    /**
     * Get the database owner for this recordowner.
     * Typically, the Environment is returned.
     * If you are using transactions, then the recordowner is returned, as the recordowner
     * needs private database connections to track transactions.
     * Just remember, if you are managing transactions, you need to call commit or your trxs are toast.
     * Also, you have to set the AUTO_COMMIT to false, before you init your records, so the database
     * object will be attached to the recordowner rather than the environment.
     * @return The database owner.
     */
    public DatabaseOwner getDatabaseOwner()
    {
        DatabaseOwner databaseOwner = null;
        if (DBConstants.FALSE.equalsIgnoreCase(this.getProperty(SQLParams.AUTO_COMMIT_PARAM)))
            databaseOwner = this; // If auto-commit is off, I am the db owner.
        else
            databaseOwner = ((BaseApplication)this.getTask().getApplication());
        return databaseOwner;
    }
    /**
     * Given the name, either get the open database, or open a new one.
     * @param strDBName The name of the database.
     * @param iDatabaseType The type of database/table.
     * @return The database (new or current).
     */
    public BaseDatabase getDatabase(String strDBName, int iDatabaseType, Map<String, Object> properties)
    {
        if (m_databaseCollection == null)
            m_databaseCollection = new DatabaseCollection(this);
        return m_databaseCollection.getDatabase(strDBName, iDatabaseType, properties);
    }
    /**
     * Add this database to my database list.<br />
     * Do not call these directly, used in database init.
     * @param database The database to add.
     */
    public void addDatabase(BaseDatabase database)
    {
        m_databaseCollection.addDatabase(database);
    }
    /**
     * Remove this database from my database list.
     * Do not call these directly, used in database free.
     * @param database The database to free.
     * @return true if successful.
     */
    public boolean removeDatabase(BaseDatabase database)
    {
        return m_databaseCollection.removeDatabase(database);
    }
    /**
     * Get the environment.
     * From the database owner interface.
     * @return The Environment.
     */
    public Environment getEnvironment()
    {
        if ((this.getTask() != null) && (this.getTask().getApplication() != null))
            return ((BaseApplication)this.getTask().getApplication()).getEnvironment();
        return null;
    }
    /**
     * Get this record owner's parent.
     * Could be anotherRecordOwner or could be a Task.
     * @return The this record owner's parent.
     */
    public RecordOwnerParent getMyParent()
    {
        return m_screenParent;
    }
    /**
     * Is this recordowner the master or slave.
     * The slave is typically the TableSessionObject that is created to manage a ClientTable.
     * @return The MASTER/SLAVE flag.
     */
    public int getMasterSlave()
    {
        return RecordOwner.MASTER;      // A screen is always the master process.
    }
    /**
     * Is this recordowner a batch process, or an interactive screen?
     * @return True if this is a batch process.
     */
    public boolean isBatch()
    {
        return this.getScreenFieldView().isBatch();
    }
}
