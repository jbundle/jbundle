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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.screen.control.swing.util.ScreenLayout;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.Task;
import org.jbundle.model.screen.ScreenComponent;


/**
 * BasePanel - This is the base for any data display screen.
 */
public class VBasePanel extends VScreenField
{
    /**
     * m_ScreenLayout - Screen layout information.
     */
    protected ScreenLayout m_ScreenLayout = null;

    /**
     * Constructor.
     */
    public VBasePanel()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VBasePanel(ScreenField model, boolean bEditableControl)
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
        m_ScreenLayout = null;
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
            this.getControl(DBConstants.CONTROL_TO_FREE).removeMouseListener(this);
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        if (m_ScreenLayout != null)
            m_ScreenLayout.free((VBasePanel)this);
        super.free();
        m_ScreenLayout = null;
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        JPanel control = new JPanel();
        control.addFocusListener(this);
        control.addKeyListener(this);
        control.addMouseListener(this);
        control.setOpaque(false);
        control.setAlignmentX(Component.LEFT_ALIGNMENT);
        control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));
        return control;
    }
    /**
     * Calculate the physical screen size of this control.
     * <p/>For a screen, survey all of the control (by doing a validate), then
     * @return the ViewSize.
     * @param Point ptLocation Origin of this control
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {   // This code is the same as the code in BasePanel.Create (Eventually use the same code)
        BasePanel parentScreen = this.getScreenField().getParentScreen();
        if (parentScreen == null)
            return new Rectangle(ptLocation.x, ptLocation.y, 0, 0);         // No parent, means we should calc later
        Container container = (Container)this.getControl();
        if (container != null)  // Panels only (not scrollpanes)
        {
            if (!container.isValid())
                container.validate(); // Calc the container's layout
        }
        else
        {   // No physical container; find one
            while (container == null)
            {
                container = (Container)parentScreen.getScreenFieldView().getControl();
                parentScreen = parentScreen.getParentScreen();
            }
            if (this.getScreenLayout() != null)
                this.getScreenLayout().layoutContainer(container);  // Calc the sub fields and resurvey!
        }
        Dimension size = new Dimension(0, 0);
        if (this.getScreenLayout() != null)
            size = this.getScreenLayout().getFarthestField();
        else
        {   // Alternate layout, calculate the size manually
            ScreenField sField = null;
            Point ptLoc = new Point(0, 0);  //ptLocation.x, ptLocation.y);
            for (int i = 0; i < ((BasePanel)this.getScreenField()).getSFieldCount(); i++)
            {
                sField = ((BasePanel)this.getScreenField()).getSField(i);
                Rectangle rectField = ((VScreenField)sField.getScreenFieldView()).getControlExtent();
                if (rectField == null)
                    rectField = ((VScreenField)sField.getScreenFieldView()).calcBoxShape(ptLoc);
                else
                {
                    ptLoc.x = rectField.x;
                    ptLoc.y = rectField.y;
                }
                if (rectField.height == 0)
                    ptLoc.y += 20;
                size.width = Math.max(size.width, rectField.x + rectField.width);
                size.height = Math.max(size.height, rectField.y + rectField.height);
                ptLoc.y = Math.max(ptLoc.y, size.height + 4);
            }
        }
        return new Rectangle(ptLocation.x , ptLocation.y, size.width, size.height);
    }
    /**
     * Get the screen information.
     * @return The screen layout.
     */
    public ScreenLayout getScreenLayout()
    {
        return m_ScreenLayout;
    } // Screen information
    /**
     * Set the screen information.
     * @param @screenLayout The screen layout.
     */
    public void setScreenLayout(ScreenLayout screenLayout)
    {
        m_ScreenLayout = screenLayout;
    } // Screen information
    /**
     * Setup this screen's screen layout.
     * Usually, you use JAVA layout managers, but you may also use ScreenLayout.
     * @return The screen layout.
     */
    public Object addScreenLayout()
    {
        return null;
    }
    /**
     * Set the the physical control color, font etc.
     * @param component The physical control.
     * @param bSelected Is it selected?
     * @param bIsInput This this an input (vs a display) field?
     * @param bGridControl Is it a grid control?
     */
    public void setControlAttributes(Object component, boolean bIsInput, boolean bSelected, boolean bGridControl)
    {
        super.setControlAttributes(component, bIsInput, bSelected, bGridControl);
        BasePanel basePanel = (BasePanel)this.getScreenField();
        for (int iIndex = 0; iIndex < basePanel.getSFieldCount(); iIndex++)
        {
            ScreenField sField = basePanel.getSField(iIndex);
            component = sField.getScreenFieldView().getControl();
            sField.getScreenFieldView().setControlAttributes(component, bIsInput, bSelected, bGridControl);
        }
    }
    /**
     * Resurvey the child control(s) and resize frame.
     * @param strTitle The screen title.
     */
    public void resizeToContent(String strTitle)
    {
        if (this.getScreenField().getParentScreen() != null)
            this.getScreenField().getParentScreen().resizeToContent(strTitle);
        Task sApplet = ((BasePanel)this.getScreenField()).getTask();//getAppletScreen().getScreenFieldView().getControl();
        if (strTitle != null)
            sApplet.setStatusText(strTitle);        // Display either the screen title in the status screen (for applets)
    }
    /**
     * Set the default button for this basepanel.
     * @param The button to default to on return.
     */
    public void setDefaultButton(ScreenFieldView button)
    {
        if (button != null)
        {
            ((JButton)button.getControl()).setDefaultCapable(true);
            ((JComponent)this.getControl()).getRootPane().setDefaultButton(((JButton)button.getControl()));
        }
        else
            ((JComponent)this.getControl()).getRootPane().setDefaultButton(null);
    }
}
