/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.screen;

import org.jbundle.model.PropertyOwner;
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
     * Number of Screen Fields in this screen.
     * @return screen field count.
     */
    public int getSFieldCount();
    /**
     * Get the SField at this index.
     * @param index location of the screen field.
     * @return The screen field at this location.
     */
    public ScreenComponent getSField(int index);
    /**
     * Code this position and Anchor to add it to the LayoutManager.
     * @param position The position of the next location (see the position constants).
     * @param setNewAnchor Set anchor?
     * @return The new screen location constant.
     */
    public ScreenLoc getNextLocation(short position, short setNewAnchor);
    /**
     * Push this command onto the history stack.
     * @param strHistory The history command to push onto the stack.
     */
    public void pushHistory(String strHistory, boolean bPushToBrowser);
    /**
     * Pop this command off the history stack.
     * NOTE: Do not use this method in most cases, use the method in BaseApplet.
     * @return The history command on top of the stack.
     */
    public String popHistory(int quanityToPop, boolean bPushToBrowser);
    /**
     * Get the view subpackage name (such as swing/xml/etc.).
     * @return the subpackage name.
     */
    public String getViewType();
    /**
     * Supply the title for this screen.
     * @return the screen title.
     */
    public String getTitle();
    /**
     * Get the key that holds all the user defaults for this screen.
     *  Format:   \Software\tourgeek.com\(This screen name)\(The screen's main file)
     *  ie., \Software\tourgeek.com\GridScreen\AmAffil.
     * @return The property owner.
     */
    public PropertyOwner retrieveUserProperties();
    /**
     * Is this report in data entry mode or print report mode?
     * <p>Note: This does not maintain an independent variable, I fake that the screen fields
     * have been modified, because this signals the report module to print.
     * @return True if this is a report.
     */
    public boolean isPrintReport();
}
