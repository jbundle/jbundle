/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.LayoutManager;
import java.io.PrintWriter;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.model.db.Convert;
import org.jbundle.model.db.Rec;
import org.jbundle.model.message.Message;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Constants;


/**
 * ScreenFieldViewAdapter is the base view for all screen views.
 */
public abstract class ScreenFieldViewAdapter extends Object
    implements ScreenFieldView
{
    /**
     * The screen model.
     */
    protected ScreenField m_model = null;
    /**
     * Is this control editable.
     */
    protected boolean m_bEditableControl = false;

    /**
     * Constructor.
     */
    public ScreenFieldViewAdapter()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public ScreenFieldViewAdapter(ScreenField model, boolean bEditableControl)
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
        m_model = model;
        m_bEditableControl = bEditableControl;
    }
    /**
     * Free.
     */
    public void free()
    {
        if (this.getScreenField() != null)
            this.getScreenField().setScreenFieldView(null);
        m_model = null;
    }
    /**
     * Get the model for this view.
     * @return The model.
     */
    public ScreenField getScreenField()
    {
        return m_model;
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        return null;
    }
    /**
     * Set the physical control for this view.
     * @param control The physical control for this view.
     */
    public void setControl(Component control)
    {
    }
    /**
     * Get the physical component associated with this view.
     * @return The physical control.
     */
    public Component getControl()
    {
        return null;
    }
    /**
     * Get one of the physical components associated with this SField.
     * @param int iLevel    CONTROL_TOP - Parent physical control; CONTROL_BOTTOM - Lowest child control
     * NOTE: This method is used for complex controls such as a scroll box, where this control must be
     * added to the parent, but sub-controls must be added to a lower level physical control.
     * @param iLevel The level for this control (top/bottom/etc).
     * @return The control for this view.
     */
    public Component getControl(int iLevel)
    {
        return this.getControl();
    }
    /**
     * Set the the physical control color, font etc.
     * @param component The physical control.
     * @param bSelected Is it selected?
     * @param bIsInput This this an input (vs a display) field?
     * @param bGridControl Is it a grid control?
     */
    public void setControlAttributes(Component component, boolean bIsInput, boolean bSelected, boolean bGridControl)
    {
    }
    /**
     * Synchronize the physical control with the ScreenField.<p>
     */
    public void addPhysicalControl()
    {
    }
    /**
     * Get the class of this component's state.
     * The state is an object which represents the state of this control.
     * @return The class of the control's value.
     */
    public Class<?> getStateClass()
    {
        return String.class;    // By default
    }
    /**
     * Get this component's value as an object that FieldInfo can use.
     * @return The control's value.
     */
    public Object getControlValue()
    {
        return this.getComponentState(this.getControl());
    }
    /**
     * Here is the field's value (data), set the component to match.
     * @param objValue The value to set the control to.
     */
    public void setControlValue(Object objValue)
    {
        this.setComponentState(this.getControl(), objValue);
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control)
    {
        return null;    // Must override
    }
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        // Must override
    }
    /**
     * Set the field to this state. State is defined by the component.
     * @param objValue The value to set the field to (class of object depends on the control).
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return Error code.
     */
    public int setFieldState(Object objValue, boolean bDisplayOption, int iMoveMode)
    {
        if (this.getScreenField().getConverter() == null)
            return DBConstants.NORMAL_RETURN;
        if (!(objValue instanceof String))
            return this.getScreenField().getConverter().setData(objValue, bDisplayOption, iMoveMode);
        else
            return this.getScreenField().getConverter().setString((String)objValue, bDisplayOption, iMoveMode);
    }
    /**
     * Get this field's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @return The field's value (class defined by the field type).
     */
    public Object getFieldState()
    {
        if (this.getScreenField().getConverter() == null)
            return null;
        String string = this.getScreenField().getConverter().getString();
        if (string == null)
            string = Constants.BLANK;
        return string;
    }
    /**
     * Give this control the input focus.
     * @return True if successful.
     */
    public boolean requestFocus()
    {
        return true;    // Focus handled
    }
    /**
     * Do I create a separate control for the description for this type of control.
     * Generally yes, but you may want to override in checkboxes or buttons to include the description in the control.
     * @return True as buttons have the description contained in the control.
     */
    public boolean getSeparateFieldDesc()
    {
        return true; // Typically, you need a separate field for the desc.
    }
    /**
     * Setup this screen's screen layout.
     * Usually, you use JAVA layout managers, but you may also use ScreenLayout.
     * @return The new layout manager.
     */
    public LayoutManager addScreenLayout()
    {
        return null;    // This is only called for Containers such as BasePanel, etc.
    }
    /**
     * Process the command using this view.
     * @param strCommand The command to process.
     * @return True if processed.
     */
    public boolean doCommand(String strCommand)
    {
        return false; // Not processed, BasePanels and above will override
    }
    /**
     * Add a standard top-level menu item and the standard detail actions.
     * @param strMenuName The menu name to add.
     * @param rgchShortcuts The shortcuts for the menu.
     * @return The new menu.
     */
    public JMenu addStandardMenu(String strMenuName, char rgchShortcuts[])
    {
        return null;    // Override this in SBaseMenuBar views.
    }
    /**
     * Add the menu items to this frame.
     */
    public JMenuItem addMenuItem(JMenu menu, String strMenuDesc)
    {
        return null;    // Override this in SBaseMenuBar views.
    }
    /**
     * Manually setup the JTable from this model.
     * Used in VGridScreen.
     */
    public void setupTableFromModel()
    {
    }
    /**
     * Requery the recordset.
     */
    public void reSelectRecords()
    {
        // Override in VGridScreen.
    }
    /**
     * Get the currently selected row.
     * @return The current row (or -1 if none).
     */
    public int getSelectedRow()
    {
        return -1;  // Not used for HTML
    }
    /**
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(Message message)
    {
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Resurvey the child control(s) and resize frame.
     * @param strTitle The new title for the screen (frame).
     */
    public void resizeToContent(String strTitle)
    {
    }
    /**
     * Setup a default task for this screen.
     * @param application The application to add the task to.
     */
    public void setupDefaultTask(App application)
    {
        // Override in VAppletScreen
    }
    /**
     * Enable or disable this control.
     * @param bEnable If true, enable this field.
     */
    public void setEnabled(boolean bEnable)
    {
        // Override
    }
    @Override
    public void init(ScreenLoc itsLocation, ComponentParent parentScreen, Convert fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties) {
        this.init(null, true);  // Never called.
    }
    @Override
    public ComponentParent getParentScreen() {
        return null;    // Override
    }
    @Override
    public int controlToField() {
        return Constants.NORMAL_RETURN;    // Override
    }
    @Override
    public void fieldToControl() {
        // Override
    }
    /**
     * This is a utility method to show an HTML page.
     * @param strURL The URL to show.
     */
    public boolean showDocument(String strURL, int iOptions)
    {
    	return false;	// Should override
    }
    /**
     * Request focus?
     * @param bIsFocusTarget Is focus target?
     */
    public void setRequestFocusEnabled(boolean bIsFocusTarget)
    {   // Only for swing
    }
    /**
     * Validate the current field, update the current grid record.
     */
    public void finalizeThisScreen()
    {
           // Override this to do something.
    }
    /**
     * Process all the submitted params.
     * @param out The Html output stream.
     * @exception DBException File exception.
     */
    public void processInputData(PrintWriter out)
        throws DBException
    {
        // Override this!
    }
    /**
     * Output this screen using HTML.
     * Display the html headers, etc. then:
     * <ol>
     * - Parse any parameters passed in and set the field values.
     * - Process any command (such as move=Next).
     * - Render this screen as Html (by calling printHtmlScreen()).
     * </ol>
     * @param out The output stream.
     * @exception DBException File exception.
     */
    public void printReport(PrintWriter out, ResourceBundle reg) throws DBException
    {
        // Override this!
    }
    /**
     * Print this screen's content area.
     * @param out The out stream.
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg) throws DBException
    {
        // Override this!
    }
    /**
     * Move the HTML input to the screen record fields.
     * @param strSuffix value to add to the end of the field name before retrieving the param.
     * @exception DBException File exception.
     * @return bParamsFound True if params were found and moved.
     */
    public int moveControlInput(String strSuffix) throws DBException
    {   
        return DBConstants.NO_PARAMS_FOUND;
    }
    /**
     * Set the default button for this basepanel.
     * @param The button to default to on return.
     */
    public void setDefaultButton(ScreenFieldView button)
    {
    }
    /**
     * Move the HTML input format to the fields and do the action requested.
     * @param bDefaultParamsFound If the params have been found yet.
     * @return true if input params have been found.
     * @exception DBException File exception.
     */
    public boolean processServletCommand() throws DBException
    {
        return false;
    }
    /**
     * Get the print options (view defined).
     * @return The HTML options.
     * @exception DBException File exception.
     */
    public int getPrintOptions() throws DBException
    {
        return 0;   // Override this!
    }
    /**
     * Display this control in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions)
    {
        return false;   // Override this!
    }
    /**
     * Display this control's data in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        return false;   // Override this!
    }
    /**
     * Display this sub-control in html input format?
     * @return True if this sub-control is printable.
     */
    public boolean isPrintableControl(int iPrintOptions)
    {
        return true;    // By default all sub-controls are printable.
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartForm(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndForm(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartField(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndField(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartField(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndField(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public int printStartGridScreenData(PrintWriter out, int iPrintOptions)
    {
        return iPrintOptions;
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndGridScreenData(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordGridData(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordGridData(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printNavButtonControls(PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the start record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordData(Rec record, PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display the end record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordData(Rec record, PrintWriter out, int iPrintOptions)
    {
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @return True if a heading or footing exists.
     * @exception DBException File exception.
     */
    public boolean printHeadingFootingControls(PrintWriter out, int iPrintOptions)
    {
        return false;
    }
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void printScreenFieldData(ScreenField sField, PrintWriter out, int iPrintOptions)
    {
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
        return false;
    }
    /**
     * Display this screen's toolbars in html input format.
     * @param out The HTML output stream.
     * @param iHtmlAttributes The HTML attributes.
     *  returns true if default params were found for this form.
     * @exception DBException File exception.
     */
    public boolean printToolbarControl(boolean bFieldsFound, PrintWriter out, int iHtmlAttributes)
    {
        return false;
    }
    /**
     * Get the converter for this screen field.
     * NOTE: Be careful, this method is just for convenience and is required by the ScreenComponent
     * interface, although this is not the screen component.
     * @return The converter for this screen field.
     */
    public Convert getConverter()
    {
        return this.getScreenField().getConverter();    // Utility method
    }
    /**
     * Get the converter for this screen field.
     * NOTE: Be careful, this method is just for convenience and is required by the ScreenComponent
     * interface, although this is not the screen component.
     * @return The converter for this screen field.
     */
    public void setConverter(Convert converter)
    {
        this.getScreenField().setConverter(converter);    // Utility method
    }
    /**
     * Is this recordowner a batch process, or an interactive screen?
     * @return True if this is a batch process.
     */
    public boolean isBatch()
    {
        return false;   // Override this
    }
    /**
     * Get the rich text type for this control.
     * @return The type (Such as HTML, Date, etc... very close to HTML control names).
     */
    public String getInputType(String strViewType)
    {
        return this.getScreenField().getInputType(strViewType);
    }
    /**
     * Get the name of the stylesheet.
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getStylesheetPath()
    {
    	return null;	// Override this
    }
    /**
     * Return the first char of this string converted to upper case.
     * @param The string to get the first char to upper.
     * @param chDefault If the string is empty, return this.
     * @return The first char in the string converted to upper.
     */
    public static char getFirstToUpper(String string, char chDefault)
    {
        if ((string != null) && (string.length() > 0))
            chDefault = Character.toUpperCase(string.charAt(0));
        return chDefault;
    }
    /**
     * Return the first char of this string converted to upper case.
     * @param The string to get the first char to upper.
     * @return The first char in the string converted to upper.
     */
    public static char getFirstToUpper(String string)
    {
        return getFirstToUpper(string, ' ');
    }
    /**
     * Get this property.
     * @param strKey The property key.
     * @return The property value.
     */
    public String getProperty(String strKey)
    {
        if (this.getScreenField() instanceof BasePanel)
            return ((BasePanel)this.getScreenField()).getProperty(strKey);
        else
            return this.getScreenField().getParentScreen().getProperty(strKey);
    }
    /**
     * Convenience method to get the model's main record.
     * @return The model's main record.
     */
    public Record getMainRecord()
    {
        if (this.getScreenField() instanceof BasePanel)
            return ((BasePanel)this.getScreenField()).getMainRecord();
        else
            return this.getScreenField().getParentScreen().getMainRecord();
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        if (this.getScreenField() instanceof BasePanel)
            return ((BasePanel)this.getScreenField()).getTask();
        else
            return this.getScreenField().getParentScreen().getTask();
    }
}
