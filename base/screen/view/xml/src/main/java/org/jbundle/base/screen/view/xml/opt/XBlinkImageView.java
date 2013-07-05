/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml.opt;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.xml.XScreenField;
import org.jbundle.model.screen.ScreenComponent;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class XBlinkImageView extends XScreenField
{
    /**
     * Constructor.
     */
    public XBlinkImageView()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XBlinkImageView(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
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
     * display this field in html input format.
     */
    public void printInputControl(PrintWriter out, String strFieldDesc, String strFieldName, String strSize, String strMaxSize, String strValue, String strControlType, String strFieldType)
    {
        super.printInputControl(out, strFieldDesc, strFieldName, strSize, strMaxSize, strValue, strControlType, strFieldType);
//      String strImage = "";
//      out.println("<td>" + strImage + "</td>");
    }
}
