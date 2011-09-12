/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jbundle.base.screen.control.swing.SApplet;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.DualReportScreen;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.util.html.JHtmlEditor;
import org.jbundle.thin.base.screen.util.html.JHtmlView;


/**
 *  HTML Display screen (simply display some HTML in the content area of a screen).
 *  This screen serves a dual purpose; first, it allows the user to enter data in the parameter
 *  form (if isPrintReport() == false), then it outputs the report as Html (when isPrintReport() == true).
 *  WARNING: Be very careful; This screen is used differently when it is used for HTML or a Screen.
 *  When it is a screen, it just throws up an HTML Editor window and asks the http server for the
 *  URL that it is suppose to show.
 *  When it is running on the server, it actually formats and builds the html help page.
 */
public class VDualReportScreen extends VBaseReportScreen
{

    /**
     * Constructor.
     */
    public VDualReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VDualReportScreen(ScreenField model, boolean bEditableControl)
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
        Component panel = this.getControl();
        super.free();
        if (panel instanceof JHtmlView)
            ((JHtmlView)panel).free();
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        this.setControlExtent(new Rectangle(0, 0, 500, 400));

        String strURL = ((DualReportScreen)this.getScreenField()).getScreenURL();   // Don't need outside frame stuff in a window
        URL url = this.getURLFromPath(strURL);
        BaseApplet applet = null;
        applet = (BaseApplet)this.getScreenField().getParentScreen().getAppletScreen().getScreenFieldView().getControl();
        JHtmlEditor helpPane = new JHtmlEditor(applet, url);
        helpPane.setOpaque(false);

        JScrollPane scrollpane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollpane.getViewport().add(helpPane);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(scrollpane);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return helpPane;
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
     * 
     * @param strURL
     * @return
     */
    public URL getURLFromPath(String strURL)
    {
        BaseApplet applet = null;
        if (this.getScreenField().getParentScreen().getTask() instanceof BaseApplet)
        	applet = (BaseApplet)this.getScreenField().getParentScreen().getTask();
        if (applet == null)
        	applet = (BaseApplet)((SApplet)this.getScreenField().getParentScreen().getAppletScreen().getScreenFieldView()).getApplet();
        URL url = JHtmlView.getURLFromPath(strURL, applet);
        return url;
    }
}
