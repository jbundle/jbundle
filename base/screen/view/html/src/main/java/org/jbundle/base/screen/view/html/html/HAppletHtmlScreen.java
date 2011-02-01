package org.jbundle.base.screen.view.html.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.ResourceBundle;

import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.ScreenFieldViewAdapter;
import org.jbundle.base.screen.view.html.HBaseScreen;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;


/**
 * HAppletHtmlScreen - The applet html display screen.
 */
public class HAppletHtmlScreen extends HBaseScreen
{

    /**
     * Constructor.
     */
    public HAppletHtmlScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HAppletHtmlScreen(ScreenField model,boolean bEditableControl)
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
     * Output this screen using HTML.
     * Display the html headers, etc. then:
     * <ol>
     * - Parse any parameters passed in and set the field values.
     * - Process any command (such as move=Next).
     * - Render this screen as Html (by calling printHtmlScreen()).
     * </ol>
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printReport(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        if (reg == null)
        	reg = ((BaseApplication)this.getTask().getApplication()).getResources("HtmlApplet", false);
        super.printReport(out, reg);
    }
    /**
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        this.printAppletHtmlScreen(out, reg);
    }
    /**
     * Form Header.
     * @param out The html out stream.
     * @param reg The resources object.
     */
    public void printHtmlHeader(PrintWriter out, ResourceBundle reg)
    {
        String strTitle = this.getProperty("title");                // Menu page
        if ((strTitle == null) || (strTitle.length() == 0))
            strTitle = ((BasePanel)this.getScreenField()).getTitle();
        String strHTMLStart = reg.getString("htmlHeaderStart");
        String strHTMLEnd = reg.getString("htmlHeaderEnd");
            // Note: don't free the reg key (DBServlet will)
        this.printHtmlHeader(out, strTitle, strHTMLStart, strHTMLEnd);
    }
    /**
     * Print the top nav menu.
     * @exception DBException File exception.
     */
    public void printHtmlMenuStart(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        this.printHtmlBanner(out, reg);
        this.printHtmlLogo(out, reg);
        this.printHtmlMenubar(out, reg);
        // Don't print the table start portion or the nav menus
    }
    /**
     * Not allowed for an applet.
     */
    public void printHtmlMenuEnd(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        // Don't print the end table stuff
    }
    /**
     * Print the top nav menu.
     * @param out The html out stream.
     * @param reg The resources object.
     * @exception DBException File exception.
     */
    public void printHtmlMenubar(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        char chMenubar = ScreenFieldViewAdapter.getFirstToUpper(this.getProperty(DBParams.MENUBARS), 'Y');
        if (chMenubar == 'Y')
        {
            String strUserName = this.getProperty(DBParams.USER_NAME);
            String strUserID = this.getProperty(DBParams.USER_ID);
            if ((strUserName == null) || (DBConstants.ANON_USER_ID.equals(strUserID)) || (Utility.isNumeric(strUserName)))
                strUserName = DBConstants.BLANK;
            String strNav = reg.getString((strUserName.length() > 0) ? "htmlMenubar" : "htmlMenubarAnon");
            strNav = Utility.replaceResources(strNav, reg, null, null);
            String strScreen = ((BasePanel)this.getScreenField()).getScreenURL();
            strScreen = Utility.encodeXML(strScreen);
            String strTitle = this.getProperty("title");                // Menu page
            if ((strTitle == null) || (strTitle.length() == 0))
                strTitle = ((BasePanel)this.getScreenField()).getTitle();
            strNav = Utility.replace(strNav, HtmlConstants.URL_TAG, strScreen);
            strNav = Utility.replace(strNav, HtmlConstants.TITLE_TAG, strTitle);
            strNav = Utility.replace(strNav, HtmlConstants.USER_NAME_TAG, strUserName);
            this.writeHtmlString(strNav, out);
        }
    }
}

