/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.chat;

/**
 * OrderEntry.java:   Applet
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Container;

import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.JBasePanel;


/**
 * Main Class for applet OrderEntry
 */
public class ChatApplet extends BaseApplet
{
    private static final long serialVersionUID = 1L;


    /**
     *  OrderEntry Class Constructor.
     */
    public ChatApplet()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public ChatApplet(String[] args)
    {
        this();
        this.init(args);
    }
    /**
     * The getAppletInfo() method returns a string describing the applet's
     * author, copyright date, or miscellaneous information.
     */
    public String getAppletInfo()
    {
        return "Name: Thin Test\r\n" +
               "Author: Don Corley\r\n" +
               "E-Mail: don@tourgeek.com";
    }
    /**
     * Add any applet sub-panel(s) now.
     */
    public boolean addSubPanels(Container parent)
    {
        FieldList record = null;

        JBasePanel baseScreen = new ChatScreen(this, record);
        boolean success = super.addSubPanels(parent);
        if (success)
        	success = this.changeSubScreen(parent, baseScreen, null);
        return success;
    }
    /**
     *  The main() method acts as the applet's entry point when it is run
     *  as a standalone application. It is ignored if the applet is run from
     *  within an HTML page.
     */
    public static void main(String args[])
    {
        BaseApplet.main(args);
        ChatApplet applet = (ChatApplet)ChatApplet.getSharedInstance();
        if (applet == null)
            applet = new ChatApplet(args);
        new JBaseFrame("Test", applet);
    }
}
