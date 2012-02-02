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
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JToggleButton;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.screen.model.SToggleButton;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;


/**
 * Implements a standard toggle button.
 * Each time you press a toggle button, the converter's value is flipped.
 */
public class VToggleButton extends VBaseButton
{

    /**
     * Constructor.
     */
    public VToggleButton()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VToggleButton(ScreenField model, boolean bEditableControl)
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
            ((JToggleButton)this.getControl(DBConstants.CONTROL_TO_FREE)).removeActionListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Create the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        String strDesc = ((SToggleButton)this.getScreenField()).getButtonDesc();
        JToggleButton control = null;
        String m_strImageButton = ((SToggleButton)this.getScreenField()).getImageButtonName();
        if (m_strImageButton == null)
            control = new JToggleButton(strDesc); // Physical control
        else if ((strDesc == null) || (strDesc.length() == 0))
            control = new JToggleButton(this.loadImageIcon(m_strImageButton, null));    // Get this image, then redisplay me when you're done
        else
            control = new JToggleButton(strDesc, this.loadImageIcon(m_strImageButton, null)); // Get this image, then redisplay me when you're done
        control.setHorizontalTextPosition(JToggleButton.LEFT);
        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addActionListener(this);
        }
        control.setOpaque(false);
        return control;
    }
    /**
     * Calculate the box size.
     * @param ptLocation The location of the control in the parent.
     * @return The control's bounds.
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {
        Rectangle rect = super.calcBoxShape(ptLocation);
        rect.width += DEFAULT_BUTTON_WIDTH;     // **HACK**
        return rect;
    }
    /**
     * Shift this box left by the size of the string.
     * @param rect The current bounds.
     * @return The new bounds.
     */
    public Rectangle shiftBoxLeft(Rectangle rect)
    {
        String tempString = ((SToggleButton)this.getScreenField()).getButtonDesc();
        int iTextWidth = 0;
        if (tempString != null)
            iTextWidth = this.getTextExtent(tempString).width;
//        Dimension descSize = this.getTextBoxSize(ScreenConstants.MAX_DESC_LENGTH, ScreenConstants.FIELD_DESC, 1);
//        int sizeFix = descSize.width - iTextWidth;
//        if (tempString != null)
//            rect.x += -descSize.width + sizeFix;
        rect.x -= iTextWidth + org.jbundle.base.screen.control.swing.util.ScreenInfo.EXTRA_COL_SPACING;
//        rect.width -= sizeFix;
        if (rect.x < 0)
            rect.x = 0;
        return rect;
    }
    /**
     * Get the class of this component's state.
     * The state is an object which represents the state of this control.
     * @return The class of the control's value.
     */
    public Class<?> getStateClass()
    {
        return Boolean.class; // By default
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control)
    {
        if (((JToggleButton)control).isSelected())
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        if (objValue == null)
            objValue = Boolean.FALSE;
        ((JToggleButton)control).setSelected(((Boolean)objValue).booleanValue());
    }
    /**
     * Set the converter to this state. State is defined by the component.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @param objValue The value to set the field to (class of object depends on the control).
     * @return Error code.
     */
    public int setFieldState(Object objValue, boolean bDisplayOption, int iMoveMode)
    {
        if (this.getScreenField().getConverter() == null)
            return DBConstants.NORMAL_RETURN; // Being careful
        if (objValue == null)
            objValue = Boolean.FALSE;
        return this.getScreenField().getConverter().setState(((Boolean)objValue).booleanValue(), bDisplayOption, iMoveMode);
    }
    /**
     * Get this field's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @return The field's value (class defined by the field type).
     */
    public Object getFieldState()
    {
        if (this.getScreenField().getConverter().getState())
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }
}
