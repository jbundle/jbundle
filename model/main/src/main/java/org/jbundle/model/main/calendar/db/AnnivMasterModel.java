/**
 * @(#)AnnivMasterModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.calendar.db;

import org.jbundle.model.db.*;

public interface AnnivMasterModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String START_DATE_TIME = "StartDateTime";
    public static final String END_DATE_TIME = "EndDateTime";
    public static final String DESCRIPTION = "Description";
    public static final String REPEAT_INTERVAL_ID = "RepeatIntervalID";
    public static final String REPEAT_COUNT = "RepeatCount";
    public static final String CALENDAR_CATEGORY_ID = "CalendarCategoryID";
    public static final String HIDDEN = "Hidden";
    public static final String PROPERTIES = "Properties";

    public static final String START_DATE_TIME_KEY = "StartDateTime";
    public static final String ANNIV_MASTER_SCREEN_CLASS = "org.jbundle.main.calendar.screen.AnnivMasterScreen";
    public static final String ANNIV_MASTER_GRID_SCREEN_CLASS = "org.jbundle.main.calendar.screen.AnnivMasterGridScreen";

    public static final String ANNIV_MASTER_FILE = "AnnivMaster";
    public static final String THIN_CLASS = "org.jbundle.thin.main.calendar.db.AnnivMaster";
    public static final String THICK_CLASS = "org.jbundle.main.calendar.db.AnnivMaster";

}
