/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.screen;

import java.util.Map;

import org.jbundle.model.Freeable;
import org.jbundle.model.db.Convert;

/**
 * FieldComponent.java
 *
 * Created on November 9, 2000, 2:31 AM
 */

/** 
 * ScreenComponent is a simple interface that allows a screen control
 * to get it's connection to the field information.
 * @author  Administrator
 * @version 1.0.0
 */
public interface ScreenComponent extends Freeable
{
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties Extra properties
     */
    public void init(ScreenLoc itsLocation, ComponentParent parentScreen, Convert fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties);
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Convert getConverter();
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public void setConverter(Convert converter);
    /**
     * Enable or disable this control.
     * @param bEnable If true, enable this field.
     */
    public void setEnabled(boolean enabled);
    /**
     * Get the top level screen.
     * @return The top level screen.
     */
    public ComponentParent getParentScreen();
    /**
     * Move the control's value to the field.
     * @return An error value.
     */
    public int controlToField();
    /**
     * Move the field's value to the control.
     */
    public void fieldToControl();
}
