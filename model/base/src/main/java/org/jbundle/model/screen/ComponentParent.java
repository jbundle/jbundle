/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.screen;

import org.jbundle.model.RecordOwnerParent;


/**
 * FieldComponent.java
 *
 * Created on November 9, 2000, 2:31 AM
 */

/** 
 * ScreenComponent is a simple interface that allows a screen control
 * to get it's connection to the field information.
 * @author  Administrator
 * @version 1.0.0
 */
public interface ComponentParent extends ScreenComponent, RecordOwnerParent
{
    /**
     * Get the top level screen.
     * @return The top level screen.
     */
    public ComponentParent getRootScreen();
}
