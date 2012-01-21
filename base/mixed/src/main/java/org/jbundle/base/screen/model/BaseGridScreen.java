/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)ReportScreen.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.report.ReportBreakScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.GridScreenParent;
import org.jbundle.thin.base.db.Converter;


/**
 * ReportScreen - The abstract class for displaying records in a report (horizontal) format.
 * The concrete classes that extend this class are GridScreen and XmlHtmlScreen.
 */
public class BaseGridScreen extends BaseScreen
    implements GridScreenParent
{
    /**
     * Max items to display on Report/Grid Report.
     */
    public static final String LIMIT_PARAM = "limit";
    /**
     *
     */
    public static final String EDIT_PARAM = "edit";
    /**
     * Default limit.
     */
    public static final int DEFAULT_LIMIT = 200;
    /**
     * Unlimited limit.
     */
    public static final String LIMIT_UNLIMITED = "-1";
    /**
     * The heading for this report.
     */
    protected BaseScreen m_scrReportHeading = null;
    /**
     * The detail for this report.
     */
    protected BaseScreen m_scrReportDetail = null;
    /**
     * The heading for this report.
     */
    protected BaseScreen m_scrReportFooting = null;

    /**
     * Constructor.
     */
    public BaseGridScreen()
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
    public BaseGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Open the files and setup the screen.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record mainRecord, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(mainRecord, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Open the files and setup the screen.
     */
    public void addListeners()
    {
        if (ScreenConstants.HTML_SCREEN_TYPE.equalsIgnoreCase(this.getViewFactory().getViewSubpackage()))
            if (this.getProperty(EDIT_PARAM) == null)
                this.setEditing(false);     // For HTML default is no editing (specify explicitly by edit=true)
        super.addListeners();
    }
    /**
     * Do I include a description for sField?
     * Generally yes, except for on GridScreens.
     * @param sField field to check for description includes.
     * @return True if successful.
     */
    public boolean getDisplayFieldDesc(ScreenField sField)
    {
        if (sField instanceof SButtonBox)
            if (((SButtonBox)sField).getImageButtonName() == null)
                return true;    // Buttons need their descriptions (if there is no icon) in grid screens
        return false; // Descriptions are in the header bar (Don't put descriptions in the controls)!
    }
    /**
     * Set up the screen fields (default = set them all up for the current record).
     * @param converter The converter to creat a default screen field for.
     */
    public Object addColumn(Converter converter)
    {
        return converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Process the "Delete" toolbar command.
     * @return  true    If command was handled
     */
    public boolean onDelete()
    {
        if (this.getEditing() == false)
        {   // Can't delete on a disabled grid.
            String strError = "Can't Delete from disabled grid";
            if (this.getTask() != null)
                if (this.getTask().getApplication() != null)
                    strError = ((BaseApplication)this.getTask().getApplication()).getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strError);
            this.displayError(strError);
            return false;
        }
        return super.onDelete();
    }
    /**
     * Get the heading for this report.
     * @return The heading screen (or null).
     */
    public final BaseScreen getReportHeading()
    {
        if (m_scrReportHeading == null)
            m_scrReportHeading = this.addReportHeading();
        return m_scrReportHeading;
    }
    /**
     * Get the heading for this report.
     * @return The heading screen (or null).
     */
    public BaseScreen addReportHeading()
    {
        return null;
    }
    /**
     * Get the Detail for this report.
     * @return The heading screen (or null).
     */
    public final BaseScreen getReportDetail()
    {
        if (m_scrReportDetail == null)
            m_scrReportDetail = this.addReportDetail();
        return m_scrReportDetail;
    }
    /**
     * Get the heading for this report.
     * <p>Note: The detail screen has to have a way to know what record is the detail record, so you have to either:
     * <pre>
     * return this.getRecord(AcctDetail.kAcctDetailFile);
     * </pre>
     * in getMainRecord(), or set up the detail file(s) and listener(s) in the detail screen.
     * @return The detail screen (or null).
     */
    public BaseScreen addReportDetail()
    {
        return null;
    }
    /**
     * Get the footing for this report.
     * @return The heading screen (or null).
     */
    public final BaseScreen getReportFooting()
    {
        if (m_scrReportFooting == null)
            m_scrReportFooting = this.addReportFooting();
        return m_scrReportFooting;
    }
    /**
     * Get the footing for this HTML report.
     * @return The footing screen (or null).
     */
    public BaseScreen addReportFooting()
    {
        return null;
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        boolean bFieldsFound = false;
        iPrintOptions = this.printStartGridScreenData(out, iPrintOptions);

        Record record = this.getMainRecord();
        if (record == null)
            return bFieldsFound;
        int iNumCols = this.getSFieldCount();

        int iLimit = DEFAULT_LIMIT;
        String strParamLimit = this.getProperty(LIMIT_PARAM);   // Display screen
        if ((strParamLimit != null) && (strParamLimit.length() > 0))
        {
            try   {
                iLimit = Integer.parseInt(strParamLimit);
            } catch (NumberFormatException e) {
                iLimit = DEFAULT_LIMIT;
            }
        }

        int iFieldCount = 0;
        for (int i = 0; i < iNumCols; i++)
        {
            if (!(this.getSField(i) instanceof BasePanel))
                iFieldCount++;
        }
        if (iFieldCount > 0)
        {
            bFieldsFound = true;
            BasePanel scrHeading = this.getReportHeading();
            if (scrHeading != null)
                scrHeading.printData(out, iPrintOptions | HtmlConstants.MAIN_HEADING_SCREEN);
            
            BasePanel scrDetail = this.getReportDetail();

            this.printStartRecordGridData(out, iPrintOptions);
            this.printNavButtonControls(out, iPrintOptions);

            // for entire data
            try {
                boolean bHeadingFootingExists = this.isAnyHeadingFooting();

                record = this.getNextRecord(out, iPrintOptions, true, bHeadingFootingExists);

                while (record != null)
                {
                    this.printStartRecordData(record, out, iPrintOptions);

                    if (bHeadingFootingExists)
                        this.printHeadingFootingData(out, iPrintOptions | HtmlConstants.HEADING_SCREEN | HtmlConstants.DETAIL_SCREEN);

                    bFieldsFound = super.printData(out, (iPrintOptions & (~HtmlConstants.HTML_ADD_DESC_COLUMN)) | HtmlConstants.MAIN_SCREEN); // Don't add description

                    if (scrDetail != null)
                        scrDetail.printData(out, iPrintOptions | HtmlConstants.DETAIL_SCREEN);

                    Record recordNext = this.getNextRecord(out, iPrintOptions, false, bHeadingFootingExists);

                    this.printEndRecordData(record, out, iPrintOptions);
                    
                    record = recordNext;

                    if (--iLimit == 0)
                        break;  // Max rows displayed
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            }

            this.printEndRecordGridData(out, iPrintOptions);

            BasePanel scrFooting = this.getReportFooting();
            if (scrFooting != null)
                scrFooting.printData(out, iPrintOptions | HtmlConstants.MAIN_FOOTING_SCREEN);
        }

        this.printEndGridScreenData(out, iPrintOptions);
        return bFieldsFound;
    }
    /**
     * Get the next record.
     * This is the special method for a report. It handles breaks by disabling all listeners
     * except filter listeners, then reenabling and calling the listeners after the footing
     * has been printed, so totals, etc will be in the next break.
     * @param out The out stream.
     * @param iPrintOptions Print options.
     * @param bFirstTime Reading the first record?
     * @param bHeadingFootingExists Does a break exist (skips all the fancy code if not).
     * @return The next record (or null if EOF).
     */
    public Record getNextRecord(PrintWriter out, int iPrintOptions, boolean bFirstTime, boolean bHeadingFootingExists)
        throws DBException
    {
        Object[] rgobjEnabled = null;
        boolean bAfterRequery = !this.getMainRecord().isOpen();
        if (!this.getMainRecord().isOpen())
            this.getMainRecord().open();    // Make sure any listeners are called before disabling.
        if (bHeadingFootingExists)
            rgobjEnabled = this.getMainRecord().setEnableNonFilter(null, false, false, false, false, true);

        Record record = this.getNextGridRecord(bFirstTime);

        if (bHeadingFootingExists)
        {
            boolean bBreak = this.printHeadingFootingData(out, iPrintOptions | HtmlConstants.FOOTING_SCREEN | HtmlConstants.DETAIL_SCREEN);
            this.getMainRecord().setEnableNonFilter(rgobjEnabled, (record != null), bBreak, bFirstTime | bAfterRequery, (record == null), true);
        }
        return record;

    }
    /**
     * Get the next grid record.
     * @param bFirstTime If true, I want the first record.
     * @return the next record (or null if EOF).
     */
    public Record getNextGridRecord(boolean bFirstTime) throws DBException
    {
        Record record = this.getMainRecord();
        BaseTable table = record.getTable();
        if (bFirstTime)
            table.close();
        boolean bHasNext = table.hasNext();
        if (!bHasNext)
            return null;
        return (Record)table.next();
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @return True if a heading or footing exists.
     * @exception DBException File exception.
     */
    public boolean printHeadingFootingData(PrintWriter out, int iPrintOptions)
    {
        boolean bIsBreak = false;
        int iNumCols = this.getSFieldCount();
        for (int iIndex = 0; iIndex < iNumCols; iIndex++)
        {
            ScreenField sField = this.getSField(iIndex);
            boolean bPrintControl = this.isPrintableControl(sField, iPrintOptions);
//x            if (!(sField instanceof ReportBreakScreen))
//x                bPrintControl = false;
            if (sField == this.getReportHeading())
                bPrintControl = false;
            if (sField == this.getReportFooting())
                bPrintControl = false;
            if (sField == this.getReportDetail())
            	bPrintControl = false;
            if ((iPrintOptions & HtmlConstants.HEADING_SCREEN) != 0)
                if ((sField.getDisplayFieldDesc() & HtmlConstants.HEADING_SCREEN) == 0)
                    bPrintControl = false;
            if ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) != 0)
                if ((sField.getDisplayFieldDesc() & HtmlConstants.FOOTING_SCREEN) == 0)
                    bPrintControl = false;
            if (bPrintControl)
            {
                sField.printData(out, iPrintOptions);
                if (!bIsBreak)
                {
                	if (sField instanceof ReportBreakScreen)
                		bIsBreak = ((ReportBreakScreen)sField).isLastBreak();
                	else
                		bIsBreak = true;	// Always assume a break if this is not a break screen
                }
            }
        }
        return bIsBreak;
    }
    /**
     * Display this sub-control in html input format?
     * @param iPrintOptions The view specific print options.
     * @return True if this sub-control is printable.
     */
    public boolean isPrintableControl(ScreenField sField, int iPrintOptions)
    {
        boolean bPrintControl = super.isPrintableControl(sField, iPrintOptions);
        if ((iPrintOptions & HtmlConstants.MAIN_SCREEN) != 0)
            if (sField != null)
        {
            if (sField == this.getReportHeading())
                bPrintControl = false;
            if (sField == this.getReportFooting())
                bPrintControl = false;
            if ((sField.getDisplayFieldDesc() & HtmlConstants.HEADING_SCREEN) != 0)
                bPrintControl = false;
            if ((sField.getDisplayFieldDesc() & HtmlConstants.FOOTING_SCREEN) != 0)
                bPrintControl = false;
        }
        return bPrintControl;
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @return True if a heading or footing exists.
     * @exception DBException File exception.
     */
    public boolean isAnyHeadingFooting()
    {
        boolean bHeadingFootingExists = false;
        int iNumCols = this.getSFieldCount();
        for (int iIndex = 0; iIndex < iNumCols; iIndex++)
        {
            ScreenField sField = this.getSField(iIndex);
            if (sField == this.getReportHeading())
                continue;	// Report headings and footings don't count, only detail headings and footings
            if (sField == this.getReportFooting())
                continue;
            if (sField == this.getReportDetail())
            	continue;
            if ((sField.getDisplayFieldDesc() & HtmlConstants.HEADING_SCREEN) == 0)
                bHeadingFootingExists = true;   // Yes, there is a heading or footing.
            if ((sField.getDisplayFieldDesc() & HtmlConstants.FOOTING_SCREEN) == 0)
                bHeadingFootingExists = true;   // Yes, there is a heading or footing.
        }
        return bHeadingFootingExists;
    }
    @Override
    public void reSelectRecords() {
        // Not used here
    }
}
