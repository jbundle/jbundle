/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model;

import java.io.InputStream;
import java.util.Map;


/**
 * An independent thread.
 */
public interface Task
    extends RecordOwnerParent
{
    /**
     * Get this task's parent application object.
     * @return this task's application parent.
     */
    public App getApplication();
    /**
     * This is task's parent application.
     * @param application This task's application.
     */
    public void setApplication(App application);
    /**
     * This is a special method that runs some code when this screen is opened as a task.
     */
    public void run();
    /**
     * This is a special method that stops the code when this screen is opened as a task.
     */
    public void stopTask();
    /**
     * This is a special method that stops the code when this screen is opened as a task.
     */
    public void free();
    /**
     * Is this task currently involved in computations?
     * @return True if the task is currently active.
     */
    public boolean isRunning();
    /**
     * If this task object was created from a class name, call init(xxx) for the task.
     * You may want to put logic in here that checks to make sure this object was not already inited.
     * Typically, you init a Task object and pass it to the job scheduler. The job scheduler
     * will check to see if this task is owned by an application... if not, initTask() is called.
     * @param application This task's application.
     * @param properties This task's initial properties.
     */
    public void initTask(App application, Map<String, Object> properties);
    /**
     * Get the last error code for this task.
     * @param iErrorCode It must match the iErrorCode, or pass a 0 to get the last error.
     * @return The error text.
     */
    public String getLastError(int iErrorCode);
    /**
     * Set the last error string for this task.
     * @param strError The error text to save for later display.
     * @return an errorcode that has been associated with this error code.
     */
    public int setLastError(String strError);
    /**
     * Display this status message in the status box or at the bottom of the browser.
     * Displays at the "information" level.
     * @param strStatus The text to display/set.
     */
    public void setStatusText(String strStatus);
    /**
     * Display this status message in the status box or at the bottom of the browser.
     * @param strStatus The text to display/set.
     * @param iWarningLevel The warning level of this message.
     */
    public void setStatusText(String strStatus, int iWarningLevel);
    /**
     * Get the last status message if it is at this level or above.
     * Typically you do this to see if the current message you want to display can
     * be displayed on top of the message that is there already.
     * Calling this method will clear the last status text.
     * @param iWarningLevel The maximum warning level to retrieve.
     * @return The current message if at this level or above, otherwise return null.
     */
    public String getStatusText(int iWarningLevel);
    /**
     * Get the remote server for this task.
     * @return The server.
     */
    public Object getRemoteTask();
    /**
     * Convert this string to a local string.
     * @param strKey The string to convert.
     * @return The local version of the key.
     */
    public String getString(String strKey);
    /**
     * A utility method to get an Input stream from a filename or URL string.
     * @param strFilename The filename or url to open as an Input Stream.
     * @return The imput stream (or null if there was an error).
     */
    public InputStream getInputStream(String strFilename);
    /**
     * Can this task be the main task?
     * @return true If it can.
     */
    public boolean isMainTaskCandidate();
    /**
     * Get the default lock strategy to use for this type of table.
     * @return The lock strategy.
     */
    public int getDefaultLockType(int iDatabaseType);
}
