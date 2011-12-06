/**
 * @(#)MenusSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.remote;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.base.remote.db.*;
import org.jbundle.thin.base.remote.*;
import org.jbundle.main.db.*;
import org.jbundle.base.remote.*;

/**
 *  MenusSession - Handle the remote end of thin menus.
 */
public class MenusSession extends Session
{
    /**
     * Default constructor.
     */
    public MenusSession() throws RemoteException
    {
        super();
    }
    /**
     * MenusSession Method.
     */
    public MenusSession(BaseSession parentSessionObject, Record record, Object objectID) throws RemoteException
    {
        this();
        this.init(parentSessionObject, record, objectID);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseSession parentSessionObject, Record record, Object objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Override this to open the main file for this session.
     */
    public Record openMainRecord()
    {
        return new Menus(this);
    }
    /**
     * Add behaviors to this session.
     */
    public void addListeners()
    {
        super.addListeners();
        try   {
            this.doRemoteAction(DBConstants.BLANK, null); // Initial default menu
        } catch (Exception ex)  {
            // Never
        }
        Record recMenus = this.getMainRecord();
        recMenus.setOpenMode(DBConstants.OPEN_NORMAL);  // Double check to see that I don't update on change
        recMenus.addListener(new FileListener(null)
        {
            /**
             * Called when a valid record is read from the table/query.
             * @param bDisplayOption If true, display any changes.
             */
            public void doValidRecord(boolean bDisplayOption) // init this field override for other value
            {   // Convert the XMLProperties field to a URL type string (yikes... in the same field)
                Record recMenus = this.getOwner();
                XMLPropertiesField field = (XMLPropertiesField)recMenus.getField(Menus.kParams);
                Map<String,Object> properties = field.getProperties();
                String strURL = null;
                strURL = Utility.propertiesToURL(strURL, properties);
                if (strURL != null)
                    if (strURL.length() > 0)
                        if (strURL.charAt(0) == '?')
                            strURL = strURL.substring(1);
                field.setString(strURL);
                super.doValidRecord(bDisplayOption);
            }
        });
    }
    /**
     * Override this to do an action sent from the client.
     * @param strCommand The command to execute
     * @param properties The properties for the command
     * @returns Object Return a Boolean.TRUE for success, Boolean.FALSE for failure.
     */
    public Object doRemoteCommand(String strCommand, Map<String,Object> properties) throws RemoteException, DBException
    {
        Map<String,Object> propMenu = properties; // I know for a fact this is a Properties object.
        if (propMenu == null)
            propMenu = new Hashtable<String,Object>();
        if (strCommand != null)
            if (strCommand.length() > 0)
                if (strCommand.indexOf('=') == -1)
                    strCommand = DBParams.MENU + '=' + strCommand;  // If no param specified, it is a menu=
        Utility.parseArgs(propMenu, strCommand);
        String strMenu = (String)propMenu.get(DBParams.MENU);
        if ((strMenu == null) || (strMenu.length() == 0))
            strMenu = this.getProperty(DBParams.MENU);
        if ((strMenu == null) || (strMenu.length() == 0))
            strMenu = this.getProperty(DBParams.HOME);
        if ((strMenu == null) || (strMenu.length() == 0))
            strMenu = HtmlConstants.MAIN_MENU_KEY;
        
        if (strMenu != null)
        {
            this.setupSubMenus(strMenu);
            return Boolean.TRUE;
        }
        return super.doRemoteCommand(strCommand, properties);
    }
    /**
     * SetupSubMenus Method.
     */
    public void setupSubMenus(String strMenu)
    {
        Record recMenu = this.getMainRecord();
        try   {
            String strCommandNoCommas = Utility.replace(strMenu, ",", null);    // Get any commas out
            boolean bIsNumeric = Utility.isNumeric(strCommandNoCommas);
            if (bIsNumeric)
            {
                recMenu.setKeyArea(Menus.kIDKey);
                recMenu.getField(Menus.kID).setString(strCommandNoCommas);
                bIsNumeric = recMenu.seek("=");
            }
            if (!bIsNumeric)
            {
                recMenu.setKeyArea(Menus.kCodeKey);
                recMenu.getField(Menus.kCode).setString(strMenu);
                if (!recMenu.seek("="))
                {   // Not found, try the default main menu
                    recMenu.getField(Menus.kCode).setString(HtmlConstants.MAIN_MENU_KEY);
                    recMenu.seek("=");
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace(); // Never
        }
        String strParentID = recMenu.getField(Menus.kID).toString();
        BaseListener listener = recMenu.getListener(StringSubFileFilter.class.getName());
        if (listener != null)
        { // Should just change the string
            recMenu.removeListener(listener, true);
        }
        recMenu.setKeyArea(Menus.kParentFolderIDKey);
        recMenu.addListener(new StringSubFileFilter(strParentID, Menus.kParentFolderID, null, -1, null, -1));
    }

}
