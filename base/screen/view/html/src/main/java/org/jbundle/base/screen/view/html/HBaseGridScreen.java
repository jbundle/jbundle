/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.screen.model.BaseGridScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.DisplayToolbar;
import org.jbundle.base.screen.model.util.MaintToolbar;
import org.jbundle.base.screen.model.util.MenuToolbar;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.db.Constants;


/**
 * The class for displaying records in a report (horizontal) format.
 * The concrete classes that extend this class are GridScreen and XmlHtmlScreen.
 */
public class HBaseGridScreen extends HBaseScreen
{

    /**
     * Constructor.
     */
    public HBaseGridScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HBaseGridScreen(ScreenField model, boolean bEditableControl)
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
     * display this screen in html input format.
     * @return The HTML options.
     * @exception DBException File exception.
     */
    public int getPrintOptions()
        throws DBException
    {
        int iHtmlOptions = HtmlConstants.HTML_DISPLAY;
        if (((BaseGridScreen)this.getScreenField()).getEditing())
            iHtmlOptions |= HtmlConstants.HTML_INPUT;
        String strParamForm = this.getProperty(HtmlConstants.FORMS);        // Display record
        if ((strParamForm == null) || (strParamForm.length() == 0))
            strParamForm = HtmlConstants.BOTHIFDATA;

        // First, move any params up
        boolean bPrintReport = true;
        if ((strParamForm.equalsIgnoreCase(HtmlConstants.INPUT))
            || (strParamForm.equalsIgnoreCase(HtmlConstants.BOTHIFDATA))
            || (strParamForm.equalsIgnoreCase(HtmlConstants.BOTH)))
        {   // For these options, check to see if the input data has been entered.
            bPrintReport = false;
            boolean bScreenFound = false;
            int iNumCols = ((BaseGridScreen)this.getScreenField()).getSFieldCount();
            for (int iIndex = 0; iIndex < iNumCols; iIndex++)
            {
                ScreenField sField = ((BaseGridScreen)this.getScreenField()).getSField(iIndex);
                if (sField instanceof ToolScreen)
                    if ((!(sField instanceof DisplayToolbar)) &&
                        (!(sField instanceof MaintToolbar)) &&
                        (!(sField instanceof MenuToolbar)))
                {   // Output the Input screen
                    HScreenField vField = (HScreenField)sField.getScreenFieldView();
                    if (vField.moveControlInput(Constants.BLANK) == DBConstants.NORMAL_RETURN)  // I already moved them, but I need to know if they were passed
                        bPrintReport = true;
                    bScreenFound = true;
                }
            }
            if (!bScreenFound)
                bPrintReport = true;    // If no screen, say params were entered
            if (!bPrintReport)
                if (this.getProperty(DBConstants.STRING_OBJECT_ID_HANDLE) != null)
                    bPrintReport = true;    // Special Grid with a header record = found
        }
        if (strParamForm.equalsIgnoreCase(HtmlConstants.DISPLAY))
            iHtmlOptions |= HtmlConstants.HTML_DISPLAY;     // No toolbar, print report
        else if (strParamForm.equalsIgnoreCase(HtmlConstants.DATA))
            iHtmlOptions |= HtmlConstants.PRINT_TOOLBAR_BEFORE;   // Toolbar and report
        else if (strParamForm.equalsIgnoreCase(HtmlConstants.INPUT))
            iHtmlOptions |= HtmlConstants.PRINT_TOOLBAR_BEFORE | HtmlConstants.DONT_PRINT_SCREEN; // Toolbar, no report
        else if (strParamForm.equalsIgnoreCase(HtmlConstants.BOTH))
            iHtmlOptions |= HtmlConstants.PRINT_TOOLBAR_BEFORE;   // Toolbar and report
        else if (strParamForm.equalsIgnoreCase(HtmlConstants.BOTHIFDATA))
        {
            iHtmlOptions |= HtmlConstants.PRINT_TOOLBAR_BEFORE;     // Toolscreen is always displayed
            if (!bPrintReport)
                iHtmlOptions |= HtmlConstants.DONT_PRINT_SCREEN;
        }
        return iHtmlOptions;
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public int printStartGridScreenData(PrintWriter out, int iPrintOptions)
    {
        if ((iPrintOptions & HtmlConstants.HTML_ADD_DESC_COLUMN) != 0)
            out.println("<td></td>"); // No description needed for a table
        if ((iPrintOptions & HtmlConstants.HTML_IN_TABLE_ROW) != 0)
            out.println("<td>");
        iPrintOptions = iPrintOptions & (~HtmlConstants.HTML_IN_TABLE_ROW);     // sub-controls not in a table row
        iPrintOptions = iPrintOptions & (~HtmlConstants.HTML_ADD_DESC_COLUMN);  // Sub-controls - don't add desc.
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
        if ((iPrintOptions & HtmlConstants.HTML_IN_TABLE_ROW) != 0)
            out.println("</td>");
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordGridData(PrintWriter out, int iPrintOptions)
    {
        if ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) == HtmlConstants.DETAIL_SCREEN)
        {
            out.println("<tr>\n<td colspan=\"20\">");
        }
        out.println("<table border=\"1\">");
    }
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordGridData(PrintWriter out, int iPrintOptions)
    {
        out.println("</table>");
        if ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) == HtmlConstants.DETAIL_SCREEN)
        {
            out.println("</td>\n</tr>");
        }
    }
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printNavButtonControls(PrintWriter out, int iPrintOptions)
    {
        out.println("<tr>");
        int iNumCols = ((BaseGridScreen)this.getScreenField()).getSFieldCount();
        for (int i = 0; i < iNumCols; i++)
        {       // First, print the nav controls
            ScreenField sField = ((BaseGridScreen)this.getScreenField()).getSField(i);
            HScreenField sFieldView = (HScreenField)sField.getScreenFieldView();
            boolean bPrintControl = this.getScreenField().isPrintableControl(sField, iPrintOptions);
//x            if (sField instanceof BasePanel)
//x                bPrintControl = false;  // ?
            if (i < ((BaseGridScreen)this.getScreenField()).getNavCount())
                bPrintControl = true;
            if (bPrintControl)
                sFieldView.printHtmlHeading(out);
        }
        out.println("</tr>");
    }
    /**
     * Display the start record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordData(Rec record, PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the end record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordData(Rec record, PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions)
    {
        out.println("<tr>");
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions)
    {
        out.println("</tr>");
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartField(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndField(PrintWriter out, int iPrintOptions)
    {
    }
}
