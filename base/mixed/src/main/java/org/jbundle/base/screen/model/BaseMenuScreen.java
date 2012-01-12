/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
* BaseMenuScreen.
* Copyright � 1997 jbundle.org. All rights reserved.
*/

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.XMLPropertiesField;
import org.jbundle.base.screen.model.menu.SBaseMenuBar;
import org.jbundle.base.screen.model.menu.SGridMenuBar;
import org.jbundle.base.screen.model.report.parser.BaseMenuParser;
import org.jbundle.base.screen.model.report.parser.XMLParser;
import org.jbundle.base.screen.model.util.MenuToolbar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Debug;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.main.db.MenusModel;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
* Menu screen.
*/
public class BaseMenuScreen extends BaseScreen
{
    /**
     * Cache for the menu name.
     */
    protected String m_strMenu = null;

    /**
     *  Constructor.
     */
    public BaseMenuScreen()
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
    public BaseMenuScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     *  Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);

        this.setMenuProperty(null);     // Set the menu to the current "menu" parameter.
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
     * Enable or disable this control.
     * @param bEnable If true, enable this field.
     */
    public void setEnabled(boolean bEnable)
    {
        return;	// Can't disable a menu screen!
    }
    /**
     * Clear any cached data from this screen.
     * So this screen can be reused.
     */
    public void clearCachedData()
    {
        super.clearCachedData();
        m_strMenu = null;
    }
    /**
     * The title for this screen.
     * @return The title.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    { // This is almost always overidden!
        Record record = this.getMainRecord();
        return record.getRecordName() + " menu";
    }
    /**
     * Set the current menu.
     * @param strMenu The current menu name.
     */
    public void setMenuProperty(String strMenu)
    {
    }
    /**
     * Is this the user's main menu?
     * @return true if this is the main menu.
     */
    public boolean isMainMenu()
    {
        boolean bIsMainMenu = false;
        if (m_strMenu != null)
        {
            if (this.getTask() != null)
                if (HtmlConstants.MAIN_MENU_KEY.equalsIgnoreCase(this.getTask().getProperty(DBParams.MENU)))
                bIsMainMenu = true;
            if (m_strMenu.equalsIgnoreCase(HtmlConstants.MAIN_MENU_KEY))
                bIsMainMenu = true;
            else if (((this.getProperty(DBParams.HOME) == null) || (this.getProperty(DBParams.HOME).length() == 0))
                    && (DBConstants.ANON_USER_ID.equals(this.getProperty(DBParams.USER_ID))))
                bIsMainMenu = true;
        }
        return bIsMainMenu;
    }
    /**
     * Get this property.
     * @param strProperty The key to lookup.
     * @return The property for this key.
     */
    public String getProperty(String strProperty)
    {
        if (strProperty.equalsIgnoreCase(DBParams.MENU)) if (m_strMenu != null)
            return m_strMenu; // Current menu property
        if (this.getMainRecord() != null)
            if ((this.getMainRecord().getEditMode() == DBConstants.EDIT_CURRENT) || (this.getMainRecord().getEditMode() == DBConstants.EDIT_IN_PROGRESS))
        {
            if ("menutitle".equalsIgnoreCase(strProperty))
                return this.getMenuName(this.getMainRecord());
            if ("type".equalsIgnoreCase(strProperty))
                return this.getMenuType(this.getMainRecord());
            if ("icon".equalsIgnoreCase(strProperty))
                return this.getMenuIcon(this.getMainRecord());
            if ("menudesc".equalsIgnoreCase(strProperty))
                return this.getMenuDesc(this.getMainRecord());
            if ("link".equalsIgnoreCase(strProperty))
                return this.getMenuLink(this.getMainRecord());
            if (this.getMainRecord() instanceof MenusModel)
            	if (((XMLPropertiesField)this.getMainRecord().getField(MenusModel.PARAMS)).getProperty(strProperty) != null)
            		return ((XMLPropertiesField)this.getMainRecord().getField(MenusModel.PARAMS)).getProperty(strProperty);
        }
        return super.getProperty(strProperty);
    }
    /**
     * Throw up a Menu Toolbar if no toolbars yet.
     * @return The new (menu) toolbar.
     */
    public ToolScreen addToolbars()
    {   // Override this to add (call this) or replace (don't call) this default toolbar.
        return new MenuToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
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
                new SBaseMenuBar(new ScreenLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), appletScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
            }
        }
    }
    /**
     * Code to display a Menu.
     * @param strMenu The menu to set up.
     */
    public void preSetupGrid(String strMenu)
    {
        Record record = this.getMainRecord();
        if (record != null)
            record.setOpenMode(record.getOpenMode() | DBConstants.OPEN_CACHE_RECORDS);    // Cache recently used records.
        if (record != null) if (record.getKeyArea().getKeyName().equals(DBConstants.PRIMARY_KEY))
            record.setKeyArea(record.getDefaultScreenKeyArea());
    }
    /**
     * Code to display a Menu.
     */
    public void postSetupGrid()
    {
    }
    /**
     * Get menu name.
     * @param recMenu The menu record.
     * @return The menu name.
     */
    public String getMenuName(Record recMenu)
    {
        return recMenu.getField(DBConstants.MAIN_FIELD + 1).getString();
    }
    /**
     * Get menu type.
     * @param recMenu The menu record.
     * @return The menu type.
     */
    public String getMenuType(Record recMenu)
    {
        return Constants.BLANK;
    }
    /**
     * Get menu icon.
     * @param recMenu The menu record.
     * @return The icon name.
     */
    public String getMenuIcon(Record recMenu)
    {
        return "Menu";
    }
    /**
     * Get menu desc.
     * @param recMenu The menu record.
     * @return The menu description.
     */
    public String getMenuDesc(Record recMenu)
    {
        return Constants.BLANK;
    }
    /**
     * Get menu link.
     * @param recMenu The menu record.
     * @return The menu link.
     */
    public String getMenuLink(Record recMenu)
    {
        String strRecordClass = recMenu.getClass().getName();
        String strLink = HtmlConstants.SERVLET_LINK;
        strLink = Utility.addURLParam(strLink, DBParams.RECORD, strRecordClass);
        strLink = Utility.addURLParam(strLink, DBParams.COMMAND, MenuConstants.FORM);
        if ((recMenu.getEditMode() == Constants.EDIT_IN_PROGRESS) || (recMenu.getEditMode() == Constants.EDIT_CURRENT))
        {
            try   {
                String strBookmark = recMenu.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                strLink = Utility.addURLParam(strLink, DBConstants.STRING_OBJECT_ID_HANDLE, strBookmark);
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        return strLink;
    }
    /**
     *  Specify the fields to display.
     *  (Don't set up any fields)
     */
    public void setupSFields()
    {
    }
    /**
     * Get the command string that will restore this screen.
     * @return The URL for the current screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        if (this.getClass().getName().equals(BaseMenuScreen.class.getName()))
        {
            strURL = this.addURLParam(strURL, DBParams.RECORD, this.getMainRecord().getClass().getName());
            strURL = this.addURLParam(strURL, DBParams.COMMAND, MenuConstants.MENUREC);
        }
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
            Debug.print(ex);
            ex.printStackTrace();
        }
        return strURL;
    }
    /**
     * Get the parser.
     * @param recMenu The menu record.
     * @return The XML parser.
     */
    public XMLParser getXMLParser(Record recMenu)
    {
        return new BaseMenuParser(this, recMenu);
    }
}
