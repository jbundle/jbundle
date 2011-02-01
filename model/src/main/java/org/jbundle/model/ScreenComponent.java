package org.jbundle.model;

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
public interface ScreenComponent
{
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Convert getConverter();
}
