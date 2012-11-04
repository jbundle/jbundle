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
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;

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
    /**
     * Ask the editor if it can start editing using anEvent. anEvent is in the invoking component coordinate system. The editor
     * can not assume the Component returned by getCellEditorComponent() is installed. This method is intended for the use of
     * client to avoid the cost of setting up and installing the editor component if editing is not possible. If editing can be started
     * this method returns true. 
     * @param anEvent - the event the editor should use to consider whether to begin editing or not. 
     * @return true if editing can be started. 
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        lastKeyTyped = 0;
        if (anEvent instanceof KeyEvent)
            lastKeyTyped = ((KeyEvent)anEvent).getKeyChar(); // Hack, First key is not passed to newly focused component.
        return super.isCellEditable(anEvent);
    }
    char lastKeyTyped = 0;
    /**
     * Sets an initial value for the editor. This will cause the editor to stopEditing and lose any partially edited value if the editor
     * is editing when this method is called. 
     */
    public Component getTableCellEditorComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       int row,
                                                       int column)
    {
        char lastKey = lastKeyTyped;
        JComboBox component = (JComboBox)super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (component.isEditable())
            if (lastKey != 0)
        { // Hack, First key is not passed to newly focused component.
            String text = ((JTextField)(((JComboBox)component).getEditor()).getEditorComponent()).getText();
            ((JTextField)(((JComboBox)component).getEditor()).getEditorComponent()).setText(text + lastKey);
        }
        lastKeyTyped = 0;
        return component;
    }
}
