/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.DualReportScreen;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.main.user.db.UserInfoModel;


/**
 *  HTML Display screen (simply display some HTML in the content area of a screen).
 *  This screen serves a dual purpose; first, it allows the user to enter data in the parameter
 *  form (if isPrintReport() == false), then it outputs the report as Html (when isPrintReport() == true).
 *  WARNING: Be very careful; This screen is used differently when it is used for HTML or a Screen.
 *  When it is a screen, it just throws up an HTML Editor window and asks the http server for the
 *  URL that it is suppose to show.
 *  When it is running on the server, it actually formats and builds the html help page.
 */
public class HDualReportScreen extends HBaseReportScreen
{

    /**
     * Constructor.
     */
    public HDualReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HDualReportScreen(ScreenField model, boolean bEditableControl)
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
     * Code to display a Menu.
     * Skip Nav menu for reports.
     * @exception DBException File exception.
     */
    public void printHtmlNavMenu(PrintWriter out)
        throws DBException
    {
        String strForms = this.getProperty(HtmlConstants.FORMS);                // Html page
        if (strForms != null) if (strForms.equalsIgnoreCase(HtmlConstants.DISPLAY))
            ((BasePanel)this.getScreenField()).setProperty(DBParams.NAVMENUS, UserInfoModel.NO_ICONS);   // Don't display NAV menus
//x            return;   // Don't display NAV menus
        super.printHtmlNavMenu(out);
    }
    /**
     * display this screen in html input format.
     *  returns true if default params were found for this form.
     * @ param bAddDescColumn true if form, otherwise grid format
     * @exception DBException File exception.
     */
    public int getPrintOptions()
        throws DBException
    {
        int iHtmlOptions = super.getPrintOptions();
        String strForms = this.getProperty(HtmlConstants.FORMS);
        if ((strForms == null) || (strForms.length() == 0))
        {
            if (((DualReportScreen)this.getScreenField()).isPrintReport())
                iHtmlOptions = iHtmlOptions & (~HtmlConstants.DONT_PRINT_SCREEN);
            else
                iHtmlOptions = iHtmlOptions | HtmlConstants.DONT_PRINT_SCREEN;
        }
        return iHtmlOptions;
    }
}
