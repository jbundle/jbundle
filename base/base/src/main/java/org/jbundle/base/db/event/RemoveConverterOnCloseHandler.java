/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)RemoveConverterOnCloseHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.event.FieldRemoveBOnCloseHandler;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * RemoveConverterOnCloseHandler - Remove this converter when this file closes!
 * This is typically used to free a field that doesn't belong to a record.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RemoveConverterOnCloseHandler extends FileListener
{
    /**
     * The converter to remove on close.
     */
    protected Converter m_converter = null;

    /**
     * Constructor.
     */
    public RemoveConverterOnCloseHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The converter to remove on close.
     */
    public RemoveConverterOnCloseHandler(Converter converter)
    {
        this();
        this.init(null, converter);
    }
    /**
     * Constructor.
     * @param converter The converter to remove on close.
     */
    public void init(Record record, Converter converter)
    {
        super.init(record);
        m_converter = converter;
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
            if (m_converter != null) if (m_converter.getField() != null)
                if (((BaseField)m_converter.getField()).getRecord() != owner)
                    {   // Points to different file... make sure this is removed if the other file is closed
                        ((BaseField)m_converter.getField()).addListener(new FieldRemoveBOnCloseHandler(this));
                    }
    }
    /**
     * Free this listener and free this converter if it doesn't belong to a record.
     */
    public void free()
    {
        this.removeConverter();
        super.free();
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {   // Return an error to stop the change
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }
    /**
     * Free the dependent object.
     */
    public void removeConverter()
    {
        if (m_converter != null)
        {
            Converter converter = m_converter;
            m_converter = null;   // This keeps this from being called twice
            converter.removeComponent(null);    // Just in case a converter was used (removes converter, leaves field!)
            if (m_converter instanceof BaseField)
            { // Special case - free this field if it is temporary and belongs to no records.
                if (((BaseField)m_converter).getRecord() == null)
                    m_converter.free();
            }
        }
        m_converter = null;
    }
}
