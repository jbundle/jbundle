/**
 * @(#)CalendarCategoryModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.calendar.db;

import org.jbundle.model.db.*;

public interface CalendarCategoryModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String ICON = "Icon";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CALENDAR_CATEGORY_FILE = "CalendarCategory";
    public static final String THIN_CLASS = "org.jbundle.thin.main.calendar.db.CalendarCategory";
    public static final String THICK_CLASS = "org.jbundle.main.calendar.db.CalendarCategory";

}
