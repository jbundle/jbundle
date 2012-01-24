/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)CalendarScreen.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Date;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.calendar.CalendarRecordItem;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.util.calendarpanel.model.CalendarModel;


/**
 * The window for displaying the record in a calendar window.
 */
public class CalendarScreen extends BaseGridTableScreen
{

    /**
     * Constructor.
     */
    public CalendarScreen()
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
     */
    public CalendarScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Open the files and setup the screen.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record mainRecord, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {

        super.init(mainRecord, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        
        this.setAppending(false);
        
        this.resizeToContent(this.getTitle());  //?
    }
    /**
     * Free this screen's resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set up this default screen field (default = set them all up for the current record).
     * @param converter The converter to creat a default screen field for.
     */
    public Object addColumn(Converter converter)
    {
        return new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, converter, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * The title for this screen.
     * @return The title for this screen.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    { // This is almost always overidden!
        String windowName = "Calendar"; // Default
        Record query = this.getMainRecord();
        if (query != null)
            windowName = query.getRecordName() + " Calendar";
        return windowName;
    }
    /**
     * Set up the screen fields (default = set them all up for the current record).
     * Not used.
     */
    public void setupSFields()
    {   // Set up the screen fields
        // Not used
    }
    /**
     * Get the calendar model from the overriding class.
     * @return The (new) calendar model.
     */
//    public CalendarModel setupCalendarModel()
  //  {
    //    return null;    // Override this
    //}
    /**
     * Get the model.
     */
    public CalendarItem getCalendarItem(Rec fieldList)
    {
        return new CalendarRecordItem(this);
    }
    /**
     * Get the calendar model from the overriding class.
     * @return The (new) calendar model.
     */
    public CalendarModel setupCalendarModel()
    {
        return null;    // Override to supply a different mode
    }
    /**
     * Start date for the calendar; return null to automatically set from the model.
     * @return The start date.
     */
    public Date getStartDate()
    {
        return null;    // Override this to supply a start date
    }
    /**
     * Start date for the calendar; return null to automatically set from the model.
     * @return The start date.
     */
    public Date getEndDate()
    {
        return null;    // Override this to supply a start date
    }
    /**
     * Initial select date for the calendar; return null to automatically set from the model.
     * @return The start date.
     */
    public Date getSelectDate()
    {
        return null;    // Override this to supply a start date
    }
}
