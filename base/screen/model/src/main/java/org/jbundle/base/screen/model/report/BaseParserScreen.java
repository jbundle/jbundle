/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

/**
 *  XmlHtmlScreen.
 *  Copyright � 1997 jbundle.org. All rights reserved.
 */

import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SStaticString;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.report.parser.XMLParser;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;


/**
 *  HTML Display screen (simply display some HTML in the content area of a screen).
 *  This screen serves a dual purpose; first, it allows the user to enter data in the parameter
 *  form (if isPrintReport() == false), then it outputs the report as Html (when isPrintReport() == true).
 *  WARNING: Be very careful; This screen is used differently when it is used for HTML or a Screen.
 *  When it is a screen, it just throws up an HTML Editor window and asks the http server for the
 *  URL that it is suppose to show.
 *  When it is running on the server, it actually formats and builds the html help page.
 */
public class BaseParserScreen extends DualReportScreen
{

    /**
     * Constructor.
     */
    public BaseParserScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public BaseParserScreen(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     *  Initialize the member fields.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);

        this.resizeToContent(this.getTitle());
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     *  Specify the fields to display.
     */
    public void setupSFields()
    {	// Can't be empty so layout will work
    	new SStaticString(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, DBConstants.BLANK);
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        return null;	// No toolbars
    }
    /**
     * Output this screen using HTML.
     * Override this to get the correct XML Parser.
     * @return The XML parser.
     */
    public XMLParser getXMLParser()
    {
        return null;    // return new XMLParser(this);
    }
    /**
     * Get the command string that will restore this screen.
     * @return The URL to recreate this screen.
     */
    public String getScreenURL()
    {
        if (m_strURL != null)
            return m_strURL;
        else
            return super.getScreenURL();
    }
    protected String m_strURL = null;
    /**
     * Set up the physical control (that implements Component).
     * @param strURL The URL to display for this screen.
     */
    public void setURL(String strURL)
    {
        m_strURL = strURL;
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        if (this.getProperty(DBParams.XML) != null)
            return this.getScreenFieldView().printData(out, iPrintOptions);
        else
            return super.printData(out, iPrintOptions);
    }
    /**
     * Get the print options (view defined).
     * @return The HTML options.
     * @exception DBException File exception.
     */
    public int getPrintOptions()
        throws DBException
    {
        int iHtmlOptions = super.getPrintOptions();
        if (this.getProperty(DBParams.XML) != null)
        {
            iHtmlOptions = iHtmlOptions & ~HtmlConstants.PRINT_TOOLBAR_BEFORE;  // No
            iHtmlOptions = iHtmlOptions & ~HtmlConstants.DONT_PRINT_SCREEN;     // Yes (look > this says DONT)
            iHtmlOptions = iHtmlOptions & ~HtmlConstants.PRINT_TOOLBAR_AFTER;  // No
        }
        return iHtmlOptions;
    }
}
