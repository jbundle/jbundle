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
        Record recContactType = ((ReferenceField)this.getMainRecord().getField(MessageDetail.CONTACT_TYPE_ID)).getReferenceRecord(this);
    }
    /**
     * OpenHeaderRecord Method.
     */
    public Record openHeaderRecord()
    {
        Record record = null;
        this.syncContactTypeToMain();    // Read in the current contact record
        ReferenceField fldContactType = (ReferenceField)this.getMainRecord().getField(MessageDetail.CONTACT_TYPE_ID);
        ContactType recContactType = (ContactType)fldContactType.getReferenceRecord(this);
        String strHeaderRecordName = null;
        if (recContactType != null)
            strHeaderRecordName = recContactType.getField(ContactType.CODE).toString();
        if ((strHeaderRecordName == null) || (strHeaderRecordName.length() == 0))
            strHeaderRecordName = this.getProperty(fldContactType.getFieldName());
        record = recContactType.makeRecordFromRecordName(strHeaderRecordName, this);
        if (record != null)
                ((ReferenceField)this.getMainRecord().getField(MessageDetail.PERSON_ID)).setReferenceRecord(record);
        return record;
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getMainRecord().addListener(new MessageDetailDefTransHandler(null));
        String strManualTransportID = Integer.toString(((ReferenceField)this.getMainRecord().getField(MessageDetail.MESSAGE_TRANSPORT_ID)).getIDFromCode(MessageTransport.MANUAL));
        this.getMainRecord().getField(MessageDetail.MESSAGE_TRANSPORT_ID).setDefault(strManualTransportID);
    }
    /**
     * Read the current file in the header record given the current detail record.
     */
    public void syncHeaderToMain()
    {
        // Don't call super.
        this.syncContactTypeToMain();
        ReferenceField fldMain = (ReferenceField)this.getMainRecord().getField(MessageDetail.PERSON_ID);
        this.syncRecordToMainField(fldMain, null, DBParams.HEADER_OBJECT_ID);
    }
    /**
     * SyncContactTypeToMain Method.
     */
    public void syncContactTypeToMain()
    {
        ReferenceField fldContactType = (ReferenceField)this.getMainRecord().getField(MessageDetail.CONTACT_TYPE_ID);
        String strContactTypeParam = fldContactType.getFieldName();
        this.syncRecordToMainField(fldContactType, null, strContactTypeParam);
    }
    /**
     * AddSubFileFilter Method.
     */
    public void addSubFileFilter()
    {
        ContactType recContactType = (ContactType)this.getRecord(ContactType.CONTACT_TYPE_FILE);
        Record recHeader = this.getHeaderRecord();
        recContactType = recContactType.getContactType(recHeader);
        Record recMessageDetail = this.getMainRecord();
        recMessageDetail.setKeyArea(MessageDetail.CONTACT_TYPE_ID_KEY);
        recMessageDetail.addListener(new SubFileFilter(recContactType.getField(ContactType.ID), MessageDetail.CONTACT_TYPE_ID, recHeader.getField(VirtualRecord.ID), MessageDetail.PERSON_ID, null, null));
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(MessageDetail.MESSAGE_DETAIL_FILE).getField(MessageDetail.MESSAGE_PROCESS_INFO_ID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        Converter convDefaultTransport = new CheckConverter(this.getRecord(MessageDetail.MESSAGE_DETAIL_FILE).getField(MessageDetail.DEFAULT_MESSAGE_TRANSPORT_ID), this.getRecord(MessageDetail.MESSAGE_DETAIL_FILE).getField(MessageDetail.MESSAGE_TRANSPORT_ID), null, true);
        convDefaultTransport.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.MESSAGE_DETAIL_FILE).getField(MessageDetail.MESSAGE_TRANSPORT_ID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Get the command string to restore screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        ReferenceField fldContactType = (ReferenceField)this.getMainRecord().getField(MessageDetail.CONTACT_TYPE_ID);
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
            ((ReferenceField)recMessageDetail.getField(MessageDetail.PERSON_ID)).setReferenceRecord(recHeader);   // Make sure this is hooked up
        return new MessageDetailHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
    }

}
