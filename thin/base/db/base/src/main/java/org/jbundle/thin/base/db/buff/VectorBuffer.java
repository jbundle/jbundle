/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.buff;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.Serializable;
import java.util.Vector;

import org.jbundle.thin.base.db.FieldList;


/**
 * Utility class used to get and move a record's fields to a Vector.
 */
public class VectorBuffer extends BaseBuffer
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * The physical data object.
     */
    protected Vector<Object> m_vector = null;
    /**
     * The current index for retrieving fields.
     */
    protected int m_iCurrentIndex = 0;

    /**
     * Constructor.
     */
    public VectorBuffer()
    {
        super();
    }
    /**
     * Constructor.
     */
    public VectorBuffer(Vector<Object> data)
    {
        this();
        this.init(data, PHYSICAL_FIELDS);
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical data to initialize this buffer to.
     * @param iFieldTypes The default field types to cache.
     */
    public VectorBuffer(Vector<Object> data, int iFieldsTypes)
    {
        this();
        this.init(data, iFieldsTypes);
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical data to initialize this buffer to.
     * @param iFieldTypes The default field types to cache.
     */
    public void init(Object data, int iFieldsTypes)
    {
        m_iCurrentIndex = 0;
        super.init(data, iFieldsTypes);
    }
    /**
     * Release the objects connected to this buffer.
     */
    public void free()
    {
        super.free();
        if (m_vector != null)
            m_vector.removeAllElements();
        m_vector = null;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        Vector<Object> vector = null;
        if (m_vector != null)
            vector = (Vector)m_vector.clone();
        BaseBuffer buffer = new VectorBuffer(vector);
        return buffer;
    }
    /**
     * Add this string to the buffer.
     * @param string The string to add.
     */
    public void addNextData(Object data)
    {
        m_vector.addElement(data);
    }
    /**
     * Initialize the physical data buffer.
     */
    public void clearBuffer()
    {
        super.clearBuffer();
        if (m_vector == null)
            m_vector = new Vector<Object>();
        else
            m_vector.removeAllElements();
        m_iCurrentIndex = 0;
    }
    /**
     * Are these buffers equal?
     * @param obj The buffer to compare.
     * @return True if equal.
     */
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }
    /**
     * Done adding fields, do buffer cleanup.
     */
    public void finishBuffer()
    {
        super.finishBuffer();
    }
    /**
     * Get next next object from this buffer.
     * @return The next data object.
     */
    public Object getNextData()
    {
        if (m_iCurrentIndex >= m_vector.size())
            m_iCurrentIndex = -1;
        if (m_iCurrentIndex == -1)
            return DATA_EOF;  // EOF
        return m_vector.elementAt(m_iCurrentIndex++);
    }
    /**
     * Set the position to the start for GetXXXX methods.
     */
    public void resetPosition()
    {
        m_iCurrentIndex = 0;
        super.resetPosition();
    }
    /**
     * Get the physical data that this Buffer uses.
     * You must override this method.
     * @return The physical data object.
     */
    public Object getPhysicalData()
    {
        return m_vector;
    }
    /**
     * Set the physical data.
     * You must override this method.
     * @param data The physical data object.
     */
    public void setPhysicalData(Object data)
    {
        m_vector = (Vector)data;
        m_iCurrentIndex = 0;
    }
    /**
     * Move the output buffer to all the fields.
     * This is a utility method that populates this record.
     * Note: This only adds synchronization as VectorBuffer is not thread safe.
     * @param record The target record.
     * @param iFieldTypes The field types to move.
     * @param bDisplayOption The display option for the movetofield call.
     * @param iMoveMove The move mode for the movetofield call.
     * @return The error code.
     */
    public synchronized int bufferToFields(FieldList record, boolean bDisplayOption, int iMoveMode)
    {
        return super.bufferToFields(record, bDisplayOption, iMoveMode);
    }
}
