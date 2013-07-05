/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

/**
 * @(#)ReportScreen.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.BaseGridScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;


/**
 * ReportScreen - The abstract class for displaying records in a report (horizontal) format.
 * The concrete classes that extend this class are GridScreen and XmlHtmlScreen.
 */
public class BaseReportScreen extends BaseGridScreen
{

    /**
     * Constructor.
     */
    public BaseReportScreen()
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
    public BaseReportScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Display this screen in html input format.
     *  returns true if default params were found for this form.
     * @param out The html out stream.
     * @param iHtmlAttributes The Html attributes.
     * @return True if fields were found.
     * @exception DBException File exception.
     */
    public String addScreenParams(BasePanel screen, String strURL)
    {
        int iNumCols = screen.getSFieldCount();
        for (int iIndex = 0; iIndex < iNumCols; iIndex++)
        {
            ScreenField sField = screen.getSField(iIndex);
            boolean bPrintControl = true;
            if (sField instanceof BasePanel)
            {
                strURL = this.addScreenParams((BasePanel)sField, strURL);
                bPrintControl = false;      // ie., Don't print buttons
            }
            if (sField.getConverter() == null)
                bPrintControl = false;
            if (screen == this)
                bPrintControl = false;  // My children are the report detail (Not params).
            if (!sField.isInputField())
            	bPrintControl = false;
            if (!sField.isEnabled())
            	bPrintControl = false;
            if (bPrintControl)
                if (sField.getScreenFieldView() != null)
            {
                String strData = sField.getSFieldValue(false, false);    // Match HTML submit (input format/go through converters).
                strURL = Utility.addURLParam(strURL, sField.getSFieldParam(null, false), strData);   // Don't need outside frame stuff in a window
            }
        }
        return strURL;
    }
}
