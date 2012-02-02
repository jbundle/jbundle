/**
 * @(#)FieldDataScreen.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.db.*;

/**
 *  FieldDataScreen - .
 */
public class FieldDataScreen extends Screen
{
    /**
     * Default constructor.
     */
    public FieldDataScreen()
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
    public FieldDataScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return new FieldData(this);
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
            record.setKeyArea(FieldData.FIELD_FILE_NAME_KEY);
            SubFileFilter listener = new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), FieldData.FIELD_FILE_NAME, null, null, null, null, true);
            record.addListener(listener);
        }
        MainFieldHandler fieldBeh = new MainFieldHandler(DBConstants.MAIN_KEY_AREA);
        this.getMainRecord().getField(FieldData.FIELD_NAME).addListener(fieldBeh);
        this.getMainRecord().setKeyArea(FieldData.FIELD_FILE_NAME_KEY);
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        this.getRecord(FieldData.kFieldDataFile).getField(FieldData.kFieldName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(FieldData.kFieldDataFile).getField(FieldData.kFieldSeqNo).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(FieldData.kFieldDataFile).getField(FieldData.kBaseFieldName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(FieldData.kFieldDataFile).getField(FieldData.kFieldClass).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        ScreenLocation lastFieldPosition;
        Record query = this.getMainRecord();
        for (int fieldSeq = query.getFieldSeq(FieldData.DEPENDENT_FIELD_NAME); fieldSeq <= query.getFieldSeq(FieldData.FIELD_FILE_NAME); fieldSeq++)
        {
            lastFieldPosition = this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR);
            query.getField(fieldSeq).setupDefaultView(lastFieldPosition, this, ScreenConstants.DISPLAY_DESC); // Add this view to the list
        }
        this.getRecord(FieldData.kFieldDataFile).getField(FieldData.kFieldNotNull).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(FieldData.kFieldDataFile).getField(FieldData.kIncludeScope).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(FieldData.kFieldDataFile).getField(FieldData.kHidden).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
