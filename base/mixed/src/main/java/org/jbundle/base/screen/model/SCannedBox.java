/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SCannedBox.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.event.RemoveConverterOnCloseHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.field.convert.FieldDescConverter;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * SCannedBox - Implements a button to carry out some standard functions.<p/>
 *
 * @version 1.0.0
 * @author    Don Corley
 *
 * <pre>
 * Add - Create a new "Maintenance" screen for this table.
 * Next - Move to the next record in this table.
 * Cancel - Set this field to a blank
 * Undo - Set this field to a blank
 * Lookup - Create the "Lookup" screen for this table (and link back to this table).
 * Print - Print this screen.
 * </pre>
 */
public class SCannedBox extends SButtonBox
{
    /**
     * The (optional) record target.
     */
    protected Record m_record = null;
    /**
     * The (optional) field target.
     */
    protected BaseField m_field = null;
    /**
     * The (optional) value.
     */
    protected String m_strValue = null;
    /**
     * Constants.
     */
    public static final String MAIL = "Mail";
    public static final String EMAIL = "EMail";
    public static final String PHONE = "Phone";
    public static final String FAX = "Fax";
    public static final String URL = "URL";
    /**
     * The name of the open button.
     */
    public static final String OPEN = "Open";
    public static final String EDIT = "Edit";
    public static final String CLEAR = "Clear";

