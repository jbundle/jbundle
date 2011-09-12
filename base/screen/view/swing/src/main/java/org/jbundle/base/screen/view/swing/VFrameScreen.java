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
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.jbundle.base.screen.control.swing.util.ScreenInfo;
import org.jbundle.base.screen.model.AppletScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.FrameScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.menu.SBaseMenuBar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.db.Constants;


/**
 * The Frame is the the screen frame for AWT or Swing views.
 */
public class VFrameScreen extends VBasePanel
    implements WindowListener
{

    /**
     * Constructor.
     */
    public VFrameScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VFrameScreen(ScreenField model, boolean bEditableControl)
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
        if (this.getControl() != null)
            if (m_bEditableControl)
        {
            ((JFrame)this.getControl()).removeWindowListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        JFrame frame = (JFrame)this.getControl();
        super.free();
        if (frame != null)
        {
            frame.dispose();        // Get rid of this physical control
        }
    }
    /**
     * Set up the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new (JFrame) component.
     */
    public Component setupControl(boolean bEditableControl)
    {
        if (this.getScreenField().getParentScreen() != null)
            return null;
        JFrame control = new JFrame("Java Application");

        control.setFocusTraversalPolicy(new org.jbundle.base.screen.control.swing.MyFocusTraversalPolicy());
        control.addWindowListener(this);        // To send window close
        return control;
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
        if (iLevel == DBConstants.CONTROL_BOTTOM)
            return ((JFrame)this.getControl()).getContentPane();
        return super.getControl(iLevel);
    }
    /**
     * Get the screen information.
     * @return The screen information.
     */
    public ScreenInfo getScreenInfo()
    {
        FrameScreen frameScreen = (FrameScreen)this.getScreenField();
        if (frameScreen.getAppletScreen() != null)
            return ((VScreenField)frameScreen.getAppletScreen().getScreenFieldView()).getScreenInfo();
        else
            return null;
    } // Screen information
    /**
     * Resurvey the child control(s) and resize frame.
     * @param strTitle The title for the frame.
     */
    public void resizeToContent(String strTitle)
    {
        JFrame frame = (JFrame)this.getControl();
        boolean bFirstTime = true;
        if (frame.isVisible())
            bFirstTime = false;
        if (strTitle != null)
            frame.setTitle(strTitle);
        AppletScreen appletScreen = (AppletScreen)((FrameScreen)this.getScreenField()).getSField(0);
        ScreenField menuBar = appletScreen.getSField(0);
        if (!(menuBar instanceof SBaseMenuBar))     // If there is not menu, throw up a default
            new SBaseMenuBar(new ScreenLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), appletScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
        try   {
            if (!frame.isValid())
                frame.validate(); // Make sure all subs are laid out before show
            if (bFirstTime)
                frame.setVisible(true);
        } catch (Exception ex)  {
            ex.printStackTrace();
            System.exit(0);
        }
        Dimension dimFrame = this.getFrameSize(frame);
        if ((bFirstTime) || ((frame.getWidth() < Constants.MIN_SCREEN_SIZE.width) && (frame.getHeight() < Constants.MIN_SCREEN_SIZE.height)))
        {
            frame.setSize(dimFrame);
            if (m_rectExtent == null)   // Always
                m_rectExtent = frame.getBounds();
            frame.invalidate();
            frame.validate();
            frame.repaint();
        }
        else
            appletScreen.getScreenFieldView().getControl().repaint();
    }
    /**
     * Calculate the frame size.
     * @param frame
     * @return
     */
    public Dimension getFrameSize(JFrame frame)
    {
        Dimension dimFrame = new Dimension(Constants.PREFERRED_SCREEN_SIZE.width, Constants.PREFERRED_SCREEN_SIZE.height);  // Default
        
        Dimension dimScreen = null;
        Toolkit toolkit = frame.getToolkit();
        if (toolkit != null)
        {
            dimScreen = toolkit.getScreenSize();
            Rectangle m_dimFarthest = ((VScreenField)((BasePanel)this.getScreenField()).getSField(0).getScreenFieldView()).calcBoxShape(new Point(20, 20));
            if (dimScreen != null)
            {
                dimFrame.width = Math.min(dimScreen.width - 50, Math.max(m_dimFarthest.width, 100));
                dimFrame.height = Math.min(dimScreen.height - 50, Math.max(m_dimFarthest.height, 50));
            }
        }
        int ilrBorders = Math.min(frame.getInsets().left + frame.getInsets().right, 25);
        int itbBorders = Math.min(frame.getInsets().top  + frame.getInsets().bottom, 25);
        itbBorders += 20; // HACK to compensate for menu
        dimFrame.width += ilrBorders;
        dimFrame.height += itbBorders;

        return dimFrame;
    }
    /**
     * Use clicked the close button on a frame, close the window and/or close the app.
     * The handleEvent() method receives all events generated within the frame
     * window. You can use this method to respond to window events. To respond
     * to events generated by menus, buttons, etc. or other controls in the
     * frame window but not managed by the applet, override the window's
     * action() method.
     * @param evt The window event.
     */
    public void windowClosing(WindowEvent evt)
    {
        this.getScreenField().free(); // Close/destroy the window.
    }
    /**
     *  Invoked when a window has been closed (Window Listener).
     * @param evt The window event.
     */
    public void windowClosed(WindowEvent evt)
    {
    }
    /**
     *  Invoked when a window has been opened (Window Listener).
     * @param evt The window event.
     */
    public void windowOpened(WindowEvent evt)
    {
    }
    /**
     *  Invoked when a window is iconified (Window Listener).
     * @param evt The window event.
     */
    public void windowIconified(WindowEvent evt)
    {
    }
    /**
     *  Invoked when a window is de-iconified (Window Listener).
     * @param evt The window event.
     */
    public void windowDeiconified(WindowEvent evt)
    {
    }
    /**
     *  Invoked when a window is activated (Window Listener).
     * @param evt The window event.
     */
    public void windowActivated(WindowEvent evt)
    {
    }
    /**
     *  Invoked when a window is de-activated (Window Listener).
     * @param evt The window event.
     */
    public void windowDeactivated(WindowEvent evt)
    {
    }
}
