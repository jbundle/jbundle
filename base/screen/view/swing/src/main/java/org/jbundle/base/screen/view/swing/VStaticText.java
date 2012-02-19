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

import javax.swing.JLabel;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.db.Constants;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p>
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class VStaticText extends VEditText
{

    /**
     * Constructor.
     */
    public VStaticText()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VStaticText(ScreenField model,boolean bEditableControl)
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
     * Make the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        JLabel control = null;
        if (this.getScreenField().getParentScreen() == null)
            return null;
//?        short itsMaxChars = (short)this.getScreenField().getConverter().getMaxLength();   
        String tempString = this.getScreenField().getConverter().getString(); // Should I do this?
        control = new JLabel(tempString); //?, Label.RIGHT);
        control.setOpaque(false);
        return control;
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Object control)
    {
        return ((JLabel)control).getText();
    }
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Object control, Object objValue)
    {
        if (objValue == null)
            objValue = Constants.BLANK;
        ((JLabel)control).setText((String)objValue);
    }
}