    /**
     * Constructor.
     */
    public SCannedBox()
    {
        super();
    }
    /**
     * Constructor (To set a target record).
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strDesc The description of this button.
     * @param record The (optional) record.
     */
    public SCannedBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, String strDesc, int iDisplayFieldDesc, Record record)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null, null, strDesc, strDesc, null, record, null);
    }
    /**
     * Constructor (This version has a field as its target).
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strDesc The description of this button.
     * @param field The (optional) field.
     */
    public SCannedBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, String strDesc, int iDisplayFieldDesc, BaseField field)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null, null, strDesc, null, null, null, field);
    }
    /**
     * Constructor (use this constructor for a standard command).
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strDesc The description of this button.
     * @param strCommand The command to send on button press.
     */
    public SCannedBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strCommand, String strDesc)
    {
        this();
        String strImage = null;
        if (strDesc == null)
            strImage = strCommand;  // Load the bitmap that matches this name
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null, strDesc, strImage, strCommand, null, null, null);
    }
    /**
     * Constructor - string is the menu resource.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param resource The resource class for this button.
     */
    public SCannedBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strResource)
    {
        this();
        String strValue = null;         // Change this to look up the resources!
        String strImage = strResource;
        BaseApplication application = (BaseApplication)parentScreen.getTask().getApplication();
        String strDesc = application.getResources(ResourceConstants.MENU_RESOURCE, true).getString(strResource);
        String strCommand = strResource;
        String strToolTip = application.getResources(ResourceConstants.MENU_RESOURCE, true).getString(strResource + DBConstants.TIP);
        if (strToolTip.equals(strResource + DBConstants.TIP))
            strToolTip = null;
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, strImage, strCommand, strToolTip, null, null);
    }
    /**
     * Constructor (usually for setting a field value).
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strValue The value to set the field on button press.
     * @param strDesc The description of this button.
     * @param strImage The image filename for this button.
     * @param strCommand The command to send on button press.
     * @param strToolTip The tooltip for this button.
     */
    public SCannedBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc, String strImage, String strCommand, String strToolTip)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, strImage, strCommand, strToolTip, null, null);
    }
    /**
     * Constructor (with all available options).
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strValue The value to set the field on button press.
     * @param strDesc The description of this button.
     * @param strImage The image filename for this button.
     * @param strCommand The command to send on button press.
     * @param strToolTip The tooltip for this button.
     * @param record The (optional) record.
     * @param field The (optional) field.
     */
    public SCannedBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc, String strImage, String strCommand, String strToolTip, Record record, BaseField field)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, strImage, strCommand, strToolTip, record, field);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strValue The value to set the field on button press.
     * @param strDesc The description of this button.
     * @param strImage The image filename for this button.
     * @param strCommand The command to send on button press.
     * @param strToolTip The tooltip for this button.
     * @param record The (optional) record.
     * @param field The (optional) field.
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc, String strImage, String strCommand, String strToolTip, Record record, BaseField field)
    {
        m_record = record;
        m_field = field;
        m_strValue = strValue;
        m_iDisplayFieldDesc = iDisplayFieldDesc;
    
        if (record != null) if (fieldConverter == null) if (this.getDisplayFieldDesc(this))
        {   // Use the record name as the desc
            fieldConverter = new FieldDescConverter(null, record.getRecordName());
            record.addListener(new RemoveConverterOnCloseHandler(fieldConverter));  // Remove on close
        }

        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, strImage, strCommand, strToolTip);
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
        if (strCommand.equalsIgnoreCase(this.getButtonCommand()))
            if (sourceSField == this)
        {       // Only process this command
            Map<String,Object> properties = new Hashtable<String,Object>();
            strCommand = this.getProperties(strCommand, properties);
            if (this.getRecord() != null)
            {
                Task task = null;
                if (this.getRecord().getRecordOwner() != null)
                    task = this.getRecord().getRecordOwner().getTask();
                if (task == null)
                    task = BaseApplet.getSharedInstance();
                Application application = (Application)task.getApplication();
                if (strCommand.equalsIgnoreCase(ThinMenuConstants.LOOKUP))
                {
                    BasePanel parentScreen = Screen.makeWindow(application);
                    GridScreen screen = (GridScreen)this.getRecord().makeScreen(null, parentScreen, ScreenConstants.SELECT_MODE, true, true, true, true, properties);
                    if (this.getRecord().getScreen() == null)
                        screen.setSelectQuery(this.getRecord(), false); // Since this record isn't linked to the screen, manually link it.
                }
                if (strCommand.equals(ThinMenuConstants.NEXT))
                {
                    int errorCode = DBConstants.NORMAL_RETURN;
                    if (this.getRecord() != null)
                    {
                        try
                        {
                            if (this.getRecord().getEditMode() == Constants.EDIT_IN_PROGRESS)
                                this.getRecord().set();
                            if (this.getRecord().getEditMode() == Constants.EDIT_ADD)
                                this.getRecord().add();
                        }
                        catch( DBException e )
                        {
                            this.getRootScreen().displayError(e);
                        }
                        if (errorCode == DBConstants.NORMAL_RETURN)
                        {
                            try
                            {
                                this.getRecord().next();
                            }
                            catch( DBException e )
                            {
                                this.getRootScreen().displayError(e);
                            }
                        }
                    }
                }
                if (strCommand.equalsIgnoreCase(ThinMenuConstants.FORM))
                {
                    BasePanel parentScreen = Screen.makeWindow(application);
                    this.getRecord().makeScreen(null, parentScreen, ScreenConstants.MAINT_MODE, true, true, true, true, properties);
                }
                if (strCommand.equals(ThinMenuConstants.PRINT))
                {   // *** FIX THIS ***
                    BasePanel parentScreen = Screen.makeWindow(application);
                    this.getRecord().makeScreen(null, parentScreen, ScreenConstants.MAINT_MODE, true, true, true, true, properties);
                }
                return true;    // Command handled
            }
            if (m_field != null)
            {
                if (strCommand.equals(ThinMenuConstants.CANCEL))
                {
                    m_field.setString(Constants.BLANK);
                }
                else if (strCommand.equals(CLEAR))
                {
                    m_field.setData(null);
                }
                else if (strCommand.equals(ThinMenuConstants.UNDO))
                {
                    m_field.setString(Constants.BLANK);
                }
                else if (strCommand.equals(MAIL))
                {
                    String strHyperlink = m_field.getHyperlink();
                    if (strHyperlink != null) if (strHyperlink.length() > 0)
                        if (this.getScreenFieldView() != null)
                            this.getScreenFieldView().showDocument(strHyperlink, 0);
                }
                else if (strCommand.equals(EMAIL))
                {
                    String strHyperlink = m_field.getHyperlink();
                    if (strHyperlink != null) if (strHyperlink.length() > 0)
                        if (this.getScreenFieldView() != null)
                            this.getScreenFieldView().showDocument(strHyperlink, 0);
                }
                else if (strCommand.equals(PHONE))
                {
                    String strHyperlink = m_field.getHyperlink();
                    if (strHyperlink != null) if (strHyperlink.length() > 0)
                        if (this.getScreenFieldView() != null)
                            this.getScreenFieldView().showDocument(strHyperlink, 0);
                }
                else if (strCommand.equals(FAX))
                {
                    String strHyperlink = m_field.getHyperlink();
                    if (strHyperlink != null) if (strHyperlink.length() > 0)
                        if (this.getScreenFieldView() != null)
                            this.getScreenFieldView().showDocument(strHyperlink, 0);
                }
                else if (strCommand.equals(URL))
                {
                    String strHyperlink = m_field.getHyperlink();
                    if (strHyperlink != null) if (strHyperlink.length() > 0)
                        if (this.getScreenFieldView() != null)
                            this.getScreenFieldView().showDocument(strHyperlink, 0);
                }
                else
                    return super.doCommand(strCommand, sourceSField, iCommandOptions);    // Command handled
                return true;    // Command handled
            }
            if (m_strValue != null)
            {
                Task task = null;
                RecordOwner recordOwner = null;
                if (m_field != null)
                    recordOwner = m_field.getRecord().getRecordOwner();
                if (recordOwner == null)
                    if (this.getRecord() != null)
                        recordOwner = this.getRecord().getRecordOwner();
                if (recordOwner == null)
                    if (this.getConverter() != null)
                        if (this.getConverter().getField() != null)
                            recordOwner = ((BaseField)this.getConverter().getField()).getRecord().getRecordOwner();
                if (recordOwner != null)
                    task = recordOwner.getTask();
                if (task == null)
                    task = BaseApplet.getSharedInstance();
                Application application = (Application)task.getApplication();
                BasePanel screenParent = BasePanel.makeWindow(application);
                if (strCommand.equals("Record"))
                    BaseScreen.makeScreenFromRecord(null, screenParent, m_strValue, ScreenConstants.MAINT_MODE | ScreenConstants.DEFAULT_DISPLAY, null);
                else
                    BaseScreen.makeNewScreen(m_strValue, null, screenParent, ScreenConstants.DEFAULT_DISPLAY, null, true);
                return true;    // Command handled
            }
            if (strCommand != null) if (sourceSField == this) if (this.getConverter() != null)
            {
            // HACK - I say that SCannedBox should set the target field to the command value, but
            // if I connect a command cannedbox to a field so it gets enabled with it I don't want
            // to set the value, I just want to send the command. You should use a SButtonBox
            // with the value you want set in the constructor!
            // This is a hack, until you can review the code and take this out.
                if (this.getConverter().getField() != null)
                    if ((!(this.getConverter().getField() instanceof NumberField))
                        || (Utility.isNumeric(strCommand)))
                {
                    this.getConverter().setString(strCommand);
                    return true;    // Command handled
                }
            // End hack
            }
        }
        // Do the normal command
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }
    /**
     * Parse the command string into properties.
     * @param strCommand
     * @param properties
     * @return
     */
    public String getProperties(String strCommand, Map<String,Object> properties)
    {
        int iIndex = strCommand.indexOf('=');
        if (iIndex != -1)
        {
            if (this.getParentScreen().getTask() != null)
            	Util.parseArgs(properties, strCommand);
            if (properties.get(DBParams.COMMAND) != null)
                strCommand = (String)properties.get(DBParams.COMMAND);
        }
        return strCommand;
    }
    /**
     * Get the target field for this button's command.
     * @return The target field.
     */
    public BaseField getField()
    {
        return m_field;
    }
    /**
     * Get the target field for this button's command.
     * @return The target field.
     */
    public Record getRecord()
    {
        return m_record;
    }
}
