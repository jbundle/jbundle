/**
 * @(#)LogicFileScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

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
        Record recClassInfo = this.getRecord(ClassInfo.kClassInfoFile);
        if (recClassInfo != null)
        {
            record.setKeyArea(LogicFile.kSequenceKey);
            SubFileFilter listener = new SubFileFilter(recClassInfo.getField(ClassInfo.kClassName), LogicFile.kMethodClassName, null, -1, null, -1, true);
            record.addListener(listener);
        }
        MainFieldHandler fieldBeh = new MainFieldHandler(DBConstants.MAIN_KEY_AREA);
        this.getMainRecord().getField(LogicFile.kMethodName).addListener(fieldBeh);

    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        Converter converter = null;
        this.getRecord(LogicFile.kLogicFileFile).getField(LogicFile.kMethodName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.kLogicFileFile).getField(LogicFile.kLogicSource).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.kLogicFileFile).getField(LogicFile.kLogicDescription).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        converter = this.getRecord(LogicFile.kLogicFileFile).getField(LogicFile.kMethodReturns);
        converter = new FieldLengthConverter(converter, 50);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, converter, ScreenConstants.DEFAULT_DISPLAY);
        Record query = this.getMainRecord();
            
        BaseField field = query.getField(LogicFile.kMethodInterface);
        Converter converter2 = new FieldLengthConverter(field, 40);
        SEditText screenField = new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, converter2, ScreenConstants.DISPLAY_DESC);
        this.getRecord(LogicFile.kLogicFileFile).getField(LogicFile.kSequence).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.kLogicFileFile).getField(LogicFile.kLogicThrows).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.kLogicFileFile).getField(LogicFile.kProtection).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(LogicFile.kLogicFileFile).getField(LogicFile.kCopyFrom).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
