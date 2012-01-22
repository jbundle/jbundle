/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Utility;
import org.jbundle.base.util.XMLTags;
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
public class XBaseParserScreen extends XDualReportScreen
{

    /**
     * Constructor.
     */
    public XBaseParserScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XBaseParserScreen(ScreenField model, boolean bEditableControl)
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
            strXml = DBConstants.DEFAULT_HELP_FILE;
        if (strXml != null)
        {
            if ((strXml.indexOf(':') == -1) && (strXml.charAt(0) != '/'))
            {
            	if (strXml.indexOf('/') == -1)
            		strXml = DBConstants.DEFAULT_HELP_FILE.substring(0, DBConstants.DEFAULT_HELP_FILE.lastIndexOf('/') + 1) + strXml;
                String strLanguage = ((BaseApplication)this.getTask().getApplication()).getLanguage(false);
                if ((strLanguage == null) || (strLanguage.equalsIgnoreCase("en")))
                    strLanguage = Constants.BLANK;
                if (strLanguage.length() > 0)
                    strLanguage += "/";
                strXml = strLanguage + strXml;  // DBConstants.kxxx;
            }
            boolean bHTML = false;
            if ((strXml.endsWith(".html"))
                || (strXml.endsWith(".htm")))
                    bHTML = true;
            if (bHTML)
                out.println(Utility.startTag(XMLTags.HTML));
            InputStream streamIn = this.getTask().getInputStream(strXml);
            Utility.transferURLStream(null, null, new InputStreamReader(streamIn), out);
            if (bHTML)
                out.println(Utility.endTag(XMLTags.HTML));
        }
        else
            return super.printData(out, HtmlConstants.HTML_DISPLAY);     // DO print screen
        return bFieldsFound;
    }
}
