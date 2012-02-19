/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.util.buff;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.jbundle.base.field.BaseField;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;


/**
 * Utility class used to serialize/de-serialize the fields.
 * Using a ByteArray (Actually a ByteArrayOutputStream/ByteArrayInputStream).
 * The class is useful when you need to save a buffer on a physical media such as disk.
 */
public class ByteBuffer extends BaseBuffer
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * Temporary stream utility for serializing the data.
     */
    protected ByteArrayOutputStream m_baOut = null;
    /**
     * Temporary stream utility for serializing the data.
     */
    protected DataOutputStream m_daOut = null;
    /**
     * Temporary stream utility for serializing the data.
     */
    protected ByteArrayInputStream m_baIn = null;
    /**
     * Temporary stream utility for serializing the data.
     */
    protected DataInputStream m_daIn = null;
    /**
     * If true, the data is serialized with a length byte in each field.
     */
    protected boolean m_bFixedLength = false;
    /**
     * Constructor.
     */
    public ByteBuffer()
    {
        super();
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical data to initialize this buffer to.
     */
    public ByteBuffer(byte[] byInput)
    {
        this();
        this.init(byInput, PHYSICAL_FIELDS, false);
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical data to initialize this buffer to.
     * @param iFieldTypes The default field types to cache.
     */
    public ByteBuffer(byte[] data, int iFieldsTypes)
    {
        this();
        this.init(data, iFieldsTypes, false);
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical data to initialize this buffer to.
     * @param iFieldTypes The default field types to cache.
     * @param bFixedLength Fixed length fields?
     */
    public ByteBuffer(byte[] byInput, boolean bFixedLength)
    {
        this();
        this.init(byInput, PHYSICAL_FIELDS, bFixedLength);
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical data to initialize this buffer to.
     * @param iFieldTypes The default field types to cache.
     * @param bFixedLength Fixed length fields?
     */
    public void init(byte[] byInput, int iFieldsTypes, boolean bFixedLength)
    {
        m_bFixedLength = bFixedLength;
        super.init(byInput, iFieldsTypes);
    }
    /**
     * Release the objects connected to this buffer.
     */
    public void free()
    {
        super.free();
        m_baOut = null;
        m_daOut = null;
        m_baIn = null;
        m_daIn = null;
    }
    /**
     * Add this field to the buffer.
     * @param field The field to add to this buffer.
     */
    public void addNextField(FieldInfo field)
    {
        ((BaseField)field).write(m_daOut, m_bFixedLength);
    }
    /**
     * Add this string to the buffer.
     * @param string The string to add.
     */
    public void addNextString(String string)
    {
        try   {
            m_daOut.writeUTF(string);
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Initialize the buffer.
     */
    public void clearBuffer()
    {
        super.clearBuffer();
        m_baOut = new ByteArrayOutputStream();
        m_daOut = new DataOutputStream(m_baOut);
    }
    /**
     * Are these buffers equal?
     * @param obj The buffer to compare.
     * @return True if equal.
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ByteBuffer))
            return false;
        byte[] byThis = (byte[])this.getPhysicalData();
        byte[] byObj = (byte[])((ByteBuffer)obj).getPhysicalData();
        if (byThis.length != byObj.length)
            return false;
        for (int i = 0; i < byThis.length; i++)
        {
            if (byThis[i] != byObj[i])
                return false;
        }
        return true;
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
        boolean bDone = ((BaseField)field).read(m_daIn, m_bFixedLength);
        if (!bDone)
            return Constants.NORMAL_RETURN;
        else
            return Constants.ERROR_RETURN;
    }
    /**
     * Get next next object from this buffer.
     * @return The next data object.
     */
    public Object getNextData()
    {
        try   {
            String strNext = m_daIn.readUTF();
            return strNext;
        } catch (IOException ex)    {
            ex.printStackTrace();
            return null;
        }
    }
    /**
     * Set the position to the start for GetXXXX methods.
     */
    public void resetPosition()
    {
        try   {
            if (m_daIn != null)
                m_daIn.reset();
            else if (m_baOut != null)
            {
                byte[] byInput = m_baOut.toByteArray();
                this.setPhysicalData(byInput);
            }
            super.resetPosition();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Return the string this is based on.
     */
    public String toString()
    {
        if (m_baOut == null)
            return null;
        return m_baOut.toString();
    }
    /**
     * Get the physical data that this Buffer uses.
     * You must override this method.
     * @return The physical data object.
     */
    public Object getPhysicalData()
    {
        if (m_baOut == null)
        {
            if (m_baIn == null)
                return null;
            m_baIn.reset();
            int iLen = m_baIn.available();
            byte[] rgby = new byte[iLen];
            m_baIn.read(rgby, 0, iLen);
            return rgby;
        }
        return m_baOut.toByteArray();
    }
    /**
     * Set the physical data.
     * You must override this method.
     * @param data The physical data object.
     */
    public void setPhysicalData(Object data)
    {
        m_baIn = new ByteArrayInputStream((byte[])data);
        m_daIn = new DataInputStream(m_baIn);
    }
}
