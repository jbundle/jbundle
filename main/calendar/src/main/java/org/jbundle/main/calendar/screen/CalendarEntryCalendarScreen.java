/**
 * @(#)CalendarEntryCalendarScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.calendar.screen;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FreeOnFreeHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BooleanField;
import org.jbundle.base.field.convert.MultipleTableFieldConverter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ResourceConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.CalendarScreen;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.main.calendar.db.AnnivMaster;
import org.jbundle.main.calendar.db.Appointment;
import org.jbundle.main.calendar.db.CalendarEntry;
import org.jbundle.main.calendar.db.CalendarEntryTypeField;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.util.calendarpanel.model.CalendarItem;

/**
 *  CalendarEntryCalendarScreen - Calendar entry screen.
 */
public class CalendarEntryCalendarScreen extends CalendarScreen
{
    /**
     * Default constructor.
     */
    public CalendarEntryCalendarScreen()
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
    public CalendarEntryCalendarScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return "Calendar entry screen";
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new CalendarEntry(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getMainRecord().setKeyArea(CalendarEntry.START_DATE_TIME_KEY);
        BaseField fieldTrue = new BooleanField(null, "FalseField", DBConstants.DEFAULT_FIELD_LENGTH, "FalseField", null);
        fieldTrue.setState(true);
        this.getMainRecord().addListener(new FreeOnFreeHandler(fieldTrue));
        //+this.getMainRecord().addListener(new CompareFileFilter(CalendarEntry.HIDDEN, fieldTrue, "<>", null, true));
        this.setEditing(true);
    }
    /**
     * Add button(s) to the toolbar.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
        super.addToolbarButtons(toolScreen);
        BaseApplication application = (BaseApplication)this.getTask().getApplication();
        new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(CalendarEntryTypeField.APPOINTMENT), CalendarEntryTypeField.APPOINTMENT, CalendarEntryTypeField.APPOINTMENT, null);
        new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, application.getResources(ResourceConstants.MAIN_RESOURCE, true).getString(CalendarEntryTypeField.ANNIVERSARY), CalendarEntryTypeField.ANNIVERSARY, CalendarEntryTypeField.ANNIVERSARY, null);
    }
    /**
     * Get the CalendarItem for this record.
     */
    public CalendarItem getCalendarItem(Rec fieldList)
    {
        return new CalendarEntryItem(this, -1, 0, 1, 2, -1);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        Record record = this.getMainRecord();
        this.addColumn(new MultipleTableFieldConverter(record, CalendarEntry.START_DATE_TIME));
        this.addColumn(new MultipleTableFieldConverter(record, CalendarEntry.END_DATE_TIME));
        this.addColumn(new MultipleTableFieldConverter(record, CalendarEntry.DESCRIPTION));
    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        Record recordMain = null;
        int iDocMode = ScreenConstants.MAINT_MODE;
        boolean bReadCurrentRecord = false;
        Map<String,Object> properties = null;
        if (CalendarEntryTypeField.APPOINTMENT.equalsIgnoreCase(strCommand))
        {
            recordMain = new Appointment(this);
            this.onForm(recordMain, iDocMode, bReadCurrentRecord, iCommandOptions, properties);
            return true;
        }
        if (CalendarEntryTypeField.ANNIVERSARY.equalsIgnoreCase(strCommand))
        {
            recordMain = new AnnivMaster(this);
            this.onForm(recordMain, iDocMode, bReadCurrentRecord, iCommandOptions, properties);
            return true;
        }
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }

}
