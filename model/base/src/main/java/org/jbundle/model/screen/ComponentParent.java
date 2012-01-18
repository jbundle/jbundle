/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.screen;

import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.db.Rec;


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
     * Get the main record for this screen.
     * @return The main record.
     */
    public Rec getMainRecord();
    /**
     * Get the top level screen.
     * @return The top level screen.
     */
    public ComponentParent getRootScreen();
    /**
     * Code this position and Anchor to add it to the LayoutManager.
     * @param position The position of the next location (see the position constants).
     * @param setNewAnchor Set anchor?
     * @return The new screen location constant.
     */
    public ScreenLoc getNextLocation(short position, short setNewAnchor);
}
