/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

/**
* @(#)Screen.java 0.00 12-Feb-97 Don Corley
*
* Copyright (c) 2009 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.ReportToolbar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;

/**
 * This is the base screen for reports.
 * This class can simply output the record in report format.
 */
public class ReportScreen extends DualReportScreen
{
    /**
     * Constructor.
     */
    public ReportScreen()
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
    public ReportScreen(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
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
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);

        this.resizeToContent(this.getTitle());
    }
    /**
     * Throw up a NAV Toolbar if no toolbars yet.
     * @return The toolbar.
     */
    public ToolScreen addToolbars()
    {   // Override this to add (call this) or replace (don't call) this default toolbar.
        return new ReportToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
    }
    /**
     * The title for this screen.
     * @return The title
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    {
        String strTitle = "Query"; // Default
        Record record = this.getMainRecord();
        if (record != null)
            strTitle = record.getRecordName() + ' ' + strTitle;
        return strTitle;
    }
    /**
     * Get the command string that will restore this screen.
     * @return The screen URL
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        if ((this.getClass().getName().equals(ReportScreen.class.getName())) && (this.getMainRecord() != null))
            strURL = this.addURLParam(strURL, DBParams.RECORD, this.getMainRecord().getClass().getName());
        else
            strURL = this.addURLParam(strURL, DBParams.SCREEN, this.getClass().getName());
        try   {
            if (this.getMainRecord() != null)
                if ((this.getMainRecord().getEditMode() == Constants.EDIT_IN_PROGRESS) || (this.getMainRecord().getEditMode() == Constants.EDIT_CURRENT))
                {
                    String strBookmark = this.getMainRecord().getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                    strURL = this.addURLParam(strURL, DBConstants.STRING_OBJECT_ID_HANDLE, strBookmark);
                }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return strURL;
    }
}
