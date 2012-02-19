/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.xmlutil.XmlUtilities;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.HelpScreen;
import org.jbundle.base.screen.model.report.TechHelpScreen;
import org.jbundle.model.app.program.db.ClassInfoModel;
import org.jbundle.model.main.db.MessagesModel;
import org.jbundle.model.screen.ScreenComponent;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 */
public class XHelpScreen extends XBaseParserScreen
{

    /**
     * Constructor.
     */
    public XHelpScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public XHelpScreen(ScreenField model, boolean bEditableControl)
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
     * Get the name of the stylesheet.
     * @return The filename of the xsl stylesheet.
     */
    public String getStylesheetName()
    {
        return "help";
    }
    /**
     * Form Header.
     */
    public void printXmlHeader(PrintWriter out, ResourceBundle reg)
    {
        try {
            String strTitle = this.getProperty("title");                // Menu page
            if ((strTitle == null) || (strTitle.length() == 0))
                strTitle = ((BasePanel)this.getScreenField()).getTitle();
            String strURL = this.getThisURL();
            if (strURL.length() > 0)
                if (strURL.charAt(0) == '?')
                    strURL = strURL.substring(1); // Always
            strURL = Utility.encodeXML(strURL);   // Convert < to &lt; , etc.
            String strTopMenu =
                " <top-menu>" +
                "   <menu-item>" +
                "       <description>Home</description>" +
                "       <link>" + HtmlConstants.SERVLET_LINK + "?menu=</link>" +
                "       <image>Home</image>" +
                "   </menu-item>" +
                "   <menu-item>" +
                "       <description>Programmer's technical Information</description>" +
                "       <link>" + '?' + DBParams.HELP + "=" + TechHelpScreen.class.getName() + "&amp;" + strURL + "</link>" +
                "       <image>Tech</image>" +
                "   </menu-item>" +
                "   <menu-item>" +
                "       <description>Run this program</description>" +
                "       <link>" + HtmlConstants.SERVLET_LINK + "?" + strURL + "</link>" +
                "       <image>Run</image>" +
                "   </menu-item>" +
                "   <menu-item>" +
                "       <description>Report a bug in this program</description>" +
                "       <link>" + HtmlConstants.SERVLET_LINK + "?record=" + MessagesModel.THICK_CLASS + "&amp;command=New&amp;url=" + URLEncoder.encode(strURL, DBConstants.URL_ENCODING) + "</link>" +
                "       <image>Bugs</image>" +
                "   </menu-item>" +
                "   <help-item>" +
                "       <menu-item>" +
                "           <description>Main Help Menu</description>" +
                "           <link>" + '?' + DBParams.HELP + "=" + "</link>" +
                "           <image>Help</image>" +
                "       </menu-item>" +
                "   </help-item>" +
                " </top-menu>";
            out.println(strTopMenu);
        } catch (java.io.UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Get the top menu.
     */
    public void printHtmlHeaderArea(PrintWriter out)
    {
        ClassInfoModel classInfo = ((HelpScreen)this.getScreenField()).getClassInfo();
        
        if (classInfo != null)
            if (classInfo.isValidRecord())
        {
            String strHeader =
    " <title>Help Screen - " + Utility.encodeXML(classInfo.getClassDesc()) + " </title>" +
    //+"    <meta-keywords>" + Utility.encodeXML(m_classInfo.getField(ClassInfo.KEYWORDS).toString()) + "</meta-keywords>" +
    " <meta-description>" + Utility.encodeXML(classInfo.getClassExplain()) + "</meta-description>";
            out.println(strHeader);
        }
    }
    /**
     * Output the main screen.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
    {
        String strClassXML = null;
        
        ClassInfoModel classInfo = ((HelpScreen)this.getScreenField()).getClassInfo();
        if (classInfo != null)
        {
            if (!classInfo.isValidRecord())	// Not found = Main help screen
            	classInfo = classInfo.readClassInfo((HelpScreen)this.getScreenField(), HelpScreen.class.getName());
            if (classInfo.isValidRecord())
	            strClassXML = XmlUtilities.createXMLStringRecord((Record)classInfo);
        }
        
        if ((strClassXML == null)
            || (strClassXML.length() == 0))
        strClassXML =
            "<ClassInfo>" +
            " <ClassName>ClassName</ClassName>" +
            " <BaseClassName>BaseClassName</BaseClassName>" +
            " <ClassDesc>Class not found.</ClassDesc>" +
            " <ClassPackage>ClassPackage</ClassPackage>" +
            " <ClassSourceFile>ClassSourceFile</ClassSourceFile>" +
            " <ClassType>ClassType</ClassType>" +
            " <ClassExplain>ClassExplain</ClassExplain>" +
            " <ClassHelp>ClassHelp</ClassHelp>" +
            " <ClassImplements>ClassImplements</ClassImplements>" +
            " <SeeAlso>SeeAlso</SeeAlso>" +
            " <TechnicalInfo>TechnicalInfo</TechnicalInfo>" +
            "</ClassInfo>";

        String strContentArea = strClassXML;
        out.println(strContentArea);
    }
}
