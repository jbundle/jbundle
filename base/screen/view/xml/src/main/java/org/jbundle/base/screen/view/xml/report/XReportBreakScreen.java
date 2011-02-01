package org.jbundle.base.screen.view.xml.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.ReportBreakScreen;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.base.util.XMLTags;
import org.jbundle.model.DBException;


/**
 * A Report heading screen.
 */
public class XReportBreakScreen extends XHeadingScreen
{

    /**
     * Constructor.
     */
    public XReportBreakScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XReportBreakScreen(ScreenField model, boolean bEditableControl)
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
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartForm(PrintWriter out, int iPrintOptions)
    {
      //?        Record record = ((ReportBreakScreen)this.getScreenField()).getMainRecord();
      //?        String strFile = record.getTableNames(false);
          if ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
              out.println(Utility.startTag(XMLTags.FOOTING));
          else
              out.println(Utility.startTag(XMLTags.HEADING));
          out.println(Utility.startTag(XMLTags.CONTROLS));
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndForm(PrintWriter out, int iPrintOptions)
    {
        out.println(Utility.endTag(XMLTags.CONTROLS));
        if ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
            out.println(Utility.endTag(XMLTags.FOOTING));
        else
            out.println(Utility.endTag(XMLTags.HEADING));
    }
    /**
     * Print this field's data in XML format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions)
    {
        Record record = ((ReportBreakScreen)this.getScreenField()).getMainRecord();
        String strFile = record.getTableNames(false);
        if ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
            out.println(Utility.startTag(XMLTags.FOOTING));
        else
            out.println(Utility.startTag(XMLTags.HEADING));
        out.println(Utility.startTag(XMLTags.FILE));
        out.println(Utility.startTag(strFile));
    }
    /**
     * Print this field's data in XML format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions)
    {
        Record record = ((ReportBreakScreen)this.getScreenField()).getMainRecord();
        String strFile = record.getTableNames(false);
        out.println(Utility.endTag(strFile));
        out.println(Utility.endTag(XMLTags.FILE));
        if ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
            out.println(Utility.endTag(XMLTags.FOOTING));
        else
            out.println(Utility.endTag(XMLTags.HEADING));
    }
}
