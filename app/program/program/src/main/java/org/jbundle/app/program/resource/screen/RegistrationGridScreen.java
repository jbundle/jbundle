/**
 * @(#)RegistrationGridScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.resource.screen;

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
import org.jbundle.base.thread.*;
import org.jbundle.main.screen.*;
import org.jbundle.app.program.resource.db.*;

/**
 *  RegistrationGridScreen - .
 */
public class RegistrationGridScreen extends DetailGridScreen
{
    /**
     * Default constructor.
     */
    public RegistrationGridScreen()
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
    public RegistrationGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * RegistrationGridScreen Method.
     */
    public RegistrationGridScreen(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
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
     * OpenHeaderRecord Method.
     */
    public Record openHeaderRecord()
    {
        return new Resource(this);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new Registration(this);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new ResourceScreenRecord(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        // This is all temporary. It will be much better to display the resources next to the key for each record
        this.getScreenRecord().getField(ResourceScreenRecord.kLanguage).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(Registration.kLanguage, this.getScreenRecord().getField(ResourceScreenRecord.kLanguage), DBConstants.EQUALS, null, false));
        this.getMainRecord().getField(Registration.kLanguage).addListener(new InitFieldHandler(this.getScreenRecord().getField(ResourceScreenRecord.kLanguage)));
        this.getScreenRecord().getField(ResourceScreenRecord.kLocale).addListener(new FieldReSelectHandler(this));
        this.getMainRecord().addListener(new CompareFileFilter(Registration.kLocale, this.getScreenRecord().getField(ResourceScreenRecord.kLocale), DBConstants.EQUALS, null, false));
        this.getMainRecord().getField(Registration.kLocale).addListener(new InitFieldHandler(this.getScreenRecord().getField(ResourceScreenRecord.kLocale)));
        
        this.getMainRecord().getField(Registration.kCode).addListener(new InitFieldHandler(this.getHeaderRecord().getField(Resource.kCode)));
    }
    /**
     * Add button(s) to the toolbar.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
        String strJob = null;
        strJob = Utility.addURLParam(strJob, DBParams.TASK, ProcessRunnerTask.class.getName()); // Screen class
        strJob = Utility.addURLParam(strJob, DBParams.PROCESS, WriteResources.class.getName());
        new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, MenuConstants.PRINT, MenuConstants.PRINT, strJob, null);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        Converter converter = new FieldLengthConverter(this.getMainRecord().getField(Registration.kKeyValue), 60);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this,converter, ScreenConstants.DEFAULT_DISPLAY);
        converter = new FieldLengthConverter(this.getMainRecord().getField(Registration.kObjectValue), 60);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this,converter, ScreenConstants.DEFAULT_DISPLAY);
        converter = new FieldLengthConverter(this.getMainRecord().getField(Registration.kCode), 60);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this,converter, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        return new ResourceHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
    }

}
