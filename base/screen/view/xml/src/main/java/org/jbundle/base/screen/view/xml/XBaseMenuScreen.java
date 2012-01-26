/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.BaseMenuScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.main.db.MenusModel;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.DBException;
import org.jbundle.thin.main.db.Menus;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class XBaseMenuScreen extends XBaseScreen
{

    /**
     * Constructor.
     */
    public XBaseMenuScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XBaseMenuScreen(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the name of the stylesheet.
     */
    public String getStylesheetName()
    {
    	if (this.getProperty(DBParams.HELP) != null)
            return "menuhelp";
        else if (this.getProperty(DBParams.STYLESHEET) != null)
            return this.getProperty(DBParams.STYLESHEET);
        else 
            return "menus";
    }
    /**
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions)
    {
        boolean bFieldsFound = true;    // This will keep screen from printing (non-existent sub-fields)
        MenusModel recMenus = (MenusModel)((BaseMenuScreen)this.getScreenField()).getMainRecord();
        
        if ((recMenus.getField(MenusModel.XML_DATA).isNull()) || (recMenus.getField(MenusModel.XML_DATA).toString().startsWith("<HTML>")) || (recMenus.getField(Menus.XML_DATA).toString().startsWith("<html>")))
        {
            String filename = ((PropertiesField)recMenus.getField(MenusModel.PARAMS)).getProperty(DBParams.XML);
            if ((filename != null) && (filename.length() > 0))
            {
                InputStream streamIn = this.getTask().getInputStream(filename);
                if (streamIn == null)
                {
                	Utility.getLogger().warning("XmlFile not found " + filename);
                	return false;
                }
                String str = Utility.transferURLStream(null, null, new InputStreamReader(streamIn));
                recMenus.getField(MenusModel.XML_DATA).setString(str);
            }
        }
        if (!recMenus.getField(MenusModel.XML_DATA).isNull())
        {
            String str = recMenus.getField(Menus.XML_DATA).toString();
            str = Utility.replaceResources(str, null, null, (BaseMenuScreen)this.getScreenField());
            recMenus.getField(MenusModel.XML_DATA).setString(str);
        }

        String strContentArea = recMenus.getSubMenuXML();
        out.println(strContentArea);

        return bFieldsFound;
    }
}
