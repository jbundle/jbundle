/**
 * @(#)MessageInfoHeaderScreen.
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
import org.jbundle.main.screen.*;
import org.jbundle.main.msg.db.*;

/**
 *  MessageInfoHeaderScreen - .
 */
public class MessageInfoHeaderScreen extends HeaderScreen
{
    /**
     * Default constructor.
     */
    public MessageInfoHeaderScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?.
     */
    public MessageInfoHeaderScreen(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getScreenRecord().getField(MessageInfoScreenRecord.MESSAGE_INFO_ID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.MESSAGE_INFO_TYPE_ID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.MESSAGE_TYPE_ID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.PROCESS_TYPE_ID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.CONTACT_TYPE_ID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.REQUEST_TYPE_ID).setEnabled(true);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(MessageInfoScreenRecord.kMessageInfoScreenRecordFile).getField(MessageInfoScreenRecord.kMessageInfoID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageInfoScreenRecord.kMessageInfoScreenRecordFile).getField(MessageInfoScreenRecord.kMessageTypeID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageInfoScreenRecord.kMessageInfoScreenRecordFile).getField(MessageInfoScreenRecord.kProcessTypeID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageInfoScreenRecord.kMessageInfoScreenRecordFile).getField(MessageInfoScreenRecord.kMessageInfoTypeID).setupDefaultView(this.getNextLocation(ScreenConstants.TOP_NEXT, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageInfoScreenRecord.kMessageInfoScreenRecordFile).getField(MessageInfoScreenRecord.kContactTypeID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageInfoScreenRecord.kMessageInfoScreenRecordFile).getField(MessageInfoScreenRecord.kRequestTypeID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
