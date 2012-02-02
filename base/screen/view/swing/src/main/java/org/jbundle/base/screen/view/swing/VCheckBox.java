/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.SCheckBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;


/**
 * Implements a standard check box.
 */
public class VCheckBox extends VToggleButton
{
    /**
     * Constructor.
     */
    public VCheckBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VCheckBox(ScreenField model, boolean bEditableControl)
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
        if (this.getControl(DBConstants.CONTROL_TO_FREE) != null)
            if (m_bEditableControl)
        {
            this.getControl(DBConstants.CONTROL_TO_FREE).removeFocusListener(this);
            ((JCheckBox)this.getControl(DBConstants.CONTROL_TO_FREE)).removeActionListener(this);
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control (A JCheckBox).
     */
    public Component setupControl(boolean bEditableControl)
    {
        String strDesc = ((SCheckBox)this.getScreenField()).getButtonDesc();
        JCheckBox control = null;
        String m_strImageButton = ((SCheckBox)this.getScreenField()).getImageButtonName();
        if ((strDesc == null) && (m_strImageButton == null))
            control = new JCheckBox();  // Physical control
        else if (m_strImageButton == null)
            control = new JCheckBox(strDesc); // Physical control
        else if ((strDesc == null) || (strDesc.length() == 0))
            control = new JCheckBox(this.loadImageIcon(m_strImageButton, null));    // Get this image, then redisplay me when you're done
        else
            control = new JCheckBox(strDesc, this.loadImageIcon(m_strImageButton, null)); // Get this image, then redisplay me when you're done
        control.setOpaque(false);
        control.setHorizontalTextPosition(JToggleButton.LEFT);
        control.setIconTextGap(org.jbundle.base.screen.control.swing.util.ScreenInfo.EXTRA_COL_SPACING);
        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addActionListener(this);
            control.addKeyListener(this); // Required for tab and shift-tab
        }
        return control;
    }
    /**
     * Calculate the physical box size.
     * @param ptLocation The location of this control.
     * @return The bounds for this control.
     */
    public Rectangle calcBoxShape(Point descLocation)
    {
        String strDesc = ((SCheckBox)this.getScreenField()).getButtonDesc();
        if (strDesc == null)
            strDesc = DBConstants.BLANK;
//        Dimension itsSize = this.getTextBoxSize(1, ScreenConstants.CHECK_BOX_DESC, 1);
        Dimension itsSize = this.getTextBoxSize(strDesc.length(), ScreenConstants.CHECK_BOX_DESC, 1);
        return this.shiftBoxLeft(new Rectangle(descLocation.x, descLocation.y, itsSize.width, itsSize.height));     
    }
}
