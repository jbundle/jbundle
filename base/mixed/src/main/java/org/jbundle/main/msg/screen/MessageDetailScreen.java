/**
 *  @(#)MessageDetailScreen.
 *  Copyright © 2010 tourapp.com. All rights reserved.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.main.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.main.msg.db.*;

/**
 *  MessageDetailScreen - Message detail.
 */
public class MessageDetailScreen extends DetailScreen
{
    /**
     * Default constructor.
     */
    public MessageDetailScreen()
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
    public MessageDetailScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new MessageDetail(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        ((MessageDetail)this.getMainRecord()).addPropertyListeners();
        this.getMainRecord().addListener(new MessageDetailDefTransHandler(null));
        String strManualTransportID = Integer.toString(((ReferenceField)this.getMainRecord().getField(MessageDetail.kMessageTransportID)).getIDFromCode(MessageTransport.MANUAL));
        
        this.getMainRecord().getField(MessageDetail.kMessageTransportID).addListener(new DisableOnFieldHandler(this.getMainRecord().getField(MessageDetail.kInitialManualTransportStatusID), strManualTransportID, false));
        Converter convCheckMark = new RadioConverter(this.getMainRecord().getField(MessageDetail.kMessageTransportID), strManualTransportID, false);
        this.getMainRecord().getField(MessageDetail.kMessageTransportID).addListener(new RemoveConverterOnFreeHandler(convCheckMark));
        this.getMainRecord().getField(MessageDetail.kMessageTransportID).addListener(new CopyDataHandler(this.getMainRecord().getField(MessageDetail.kInitialManualTransportStatusID), null, convCheckMark));
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        ((ReferenceField)this.getMainRecord().getField(MessageDetail.kContactTypeID)).getReferenceRecord(this);
        super.openOtherRecords();
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
     * Sync the contact type record to the main value.
     */
    public void syncContactTypeToMain()
    {
        ReferenceField fldContactType = (ReferenceField)this.getMainRecord().getField(MessageDetail.kContactTypeID);
        String strContactTypeParam = fldContactType.getFieldName();
        this.syncRecordToMainField(fldContactType, null, strContactTypeParam);
    }
    /**
     * Open the header record.
     * @return The new header record.
     */
    public Record openHeaderRecord()
    {
        Record record = null;
        this.syncContactTypeToMain();    // Read in the current contact record
        ReferenceField fldContactType = (ReferenceField)this.getMainRecord().getField(MessageDetail.kContactTypeID);
        ContactType recContactType = (ContactType)fldContactType.getReferenceRecord(this);
        recContactType = (ContactType)fldContactType.getReference(); // Being careful
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
     * If there is a header record, return it, otherwise, return the main record.
     * The header record is the (optional) main record on gridscreens and is sometimes used
     * to enter data in a sub-record when a header is required.
     * @return The header record.
     */
    public Record getHeaderRecord()
    {
        return ((ReferenceField)this.getMainRecord().getField(MessageDetail.kPersonID)).getReferenceRecord();
    }
    /**
     * Add the sub file filter (linking the header to the main file)
     * Override this if the header does not have a direct link to the detail.
     */
    public void addSubFileFilter()
    {
        ContactType recContactType = (ContactType)((ReferenceField)this.getMainRecord().getField(MessageDetail.kContactTypeID)).getReferenceRecord(this);
        Record recHeader = this.getHeaderRecord();
        recContactType = recContactType.getContactType(recHeader);
        Record recMessageDetail = this.getMainRecord();
        recMessageDetail.setKeyArea(MessageDetail.kContactTypeIDKey);
        recMessageDetail.addListener(new SubFileFilter(recContactType.getField(ContactType.kID), MessageDetail.kContactTypeID, recHeader.getField(VirtualRecord.kID), MessageDetail.kPersonID, null, -1));
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kMessageTransportID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kDefaultMessageVersionID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kMessageProcessInfoID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kDestinationSite).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kDestinationPath).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kReturnSite).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kXSLTDocument).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        Converter convDefaultTransport = new CheckConverter(this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kDefaultMessageTransportID), this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kMessageTransportID), null, true);
        convDefaultTransport.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kInitialManualTransportStatusID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageDetail.kMessageDetailFile).getField(MessageDetail.kProperties).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
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
        return new MessageDetailHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
    }

}
