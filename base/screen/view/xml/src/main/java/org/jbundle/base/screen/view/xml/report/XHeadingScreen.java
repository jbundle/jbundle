/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.xml.XBaseScreen;
import org.jbundle.model.screen.ScreenComponent;

/**
 * A Report heading screen.
 */
public class XHeadingScreen extends XBaseScreen
{

    /**
     * Constructor.
     */
    public XHeadingScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XHeadingScreen(ScreenField model, boolean bEditableControl)
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
}
