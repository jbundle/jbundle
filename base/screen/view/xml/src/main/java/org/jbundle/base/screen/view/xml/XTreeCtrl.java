/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;

/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class XTreeCtrl extends XBaseScreen
{

    /**
     * Constructor.
     */
    public XTreeCtrl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XTreeCtrl(ScreenField model,boolean bEditableControl)
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
     * Get the rich text type for this control.
     * @return The type (Such as HTML, Date, etc... very close to HTML control names).
     */
    public String getInputType(String strViewType)
    {
        if (ScreenModel.DOJO_TYPE.equalsIgnoreCase(strViewType))
            return "Tree";
        return super.getInputType(strViewType);
    }
}
