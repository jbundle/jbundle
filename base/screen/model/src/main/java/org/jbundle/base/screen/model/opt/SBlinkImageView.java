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

import javax.swing.ImageIcon;

import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.util.JBlinkLabel;

/**
 * Image view with multiple image display.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SBlinkImageView extends ScreenField
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
     * @param properties TODO
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        for (int iIndex = 0; iIndex < 32; iIndex++)
        {
            ImageIcon icon = this.getIcon(iIndex);
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
    public ImageIcon getIcon(int iIndex)
    {
        return null;    // Override this!
    }
    /**
     * Get the icon at this location.
     * @param value
     * @return
     */
    public ImageIcon getImageIcon(Object value)
    {
        if (this.getScreenFieldView().getControl() != null)
            return ((JBlinkLabel)this.getScreenFieldView().getControl()).getImageIcon(value);
        return null;
    }
    /**
     * Add an Icon to the Icon list.
     * @param iIndex The icon's index.
     * @param icon The icon at this index.
     */
    public void addIcon(ImageIcon icon, int iIndex)
    {
        if (this.getScreenFieldView().getControl() != null)
            ((JBlinkLabel)this.getScreenFieldView().getControl()).addIcon(icon, iIndex);
    }
}
