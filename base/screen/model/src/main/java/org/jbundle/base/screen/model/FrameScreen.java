/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)FrameScreen.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * FrameScreen - Set up Screen Frame.
 * The Frame is the model for the screen frame for AWT or Swing views.
 */
public class FrameScreen extends BasePanel
{

    /**
     * Constructor.
     */
    public FrameScreen()
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
    public FrameScreen(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Free this control's resources.
     */
    boolean freeing = false;
    public void free()
    {
    	if (!freeing)
    	{	// Only call this once (since my child (AppletScreen) tries to free me.
    		freeing = true;
    		super.free();
    	}
    }
    /**
     * Add toolbars.
     * @return The toolbar (Don't add toolbars from a frame).
     */
    public ToolScreen addToolbars()
    {   // Obviously, a frame shouldn't add a toolbar!
        return null;
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child the matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @param true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        if (strCommand.equalsIgnoreCase(ThinMenuConstants.CLOSE))
        {
            this.free();
            return true;
        }
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }
    /**
     * Utility to get the sub-screen (applet).
     * @return The applet (sub) screen.
     */
    public AppletScreen getAppletScreen()
    {
        AppletScreen appletScreen = null;
        if (this.getSFieldCount() > 0)
            appletScreen = (AppletScreen)this.getSField(0);
        return appletScreen;
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        if (this.getSFieldCount() > 0)
            return ((AppletScreen)this.getSField(0)).getTask();   // the applet screen has the task
        return null;    // Never
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
            position = ScreenConstants.FIRST_FRAME_LOCATION;
        if (position == ScreenConstants.NEXT_LOGICAL)
            position = ScreenConstants.BELOW_LAST_CONTROL;      // Stack all screens (should only be applet)
        if (position == ScreenConstants.ADD_SCREEN_VIEW_BUFFER)
            position = ScreenConstants.ADD_VIEW_BUFFER;     // No buffer around frame!
        return super.getNextLocation(position, setNewAnchor);
    }
    /**
     * Create the description for this control.
     * (None on Frames)
     */   
    public void setupControlDesc()
    {
    } // No desc on Frames!
    /**
     * Don't setup any screen fields.
     */   
    public void setupSFields()
    {
        // Don't setup any screen fields
    }
}
