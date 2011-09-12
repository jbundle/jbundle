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
import java.io.PrintWriter;
import java.util.ResourceBundle;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BaseMenuScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.parser.XMLParser;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.model.DBException;


/**
* Menu screen.
 */
public class HBaseMenuScreen extends HBaseScreen
{

    /**
     * Constructor.
     */
    public HBaseMenuScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HBaseMenuScreen(ScreenField model, boolean bEditableControl)
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
     * Return the html options.
     * @return HTML options (No toolbars for menus).
     * @exception DBException File exception.
     */
    public int getPrintOptions()
        throws DBException
    {
        int iHtmlOptions = HtmlConstants.HTML_DISPLAY;
        return iHtmlOptions;
    }
    /**
     * Code to display a Menu.
     * @param out The html out stream.
     * @param iHtmlAttributes The HTML attributes.
     * @return true If fields have been found.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iHtmlAttributes)
    {
        boolean bFieldsFound = false;
        Record recMenu = ((BaseMenuScreen)this.getScreenField()).getMainRecord();
        String strCellFormat = this.getHtmlString(recMenu);
        XMLParser parser = ((BaseMenuScreen)this.getScreenField()).getXMLParser(recMenu);
        parser.parseHtmlData(out, strCellFormat);
        parser.free();
        parser = null;
        return bFieldsFound;
    }
    /**
     * Get the Html String.
     * @param The menu record.
     * @return The HTML string for this menu.
     */
    public String getHtmlString(Record recMenu)
    {
        String strCellFormat = null;
        if ((strCellFormat == null) || (strCellFormat.length() == 0))
        {
            ResourceBundle reg = ((BaseApplication)((BaseMenuScreen)this.getScreenField()).getTask().getApplication()).getResources(HtmlConstants.HTML_RESOURCE, false);
            strCellFormat = reg.getString("htmlAppMenu");
        }
        if ((strCellFormat == null) || (strCellFormat.length() == 0))
            strCellFormat = "<items columns=\"5\"><td align=center valign=\"top\"><a href=\""
                + HtmlConstants.LINK_TAG + "\"><img src=\"" + HtmlConstants.HTML_ROOT + HtmlConstants.ICON_TAG
                + "\" width=\"24\" height=\"24\"><br/>" + HtmlConstants.TITLE_TAG + "</a></td></items>";
        return strCellFormat;
    }
}
