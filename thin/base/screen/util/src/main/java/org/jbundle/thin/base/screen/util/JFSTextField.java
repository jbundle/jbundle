/*
 * JFSTextField.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import java.util.Map;

import javax.swing.JTextField;

import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.FieldComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Constant;


/** 
 * JFSTextField is a text field that works in a thin Table.
 * This class is usually used for text field that have
 * non-default behavior, such as right justification;
 * </pre>
 *
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JFSTextField extends JTextField
    implements FieldComponent
{
	private static final long serialVersionUID = 1L;

	/**
     * The field this component is tied to.
     */
    protected Convert m_converter = null;   

    /**
     * Creates new JCellTextField.
     */
    public JFSTextField()
    {
        super();
    }
    /**
     * Creates new JCellTextField.
     * @param iMaxLength The number of columns of text in this field.
     * @param bAlignRight If true, align the text to the right.
     */
    public JFSTextField(Convert converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Creates new JCellButton.
     * @param iMaxLength The number of columns of text in this field.
     * @param bAlignRight If true, align the text to the right.
     */
    public void init(Convert converter)
    {
        m_converter = converter;
        if (converter != null)
            this.setColumns(converter.getMaxLength());
    }
    /**
     * Free this object's resources.
     */
    public void free()
    {
        m_converter = null;
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw data (a Boolean).
     */
    public Object getControlValue()
    {
        return this.getText();
    }
    /**
     * Set the value.
     * @param objValue The raw data (a Boolean).
     */
    public void setControlValue(Object objValue)
    {
        if (objValue != null)
            this.setText(objValue.toString());
        else
            this.setText(null);
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Convert getConverter()
    {
        return m_converter;
    }
    /**
     * Set the converter for this screen field.
     * @converter The converter for this screen field.
     */
    public void setConverter(Convert converter)
    {
        m_converter = converter;
    }
    /**
     * Enable or disable this control.
     * @param bEnable If true, enable this field.
     */
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);  // Nice, this component has this method already
    }
    /**
     * Get the top level screen.
     * @return The top level screen.
     */
    public ComponentParent getParentScreen()
    {
        return null;
    }
    /**
     * Move the control's value to the field.
     * @return An error value.
     */
    public int controlToField()
    {
        return Constant.NORMAL_RETURN;
    }
    /**
     * Move the field's value to the control.
     */
    public void fieldToControl()
    {
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLoc itsLocation, ComponentParent parentScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this.init(converter);
    }
    /**
     * Request focus?
     * @param bIsFocusTarget If true this is a focus target.
     */
    public void setRequestFocusEnabled(boolean bIsFocusTarget)
    {
    }
    /**
     * Get the physical component associated with this view.
     * @return The physical control.
     */
    @Override
    public Object getControl()
    {
        return this;
    }
    /**
     * Move the HTML input to the screen record fields.
     * @param strSuffix Only move fields with the suffix.
     * @return true if one was moved.
     * @exception DBException File exception.
     */
    public int setSFieldToProperty(String strSuffix, boolean bDisplayOption, int iMoveMode)
    {
        return Constant.NORMAL_RETURN;
    }
}
