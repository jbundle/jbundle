/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;
import java.io.PrintWriter;
import java.util.Map;
import java.util.ResourceBundle;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.MenuConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.screen.view.ViewFactory;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Constant;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.Params;

/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 */
public abstract class ScreenField extends Object
    implements ScreenComponent
{
    /**
     * Screen view for this model.
     */
    protected ScreenFieldView m_screenFieldView = null;
    /**
     * Converter for this field.
     */
    protected Convert m_converterField = null;
    /**
     * Parent screen.
     */
    protected BasePanel m_screenParent = null;
    /**
     * Location of this screenfield within the parent.
     */
    protected ScreenLocation m_itsLocation = null;
    /**
     * Tab to this screen field?
     */
    protected boolean m_bIsFocusTarget = true;
    /**
     * Will accept input?
     */
    protected boolean m_bEnabled = true;
    /**
     * The value passed in is...
     */
    protected int m_iDisplayFieldDesc = ScreenConstants.DEFAULT_DISPLAY;

    /**
     * Constructor.
     */
    public ScreenField()
    {
        super();
        m_screenFieldView = null; // This can be set between the null constructor and the init call.
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public ScreenField(ScreenLocation itsLocation, BasePanel parentScreen, Convert fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public final void init(ScreenLoc itsLocation, ComponentParent parentScreen, Convert fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this.init((ScreenLocation)itsLocation, (BasePanel)parentScreen, (Converter)fieldConverter, iDisplayFieldDesc, properties);
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
        m_converterField = null;
        m_screenParent = null;
        
        if (itsLocation == null) if (parentScreen != null)
            itsLocation = parentScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR); 
        m_itsLocation = itsLocation;

        m_bIsFocusTarget = true;
        m_bEnabled = true;
        m_screenParent = parentScreen;
        m_converterField = fieldConverter;
        m_iDisplayFieldDesc = iDisplayFieldDesc;    // The value passed in is...

        if (m_screenParent != null)
        {
            if (itsLocation.getLocationConstant() != ScreenConstants.FIRST_SCREEN_LOCATION)
                m_screenParent.addSField(this);
            else
                m_screenParent.addSField(this, 0);
        }
        boolean bEditableControl = true;
        if (this.getScreenFieldView() == null)
            this.setScreenFieldView(this.setupScreenFieldView(bEditableControl));
        this.getScreenFieldView().addPhysicalControl();
        if (m_converterField != null)
            m_converterField.addComponent(this);        // Have the field add me to its list for display
        if (this.getDisplayFieldDesc(this)) if (this.getSeparateFieldDesc()) if (m_converterField != null)
            this.setupControlDesc();
    }
    /**
     * Free.
     */
    public void free()
    {
        if (m_screenFieldView != null)
        {
            m_screenFieldView.free();
            m_screenFieldView = null;
        }
        if (m_screenParent != null)
        {
            m_screenParent.removeSField(this);    // Remove this from the screen's list
        }
        if (m_converterField != null)
            m_converterField.removeComponent(this);   // Make sure field won't send any more updates
        m_converterField = null;
    }
    /**
     * Get the physical component associated with this view.
     * @return The physical control.
     */
    public Object getControl()
    {
        if (this.getScreenFieldView() != null)
            return this.getScreenFieldView().getControl();
        else
            return null;
    }
    /**
     * Get the screen model for this view.
     * @return The screen view.
     */
    public ScreenFieldView getScreenFieldView()
    {
        return m_screenFieldView;
    }
    /**
     * Get the screen model for this view.
     * @param screenFieldView The view for this model.
     */
    public void setScreenFieldView(ScreenFieldView screenFieldView)
    {
        m_screenFieldView = screenFieldView;
    }
    /**
     * Move the control's value to the field.
     * @return An error value.
     */
    public int controlToField()
    {
        int iErrorCode = Constant.NORMAL_RETURN;
        if ((this.getScreenFieldView().getControl() != null) && (this.getConverter() != null))
        {
            Object objValue = this.getScreenFieldView().getComponentState(this.getScreenFieldView().getControl());
            iErrorCode = this.getScreenFieldView().setFieldState(objValue, Constant.DISPLAY, Constant.SCREEN_MOVE);
        }
        return iErrorCode;
    }
    /**
     * Move the field's value to the control.
     */
    public void fieldToControl()
    {
        if (this.getConverter() != null)
        {
            Object objValue = this.getScreenFieldView().getFieldState();
            if (this.getScreenFieldView().getControl() != null)
                this.getScreenFieldView().setComponentState(this.getScreenFieldView().getControl(), objValue);
        }
    }
    /**
     * Do I include a description for sField?
     * Generally yes, except for on GridScreens.
     * @param sField field to check for description includes.
     * @return True if I should display the field desc.
     */
    public boolean getDisplayFieldDesc(ScreenField sField)
    {
        boolean bDisplayDesc = ((m_iDisplayFieldDesc & ScreenConstants.DISPLAY_MASK) != ScreenConstants.DONT_DISPLAY_DESC);   // yes if yes, otherwise no!
        if (m_screenParent != null) if ((m_iDisplayFieldDesc & ScreenConstants.DISPLAY_MASK) == ScreenConstants.DEFAULT_DISPLAY)
            bDisplayDesc = m_screenParent.getDisplayFieldDesc(sField);
        return bDisplayDesc;
    }
    /**
     * Get the raw display field desc.
     */
    public int getDisplayFieldDesc()
    {
        return m_iDisplayFieldDesc;
    }
    /**
     * Do I create a separate control for the description for this type of control.
     * Generally yes, but you may want to override in checkboxes or buttons to include the description in the control.
     * <br/>Note: THis should probably be in the view, but so far it works fine here in the model.
     * @return True if the field desc is separate.
     */
    public boolean getSeparateFieldDesc()
    {
        if (this.getScreenFieldView() != null)
            return this.getScreenFieldView().getSeparateFieldDesc();
        return true;
    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iUseSameWindow If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean handleCommand(String strCommand, ScreenField sourceSField, int iUseSameWindow)
    {
        boolean bHandled = false;
        if (!(this instanceof BasePanel))   // BasePanel already called doCommand.
            bHandled = this.doCommand(strCommand, sourceSField, iUseSameWindow);   // Do I handle it?

        if (bHandled == false)
            if (this.getParentScreen() != null)
                if (this.getParentScreen() != sourceSField)
                    return this.getParentScreen().handleCommand(strCommand, this, iUseSameWindow);  // Send it up to see if the parent wants to handle it
        return bHandled;
    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        if (this.getScreenFieldView() != null)
        {
            if (MenuConstants.SELECT.equalsIgnoreCase(strCommand))
                if ((iCommandOptions & ScreenConstants.USE_NEW_WINDOW) == ScreenConstants.USE_NEW_WINDOW)
                    strCommand = MenuConstants.SELECT_DONT_CLOSE;   // Special - Click select w/shift = don't close
            return this.getScreenFieldView().doCommand(strCommand);    // Give my view the first shot.
        }
        return false; // Not processed, BasePanels and above will override
    }
    /**
     * This field changed, if this is the main record, lock it!
     * @param field The field that changed.
     */
    public void fieldChanged(Field field)
    {
        BasePanel screenParent = this.getParentScreen();
        if (field == null) if (m_converterField != null) 
            field = m_converterField.getField();   // This field changed
        if (screenParent != null)
            screenParent.fieldChanged(field);
    }
    /**
     * Get first parent up from here (or this if BasePanel!).
     * @return The parent screen.
     */
    public BasePanel getBasePanel()
    {
        return m_screenParent;
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Convert getConverter()
    {
        return m_converterField;
    }
    /**
     * Is this field Enabled or disabled?
     * @return true if enabled.
     */
    public boolean isEnabled()
    {
        return m_bEnabled;
    }
    /**
     * File to use for menu action.
     * @return The main record (override to do something).
     */
    public Rec getMainRecord()
    {
        return null;
    }
    /**
     * Get this control's parent.
     * @return The parent screen.
     */
    public BasePanel getParentScreen()
    {
        return m_screenParent;
    }
    /**
     * Get the top level screen.
     * @return The top level screen.
     */
    public BasePanel getRootScreen()
    { // The root window is the ChildScreen
        if (m_screenParent != null)
            return m_screenParent.getRootScreen();
        else
            return null;    // No parent
    }
    /**
     * Get the location constant.
     * @return The location constant.
     */
    public ScreenLocation getScreenLocation()
    {
        return m_itsLocation;
    }
    /**
     * Can this control be a focus target.
     * @return true if this can accept the focus.
     */
    public boolean isFocusTarget()
    {
        return m_bIsFocusTarget;
    }
        /**
         * Returns the Component that should receive the focus after aComponent.
         */
    public ScreenField getComponentAfter(ScreenField sfCurrent, int iSelectField)
    {
        return this.getParentScreen().getComponentAfter(sfCurrent, iSelectField);
    }
    /**
     * Select the first/next/prev/last input field.
     * @sfCurrent The currently selected screen field.
     * @iPrevious Which field should I select (next/prev/first/last).
     */
    public void selectField(ScreenField sfCurrent, int iSelectField)
    {
        this.getParentScreen().selectField(sfCurrent, iSelectField);
    }
    /**
     * Set the converter for this screen field.
     * @param fieldConverter The converter for this screen field.
     */
    public void setConverter(Convert fieldConverter)
    {
        m_converterField = fieldConverter;
    }
    /**
     * Enable or disable this control.
     * @param bEnable If true, enable this field.
     */
    public void setEnabled(boolean bEnable)
    {
        m_bEnabled = bEnable;
        if (this.getScreenFieldView() != null)
            this.getScreenFieldView().setEnabled(bEnable);
    }
    /**
     * Request focus?
     * @param bIsFocusTarget If true this is a focus target.
     */
    public void setRequestFocusEnabled(boolean bIsFocusTarget)
    {
        m_bIsFocusTarget = bIsFocusTarget;
        this.getScreenFieldView().setRequestFocusEnabled(bIsFocusTarget);
    }
    /**
     * Find the sub-screen that uses this grid query and set for selection.
     * When you select a new record here, you read the same record in the SelectQuery.
     * (Override to do something).
     * @param selectTable The record which is synced on record change.
     * @param bUpdateOnSelect Do I update the current record if a selection occurs.
     * @return True if successful.
     */
    public boolean setSelectQuery(Rec selectTable, boolean bUpdateOnSelect)
    {
        return false;
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl If false, set up a read-only control.
     * @return The new view.
     */
    public ScreenFieldView setupScreenFieldView(boolean bEditableControl)
    {
        return this.getViewFactory().setupScreenFieldView(this, null, bEditableControl);
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public ViewFactory getViewFactory()
    {
        if (this.getParentScreen() != null)
            return this.getParentScreen().getViewFactory();
        return null;
    }
    /**
     * Create the description for this control.
     */   
    public void setupControlDesc()
    {
        if (m_screenParent == null)
            return;
        String strDisplay = m_converterField.getFieldDesc();
        if ((strDisplay != null) && (strDisplay.length() > 0))
        {
            ScreenLocation descLocation = m_screenParent.getNextLocation(ScreenConstants.FIELD_DESC, ScreenConstants.DONT_SET_ANCHOR);
            new SStaticString(descLocation, m_screenParent, strDisplay);
        }
    }
    /**
     * Give this control the input focus.
     */
    public void requestFocus()
    {
        this.getScreenFieldView().requestFocus();
    }
    /**
     * Validate the current field, update the current grid record.
     */
    public void finalizeThisScreen()
    {
        this.getScreenFieldView().finalizeThisScreen();
    }
    /**
     * Get the HTML param for this screen field.
     * (Only for XML/HTML fields).
     * Usually just the file.field name.
     * This method is typically used by the HTML model, but is also needed
     * By the swing report classes (thats why its here rather than in the html view).
     * @return The HTML param name.
     */
    public final String getSFieldParam()
    {
        return this.getSFieldParam(null, true);
    }
    /**
     * Get the HTML param for this screen field.
     * (Only for XML/HTML fields).
     * Usually just the file.field name.
     * This method is typically used by the HTML model, but is also needed
     * By the swing report classes (thats why its here rather than in the html view).
     * @return The HTML param name.
     */
    public final String getSFieldParam(String strSuffix)
    {
        return this.getSFieldParam(strSuffix, true);
    }
    /**
     * Get the HTML param for this screen field.
     * (Only for XML/HTML fields).
     * Usually just the file.field name.
     * This method is typically used by the HTML model, but is also needed
     * By the swing report classes (thats why its here rather than in the html view).
     * @return The HTML param name.
     */
    public String getSFieldParam(String strSuffix, boolean bEncodeParam)
    {
        String strFieldName = null;
        if (this.getConverter() != null)
        {
            String strFieldDesc = this.getConverter().getFieldDesc();
            BaseField field = (BaseField)this.getConverter().getField();
            if (field != null)
            {
                if ((field.getListener("org.jbundle.base.field.eventMainReadOnlyHandler") == null)   // Special case - If this is reading a secondary file there is a huge chance of name collision
                	|| (field.getComponent(0) == this))	// Only allow the first one to use this name
                {
                    strFieldName = field.getFieldName(false, true);	// Recordname.fieldname
                    int iCount = 0;
                    for (int i = 0; ; i++)
                    {   // From second component on, add the component count
                        if (field.getComponent(i) == null)
                            break;  // End of list (Almost all cases stop here on first try)
                        if (!((ScreenField)field.getComponent(i)).isInputField())
                            continue;   // Only need params for input fields (not buttons, etc)
                        if (((ScreenField)field.getComponent(i)).getParentScreen() != this.getParentScreen())
                            continue;   // Not on this screen
                        iCount++;
                        if (iCount > 1)
                            if (field.getComponent(i) == this)
                                strFieldName += Integer.toString(iCount);
                    }
                }
            }
            else
                strFieldName = Params.FIELD + strFieldDesc;
        }
        if (strFieldName == null)
        {
            int i = 0;
            for (; i < this.getParentScreen().getSFieldCount(); i++)
            {
                if (this.getParentScreen().getSField(i) == this)
                    break;
            }
            strFieldName = "ScreenField" + Integer.toString(i); 
        }
        if (strSuffix != null) if (strFieldName != null)
            strFieldName = strFieldName + strSuffix;    // Add param suffix
        if (bEncodeParam) if (strFieldName != null)
        {
        	for (int i = 0; i < strFieldName.length(); i++)
        	{
        		char ch = strFieldName.charAt(i);
        		char chx = ch;
        		if (i == 0) if (!Character.isLetter(ch))
        			chx = '_';
        		if (!Character.isLetterOrDigit(ch))
        			if (ch != '_')
            			if (ch != '-')
                			if (ch != '.')
                				chx = '_';
        		if (ch != chx)
        			strFieldName = strFieldName.substring(0, i) + chx + strFieldName.substring(i + 1);
        	}
            //xtry {
            //x    strFieldName = URLEncoder.encode(strFieldName, DBConstants.URL_ENCODING);	// Just being safe
            //x} catch (java.io.UnsupportedEncodingException ex) {
            //x    ex.printStackTrace();
            //x}
        }
        return strFieldName;
    }
    /**
     * Retrieve (in HTML format) from this field.
     * (Only for XML/HTML fields).
     * @param bDisplayFormat Display (with html codes) or Input format?
     * @param bRawData If true return the Raw data (not through the converters)?
     * @return The HTML string.
     */
    public String getSFieldValue(boolean bDisplayFormat, boolean bRawData)
    {
        Convert converter = this.getConverter();
        if (converter == null)
            return Constant.BLANK;
        if (bRawData)
        {
            converter = converter.getField();
            if (converter == null)
                return Constant.BLANK;
        }
        return converter.getString();
    }
    /**
     * Set this control's value as it was submitted by the HTML post operation.
     * @return The value the field was set to.
     */
    public String getSFieldProperty(String strFieldName)
    {
        return this.getParentScreen().getProperty(strFieldName);
    }
    /**
     * Set this control's converter to this HTML param.
     * @param strParamValue The value to set the field to.
     * @return The error code.
     */
    public int setSFieldValue(String strParamValue, boolean bDisplayOption, int iMoveMode)
    {
        if (this.getConverter() != null)
            return this.getConverter().setString(strParamValue, bDisplayOption, iMoveMode);
        else
            return Constant.NORMAL_RETURN;
    }
    /**
     * Move the HTML input to the screen record fields.
     * @param strSuffix Only move fields with the suffix.
     * @return true if one was moved.
     * @exception DBException File exception.
     */
    public int setSFieldToProperty(String strSuffix, boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = Constant.NORMAL_RETURN;
        if (this.isInputField())
        {
            String strFieldName = this.getSFieldParam(strSuffix);
            String strParamValue = this.getSFieldProperty(strFieldName);

            if (strParamValue != null)
                iErrorCode = this.setSFieldValue(strParamValue, bDisplayOption, iMoveMode);
        }
        return iErrorCode;
    }
    /**
     * Is this a valid Html Input field?
     * @return true if this is an HTML Input field.
     */
    public boolean isInputField()
    {
        return true;    //  By default screenfields are Html Input fields (this.getConverter() != null);
    }
    /**
     * Output this complete report to the output file.
     * Display the headers, etc. then:
     * <ol>
     * - Parse any parameters passed in and set the field values.
     * - Process any command (such as move=Next).
     * - Render this screen using the view (by calling printxxmlScreen()).
     * </ol>
     * @param out The output stream.
     * @exception DBException File exception.
     */
    public void printReport(PrintWriter out) throws DBException
    {
        this.getScreenFieldView().printReport(out, null);
    }
    /**
     * Process all the submitted params.
     * @param out The Html output stream.
     * @exception DBException File exception.
     */
    public void processInputData(PrintWriter out) throws DBException
    {
        this.getScreenFieldView().processInputData(out);            
    }
    /**
     * Display the result in html table format.
     * @param out The http output stream.
     * @param reg The resource bundle
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        this.getScreenFieldView().printScreen(out, reg);    
    }
    /**
     * Move the HTML input to the screen record fields.
     * @param strSuffix value to add to the end of the field name before retrieving the param.
     * @exception DBException File exception.
     * @return bParamsFound True if params were found and moved.
     */
    public ScreenModel doServletCommand(ScreenModel parentScreen)
    {
        return null;    // This should never be called for a screenField, see BaseScree.moveControlInput.
    }
    /**
     * Get the print options (view defined).
     * @return The HTML options.
     * @exception DBException File exception.
     */
    public int getPrintOptions()
        throws DBException
    {
        return this.getScreenFieldView().getPrintOptions();
    }
    /**
     * Display this control in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions)
    {
        return this.getScreenFieldView().printControl(out, iPrintOptions);
    }
    /**
     * Display this control's data in print (view) format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        if (!this.isInputField())
            return false;
        if (this.isToolbar())
            return false;
        return this.getScreenFieldView().printData(out, iPrintOptions);
    }
    /**
     * Display this sub-control in html input format?
     * @param iPrintOptions The view specific print options.
     * @return True if this sub-control is printable.
     */
    public boolean isPrintableControl(ScreenField sField, int iPrintOptions)
    {
        if ((sField == null) || (sField == this))
            return this.getScreenFieldView().isPrintableControl(iPrintOptions);
        return sField.isPrintableControl(null, iPrintOptions);
    }
    /**
     * Is this an Html toolbar?
     */
    public boolean isToolbar()
    {
        return false;
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartForm(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printControlStartForm(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndForm(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printControlEndForm(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartField(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printControlStartField(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndField(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printControlEndField(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printDataStartForm(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printDataEndForm(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartField(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printDataStartField(out, iPrintOptions);
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndField(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printDataEndField(out, iPrintOptions);
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public int printStartGridScreenData(PrintWriter out, int iPrintOptions)
    {
        return this.getScreenFieldView().printStartGridScreenData(out, iPrintOptions);
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndGridScreenData(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printEndGridScreenData(out, iPrintOptions);
    }
    /**
     * Display the start grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordGridData(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printStartRecordGridData(out, iPrintOptions);
    }
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordGridData(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printEndRecordGridData(out, iPrintOptions);
    }
    /**
     * Display the end grid in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printNavButtonControls(PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printNavButtonControls(out, iPrintOptions);
    }
    /**
     * Display the start record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printStartRecordData(Rec record, PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printStartRecordData(record, out, iPrintOptions);
    }
    /**
     * Display the end record in input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public void printEndRecordData(Rec record, PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printEndRecordData(record, out, iPrintOptions);
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
        return this.getScreenFieldView().printHeadingFootingControls(out, iPrintOptions);
    }
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void printScreenFieldData(ScreenField sField, PrintWriter out, int iPrintOptions)
    {
        this.getScreenFieldView().printScreenFieldData(sField, out, iPrintOptions);
    }
    /**
     * Get the rich text type for this control.
     * @return The type (Such as HTML, Date, etc... very close to HTML control names).
     */
    public String getInputType(String strViewType)
    {
        if (strViewType == null)
            strViewType = this.getParentScreen().getViewFactory().getViewSubpackage();
        BaseField field = null;
        if (this.getConverter() != null)
            field = (BaseField)this.getConverter().getField();
        String strFieldType = "textbox";   // Html textbox
        if (field != null)
            strFieldType = field.getInputType(strViewType);
        else
        {
            if (this instanceof SPasswordField)
            {
                if (ScreenModel.HTML_TYPE.equalsIgnoreCase(strViewType))
                    strFieldType = "password";
                else //if (TopScreen.XML_TYPE.equalsIgnoreCase(strViewType))
                    strFieldType = "secret";               
            }
            else if (this instanceof SNumberText)
            {
                if (ScreenModel.HTML_TYPE.equalsIgnoreCase(strViewType))
                    strFieldType = "float";
            }
        }
        return strFieldType;
    }
}
