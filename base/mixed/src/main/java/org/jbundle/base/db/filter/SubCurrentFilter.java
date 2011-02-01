package org.jbundle.base.db.filter;

/**
 * @(#)SubCurrentBehavior.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;


/**
 * Set up a query using the values that are currently in the record's key fields.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SubCurrentFilter extends DependentFileFilter
{
    protected BaseBuffer m_buffer = null;
    
    /**
     * Force all the keys to modified up to this key (-1 = none).
     */
    protected int m_iLastModifiedToSet = -1;

    /**
     * Constructor.
     */
    public SubCurrentFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param bStart Use the current key values for the initial key?
     * @param bEnd Use the current key values for the end key?
     */
    public SubCurrentFilter(boolean bStart, boolean bEnd)
    {
        this();
        this.init(null, -1, bStart, bEnd);
    }
    /**
     * Constructor.
     * @param iLastModifedToSet Force all the keys to modified up to this key (-1 = none).
     * @param bStart Use the current key values for the initial key?
     * @param bEnd Use the current key values for the end key?
     */
    public SubCurrentFilter(int iLastModifiedToSet, boolean bStart, boolean bEnd)
    {
        this();
        this.init(null, iLastModifiedToSet, bStart, bEnd);
    }
    /**
     * Constructor.
     * @param iLastModifedToSet Force all the keys to modified up to this key (-1 = none).
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param bStart Use the current key values for the initial key?
     * @param bEnd Use the current key values for the end key?
     */
    public void init(Record record, int iLastModifiedToSet, boolean bStart, boolean bEnd)
    {
        super.init(record, -1, -1, -1);
        this.setInitialKey(bStart);
        this.setEndKey(bEnd);
        m_iLastModifiedToSet = iLastModifiedToSet;
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        if (m_buffer != null)
            m_buffer.free();
        m_buffer = null;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        SubCurrentFilter newBehavior = new SubCurrentFilter(m_iLastModifiedToSet, m_bInitialKey, m_bEndKey);
        if (m_buffer != null)
            newBehavior.setBuffer((BaseBuffer)m_buffer.clone());
        return newBehavior;
    }
    /**
     * Set the buffer.
     * @param buffer
     */
    public void setBuffer(BaseBuffer buffer)
    {
        m_buffer = buffer;
    }
    /**
     * Set the record that owns this listener.
     * This method caches the current key values.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        if (owner != null)
            if (m_buffer == null)
        {   // Save the current key
            KeyArea keyArea = ((Record)owner).getKeyArea(-1);
            m_buffer = new VectorBuffer(null);
            keyArea.setupKeyBuffer(m_buffer, DBConstants.FILE_KEY_AREA);
        }
        super.setOwner(owner);
    }
    /**
     * Setup the target key field.
     * Restore the original value if this is called for initial or end (ie., boolSetModified us TRUE).
     * @oaram bDisplayOption If true, display changes.
     * @param boolSetModified - If not null, set this field's modified flag to this value
     * @return false If this key was set to all nulls.
     */
    public boolean setMainKey(boolean bDisplayOption, Boolean boolSetModified, boolean bSetIfModified)
    {
        super.setMainKey(bDisplayOption, boolSetModified, bSetIfModified);
        boolean bNonNulls = false;  // Default to yes, all keys are null.
        if (Boolean.TRUE.equals(boolSetModified))
        {   // Only restore the key value when setting the starting or ending key, not when adding a record.
            KeyArea keyArea = this.getOwner().getKeyArea(-1);
            m_buffer.resetPosition();
            keyArea.reverseKeyBuffer(m_buffer, DBConstants.FILE_KEY_AREA);
            for (int i = 0; i < keyArea.getKeyFields(); i++)
            {
                keyArea.getField(i).setModified(false);
                if ((i <= m_iLastModifiedToSet)
                    || (m_iLastModifiedToSet == -1))
                {
                    keyArea.getField(i).setModified(true);
                    if (!keyArea.getField(i).isNull())
                        bNonNulls = true;   // Non null.
                }
            }
        }
        return bNonNulls;
    }
}
