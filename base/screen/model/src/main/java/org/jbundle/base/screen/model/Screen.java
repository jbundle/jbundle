/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
* @(#)Screen.java 0.00 12-Feb-97 Don Corley
*
* Copyright © 2012 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import java.net.URLDecoder;
import java.util.Map;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.SelectOnUpdateHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.event.MainFieldHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.menu.SBaseMenuBar;
import org.jbundle.base.screen.model.menu.SMenuBar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * This is the base for any data maintenance screen.
 */
public class Screen extends BaseScreen
{
    /**
     * Constructor.
     */
    public Screen()
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
    public Screen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize the class.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        Record recordMain = this.getMainRecord();
        if (recordMain != null)
        {
            recordMain.setOpenMode(recordMain.getOpenMode() | DBConstants.OPEN_DONT_CACHE);    // Don't Cache recently used records.
            if (!ScreenConstants.HTML_SCREEN_TYPE.equalsIgnoreCase(this.getViewFactory().getViewSubpackage()))
                recordMain.setupRecordListener(this, false, false);   // I need to listen for record changes
            try   {
                if (recordMain.getEditMode() == Constants.EDIT_NONE)
                {   // If the record is in an indeterminate state, addNew!
                    recordMain.addNew();
                    if (!ScreenConstants.HTML_SCREEN_TYPE.equalsIgnoreCase(this.getViewFactory().getViewSubpackage()))
                    {
                        String strBookmark = this.getProperty(DBConstants.STRING_OBJECT_ID_HANDLE);
                        if ((strBookmark != null) && (strBookmark.length() > 0))
                        {
                            try   {
                                strBookmark = URLDecoder.decode(strBookmark, DBConstants.URL_ENCODING);
                            } catch (java.io.UnsupportedEncodingException ex) {
                                ex.printStackTrace();
                            }
                            recordMain.setHandle(strBookmark, DBConstants.OBJECT_ID_HANDLE);        // Initial menu
                        }
                    }
                }
                else if (recordMain.getEditMode() == Constants.EDIT_CURRENT)
                {
                    if (recordMain.getRecordOwner() == this)    // Not on sub-screens
                        recordMain.handleValidRecord();     // Do the valid record behaviors!
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
            if (recordMain.getRecordOwner() == this)    // Not on sub-screens
                if ((recordMain.getOpenMode() & (DBConstants.LOCK_STRATEGY_MASK | DBConstants.OPEN_READ_ONLY | DBConstants.OPEN_APPEND_ONLY)) == 0)
                    recordMain.setOpenMode(recordMain.getOpenMode() | DBConstants.OPEN_LOCK_ON_CHANGE_STRATEGY);
        }

        this.resizeToContent(this.getTitle());

        this.selectField(null, DBConstants.SELECT_FIRST_FIELD);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
    * Add the read-main-key listener.
    */
    public void addMainKeyBehavior()
    {   // Add the read keyed listener to all unique keys with one field
        Record record = this.getMainRecord();
        if (record == null)
            return;
        int keyCount = record.getKeyAreaCount();
        for (int keyNumber = DBConstants.MAIN_KEY_AREA; keyNumber < keyCount + DBConstants.MAIN_KEY_AREA; keyNumber++)
        {
            KeyArea keyAreaInfo = record.getKeyArea(keyNumber);
            if (((keyAreaInfo.getUniqueKeyCode() == DBConstants.UNIQUE)
                || (keyAreaInfo.getUniqueKeyCode() == DBConstants.SECONDARY_KEY))
                & (keyAreaInfo.getKeyFields() == 1))
            {
                BaseField mainField = keyAreaInfo.getField(DBConstants.MAIN_KEY_FIELD);
                MainFieldHandler readKeyed = new MainFieldHandler(record.getKeyArea(keyNumber).getKeyName());
                mainField.addListener(readKeyed);
            }
        }
    }
    /**
     * Code this position and Anchor to add it to the LayoutManager.
     * @param position The location constant (see ScreenConstants).
     * @param setNewAnchor The anchor constant.
     * @return The new screen location object.
     */
    public ScreenLocation getNextLocation(short position, short setNewAnchor)
    {
        if (position == ScreenConstants.ADD_VIEW_BUFFER)
            position = ScreenConstants.ADD_SCREEN_VIEW_BUFFER;
        if (position == ScreenConstants.FIRST_LOCATION)
            position = ScreenConstants.FIRST_INPUT_LOCATION;
        return super.getNextLocation(position, setNewAnchor);
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
            if ((menuBar == null) || (!(menuBar instanceof SMenuBar)))
            {
                if (menuBar instanceof SBaseMenuBar)
                    menuBar.free();     // Wrong menu
                new SMenuBar(new ScreenLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), appletScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
            }
        }
    }
    /**
     * The title for this screen.
     * @return This screen's title.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    {
        String windowName = "Maintenance"; // Default
        Record query = this.getMainRecord();
        if (query != null)
            windowName = query.getRecordName() + ' ' + windowName;  // xxx Maintenance.
        return windowName;
    }
    /**
     * Get the command string that will restore this screen.
     * @return The URL for this screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        if (this.getClass().getName().equals(Screen.class.getName()))
        {
            strURL = this.addURLParam(strURL, DBParams.RECORD, this.getMainRecord().getClass().getName());
            strURL = this.addURLParam(strURL, DBParams.COMMAND, ThinMenuConstants.FORM);
        }
        else
            strURL = this.addURLParam(strURL, DBParams.SCREEN, this.getClass().getName());
        try   {
            if (this.getMainRecord() != null)
                if ((this.getMainRecord().getEditMode() == Constants.EDIT_IN_PROGRESS) || (this.getMainRecord().getEditMode() == Constants.EDIT_CURRENT))
                {
                    String strBookmark = DBConstants.BLANK;
                    if (this.getMainRecord().getHandle(DBConstants.OBJECT_ID_HANDLE) != null)
                        strBookmark = this.getMainRecord().getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                    strURL = this.addURLParam(strURL, DBConstants.STRING_OBJECT_ID_HANDLE, strBookmark);
                }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return strURL;
    }
    /**
     * Add New Record Command.
     * @return True if successful.
     */
    public boolean onAdd()
    {
        boolean flag = super.onAdd();
        this.selectField(null, DBConstants.SELECT_FIRST_FIELD);
        return flag;
    }
    /**
     * Delete Record Command.
     * @return True if successful.
     */
    public boolean onDelete()
    {
        boolean flag = super.onDelete();
        this.selectField(null, DBConstants.SELECT_FIRST_FIELD);
        return flag;
    }
    /**
     * Move Record Command.
     * @return True if successful.
     */
    public boolean onMove(int nIDMoveCommand)
    {
        boolean flag = super.onMove(nIDMoveCommand);
        this.selectField(null, DBConstants.SELECT_FIRST_FIELD);
        return flag;
    }
    /**
     * Cancel Record Command.
     * @return True if successful.
     */
    public boolean onRefresh()
    {
        boolean flag = super.onRefresh();
        this.selectField(null, DBConstants.SELECT_FIRST_FIELD);
        return flag;
    }
    /**
     * Find the sub-screen that uses this grid query and set for selection.
     * When you select a new record here, you read the same record in the SelectQuery.
     * @param recMaint The record which is synced on record change.
     * @param bUpdateOnSelect Do I update the current record if a selection occurs.
     * @return True if successful.
     */
    public boolean setSelectQuery(Rec recMaint, boolean bUpdateOnSelect)
    {
        if (recMaint == null)
        {
            return true;    // BaseTable Set!
        }
        if (this.getMainRecord() != null)
            if (this.getMainRecord() != recMaint)
                if (this.getMainRecord().getBaseRecord().getTableNames(false).equals(recMaint.getTableNames(false)))
        {
            this.getMainRecord().addListener(new SelectOnUpdateHandler((Record)recMaint, bUpdateOnSelect));
                return true;    // BaseTable Set!
        }
        return false;
    }
    /**
     * Set up the screen fields (default = set them all up for the current record).
     * <p/>This method is usually overidden to place the controls in exact locations.
     */
    public void setupSFields()
    {   // Set up the screen fields
        Record record = this.getMainRecord();
        if (record == null)
            return;
        int lastColumn = record.getFieldCount() + DBConstants.MAIN_FIELD - 1;
        for (int queryColumn = DBConstants.MAIN_FIELD; queryColumn <= lastColumn; queryColumn++)
        {
            BaseField field = record.getField(queryColumn);
            if (field.isHidden())
                continue;   // Don't display the counter field
            if (field.isSelected())
                field.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, ScreenConstants.DEFAULT_DISPLAY);
        }
    }
}
