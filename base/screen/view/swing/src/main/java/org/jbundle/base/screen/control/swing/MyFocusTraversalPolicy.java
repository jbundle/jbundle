/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.swing;

/**
 * @(#)AppletScreen.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import javax.swing.LayoutFocusTraversalPolicy;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.swing.VScreenField;
import org.jbundle.base.util.DBConstants;


/**
 * My Focus Traversal Policy.
 */
public class MyFocusTraversalPolicy extends LayoutFocusTraversalPolicy
{
	private static final long serialVersionUID = 1L;

	public MyFocusTraversalPolicy()
    {
        super();
    }
    /**
     * Returns the Component that should receive the focus after aComponent.
     */
    public Component getComponentAfter(Container focusCycleRoot, Component aComponent)
    {
        ScreenField sField = VScreenField.getComponentModel(aComponent);
        Component component = this.getComponentAfter(sField, DBConstants.SELECT_NEXT_FIELD);
        if (component != null)
        {
            if (component == aComponent)
            {   // Special case - only one component is focusable, need to validate it.
                sField.controlToField();    // Validate the current field
            }
            return component;
        }
        return super.getComponentAfter(focusCycleRoot, aComponent);
    }
    /**
     * Returns the Component that should receive the focus before aComponent.
     */
    public Component getComponentBefore(Container focusCycleRoot, Component aComponent)
    {
        ScreenField sField = VScreenField.getComponentModel(aComponent);
        Component component = this.getComponentAfter(sField, DBConstants.SELECT_PREV_FIELD);
        if (component != null)
        {
            if (component == aComponent)
            {   // Special case - only one component is focusable, need to validate it.
                sField.controlToField();    // Validate the current field
            }
            return component;
        }
        return super.getComponentBefore(focusCycleRoot, aComponent);
    }
    /**
     * Returns the default Component to focus.
     */
    public Component getDefaultComponent(Container focusCycleRoot)
    {
        return super.getDefaultComponent(focusCycleRoot);
    }
    /**
     * Returns the first Component in the traversal cycle.
     */
    public Component getFirstComponent(Container focusCycleRoot)
    {
        return super.getFirstComponent(focusCycleRoot);
    }
    /**
     * Returns the Component that should receive the focus when a Window is made visible for the first time.
     */
    public Component getInitialComponent(Window window)
    {
        return super.getInitialComponent(window);
    }
    /**
     * Returns the last Component in the traversal cycle.
     */
    public Component getLastComponent(Container focusCycleRoot)
    {
        return super.getLastComponent(focusCycleRoot);
    }
    /**
     * Get the physical component that comes before or after this screen field.
     * @param sField The screen field to get the before or after component.
     * @param iSelectNextOrder Next, Previous, First or Last.
     * @return The component that comes (before or after) this sField (or null if it doesn't exist).
     */
    public Component getComponentAfter(ScreenField sField, int iSelectNextOrder)
    {
        if (sField != null)
        {
            sField = sField.getComponentAfter(sField, iSelectNextOrder);
            if (sField != null)
            {
                org.jbundle.base.screen.view.ScreenFieldView sFieldView = sField.getScreenFieldView();
                if (sFieldView != null)
                {
                    return sFieldView.getControl();
                }
            }
        }
        return null;
    }
}
