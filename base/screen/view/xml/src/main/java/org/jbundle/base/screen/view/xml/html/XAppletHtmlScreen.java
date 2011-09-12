/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.ResourceBundle;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.xml.XBaseScreen;
import org.jbundle.model.DBException;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class XAppletHtmlScreen extends XBaseScreen
{

    /**
     * Constructor.
     */
    public XAppletHtmlScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XAppletHtmlScreen(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
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
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        out.println("<html>");
        this.printAppletHtmlScreen(out, reg);
        out.println("</html>");
/*        Map<String,Object> propApplet = new Hashtable<String,Object>();
        Map<String,Object> properties = ((AppletHtmlScreen)this.getScreenField()).getAppletProperties(propApplet);
        Utility.startTag("applet-properties");
        out.println(XMLPropertiesField.propertiesToXML(propApplet));
        Utility.endTag("applet-properties");
        Utility.startTag("properties");
        out.println(XMLPropertiesField.propertiesToXML(properties));
        Utility.endTag("properties");
        Utility.startTag("jnlp-url");
        String strJnlpURL = this.getJnlpURL(propApplet, properties);
        out.println(Utility.encodeXML(strJnlpURL));
        Utility.endTag("jnlp-url");
*/    }
    /**
     * Get the name of the stylesheet.
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getStylesheetName()
    {
        return "applet";  // Form for applet display in form
    }
}
