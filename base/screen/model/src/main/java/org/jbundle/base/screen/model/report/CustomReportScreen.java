/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

/**
* @(#)Screen.java 0.00 12-Feb-97 Don Corley
*
* Copyright © 2012 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import java.util.Map;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.ReportToolbar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * This is the base screen for custom reports.
 * You can build a report that prints the controls--as laid out on the report.
 * This class has the ability to set up a custom control to be printed.
 * This is usually used for special format reports, such as labels.
 */
public class CustomReportScreen extends BaseReportScreen
{
    /**
     * Constructor.
     */
    public CustomReportScreen()
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
    public CustomReportScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this.setAppending(false);    // By default

        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        
        int iErrorCode = this.checkSecurity();
        if ((iErrorCode != DBConstants.NORMAL_RETURN) && (iErrorCode != Constants.READ_ACCESS))
            return;

// Put the grid table in front of this record, so grid operations will work.
        Record gridRecord = this.getMainRecord();
// Even though grid table caches for me, I use the cache to take advantage of multiple reads.
        if (gridRecord != null)
            gridRecord.setOpenMode(gridRecord.getOpenMode() | DBConstants.OPEN_CACHE_RECORDS);    // Cache recently used records.

        BaseTable gridTable = gridRecord.getTable();
        if (!(gridTable instanceof GridTable))
            gridTable = new GridTable(null, gridRecord);
        gridRecord.close();

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
     * Throw up a NAV Toolbar if no toolbars yet.
     * @return The new ReportToolbar.
     */
    public ToolScreen addToolbars()
    {   // Override this to add (call this) or replace (don't call) this default toolbar.
        return new ReportToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
    }
}
