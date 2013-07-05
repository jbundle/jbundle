/**
 * @(#)AppointmentModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.calendar.db;

import org.jbundle.model.main.calendar.db.*;

public interface AppointmentModel extends CalendarEntryModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String CALENDAR_ENTRY_TYPE_ID = CALENDAR_ENTRY_TYPE_ID;
    //public static final String START_DATE_TIME = START_DATE_TIME;
    //public static final String END_DATE_TIME = END_DATE_TIME;
    //public static final String DESCRIPTION = DESCRIPTION;
    //public static final String CALENDAR_CATEGORY_ID = CALENDAR_CATEGORY_ID;
    //public static final String HIDDEN = HIDDEN;
    //public static final String PROPERTIES = PROPERTIES;
    //public static final String ANNIV_MASTER_ID = ANNIV_MASTER_ID;
    public static final String APPOINTMENT_SCREEN_CLASS = "org.jbundle.main.calendar.screen.AppointmentScreen";
    public static final String APPOINTMENT_GRID_SCREEN_CLASS = "org.jbundle.main.calendar.screen.AppointmentGridScreen";

    public static final String APPOINTMENT_FILE = "CalendarEntry";
    public static final String THIN_CLASS = "org.jbundle.thin.main.calendar.db.Appointment";
    public static final String THICK_CLASS = "org.jbundle.main.calendar.db.Appointment";

}
