/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.ResourceBundle;

import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.HelpScreen;
import org.jbundle.model.app.program.db.ClassInfoModel;
import org.jbundle.model.screen.ScreenComponent;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 */
public class XTechHelpScreen extends XHelpScreen
{

    /**
     * Constructor.
     */
    public XTechHelpScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XTechHelpScreen(ScreenField model,boolean bEditableControl)
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
     * Get the name of the stylesheet.
     * @return The filename of the xsl stylesheet.
     */
    public String getStylesheetName()
    {
        return "tech";
    }
    /**
     * Get the top menu.
     */
    public void printHtmlHeaderArea(PrintWriter out)
    {
        ClassInfoModel classInfo = ((HelpScreen)this.getScreenField()).getClassInfo();
        
        if (classInfo != null)
            if (classInfo.isValidRecord())
        {
            String strHeader =
    " <title>Technical Help Screen - " + Utility.encodeXML(classInfo.getClassDesc()) + " </title>" +
    //+"    <meta-keywords>" + Utility.encodeXML(m_classInfo.getField(ClassInfo.KEYWORDS).toString()) + "</meta-keywords>" +
    " <meta-description>" + Utility.encodeXML(classInfo.getClassExplain()) + "</meta-description>";
            out.println(strHeader);
        }
    }
    /**
     * Output the main screen.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
    {
        ClassInfoModel classInfo = ((HelpScreen)this.getScreenField()).getClassInfo();
        if (classInfo != null)
            if (classInfo.isValidRecord())
            	classInfo.printScreen(out, reg);
    }
}
