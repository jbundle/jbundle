/*
 * 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.thread;

import java.util.Map;

/**
 * @author don <don@donandann.com>
 * This class is a platform-neutral implementation of SwinSyncPageWorker that
 * guarantees a page has displayed before doing a compute-intensive task.
 */
public interface SyncWorker extends Runnable {
    
    public void init(SyncPage p, SyncNotify syncNotify, Runnable swingPageLoader, Map<String,Object> map, boolean bManageCursor);
    
    /**
     * Get saved property.
     * @param strKey
     * @return
     */
    public Object get(String key);
    /**
     * Causes this thread to begin execution; the Java Virtual Machine 
     * calls the <code>run</code> method of this thread. 
     */
    public void start();
}
