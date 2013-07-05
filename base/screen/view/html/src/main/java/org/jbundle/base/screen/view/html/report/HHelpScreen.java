/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.HelpScreen;
import org.jbundle.base.screen.model.report.parser.HelpParser;
import org.jbundle.base.screen.model.report.parser.MenuParser;
import org.jbundle.base.screen.model.report.parser.XMLParser;
import org.jbundle.model.DBException;
import org.jbundle.model.app.program.db.ClassInfoModel;
import org.jbundle.model.main.db.MenusModel;
import org.jbundle.model.main.user.db.UserInfoModel;
import org.jbundle.model.screen.ScreenComponent;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class HHelpScreen extends HBaseParserScreen
{

    /**
     * Constructor.
     */
    public HHelpScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HHelpScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
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
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public void printHtmlNavMenu(PrintWriter out)
        throws DBException
    {
        if (HHelpScreen.getFirstToUpper(this.getProperty(DBParams.NAVMENUS)) != UserInfoModel.NO_ICONS.charAt(0))
            super.printHtmlNavMenu(out);
        else
        { // Applets frequently turn off menu bars temporarly
            String strHTML = "\t<tr valign=top>\n\t<td>"; // Default menu = no table items
            out.println(strHTML);
        }
    }
    /**
     * Print the top nav menu.
     * @exception DBException File exception.
     */
    public void printHtmlMenubar(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        if (HHelpScreen.getFirstToUpper(this.getProperty(DBParams.MENUBARS)) != UserInfoModel.NO.charAt(0))
        {
            if (HHelpScreen.getFirstToUpper(this.getProperty(DBParams.NAVMENUS)) != UserInfoModel.NO_ICONS.charAt(0)) // Applets frequently turn off menu bars temporarly
            {
                String strHTML = reg.getString("htmlHelpMenubar");
                Hashtable<String,String> ht = new Hashtable<String,String>();
// ** HACK ** Call this only once (very expensive)
                String strScreen = this.getProperty(DBParams.SCREEN);
                if (strScreen == null)
                    strScreen = DBConstants.BLANK;
                ht.put(HtmlConstants.SCREEN_TAG, strScreen);
                String strRecord = this.getProperty(DBParams.RECORD);
                if (strRecord == null)
                    strRecord = DBConstants.BLANK;
                ht.put(HtmlConstants.RECORD_TAG, strRecord);
                String strMenu = this.getProperty(DBParams.MENU);
                if (strMenu == null)
                    strMenu = DBConstants.BLANK;
                ht.put(HtmlConstants.MENU_TAG, strMenu);
                strHTML = Utility.replace(strHTML, ht);
                this.writeHtmlString(strHTML, out);
            }
        }
    }
    /**
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        String strHTML = "";

        ClassInfoModel classInfo = (ClassInfoModel)Record.makeRecordFromClassName(ClassInfoModel.THICK_CLASS, ((HelpScreen)this.getScreenField()));
        if (classInfo != null)
            classInfo = classInfo.readClassInfo(((HelpScreen)this.getScreenField()), null);

        String strClassName = classInfo.getClassName();
        if ((strClassName != null) && (strClassName.equalsIgnoreCase("MenuScreen")))
        {
            strHTML = reg.getString("htmlHelpMenu");
            if ((strHTML == null) || (strHTML.length() == 0))
                strHTML =
                    "<CENTER><h2>" + HtmlConstants.TITLE_TAG + "</h2></CENTER>" +
                    "<h3>Description:</h3>" +
                    "<p>" + HtmlConstants.MENU_DESC_TAG + "</p>" + 
                    "<h3>Menu items:</h3>";
            Record menu = Record.makeRecordFromClassName(MenusModel.THICK_CLASS, null);
            menu.setKeyArea(MenusModel.TYPE_KEY);
            menu.getField(MenusModel.TYPE).setString(DBParams.MENU);
            String strParamMenu = this.getProperty(DBParams.MENU);      // Display record
            menu.getField(MenusModel.PROGRAM).setString(strParamMenu);
            boolean bSuccess = menu.seek("=");
            if (!bSuccess)
                ;   // Do something?
            XMLParser parser = new MenuParser((HelpScreen)this.getScreenField(), menu);
            parser.parseHtmlData(out, strHTML);
            parser.free();
            parser = null;

            menu.free();
            menu = null;
        }
        else
        {
            if (strHTML.length() == 0)
            {
                if ((strClassName != null) && (strClassName.length() > 0))
                    strHTML = reg.getString("htmlHelp");
                else
                {
                    strHTML = reg.getString("htmlHelpDefault");
                    out.println(strHTML);
                    classInfo.free();
                    classInfo = null;
                    return;
                }
            }
            if ((strHTML == null) || (strHTML.length() == 0))
                strHTML =
                "<h2>" + HtmlConstants.TITLE_TAG + "</h2>" +
                "<h3>Description:</h3>" +
                "<p>" + HtmlConstants.MENU_DESC_TAG + "</p>" +
                "<h3>Operation:</h3>" +
                "<p>" + HtmlConstants.HELP_TAG + "</p>";
    
            XMLParser parser = new HelpParser((HelpScreen)this.getScreenField(), classInfo);
            parser.parseHtmlData(out, strHTML);
            parser.free();
            parser = null;
        }
        classInfo.free();
        classInfo = null;
    }
}
