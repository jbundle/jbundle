/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.util;

/**
 * @(#)HeaderScreen.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.thin.base.db.Converter;


/**
 * HeaderScreen - Exactly the same as a ToolScreen, but with the layout characteristics of a 'Screen' and
 * all controls are disabled by default.
 */
public class HeaderScreen extends ToolScreen
{
    /**
     * Constructor.
     */
    public HeaderScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public HeaderScreen(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Add all listeners to the screen.
     * For A header screen, disable all controls.
     */
    public void addListeners()
    {
        super.addListeners();
        this.setEnabled(false);     // Disable all controls
    }
    /**
     * Code this position and Anchor to add it to the LayoutManager.
     * @param position The position of the next location (row and column).
     * @param setNewAnchor Set anchor?
     * @return The new screen location constant.
     */
    public ScreenLocation getNextLocation(short position, short setNewAnchor)
    {
        if (position == ScreenConstants.FIRST_LOCATION)
            position = ScreenConstants.FIRST_INPUT_LOCATION;
        if (position == ScreenConstants.NEXT_LOGICAL)
            position = ScreenConstants.BELOW_LAST_ANCHOR;
        return super.getNextLocation(position, setNewAnchor);
    }
}
