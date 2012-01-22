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
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JToolBar;

import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.control.swing.util.ScreenLayout;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.model.screen.ScreenComponent;


/**
 * Set up a tool bar control.
 */
public class VToolScreen extends VBasePanel
{

    /**
     * Constructor.
     */
    public VToolScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VToolScreen(ScreenField model, boolean bEditableControl)
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
     * Setup the physical control.
     * Make sure the toolbar is at the top of the screen.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        if (this.getScreenField().getParentScreen() == null)
            return null;
        boolean bSuccess = this.getScreenField().getParentScreen().removeSField(this.getScreenField());   // Note: doesn't remove me because m_Control = null
        int iSFieldCount = this.getScreenField().getParentScreen().getSFieldCount();
        int iToolbarOrder = iSFieldCount;
        if (this.getScreenField().getScreenLocation().getLocationConstant() != ScreenConstants.LAST_LOCATION)
        {
	        for (iToolbarOrder = 0; iToolbarOrder < iSFieldCount; iToolbarOrder++)
	        {
	            if (!(this.getScreenField().getParentScreen().getSField(iToolbarOrder) instanceof ToolScreen))
	                break;  // Last toolbar
	        }
        }
        if (bSuccess)
            this.getScreenField().getParentScreen().addSField(this.getScreenField(), iToolbarOrder);    // Add this control after the other toolbars, before the screen controls
    
        JToolBar control = new JToolBar();
        control.setAlignmentX(Component.LEFT_ALIGNMENT);
        control.setAlignmentY(Component.TOP_ALIGNMENT);
        control.setFloatable(false);
        control.setOpaque(false);

        control.setMargin(new Insets(0,0,0,0));
        return control;
    }
    /**
     * Setup this screen's screen layout.
     * Usually, you use JAVA layout managers, but you may also use ScreenLayout.
     * @return The new (ScreenLayout) layout.
     */
    public LayoutManager addScreenLayout()
    {
        LayoutManager screenLayout = null;
        if (this.getScreenLayout() == null)   // Only if no parent screens
        {   // EVERY BasePanel gets a ScreenLayout!
            Container panel = (Container)this.getControl();
            screenLayout = new ScreenLayout(this);  // My LayoutManager
            if ((panel != null) && (screenLayout != null))
                panel.setLayout(screenLayout);
        }
        return screenLayout;
    }
}
