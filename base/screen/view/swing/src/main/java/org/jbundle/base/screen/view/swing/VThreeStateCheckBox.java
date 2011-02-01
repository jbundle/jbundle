package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;

import javax.swing.JToggleButton;

import org.jbundle.base.screen.model.SThreeStateCheckBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.screen.util.JThreeStateCheckBox;


/**
 * SCheckBox - Implements a three-state check box.
 * (The third state is shaded and equates to null).
 */
public class VThreeStateCheckBox extends VCheckBox
{

    /**
     * Constructor.
     */
    public VThreeStateCheckBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VThreeStateCheckBox(ScreenField model, boolean bEditableControl)
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
        if (this.getControl(DBConstants.CONTROL_TO_FREE) != null)
            if (m_bEditableControl)
        {
            this.getControl(DBConstants.CONTROL_TO_FREE).removeFocusListener(this);
            ((JThreeStateCheckBox)this.getControl(DBConstants.CONTROL_TO_FREE)).removeActionListener(this);
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
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
        String strDesc = ((SThreeStateCheckBox)this.getScreenField()).getButtonDesc();
        JThreeStateCheckBox control = new JThreeStateCheckBox(strDesc);   // Physical control
        control.setOpaque(false);
        control.setHorizontalTextPosition(JToggleButton.LEFT);
        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addActionListener(this);
            
            control.removeActionListener(control);
            control.addActionListener(control);         // Hack -- Must be before this action listener

            control.addKeyListener(this); // Required for tab and shift-tab
        }
        return control;
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control)
    {
        return ((JThreeStateCheckBox)control).getControlValue();
    }
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        ((JThreeStateCheckBox)control).setControlValue(objValue);
    }
    /**
     * Set the converter to this state. State is defined by the component.
     * @param objValue The value to set the field to (class of object depends on the control).
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return Error code.
     */
    public int setFieldState(Object objValue, boolean bDisplayOption, int iMoveMode)
    {
        if (this.getScreenField().getConverter() == null)
            return DBConstants.NORMAL_RETURN; // Being careful
        return this.getScreenField().getConverter().setData(objValue, bDisplayOption, iMoveMode);
    }
    /**
     * Get this field's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @return The field's value (class defined by the field type).
     */
    public Object getFieldState()
    {
        return this.getScreenField().getConverter().getData();
    }
}
