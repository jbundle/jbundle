package org.jbundle.model;

import java.util.Map;

/**
 * A property owner includes a reference to a "Properties" object.
 * The difference is the "retrieveUserProperties(strPropertyCode)" call which
 * returns the PropertyOwner that contains the properties for this strPropertyCode.
 * For example., If you call retrieveUserProperties(MenuConstants.SCREEN) the call
 * would typically get the the "Application" property owner who would read
 * the "screenpreferences.properties" file and return itself (or a wrapper
 * around the Properties object for the calling method to call "getProperty()" calls.
 */
public interface PropertyOwner
{
    /**
     * Get the properties.
     * @param strProperty The property key.
     * @return The properties object.
     */
    public String getProperty(String strProperty);
    /**
     * Set this property.
     * @param strProperty The property key.
     * @param strValue The property value.
     */
    public void setProperty(String strProperty, String strValue);
    /**
     * Set the properties.
     * @param strProperties The properties to set.
     */
    public void setProperties(Map<String, Object> properties);
    /**
     * Get the properties.
     * NOTE: Do NOT use this method unless you are sure of the property owner.
     * This method WILL NOT get the properties down the chain like getProperty will.
     * @return A <b>copy</b> of the properties in the propertyowner.
     */
    public Map<String, Object> getProperties();
    /**
     * Get the owner of this property key.
     * @param strPropertyCode The key I'm looking for the owner to.
     * @return The owner of this property key.
     */
    public PropertyOwner retrieveUserProperties(String strRegistrationKey);
}
