/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.ResourceBundle;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.BaseGridScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.DisplayToolbar;
import org.jbundle.base.screen.model.util.HelpToolbar;
import org.jbundle.base.screen.model.util.MaintToolbar;
import org.jbundle.base.screen.model.util.MenuToolbar;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.main.user.db.UserInfoModel;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;


/**
 * This is the base for any data display screen.
 */
public class HBasePanel extends HScreenField
{
    /**
     * Constructor.
     */
    public HBasePanel()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HBasePanel(ScreenField model, boolean bEditableControl)
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
        	reg = ((BaseApplication)this.getTask().getApplication()).getResources(HtmlConstants.HTML_RESOURCE, false);
        this.printHtmlHeader(out, reg);
        this.printHtmlMenuStart(out, reg);
        this.processInputData(out);
        ((BasePanel)this.getScreenField()).prePrintReport();
        this.printScreen(out, reg);

        this.printHtmlMenuEnd(out, reg);
        this.printHtmlTrailer(out, reg);
        this.printHtmlFooter(out, reg);
    }
    /**
     * Process all the submitted params.
     * @param out The Html output stream.
     * @exception DBException File exception.
     */
    public void processInputData(PrintWriter out)
        throws DBException
    {
        String strMoveValue = this.getProperty(DBParams.COMMAND);      // Display record
        if (strMoveValue == null)
            strMoveValue = Constants.BLANK;

        String strError = this.getTask().getLastError(0);
        if ((strError != null) && (strError.length() > 0))
            strError = strError + " on " + strMoveValue;
        if ((strError != null) && (strError.length() > 0))
        {
            out.println("<span class=\"error\">Error: " + strError + "</span><br />");
            Record record = ((BasePanel)this.getScreenField()).getMainRecord();
            if (record != null)
                record.setEditMode(Constants.EDIT_NONE);    // Make sure this isn't updated on screen free
        }
        else
        {
            String strMessage = this.getTask().getStatusText(DBConstants.INFORMATION_MESSAGE);
            if (strMessage != null)
                if (strMoveValue.length() > 0)
                    if ((strMessage.length() > 0) && (!(this.getScreenField() instanceof BaseGridScreen))) // TODO localize
                        out.println("<span class=\"information\">" + strMessage + "</span><br />");
        }
    }
    /**
     * Print this screen's content area.
     * @param out The out stream.
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        String strParamHelp = this.getProperty(DBParams.HELP);      // Display record
        if (strParamHelp != null)
            return;     // Don't do this for help screens
        this.printHtmlStartForm(out);
        int iHtmlOptions = this.getScreenField().getPrintOptions();
        if ((iHtmlOptions & HtmlConstants.PRINT_TOOLBAR_BEFORE) != 0)
            this.printZmlToolbarData(out, iHtmlOptions);

        if ((iHtmlOptions & HtmlConstants.DONT_PRINT_SCREEN) == 0)
            this.getScreenField().printData(out, iHtmlOptions);   // DO print screen

        if ((iHtmlOptions & HtmlConstants.PRINT_TOOLBAR_AFTER) != 0)
            this.printZmlToolbarData(out, iHtmlOptions);

        this.printHtmlEndForm(out);
    }
    /**
     * display this screen in html input format.
     *  returns true if default params were found for this form.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printHtmlStartForm(PrintWriter out)
        throws DBException
    {
        String strAction = "post";

        BasePanel modelScreen = (BaseScreen)this.getScreenField();
        int iNumCols = modelScreen.getSFieldCount();
        int iInputCount = 0;
        for (int i = 0; i < iNumCols; i++)
        {
            ScreenField sField = modelScreen.getSField(i);
            if (!sField.isToolbar())
                if (sField.getConverter() != null)
                    if (sField.getConverter().getField() != null)
                    {
                        if (DBParams.PASSWORD.equalsIgnoreCase(sField.getConverter().getField().getFieldName()))
                            iInputCount = 5;    // Always post if password is in the URL
                        iInputCount++;
                    }
        }
        if (iInputCount < 5)
            strAction = "get";
        if (modelScreen instanceof GridScreen)
            if (modelScreen.getEditing() == true)
                strAction = "post";
        out.println("<form action=\"" + modelScreen.getServletPath(null) + "\" method=\"" + strAction + "\">");
        this.addHiddenParams(out, this.getHiddenParams());
    }
    /**
     * display this screen in html input format.
     *  returns true if default params were found for this form.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printHtmlEndForm(PrintWriter out)
        throws DBException
    {
        out.println("</form>");
    }
    /**
     * display this screen in html input format.
     *  returns true if default params were found for this form.
     * @return The HTML options.
     * @exception DBException File exception.
     */
    public int getPrintOptions()
        throws DBException
    {
        int iHtmlOptions = HtmlConstants.HTML_DISPLAY;
        if (((BasePanel)this.getScreenField()).getEditing())
            iHtmlOptions |= HtmlConstants.HTML_INPUT;
        iHtmlOptions |= HtmlConstants.HTML_ADD_DESC_COLUMN;   // Add a desc row (default)
        iHtmlOptions |= HtmlConstants.HTML_IN_TABLE_ROW;        // Yes, you are in a table row (default)

        iHtmlOptions |= HtmlConstants.PRINT_TOOLBAR_BEFORE;
        String strParamForms = this.getProperty(HtmlConstants.FORMS);   // Display record
        if ((strParamForms == null) || (strParamForms.length() == 0))
        {
            strParamForms = Constants.BLANK;
            if (this.getScreenField().isToolbar())
                if ((!(this.getScreenField() instanceof DisplayToolbar)) &&
                (!(this.getScreenField() instanceof MaintToolbar)) &&
                (!(this.getScreenField() instanceof MenuToolbar)) &&
                (!(this.getScreenField() instanceof HelpToolbar)))
                    strParamForms = HtmlConstants.BOTH;
        }
        if (strParamForms.length() > 0) if (!strParamForms.equalsIgnoreCase(HtmlConstants.DATA))
            iHtmlOptions = (iHtmlOptions & (~HtmlConstants.PRINT_TOOLBAR_BEFORE)) | HtmlConstants.PRINT_TOOLBAR_AFTER;
        return iHtmlOptions;
    }
    /**
     * Bottom of HTML form.
     * @param out The html out stream.
     * @param reg The resources object.
     */
    public void printHtmlFooter(PrintWriter out, ResourceBundle reg)
    {
        String strHTML = reg.getString("htmlFooter");
        if ((strHTML == null) || (strHTML.length() == 0))
            strHTML = "</body>\n</html>";
        out.println(strHTML);
        out.flush();
    }
    /**
     * Print the top nav menu.
     * @param out The html out stream.
     * @param reg The resources object.
     * @exception DBException File exception.
     */
    public void printHtmlBanner(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        if (HBasePanel.getFirstToUpper(this.getProperty(DBParams.BANNERS), 'N') == 'Y')
        {
            String strNav = reg.getString("htmlBanner");
            strNav = Utility.replaceResources(strNav, reg, null, null);
            this.writeHtmlString(strNav, out);
        }
    }
    /**
     * Print the top nav menu.
     * @param out The html out stream.
     * @param reg The resources object.
     * @exception DBException File exception.
     */
    public void printHtmlLogo(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        char chMenubar = HBasePanel.getFirstToUpper(this.getProperty(DBParams.LOGOS), 'H');
        if (chMenubar == 'H') if (((BasePanel)this.getScreenField()).isMainMenu())
            chMenubar = 'Y';
        if (chMenubar == 'Y')
        {
            String strNav = reg.getString("htmlLogo");
            strNav = Utility.replaceResources(strNav, reg, null, null);
            String strScreen = ((BasePanel)this.getScreenField()).getScreenURL();
            strScreen = Utility.encodeXML(strScreen);
            String strUserName = ((MainApplication)this.getTask().getApplication()).getUserName();
            if (Utility.isNumeric(strUserName))
                strUserName = DBConstants.BLANK;
            String strLanguage = this.getTask().getApplication().getLanguage(false);
            strNav = Utility.replace(strNav, HtmlConstants.URL_TAG, strScreen);
            strNav = Utility.replace(strNav, HtmlConstants.USER_NAME_TAG, strUserName);
            strNav = Utility.replace(strNav, "<language/>", strLanguage);
            this.writeHtmlString(strNav, out);
        }
    }
    /**
     * Print the top nav menu.
     * @param out The html out stream.
     * @param reg The resources object.
     * @exception DBException File exception.
     */
    public void printHtmlTrailer(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        char chMenubar = HBasePanel.getFirstToUpper(this.getProperty(DBParams.TRAILERS), 'H');
        if (chMenubar == 'H') if (((BasePanel)this.getScreenField()).isMainMenu())
            chMenubar = 'Y';
        if (chMenubar == 'Y')
        {
            String strNav = reg.getString("htmlTrailer");
            strNav = Utility.replaceResources(strNav, reg, null, null);
            String strScreen = ((BasePanel)this.getScreenField()).getScreenURL();
            strScreen = Utility.encodeXML(strScreen);
            strNav = Utility.replace(strNav, HtmlConstants.URL_TAG, strScreen);
            this.writeHtmlString(strNav, out);
        }
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
        char chMenubar = HBasePanel.getFirstToUpper(this.getProperty(DBParams.MENUBARS), 'Y');
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
    /**
     * Print the top nav menu.
     * @param out The html out stream.
     * @param reg The resources object.
     * @exception DBException File exception.
     */
    public void printHtmlMenuStart(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        this.printHtmlBanner(out, reg);
        this.printHtmlLogo(out, reg);
        this.printHtmlMenubar(out, reg);

        String strHTML = reg.getString("htmlTableStart");
        strHTML = Utility.replaceResources(strHTML, reg, null, null);
        if ((strHTML == null) || (strHTML.length() == 0))
            strHTML = "<table border=\"0\" cellspacing=\"10\" width=\"100%\">\n<tr>";
        out.println(strHTML);

        this.printHtmlNavMenu(out);

        strHTML = reg.getString("htmlContentStart");
        if ((strHTML == null) || (strHTML.length() == 0))
            strHTML = "<td valign=\"top\">";
        out.println(strHTML);
    }
    /**
     * Code to display a Menu.
     * @param out The html out stream.
     * @param reg The resources object.
     * @exception DBException File exception.
     */
    public void printHtmlMenuEnd(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        String strHTML = reg.getString("htmlContentEnd");
        strHTML = Utility.replaceResources(strHTML, reg, null, null);
        if ((strHTML == null) || (strHTML.length() == 0))
            strHTML = "</td>";
        out.println(strHTML);

        strHTML = null;
        char ch = HBasePanel.getFirstToUpper(this.getProperty(DBParams.NAVMENUS), ' ');
        if (ch != UserInfoModel.NO_ICONS.charAt(0))
        {
            if (ch == UserInfoModel.ICONS_ONLY.charAt(0))  /** Icons only */
                strHTML = reg.getString("htmlNavIconsOnlyAfterContent");
            if ((strHTML == null) || (strHTML.length() == 0))
                strHTML = reg.getString("htmlNavAfterContent");
            if ((strHTML != null) && (strHTML.length() > 0))
                out.println(strHTML);
        }
        strHTML = reg.getString("htmlTableEnd");
        if ((strHTML == null) || (strHTML.length() == 0))
            strHTML = "</tr>\n</table>";
        out.println(strHTML);
    }
    /**
     * Code to display a Menu.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printHtmlNavMenu(PrintWriter out)
        throws DBException
    {
        String strHTML = null, strStart = null, strEnd = null;
        ResourceBundle reg = ((BaseApplication)this.getTask().getApplication()).getResources(HtmlConstants.HTML_RESOURCE, false);

        char ch = HBasePanel.getFirstToUpper(this.getProperty(DBParams.NAVMENUS), ' ');
        if (ch != UserInfoModel.NO_ICONS.charAt(0))
        {
            String strMenu = this.getProperty(DBParams.MENU); // This user's nav menu
            if ((strMenu == null) || (strMenu.length() == 0))
                strMenu = "NavMenu";
            if (ch == UserInfoModel.ICONS_ONLY.charAt(0))  /** Icons only */
            {
                strStart = reg.getString("htmlNavIconsOnlyStart");
                strHTML = reg.getString(strMenu + "IconsOnly");
                if ((strHTML == null) || (strHTML.length() == 0))
                    strHTML = reg.getString("htmlNavMenuIconsOnly");
                strEnd = reg.getString("htmlNavIconsOnlyEnd");
            }
            if ((strHTML == null) || (strHTML.length() == 0))
                strHTML = reg.getString(strMenu);
            if ((strHTML == null) || (strHTML.length() == 0))
                strHTML = reg.getString("htmlNavMenu");
                
            if ((strStart == null) || (strStart.length() == 0))
                strStart = reg.getString("htmlNavStart");
            if ((strEnd == null) || (strEnd.length() == 0))
                strEnd = reg.getString("htmlNavEnd");
        }
        if ((strHTML == null) || (strHTML.length() == 0) || 
            (strStart == null) || (strStart.length() == 0) || 
                (strEnd == null) || (strEnd.length() == 0))
        {
            strStart = Constants.BLANK;
            strHTML = Constants.BLANK;  // "\t<tr valign=top>\n\t<td>";  // Default menu = no table items
            strEnd = Constants.BLANK;
        }
        out.println(strStart);
        out.println(strHTML);
        out.println(strEnd);
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
     * Default Form Header.
     * @param out The html out stream.
     * @param strTitle The screen title.
     * @param strHTMLStart The start HTML.
     * @param strHTMLEnd The ending HTML.
     */
    public void printHtmlHeader(PrintWriter out, String strTitle, String strHTMLStart, String strHTMLEnd)
    {
        if ((strHTMLStart == null) || (strHTMLStart.length() == 0))
            strHTMLStart = "<html>\n" +
                "<head>\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"org/jbundle/res/docs/styles/css/style.css\" title=\"basicstyle\">";
        out.println(strHTMLStart);
        String strStyleParam = this.getProperty("style");           // Menu page
        if (strStyleParam != null) if (strStyleParam.length() > 0)
        {   // Include style
            out.println("<style type=\"text/css\">");
            out.println("<!--");
            out.println("@import url(\"org/jbundle/res/docs/styles/css/" + strStyleParam + ".css\");");
            out.println("-->");
            out.println("</style>");
        }
        String strParamTitle = this.getProperty("title");           // Menu page
        if (strParamTitle != null) if (strParamTitle.length() > 0)
            strTitle = strParamTitle;
        if (strTitle.length() == 0)
            strTitle = "&nbsp;";
        if ((strHTMLEnd == null) || (strHTMLEnd.length() == 0))
            strHTMLEnd = "<title>" + HtmlConstants.TITLE_TAG + "</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>" + HtmlConstants.TITLE_TAG + "</h1>";
        strHTMLEnd = Utility.replace(strHTMLEnd, HtmlConstants.TITLE_TAG, strTitle);
        String strKeywords = this.getHtmlKeywords();
        if ((strKeywords != null) && (strKeywords.length() > 0))
            strKeywords += ", ";
        strHTMLEnd = Utility.replace(strHTMLEnd, "<keywords/>", strKeywords);
        String strMenudesc = this.getHtmlMenudesc();
        strHTMLEnd = Utility.replace(strHTMLEnd, HtmlConstants.MENU_DESC_TAG, strMenudesc);
        out.println(strHTMLEnd);
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        return false;   // Don't print
    }
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void printScreenFieldData(ScreenComponent sField, PrintWriter out, int iPrintOptions)
    {
        ((ScreenField)sField).printData(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions)
    {
        if ((iPrintOptions & HtmlConstants.MAIN_SCREEN) == HtmlConstants.MAIN_SCREEN)
        {
        }
        else if ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) == HtmlConstants.DETAIL_SCREEN)
        {
            out.println("<tr>\n<td colspan=\"20\">");
        }
        else if ((iPrintOptions & HtmlConstants.HEADING_SCREEN) == HtmlConstants.HEADING_SCREEN)
        {
            out.println("<tr>\n<td colspan=\"20\">");
        }
        else if ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
        {
            out.println("<tr>\n<td colspan=\"20\">");
        }
        else
            out.println("<table border=\"0\">");    // First time
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions)
    {
        if ((iPrintOptions & HtmlConstants.MAIN_SCREEN) == HtmlConstants.MAIN_SCREEN)
        {
        }
        else if ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) == HtmlConstants.DETAIL_SCREEN)
        {
            out.println("</td>\n</tr>");
        }
        else if ((iPrintOptions & HtmlConstants.HEADING_SCREEN) == HtmlConstants.HEADING_SCREEN)
        {
            out.println("</td>\n</tr>");
        }
        else if ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
        {
            out.println("</td>\n</tr>");
        }
        else
            out.println("</table>");
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartField(PrintWriter out, int iPrintOptions)
    {
        if ((iPrintOptions & HtmlConstants.MAIN_SCREEN) == HtmlConstants.MAIN_SCREEN)
        {
        }
        else 
            out.println("<tr>");
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndField(PrintWriter out, int iPrintOptions)
    {
        if ((iPrintOptions & HtmlConstants.MAIN_SCREEN) == HtmlConstants.MAIN_SCREEN)
        {
        }
        else 
            out.println("</tr>");
    }
    /**
     * Add this hidden param to the output stream.
     * @param out The html output stream.
     * @param strParam The parameter.
     * @param strValue The param's value.
     */
    public void addHiddenParam(PrintWriter out, String strParam, String strValue)
    {
        out.println("<input type=\"hidden\" name=\"" + strParam + "\" value=\"" + strValue + "\">");
    }
    /**
     * Parse the HTML for variables and print it.
     * @param strHTML the html string to output.
     * @param out The html out stream.
     */
    public void writeHtmlString(String strHTML, PrintWriter out)
    {
        int iIndex;
        if (strHTML == null)
            return;
        while ((iIndex = strHTML.indexOf(HtmlConstants.TITLE_TAG)) != -1)
        {   // ** FIX THIS to look for a <xxx/> and look up the token **
            strHTML = strHTML.substring(0, iIndex) + ((BasePanel)this.getScreenField()).getTitle() + strHTML.substring(iIndex + HtmlConstants.TITLE_TAG.length());
        }
        out.println(strHTML);
    }
}
