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
public class JFaxView extends JMailView
{
	private static final long serialVersionUID = 1L;
    
    /**
     * HTMLView Constructor.
     */
    public JFaxView()
    {
        super();
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public JFaxView(Object parent, Object strURL)
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
                String strError = applet.getString("Fax") + ' ' + applet.getString("Not Configured" + " (" + strURL + ")");
                applet.setStatusText(strError, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    /**
     *
     */
    public String getFaxURL(String strMailTo)
    {
        if (strMailTo != null) if (strMailTo.length() > 0)
        {   // Fix the fax number and try it out
            int iLastBreak = 0;
            boolean bUS = true;
            int iIndex = 0;
            for (iIndex = 0; iIndex < strMailTo.length(); iIndex++)
            {
                if ((strMailTo.charAt(iIndex) < '0') || (strMailTo.charAt(iIndex) > '9'))
                {
                    if (iIndex > 1) if ((iIndex - iLastBreak != 3) && (iIndex - iLastBreak != 4) && (iIndex - iLastBreak != 0))
                        bUS = false;    // Can't be a U.S. number 123-123-1234
                    iLastBreak = iIndex;
                    strMailTo = strMailTo.substring(0, iIndex) + strMailTo.substring(iIndex + 1, strMailTo.length());
                    iIndex = iIndex - 1;
                }
            }
            iIndex = strMailTo.length();
            if (iIndex > 1) if ((iIndex - iLastBreak != 3) && (iIndex - iLastBreak != 4) && (iIndex - iLastBreak != 0))
                bUS = false;    // Can't be a U.S. number 123-123-1234
            if ((strMailTo.length() > 3) && (strMailTo.substring(0, 3).equals("011")))
                strMailTo = strMailTo.substring(3);   // International Country code
            else
                if (bUS) if (strMailTo.length() == 10) if (strMailTo.charAt(0) > '1')
                    strMailTo = "1" + strMailTo;    // U.S. Country code
            strMailTo = "mailto:" + strMailTo + "@faxaway.com";
        }
        return strMailTo;
    }
}
