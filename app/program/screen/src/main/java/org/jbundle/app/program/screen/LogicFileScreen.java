/**
 * @(#)LogicFileScreen.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

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
import org.jbundle.app.program.db.*;

/**
 *  LogicFileScreen - .
 */
public class LogicFileScreen extends Screen
{
    /**
     * Default constructor.
     */
    public LogicFileScreen()
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
    public LogicFileScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new LogicFile(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        Record record = this.getMainRecord();
        Record recClassInfo = this.getRecord(ClassInfo.CLASS_INFO_FILE);
        if (recClassInfo != null)
        {
            record.setKeyArea(LogicFile.SEQUENCE_KEY);
            SubFileFilter listener = new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), LogicFile.METHOD_CLASS_NAME, null, null, null, null, true);
            record.addListener(listener);
        }
        MainFieldHandler fieldBeh = new MainFieldHandler(null);
        this.getMainRecord().getField(LogicFile.METHOD_NAME).addListener(fieldBeh);

    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        Converter converter = null;
        this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.METHOD_NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.LOGIC_SOURCE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.LOGIC_DESCRIPTION).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        converter = this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.METHOD_RETURNS);
        converter = new FieldLengthConverter(converter, 50);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, converter, ScreenConstants.DEFAULT_DISPLAY);
        Record query = this.getMainRecord();
            
        BaseField field = query.getField(LogicFile.METHOD_INTERFACE);
        Converter converter2 = new FieldLengthConverter(field, 40);
        SEditText screenField = new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, converter2, ScreenConstants.DISPLAY_DESC);
        this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.SEQUENCE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.LOGIC_THROWS).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.PROTECTION).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.COPY_FROM).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.LOGIC_FILE_FILE).getField(LogicFile.INCLUDE_SCOPE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
