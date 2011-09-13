/*
 * 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.print.thread;

/**
 * @author don
 *
 */
public interface SyncPage {

    boolean isPaintCalled();
    
    void setPaintCalled(boolean bPaintCalled);
    
}
