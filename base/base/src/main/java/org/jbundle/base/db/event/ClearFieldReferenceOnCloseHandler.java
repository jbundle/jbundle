/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)ClearFieldReferenceOnCloseHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.RecordReferenceField;
import org.jbundle.base.field.event.FieldRemoveBOnCloseHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * ClearFieldReferenceOnCloseHandler - Remove this file reference when this file is freed.
 * Calls field.setReferenceRecord(null) when this owner is freed.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ClearFieldReferenceOnCloseHandler extends FileListener
{
    /**
     * The reference field to remove this record reference from on close.
     */
    protected RecordReferenceField m_field = null;

    /**
     * Constructor.
     */
    public ClearFieldReferenceOnCloseHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The reference field to move this record from on close.
     */
    public ClearFieldReferenceOnCloseHandler(RecordReferenceField field)
    {
        this();
        this.init(null, field);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param field The reference field to move this record from on close.
     */
    public void init(Record record, RecordReferenceField field)
    {
        super.init(record);
        m_field = field;
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
        {
            BaseField field = m_field;
            if (field != null)
                if (field.getRecord() != owner)
            {   // Points to different file... make sure this is removed if the other file is closed
                field.addListener(new FieldRemoveBOnCloseHandler(this));
            }
        }
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        this.clearFieldReference();
        super.free();
    }
    /**
     * Get the reference field.
     * @return The reference field.
     */
    public RecordReferenceField getField()
    {
        return m_field;
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
    public void clearFieldReference()
    {
        RecordReferenceField field = m_field;
        m_field = null;   // This keeps this from being called twice
        if (field != null)
            field.setReferenceRecord(null);   // Just in case a converter was used (removes converter, leaves field!)
    }
}
