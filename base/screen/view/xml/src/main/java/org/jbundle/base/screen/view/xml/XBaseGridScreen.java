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

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BaseGridScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.ReportBreakScreen;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.base.util.XMLTags;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;


/**
 * The class for displaying records in a report (horizontal) format.
 * The concrete classes that extend this class are GridScreen and XmlHtmlScreen.
 */
public class XBaseGridScreen extends XBaseScreen
{

    /**
     * Constructor.
     */
    public XBaseGridScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XBaseGridScreen(ScreenField model, boolean bEditableControl)
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
     * Get the name of the stylesheet.
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getStylesheetName()
    {
        return "report";
    }
    /**
     * Get the print options (view defined).
     * @return The HTML options.
     * @exception DBException File exception.
     */
    public int getPrintOptions() throws DBException
    {
        int iPrintOptions = super.getPrintOptions();
        if (HtmlConstants.DISPLAY.equalsIgnoreCase(this.getProperty(HtmlConstants.FORMS)))
            iPrintOptions = iPrintOptions | HtmlConstants.REPORT_SCREEN;    // In report mode
        return iPrintOptions;
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartForm(PrintWriter out, int iPrintOptions)
    {
        super.printControlStartForm(out, iPrintOptions);
        
        BasePanel scrHeading = ((BaseGridScreen)this.getScreenField()).getReportHeading();
        if (scrHeading != null)
        {
            out.println(Utility.startTag(XMLTags.HEADING));
            scrHeading.printControl(out, iPrintOptions | HtmlConstants.HEADING_SCREEN);
            out.println(Utility.endTag(XMLTags.HEADING));
        }

        Record record = ((BaseGridScreen)this.getScreenField()).getMainRecord();
        if (record == null)
            return;
        String strRecordName = record.getTableNames(false);
        if ((strRecordName == null) || (strRecordName.length() == 0))
        	strRecordName = "Record";
        out.println(Utility.startTag(XMLTags.DETAIL + " name=\"" + strRecordName + "\""));
        this.printHeadingFootingControls(out, iPrintOptions | HtmlConstants.HEADING_SCREEN);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndForm(PrintWriter out, int iPrintOptions)
    {
        BasePanel scrDetail = ((BaseGridScreen)this.getScreenField()).getReportDetail();

        this.printHeadingFootingControls(out, iPrintOptions | HtmlConstants.FOOTING_SCREEN);
        if (scrDetail != null)
        {
            scrDetail.printControl(out, iPrintOptions | HtmlConstants.DETAIL_SCREEN);
        }
        Record record = ((BaseGridScreen)this.getScreenField()).getMainRecord();
        if (record != null)
        	out.println(Utility.endTag(XMLTags.DETAIL));

        BasePanel scrFooting = ((BaseGridScreen)this.getScreenField()).getReportFooting();
        if (scrFooting != null)
        {
            out.println(Utility.startTag(XMLTags.FOOTING));
            scrFooting.printControl(out, iPrintOptions | HtmlConstants.FOOTING_SCREEN);
            out.println(Utility.endTag(XMLTags.FOOTING));
        }

        super.printControlEndForm(out, iPrintOptions);
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public int printStartGridScreenData(PrintWriter out, int iPrintOptions)
    {
        out.println(Utility.startTag(XMLTags.DATA));
        return iPrintOptions;
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndGridScreenData(PrintWriter out, int iPrintOptions)
    {
        out.println(Utility.endTag(XMLTags.DATA));
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions)
    {
        super.printDataStartForm(out, iPrintOptions | HtmlConstants.REPORT_SCREEN);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions)
    {
        super.printDataEndForm(out, iPrintOptions | HtmlConstants.REPORT_SCREEN);
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordGridData(PrintWriter out, int iPrintOptions)
    {
        out.println(Utility.startTag(XMLTags.DETAIL));
        out.println(Utility.startTag(XMLTags.FILE));
    }
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordGridData(PrintWriter out, int iPrintOptions)
    {
        out.println(Utility.endTag(XMLTags.FILE));
        out.println(Utility.endTag(XMLTags.DETAIL));
    }
    /**
     * Display the start record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordData(Rec record, PrintWriter out, int iPrintOptions)
    {
        String strRecordName = record.getTableNames(false);
        if ((strRecordName == null) || (strRecordName.length() == 0))
        	strRecordName = "Record";
        String strID = DBConstants.BLANK;
        if (record.getCounterField() != null)
            strID = " id=\"" + record.getCounterField().toString() + "\" ";
        out.println(Utility.startTag(strRecordName + strID));
    }
    /**
     * Display the end record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordData(Rec record, PrintWriter out, int iPrintOptions)
    {
        String strRecordName = record.getTableNames(false);
        if ((strRecordName == null) || (strRecordName.length() == 0))
        	strRecordName = "Record";
        out.println(Utility.endTag(strRecordName));
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @return True if a heading or footing exists.
     * @exception DBException File exception.
     */
    public boolean printHeadingFootingControls(PrintWriter out, int iPrintOptions)
    {
        boolean bIsBreak = false;
        int iNumCols = ((BasePanel)this.getScreenField()).getSFieldCount();
        for (int iIndex = 0; iIndex < iNumCols; iIndex++)
        {
            ScreenField sField = ((BasePanel)this.getScreenField()).getSField(iIndex);
            boolean bPrintControl = this.getScreenField().isPrintableControl(sField, iPrintOptions);
            if (!(sField instanceof ReportBreakScreen))
                bPrintControl = false;
            if (bPrintControl)
                sField.printControl(out, iPrintOptions);
        }
        return bIsBreak;
    }
}
