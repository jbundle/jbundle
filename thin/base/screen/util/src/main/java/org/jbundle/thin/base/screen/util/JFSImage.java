/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.FieldComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Constant;


/** 
 * A JThreeStateCheckBox works the same as a checkbox, but with a third (shaded)
 * state that translates to a null value.
 * JCellButton is a button that works in a JTable.
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JFSImage extends JPanel
    implements FieldComponent
{
	private static final long serialVersionUID = 1L;

	/**
     * The current image value.
     */
    protected Object m_image = null;
    /**
     * The minimum size
     */
    public static Dimension IMAGE_MIN_SIZE = new Dimension(10, 10);

    /**
     * Creates new JThreeStateCheckBox.
     */
    public JFSImage()
    {
        super();
    }
    /**
     * Creates new JThreeStateCheckBox.
     * @param text The checkbox description.
     */
    public JFSImage(ImageIcon image)
    {
        this();
        this.init(image);
    }
    /**
     * Creates new JThreeStateCheckBox.
     * @param text The checkbox description.
     */
    public void init(ImageIcon image)
    {
//        this.setBorder(JScreen.m_borderLine);
        this.setOpaque(false);
        this.setControlValue(image);
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
        return m_image;
    }
    /**
     * Set the value.
     * @param objValue The raw data (a Boolean).
     */
    public void setControlValue(Object objValue)
    {
        m_image = objValue;
        if (m_image instanceof ImageIcon)
            this.setPreferredSize(new Dimension(((ImageIcon)m_image).getIconWidth(), ((ImageIcon)m_image).getIconHeight()));
        else
            this.setPreferredSize(IMAGE_MIN_SIZE);
        this.revalidate();
        this.repaint();
    }
    /**
     * Tile this image on the background, using this background color.
     * @param g The graphics object.
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);        // Just in case the image is bad/not loaded

        if (m_image instanceof ImageIcon)
            ((ImageIcon)m_image).paintIcon(this, g, 0, 0);
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
        this.init(null);
    }
}
