package org.jbundle.model.db;


/**
 * FieldComponent.java
 *
 * Created on November 9, 2000, 2:31 AM
 */

/** 
 * FieldComponent is a simple interface that allows the fieldInfo to
 * set and get this component's state.
 * @author  Administrator
 * @version 1.0.0
 */
public interface FieldComponent extends ScreenComponent
{
    /**
     * Here is the field's value (data), set the component to match.
     * @param objValue The raw-data to set this control to.
     */
    public void setControlValue(Object objValue);
    /**
     * Get this component's value as an object that FieldInfo can use.
     * @return The raw data in this control.
     */
    public Object getControlValue();
}
