/**
 * @(#)CalendarUpdateProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.schedule.app;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.thread.*;
import org.jbundle.main.calendar.db.*;

/**
 *  CalendarUpdateProcess - Update this calendar entry to say that it ran..
 */
public class CalendarUpdateProcess extends BaseProcess
{
    public static final String TASK_COMPLETED = "taskCompleted";
    /**
     * Default constructor.
     */
    public CalendarUpdateProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CalendarUpdateProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        return new CalendarEntry(this);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        String strID = this.getProperty(DBParams.ID);
        if ((strID != null) && (strID.length() > 0))
        {
            Record recCalendarEntry = this.getMainRecord();
            recCalendarEntry.getCounterField().setString(strID);
            try {
                if (recCalendarEntry.seek(null))
                {
                    recCalendarEntry.edit();
                    boolean[] rgbEnabled = recCalendarEntry.setEnableListeners(false);  // I need to do this since a change in properties will reschedule the jobs.
                    ((PropertiesField)recCalendarEntry.getField(CalendarEntry.kProperties)).setProperty(TASK_COMPLETED, DBConstants.TRUE);
                    recCalendarEntry.setEnableListeners(rgbEnabled);
                    recCalendarEntry.set();
                }
            } catch (DBException ex) {
                ex.printStackTrace();
            }
        }
    }

}
