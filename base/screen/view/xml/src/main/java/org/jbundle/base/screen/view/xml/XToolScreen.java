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

import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.MenuConstants;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.report.BaseReportScreen;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class XToolScreen extends XBasePanel
{

    /**
     * Constructor.
     */
    public XToolScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XToolScreen(ScreenField model,boolean bEditableControl)
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
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions)
    {
        boolean bFieldsFound = super.printControl(out, iPrintOptions);
        
        if ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) == HtmlConstants.DETAIL_SCREEN)
            this.printToolbarControl(true, out, iPrintOptions);

        return bFieldsFound;
    }
    /**
     * Display this screen's toolbars in html input format.
     *  returns true if default params were found for this form.
     * @exception DBException File exception.
     */
    public boolean printToolbarControl(boolean bFieldsFound, PrintWriter out, int iHtmlAttributes)
    {
        out.println(Utility.startTag("toolbars"));
        int iNumCols = ((ToolScreen)this.getScreenField()).getSFieldCount();
        for (int iIndex = 0; iIndex < iNumCols; iIndex++)
        {
            ScreenField sField = ((ToolScreen)this.getScreenField()).getSField(iIndex);
            if (sField.getConverter() == null)
                if (sField instanceof SCannedBox)
            {
                SCannedBox button = (SCannedBox)sField;   // Found the toolscreen
                String strCommand = button.getButtonCommand();
                if (strCommand.equalsIgnoreCase(MenuConstants.BACK))
                { // Ignore back for HTML
                }
                else if (strCommand.equalsIgnoreCase(MenuConstants.HELP))
                { // Ignore help for HTML
                }
                else if (strCommand.equalsIgnoreCase(MenuConstants.RESET))
                {   // Special case - for reset do an HTML reset
                    out.println(" <xfm:submit name=\"" + MenuConstants.RESET + "\" ref=\"" + MenuConstants.RESET + "\">");
                    out.println("   <xfm:hint>Click to reset</xfm:hint>");
                    out.println("   <xfm:image>" + MenuConstants.RESET + "</xfm:image>");
                    out.println("   <xfm:caption>" + MenuConstants.RESET + "</xfm:caption>");
                    out.println(" </xfm:submit>");
                    bFieldsFound = false; // Don't need Submit/Reset button
                }
                else if ((strCommand.equalsIgnoreCase(MenuConstants.FIRST))
                        || (strCommand.equalsIgnoreCase(MenuConstants.PREVIOUS))
                        || (strCommand.equalsIgnoreCase(MenuConstants.NEXT))
                        || (strCommand.equalsIgnoreCase(MenuConstants.LAST))
                        || (strCommand.equalsIgnoreCase(MenuConstants.SUBMIT))
                        || (strCommand.equalsIgnoreCase(MenuConstants.DELETE)))
                { // Valid command - send it as a post command
                    out.println(" <xfm:submit name=\"" + DBParams.COMMAND + "\" ref=\"" + strCommand + "\" to=\"submit1\">");
                    out.println("   <xfm:hint>Click to submit</xfm:hint>");
                    out.println("   <xfm:image>" + strCommand + "</xfm:image>");
                    out.println("   <xfm:caption>" + strCommand + "</xfm:caption>");
                    out.println(" </xfm:submit>");
                    bFieldsFound = false; // Don't need Submit button
                }
                else if ((strCommand.equalsIgnoreCase(MenuConstants.FORM)) || (strCommand.equalsIgnoreCase(MenuConstants.FORMLINK)))
                { // Valid command - send it as a post command
                    strCommand = MenuConstants.FORM;
                    String strRecord = this.getMainRecord().getClass().getName().toString();
                    String strLink = "?" + DBParams.RECORD + "=" + strRecord + "&amp;" + DBParams.COMMAND + "=" + strCommand;
                    out.println(" <xfm:trigger name=\"" + strCommand + "\" ref=\"" + strLink + "\">");
                    out.println("   <xfm:hint>Form view</xfm:hint>");
                    out.println("   <xfm:image>" + strCommand + "</xfm:image>");
                    out.println("   <xfm:caption>" + strCommand + "</xfm:caption>");
                    out.println(" </xfm:trigger>");
                }
                else if (strCommand.equalsIgnoreCase(MenuConstants.LOOKUP))
                { // Valid command - send it as a post command
                	String strLink = null;
                	if (this.getMainRecord() != null)
                	{
                		String strRecord = this.getMainRecord().getClass().getName().toString();
                		strLink = "?" + DBParams.RECORD + "=" + strRecord;
                	}
                	else
                	{
                		String strScreen = this.getScreenField().getParentScreen().getClass().getName().toString();
                		strLink = "?" + DBParams.SCREEN + "=" + strScreen;               		
                	}
                    out.println(" <xfm:trigger name=\"" + strCommand + "\" ref=\"" + strLink + "\">");
                    out.println("   <xfm:hint>Form view</xfm:hint>");
                    out.println("   <xfm:image>" + strCommand + "</xfm:image>");
                    out.println("   <xfm:caption>" + strCommand + "</xfm:caption>");
                    out.println(" </xfm:trigger>");
                }
                else
                { // Valid command - send it as a post command
                    //+ Add code here to process a doCommand(xxx)
                }
            }
        }
        if (this.getScreenField().getParentScreen() instanceof GridScreen)
            bFieldsFound = false;        // Don't Need these buttons for grid display
        if (this.getScreenField().getParentScreen() instanceof BaseReportScreen)
            if (HtmlConstants.DISPLAY.equalsIgnoreCase(this.getProperty(HtmlConstants.FORMS)))
                bFieldsFound = false;        // Don't Need these buttons for grid display
//            if (!this.getScreenField().getParentScreen().getEditing())
//                bFieldsFound = false;        // Don't Need these buttons for grid display
        if (bFieldsFound)
        {
            out.println(" <xfm:submit name=\"" + DBParams.COMMAND + "\" ref=\"" + MenuConstants.SUBMIT + "\" to=\"submit1\">");
            out.println("   <xfm:hint>Click to submit</xfm:hint>");
            out.println("   <xfm:image>" + MenuConstants.SUBMIT + "</xfm:image>");
            out.println("   <xfm:caption>" + MenuConstants.SUBMIT + "</xfm:caption>");
            out.println(" </xfm:submit>");

            out.println(" <xfm:submit name=\"" + MenuConstants.RESET + "\" ref=\"" + MenuConstants.RESET + "\">");
            out.println("   <xfm:hint>Click to reset</xfm:hint>");
            out.println("   <xfm:image>" + MenuConstants.RESET + "</xfm:image>");
            out.println("   <xfm:caption>" + MenuConstants.RESET + "</xfm:caption>");
            out.println(" </xfm:submit>");
        }
        out.println(Utility.endTag("toolbars"));
        return bFieldsFound;
    }
}
