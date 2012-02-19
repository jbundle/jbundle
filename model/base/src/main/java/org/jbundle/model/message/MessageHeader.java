/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.message;


public interface MessageHeader {
    
    /**
     * Return the state of this object as a properties object.
     * @param strKey The key to return.
     * @return The properties.
     */
    public Object get(String strKey);

}
