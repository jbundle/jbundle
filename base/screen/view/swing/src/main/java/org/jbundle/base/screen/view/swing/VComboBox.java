/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComboBox;

import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.control.swing.util.ScreenInfo;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;


/**
 * A screen combo box.
 */
public class VComboBox extends VPopupBox
{
    /**
     * Constructor.
     */
    public VComboBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VComboBox(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
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
     * Create the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        JComboBox control = (JComboBox)super.setupControl(bEditableControl);
        control.setEditable(true);
        return control;
    }
    /**
     * Calculate the box size.
     * @param ptLocation The location of the control in the parent.
     * @return The control's bounds.
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {
        String tempString;
        int maxLength = 0;
        short index = 0;
        for (index = 0; index < 10; index++)
        {
            tempString = this.getScreenField().getConverter().convertIndexToDisStr(index);
            if (index > 0) if ((tempString == null) || (tempString.length() == 0))
                break;      // Far Enough
            maxLength = Math.max((short)maxLength, tempString.length());
        }
        if (this.getScreenField().getConverter() != null)
            maxLength = Math.max(maxLength, this.getScreenField().getConverter().getMaxLength());
        if (maxLength < 15)
            maxLength = 15;
        Dimension itsSize = this.getTextBoxSize(maxLength, ScreenConstants.POPUP_DESC, 1);
        itsSize.width += ScreenInfo.EXTRA_COL_SPACING;
        return new Rectangle(ptLocation.x , ptLocation.y, itsSize.width, itsSize.height);
    }
}
