/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import java.awt.Component;
import java.awt.event.FocusListener;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.jbundle.model.DBException;
import org.jbundle.model.Freeable;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.FieldComponent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Constant;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.JBasePanel;


/** 
 * A Calendar dual field displays the calendar text and the calendar button popup and
 * automatically synchronizes the fields.
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JMultiFieldPanel extends JPanel
    implements FieldComponent, Freeable
{
	private static final long serialVersionUID = 1L;

	/**
     * The field this component is tied to.
     */
    protected Convert m_converter = null;
    /**
     * 
     */
    protected boolean[] brgComponentsLinkedToConverter = new boolean[5];
    
    /**
     * Creates new JCalendarDualField.
     */
    public JMultiFieldPanel()
    {
        super();
    }
    /**
     * Creates new JCalendarDualField.
     * @param The field this component is tied to.
     */
    public JMultiFieldPanel(Convert converter)
    {
        this();
        this.init(converter, null, null);
    }
    /**
     * Creates new JCalendarDualField.
     * @param The field this component is tied to.
     */
    public JMultiFieldPanel(Convert converter, JComponent component1, JComponent component2)
    {
        this();
        this.init(converter, component1, component2);
    }
    /**
     * Creates new JCalendarDualField.
     * @param The field this component is tied to.
     */
    public void init(Convert converter, JComponent component1, JComponent component2)
    {
        m_converter = converter;
        this.setBorder(null);
        this.setOpaque(false);
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (component1 != null)
            this.addComponent(component1, true);
        
        if (component2 != null)
            this.addComponent(component2, true);
    }
    /**
     * Add this component to this panel.
     */
    public void addComponent(JComponent component)
    {
        this.addComponent(component, true);
    }
    /**
     * Add this component to this panel.
     */
    public void addComponent(JComponent component, boolean bLinkComponentToConverter)
    {
        this.add(Box.createHorizontalStrut(3));
        
        if (this.getComponentCount() < brgComponentsLinkedToConverter.length)
            brgComponentsLinkedToConverter[this.getComponentCount()] = bLinkComponentToConverter;

        this.add(component);
}
    /**
     * Free this converter.
     */
    public void free()
    {
        m_converter = null;
        for (int i = this.getComponentCount() - 1; i >= 0; i--)
        {
            JComponent component = (JComponent)this.getComponent(i);
            if (component instanceof Freeable)
                ((Freeable)component).free();
        }
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw-date value of this component.
     */
    public Object getControlValue()
    {
        int i = this.getComponentCount() - 1;
        JComponent component = (JComponent)this.getComponent(i);
        if (component instanceof FieldComponent)
            ((FieldComponent)component).setControlValue(m_converter.getData());
        else if (component instanceof JTextComponent)
            ((JTextComponent)component).setText(m_converter.toString());

        return m_converter.getData();
    }
    /**
     * Set the value.
     * @param objValue The raw-date value of this component.
     */
    public void setControlValue(Object objValue)
    {
        m_converter.setData(objValue, false, Constants.SCREEN_MOVE);    // DO NOT! Display (will loop)

        for (int i = this.getComponentCount() - 1; i >= 0; i--)
        {
            JComponent component = (JComponent)this.getComponent(i);
            if ((i >= brgComponentsLinkedToConverter.length) || (brgComponentsLinkedToConverter[i]))
                this.setControlValue(objValue, component);
        }
    }
    /**
     * Set this control to this value.
     * @param objValue The value to set the component to.
     * @param component The component to set to this value.
     */
    public void setControlValue(Object objValue, Component component)
    {
        Convert converter = null;
        if (component instanceof ScreenComponent)
            converter = ((ScreenComponent)component).getConverter();
        if (converter == null)
            converter = m_converter;

        if (component instanceof FieldComponent)
            ((FieldComponent)component).setControlValue(converter.getData());
        else if (component instanceof JTextComponent)
            ((JTextComponent)component).setText(converter.toString());
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
            for (int i = this.getComponentCount() - 1; i >= 0; i--)
            {
                JComponent component = (JComponent)this.getComponent(i);
                component.addFocusListener(l);
            }
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
            for (int i = this.getComponentCount() - 1; i >= 0; i--)
            {
                JComponent component = (JComponent)this.getComponent(i);
                component.removeFocusListener(l);
            }
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
        for (int i = this.getComponentCount() - 1; i >= 0; i--)
        {
            JComponent component = (JComponent)this.getComponent(i);
            component.setName(name);
        }
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
        this.init(converter, null, null);
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
