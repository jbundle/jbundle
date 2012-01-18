/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import javax.swing.JCheckBox;

import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.FieldComponent;


/** 
 * A JThreeStateCheckBox works the same as a checkbox, but with a third (shaded)
 * state that translates to a null value.
 * JCellButton is a button that works in a JTable.
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JFSCheckBox extends JCheckBox
    implements FieldComponent
{
	private static final long serialVersionUID = 1L;

    /**
     * Creates new JThreeStateCheckBox.
     */
    public JFSCheckBox()
    {
        super();
    }
    /**
     * Creates new JThreeStateCheckBox.
     * @param text The checkbox description.
     */
    public JFSCheckBox(String text)
    {
        super(text);
        this.init(text);
    }
    /**
     * Creates new JThreeStateCheckBox.
     * @param text The checkbox description.
     */
    public void init(String text)
    {
        this.setBorder(null);
        this.setOpaque(false);
    }
    /**
     * Free this object's resources.
     */
    public void free()
    {
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw data (a Boolean).
     */
    public Object getControlValue()
    {
        return new Boolean(this.isSelected());
    }
    /**
     * Set the value.
     * @param objValue The raw data (a Boolean).
     */
    public void setControlValue(Object objValue)
    {
        boolean bSelected = false;
        if (objValue instanceof Boolean)
        {
            bSelected = ((Boolean)objValue).booleanValue();
        }
        else if (objValue instanceof String)
        {
            String strValue = objValue.toString();
            if (strValue.length() > 0)
            {
                if ((Character.toUpperCase(strValue.charAt(0)) == 'T')
                    || (Character.toUpperCase(strValue.charAt(0)) == 'Y'))
                        bSelected = true;
            }
        }
        this.setSelected(bSelected);
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Convert getConverter()
    {
        return null;
    }
    /**
     * Set the converter for this screen field.
     * @converter The converter for this screen field.
     */
    public void setConverter(Convert converter)
    {
    }
}
