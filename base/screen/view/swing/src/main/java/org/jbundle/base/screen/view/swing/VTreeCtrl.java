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

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;


/**
 * Implements a tree control.
 */
public class VTreeCtrl extends VBaseScreen
{

    /**
     * Constructor.
     */
    public VTreeCtrl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public VTreeCtrl(ScreenField model,boolean bEditableControl)
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
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        JTree control = new JTree();
        control.setOpaque(false);
        JScrollPane scrollpane = new JScrollPane(control);
        scrollpane.setPreferredSize(new Dimension(10,10));
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        panel.add(scrollpane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return control;
    }
    /**
     * Get one of the physical components associated with this SField.
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
            while (!(parent instanceof JScrollPane))
            {
                parent = parent.getParent();
            }
            if (parent != null)
                return parent.getParent();  // scrollpane->JPanel
        }
        if (iLevel == DBConstants.CONTROL_BOTTOM)
            return null;    // Make sure controls are not directly added to this JTable
        return super.getControl(iLevel);
    }
}
