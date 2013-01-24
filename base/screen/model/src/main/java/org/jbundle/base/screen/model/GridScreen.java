/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)GridScreen.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.filter.FileFilter;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ImageField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.MenuConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * The window for displaying several records at once.
 */
public class GridScreen extends BaseGridTableScreen
{
    /**
     * Nav buttons.
     */
    protected int m_iNavCount = 0;

    /**
     * Constructor.
     */
    public GridScreen()
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
    public GridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
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
        m_iNavCount = 0;                        // Nav buttons

        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);

        int iErrorCode = this.checkSecurity();
        if ((iErrorCode != DBConstants.NORMAL_RETURN) && (iErrorCode != Constants.READ_ACCESS))
            return;
        if (iErrorCode == Constants.READ_ACCESS)
        	this.setAppending(false);
        
        // Add in the columns
        m_iNavCount = this.getSFieldCount();
        this.addNavButtons();
        m_iNavCount = this.getSFieldCount() - m_iNavCount;  // Nav buttons

        this.getScreenFieldView().setupTableFromModel();

        this.resizeToContent(this.getTitle());
    }
    /**
     * Free this screen's resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Code this position and Anchor to add it to the LayoutManager.
     * @param position The location constant (see ScreenConstants).
     * @param setNewAnchor The anchor constant.
     * @return The new screen location object.
     */
    public ScreenLocation getNextLocation(short position, short setNewAnchor)
    {
        if (position == ScreenConstants.NEXT_LOGICAL)
            position = ScreenConstants.RIGHT_OF_LAST;
        if (position == ScreenConstants.FIRST_LOCATION)
            position = ScreenConstants.FIRST_DISPLAY_LOCATION;
        if (position == ScreenConstants.ADD_SCREEN_VIEW_BUFFER)
            position = ScreenConstants.ADD_GRID_SCREEN_BUFFER;      // No buffer around frame!
        return super.getNextLocation(position, setNewAnchor);
    }
    /**
     * The title for this screen.
     * @return The title for this screen.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    { // This is almost always overidden!
        String windowName = "Display"; // Default
        Record query = this.getMainRecord();
        if (query != null)
            windowName = query.getRecordName() + ' ' + windowName;
        return windowName;
    }
    /**
     * Get the navigation button count.
     * @return The nav button count.
     */
    public int getNavCount()
    {
        return m_iNavCount;
    }
    /**
     * Add the navigation button(s) to the left of the grid row.
     */
    public void addNavButtons()
    {
        SCannedBox box = new SCannedBox(this.getNextLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, null, MenuConstants.FORM, MenuConstants.FORMLINK, null);
        box.setRequestFocusEnabled(false);
        if ((m_iDisplayFieldDesc & ScreenConstants.SELECT_MODE) == ScreenConstants.SELECT_MODE)
            new SCannedBox(this.getNextLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, null, MenuConstants.SELECT, MenuConstants.SELECT, null);
    }
    /**
     * Set up the screen fields (default = set them all up for the current record).
     * This method is usually overidden to place the controls in exact locations.
     */
    public void setupSFields()
    {   // Set up the screen fields
        int lastColumn;
        Record record = this.getMainRecord();
        if (record == null)
            return;
        lastColumn = record.getFieldCount() + DBConstants.MAIN_FIELD - 1;
        int iSelectCount = 0;
        for (int iQueryColumn = DBConstants.MAIN_FIELD; iQueryColumn <= lastColumn; iQueryColumn++)
        {
            BaseField field = record.getField(iQueryColumn);
            if (field.isHidden())
                continue;   // Don't display the counter field
            if (field instanceof ImageField)
                continue;   // Typically don't display in a grid
            if (field.getMaxLength() > ScreenConstants.MAX_EDIT_LINE_CHARS)
                continue;   // By default, don't display large fields (speeds up query)
            if (field.isSelected())
                this.addColumn(field);
            if (iSelectCount++ > 8)
                break;          // Don't display all the fields (just the first 8?)
        }
    }
    /**
     * Move the field's value to the control.
     * Typically not used for panels, EXCEPT for grid screens, the value is the sort column.
     */
    public void fieldToControl()
    {
        if (this.getConverter() != null)
        {
            Object objValue = this.getScreenFieldView().getFieldState();
            if (this.getScreenFieldView().getControl() != null)
                this.getScreenFieldView().setComponentState(this.getScreenFieldView().getControl(), objValue);
        }
    }
    /**
     * Read the current file in the header record given the current detail record.
     */
    public void syncHeaderToMain()
    {
        super.syncHeaderToMain();
        // NOTE! This logic is VERY similar to the logic in getScreenURL, so change both.
        FileListener listener = this.getMainRecord().getListener();
        while (listener != null)
        {
            if (listener instanceof FileFilter)
            {
                for (int iIndex = 0; iIndex < 4; iIndex++)
                {
                    BaseField field = ((FileFilter)listener).getReferencedField(iIndex);
                    if (field == null)
                        break;
                    if (field.getRecord() == this.getScreenRecord())
                    {   // Okay here is one that needs to be added
                        Utility.restoreFieldParam(this, field);                        
                    }
                }
            }
            listener = (FileListener)listener.getNextListener();
        }
    }
    /**
     * Get the command string to restore screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        // NOTE! This logic is VERY similar to the logic in syncHeaderToMain, so change both.
        FileListener listener = this.getMainRecord().getListener();
        while (listener != null)
        {
            if (listener instanceof FileFilter)
            {
                for (int iIndex = 0; iIndex < 4; iIndex++)
                {
                    BaseField field = ((FileFilter)listener).getReferencedField(iIndex);
                    if (field == null)
                        break;
                    if (field.getRecord() == this.getScreenRecord())
                    {   // Okay here is one that needs to be added
                        strURL = Utility.addFieldParam(strURL, field);
                    }
                }
            }
            listener = (FileListener)listener.getNextListener();
        }
        return strURL;
    }
}
