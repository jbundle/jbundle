/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)GridScreen.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.OnSelectHandler;
import org.jbundle.base.screen.model.menu.SBaseMenuBar;
import org.jbundle.base.screen.model.menu.SGridMenuBar;
import org.jbundle.base.screen.model.util.DisplayToolbar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * The window for displaying several records at once.
 */
public class BaseGridTableScreen extends BaseGridScreen
{

    /**
     * Constructor.
     */
    public BaseGridTableScreen()
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
    public BaseGridTableScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
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
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this.setAppending(true);    // By default

        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        
        int iErrorCode = this.checkSecurity();
        if ((iErrorCode != DBConstants.NORMAL_RETURN) && (iErrorCode != Constants.READ_ACCESS))
            return;

// Put the grid table in front of this record, so grid operations will work.
        Record gridRecord = this.getMainRecord();
// Even though grid table caches for me, I use the cache to take advantage of multiple reads.
        if (gridRecord != null)
            gridRecord.setOpenMode(gridRecord.getOpenMode() | DBConstants.OPEN_CACHE_RECORDS);    // Cache recently used records.
        if (!ScreenConstants.HTML_SCREEN_TYPE.equalsIgnoreCase(this.getViewFactory().getViewSubpackage()))
        {
            gridRecord.setupRecordListener(this, true, false);  // I need to listen for record changes
            BaseTable gridTable = gridRecord.getTable();
            if (!(gridTable instanceof GridTable))
                gridTable = new GridTable(null, gridRecord);
        // The record should not be in refresh mode (the table model handles read-rewrites).
            gridRecord.setOpenMode(gridRecord.getOpenMode() & ~DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);  // Must have for GridTable to re-read.
            gridRecord.close();
        }

