/**
 *  @(#)MessageInfoHeaderScreen.
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
    public MessageInfoHeaderScreen(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize class fields.
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageInfoID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageInfoTypeID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageTypeID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.kProcessTypeID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.kContactTypeID).setEnabled(true);
        this.getScreenRecord().getField(MessageInfoScreenRecord.kRequestTypeID).setEnabled(true);
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
