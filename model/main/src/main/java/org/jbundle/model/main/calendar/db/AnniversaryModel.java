/**
 * @(#)AnniversaryModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.calendar.db;

public interface AnniversaryModel extends org.jbundle.model.main.calendar.db.CalendarEntryModel
{
    public static final String START_DATE_TIME = "StartDateTime";
    public static final String END_DATE_TIME = "EndDateTime";
    public static final String ANNIV_MASTER_ID = "AnnivMasterID";

    public static final String ANNIVERSARY_FILE = "CalendarEntry";
    public static final String THIN_CLASS = "org.jbundle.thin.main.calendar.db.Anniversary";
    public static final String THICK_CLASS = "org.jbundle.main.calendar.db.Anniversary";

}
