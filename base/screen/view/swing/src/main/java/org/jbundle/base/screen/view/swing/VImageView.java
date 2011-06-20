package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jbundle.base.screen.model.SImageView;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.screen.util.JFSImage;
import org.jbundle.thin.base.screen.util.SerializableImage;

/**
 * Image display.
 */
public class VImageView extends VScreenField
{

    /**
     * Constructor.
     */
    public VImageView()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VImageView(ScreenField model, boolean bEditableControl)
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
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
            this.getControl(DBConstants.CONTROL_TO_FREE).removeMouseListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Get one of the physical components associated with this SField.
     * @param int iLevel    CONTROL_TOP - Parent physical control; CONTROL_BOTTOM - Lowest child control
     * NOTE: This method is used for complex controls such as a scroll box, where this control must be
     * added to the parent, but sub-controls must be added to a lower level physical control.
     * @param iLevel The level for this control (top/bottom/etc).
     * @return The control for this view.
     */
    public Component getControl(int iLevel)
    {
        if (iLevel == DBConstants.CONTROL_TOP)
        {
            Container parent = this.getControl().getParent();
            Component sParent = null;
            if (this.getScreenField().getParentScreen() != null)
                if (this.getScreenField().getParentScreen().getScreenFieldView() != null)
                    sParent = this.getScreenField().getParentScreen().getScreenFieldView().getControl();
            while ((!(parent instanceof JScrollPane)) && (parent != null))
            {
                parent = parent.getParent();
                if (parent == sParent)
                    parent = null;  // Don't go up above this view
            }
            if (parent != null)
                return parent;  // ->Viewport->scrollpane
        }
        return super.getControl(iLevel);
    }
    /**
     * Create the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
// Don't ask me why, buy JLabel does not work for GridViews.
//        JLabel control = new JLabel();
        JFSImage control = new JFSImage(null);

        control.setBorder(null);        // No border inside a scroller.
        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addKeyListener(this);
            control.addMouseListener(this);
        }
        if (bEditableControl)
        {
            boolean bAddScrollPane = true;
            if (this.getScreenField() != null)  // See if the model is specifying a size
                if (((SImageView)this.getScreenField()).getImageSize() != null)
                    if (((SImageView)this.getScreenField()).getImageSize().getHeight() <= ScreenConstants.kMaxEditLineChars)
                        bAddScrollPane = false; // Image is small enough not to need a scroll bar.
            if (bAddScrollPane)
                new JScrollPane(control, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
  
        return control;
    }
    /**
     * Calc the control size.
     * @param ptLocation The location of the control.
     * @return The control bounds.
     */   
    public Rectangle calcBoxShape(Point ptLocation)
    {
        Dimension itsSize = null;
        if (this.getScreenField() != null)  // See if the model is specifying a size
            itsSize = ((SImageView)this.getScreenField()).getImageSize();
        if (itsSize == null)
        {
            short boxChars, boxLines = ScreenConstants.kMaxDoubleLines;
            boxChars = ScreenConstants.kMaxSingleChars;
            itsSize = this.getTextBoxSize(boxChars, ScreenConstants.NEXT_LOGICAL, boxLines);
        }
        return new Rectangle(ptLocation.x , ptLocation.y, itsSize.width, itsSize.height);
    }
    /**
     * Get the class of this component's state.
     * The state is an object which represents the state of this control.
     * @return The class of this control's value (ImageIcon).
     */
    public Class<?> getStateClass()
    {
        return ImageIcon.class;   // By default
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
            return DBConstants.NORMAL_RETURN;
        if (objValue instanceof SerializableImage)
            objValue = new ImageIcon(((SerializableImage)objValue).getImage())
        if (!(objValue instanceof ImageIcon))
            objValue = null;
        if (objValue != null)
            System.out.println("Error: .............Trying to set a imageicon...............");
        return this.getScreenField().getConverter().setData((ImageIcon)objValue, bDisplayOption, iMoveMode);
    }
    /**
     * Get this field's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @return The field's value (class defined by the field type).
     */
    public Object getFieldState()
    {
        Object tempIcon = this.getScreenField().getConverter().getData();
        if (!(tempIcon instanceof ImageIcon) && !(tempIcon instanceof SerializableImage))
            tempIcon = null;
        return tempIcon;
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control)
    {
        return ((JFSImage)control).getControlValue();
//        return ((JLabel)control).getIcon();
    }
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        if (objValue instanceof SerializableImage)
            objValue = new ImageIcon(((SerializableImage)objValue).getImage());
        if (!(objValue instanceof ImageIcon))
            objValue = null;
        ((JFSImage)control).setControlValue((ImageIcon)objValue);
    }
    /**
     * Invoked when a mouse button has been clicked on a component.
     */
    public void onMouseClicked(MouseEvent evt)
    {
        /*int iErrorCode = */this.validateCurrentFocus();   // 1.4 HACK
        int bUseSameWindow = ScreenConstants.USE_SAME_WINDOW | ScreenConstants.PUSH_TO_BROSWER;
        if ((evt != null)
            && ((evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0))
                bUseSameWindow = ScreenConstants.USE_NEW_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER;
        String strCommand = ((SImageView)this.getScreenField()).getButtonCommand();
        if ((this.getControl() != null) && (strCommand != null))
            this.getScreenField().handleCommand(strCommand, this.getScreenField(), bUseSameWindow);   // Execute this specific command
        super.onMouseClicked(evt);
    }
}
