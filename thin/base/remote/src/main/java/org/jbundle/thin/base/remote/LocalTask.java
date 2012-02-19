/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 *  RemoteTask - The interface to server objects.
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */

/**
 *  RemoteTask - The interface to server objects.
 */
public interface LocalTask extends RemoteBaseSession
{
    /**
     * Set the task that handles messages.
     * This is optional, it keeps remote file messages from being send to the client.
     * @param messageTask The remote message task that handles messages for this remote task.
     */
    public RemoteTask getRemoteMessageTask();
}
