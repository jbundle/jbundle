/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SStaticText.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * Static text box.
 * This is the same as an edit box, but you can't edit it.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SStaticText extends SEditText
{

    /**
     * Constructor.
     */
    public SStaticText()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param displayString The static string to display.
     */
    public SStaticText(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
        m_bIsFocusTarget = false; // Can't tab to a static item
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param displayString The static string to display.
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
     * Is this field Enabled or disabled?
     * SStatic text is always disabled (unable to edit!).
     * @return False (disabled).
     */
    public boolean isEnabled()
    {
        return false;
    }
}
