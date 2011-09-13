/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)FieldRemoveBOnCloseHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BaseListener;

/**
 * Remove a field listener when this listener is freed.
 * This is just a shell listener, all of the work is done by the dependent
 * listener logic in the inherited classes.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldRemoveBOnCloseHandler extends FieldListener
{
    /**
     * Constructor.
     */
    public FieldRemoveBOnCloseHandler()
    {
        super();
    }
    /**
     * Constructor.
     */
    public FieldRemoveBOnCloseHandler(BaseListener listener)
    {
        this();
        this.init(null, listener);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field, BaseListener listener)
    {
        super.init(field);
        this.setDependentListener(listener);    // If either of these are deleted, the other is also
    }
    /**
     * Creates a new object of the same class as this object.
     * @param field The field to add the new cloned behavior to.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone(BaseField field) throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException(); // For now
    }
}
