/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.BaseParserScreen;
import org.jbundle.base.screen.model.report.parser.XMLParser;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.db.Constants;


/**
 *  HTML Display screen (simply display some HTML in the content area of a screen).
 *  This screen serves a dual purpose; first, it allows the user to enter data in the parameter
 *  form (if isPrintReport() == false), then it outputs the report as Html (when isPrintReport() == true).
 *  WARNING: Be very careful; This screen is used differently when it is used for HTML or a Screen.
 *  When it is a screen, it just throws up an HTML Editor window and asks the http server for the
 *  URL that it is suppose to show.
 *  When it is running on the server, it actually formats and builds the html help page.
 */
public class HBaseParserScreen extends HDualReportScreen
{

    /**
     * Constructor.
     */
    public HBaseParserScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HBaseParserScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
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
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iHtmlAttributes)
    {
        boolean bFieldsFound = false;
        String strXml = this.getProperty(DBParams.XML);                // Html page
        if (strXml == null)
            strXml = this.getProperty(DBParams.TEMPLATE);
        if (DBConstants.BLANK.equals(strXml))
            strXml = null;
        if (strXml != null)
        {
            if ((strXml.indexOf(':') == -1) && (strXml.charAt(0) != '/'))
            {
                String strLanguage = ((BaseApplication)this.getTask().getApplication()).getLanguage(false);
                if (strLanguage == null)
                    strLanguage = Constants.BLANK;
                if (strLanguage.length() > 0)
                    strLanguage += "/";
                strXml = strLanguage + strXml;  // DBConstants.kxxx;
            }
            InputStream streamIn = this.getTask().getInputStream(strXml);
            this.parseStreamPrintHtml(out, streamIn);
        }
        else
            return super.printData(out, HtmlConstants.HTML_DISPLAY);     // DO print screen
        return bFieldsFound;
    }
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void parseStreamPrintHtml(PrintWriter out, InputStream streamIn)
    {
        XMLParser parser = ((BaseParserScreen)this.getScreenField()).getXMLParser();
        if (parser != null)
        {
            parser.printHtmlData(out, streamIn);
            parser.free();
            parser = null;
        }
        else
        {
            Utility.transferURLStream(null, null, new InputStreamReader(streamIn), out);
        }
    }
    /**
     * Print the top nav menu.
     * @exception DBException File exception.
     */
    public void printHtmlLogo(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        String strXml = this.getProperty("xml");                // Html page
        if ((strXml != null) && (strXml.length() > 0))
            ((BaseParserScreen)this.getScreenField()).setURL(((BaseParserScreen)this.getScreenField()).addURLParam(Constants.BLANK, "xml", strXml));
        super.printHtmlLogo(out, reg);
    }
}
