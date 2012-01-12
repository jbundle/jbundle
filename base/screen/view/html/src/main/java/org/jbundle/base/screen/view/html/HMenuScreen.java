/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.screen.model.MenuScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.main.db.MenusModel;
import org.jbundle.model.DBException;


/**
 * Menu screen.
 */
public class HMenuScreen extends HBaseMenuScreen
{

    /**
     * Constructor.
     */
    public HMenuScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HMenuScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenField model, boolean bEditableControl)
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
     * Get the Html String.
     * @param recMenu The menu file.
     * @return The HTML string.
     */
    public String getHtmlString(Record recMenus)
    {
        boolean bHTML = false;
        String strXMLData = recMenus.getField(MenusModel.XML_DATA).toString();    // Rare to supply HTML here
        if ((strXMLData == null) || (strXMLData.length() == 0))
        {
            String strFilename = ((PropertiesField)recMenus.getField(MenusModel.PARAMS)).getProperty("html");
            if (strFilename == null)
                strFilename = ((PropertiesField)recMenus.getField(MenusModel.PARAMS)).getProperty(DBParams.XML);
            else
                bHTML = true;   // This is the format file
            if (strFilename != null)
            {
                InputStream streamIn = this.getScreenField().getParentScreen().getTask().getInputStream(strFilename);
                strXMLData = Utility.transferURLStream(null, null, new InputStreamReader(streamIn));
            }
        }
        if (strXMLData != null)
            if ((!strXMLData.endsWith("</HTML>")) && (!strXMLData.endsWith("</html>")))
                strXMLData = null;
            else
                bHTML = true;   // This is the format data
        if (strXMLData != null)
            if ((strXMLData.startsWith("<?xml"))
                || (strXMLData.startsWith("<?XML")))
                    strXMLData = strXMLData.substring(strXMLData.indexOf('>') + 1);
        String strCellFormat = super.getHtmlString(recMenus);
        if (strXMLData != null)
        {
            if (bHTML)
                strCellFormat = strXMLData;
            else
                strCellFormat = strXMLData + "\n<br/>\n" + strCellFormat;
        }
        if (strCellFormat != null)
            strCellFormat = Utility.replaceResources(strCellFormat, null, null, (MenuScreen)this.getScreenField());
        return strCellFormat;
    }
    /**
     * Return the html options.
     * No toolbars for menus.
     * @exception DBException File exception.
     * @return The HTML options.
     */
    public int getPrintOptions()
        throws DBException
    {
        int iHtmlOptions = HtmlConstants.HTML_DISPLAY;
        return iHtmlOptions;
    }
    /**
     * Get the Html keywords.
     * @return The Keywords.
     */
    public String getHtmlKeywords()
    {
        Record recMenu = this.getMainRecord();
        if (recMenu.getField(MenusModel.KEYWORDS).getLength() > 0)
            return recMenu.getField(MenusModel.KEYWORDS).toString();
        else
            return super.getHtmlKeywords();
    }
    /**
     * Get the Html Description.
     * @return The menu description.
     */
    public String getHtmlMenudesc()
    {
        Record recMenu = this.getMainRecord();
        if (recMenu.getField(MenusModel.COMMENT).getLength() > 0)
            return recMenu.getField(MenusModel.COMMENT).toString();
        else
            return super.getHtmlMenudesc();
    }
}
