/*
 * 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.thread;

/**
 * @author don <don@donandann.com>
 * This class is a platform-neutral implementation of SwinSyncPageWorker that
 * guarantees a page has displayed before doing a compute-intensive task.
 */
public abstract class SyncNotifyAdapter extends Object
    implements SyncNotify
{
    SyncWorker syncWorker = null;

    abstract public void done();
    
    /**
     * Set my sync worker.
     * @param strKey
     * @return
     */
    public void setSyncWorker(SyncWorker syncWorker)
    {
        this.syncWorker = syncWorker;
    }
    /**
     * Get saved property.
     * @param strKey
     * @return
     */
    public Object get(String key)
    {
        if (syncWorker != null)
            return syncWorker.get(key);
        return null;
    }
}