        if (gridRecord != null)
            gridRecord.setDisplayOption(false);     // Don't need to auto-display on change
        if ((m_iDisplayFieldDesc & ScreenConstants.SELECT_MODE) == ScreenConstants.SELECT_MODE)
            this.setEditing(false);     // Don't allow editing, if select mode
        if ((gridRecord.getOpenMode() & DBConstants.OPEN_READ_ONLY) == DBConstants.OPEN_READ_ONLY)
            this.setEditing(false);     // Don't allow editing, if read-only record
        if (this.getEditing() == false)
            this.setAppending(false);   // Don't allow appending if read-only (by default)
        if ((gridRecord.getOpenMode() & DBConstants.OPEN_APPEND_ONLY) == DBConstants.OPEN_APPEND_ONLY)
            this.setAppending(true);    // Do allow appending, if read-only record
        // Overriding class must add the columns and resize to content
    }
    /**
     * Free this screen's resources.
     */
    public void free()
    {
        if (m_screenFieldView != null)
        {   // Sorry, but I need to free the physical control before the sub-controls are released!
            m_screenFieldView.free();
            m_screenFieldView = null;
        }
        super.free();
    }
    /**
     * Throw up a NAV Toolbar if no toolbars yet.
     * @return The new toolbar screen.
     */
    public ToolScreen addToolbars()
    {   // Override this to add (call this) or replace (don't call) this default toolbar.
        return new DisplayToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
    }
    /**
     * Add the menus that belong with this screen.
     */
    public void addScreenMenus()
    {
        AppletScreen appletScreen = this.getAppletScreen();
        if (appletScreen != null)
        {
            ScreenField menuBar = appletScreen.getSField(0);
            if ((menuBar == null) || (!(menuBar instanceof SGridMenuBar)))
            {
                if (menuBar instanceof SBaseMenuBar)
                    menuBar.free();     // Wrong menu
                new SGridMenuBar(new ScreenLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), appletScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
            }
        }
    }
    /**
     * Requery the recordset.
     */
    public void reSelectRecords()
    {
        this.getScreenFieldView().reSelectRecords();
    }
    /**
     * Override to optimize the query.
     * @param bEditing If true, allow editing; if false, otomize the query by only selecting the displayed fields.
     */
    public void setEditing(boolean bEditing)
    {
        super.setEditing(bEditing);   // Do Object
        if (this.getEditing() == false)
            this.getMainRecord().selectScreenFields();  // Optimize the query by selecting only the display fields
    }
    /**
     * Find the sub-screen that uses this grid query and set for selection.
     * When you select a new record here, you read the same record in the SelectQuery.
     * @param recMaint The record which is synced on record change.
     * @param bUpdateOnSelect Do I update the current record if a selection occurs.
     * @return True if successful.
     */
    public boolean setSelectQuery(Record recMaint, boolean bUpdateOnSelect)
    {
        if (recMaint == null)
            return true;    // BaseTable Set!
        if (this.getMainRecord() != null)
            if (this.getMainRecord() != recMaint)
                if (this.getMainRecord().getBaseRecord().getTableNames(false).equals(recMaint.getTableNames(false)))
        {   // Only trigger when the grid table sends the selection message
            this.getMainRecord().addListener(new OnSelectHandler(recMaint, bUpdateOnSelect, DBConstants.USER_DEFINED_TYPE));
            return true;    // BaseTable Set!
        }
        return false;
    }
    /**
     * Get the command string that will restore this screen.
     * @return The URL for this screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        if (this.getClass().getName().equals(GridScreen.class.getName()))
            strURL = this.addURLParam(strURL, DBParams.RECORD, this.getMainRecord().getClass().getName());
        else
            strURL = this.addURLParam(strURL, DBParams.SCREEN, this.getClass().getName());
        return strURL;
    }
    /**
     * Create a data entry screen with this main record.
     * (null means use this screen's grid record)
     * @param record The main record for the new form.
     * @param iDocMode The document type of the new form.
     * @param bReadCurrentRecord Sync the new screen with my current record?
     * @param bUseSameWindow Use the same window?
     * @return true if successful.
     */
    public BasePanel onForm(Record record, int iDocMode, boolean bReadCurrentRecord, int bUseSameWindow, Map<String,Object> properties)
    {
        if (record == null)
            record = this.getMainRecord();
        if (record == this.getMainRecord())
        {
            try   {
                int iSelection = this.getScreenFieldView().getSelectedRow();
                if (iSelection != -1)
                {
                    Record recAtTarget = null;
                    this.finalizeThisScreen();  // Validate current control, update record, get ready to close screen.
                    recAtTarget = (Record)((GridTable)record.getTable()).get(iSelection);
                    if (recAtTarget == null)
                        if (record != null)
                            if ((iDocMode & ScreenConstants.DETAIL_MODE) == ScreenConstants.DETAIL_MODE)
                                if (record.getEditMode() == DBConstants.EDIT_ADD)
                                    if (record.isModified())
                    {
                        record.add();
                        Object bookmark = record.getLastModified(DBConstants.BOOKMARK_HANDLE);
                        recAtTarget = record.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                    }
                }
                else
                    record.addNew();
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        return super.onForm(record, iDocMode, bReadCurrentRecord, bUseSameWindow, properties);
    }
    /**
     * Display the initial query.
     * @param bDisplayInitialQuery If false, don't query the table and display the grid until the user requerys.
     */
    public void setDisplayInitialQuery(boolean bDisplayInitialQuery)
    {
        // TODO - Add the code to suppress the initial query if flag set.
    }
    /**
     * Enable or disable all fields.
     * @param bEditing True to enable all the fields (for input).
     */
    public void setEnabled(boolean bEditing)
    {
        super.setEnabled(bEditing);
        if (!bEditing)
        {   // Turn the nav buttons back on!
            for (int iFieldSeq = 0; iFieldSeq < this.getNavCount(); iFieldSeq++)
            {
                ScreenField sField = this.getSField(iFieldSeq);
                if (!(sField instanceof ToolScreen))
                    sField.setEnabled(true);
            }
            if (this.getScreenFieldView().getControl() != null)
                this.getScreenFieldView().getControl().setEnabled(true);    // Sorry, but the JTable needs to be enabled always
        }
    }
}
