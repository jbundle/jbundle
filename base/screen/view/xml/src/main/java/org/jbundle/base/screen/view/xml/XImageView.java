/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class XImageView extends XScreenField
{

    /**
     * Constructor.
     */
    public XImageView()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XImageView(ScreenField model,boolean bEditableControl)
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
     * Get the rich text type for this control.
     * @return The type (Such as HTML, Date, etc... very close to HTML control names).
     */
    public String getInputType(String strViewType)
    {
        return "image";   // Hey I made this up!
    }
    /**
     * Print this field's data in XML format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        boolean bFieldsFound = false;
        String strFieldName = this.getScreenField().getSFieldParam();
        String strFieldData = this.getZmlImagePath();
        if (strFieldData == null)
            strFieldData = HtmlConstants.ICON_DIR + "Noimage.gif";
        strFieldData = Utility.encodeXML(strFieldData);
        out.println("    " + Utility.startTag(strFieldName) + strFieldData + Utility.endTag(strFieldName));
        return bFieldsFound;
    }
}
