/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.thread;

/**
 * @(#)PrivateTaskScheduler.java    1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import org.jbundle.model.App;


/**
 * Schedules jobs to run in the background (under different tasks).
 * WARNING - Currently KeepAlive Threads can only be used for private PrivateTaskSchedulers-they don't work for the pooled threads.
 */
public class PrivateTaskScheduler extends TaskScheduler
{
    public static final String END_OF_JOBS = "?endofjobs";
    public static final String EMPTY_TIMED_JOBS = "?emptyjobs";   // Clear out all the waiting jobs
    public static final String CLEAR_JOBS = "?clearjobs";           // Clear out all the unrun jobs on the queue

    public static final String TIME_TO_RUN = "timeToRun";
    public static final String NO_DUPLICATE = "noDuplicate";
    /**
     * List of my private jobs.
     * NOTE: A private job scheduler is used to create a single task that processes jobs in FIFO order (Usually to synchronize calls to an EJB session).
     */
    protected Vector<Object> m_vPrivateJobs = null;
    /**
     * Keep the task alive after execution.
     */
    protected boolean m_bKeepAlive = true;
    /**
     * Thread is currently suspended (Only for KeepAlive threads!).
     */
    protected boolean m_bThreadSuspended = false;

    /**
     * Constructor.
     */
    public PrivateTaskScheduler()
    {
        super();
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param iMaxThreads The maximum number of threads to run (-1 = default).
     * @param bKeepAlive Keep the task alive after execution.
     */
    public PrivateTaskScheduler(App application, int iMaxThreads, boolean bKeepAlive)
    {
        this();
        this.init(application, iMaxThreads, bKeepAlive);
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param iMaxThreads The maximum number of threads to run (-1 = default).
     * @param bKeepAlive Keep the task alive after execution.
     */
    public void init(App application, int iMaxThreads, boolean bKeepAlive)
    {
        iMaxThreads = 0;
        super.init(application, iMaxThreads);
        m_vPrivateJobs = new Vector<Object>();      // List of my private jobs
        m_bKeepAlive = bKeepAlive;          // Keep the task alive after execution.
        if (m_bKeepAlive)
            new Thread(this, "KeepAliveThread").start();
    }
    /**
     * Free/Kill this task.
     */
    public void free()
    {
        if (m_bKeepAlive)
        {
            m_bKeepAlive = false; // Don't stay alive
            m_bThreadSuspended = false;   // Unsuspend this puppy
            try {
                this.notify();          // If you're waiting, continue (and the thread will die)
            } catch (IllegalMonitorStateException ex) {
                // Ignore
            }
        }
        super.free();
    }
    /**
     * Add a job to the queue.
     * @param objJobDef Can be a AutoTask object, or a string describing the job to run (in the overriding class)
     */
    public void addTask(Object objJobDef)
    {
        synchronized (this)
        {
            if (objJobDef == CLEAR_JOBS)
            {   // Dump all waiting jobs
                m_vPrivateJobs.removeAllElements();
                return;
            }
            m_vPrivateJobs.addElement(objJobDef);
            if (m_bThreadSuspended)
            {
                synchronized(this)
                {
                    m_bThreadSuspended = false;   // Unsuspend this puppy
                }
                this.notify();              // Notify the job processor to continue
            }
        }
        if (objJobDef == END_OF_JOBS)
        	Thread.yield();		// Give this task a chance to exit.
    }
    /**
     * Run this thread.
     */
    public void run()
    {
        Object objJobDef = null;
        while (m_bKeepAlive)
        {   // Only loop, if this thread should be kept alive.
            Date earliestDate = null;
            Vector<AutoTask> vJobsToRunLater = null;
            while ((objJobDef = this.getNextJob()) != null)     // Get and remove next job
            { // Until end of jobs
                if (EMPTY_TIMED_JOBS == objJobDef)
                {
                    vJobsToRunLater = null; // Clear everything up to this job
                    earliestDate = null;
                }
                else if (END_OF_JOBS == objJobDef)
                    m_bKeepAlive = false; // Special - end of Jobs message, quit this thread
                else
                {
                    if (objJobDef instanceof AutoTask)
                        if (((AutoTask)objJobDef).getProperties() != null)
                    {
                        Date date = (Date)((AutoTask)objJobDef).getProperties().get(TIME_TO_RUN);
                        if (date != null)
                        {
                            Date now = new Date();
                            if (date.after(now))
                            {   // Don't run this one until later
                                if ((earliestDate == null)
                                    || (earliestDate.after(date)))
                                        earliestDate = date;
                                if (vJobsToRunLater == null)
                                    vJobsToRunLater = new Vector<AutoTask>();
                                this.addJobToRunLater(vJobsToRunLater, (AutoTask)objJobDef);
                                continue;
                            }
                        }
                    }
                    if (objJobDef instanceof AutoTask)
                        if (((AutoTask)objJobDef).getApplication() == null)
                            ((AutoTask)objJobDef).setApplication(this.getApplication());
                    this.runThread(objJobDef);
                }
            }
            long lWaitTime = 0;
            if (earliestDate != null)
            {
                lWaitTime = earliestDate.getTime() - new Date().getTime();
                if (lWaitTime <= 0)
                    lWaitTime = 1;  // Don't wait (long) there is a task that is ready to go now.
            }
            // No more tasks to process, either stop, or go to sleep
            if (m_bKeepAlive)
            {
                synchronized(this)
                {
                    m_bThreadSuspended = true;  // Suspend this puppy
                    try   {
                        while (m_bThreadSuspended)
                        {
                            wait(lWaitTime);     // Wait for notification that I can continue
                            if (lWaitTime != 0)
                            {
                                m_bThreadSuspended = false;     // I was waiting for the time to expire.
                                m_vPrivateJobs.addAll(vJobsToRunLater);     // Add all these jobs back
                            }
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * Add this job to this list (If there is a duplicate job, add the first to execute).
     * @param vJobsToRunLater
     * @param jobToAdd
     */
    public void addJobToRunLater(Vector<AutoTask> vJobsToRunLater, AutoTask jobToAdd)
    {
        if (jobToAdd.getProperty(NO_DUPLICATE) != null)
        {
            Date timeJobToAdd = (Date)jobToAdd.getProperties().get(TIME_TO_RUN);
            for (int iIndex = 0; iIndex < vJobsToRunLater.size(); iIndex++)
            {
                AutoTask jobAtIndex = vJobsToRunLater.elementAt(iIndex);
                
                if (this.sameJob(jobAtIndex, jobToAdd))
                {
                    Date timeJobAtIndex = (Date)jobAtIndex.getProperties().get(TIME_TO_RUN);
                    if (timeJobAtIndex.getTime() > timeJobToAdd.getTime())
                    {
                        jobAtIndex.free();  // Being careful (this may have been added to application)
                        vJobsToRunLater.setElementAt(jobToAdd, iIndex); // This job is earlier, replace the one in the list
                    }
                    else
                        jobToAdd.free();
                    return;    // Either way, this duplicate has been resolved
                }
            }
        }
        // No match, add this one to the end.
        vJobsToRunLater.add(jobToAdd);
    }
    /**
     * Do these two jobs match?
     * @param propJobAtIndex
     * @param propJobToAdd
     * @return
     */
    public boolean sameJob(AutoTask jobAtIndex, AutoTask jobToAdd)
    {
        Map<String, Object> propJobAtIndex = jobAtIndex.getProperties();
        Map<String, Object> propJobToAdd = jobToAdd.getProperties();

        if (propJobAtIndex.size() != propJobToAdd.size())
            return false;
        boolean bSameJob = false;
        // They are equal if every property (except time) match
        for (String strNewKey : propJobToAdd.keySet())
        {
            if (!strNewKey.equalsIgnoreCase(TIME_TO_RUN))
            {
                if (propJobAtIndex.get(strNewKey).equals(propJobToAdd.get(strNewKey)))
                    bSameJob = true;    // Okay, this is a potential match
                else
                    return false;   // This property doesn't match, stop compare
            }
        }
        return bSameJob;
    }
    /**
     * Pull the next job off the queue and remove it.
     * @return The next job.
     */
    public synchronized Object getNextJob()
    {
        Object strJobDef = null;
        synchronized (this)
        {
            if (m_vPrivateJobs.size() > 0)
                strJobDef = m_vPrivateJobs.remove(0);
        }
        return strJobDef;
    }
    /**
     * After this job, quit this thread.
     * Add an end-of-job task to this scheduler.
     */
    public void endOfJobs()
    {
        this.addTask(END_OF_JOBS);
    }
}
