/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing.opt;

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

import javax.swing.JTable;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.swing.VBaseButton;
import org.jbundle.base.screen.view.swing.VScreenField;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.screen.util.JBlinkLabel;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class VBlinkImageView extends VScreenField
{

    /**
     * Constructor.
     */
    public VBlinkImageView()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VBlinkImageView(ScreenField model, boolean bEditableControl)
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
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Create the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new component (a JBlinkLabel).
     */
    public Component setupControl(boolean bEditableControl)
    {
        JBlinkLabel control = new JBlinkLabel(null);

        control.setBorder(null);        // No border inside a scroller.
        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addKeyListener(this);
        }
        return control;
    }
    /**
     * Calc the control size.
     * @param ptLocation The location of the control in the parent.
     * @return The bounds of the new control.
     */   
    public Rectangle calcBoxShape(Point ptLocation)
    {
        Dimension itsSize = this.getTextBoxSize(1, ScreenConstants.NEXT_LOGICAL, 1);
        itsSize.width = VBaseButton.DEFAULT_BUTTON_WIDTH;   // 16
        itsSize.height = Math.max(itsSize.height, VBaseButton.DEFAULT_BUTTON_HEIGHT);   // 15
        return new Rectangle(ptLocation.x , ptLocation.y, itsSize.width, itsSize.height);
    }
    /**
     * Get the class of this component's state.
     * The state is an object which represents the state of this control.
     * @return The class of the control's value (String).
     */
    public Class<?> getStateClass()
    {
        return String.class;    // By default
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control)
    {
        return control.getName();
    }
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        if (objValue == null)
            objValue = "1";     // Display the default icon
        control.setName(objValue.toString());
    }
    /**
     * Set the converter to this state. State is defined by the component.
     * @param objValue The value to set the control to (class of object depends on the control).
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return Error code.
     */
    public int setFieldState(Object objValue, boolean bDisplayOption, int iMoveMode)
    {
        return DBConstants.NORMAL_RETURN; // Read only
    }
    /**
     * Get this field's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @return The field's value (class defined by the field type).
     */
    public Object getFieldState()
    {
        Object tempIcon = this.getScreenField().getConverter().getData();
        if (tempIcon != null)
            tempIcon = tempIcon.toString();
        return tempIcon;
    }
    /**
     * If this control is in a JTable, this is how to render it.
     * @param table The table this component is in.
     * @param value The value of this cell.
     * @param isSelected True if selected.
     * @param hasFocus True if focused.
     * @param row The table row.
     * @param column The table column.
     * @return This component (after updating for blink).
     */
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
    	java.awt.Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	JBlinkLabel label = (JBlinkLabel)this.getControl();
    	if (label != null)
    		label.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	return component;
    }
}
