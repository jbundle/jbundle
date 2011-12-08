/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jbundle.model.db.FieldComponent;
import org.jbundle.thin.base.db.Converter;


/** 
 * A bit mask field displays a row of check boxes that set bit 0, 1, etc in the field.
 * JBitMaskField also works in a JTable.
 * Bit 0 is the L.O. bit.
 * Add this code to make it work:
 * <pre>
    JCellButton button = new JCellButton(icon);
    newColumn.setCellEditor(button);
    button.addActionListener(this);
    button = new JCellButton(icon);
    newColumn.setCellRenderer(button);
 * </pre>
 * @author  Don Corley
 * @version 1.0.0
 */
public class JBitMaskField extends JPanel
    implements FieldComponent
{
	private static final long serialVersionUID = 1L;

	/**
     * An on bit is a checked checkbox.
     */
    protected boolean m_bCheckedIsOn = true;
    

    /**
     * Creates new JCellButton.
     */
    public JBitMaskField()
    {
        super();
    }
    /**
     * Creates new JCellButton.
     * @param iCheckCount The number of checkboxes to add.
     */
    public JBitMaskField(int iCheckCount)
    {
        this();
        this.init(iCheckCount, true);
    }
    /**
     * Creates new JCellButton.
     * @param iCheckCount The number of checkboxes to add.
     * @param bCheckedIsOn A 1 bit is a checked checkbox.
     */
    public JBitMaskField(int iCheckCount, boolean bCheckedIsOn)
    {
        this();
        this.init(iCheckCount, bCheckedIsOn);
    }
    /**
     * Creates new JCellButton.
     * @param iCheckCount The number of checkboxes to add.
     * @param bCheckedIsOn A 1 bit is a checked checkbox.
     */
    public void init(int iCheckCount, boolean bCheckedIsOn)
    {
        m_bCheckedIsOn = bCheckedIsOn;

        this.setBorder(null);
        this.setOpaque(false);
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        for (int i = 0; i < iCheckCount; i++)
        {
            JCheckBox checkBox = new JCheckBox();
            this.add(checkBox);
            checkBox.setName(Integer.toString(i));
            checkBox.setOpaque(false);
        }
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw data (a Short object).
     */
    public Object getControlValue()
    {
        short iFieldValue = 0;
        for (int iBitNumber = 0; iBitNumber < this.getComponentCount(); iBitNumber++)
        {
            JCheckBox checkBox = (JCheckBox)this.getComponent(iBitNumber);
            boolean bState = checkBox.isSelected();
            if (!m_bCheckedIsOn)
                bState = !bState; // Do opposite operation
            if (bState)
                iFieldValue |= (1 << iBitNumber);   // Set the bit
            else
                iFieldValue &= ~(1 << iBitNumber);  // Clear the bit
        }
        return new Short(iFieldValue);
    }
    /**
     * Set the value.
     * @param objValue The raw data (a Short object).
     */
    public void setControlValue(Object objValue)
    {
        short iFieldValue = 0;
        if (objValue instanceof Short)  // Always
            iFieldValue = ((Short)objValue).shortValue();
        for (int iBitNumber = 0; iBitNumber < this.getComponentCount(); iBitNumber++)
        {
            JCheckBox checkBox = (JCheckBox)this.getComponent(iBitNumber);
            boolean bState = ((iFieldValue & (1 << iBitNumber)) != 0);
            if (!m_bCheckedIsOn)
                bState = !bState; // Do opposite operation
            checkBox.setSelected(bState);
        }
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Converter getConverter()
    {
        return null;
    }
}
