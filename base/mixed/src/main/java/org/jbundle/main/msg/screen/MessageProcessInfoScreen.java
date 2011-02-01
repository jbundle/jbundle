/**
 *  @(#)MessageProcessInfoScreen.
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
 *  MessageProcessInfoScreen - Message process information.
 */
public class MessageProcessInfoScreen extends DetailScreen
{
    /**
     * Default constructor.
     */
    public MessageProcessInfoScreen()
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
    public MessageProcessInfoScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return "Message process information";
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new MessageProcessInfo(this);
    }
    /**
     * Open the header record.
     * @return The new header record.
     */
    public Record openHeaderRecord()
    {
        return new MessageInfo(this);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new MessageInfoScreenRecord(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
            // Link the screen field to the passed in record
        ((ReferenceField)this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageInfoID)).syncReference(this.getHeaderRecord());
        
        String strManualTransportID = Integer.toString(((ReferenceField)this.getMainRecord().getField(MessageProcessInfo.kDefaultMessageTransportID)).getIDFromCode(MessageTransport.MANUAL));
        this.getMainRecord().getField(MessageProcessInfo.kDefaultMessageTransportID).addListener(new DisableOnFieldHandler(this.getMainRecord().getField(MessageProcessInfo.kInitialMessageStatusID), strManualTransportID, false));
        Converter convCheckMark = new RadioConverter(this.getMainRecord().getField(MessageProcessInfo.kDefaultMessageTransportID), strManualTransportID, false);
        this.getMainRecord().getField(MessageProcessInfo.kDefaultMessageTransportID).addListener(new RemoveConverterOnFreeHandler(convCheckMark));
        this.getMainRecord().getField(MessageProcessInfo.kDefaultMessageTransportID).addListener(new CopyDataHandler(this.getMainRecord().getField(MessageProcessInfo.kInitialMessageStatusID), null, convCheckMark));
    }
    /**
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        return new MessageInfoHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
    }
    /**
     * Add button(s) to the toolbar.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, application.getResources(ResourceConstants.PRODUCT_RESOURCE, true).getString(MessageInfo.TRANSPORT_DETAIL_SCREEN), MenuConstants.GRID, MessageInfo.TRANSPORT_DETAIL_SCREEN, null);
        super.addToolbarButtons(toolScreen);
    }
    /**
     * Add the sub file filter (linking the header to the main file)
     * Override this if the header does not have a direct link to the detail.
     */
    public void addSubFileFilter()
    {
        this.getMainRecord().addListener(new CompareFileFilter(MessageProcessInfo.kMessageInfoID, (BaseField)this.getHeaderRecord().getCounterField(), DBConstants.EQUALS, null, true));
        this.getMainRecord().getField(MessageProcessInfo.kMessageInfoID).addListener(new InitFieldHandler((BaseField)this.getHeaderRecord().getCounterField(), false));
    }

}
