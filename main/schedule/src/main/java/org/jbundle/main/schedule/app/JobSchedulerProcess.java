/**
 * @(#)JobSchedulerProcess.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.thread.*;
import org.jbundle.main.calendar.db.*;

/**
 *  JobSchedulerProcess - .
 */
public class JobSchedulerProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public JobSchedulerProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public JobSchedulerProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
     * Add the behaviors.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getMainRecord().setKeyArea(CalendarEntry.kStartDateTimeKey);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        CalendarEntry recCalendarEntry = (CalendarEntry)this.getMainRecord();
        recCalendarEntry.close();
        try {
            Date now = new Date();
            Date date = null;
        
            ((Application)this.getTask().getApplication()).getTaskScheduler().addTask(PrivateTaskScheduler.EMPTY_TIMED_JOBS);    // Clear out any jobs queued for a later time
        
            while (recCalendarEntry.hasNext())
            {
                recCalendarEntry.next();
                
                Map<String,Object> properties = recCalendarEntry.getProperties();
                if ((properties == null) || (properties.get(DBParams.PROCESS) == null))
                    continue;   // Not a task
                if (properties.get(CalendarUpdateProcess.TASK_COMPLETED) != null)
                    continue;   // It has already been run
        
                date = ((DateTimeField)recCalendarEntry.getField(CalendarEntry.kStartDateTime)).getDateTime();
                if (date == null)
                    continue;   // Never?
                if ((date.getTime() - now.getTime()) > 0)
                    break;  // The one should be run later
        
                this.runCalendarEntry(properties);  // Queue this job
                // Now, Add a process that will mark this job as done (todo(don) NOTE: This will only work if there is one thread on the task scheduler)
                Map<String,Object> propUpdateTask = new Hashtable<String,Object>();
                propUpdateTask.put(DBParams.PROCESS, CalendarUpdateProcess.class.getName());
                propUpdateTask.put(DBParams.ID, recCalendarEntry.getCounterField().toString());
                this.runCalendarEntry(propUpdateTask);  // Queue this job
            }
            
            if (date != null)
                if ((date.getTime() - now.getTime()) > 0)
            {
                Map<String,Object> propSleepTask = new Hashtable<String,Object>();
                propSleepTask.put(PrivateTaskScheduler.TIME_TO_RUN, date);
                propSleepTask.put(PrivateTaskScheduler.NO_DUPLICATE, Constants.TRUE);   // Being careful
                propSleepTask.put(DBParams.PROCESS, JobSchedulerProcess.class.getName());
                this.runCalendarEntry(propSleepTask);  // Queue this job
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * RunCalendarEntry Method.
     */
    public void runCalendarEntry(Map<String,Object> properties)
    {
        ProcessRunnerTask task = new ProcessRunnerTask(null, null, properties);
        ((Application)this.getTask().getApplication()).getTaskScheduler().addTask(task);
    }

}
