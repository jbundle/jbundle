/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util.mail;

import javax.swing.JOptionPane;

import org.jbundle.thin.base.screen.BaseApplet;


/**
 * Html View
 */
public class JPhoneView extends JMailView
{
	private static final long serialVersionUID = 1L;
    
    /**
     * HTMLView Constructor.
     */
    public JPhoneView()
    {
        super();
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public JPhoneView(Object parent, Object strURL)
    {
        this();
        this.init(parent, strURL);
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public void init(Object parent, Object strURL)
    {
        super.init(parent, strURL);
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
                String strError = applet.getString("Phone") + ' ' + applet.getString("Not Configured" + " (" + strURL + ")");
                applet.setStatusText(strError, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
