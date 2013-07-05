/**
 * @(#)CalendarControlModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.calendar.db;

import org.jbundle.model.db.*;

public interface CalendarControlModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String LAST_UPDATE_DATE = "LastUpdateDate";
    public static final String START_ANNIV_DATE = "StartAnnivDate";
    public static final String END_ANNIV_DATE = "EndAnnivDate";
    public static final String UPDATE_DAYS = "UpdateDays";
    public static final String ANNIV_BACK_DAYS = "AnnivBackDays";
    public static final String ANNIVERSARY_DAYS = "AnniversaryDays";
    public static final String ANNIVERSARY_CATEGORY_ID = "AnniversaryCategoryID";
    public static final String APPOINTMENT_CATEGORY_ID = "AppointmentCategoryID";

    public static final String CALENDAR_CONTROL_FILE = "CalendarControl";
    public static final String THIN_CLASS = "org.jbundle.thin.main.calendar.db.CalendarControl";
    public static final String THICK_CLASS = "org.jbundle.main.calendar.db.CalendarControl";

}
