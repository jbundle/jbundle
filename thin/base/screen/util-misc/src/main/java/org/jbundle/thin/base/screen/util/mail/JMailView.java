/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util.mail;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBasePanel;
import org.jbundle.thin.base.screen.JBaseToolbar;


/**
 * Html View
 */
public class JMailView extends JBasePanel
{
	private static final long serialVersionUID = 1L;
    
    /**
     * HTMLView Constructor.
     */
    public JMailView()
    {
        super();
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public JMailView(Object parent, Object strURL)
    {
        this();
        this.init(parent, strURL);
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public void init(Object parent, Object objURL)
    {
        super.init(parent, objURL);

        this.setLayout(new BorderLayout());

        String strURL = this.getBaseApplet().getProperty(Params.URL);
        if (strURL != null)
            if (strURL.indexOf(':') != -1)
                strURL = strURL.substring(strURL.indexOf(':') + 1);
        this.displayURL(strURL);
    }
    /**
     * Display this URL (string or URL).
     * @param strURL a string URL (without the protocol).
     */
    public void displayURL(String strURL)
    {
//        if ((path == null) || (path.length() == 0))
        {
            BaseApplet applet = this.getBaseApplet();
            if (applet != null)
            {
                String strError = applet.getString("Mail") + ' ' + applet.getString("Not Configured" + " (" + strURL + ")");
                applet.setStatusText(strError, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    /**
     * Process this action.
     * @param strAction The command to execute.
     * @return True if handled.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        return true;    // Handled
    }
    /**
     * Add the toolbars?
     * @return The newly created toolbar or null.
     */
    public JComponent createToolbar()
    {
        return new JBaseToolbar(this, null);
    }
}
