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
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.screen.control.swing.util.ScreenInfo;
import org.jbundle.base.screen.model.SBaseButton;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;


/**
 * The base class for all buttons and check boxes.
 */
public class VBaseButton extends VScreenField
{
    public static final int DEFAULT_BUTTON_WIDTH = 24;
    public static final int DEFAULT_BUTTON_HEIGHT = 22;

    /**
     * Constructor.
     */
    public VBaseButton()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VBaseButton(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenField model, boolean bEditableControl)
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
     * Override this!
     * @param bEditableControl Is this control editable?
     * @return The physical control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        return null;
    }
    /**
     * Process the command using this view.
     * @param strCommand The command to process.
     * @return True if processed.
     */
    public boolean doCommand(String strCommand)
    {
        if (strCommand != null)
            if (this.getScreenField().getConverter() != null)
                if (this.getScreenField().getConverter().getField() != null)
        {   // This is a special case. This hits the command listeners.
//?            Boolean objValue = Boolean.TRUE;
//?            int iErrorCode = this.setFieldState(objValue, Constants.DISPLAY, Constants.SCREEN_MOVE);
//?            if (iErrorCode == DBConstants.NORMAL_RETURN)
//?                return true;
        }
        return super.doCommand(strCommand); // Not processed, BasePanels and above will override
    }
    /**
     * Calculate the box size.
     * @param ptLocation The location of this control.
     * @return The bounds for this control.
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {
        Dimension itsSize = this.getTextBoxSize(1, ScreenConstants.CHECK_BOX_DESC, 1);
        String tempString = ((SBaseButton)this.getScreenField()).getButtonDesc();
        if ((tempString != null) && (tempString.length() > 0))
            itsSize.width = this.getTextExtent(tempString).width + ScreenInfo.kExtraColSpacing;
        String m_strImageButton = ((SBaseButton)this.getScreenField()).getImageButtonName();
        if (m_strImageButton != null)
        {   // Assume toolbutton size
            if ((tempString == null) || (tempString.length() == 0))
                itsSize.width = DEFAULT_BUTTON_WIDTH;   // 16
            else
                itsSize.width += DEFAULT_BUTTON_WIDTH;      // add 16
            itsSize.height = Math.max(itsSize.height, DEFAULT_BUTTON_HEIGHT);   // 15
        }
        return new Rectangle(ptLocation.x, ptLocation.y, itsSize.width, itsSize.height);
    }
    /**
     *  For the action listener (menu commands).
     * @param evt The action event.
     */
    public void actionPerformed(ActionEvent evt)
    {
        /*int iErrorCode = */this.validateCurrentFocus();   // 1.4 HACK
        int bUseSameWindow = ScreenConstants.USE_SAME_WINDOW | DBConstants.PUSH_TO_BROSWER;
        if ((evt != null)
            && ((evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0))
                bUseSameWindow = ScreenConstants.USE_NEW_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER;
        String strCommand = ((SBaseButton)this.getScreenField()).getButtonCommand();
        if ((this.getScreenField().getConverter() == null) && (strCommand != null))
            this.getScreenField().handleCommand(strCommand, this.getScreenField(), bUseSameWindow);   // Execute this specific command
        else if ((this.getControl() == null) && (strCommand != null))
            this.getScreenField().handleCommand(strCommand, this.getScreenField(), bUseSameWindow);   // Execute this specific command
        else
            super.actionPerformed(evt);
    }
    /**
     * Lost the focus.
     * @param evt The focus event.
     */
    public void focusLost(FocusEvent evt)
    { //    Don't do Object!
        if (this.getScreenField().getConverter() != null)
            if (this.getScreenField().getConverter().getField() != null)
                if (((BaseField)this.getScreenField().getConverter().getField()).isJustModified())
            this.getScreenField().fieldChanged(null); // Lock record if just modified
    }
    /**
     * Do I create a separate control for the description for this type of control.
     * Generally yes, but you may want to override in checkboxes or buttons to include the description in the control.
     * @return True as buttons have the description contained in the control.
     */
    public boolean getSeparateFieldDesc()
    {
        return false; // **Switch this line to use separate desc (labels) for checkboxes**
    }
}
