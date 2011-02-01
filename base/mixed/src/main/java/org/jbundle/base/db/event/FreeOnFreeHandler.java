package org.jbundle.base.db.event;

/**
 * @(#)CloseOnFreeHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.Freeable;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Free this other object when this record removes this behavior.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FreeOnFreeHandler extends FileListener
{
    /**
     * The record to close when the owner record removes this behavior.
     */
    protected Record m_recDependent = null;
    /**
     * The object to free when the owner record removes this behavior.
     */
    protected Freeable m_freeable = null;
    /**
     * If true, the record is closed, otherwise it is freed.
     */
    protected boolean m_bCloseOnFree = false;

    /**
     * Constructor.
     */
    public FreeOnFreeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param freeable A object to free when this record is freed.
     */
    public FreeOnFreeHandler(Freeable freeable)
    {
        this();
        this.init(null, freeable, null, false);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recDependent The record to free when this record is freed.
     * @param bCloseOnFree If true, the record freed (always set to true-some special overrides set this to non-true).
     */
    public FreeOnFreeHandler(Record recDependent)
    {
        this();
        this.init(null, null, recDependent, false); //free
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recDependent The record to free when this record is freed.
     * @param bCloseOnFree If true, the record freed (always set to true-some special overrides set this to non-true).
     */
    public FreeOnFreeHandler(Record recDependent, boolean bCloseOnFree)
    {
        this();
        this.init(null, null, recDependent, bCloseOnFree);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param freeable A object to free when this record is freed.
     * @param recDependent The record to free when this record is freed.
     * @param bCloseOnFree If true, the record is freed.
     */
    public void init(Record record, Freeable freeable, Record recDependent, boolean bCloseOnFree)
    {
        super.init(record);
        m_freeable = freeable;
        m_recDependent = recDependent;
        m_bCloseOnFree = bCloseOnFree;
        if (m_recDependent != null)
            m_recDependent.addListener(new FileRemoveBOnCloseHandler(this));
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        if (owner == null)
            this.freeDependent();
        super.setOwner(owner);
    }
    /**
     * Get the dependent object.
     */
    public Object getDependentObject()
    {
        if (m_recDependent != null)
            return m_recDependent;
        return m_freeable;
    }
    /**
     * Get the dependent object.
     */
    public void setDependentObject(Record recDependent)
    {
        m_recDependent = recDependent;
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
    public void freeDependent()
    {
        if (m_recDependent != null) // If close and file is still open
            if (!m_recDependent.isInFree())
        {
            this.setDependentListener(null);    // In case you want to delete me!
            if (m_recDependent != null)
            {
                if (m_bCloseOnFree)
                    m_recDependent.close();    // File is still open, and my listener is still there, close it!
                else
                    m_recDependent.free();    // File is still open, and my listener is still there, close it!
            }
        }
        m_recDependent = null;
        if (m_freeable != null)
            m_freeable.free();
        m_freeable = null;
    }
}
