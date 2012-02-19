/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.FieldComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Constant;


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
     * Free this object's resources.
     */
    public void free()
    {
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
    @Override
    public Convert getConverter()
    {
        return null;
    }
    /**
     * Set the converter for this screen field.
     * @converter The converter for this screen field.
     */
    @Override
    public void setConverter(Convert converter)
    {
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
        this.init(1, true);
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
