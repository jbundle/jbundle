package org.jbundle.base.screen.model;

/**
 * @(#)AppletScreen.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.field.BaseField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * AppletScreen - Set up Application Screen.
 * <p/>Contains the main screen, toolbars, status bar(s), etc.
 */
public class AppletScreen extends TopScreen
{
    /**
     * Constructor.
     */
    public AppletScreen()
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
    public AppletScreen(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize the RecordOwner.
     * This initializer is required by the RecordOwner interface.
     * @param record The main record for this screen.
     * @param parent The parent screen.
     * @param properties The properties object.
     * @param location (property) The location of this component within the parent.
     * @param display (property) Do I display the field desc?
     */
    public AppletScreen(RecordOwnerParent parent, FieldList record, Object properties)
    {
        this();
        this.init(parent, record, properties);
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
        m_screenParent = parentScreen;          // Now screeninfo can get some FontMetrics

        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Free the resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Add the toolbars (not usually used for AppletScreens).
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {   // Obviously, an applet shouldn't add a toolbar!
        return null;
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        Task task = super.getTask();
        if (task != null)
            return task;
        if (this.getScreenFieldView() != null)
            if (this.getScreenFieldView().getControl() != null)
                return (Task)this.getScreenFieldView().getControl();    // The JAppletScreen is the root task for this screen.
        return null;    //?return m_Applet;   // Never
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
            position = ScreenConstants.FIRST_SCREEN_LOCATION;
        if (position == ScreenConstants.NEXT_LOGICAL)
            position = ScreenConstants.BELOW_LAST_CONTROL;      // Stack all screens (should only be applet)
        if (position == ScreenConstants.ADD_SCREEN_VIEW_BUFFER)
            position = ScreenConstants.ADD_VIEW_BUFFER;     // No buffer around frame!
        return super.getNextLocation(position, setNewAnchor);
    }
    /**
     * Get the top applet screen.
     * @return The applet screen (this).
     */
    public AppletScreen getAppletScreen()
    { // The root window is the ChildScreen
        return this;    // Found it!
    }
    /**
     * Create the description for this control.
     * (None on Applets)
     */   
    public void setupControlDesc()
    {
    }
    /**
     * Don't set up any screen fields (initially) for an applet.
     */   
    public void setupSFields()
    {
        // Don't setup any screen fields (as the default!)
    }
    /**
     * The object/screenfield to undo.
     */
    private Object m_objUndo = null;
    /**
     * The screen field target.
     */
    private ScreenField m_sfTarget = null;
    /**
     * New target means can't undo last target.
     * @param sField The new undo target.
     */
    public void undoTargetGained(ScreenField sField)
    {
        if (m_sfTarget != sField)
        {   // To restore this field, just do a fieldToControl()
            m_sfTarget = sField;
            m_objUndo = sField;
        }
    }
    /**
     * Set the target and original data of the next undo.
     * @param sField The new undo target.
     * @param data the data to undo.
     */
    public void undoTargetLost(ScreenField sField, Object data)
    {       // To restore this field, you will have to set Data (this is usually called using the Undo menu item (not ctrl-z))
        m_objUndo = data;
        m_sfTarget = sField;
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @param true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        boolean bFlag = false;
        if (strCommand.equalsIgnoreCase(ThinMenuConstants.UNDO))
        {   // Special - handle undo
            if (m_sfTarget != null)
                if (m_sfTarget.getScreenFieldView().getControl() != null)
                if (m_sfTarget.getScreenFieldView().getControl().hasFocus())
            {
                if (m_objUndo == m_sfTarget)
                {
                    m_sfTarget.fieldToControl();    // Restore original data
                }
                else
                {
                    BaseField field = (BaseField)m_sfTarget.getConverter().getField();
                    if (field != null)
                        field.setData(m_objUndo);
                }
                bFlag = true; // Command handled
            }
        }
        //xif (bFlag == false) The AppletScreen doesn't processes any commands (child screens will)
        //x    bFlag = super.doCommand(strCommand, sourceSField, iUseSameWindow);  // This will send the command to my parent
        if (this.getScreenFieldView() != null)
            return this.getScreenFieldView().doCommand(strCommand);    // Instead I do this
        return bFlag;
    }
    /**
     * Setup a default task for this screen.
     * @param application The application for the new task.
     */
    public void setupDefaultTask(Application application)
    {
        this.getScreenFieldView().setupDefaultTask(application);
    }
    public boolean showHelpScreen(String strURL, int iOptions)
    {
    	return this.getScreenFieldView().showDocument(strURL, iOptions);
    }
    /**
     * Title for this screen.
     * @return the screen title.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    {
        for (int i = 0; i < this.getSFieldCount(); i++)
        {
        	if (this.getSField(i) instanceof BaseScreen)
        		return ((BaseScreen)this.getSField(i)).getTitle();
        }
        return super.getTitle();
    }
}
