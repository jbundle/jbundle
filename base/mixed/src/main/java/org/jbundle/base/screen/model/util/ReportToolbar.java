/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.util;

/**
 * @(#)STEView.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.model.MenuConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.thin.base.db.Converter;


/**
 * Set up a tool bar control for a display screen.
 */
public class ReportToolbar extends ToolScreen
{

    /**
     * Constructor.
     */
    public ReportToolbar()
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
    public ReportToolbar(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Controls for a report screen.
     */
    public void setupMiddleSFields()
    {
        new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.DISPLAY);
        new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.PRINT);
    }
}
