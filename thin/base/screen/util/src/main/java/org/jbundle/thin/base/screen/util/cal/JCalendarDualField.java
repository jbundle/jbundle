/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util.cal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jbundle.model.Freeable;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.FieldComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Constant;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBasePanel;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.util.jcalendarbutton.JCalendarPopup;
import org.jbundle.util.jcalendarbutton.JTimePopup;



/** 
 * A Calendar dual field displays the calendar text and the calendar button popup and
 * automatically synchronizes the fields.
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JCalendarDualField extends JPanel
    implements ActionListener, FieldComponent,
        PropertyChangeListener, Freeable
{
	private static final long serialVersionUID = 1L;

	/**
     * The field this component is tied to.
     */
    protected Convert m_converter = null;
    /**
     * The text field part of this component.
     */
    protected JTextField m_tf = null;
    /**
     * The button part of this component.
     */
    protected JButton m_button = null;
    /**
     * The button part of this component.
     */
    protected JButton m_buttonTime = null;
    
    /**
     * Creates new JCalendarDualField.
     */
    public JCalendarDualField()
    {
        super();
    }
    /**
     * Creates new JCalendarDualField.
     * @param The field this component is tied to.
     */
    public JCalendarDualField(Convert converter)
    {
        this();
        this.init(converter, true, false);
    }
    /**
     * Creates new JCalendarDualField.
     * @param The field this component is tied to.
     */
    public JCalendarDualField(Convert converter, boolean bAddCalendarButton, boolean bAddTimeButton)
    {
        this();
        this.init(converter, bAddCalendarButton, bAddTimeButton);
    }
    /**
     * Creates new JCalendarDualField.
     * @param The field this component is tied to.
     */
    public void init(Convert converter, boolean bAddCalendarButton, boolean bAddTimeButton)
    {
        m_converter = converter;
        this.setBorder(null);
        this.setOpaque(false);
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        String strDefault = null;
        int iColumns = 15;
        if (m_converter != null)
        {
            strDefault = m_converter.getString();
            iColumns = m_converter.getMaxLength();
        }
        m_tf = new JTextField(strDefault, iColumns);
        this.add(m_tf);
        BaseApplet applet = BaseApplet.getSharedInstance();
        if (bAddCalendarButton)
        {
            m_button = new JButton(applet.loadImageIcon(JCalendarPopup.CALENDAR_ICON));
            m_button.setMargin(JCalendarPopup.NO_INSETS);
            m_button.setOpaque(false);
            this.add(m_button);
            m_button.addActionListener(this);
        }
        
        if (bAddTimeButton)
        {
            m_buttonTime = new JButton(applet.loadImageIcon(JTimePopup.TIME_ICON));
            m_buttonTime.setMargin(JCalendarPopup.NO_INSETS);
            m_buttonTime.setOpaque(false);            
            this.add(m_buttonTime);
            m_buttonTime.addActionListener(this);
        }
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        m_converter = null;
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw-date value of this component.
     */
    public Object getControlValue()
    {
        int iErrorCode = m_converter.setString(m_tf.getText());
        if (iErrorCode != 0)
            if (this.getParent() instanceof JBaseScreen)
                ((JBaseScreen)this.getParent()).getBaseApplet().setStatusText(((JBaseScreen)this.getParent()).getBaseApplet().getLastError(iErrorCode));
        m_tf.setText(m_converter.toString());   // Redisplay formatted text.
        return m_converter.getData();
    }
    /**
     * Set the value.
     * @param objValue The raw-date value of this component.
     */
    public void setControlValue(Object objValue)
    {
        boolean bDisplayData = (m_converter.getData() != objValue); // Displaying the same value will cause an endless loop

        m_converter.setData(objValue, bDisplayData, Constants.READ_MOVE);    // DO NOT! Display (will loop)
        String strDate = m_converter.toString();    // Get it in string format.
        m_converter.setString(strDate, false, Constants.READ_MOVE); // This validates the date
        m_tf.setText(m_converter.toString());
    }
    /**
     * Handle action listener (button press).
     * If they press the button, display the calendar popup.
     * @param e The actionevent.
     */
    public void actionPerformed(ActionEvent e)
    {
        if ((m_button != null) && (e.getSource() == m_button))
        {
            JCalendarPopup popup = JCalendarPopup.createCalendarPopup((Date)this.getControlValue(), m_button);
            popup.addPropertyChangeListener(this);
        }
        else if ((m_buttonTime != null) && (e.getSource() == m_buttonTime))
        {
            JTimePopup popup = JTimePopup.createTimePopup((Date)this.getControlValue(), m_buttonTime);
            popup.addPropertyChangeListener(this);
        }
    }
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (JCalendarPopup.DATE_PARAM.equalsIgnoreCase(evt.getPropertyName()))
            if (evt.getNewValue() instanceof java.util.Date)
        {
            this.setControlValue(evt.getNewValue());
        }
    }
    /**
     * When a focus listener is added to this, actually add it to the components.
     * @param l The listener to add.
     */
    public void addFocusListener(FocusListener l)
    {
        super.addFocusListener(l);
        if (l instanceof JBasePanel)
        {
            m_tf.addFocusListener(l);
            if (m_button != null)
                m_button.addFocusListener(l);
            if (m_buttonTime != null)
                m_buttonTime.addFocusListener(l);
        }
    }
    /**
     * When a focus listener is removed from this, actually add it to the components.
     * @param l The listener to add.
     */
    public void removeFocusListener(FocusListener l)
    {
        super.removeFocusListener(l);
        if (l instanceof JBasePanel)
        {
            m_tf.removeFocusListener(l);
            if (m_button != null)
                m_button.removeFocusListener(l);
            if (m_buttonTime != null)
                m_buttonTime.removeFocusListener(l);
        }
    }
    /**
     * Set this component's name.
     * Make sure the text component has the same name.
     * @param name The name to set.
     */
    public void setName(String name)
    {
        super.setName(name);
        m_tf.setName(name);
        if (m_button != null)
            m_button.setName(name);
        if (m_buttonTime != null)
            m_buttonTime.setName(name);
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
        this.init(converter, true, true);
    }
}
