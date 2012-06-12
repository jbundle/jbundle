/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.opt;

/**
 * @(#)SImageView.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 jbundle.org. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SButtonBox;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.screen.ExtendedComponent;
import org.jbundle.thin.base.db.Converter;

/**
 * Image view with multiple image display.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SBlinkImageView extends SButtonBox
    implements ExtendedComponent
{

    /**
     * Constructor.
     */
    public SBlinkImageView()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public SBlinkImageView(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        for (int iIndex = 0; iIndex < 32; iIndex++)
        {
            Object icon = this.getIcon(iIndex);
            if (icon == null)
                break;
            this.addIcon(icon, iIndex);
        }
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the icon for this index (Override this method).
     * @param iIndex The icon's index.
     * @return The icon at this index (or null).
     */
    public Object getIcon(int iIndex)
    {
        return null;    // Override this!
    }
    /**
     * Maximum icons.
     */
    private static final int MAX_ICONS = 16;
    /**
     * All the icons.
     */
    protected Object m_rgIcons[] = new Object[MAX_ICONS];
    /**
     * Get the icon at this location.
     * @param value
     * @return
     */
    public Object getImageIcon(Object value)
    {
        int index = 0;
        if (value instanceof Integer)
            index = ((Integer)value).intValue();
        else if (value != null)
        {
            try {
                index = Integer.parseInt(value.toString());
            } catch (NumberFormatException ex) {
            }
        }
        return this.getIcon(index);
    }
    /**
     * Add an Icon to the Icon list.
     * @param iIndex The icon's index.
     * @param icon The icon at this index.
     */
    public void addIcon(Object icon, int iIndex)
    {
        m_rgIcons[iIndex] = icon;
        if (this.getScreenFieldView().getControl() instanceof ExtendedComponent)	// Always
            ((ExtendedComponent)this.getScreenFieldView().getControl()).addIcon(icon, iIndex);
    }
}
