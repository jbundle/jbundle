/**
 * @(#)AppointmentModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.calendar.db;

public interface AppointmentModel extends org.jbundle.model.main.calendar.db.CalendarEntryModel
{
    public static final String START_DATE_TIME = "StartDateTime";
    public static final String END_DATE_TIME = "EndDateTime";

    public static final String APPOINTMENT_FILE = "CalendarEntry";
    public static final String THIN_CLASS = "org.jbundle.thin.main.calendar.db.Appointment";
    public static final String THICK_CLASS = "org.jbundle.main.calendar.db.Appointment";

}
