package org.jbundle.base.field.event;

/**
 * @(#)RemoveConverterOnDeleteHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.thin.base.db.Converter;


/**
 * Remove this converter when this field is deleted.
 * Use this when the converter is not hooked to an SField!
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RemoveConverterOnFreeHandler extends FieldListener
{
    /**
     * The converter to remove on field free.
     */
    protected Converter m_convTarget = null;

    /**
     * Constructor.
     */
    public RemoveConverterOnFreeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param convTarget The converter to remove on field free.
     */
    public RemoveConverterOnFreeHandler(Converter convTarget)
    {
        this();
        this.init(null, convTarget);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param convTarget The converter to remove on field free.
     */
    public void init(BaseField field, Converter convTarget)
    {
        super.init(field);
        m_convTarget = convTarget;
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
        throw new CloneNotSupportedException(); // Not supported.
    }
    /**
     * This listener is being freed, remove the target converter.
     */
    public void free()
    {
        if (m_convTarget != null)
            m_convTarget.removeComponent(null);   // Just in case a converter was used (removes converter, leaves field!)
        super.free();
    }
}
