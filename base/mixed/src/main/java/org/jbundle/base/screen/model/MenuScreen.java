/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 *  MenuScreen.
 *  Copyright � 1997 jbundle.org. All rights reserved.
 */

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.field.BaseListener;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.screen.model.report.parser.MenuParser;
import org.jbundle.base.screen.model.report.parser.XMLParser;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.main.db.MenusModel;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.util.Application;


/**
 * Menu screen.
 */
public class MenuScreen extends BaseMenuScreen
{
    /**
     * Current menu object ID.
     */
    protected String m_strMenuObjectID = null;
    /**
     * Current menu title.
     */
    protected String m_strMenuTitle = null;
    /**
     *
     */
    public static final String SAME_WINDOW_PARAM = "samewindow";

    /**
     * Constructor.
     */
    public MenuScreen()
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
    public MenuScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Clear any cached data from this screen.
     * So this screen can be reused.
     */
    public void clearCachedData()
    {
        super.clearCachedData();
        m_strMenuObjectID = null;
        m_strMenuTitle = null;
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        if ((this.getClass() == MenuScreen.class) &&
                (!Application.LOGIN_REQUIRED.equalsIgnoreCase(this.getProperty(Params.SECURITY_MAP))) && (!Application.CREATE_USER_REQUIRED.equalsIgnoreCase(this.getProperty(Params.SECURITY_MAP))))
            return DBConstants.NORMAL_RETURN;        // For now... allow access to all menus (unless I'm asking for a login)
        else if (this.getProperty(DBParams.HELP) != null)
    		return DBConstants.NORMAL_RETURN;	// If this is a help (menu) screen, allow access
        return super.checkSecurity();           // If you override this class, use the standard security
    }
    /**
     * Set this property.
     * @param strProperty The property key.
     * @param strValue The property value.
     */
    public void setProperty(String strProperty, String strValue)
    {
        if (DBParams.MENU.equalsIgnoreCase(strProperty))
            this.setMenuProperty(strValue);     // Special case
        else
            super.setProperty(strProperty, strValue);
    }
    /**
     * Set the current menu.
     * @param strMenu If null, get the current property from the current parameters.
     */
    public void setMenuProperty(String strMenu)
    {
        if ((strMenu == null) || (strMenu.length() == 0) || (strMenu == DEFAULT))
        {
            if (m_strMenuObjectID != null)
                return;     // It's already set to the default
            if (strMenu != DEFAULT)
            	strMenu = this.getProperty(DBParams.MENU);
            if ((strMenu == null) || (strMenu.length() == 0) || (strMenu == DEFAULT))
                strMenu = this.getProperty(DBParams.HOME);
        }
        if ((strMenu == null) || (strMenu.length() == 0) || (HtmlConstants.MAIN_MENU_KEY.equalsIgnoreCase(strMenu)) || (strMenu == DEFAULT))
        {
            strMenu = this.getProperty(DBParams.HOME);
            if ((strMenu == null) || (strMenu.length() == 0) || (strMenu == DEFAULT))
            	strMenu = this.getURLMenu();
            if ((strMenu == null) || (strMenu.length() == 0) || (strMenu == DEFAULT))
                strMenu = HtmlConstants.MAIN_MENU_KEY;
        }
        Record recMenus = this.getMainRecord();
        m_strMenu = strMenu;
        m_strMenuObjectID = recMenus.getField(MenusModel.ID).toString();
        m_strMenuTitle = recMenus.getField(MenusModel.NAME).toString();
        int oldKeyArea = recMenus.getDefaultOrder();
        try   {
            boolean bIsNumeric = Utility.isNumeric(strMenu, true);
            if (bIsNumeric)
            {
            	strMenu = Converter.stripNonNumber(strMenu);
                recMenus.setKeyArea(MenusModel.ID_KEY);
                recMenus.getField(MenusModel.ID).setString(strMenu);
                bIsNumeric = recMenus.seek("=");
            }
            if (!bIsNumeric)
            {
                recMenus.setKeyArea(MenusModel.CODE_KEY);
                recMenus.getField(MenusModel.CODE).setString(strMenu);
                if (recMenus.seek("="))
                {
                    if (!recMenus.getField(MenusModel.PROGRAM).isNull())
                        if (!recMenus.getField(MenusModel.PROGRAM).equals(recMenus.getField(MenusModel.CODE)))
                            if ("menu".equalsIgnoreCase(recMenus.getField(MenusModel.TYPE).toString()))
                    {   // Use a different menu
                        Map<String,Object> map = ((PropertiesField)recMenus.getField(MenusModel.PARAMS)).getProperties();
                        int iOldKeyArea = recMenus.getDefaultOrder();
                        recMenus.getField(MenusModel.CODE).moveFieldToThis(recMenus.getField(MenusModel.PROGRAM));
                        recMenus.setKeyArea(MenusModel.CODE_KEY);
                        int oldOpenMode = recMenus.getOpenMode();
                        recMenus.setOpenMode(oldOpenMode | DBConstants.OPEN_READ_ONLY);
                        if (recMenus.seek(null))
                        {
                            strMenu = recMenus.getField(MenusModel.ID).toString();
                            if (map != null)
                            {
	                            Iterator<? extends Map.Entry<?, ?>> i = map.entrySet().iterator();
	                            while (i.hasNext()) {
	                                Map.Entry<?, ?> e = i.next();
	                            	((PropertiesField)recMenus.getField(MenusModel.PARAMS)).setProperty((String)e.getKey(), (String)e.getValue());
	                            }
	                            recMenus.getField(MenusModel.PARAMS).setModified(false);	// Make sure this doesn't get written
	                            recMenus.setOpenMode(oldOpenMode);
                            }
                        }
                        recMenus.setKeyArea(iOldKeyArea);
                    }
                }
                else
                { // Not found, display default screen
                	if ((strMenu != DEFAULT)
                		&& (!HtmlConstants.MAIN_MENU_KEY.equalsIgnoreCase(strMenu))
                		&& ((strMenu != null ) && (!strMenu.equalsIgnoreCase(this.getProperty(DBParams.HOME)))))
                	{	// Try the default menu once
                		m_strMenuObjectID = null;
                		this.setMenuProperty(DEFAULT);
                	}
                	else
                	{	// Should never happen, the default menu doesn't exist
                		recMenus.addNew();
                		((CounterField)recMenus.getField(MenusModel.ID)).setValue(-1); // Don't read any detail
                	}
                }
            }
            m_strMenuObjectID = recMenus.getField(MenusModel.ID).toString();
            m_strMenu = recMenus.getField(MenusModel.CODE).toString();
            m_strMenuTitle = recMenus.getField(MenusModel.NAME).toString();
        } catch (DBException ex)    {
            ex.printStackTrace(); // Never
        }
        recMenus.setKeyArea(oldKeyArea);
    }
    public static final String DEFAULT = "default";
    /**
     * Code to display a Menu.
     * @param strMenu The name of the menu to set up.
     */
    public void preSetupGrid(String strMenu)
    {
        super.preSetupGrid(strMenu);
        if (m_strMenuObjectID == null)
            this.setMenuProperty(strMenu);
        Record menu = this.getMainRecord();
        menu.setKeyArea(MenusModel.PARENT_FOLDER_ID_KEY);
        if (m_strMenuObjectID != null)
            strMenu = m_strMenuObjectID;
        StringSubFileFilter behMenu = new StringSubFileFilter(strMenu, menu.getField(MenusModel.PARENT_FOLDER_ID), null, null, null, null);
        menu.addListener(behMenu);
    }
    /**
     * Code to display a Menu.
     */
    public void postSetupGrid()
    {
        Record menu = this.getMainRecord();
        BaseListener behMenu = menu.getListener(StringSubFileFilter.class.getName());
        menu.removeListener(behMenu, true);
    }
    /**
     * From the URL, get the menu.
     * First try the app URL, then try the host URL (ie., www.tourapp.com/fred then www.tourapp.com).
     * @return The menu name.
     */
    public String getURLMenu()
    {
        String strMenu = this.getProperty(DBParams.URL);
        if ((strMenu != null) && (strMenu.length() > 0))
        {   // Look to see if this URL has a menu associated with it.
            strMenu = Utility.getDomainFromURL(strMenu, null);
            // Now I have the domain name, try to lookup the menu name...
            Record recMenu = this.getMainRecord();
            recMenu.setKeyArea(MenusModel.CODE_KEY);
            
            try {
                while (strMenu.length() > 0)
                {
                    recMenu.getField(MenusModel.CODE).setString(strMenu);
                    if (recMenu.seek("="))
                        return recMenu.getField(MenusModel.CODE).toString();
                    if (strMenu.indexOf('.') == strMenu.lastIndexOf('.'))
                        break;  // xyz.com = stop looking
                    strMenu = strMenu.substring(strMenu.indexOf('.') + 1);  // Remove the next top level domain (ie., www)
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        return null;
    }
    /**
     * Get menu name.
     * @param recMenu The menu record.
     * @return The menu name.
     */
    public String getMenuName(Record recMenu)
    {
        return recMenu.getField(MenusModel.NAME).getString();
    }
    /**
     * Get menu type.
     * @param recMenu The menu record.
     * @return The menu type.
     */
    public String getMenuType(Record recMenu)
    {
        return recMenu.getField(MenusModel.TYPE).getString();
    }
    /**
     * Get menu icon.
     * @param recMenu The menu record.
     * @return The icon name.
     */
    public String getMenuIcon(Record recMenu)
    {
        return recMenu.getField(MenusModel.ICON_RESOURCE).getString();
    }
    /**
     * Get menu desc.
     * @param recMenu The menu record.
     * @return The menu description.
     */
    public String getMenuDesc(Record recMenu)
    {
        return recMenu.getField(MenusModel.COMMENT).toString();
    }
    /**
     * Get menu link.
     * @param recMenu The menu record.
     * @return The menu link.
     */
    public String getMenuLink(Record recMenu)
    {
        return ((MenusModel)recMenu).getLink();
    }
    /**
     *  OpenMainFile Method.
     * @return The new main file.
     */
    public Record openMainRecord()
    {
        Record record = Record.makeRecordFromClassName(MenusModel.THICK_CLASS, this);
        record.setOpenMode(DBConstants.OPEN_READ_ONLY);   // This will optimize the cache when client is remote.
        return record;
    }
    /**
     * Get the parser.
     * @param recMenu The menu record.
     * @return The XML parser.
     */
    public XMLParser getXMLParser(Record recMenu)
    {
        return new MenuParser(this, recMenu);
    }
    /**
     * The title for this screen.
     * @return The menu title.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    { // This is almost always overidden!
        String strMenu = m_strMenuTitle;
        if (strMenu == null)
            strMenu = DBConstants.BLANK;
        int iLength = strMenu.length();
        if (iLength >= 4)
            if (!strMenu.substring(iLength - 4, iLength).equalsIgnoreCase("Menu"))
                if (Character.isLetterOrDigit(strMenu.charAt(iLength - 1)))   // Don't add "menu" if this ends in punctuation or space
                    return strMenu + " menu";
        return strMenu;
    }
    /**
     * Process the command.
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @param true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        int iIndex = strCommand.indexOf(DBParams.MENU + '=');
        if (iIndex != -1)
            if ((strCommand.indexOf(DBParams.TASK + '=') != -1) 
                || (strCommand.indexOf(DBParams.APPLET + '=') != -1) 
                || (strCommand.indexOf(DBParams.SCREEN + '=') != -1))
                    iIndex = -1;    // Special case - thin menu (create an applet window)
        if (iIndex == -1)
        {
            String strJobCommand = DBParams.JOB + '=';
            int iCommandIndex = strCommand.indexOf(strJobCommand);
            if (iCommandIndex != -1)
            {
                iCommandIndex += strJobCommand.length();
                strCommand = strCommand.substring(iCommandIndex, Math.max(strCommand.indexOf('&', iCommandIndex), strCommand.length()));
                try   {
                    strCommand = URLDecoder.decode(strCommand, DBConstants.URL_ENCODING);
                } catch (java.io.UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                    strCommand = "";
                }
            }
        }
        if ((iIndex != -1) && ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) != ScreenConstants.USE_NEW_WINDOW))
        {	// Same window
//x            strCommand = "?samewindow=&" + strCommand.substring(iIndex);
            if (strCommand.indexOf(MenuScreen.SAME_WINDOW_PARAM) == -1)
                strCommand = Utility.addURLParam(strCommand, MenuScreen.SAME_WINDOW_PARAM, DBConstants.BLANK);
            this.freeAllSFields(false);
            if (this.getTask() != null)
                this.getTask().setProperties(null);
            //          this.setMenuProperty(strParam);   // Set the menu to the current "menu" parameter.
            m_strMenu = null;
            m_strMenuObjectID = null;
            m_strMenuTitle = null;
            boolean bSuccess = this.getScreenFieldView().doCommand(strCommand);     // Initial menu
            this.resizeToContent(this.getTitle());
            //          Container control = (Container)this.getScreenFieldView().getControl();  // Redisplay
            //          control.validate();     // Re-layout the container
            //          control.repaint();      // Re-display this screen
            this.getParentScreen().pushHistory(strCommand, ((iCommandOptions & DBConstants.DONT_PUSH_TO_BROSWER) == DBConstants.PUSH_TO_BROSWER));   // History of screens displayed
            return bSuccess;
        }
        else
            return super.doCommand(strCommand, sourceSField, iCommandOptions); // Do inherited
    }
    /**
     * Get the command string that will restore this screen.
     * @return The URL for the current screen.
     */
    public String getScreenURL()
    {
        String strURL = super.getScreenURL();
        if (m_strMenu != null)
        strURL = this.addURLParam(strURL, DBParams.MENU, m_strMenu);
        else if ((this.getAppletScreen() != null) && (this.getAppletScreen().getScreenFieldView().getControl() != null))
        {
            String strMenu = this.getProperty(DBParams.MENU);
            if (strMenu == null)
                strMenu = Constants.BLANK;
            strURL = this.addURLParam(strURL, DBParams.MENU, strMenu);
        }
        return strURL;
    }
    /**
     * Display this control's data in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        return this.getScreenFieldView().printData(out, iPrintOptions);  // HACK
    }
}
