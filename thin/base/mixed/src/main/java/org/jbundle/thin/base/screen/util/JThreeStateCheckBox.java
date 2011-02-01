/**
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 */
 
package org.jbundle.thin.base.screen.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.jbundle.model.FieldComponent;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.BaseApplet;


/** 
 * A JThreeStateCheckBox works the same as a checkbox, but with a third (shaded)
 * state that translates to a null value.
 * JCellButton is a button that works in a JTable.
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JThreeStateCheckBox extends JButton
    implements ActionListener, FieldComponent
{
	private static final long serialVersionUID = 1L;

	public static final int OFF = 0;
    public static final int ON = 1;
    public static final int NULL = -1;
    
    public static final String CHECKBOX_OFF = "CheckOff";
    public static final String CHECKBOX_ON = "CheckOn";
    public static final String CHECKBOX_NULL = "CheckNull";

    protected static ImageIcon m_iconOff = null;
    protected static ImageIcon m_iconOn = null;
    protected static ImageIcon m_iconNull = null;

    protected int m_iCurrentState = OFF;

    /**
     * Creates new JThreeStateCheckBox.
     */
    public JThreeStateCheckBox()
    {
        super();
    }
    /**
     * Creates new JThreeStateCheckBox.
     * @param text The checkbox description.
     */
    public JThreeStateCheckBox(String text)
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
        m_iCurrentState = OFF;
        if (m_iconOff == null)
        {
            m_iconOff = BaseApplet.getSharedInstance().loadImageIcon(CHECKBOX_OFF);
            m_iconOn = BaseApplet.getSharedInstance().loadImageIcon(CHECKBOX_ON);
            m_iconNull = BaseApplet.getSharedInstance().loadImageIcon(CHECKBOX_NULL);
        }
        this.setIcon(m_iconOff);
        this.setBorder(null);
        this.setMargin(Constants.NO_INSETS);
        this.setOpaque(false);
        
        this.addActionListener(this);
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw data (a Boolean).
     */
    public Object getControlValue()
    {
        Object objValue = null;
        if (m_iCurrentState == ON)
            objValue = Boolean.TRUE;
        else if (m_iCurrentState == OFF)
            objValue = Boolean.FALSE;
        return objValue;
    }
    /**
     * Set the value.
     * @param objValue The raw data (a Boolean).
     */
    public void setControlValue(Object objValue)
    {
        if (objValue instanceof Boolean)
        {
            if (((Boolean)objValue).booleanValue())
                m_iCurrentState = ON;
            else
                m_iCurrentState = OFF;
        }
        else
            m_iCurrentState = NULL;
        this.updateImage();
    }
    /**
     * Update the image to the current state.
     */
    public void updateImage()
    {
        if (m_iCurrentState == ON)
            this.setIcon(m_iconOn);
        else if (m_iCurrentState == OFF)
            this.setIcon(m_iconOff);
        else
            this.setIcon(m_iconNull);
    }
    /**
     * Handle action listener (button press).
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == this)
            this.toggleState();
    }
    /**
     * Toggle the button state.
     */
    public void toggleState()
    {
        if (m_iCurrentState == ON)
            m_iCurrentState = NULL;
        else if (m_iCurrentState == OFF)
            m_iCurrentState = ON;
        else
            m_iCurrentState = OFF;
        this.updateImage();
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
