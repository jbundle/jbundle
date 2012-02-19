/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * BaseListener - Base BaseListener.
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */


/**
 * ListenerOwner - Owners of a listener.
 */
public interface ListenerOwner
{
    /**
     * Add this listener to this owner.
     * @param listener The listener to add.
     */
    public void addListener(BaseListener listener);
    /**
     * Add this listener to this owner.
     * @param listener The listener to add.
     */
    public void doAddListener(BaseListener listener);
    /**
     * Remove this listener from this owner.
     * @param listener The listener to remove.
     * @param bDeleteFlag If true, free the listener.
     */
    public void removeListener(BaseListener listener, boolean bDeleteFlag);
}
