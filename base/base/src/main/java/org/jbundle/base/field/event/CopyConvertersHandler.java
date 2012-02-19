/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CopyConvertersHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.event.FileRemoveBOnCloseHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.convert.FieldConverter;
import org.jbundle.thin.base.db.Converter;


/**
 * When this field changes, copy a source converter to a destination converter.
 * The default implementation moves this field to a converter.
 * <p>WARNING: This should not pose a problem, but the converter and all chained converters
 * are free(d) on setOwner(null). The target field is not affected.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CopyConvertersHandler extends FieldListener
{
    /**
     * Source converter (if null, this listener's field is the source).
     */
    protected Converter m_converterSource = null;
    /**
     * Destination converter.
     */
    protected Converter m_converterDest = null;
    /**
     *
     */
    protected boolean m_bFreeChainedConverters = true;

    /**
     * Constructor.
     */
    public CopyConvertersHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param converterDest The destination converter.
     */
    public CopyConvertersHandler(Converter converterDest)
    { // Split name into title/first/middle/last
        this();
        this.init(null, converterDest, null, true);
    }
    /**
     * Constructor.
     * @param converterDest The destination converter.
     * @param converterSource The source converter.
     */
    public CopyConvertersHandler(Converter converterDest, Converter converterSource)
    { // Split name into title/first/middle/last
        this();
        this.init(null, converterDest, converterSource, true);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param converterDest The destination converter.
     * @param converterSource The source converter.
     */
    public void init(BaseField field, Converter converterDest, Converter converterSource, boolean bFreeChainedConverters)
    {
        m_converterDest = converterDest;
        m_converterSource = converterSource;
        m_bFreeChainedConverters = bFreeChainedConverters;
        super.init(field);
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
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
        {
            if (m_converterSource == null)
                m_converterSource = this.getOwner();
            if (m_converterDest != null)
                if (m_converterDest.getField() != null)
                    if (m_converterSource.getField() == this.getOwner())    // Always!
                        if (((BaseField)m_converterDest.getField()).getRecord() != this.getOwner().getRecord())
            { // Not same file, if target file closes, remove this listener!
                ((BaseField)m_converterDest.getField()).getRecord().addListener(new FileRemoveBOnCloseHandler(this));
            }
        }
        else if (m_bFreeChainedConverters)
        {   // Free all the converters in the chain
            while ((m_converterDest != null) && (m_converterDest != m_converterDest.getField()))
            {
                Converter converterNext = null;
                if (m_converterDest instanceof FieldConverter)
                    converterNext = ((FieldConverter)m_converterDest).getNextConverter();
                m_converterDest.free();
                m_converterDest = converterNext;
            }
            while ((m_converterSource != null) && (m_converterSource != m_converterSource.getField()))
            {
                Converter converterNext = null;
                if (m_converterSource instanceof FieldConverter)
                    converterNext = ((FieldConverter)m_converterSource).getNextConverter();
                m_converterSource.free();
                m_converterSource = converterNext;
            }
        }
    }
    /**
     * The Field has Changed.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        return m_converterDest.setString(m_converterSource.toString(), bDisplayOption, iMoveMode);
    }
}
