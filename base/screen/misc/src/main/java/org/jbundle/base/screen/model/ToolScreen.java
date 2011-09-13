/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)ToolScreen.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;


/**
 * ToolScreen - Set up a tool bar control.
 * <pre>
 *      To set up a toolbar that will set the field to 1, 2, 3, etc. on buttons, include a converter and no IDArray
 *          (Pass button count)
 *  new ToolScreen(this.getNextLocation(), this, this.getRecord(kAmAffilFile).getField(kAmAffilComm), false, IDB_MBOOK, null, 17);  // Seventeen buttons
 *      To make a toolbar float, pass location 0, 0
 *      To make a toolbar that does commands, pass null field, pass pointer to button list
 *          (and pass button AND SEPARATOR count)
 *  new ToolScreen(Point(0, 0), this, null, false, IDB_MBOOK, buttons, sizeof(buttons)/sizeof(int));    // Seventeen buttons
 * </pre>
 */
public class ToolScreen extends BasePanel
{

    /**
     * Constructor.
     */
    public ToolScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public ToolScreen(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        int iErrorCode = DBConstants.NORMAL_RETURN; 
        return iErrorCode;
    }
    /**
     * Add toolbars.
     * Obviously, a toolbar shouldn't add a toolbar!
     * @return null.
     */
    public ToolScreen addToolbars()
    {
        return null;
    }
    /**
     * Code this position and Anchor to add it to the LayoutManager.
     * @param position The location constant (see ScreenConstants).
     * @param setNewAnchor The anchor constant.
     * @return The new screen location object.
     */
    public ScreenLocation getNextLocation(short position, short setNewAnchor)
    {
        if (position == ScreenConstants.FIRST_LOCATION)
            position = ScreenConstants.FIRST_FIELD_BUTTON_LOCATION;
        if (position == ScreenConstants.NEXT_LOGICAL)
            position = ScreenConstants.RIGHT_OF_LAST_BUTTON;
        return super.getNextLocation(position, setNewAnchor);
    }
    /**
     * Create the description for this control.
     * (None on Toolbars)
     */   
    public void setupControlDesc()
    {
    }
    /**
     * Override this to add your tool buttons.
     */
    public void setupSFields()
    {
        this.setupStartSFields();   // Back button
        this.setupMiddleSFields();  // Supplied in overriding class
        if (this.getParentScreen() != null)
            this.getParentScreen().addToolbarButtons(this);     // See if my parent has anything to add
        this.setupEndSFields();         // Help button
    }
    /**
     * Override this to add your tool buttons.
     */
    public void setupStartSFields()
    {
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.BACK);
    }
    /**
     * Override this to add your tool buttons.
     */
    public void setupMiddleSFields()
    {
    }
    /**
     * Override this to add your tool buttons.
     */
    public void setupEndSFields()
    {
        new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.HELP);
    }
    /**
     * Controls for a display screen.
     */
    public void setupDisplaySFields()
    {
        new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.FORMLINK);
    }
    /**
     * Controls for a maint screen.
     */
    public void setupMaintSFields()
    {
        new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.LOOKUP);
        new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.SUBMIT);
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.RESET);
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, MenuConstants.DELETE);
    }
    /**
     * Process the command.
     * For a toolscreen, Don't process the command, pass it to my parent for processing.
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        return false; // Not processed, BasePanels and above will override
    }
    /**
     * Display this sub-control in html input format?
     * @param iPrintOptions The view specific print options.
     * @return True if this sub-control is printable.
     */
    public boolean isPrintableControl(ScreenField sField, int iPrintOptions)
    {
        // Override this to break
        if ((sField == null) || (sField == this))
        {       // Asking about this control
            return false;    // Tool screens are not printed as a sub-screen.
        }
        return super.isPrintableControl(sField, iPrintOptions);
    }
    /**
     * Is this an Html toolbar?
     */
    public boolean isToolbar()
    {
        return true;
    }
    /**
     * Display this control's data in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        boolean bControls = super.printData(out, iPrintOptions);
        bControls = this.getScreenFieldView().printToolbarData(bControls, out, iPrintOptions);
        return bControls;
    }
    /**
     * Display this control's data in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions)
    {
        boolean bControls = super.printControl(out, iPrintOptions);
//x        bControls = this.getScreenFieldView().printToolbarControl(bControls, out, iPrintOptions);
        return bControls;
    }
}
