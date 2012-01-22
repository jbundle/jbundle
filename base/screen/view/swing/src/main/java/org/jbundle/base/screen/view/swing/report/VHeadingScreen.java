/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.swing.VBaseScreen;
import org.jbundle.model.screen.ScreenComponent;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class VHeadingScreen extends VBaseScreen
{

    /**
     * Constructor.
     */
    public VHeadingScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public VHeadingScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set up the physical control (that implements Component).
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        return null;
    }
}
