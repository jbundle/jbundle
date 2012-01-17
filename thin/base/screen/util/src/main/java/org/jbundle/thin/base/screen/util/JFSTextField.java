/*
 * JFSTextField.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import javax.swing.JTextField;

import org.jbundle.model.screen.FieldComponent;
import org.jbundle.model.db.Convert;
import org.jbundle.thin.base.db.Converter;


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
    protected Converter m_converter = null;   

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
    public JFSTextField(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Creates new JCellButton.
     * @param iMaxLength The number of columns of text in this field.
     * @param bAlignRight If true, align the text to the right.
     */
    public void init(Converter converter)
    {
        m_converter = converter;
        if (converter != null)
            this.setColumns(converter.getMaxLength());
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
    }
}
