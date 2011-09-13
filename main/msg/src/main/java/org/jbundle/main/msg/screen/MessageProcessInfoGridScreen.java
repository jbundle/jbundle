/**
 * @(#)MessageProcessInfoGridScreen.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.main.screen.*;
import org.jbundle.main.msg.db.*;

/**
 *  MessageProcessInfoGridScreen - Message process information.
 */
public class MessageProcessInfoGridScreen extends DetailGridScreen
{
    /**
     * Default constructor.
     */
    public MessageProcessInfoGridScreen()
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
    public MessageProcessInfoGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * MessageProcessInfoGridScreen Method.
     */
    public MessageProcessInfoGridScreen(Record recMain, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        this();
        this.init(recMain, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
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
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new MessageInfoScreenRecord(this);
    }
    /**
     * OpenHeaderRecord Method.
     */
    public Record openHeaderRecord()
    {
        return new MessageInfo(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
        // Link the screen field to the passed in record
        ((ReferenceField)this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageInfoID)).syncReference(this.getHeaderRecord());
        this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageInfoID).addListener(new FieldReSelectHandler(this));
        
        this.getMainRecord().addListener(new CompareFileFilter(MessageProcessInfo.kMessageTypeID, this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageTypeID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageTypeID).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(MessageProcessInfo.kProcessTypeID, this.getScreenRecord().getField(MessageInfoScreenRecord.kProcessTypeID), DBConstants.EQUALS, null, true));
        this.getScreenRecord().getField(MessageInfoScreenRecord.kProcessTypeID).addListener(new FieldReSelectHandler(this));
        
        this.getScreenRecord().getField(MessageInfoScreenRecord.kMessageInfoTypeID).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(MessageInfoScreenRecord.kContactTypeID).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(MessageInfoScreenRecord.kRequestTypeID).addListener(new FieldReSelectHandler(this));
        // This is a little inefficient, but this filter is not used much.
        this.getMainRecord().addListener(new FileFilter(null)
        {
            /**
             * Set up/do the remote criteria.
             * @param strbFilter The SQL query string to add to.
             * @param bIncludeFileName Include the file name with this query?
             * @param vParamList The param list to add the raw data to (for prepared statements).
             * @return True if you should not skip this record (does a check on the local data).
             */
            public boolean doLocalCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
            {
                Record recMessageInfoScreenRecord = getScreenRecord();
                if (recMessageInfoScreenRecord != null)
                    if ((!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kMessageInfoTypeID).isNull())
                        || (!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kContactTypeID).isNull())
                        || (!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kRequestTypeID).isNull()))
                {
                    // Note I move the MessageInfoField to a temp field since the reference is a header filter.
                            recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kMessageInfoCompareID).moveFieldToThis(this.getOwner().getField(MessageProcessInfo.kMessageInfoID));
                            Record recMessageInfo = ((ReferenceField)recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kMessageInfoCompareID)).getReference();
                            if (recMessageInfo != null)
                            {
                                if (!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kMessageInfoTypeID).isNull())
                                    if (!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kMessageInfoTypeID).equals(recMessageInfo.getField(MessageInfo.kMessageInfoTypeID)))
                                        return false;
                                if (!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kContactTypeID).isNull())
                                    if (!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kContactTypeID).equals(recMessageInfo.getField(MessageInfo.kContactTypeID)))
                                        return false;
                                if (!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kRequestTypeID).isNull())
                                    if (!recMessageInfoScreenRecord.getField(MessageInfoScreenRecord.kRequestTypeID).equals(recMessageInfo.getField(MessageInfo.kRequestTypeID)))
                                        return false;
                            }
                        }
                        return super.doLocalCriteria(strbFilter, bIncludeFileName, vParamList);                
                    }
         
                });
    }
    /**
     * Read the current file in the header record given the current detail record.
     */
    public void syncHeaderToMain()
    {
        super.syncHeaderToMain();
        
        this.restoreScreenParam(MessageInfoScreenRecord.kMessageTypeID);
        this.restoreScreenParam(MessageInfoScreenRecord.kProcessTypeID);
        this.restoreScreenParam(MessageInfoScreenRecord.kMessageInfoTypeID);
        this.restoreScreenParam(MessageInfoScreenRecord.kContactTypeID);
        this.restoreScreenParam(MessageInfoScreenRecord.kRequestTypeID);
    }
    /**
     * AddSubFileFilter Method.
     */
    public void addSubFileFilter()
    {
        this.getMainRecord().addListener(new CompareFileFilter(MessageProcessInfo.kMessageInfoID, (BaseField)this.getHeaderRecord().getCounterField(), DBConstants.EQUALS, null, true));
        this.getMainRecord().getField(MessageProcessInfo.kMessageInfoID).addListener(new InitFieldHandler((BaseField)this.getHeaderRecord().getCounterField(), false));
    }
    /**
     * Add the navigation button(s) to the left of the grid row.
     */
    public void addNavButtons()
    {
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        new SCannedBox(this.getNextLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, null, MenuConstants.GRID, MessageInfo.TRANSPORT_DETAIL_SCREEN, application.getResources(ResourceConstants.PRODUCT_RESOURCE, true).getString(MessageInfo.TRANSPORT_DETAIL_SCREEN));
        super.addNavButtons();  // Next buttons will be "First!"
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
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        return new MessageInfoHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(MessageProcessInfo.kMessageProcessInfoFile).getField(MessageProcessInfo.kCode).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageProcessInfo.kMessageProcessInfoFile).getField(MessageProcessInfo.kDescription).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageProcessInfo.kMessageProcessInfoFile).getField(MessageProcessInfo.kQueueNameID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(MessageProcessInfo.kMessageProcessInfoFile).getField(MessageProcessInfo.kProcessorClass).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
