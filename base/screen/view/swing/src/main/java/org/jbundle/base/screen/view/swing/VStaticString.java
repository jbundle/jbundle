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

import javax.swing.JLabel;

import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.SStaticString;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.db.Constants;


/**
 * A static text area.
 */
public class VStaticString extends VScreenField
{

    /**
     * Constructor.
     */
    public VStaticString()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VStaticString(ScreenField model, boolean bEditableControl)
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
     * Set up the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        if (this.getScreenField().getParentScreen() == null)
            return null;
        JLabel control = null;
        String m_StaticString = ((SStaticString)this.getScreenField()).getStaticString();
        if (m_StaticString.length() != 0)
            control = new JLabel(m_StaticString);

        return control;
    }
    /**
     * Calculate the box size.
     * @param ptLocation The location of the control in the parent.
     * @return The control's bounds.
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {
        int boxChars, boxLines;
        String m_StaticString = ((SStaticString)this.getScreenField()).getStaticString();
        boxChars = m_StaticString.length();
        boxLines = 1;
        if (boxChars > ScreenConstants.kMaxEditLineChars)
        {       // This should depend on the m_ScreenSize relative values
            boxChars = ScreenConstants.kMaxTEChars;
            boxLines = ScreenConstants.kMaxTELines;
        }
        else if (boxChars > ScreenConstants.kMaxSingleLineChars)
        {       // not int enough for a scroll box, but not single line
            if (boxChars > ScreenConstants.kMaxDoubleLineChars)
                boxLines = ScreenConstants.kMaxDoubleLines;
            else
                boxLines = ScreenConstants.kMaxSingleLines;
            boxChars = ScreenConstants.kMaxSingleChars;
        }
        Dimension itsSize = this.getTextBoxSize(boxChars, ScreenConstants.NEXT_LOGICAL, boxLines);
        Rectangle rect = new Rectangle(ptLocation.x , ptLocation.y, itsSize.width, itsSize.height);
        short sLocation = this.getScreenField().getScreenLocation().getLocationConstant();
        String descString = m_StaticString;
        if (descString != null)
            if (sLocation == ScreenConstants.FIELD_DESC)
        {
            Point location = new Point(0, 0);
            Dimension cSize = new Dimension(0, 0);
            location.x = rect.x;
            location.y = rect.y;
            cSize.width = rect.width;
            cSize.height = rect.height;

//?            ScreenLocation screenLocation = new ScreenLocation(ScreenConstants.FIELD_DESC, ScreenConstants.DONT_SET_ANCHOR);
            Point descLocation = ptLocation;//xm_ParentScreen.getScreenLayout().calcLocation(screenLocation);
            Dimension itsSize2 = this.getTextBoxSize(ScreenConstants.kMaxDescLength, ScreenConstants.FIELD_DESC, (short)1);
            if (descLocation.x < 1)
            { // Too far to the left, shrink the size of the box
                itsSize2.width += descLocation.x + 1;
                descLocation.x = 1;
            }
            else
            {   // Shrink box by size of desc text
                int sizeFix = this.getScreenInfo().getBoxWidth(ScreenConstants.kMaxDescLength);
                sizeFix -= this.getTextExtent(descString).width;
                itsSize2.width -= sizeFix;
                descLocation.x += sizeFix;
            }
            this.setControlExtent(new Rectangle(descLocation.x, descLocation.y, itsSize2.width, itsSize2.height));
            rect = this.getControlExtent();
            rect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
        }
        return rect;
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     */
    public Object getComponentState(Component control)
    {
        return ((JLabel)control).getText();
    }
    /**
     * Set the component to this state. State is defined by the component.
     */
    public void setComponentState(Component control, Object objValue)
    {
        if (objValue == null)
            objValue = Constants.BLANK;
        ((JLabel)control).setText((String)objValue);
    }
}
