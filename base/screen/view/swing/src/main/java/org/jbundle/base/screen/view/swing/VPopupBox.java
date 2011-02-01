package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.convert.QueryConverter;
import org.jbundle.base.screen.control.swing.util.ScreenInfo;
import org.jbundle.base.screen.model.SPopupBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * A screen popup box.
 */
public class VPopupBox extends VScreenField
    implements ItemListener
{
    /**
     * From the survey of max length.
     */
    protected int m_iMaxLength = 0;
    
    /**
     * Constructor.
     */
    public VPopupBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VPopupBox(ScreenField model, boolean bEditableControl)
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
        m_iMaxLength = -1;
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        if (this.getControl(DBConstants.CONTROL_TO_FREE) != null)
            if (m_bEditableControl)
        {
            this.getControl(DBConstants.CONTROL_TO_FREE).removeFocusListener(this);
            ((JComboBox)this.getControl(DBConstants.CONTROL_TO_FREE)).removeItemListener(this);
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Create the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        JComboBox control = new JComboBox();
        Component controlOld = m_control;
        m_control = control;
        this.reSelectRecords();
        m_control = controlOld;
        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addKeyListener(this);
            control.addItemListener(this);
        }
        return control;
    }
    /**
     * Get the background color for this control.
     * @return The background color, or null if there is none.
     */
    public Color getControlBackgroundColor()
    {
        if (this.getScreenInfo() != null)
            if (!this.getScreenInfo().isCustomTheme()) // If there is a custom theme, the theme will set the fonts
                return this.getScreenInfo().getControlColor();
        return null;
    }
    /**
     * Requery the recordset.
     */
    public void reSelectRecords()
    {
        JComboBox control = (JComboBox)this.getControl();
        control.removeAllItems();
        Converter converter = this.getScreenField().getConverter();

        if (converter instanceof QueryConverter)
        {
            Record record = ((QueryConverter)converter).getTargetRecord();
            record.close();
            try {
				record.open();    // Make sure any listeners are called before disabling.
			} catch (DBException e) {
				e.printStackTrace();
			}
        }
        String thisString = null;
        Object objDefaultOption = null;
        Object data = null;
        if (converter != null)
            data = converter.getData();
        Object[] rgobjEnabled = this.enableBehaviors(null);
        for (int index = 0; index < SPopupBox.MAX_POPUP_ITEMS; index++)    // Must process first time (max 250 entries)
        {
            thisString = converter.convertIndexToDisStr(index);
            if ((thisString == null) || (thisString.length() == 0))
            {
                if (index > 0)
                    break;      // All Strings added
                thisString = " ";
            }
            else if (index == 0)
                if (converter != null) // If there is no blank option, the field should default to the first option
                    if (converter.getField() != null)
                        objDefaultOption = converter.getField().getData();
            control.addItem(thisString);
            m_iMaxLength = Math.max(m_iMaxLength, thisString.length()); // Survey length.
        }
        this.enableBehaviors(rgobjEnabled);
        if (converter != null)
            converter.setData(data);
        if (objDefaultOption != null)
            if (this.getScreenField().getConverter().getField().getDefault() == null)
                this.getScreenField().getConverter().getField().setDefault(objDefaultOption); // Default to first item
    }
    /**
     * Calculate the box size.
     * @param ptLocation The location of the control in the parent.
     * @return The control's bounds.
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {
        Converter converter = this.getScreenField().getConverter();
        if (m_iMaxLength == -1)
        {
            Object data = converter.getData();
            Object[] rgobjEnabled = this.enableBehaviors(null);
            for (int index = 0; index < 10; index++)
            {
                String tempString = converter.convertIndexToDisStr(index);
                if (index > 0) if ((tempString == null) || (tempString.length() == 0))
                    break;      // Far Enough
                m_iMaxLength = Math.max(m_iMaxLength, tempString.length());
            }
            this.enableBehaviors(rgobjEnabled);
            converter.setData(data);
        }
        if (m_iMaxLength == 0)
        {   // Empty, create a default size box
            if (converter != null)
                m_iMaxLength = converter.getMaxLength();
            if (m_iMaxLength == 0)
                m_iMaxLength = 15;
        }
        Dimension itsSize = this.getTextBoxSize(m_iMaxLength, ScreenConstants.POPUP_DESC, 1);
        itsSize.width += ScreenInfo.kExtraColSpacing;
        return new Rectangle(ptLocation.x , ptLocation.y, itsSize.width, itsSize.height);
    }
    /**
     * Temporarily enable or disable all the behaviors of the linked record.
     * @param rgbEnabled Array of enabled listeners to enable (if null, disable all).
     * @return Is disabling, return an array of disabled listeners.
     */
    public Object[] enableBehaviors(Object[] mxobjEnabled)
    {
        boolean bEnable = (mxobjEnabled == null) ? false : true;

        BaseField field = null;
        Converter converter = this.getScreenField().getConverter();
        if (converter != null)
            field = (BaseField)converter.getField();
        Record record = null;
        if (converter instanceof QueryConverter)
            record = ((QueryConverter)converter).getTargetRecord();
        if (record == null)
            return null;
        
        mxobjEnabled = record.setEnableNonFilter(mxobjEnabled, false, false, false, false, true);  // Disable/enable file and field behaviors
        
        if (!bEnable)
        {   // Disable
            mxobjEnabled = Utility.growArray(mxobjEnabled, mxobjEnabled.length + 2, 0);   // I need two extra cells
            if (field != null)
                mxobjEnabled[mxobjEnabled.length - 2] = field.setEnableListeners(false);
            try {
                mxobjEnabled[mxobjEnabled.length - 1] = record.getHandle(DBConstants.DATA_SOURCE_HANDLE);
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        else
        {   // Enable
            if (field != null)
                field.setEnableListeners((boolean[])mxobjEnabled[mxobjEnabled.length - 2]);
            try {
                if (mxobjEnabled[mxobjEnabled.length - 1] != null)
                    record.setHandle(mxobjEnabled[mxobjEnabled.length - 1], DBConstants.DATA_SOURCE_HANDLE);
                mxobjEnabled[mxobjEnabled.length - 1] = null;
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
            
        }
        return mxobjEnabled;
    }
    /**
     * Lost focus. Popup boxes are updated on item events... don't u/d here.
     * @param evt The FocusEvent.
     */
    public void focusLost(FocusEvent evt)
    {
        int iErrorCode = this.getScreenField().controlToField();    // Code replaced with this
        if (iErrorCode != DBConstants.NORMAL_RETURN) if (this.getScreenField().getRootScreen() != null)
            this.getScreenField().getRootScreen().displayError(iErrorCode);
        else 
        {
            if (this.getScreenField().getConverter() != null) if (this.getScreenField().getConverter().getField() != null) if (((BaseField)this.getScreenField().getConverter().getField()).isJustModified())
                this.getScreenField().fieldChanged(null); // Lock record if just modified
        }
    }
    /**
     * Get the class of this component's state.
     * The state is an object which represents the state of this control.
     * @return The class of the control's value (Integer).
     */
    public Class<?> getStateClass()
    {
        return Integer.class; // By default
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * <p/>Note: control is always valid. No not touch the converter.
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control)
    {
        int iCurrentIndex = -1;
        if (control != null)
        {
            iCurrentIndex = ((JComboBox)control).getSelectedIndex();
            if (iCurrentIndex == -1)
                if (((JComboBox)control).isEditable())
                    return ((JComboBox)control).getSelectedItem();  // Special case - Combo Box (with input area).
        }
        return new Integer(iCurrentIndex);
    }
    /**
     * Set the component to this state. State is defined by the component.
     * <p/>Note: control is always valid. No not touch the converter.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        int iCurrentIndex = -1;
        if (objValue instanceof Integer)
            iCurrentIndex = ((Integer)objValue).intValue();
        if (iCurrentIndex != ((JComboBox)control).getSelectedIndex()) // Only for changes
        {
            ((JComboBox)control).removeItemListener(this);  // Don't respond when system say's I was changed
            try {
            	((JComboBox)control).setSelectedIndex(iCurrentIndex); // Not found, try to find this string
            } catch (IllegalArgumentException ex) {
            	ex.printStackTrace();//((JComboBox)control).setSelectedIndex(0);
            }
            ((JComboBox)control).addItemListener(this);   // Don't respond when system say's I was changed
        }
    }
    /**
     * Set the converter to this state. State is defined by the component.
     * @param objValue The value to set the field to (class of object depends on the control).
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return Error code.
     */
    public int setFieldState(Object objValue, boolean bDisplayOption, int iMoveMode)
    {
        if (this.getScreenField().getConverter() == null)
            return DBConstants.NORMAL_RETURN; // Being careful
        int currentItem = -1;
        if (objValue instanceof Integer)
        {
            currentItem = ((Integer)objValue).intValue();
            objValue = Integer.toString(currentItem);
        }
        if (currentItem != -1)
            return this.getScreenField().getConverter().convertIndexToField(currentItem, bDisplayOption, iMoveMode);    // Do need to display in case other fields are tied in!
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
        int thisItem = this.getScreenField().getConverter().convertFieldToIndex();
        if (thisItem == -1)
        {
            Component control = this.getControl();
            if (control == null)
                control = this.getRendererControl();
            if (control != null)
            {
                String tempString = this.getScreenField().getConverter().getString();
                if ((tempString == null) || (tempString.equals(Constants.BLANK)))
                    tempString = " "; // If there is a blank, this will select it!
                ((JComboBox)control).removeItemListener(this);  // Don't respond when system say's I was changed
                ((JComboBox)control).setSelectedItem(tempString); // Not found, try to find this string
                ((JComboBox)control).addItemListener(this);
                thisItem = ((JComboBox)control).getSelectedIndex();
                if (thisItem == 0) if (!tempString.equals(((JComboBox)control).getSelectedItem()))
                { // Still didn't find it! If this is the initial value, just set it to the first item to match the display
                BaseField field = (BaseField)this.getScreenField().getConverter().getField();
                    if (field != null)
                    {
                        Record record = field.getRecord();
                        if (record != null)
                            if (field.isNull())
                                if ((record.getEditMode() != DBConstants.EDIT_CURRENT)
                                    && (record.getEditMode() != DBConstants.EDIT_IN_PROGRESS)
                                    && ((record.getOpenMode() & DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY) == 0))
                                        if (!record.isModified())
                        { // Did you get all that... New and not modified yet.
                            String strTarget = (String)((JComboBox)control).getSelectedItem();
                            if ((strTarget != null) && (strTarget.length() > 0) && (strTarget.charAt(0) != ' '))
                            {
                                this.getScreenField().getConverter().convertIndexToField(0, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
                                field.setModified(false); // Don't write if this is the only change
                            }
                        }
                    }
                }
            }
        }
        return new Integer(thisItem);
    }
}
