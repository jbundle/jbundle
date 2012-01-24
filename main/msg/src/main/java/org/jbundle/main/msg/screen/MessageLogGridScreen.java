/**
 * @(#)MessageLogGridScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.screen;

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
import org.jbundle.main.db.*;
import org.jbundle.main.db.base.*;
import org.jbundle.main.msg.db.*;

/**
 *  MessageLogGridScreen - .
 */
public class MessageLogGridScreen extends DetailGridScreen
{
    /**
     * Default constructor.
     */
    public MessageLogGridScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?.
     */
    public MessageLogGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * MessageLogGridScreen Method.
     */
    public MessageLogGridScreen(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        this();
        this.init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        super.init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new MessageLog(this);
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new MessageLogScreenRecord(this);
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        int iErrorCode = super.checkSecurity();
        if ((iErrorCode == DBConstants.NORMAL_RETURN) || (iErrorCode == Constants.READ_ACCESS))
        {   // Okay, their group can access this screen, but can this user access this data?
            String strUserContactType = this.getProperty(DBParams.CONTACT_TYPE);
            String strUserContactID = this.getProperty(DBParams.CONTACT_ID);
        
            String strContactTypeID = this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID).toString();
            if ((strContactTypeID == null) || (strContactTypeID.length() == 0))
                if ((strUserContactType != null) && (strUserContactType.length() > 0))
            {
                if (!Utility.isNumeric(strUserContactType))
                {
                    ContactType recContactType = (ContactType)((ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID)).getReferenceRecord(this);
                    strUserContactType = Integer.toString(recContactType.getIDFromCode(strUserContactType));
                }
                this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID).setString(strContactTypeID = strUserContactType);
            }
            String strContactID = this.getScreenRecord().getField(MessageLogScreenRecord.kContactID).toString();
            if ((strContactID == null) || (strContactID.length() == 0))
                if ((strUserContactID != null) && (strUserContactID.length() > 0))
                    this.getScreenRecord().getField(MessageLogScreenRecord.kContactID).setString(strContactID = strUserContactID);
            iErrorCode = this.checkContactSecurity(strContactTypeID, strContactID);
        }
        return iErrorCode;
    }
    /**
     * IsContactDisplay Method.
     */
    public boolean isContactDisplay()
    {
        String strUserContactType = this.getProperty(DBParams.CONTACT_TYPE);
        String strUserContactID = this.getProperty(DBParams.CONTACT_ID);
        
        String strContactType = ((ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID)).getReference().getField(ContactType.kCode).toString();
        String strContactID = this.getScreenRecord().getField(MessageLogScreenRecord.kContactID).toString();
        
        if ((strUserContactID != null) && (strUserContactID.equals(strContactID)))
            if ((strUserContactType != null) && (strUserContactType.equals(strContactType)))
                return true;
        return false;
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
        if (this.isContactDisplay())
        {
            ReferenceField field = (ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kMessageInfoTypeID);
            field.setValue(field.getReferenceRecord(this).getIDFromCode(MessageInfoType.REQUEST));
            field = (ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kMessageTypeID);
            field.setValue(field.getReferenceRecord(this).getIDFromCode(MessageType.MESSAGE_OUT));
            field = (ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kMessageStatusID);
            field.setValue(field.getReferenceRecord(this).getIDFromCode(MessageStatus.SENT));
        }
        
        this.getMainRecord().getKeyArea().setKeyOrder(DBConstants.DESCENDING);
        
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kReferenceID, this.getScreenRecord().getField(MessageLogScreenRecord.kReferenceID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kReferenceID).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kReferenceType, this.getScreenRecord().getField(MessageLogScreenRecord.kReferenceType), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kReferenceType).addListener(new FieldReSelectHandler(this));
        
        this.getScreenRecord().getField(MessageLogScreenRecord.kContactID).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kMessageInfoTypeID, this.getScreenRecord().getField(MessageLogScreenRecord.kMessageInfoTypeID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kMessageInfoTypeID).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kMessageTypeID, this.getScreenRecord().getField(MessageLogScreenRecord.kMessageTypeID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kMessageTypeID).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kMessageStatusID, this.getScreenRecord().getField(MessageLogScreenRecord.kMessageStatusID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kMessageStatusID).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kMessageTransportID, this.getScreenRecord().getField(MessageLogScreenRecord.kMessageTransportID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kMessageTransportID).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kUserID, this.getScreenRecord().getField(MessageLogScreenRecord.kUserID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kUserID).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kMessageTime, this.getScreenRecord().getField(MessageLogScreenRecord.kStartDate), CompareFileFilter.GREATER_THAN_EQUAL, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kStartDate).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kMessageTime, this.getScreenRecord().getField(MessageLogScreenRecord.kEndDate), CompareFileFilter.LESS_THAN_EQUAL, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kEndDate).addListener(new FieldReSelectHandler(this));
        
        this.setEditing(false);
    }
    /**
     * Read the current file in the header record given the current detail record.
     */
    public void syncHeaderToMain()
    {
        super.syncHeaderToMain();
        this.restoreScreenParam(MessageLogScreenRecord.kReferenceID);
        this.restoreScreenParam(MessageLogScreenRecord.kReferenceType);
    }
    /**
     * OpenHeaderRecord Method.
     */
    public Record openHeaderRecord()
    {
        Record record = null;
        //this.syncContactTypeToMain();    // Read in the current contact record
        if (this.getScreenRecord() == null)
            this.setScreenRecord(this.addScreenRecord());
        ReferenceField fldContactType = (ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID);
        ContactType recContactType = (ContactType)fldContactType.getReferenceRecord(this);
        recContactType = (ContactType)fldContactType.getReference(); // Being careful
        String strHeaderRecordName = null;
        if (recContactType != null)
            strHeaderRecordName = recContactType.getField(ContactType.kCode).toString();
        if ((strHeaderRecordName == null) || (strHeaderRecordName.length() == 0))
            strHeaderRecordName = this.getProperty(fldContactType.getFieldName());
        record = recContactType.makeRecordFromRecordName(strHeaderRecordName, this);
        if (record != null)
            ((ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kContactID)).setReferenceRecord(record);
        return record;
    }
    /**
     * AddSubFileFilter Method.
     */
    public void addSubFileFilter()
    {
        ContactType recContactType = (ContactType)((ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID)).getReference();
        Record recHeader = this.getHeaderRecord();
        if (recHeader != null)
        {
            recContactType = recContactType.getContactType(recHeader);
            this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID).moveFieldToThis(recContactType.getField(ContactType.kID));   // Display the field
            if ((recHeader.getEditMode() == DBConstants.EDIT_CURRENT) || (recHeader.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                this.getScreenRecord().getField(MessageLogScreenRecord.kContactID).moveFieldToThis(recHeader.getField(VirtualRecord.kID));   // Display the field
        }
        Record recMessageDetail = this.getMainRecord();
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kContactTypeID, this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID).addListener(new FieldReSelectHandler(this));
        
        this.getMainRecord().addListener(new CompareFileFilter(MessageLog.kContactID, this.getScreenRecord().getField(MessageLogScreenRecord.kContactID), DBConstants.EQUALS, null, true));
        FieldListener listener = null;
        this.getScreenRecord().getField(MessageLogScreenRecord.kContactID).addListener(listener = new FieldListener(null)
        {
            public int fieldChanged(boolean bDisplayOption, int iMoveMode)
            {
                int iKeyOrder = MessageLog.kContactTypeIDKey;
                if (this.getOwner().isNull())
                    iKeyOrder = MessageLog.kMessageTimeKey;
                getMainRecord().setKeyArea(iKeyOrder);
                return super.fieldChanged(bDisplayOption, iMoveMode);
            }
        });
        listener.fieldChanged(true, DBConstants.INIT_MOVE); // Initialize key order.
        this.getScreenRecord().getField(MessageLogScreenRecord.kContactID).addListener(new FieldReSelectHandler(this));
    }
    /**
     * Get the command string to restore screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        ReferenceField fldContactType = (ReferenceField)this.getScreenRecord().getField(MessageLogScreenRecord.kContactTypeID);
        String strContactTypeParam = fldContactType.getFieldName();
        if (!fldContactType.isNull())
        {
            String strContactType = fldContactType.getReference().getField(ContactType.kCode).toString();
            strURL = this.addURLParam(strURL, strContactTypeParam, strContactType);
        }
        return strURL;
    }
    /**
     * Add the navigation button(s) to the left of the grid row.
     */
    public void addNavButtons()
    {
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        String strMessageScreen = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(MessageLog.MESSAGE_SCREEN);
        new SCannedBox(this.getNextLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, null, MessageLog.MESSAGE_ICON, MessageLog.MESSAGE_SCREEN, strMessageScreen);
        if (!this.isContactDisplay())
        {
            strMessageScreen = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(MessageLog.SOURCE_SCREEN);
            new SCannedBox(this.getNextLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, null, MessageLog.SOURCE_ICON, MessageLog.SOURCE_SCREEN, strMessageScreen);
        }
        super.addNavButtons();  // Next buttons will be "First!"
    }
    /**
     * Add button(s) to the toolbar.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        
        String strMessageScreen = null;
        String strMessageScreenTip = null;
        
        if (!this.isContactDisplay())
        {
            strMessageScreen = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(MessageLog.SOURCE_SCREEN);
            strMessageScreenTip = application.getResources(ResourceConstants.MAIN_RESOURCE, false).getString(MessageLog.SOURCE_SCREEN + DBConstants.TIP);
            new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, strMessageScreen, MessageLog.SOURCE_ICON, MessageLog.SOURCE_SCREEN, strMessageScreenTip);
        }
        
        strMessageScreen = application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(MessageLog.MESSAGE_SCREEN);
        strMessageScreenTip = application.getResources(ResourceConstants.MAIN_RESOURCE, false).getString(MessageLog.MESSAGE_SCREEN + DBConstants.TIP);
        new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, strMessageScreen, MessageLog.MESSAGE_ICON, MessageLog.MESSAGE_SCREEN, strMessageScreenTip);
    }
    /**
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        if (this.isContactDisplay())
            return null;
        Record recHeader = this.getHeaderRecord();
        Record recMessageDetail = this.getMainRecord();
        if (recHeader instanceof Company)   // Profile
            ((ReferenceField)recMessageDetail.getField(MessageDetail.kPersonID)).setReferenceRecord(recHeader);   // Make sure this is hooked up
        return new MessageLogHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(MessageLog.kMessageLogFile).getField(MessageLog.kMessageStatusID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageLog.kMessageLogFile).getField(MessageLog.kMessageTime).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageLog.kMessageLogFile).getField(MessageLog.kDescription).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageLog.kMessageLogFile).getField(MessageLog.kMessageProcessInfoID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageLog.kMessageLogFile).getField(MessageLog.kMessageTransportID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
