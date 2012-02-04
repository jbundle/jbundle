/**
 * @(#)CalendarEntryModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.calendar.db;

import org.jbundle.model.db.*;

public interface CalendarEntryModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CALENDAR_ENTRY_TYPE_ID = "CalendarEntryTypeID";
    public static final String START_DATE_TIME = "StartDateTime";
    public static final String END_DATE_TIME = "EndDateTime";
    public static final String DESCRIPTION = "Description";
    public static final String CALENDAR_CATEGORY_ID = "CalendarCategoryID";
    public static final String HIDDEN = "Hidden";
    public static final String PROPERTIES = "Properties";
    public static final String ANNIV_MASTER_ID = "AnnivMasterID";

    public static final String START_DATE_TIME_KEY = "StartDateTime";

    public static final String ANNIV_MASTER_ID_KEY = "AnnivMasterID";

    public static final String CALENDAR_CATEGORY_ID_KEY = "CalendarCategoryID";
    public static final int ANNIVERSARY_ID = 2;
    public static final int APPOINTMENT_ID = 1;
    public static final String JOB_QUEUE_NAME = "jobSchedulerQueue";

    public static final String CALENDAR_ENTRY_FILE = "CalendarEntry";
    public static final String THIN_CLASS = "org.jbundle.thin.main.calendar.db.CalendarEntry";
    public static final String THICK_CLASS = "org.jbundle.main.calendar.db.CalendarEntry";

}
