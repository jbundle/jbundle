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
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * This is the base for any data maintenance screen.
 */
public class VScreen extends VBaseScreen
{

    /**
     * Constructor.
     */
    public VScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VScreen(ScreenField model,boolean bEditableControl)
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
        JPanel panel = (JPanel)this.getControl();
        super.free();
        JScrollPane scrollPane = (JScrollPane)panel.getParent().getParent();
        panel.getParent().remove(panel);
        scrollPane.getParent().remove(scrollPane);
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        Component screen = super.setupControl(bEditableControl);    // By default, don't allow column selections
        JScrollPane scrollpane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setPreferredSize(new Dimension(10,10));
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollpane.getViewport().add(screen);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(scrollpane);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return screen;
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
            while ((!(parent instanceof JScrollPane)) && (parent != null))
            {
                parent = parent.getParent();
            }
            if (parent != null)
                return parent.getParent();  // scrollpane->JPanel
        }
        return super.getControl(iLevel);
    }
    /**
     * This control received the focus.
     * @param evt The focus event.
     */
    public void focusGained(FocusEvent evt)
    {
        super.focusGained(evt);
        Record record = ((Screen)this.getScreenField()).getMainRecord();
        if (record != null)
            this.getScreenField().selectField(null, DBConstants.SELECT_FIRST_FIELD);
    }
    /**
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return An error code.
     */
    public int handleMessage(BaseMessage message)
    {
        SwingUtilities.invokeLater(new HandleScreenUpdate(this, message));    // Update the fields on the screen (and be thread-safe).
        return DBConstants.NORMAL_RETURN;
    }

}
