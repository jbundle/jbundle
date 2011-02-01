package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Container;
import java.awt.LayoutManager;

import javax.swing.SwingUtilities;

import org.jbundle.base.screen.control.swing.util.ScreenLayout;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.message.BaseMessage;



/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 */
public class VBaseScreen extends VBasePanel
{

    /**
     * Constructor.
     */
    public VBaseScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VBaseScreen(ScreenField model, boolean bEditableControl)
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
        super.free();
    }
    /**
     * Setup this screen's screen layout.
     * Usually, you use JAVA layout managers, but you may also use ScreenLayout.
     * @return The new screen layout.
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
    /**
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(BaseMessage message)
    {
        SwingUtilities.invokeLater(new HandleBaseScreenUpdate(this, message));    // Update the fields on the screen (and be thread-safe).
        return DBConstants.NORMAL_RETURN;
    }
}
