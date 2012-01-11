/**
 * @(#)CalendarEntryModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.calendar.db;

public interface CalendarEntryModel extends org.jbundle.model.db.Rec
{
    public static final String CALENDAR_ENTRY_TYPE_ID = "CalendarEntryTypeID";
    public static final String DESCRIPTION = "Description";
    public static final int ANNIVERSARY_ID = 2;
    public static final int APPOINTMENT_ID = 1;

    public static final String CALENDAR_ENTRY_FILE = "CalendarEntry";
    public static final String THIN_CLASS = "org.jbundle.thin.main.calendar.db.CalendarEntry";
    public static final String THICK_CLASS = "org.jbundle.main.calendar.db.CalendarEntry";

}
