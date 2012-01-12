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
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.xmlutil.XmlUtilities;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.control.servlet.html.BaseServlet;
import org.jbundle.base.screen.model.BaseGridScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.HelpScreen;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.base.util.XMLTags;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;


/**
 * This is the base for any data display screen.
 */
public class XBasePanel extends XScreenField
{

    /**
     * Constructor.
     */
    public XBasePanel()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XBasePanel(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
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
     * Output this screen using XML.
     * Display the html headers, etc. then:
     * <ol>
     * - Parse any parameters passed in and set the field values.
     * - Process any command (such as move=Next).
     * - Render this screen as Html (by calling printHtmlScreen()).
     * </ol>
     * @exception DBException File exception.
     */
    public void printReport(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
    	if (reg == null)
        	reg = ((BaseApplication)this.getTask().getApplication()).getResources(HtmlConstants.XML_RESOURCE, false);
        String string = XmlUtilities.XML_LEAD_LINE;
        out.println(string);

        String strStylesheetPath = this.getStylesheetPath();
        if (strStylesheetPath != null)
        {
            string = "<?xml-stylesheet type=\"text/xsl\" href=\"" + strStylesheetPath + "\" ?>";
            out.println(string);
        }

        out.println(Utility.startTag(XMLTags.FULL_SCREEN));

        this.printXMLParams(out, reg);   // URL params
        
        this.printXMLHeaderInfo(out, reg); // Title, keywords, etc.
        this.printXmlHeader(out, reg); // Top menu

        this.printXmlMenu(out, reg);
        this.printXmlNavMenu(out, reg);
        this.printXmlTrailer(out, reg);

        this.processInputData(out);
        ((BasePanel)this.getScreenField()).prePrintReport();
        out.println(Utility.startTag(XMLTags.CONTENT_AREA));
        this.getScreenField().printScreen(out, reg);
        out.println(Utility.endTag(XMLTags.CONTENT_AREA));

        out.println(Utility.endTag(XMLTags.FULL_SCREEN));
    }
    /**
     * Get the name of the stylesheet.
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getStylesheetPath()
    {
        String stylesheetName = this.getStylesheetName();
        if (stylesheetName != null)
        {
        	if (this.getProperty(DBParams.HELP) == null)	// Not the help stylesheet
        		if (this.getProperty("stylesheet-path") != null)
        			stylesheetName = this.getProperty("stylesheet-path") + stylesheetName;
        	stylesheetName = BaseServlet.fixStylesheetPath(stylesheetName, this.getTask(), true);
        }
        return stylesheetName;
    }
    /**
     * Get the name of the stylesheet.
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getStylesheetName()
    {
        return "form";  // Override this
    }
    /**
     * Get the name of the stylesheet.
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getKeywords()
    {
        if (this.getProperty(XMLTags.META_KEYWORDS) != null)
            return this.getProperty(XMLTags.META_KEYWORDS);
        return DBConstants.BLANK;  // Override this
    }
    /**
     * Get the name of the meta tag.
     * @return The name of the meta tag(s).
     */
    public String getMetaRedirect()
    {
        if (this.getProperty(XMLTags.META_REDIRECT) != null)
            return this.getProperty(XMLTags.META_REDIRECT);
        return DBConstants.BLANK;  // Override this
    }
    /**
     * Print the header info, such as title, keywords and meta-desc.
     * @param out The http output stream.
     * @param reg Local resource bundle.
     */
    public void printXMLParams(PrintWriter out, ResourceBundle reg)
    {
        String PARAMS = "params";
        
        out.println(Utility.startTag(PARAMS));
        
        ServletTask task = (ServletTask)this.getTask();
        Map<String,Object> properties = task.getRequestProperties(task.getServletRequest(), true);	// Including the url/domain
        Map<String,Object> propUser = task.getApplication().getSystemRecordOwner().getProperties();
        Map<String,Object> propServlet = task.getApplication().getProperties();
        
        if (propUser != null)
        {
            for (String strKey : propUser.keySet())
            {
            	String value = DBConstants.BLANK;
            	if (propUser.get(strKey) != null)
            		value = propUser.get(strKey).toString();
            	if ((value.length() > 0)
            		|| ((propUser.get(strKey) == null) && (propServlet.get(strKey) == null)))	// If a user property override a default value, use it
            	{
            		out.print(Utility.startTag(strKey));
            		out.print(value);
            		out.println(Utility.endTag(strKey));
            	}
            }
        }
        for (String strKey : properties.keySet())
        {
            if ((propUser == null) || (propUser.get(strKey) == null) || (propUser.get(strKey).toString().length() == 0))
            {   // Only if it doesn't exist in user properties
                out.print(Utility.startTag(strKey));
                out.print((properties.get(strKey) != null) ? properties.get(strKey) : DBConstants.BLANK);
                out.println(Utility.endTag(strKey));
            }
        }
        for (String strKey : propServlet.keySet())
        {
            if ((propUser == null) || (propUser.get(strKey) == null) || (propUser.get(strKey).toString().length() == 0))
                if ((properties == null) || (properties.get(strKey) == null))
            {   // Only if it doesn't exist in user or app properties
                out.print(Utility.startTag(strKey));
                out.print((propServlet.get(strKey) != null) ? propServlet.get(strKey) : DBConstants.BLANK);
                out.println(Utility.endTag(strKey));
            }
        }
        
        out.println(Utility.endTag(PARAMS));
    }
    /**
     * Print the header info, such as title, keywords and meta-desc.
     * @param out The http output stream.
     * @param reg Local resource bundle.
     */
    public void printXMLHeaderInfo(PrintWriter out, ResourceBundle reg)
    {
        String strTitle = this.getProperty("title");                // Menu page
        if ((strTitle == null) || (strTitle.length() == 0))
            strTitle = ((BasePanel)this.getScreenField()).getTitle();
        out.println(Utility.startTag(XMLTags.TITLE) + strTitle + Utility.endTag(XMLTags.TITLE));
        String strKeywords = this.getKeywords();
        if ((strKeywords != null) && (strKeywords.length() > 0))        
            out.println(Utility.startTag(XMLTags.META_KEYWORDS) + strKeywords + Utility.endTag(XMLTags.META_KEYWORDS));
        String strDescription = strTitle;
        out.println(Utility.startTag(XMLTags.META_DESCRIPTION) + strDescription + Utility.endTag(XMLTags.META_DESCRIPTION));
        String redirect = this.getMetaRedirect();
        if ((redirect != null) && (redirect.length() > 0))
            out.println(Utility.startTag(XMLTags.META_REDIRECT) + redirect + Utility.endTag(XMLTags.META_REDIRECT));        
    }
    /**
     * Print the header info (the top-menu).
     * @param out The http output stream.
     * @param reg Local resource bundle.
     */
    public void printXmlHeader(PrintWriter out, ResourceBundle reg)
    {
        String strTopMenu = reg.getString("xmlHeader");
        if ((strTopMenu == null) || (strTopMenu.length() == 0))
            strTopMenu =
                " <top-menu>" +
                "   <menu-item>" +
                "       <description>Site home page</description>" +
                "       <link>?menu=Main</link>" +
                "       <image>Home</image>" +
                "       <name>Home</name>" +
                "   </menu-item>" +
                "   <menu-item>" +
                "       <description>My home</description>" +
                "       <link>?menu=</link>" +
                "       <image>MyHome</image>" +
                "   </menu-item>" +
                "   <menu-item>" +
                "       <description>View or change preferences</description>" +
                "       <link>" + HtmlConstants.SERVLET_LINK+ "?screen=org.jbundle.main.screen.UserPreferenceScreen&amp;java=no</link>" +
                "       <image>Lookup</image>" +
                "       <name>Preferences</name>" +
                "   </menu-item>" +
                "   <menu-item>" +
                "       <description>About us</description>" +
                "       <link>" + '?' + DBParams.HELP + '=' + "&amp;xml=docs/com/aboutus.xml</link>" +
                "       <image>About</image>" +
                "   </menu-item>" +
                "   <help-item>" +
                "       <menu-item>" +
                "           <description>Help for the current screen</description>" +
                "           <link>" + '?' + DBParams.HELP + '=' + "&amp;{url}&amp;class={helpclass}</link>" +
                "           <image>Help</image>" +
                "       </menu-item>" +
                "   </help-item>" +
                " </top-menu>";
        
        String strTitle = this.getProperty(XMLTags.TITLE);                // Menu page
        if ((strTitle == null) || (strTitle.length() == 0))
            strTitle = ((BasePanel)this.getScreenField()).getTitle();
        String strURL = this.getThisURL();
        if (strURL == null)
            strURL = DBConstants.BLANK;
        if (strURL.length() > 0)
            if (strURL.charAt(0) == '?')
                strURL = strURL.substring(1); // Always
        strURL = Utility.encodeXML(strURL);   // Convert < to &lt; , etc.

        Hashtable<String,Object> ht = new Hashtable<String,Object>();
        ht.put("helpclass", HelpScreen.class.getName());
        ht.put(XMLTags.URL, strURL);
        ht.put(XMLTags.TITLE, strTitle);
        String strUserName = this.getProperty(DBParams.USER_NAME);
        if (strUserName == null)
            strUserName = DBConstants.BLANK;
//x        ht.put(XMLTags.USER_NAME, strUserName);  // This is available in /full-screen/params/user
        String strLogin = "Login";
        if (strUserName.length() > 0)
            strLogin = "Logout";
        ht.put("loginIcon", strLogin);
        ht.put("loginDesc", reg.getString(strLogin));
        String strLoginLink = "?screen=" + "org.jbundle.main.user.screen.UserLoginScreen" + "&amp;java=no";
        if (strUserName.length() > 0)
            strLoginLink = "?user=&amp;menu=";
        ht.put("loginLink", strLoginLink);
        
        strTopMenu = Utility.replaceResources(strTopMenu, reg, ht, null);

        out.println(strTopMenu);
    }
    /**
     * Print the top menu.
     * @param out The http output stream.
     * @param reg Local resource bundle.
     * @exception DBException File exception.
     */
    public void printXmlMenu(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
    }
    /**
     * Code to display the side Menu.
     * @param out The http output stream.
     * @param reg Local resource bundle.
     * @exception DBException File exception.
     */
    public void printXmlNavMenu(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        String strNavMenu = reg.getString("xmlNavMenu");
        if ((strNavMenu == null) || (strNavMenu.length() == 0))
            strNavMenu =
                " <navigation-menu>" +
                "   <navigation-item>" +
                "       <name>Home</name>" +
                "       <description>Home</description>" +
                "       <link>?menu=Home</link>" +
                "       <image>Home</image>" +
                "   </navigation-item>" +
                "   <navigation-item>" +
                "       <name>My home</name>" +
                "       <description>My home</description>" +
                "       <link>?menu=</link>" +
                "       <image>MyHome</image>" +
                "   </navigation-item>" +
                " </navigation-menu>";
        out.println(strNavMenu);
    }
    /**
     * Code to display the side Menu.
     * @param out The http output stream.
     * @param reg Local resource bundle.
     * @exception DBException File exception.
     */
    public void printXmlTrailer(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        String strTrailer = reg.getString("xmlTrailer");
        if ((strTrailer == null) || (strTrailer.length() == 0))
            strTrailer =
                " <trailer>" +
                " </trailer>";
        out.println(strTrailer);
    }
    /**
     * Print this screen's content area.
     * @param out The out stream.
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        int iPrintOptions = this.getScreenField().getPrintOptions();
        this.getScreenField().printControl(out, iPrintOptions);
        this.getScreenField().printData(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartForm(PrintWriter out, int iPrintOptions)
    {
        if (((iPrintOptions & HtmlConstants.HEADING_SCREEN) == HtmlConstants.HEADING_SCREEN)
                || ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
                || ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) == HtmlConstants.DETAIL_SCREEN))
            return; // Don't need on nested forms
        super.printControlStartForm(out, iPrintOptions);
        String strAction = "post";
        BasePanel modelScreen = (BasePanel)this.getScreenField();

//?            strAction = "postxml";
        out.println(Utility.startTag("hidden-params"));
        this.addHiddenParams(out, this.getHiddenParams());
        out.println(Utility.endTag("hidden-params"));
        out.println("<xform id=\"form1\">");
        out.println("   <submission");
        out.println(" id=\"submit1\"");
        out.println(" method=\"" + strAction + "\"");
        out.println(" localfile=\"temp.xml\"");
        out.println(" action=\"" + modelScreen.getServletPath(null) + "\" />");
//?        out.println("  <model href=\"form.xsd\">");
//?        out.println("  <!-- The model is currently ignored -->");
//?        out.println("  </model>");
//?        out.println("  <instance id=\"instance1\" xmlns=\"\">");
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndForm(PrintWriter out, int iPrintOptions)
    {
        if (((iPrintOptions & HtmlConstants.HEADING_SCREEN) == HtmlConstants.HEADING_SCREEN)
                || ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
                || ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) == HtmlConstants.DETAIL_SCREEN))
            return; // Don't need on nested forms
//?     out.println("  </instance>");
        out.println("</xform>");
//?     if (bFieldsFound)
        if (!this.getScreenField().isToolbar())
            this.printZmlToolbarControls(out, iPrintOptions | HtmlConstants.DETAIL_SCREEN); // Print this screen's toolbars
        else
            this.printToolbarControl(true, out, iPrintOptions);     // Print this toolbar's controls
        super.printControlEndForm(out, iPrintOptions);
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
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions)
    {
        return false;   // Don't print here
    }
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void printScreenFieldData(ScreenField sField, PrintWriter out, int iPrintOptions)
    {
        sField.printData(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions)
    {
        String strRecordName = "norecord";
        Record record = this.getMainRecord();
        if (record != null)
            strRecordName = record.getTableNames(false);
        if ((iPrintOptions & HtmlConstants.MAIN_HEADING_SCREEN) == HtmlConstants.MAIN_HEADING_SCREEN)
        {
            out.println(Utility.startTag(XMLTags.HEADING));
            out.println(Utility.startTag(XMLTags.FILE));
            out.println(Utility.startTag(strRecordName));
        }
        if ((iPrintOptions & HtmlConstants.MAIN_FOOTING_SCREEN) == HtmlConstants.MAIN_FOOTING_SCREEN)
        {
            out.println(Utility.startTag(XMLTags.FOOTING));
            out.println(Utility.startTag(XMLTags.FILE));
            out.println(Utility.startTag(strRecordName));
        }
        if ((iPrintOptions & HtmlConstants.REPORT_SCREEN) != HtmlConstants.REPORT_SCREEN)
            out.println(Utility.startTag(XMLTags.DATA));    // If not a report
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions)
    {
        if ((iPrintOptions & HtmlConstants.REPORT_SCREEN) != HtmlConstants.REPORT_SCREEN)
            out.println(Utility.endTag(XMLTags.DATA));
        String strRecordName = "norecord";
        Record record = this.getMainRecord();
        if (record != null)
            strRecordName = record.getTableNames(false);
        if ((iPrintOptions & HtmlConstants.MAIN_HEADING_SCREEN) == HtmlConstants.MAIN_HEADING_SCREEN)
        {
            out.println(Utility.endTag(strRecordName));
            out.println(Utility.endTag(XMLTags.FILE));
            out.println(Utility.endTag(XMLTags.HEADING));
        }
        if ((iPrintOptions & HtmlConstants.MAIN_FOOTING_SCREEN) == HtmlConstants.MAIN_FOOTING_SCREEN)
        {
            out.println(Utility.endTag(strRecordName));
            out.println(Utility.endTag(XMLTags.FILE));
            out.println(Utility.endTag(XMLTags.FOOTING));
        }
    }
    /**
     * Get the URL to display this page.
     * @return The URL string.
     */
    public String getThisURL()
    {
        String strURL = DBConstants.BLANK;
        strURL = Utility.addURLParam(strURL, DBParams.RECORD, this.getProperty(DBParams.RECORD), false);
        strURL = Utility.addURLParam(strURL, DBParams.SCREEN, this.getProperty(DBParams.SCREEN), false);
        strURL = Utility.addURLParam(strURL, DBParams.CLASS, this.getProperty(DBParams.CLASS), false);
        strURL = Utility.addURLParam(strURL, DBParams.COMMAND, this.getProperty(DBParams.COMMAND), false);
        strURL = Utility.addURLParam(strURL, DBParams.MENU, this.getProperty(DBParams.MENU), false);
        return strURL;
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
            out.println(Utility.startTag(XMLTags.STATUS_TEXT));
            out.println(Utility.startTag(XMLTags.TEXT) + "Error: " + strError + Utility.endTag(XMLTags.TEXT));
            out.println(Utility.startTag(XMLTags.ERROR) + "error" + Utility.endTag(XMLTags.ERROR));
            Record record = ((BasePanel)this.getScreenField()).getMainRecord();
            if (record != null)
                record.setEditMode(Constants.EDIT_NONE);    // Make sure this isn't updated on screen free
            out.println(Utility.endTag(XMLTags.STATUS_TEXT));
        }
        else
        {
            String strMessage = this.getTask().getStatusText(DBConstants.INFORMATION_MESSAGE);
            if (strMessage != null)
                if (strMoveValue.length() > 0)
                    if ((strMessage.length() > 0) && (!(this.getScreenField() instanceof BaseGridScreen))) // TODO localize
                    {
                        out.println(Utility.startTag(XMLTags.STATUS_TEXT));
                        out.println(Utility.startTag(XMLTags.TEXT) + strMessage + Utility.endTag(XMLTags.TEXT));
                        out.println(Utility.startTag(XMLTags.ERROR) + "information" + Utility.endTag(XMLTags.ERROR));
                        out.println(Utility.endTag(XMLTags.STATUS_TEXT));
                    }
        }
    }
}
