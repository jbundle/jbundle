/**
 * @(#)MessageDetailGridScreen.
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
import org.jbundle.main.screen.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.main.db.base.*;

/**
 *  MessageDetailGridScreen - Message detail.
 */
public class MessageDetailGridScreen extends DetailGridScreen
{
    /**
     * Default constructor.
     */
    public MessageDetailGridScreen()
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
    public MessageDetailGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return "Message detail";
    }
    /**
     * MessageDetailGridScreen Method.
     */
    public MessageDetailGridScreen(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
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
        return new MessageDetail(this);
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        Record recContactType = ((ReferenceField)this.getMainRecord().getField(MessageDetail.kContactTypeID)).getReferenceRecord(this);
    }
    /**
     * OpenHeaderRecord Method.
     */
    public Record openHeaderRecord()
    {
        Record record = null;
        this.syncContactTypeToMain();    // Read in the current contact record
        ReferenceField fldContactType = (ReferenceField)this.getMainRecord().getField(MessageDetail.kContactTypeID);
        ContactType recContactType = (ContactType)fldContactType.getReferenceRecord(this);
        String strHeaderRecordName = null;
        if (recContactType != null)
            strHeaderRecordName = recContactType.getField(ContactType.kCode).toString();
        if ((strHeaderRecordName == null) || (strHeaderRecordName.length() == 0))
            strHeaderRecordName = this.getProperty(fldContactType.getFieldName());
        record = recContactType.makeRecordFromRecordName(strHeaderRecordName, this);
        if (record != null)
                ((ReferenceField)this.getMainRecord().getField(MessageDetail.kPersonID)).setReferenceRecord(record);
        return record;
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getMainRecord().addListener(new MessageDetailDefTransHandler(null));
        String strManualTransportID = Integer.toString(((ReferenceField)this.getMainRecord().getField(MessageDetail.kMessageTransportID)).getIDFromCode(MessageTransport.MANUAL));
        this.getMainRecord().getField(MessageDetail.kMessageTransportID).setDefault(strManualTransportID);
    }
    /**
     * Read the current file in the header record given the current detail record.
     */
    public void syncHeaderToMain()
    {
        // Don't call super.
        this.syncContactTypeToMain();
        ReferenceField fldMain = (ReferenceField)this.getMainRecord().getField(MessageDetail.kPersonID);
        this.syncRecordToMainField(fldMain, null, DBParams.HEADER_OBJECT_ID);
    }
    /**
     * SyncContactTypeToMain Method.
     */
    public void syncContactTypeToMain()
    {
        ReferenceField fldContactType = (ReferenceField)this.getMainRecord().getField(MessageDetail.kContactTypeID);
        String strContactTypeParam = fldContactType.getFieldName();
        this.syncRecordToMainField(fldContactType, null, strContactTypeParam);
    }
    /**
     * AddSubFileFilter Method.
     */
    public void addSubFileFilter()
    {
        ContactType recContactType = (ContactType)this.getRecord(ContactType.kContactTypeFile);
        Record recHeader = this.getHeaderRecord();
        recContactType = recContactType.getContactType(recHeader);
        Record recMessageDetail = this.getMainRecord();
        recMessageDetail.setKeyArea(MessageDetail.kContactTypeIDKey);
        recMessageDetail.addListener(new SubFileFilter(recContactType.getField(ContactType.kID), MessageDetail.kContactTypeID, recHeader.getField(VirtualRecord.kID), MessageDetail.kPersonID, null, -1));
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kMessageProcessInfoID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        Converter convDefaultTransport = new CheckConverter(this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kDefaultMessageTransportID), this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kMessageTransportID), null, true);
        convDefaultTransport.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kMessageTransportID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Get the command string to restore screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        ReferenceField fldContactType = (ReferenceField)this.getMainRecord().getField(MessageDetail.kContactTypeID);
        strURL = Utility.addFieldParam(strURL, fldContactType);
        return strURL;
    }
    /**
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        Record recHeader = this.getHeaderRecord();
        Record recMessageDetail = this.getMainRecord();        
        if (recHeader instanceof Company)   // Profile
            ((ReferenceField)recMessageDetail.getField(MessageDetail.kPersonID)).setReferenceRecord(recHeader);   // Make sure this is hooked up
        return new MessageDetailHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
    }

}
