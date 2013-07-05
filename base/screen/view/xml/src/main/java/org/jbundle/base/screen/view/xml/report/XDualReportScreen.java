/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.ResourceBundle;

import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.DualReportScreen;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;


/**
 *  HTML Display screen (simply display some HTML in the content area of a screen).
 *  This screen serves a dual purpose; first, it allows the user to enter data in the parameter
 *  form (if isPrintReport() == false), then it outputs the report as Html (when isPrintReport() == true).
 *  WARNING: Be very careful; This screen is used differently when it is used for HTML or a Screen.
 *  When it is a screen, it just throws up an HTML Editor window and asks the http server for the
 *  URL that it is suppose to show.
 *  When it is running on the server, it actually formats and builds the html help page.
 */
public class XDualReportScreen extends XBaseReportScreen
{

    /**
     * Constructor.
     */
    public XDualReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XDualReportScreen(ScreenField model, boolean bEditableControl)
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
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getStylesheetName()
    {
        if (HtmlConstants.DISPLAY.equalsIgnoreCase(this.getProperty(HtmlConstants.FORMS)))
            return super.getStylesheetName();
        else
            return "form";  // Form for data input
    }
    /**
     * Output the main screen.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        if (((DualReportScreen)this.getScreenField()).isPrintReport())
        {   // forms = display - Display the report
            super.printScreen(out, reg);
        }
        else
        {   // forms != display - Display the toolbars
            int iPrintOptions = this.getScreenField().getPrintOptions();
            this.printZmlToolbarControls(out, iPrintOptions);
            this.printZmlToolbarData(out, iPrintOptions);
        }
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartForm(PrintWriter out, int iPrintOptions)
    {
        super.printControlStartForm(out, iPrintOptions);
//        out.println(Utility.startTag(XMLTags.CONTROLS));        
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndForm(PrintWriter out, int iPrintOptions)
    {
        super.printControlEndForm(out, iPrintOptions);
        //        out.println(Utility.endTag(XMLTags.CONTROLS));
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
//x        out.println(Utility.startTag(XMLTags.DATA));
        boolean bParamsFound = super.printData(out, iPrintOptions);
//x        out.println(Utility.endTag(XMLTags.DATA));
        return bParamsFound;
    }
}
