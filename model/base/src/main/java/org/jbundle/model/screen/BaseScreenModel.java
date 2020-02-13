/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.screen;

import java.util.Properties;

import org.jbundle.model.Freeable;
import org.jbundle.model.db.Rec;


public interface BaseScreenModel extends Freeable {

    /**
     * Get the the base applet that is the parent of this screen.
     * @return The parent BaseApplet (or null).
     */
    public BaseAppletReference getBaseApplet();
    
    /**
     * Add a message handler for this record.
     * @param record The record to follow.
     * @return The message listener.
     */
    public Object addMessageHandler(Rec record, Properties properties);
    
}
