/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.buff.str;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.Serializable;
import java.util.Vector;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;


/**
 * Utility class used to get and move a record's fields to a String.
 * The string is stored a length/string/length/string (where length is a 16 bit char).
 */
public class StrBuffer extends BaseBuffer
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * The raw data string.
     */
    protected String m_string = null;   // Change this to a StringBuffer
    /**
     * The current index for retrieving fields.
     */
    protected int m_iCurrentIndex = 0;

    /**
     * Constructor.
     */
    public StrBuffer()
    {
        super();
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical string to initialize this buffer to.
     */
    public StrBuffer(String string)
    {
        this();
        this.init(string, PHYSICAL_FIELDS);
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical string to initialize this buffer to.
     * @param iFieldTypes The default field types to cache.
     */
    public StrBuffer(String data, int iFieldsTypes)
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
        m_string = null;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new StrBuffer(m_string);
    }
    /**
     * Add this field to the buffer.
     * @param field The field to add to this buffer.
     */
    public void addNextField(FieldInfo field)
    {
        this.addNextString(field.getString());
    }
    /**
     * Add this string to the buffer.
     * @param string The string to add.
     */
    public void addNextString(String string)
    {
        int iLength = string.length();
        char charLength = (char)iLength;
        String strNew = m_string + charLength + string;
        m_string = strNew;
    }
    /**
     * Initialize the buffer.
     */
    public void clearBuffer()
    {
        super.clearBuffer();
        m_string = Constants.BLANK;
        m_iCurrentIndex = 0;
    }
    /**
     * Are these buffers equal?
     * @param obj The buffer to compare.
     * @return True if equal.
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof StrBuffer))
            return false;
        return m_string.equals(((StrBuffer)obj).getPhysicalData());
    }
    /**
     * Done adding fields, do buffer cleanup.
     */
    public void finishBuffer()
    {
        super.finishBuffer();
    }
    /**
     * Get the next field and fill it with data from this buffer.
     * You must override this method.
     * @param field The field to set.
     * @param bDisplayOption The display option for setting the field.
     * @param iMoveMove The move mode for setting the field.
     * @return The error code.
     */
    public int getNextField(FieldInfo field, boolean bDisplayOption, int iMoveMode)   // Must be to call right Get calls
    {
        String string = this.getNextString();
        if (string == null)
            return Constants.ERROR_RETURN;
        if (string == DATA_SKIP)
            return Constants.NORMAL_RETURN;  // Don't set this field
        return field.setString(string, bDisplayOption, iMoveMode);
    }
    /**
     * Get next next object from this buffer.
     * @return The next data object.
     */
    public Object getNextData()
    {
        if (m_iCurrentIndex >= m_string.length())
            m_iCurrentIndex = -1;
        if (m_iCurrentIndex == -1)
            return null;    // EOF
        int iLength = m_string.charAt(m_iCurrentIndex);
        m_iCurrentIndex++;      // Bump past the length
        String strNext = m_string.substring(m_iCurrentIndex, m_iCurrentIndex + iLength);
        m_iCurrentIndex += iLength;   // Bump past the string
        return strNext;
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
     * Return the string this is based on.
     * @return The physical data for this buffer.
     */
    public String toString()
    {
        return m_string;
    }
    /**
     * Get the physical data that this Buffer uses.
     * You must override this method.
     * @return The physical data object.
     */
    public Object getPhysicalData()
    {
        return m_string;
    }
    /**
     * Set the physical data.
     * You must override this method.
     * @param data The physical data object.
     */
    public void setPhysicalData(Object data)
    {
        m_string = (String)data;
        m_iCurrentIndex = 0;
    }
}
