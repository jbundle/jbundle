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

import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.model.DBException;


/**
 * Set up a tool bar control.
 */
public class HToolScreen extends HBasePanel
{

    /**
     * Constructor.
     */
    public HToolScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HToolScreen(ScreenField model, boolean bEditableControl)
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
     * Display this screen's toolbars in html input format.
     * @param out The HTML output stream.
     * @param iHtmlAttributes The HTML attributes.
     *  returns true if default params were found for this form.
     * @exception DBException File exception.
     */
    public boolean printToolbarData(boolean bFieldsFound, PrintWriter out, int iHtmlAttributes)
    {
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
                    out.println("<input type=\"Reset\"/>");
                    bFieldsFound = false; // Don't need Submit/Reset button
                }
                else if ((strCommand.equalsIgnoreCase(MenuConstants.FIRST))
                        || (strCommand.equalsIgnoreCase(MenuConstants.PREVIOUS))
                        || (strCommand.equalsIgnoreCase(MenuConstants.NEXT))
                        || (strCommand.equalsIgnoreCase(MenuConstants.LAST))
                        || (strCommand.equalsIgnoreCase(MenuConstants.SUBMIT))
                        || (strCommand.equalsIgnoreCase(MenuConstants.DELETE)))
                { // Valid command - send it as a post command
                    out.println("<input type=\"submit\" name=\"" + DBParams.COMMAND + "\" value=\"" + strCommand + "\"/>");
                    bFieldsFound = false; // Don't need Submit button
                }
                else if (strCommand.equalsIgnoreCase(MenuConstants.FORM))
                { // Valid command - send it as a post command
                    if (this.getMainRecord() != null)
                    {
                        String strRecord = this.getMainRecord().getClass().getName().toString();
                        String strLink = "?" + DBParams.RECORD + "=" + strRecord + "&" + DBParams.COMMAND + "=" + MenuConstants.REFRESH;
                        out.println("<input type=\"button\" value=" + strCommand + " onclick=\"window.open('" + strLink + "','_top');\"/>");
                    }
                }
                else if (strCommand.equalsIgnoreCase(MenuConstants.LOOKUP))
                { // Valid command - send it as a post command
                    if (this.getMainRecord() != null)
                    {
                        String strRecord = this.getMainRecord().getClass().getName().toString();
                        String strLink = "?" + DBParams.RECORD + "=" + strRecord;
                        out.println("<input type=\"button\" value=\"Lookup\" onclick=\"window.open('" + strLink + "','_top');\"/>");
                    }
                }
                else
                { // Valid command - send it as a post command
                    //+ Add code here to process a doCommand(xxx)
                }
            }
        }
        String strCommand = MenuConstants.SUBMIT;
        if (this.getScreenField().getParentScreen() instanceof GridScreen)
        {
            strCommand = MenuConstants.LOOKUP;
            if (this.getScreenField().getParentScreen().getEditing())
                bFieldsFound = true;        // Need these buttons for grid input
        }
        if (bFieldsFound)
        {
            out.println("<input type=\"submit\" name=\"" + DBParams.COMMAND + "\" value=\"" + strCommand + "\"/>");
            out.println("<input type=\"Reset\"/>");
        }
        return bFieldsFound;
    }
}
