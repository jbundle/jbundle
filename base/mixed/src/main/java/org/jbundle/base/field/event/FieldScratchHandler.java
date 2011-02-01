package org.jbundle.base.field.event;

/**
 * @(#)FieldScratchHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;

/**
 * This listener saves a copy of the field (if you wan't to check before moving to the actual field!).
 * You have to override this class to make it useful. Call getFieldCopy() to get/create the
 * field scratch area.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldScratchHandler extends FieldListener
{
    /**
     * A cloned copy of the field to use as a scratch area.
     */
    BaseField m_fldCopy = null;

    /**
     * Constructor.
     */
    public FieldScratchHandler()
    {
        super();
        m_fldCopy = null;
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public FieldScratchHandler(BaseField field)
    {
        this();
        this.init(field);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field)
    {
        super.init(field);
        m_fldCopy = null;
    }
    /**
     * Free this listener (and the field copy).
     */
    public void free()
    {
        super.free();
        if (m_fldCopy != null)
        {
            m_fldCopy.free();
            m_fldCopy = null;
        }
    }
    /**
     * Get the field copy.
     * Clone a copy if the copy doesn't exist.
     * @return The field copy.
     */
    public BaseField getFieldCopy()
    {
        try   {
            if (m_fldCopy == null)
                m_fldCopy = (BaseField)this.getOwner().clone();     // Make a copy
        } catch (CloneNotSupportedException ex)   {
            m_fldCopy = null;
        }
        return m_fldCopy;
    }
}
