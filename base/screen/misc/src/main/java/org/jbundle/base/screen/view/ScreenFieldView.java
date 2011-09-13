/*
 * ScreenModel.java
 *
 * Created on April 27, 2000, 2:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view;

import java.awt.Component;
import java.awt.LayoutManager;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.DBException;
import org.jbundle.model.db.FieldComponent;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.util.Application;


/** 
 * The screen field view interface.
 * @author  Administrator
 * @version 1.0.0
 */
public interface ScreenFieldView extends FieldComponent
{

    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenField model, boolean bEditableControl);
    /**
     * Free.
     */
    public void free();
    /**
     * Get the model for this view.
     * @return The model.
     */
    public ScreenField getScreenField();
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl);
    /**
     * Set the physical control for this view.
     * @param control The physical control for this view.
     */
    public void setControl(Component control);
    /**
     * Get the physical component associated with this view.
     * @return The physical control.
     */
    public Component getControl();
    /**
     * Get one of the physical components associated with this SField.
     * @param int iLevel    CONTROL_TOP - Parent physical control; CONTROL_BOTTOM - Lowest child control
     * NOTE: This method is used for complex controls such as a scroll box, where this control must be
     * added to the parent, but sub-controls must be added to a lower level physical control.
     * @param iLevel The level for this control (top/bottom/etc).
     * @return The control for this view.
     */
    public Component getControl(int iLevel);
    /**
     * Set the the physical control color, font etc.
     * @param control The physical control.
     * @param bSelected Is it selected?
     * @param bIsInput This this an input (vs a display) field?
     * @param bGridControl Is it a grid control?
     */
    public void setControlAttributes(Component control, boolean bIsInput, boolean bSelected, boolean bGridControl);
    /**
     * Synchronize the physical control with the ScreenField.
     */
    public void addPhysicalControl();
    /**
     * Get the class of this component's state.
     * The state is an object which represents the state of this control.
     * @return The class of the control's value.
     */
    public Class<?> getStateClass();
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control);
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue);
    /**
     * Set the converter to this state. State is defined by the component.
     * @param objValue The value to set the control to (class of object depends on the control).
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return Error code.
     */
    public int setFieldState(Object objValue, boolean bDisplayOption, int iMoveMode);
    /**
     * Get this field's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @return The field's value (class defined by the field type).
     */
    public Object getFieldState();
    /**
     * Give this control the input focus.
     * @return True if successful.
     */
    public boolean requestFocus();
    /**
     * Do I create a separate control for the description for this type of control.
     * Generally yes, but you may want to override in checkboxes or buttons to include the description in the control.
     * @return True as buttons have the description contained in the control.
     */
    public boolean getSeparateFieldDesc();
    /**
     * Validate the current field, update the current grid record.
     */
    public void finalizeThisScreen();
    /**
     * Request focus?
     * @param bIsFocusTarget Return true if the control will accept the focus.
     */
    public void setRequestFocusEnabled(boolean bIsFocusTarget);
    /**
     * Setup this screen's screen layout.
     * Usually, you use JAVA layout managers, but you may also use ScreenLayout.
     * @return The new layout manager.
     */
    public LayoutManager addScreenLayout();
    /**
     * Process the command using this view.
     * @param strCommand The command to process.
     * @return True if processed.
     */
    public boolean doCommand(String strCommand);
    /**
     * Add a standard top-level menu item and the standard detail actions.
     * @param strMenuName The menu name to add.
     * @param rgchShortcuts The shortcuts for the menu.
     * @return The new menu.
     */
    public JMenu addStandardMenu(String strMenuName, char rgchShortcuts[]);
    /**
     * Add the menu items to this frame.
     */
    public JMenuItem addMenuItem(JMenu menu, String strMenuDesc);
    /**
     * Manually setup the JTable from this model.
     */
    public void setupTableFromModel();
    /**
     * Requery the recordset.
     */
    public void reSelectRecords();
    /**
     * Get the currently selected row.
     * @return The current row (or -1 if none).
     */
    public int getSelectedRow();
    /**
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(BaseMessage message);
    /**
     * Resurvey the child control(s) and resize frame.
     * @param strTitle The new title for the screen (frame).
     */
    public void resizeToContent(String strTitle);
    /**
     * Setup a default task for this screen.
     * @param application The application to add the task to.
     */
    public void setupDefaultTask(Application application);
    /**
     * Enable or disable this control.
     * @param bEnable If true, enable this field.
     */
    public void setEnabled(boolean bEnable);
    
    ////////////////////////////////////////////////////
    // HTML Stuff
    ////////////////////////////////////////////////////
    
    /**
     * This is a utility method to show an HTML page.
     * @param strURL The URL to show.
     * @param iOptions TODO
     * @return True if successful.
     */
    public boolean showDocument(String strURL, int iOptions);
    /**
     * Process all the submitted params.
     * @param out The Html output stream.
     * @exception DBException File exception.
     */
    public void processInputData(PrintWriter out) throws DBException;
    /**
     * Output this screen using HTML.
     * Display the html headers, etc. then:
     * <ol>
     * - Parse any parameters passed in and set the field values.
     * - Process any command (such as move=Next).
     * - Render this screen as Html (by calling printHtmlScreen()).
     * </ol>
     * @param out The output stream.
     * @param reg TODO
     * @exception DBException File exception.
     */
    public void printReport(PrintWriter out, ResourceBundle reg) throws DBException;
    /**
     * Print this screen's content area.
     * @param out The out stream.
     * @param reg TODO
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg) throws DBException;
    /**
     * Move the HTML input to the screen record fields.
     * @param strSuffix value to add to the end of the field name before retrieving the param.
     * @exception DBException File exception.
     * @return bParamsFound True if params were found and moved.
     */
    public int moveControlInput(String strSuffix) throws DBException;
    /**
     * Set the default button for this basepanel.
     * @param The button to default to on return.
     */
    public void setDefaultButton(ScreenFieldView button);
    /**
     * Move the HTML input format to the fields and do the action requested.
     * @param bDefaultParamsFound If the params have been found yet.
     * @return true if input params have been found.
     * @exception DBException File exception.
     */
    public boolean processServletCommand() throws DBException;
    /**
     * Get the print options (view defined).
     * @return The HTML options.
     * @exception DBException File exception.
     */
    public int getPrintOptions() throws DBException;
    /**
     * Display this control in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions);
    /**
     * Display this control's data in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions);
    /**
     * Display this sub-control in html input format?
     * @return True if this sub-control is printable.
     */
    public boolean isPrintableControl(int iPrintOptions);
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartForm(PrintWriter out, int iPrintOptions);
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndForm(PrintWriter out, int iPrintOptions);
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartField(PrintWriter out, int iPrintOptions);
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndField(PrintWriter out, int iPrintOptions);
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions);
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions);
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartField(PrintWriter out, int iPrintOptions);
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndField(PrintWriter out, int iPrintOptions);
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public int printStartGridScreenData(PrintWriter out, int iPrintOptions);
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndGridScreenData(PrintWriter out, int iPrintOptions);
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordGridData(PrintWriter out, int iPrintOptions);
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordGridData(PrintWriter out, int iPrintOptions);
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printNavButtonControls(PrintWriter out, int iPrintOptions);
    /**
     * Display the start record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordData(Record record, PrintWriter out, int iPrintOptions);
    /**
     * Display the end record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordData(Record record, PrintWriter out, int iPrintOptions);
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @return True if a heading or footing exists.
     * @exception DBException File exception.
     */
    public boolean printHeadingFootingControls(PrintWriter out, int iPrintOptions);
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void printScreenFieldData(ScreenField sField, PrintWriter out, int iPrintOptions);
    /**
     * Display this screen's toolbars in html input format.
     * @param out The HTML output stream.
     * @param iHtmlAttributes The HTML attributes.
     *  returns true if default params were found for this form.
     * @exception DBException File exception.
     */
    public boolean printToolbarData(boolean bFieldsFound, PrintWriter out, int iHtmlAttributes);
    /**
     * Display this screen's toolbars in html input format.
     * @param out The HTML output stream.
     * @param iHtmlAttributes The HTML attributes.
     *  returns true if default params were found for this form.
     * @exception DBException File exception.
     */
    public boolean printToolbarControl(boolean bFieldsFound, PrintWriter out, int iHtmlAttributes);
    /**
     * Is this recordowner a batch process, or an interactive screen?
     * @return True if this is a batch process.
     */
    public boolean isBatch();
    /**
     * Get the name of the stylesheet.
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getStylesheetPath();
}
