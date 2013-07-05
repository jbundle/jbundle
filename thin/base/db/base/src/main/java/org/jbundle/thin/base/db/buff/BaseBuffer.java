/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.buff;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.Serializable;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;

/**
 * Utility class used to move a record's fields to a raw data storage area
 * such as a Vector, array, or String.
 * You must override this class for functionality.
 */
public class BaseBuffer extends Object
    implements Serializable     // Make sure you include this on overriding classes
{
	private static final long serialVersionUID = 1L;

	/**
     * Include all fields excluding virtuals.
     */
    public static final int PHYSICAL_FIELDS = 1;
    /**
     * All fields including virtuals.
     */
    public static final int ALL_FIELDS = 2;
    /**
     * All selected (including virtuals).
     */
    public static final int SELECTED_FIELDS = 4;
    /**
     * All selected (Excluding virtuals).
     */
    public static final int DATA_FIELDS = PHYSICAL_FIELDS | SELECTED_FIELDS;
    /**
     * Mask to see the data type.
     */
    public static final int DATA_TYPE_MASK = 7;
    /**
     * "Skip" Fields (on restore) that haven't been modified.
     */
    public static final int MODIFIED_ONLY = 8;
    /**
     * NOTE: These error codes cannot currently be transmitted over the wire
     * as it is very possible these strings could occur in normal data streams.
     * These codes are only used to mark data after they have been transmitted.
     * (See TableSession).
     */
    public static final String DATA_ERROR = "\\ERROR\\";
    public static final String DATA_EOF = "\\EOF\\";
    public static final String DATA_SKIP = "\\SKIP\\";
    /**
     * These are the fields cached in this buffer.
     */
    protected int m_iFieldsTypes = PHYSICAL_FIELDS;
    /**
     * How many message header strings?
     */
    protected int m_iHeaderCount = 0;
    
    /**
     * Constructor.
     */
    public BaseBuffer()
    {
        super();
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical data to initialize this buffer to.
     */
    public BaseBuffer(Object data)
    {
        this();
        this.init(data, PHYSICAL_FIELDS);
    }
    /**
     * Constructor - Init the physical data.
     * @param data The physical data to initialize this buffer to.
     * @param iFieldTypes The default field types to cache.
     */
    public BaseBuffer(Object data, int iFieldsTypes)
    {
        this();
        this.init(data, iFieldsTypes);
    }
    /**
     * Constructor.
     * @param data The physical data to initialize this buffer to (optional).
     * @param iFieldTypes The default field types to cache.
     */
    public void init(Object data, int iFieldsTypes)
    {
        if (data == null)
            this.clearBuffer();
        else
        {
            m_iHeaderCount = 0;
            this.setPhysicalData(data);
            this.resetPosition();
        }
        m_iFieldsTypes = iFieldsTypes;      // Default - assume all fields (call bufferToFields(xxx, false) if not)
    }
    /**
     * Release the objects connected to this buffer.
     */
    public void free()
    {
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null;    // Override this
    }
    /**
     * Add this header string.
     * Header fields are not included in the record, but are used to hold extra information.
     * @param string Add this string to the header fields.
     */
    public void addHeader(Object data)
    {
        this.addNextData(data);
        m_iHeaderCount++;
    }
    /**
     * Add this field to the buffer.
     * @param field The field to add to this buffer.
     */
    public void addNextField(FieldInfo field)
    {
        if (((m_iFieldsTypes & MODIFIED_ONLY) == MODIFIED_ONLY) && (!field.isModified()))
            this.addNextData(DATA_SKIP);
        else
            this.addNextData(field.getData());
    }
    /**
     * Add this string to the buffer.
     * @param string The string to add.
     */
    public void addNextString(String string)
    {
        this.addNextData(string);
    }
    /**
     * Add this data to the buffer.
     * @param string The string to add.
     */
    public void addNextData(Object data)
    {
    }
    /**
     * Move the output buffer to all the fields.
     * This is a utility method that populates the record.
     * @param record The target record.
     * @param bDisplayOption The display option for the movetofield call.
     * @param iMoveMove The move mode for the movetofield call.
     * @return The error code.
     */
    public int bufferToFields(FieldList record, boolean bDisplayOption, int iMoveMode)
    {
        this.resetPosition(); // Start at the first field
        int iFieldCount = record.getFieldCount();   // Number of fields to read in
        int iErrorCode = Constants.NORMAL_RETURN;
        int iTempError;
        for (int iFieldSeq = Constants.MAIN_FIELD; iFieldSeq <= iFieldCount + Constants.MAIN_FIELD - 1; iFieldSeq++)
        {
            FieldInfo field = record.getField(iFieldSeq);
            if (this.skipField(field))
                iTempError = field.initField(bDisplayOption);
            else
              iTempError = this.getNextField(field, bDisplayOption, iMoveMode);
            if (iTempError != Constants.NORMAL_RETURN)
                iErrorCode = iTempError;
        }
        return iErrorCode;
    }
    /**
     * Move the output buffer to all the fields.
     * This is the same as the bufferToFields method, specifying the fieldTypes to move.
     * @param record The target record.
     * @param iFieldTypes The field types to move.
     * @param bDisplayOption The display option for the movetofield call.
     * @param iMoveMove The move mode for the movetofield call.
     * @return The error code.
     */
    public int bufferToFields(FieldList record, int iFieldsTypes, boolean bDisplayOption, int iMoveMode)
    {
        m_iFieldsTypes = iFieldsTypes;
        return this.bufferToFields(record, bDisplayOption, iMoveMode);
    }
    /**
     * Initialize the physical data buffer.
     */
    public void clearBuffer()
    {
        m_iHeaderCount = 0;
    }
    /**
     * Move all the fields to the output buffer.
     * This is a utility method that saves this record to the buffer.
     * @param record The target record.
     */
    public void fieldsToBuffer(FieldList record)
    {
        this.fieldsToBuffer(record, m_iFieldsTypes);    //PHYSICAL_FIELDS);
    }
    /**
     * Move all the fields to the output buffer.
     * This is the same as the fieldsToBuffer method, specifying the fieldTypes to move.
     * @param record The target record.
     * @param iFieldTypes The field types to move.
     */
    public void fieldsToBuffer(FieldList record, int iFieldsTypes)
    {
        m_iFieldsTypes = iFieldsTypes;
        if (this.getHeaderCount() == 0)
            this.clearBuffer();   // Being careful. (Remember to call this at the start anyway)
        int fieldCount = record.getFieldCount();    // Number of fields to write out
        for (int iFieldSeq = Constants.MAIN_FIELD; iFieldSeq <= fieldCount + Constants.MAIN_FIELD - 1; iFieldSeq++)
        {
            FieldInfo field = record.getField(iFieldSeq);
            if (!this.skipField(field))
                this.addNextField(field);
        }
        this.finishBuffer();    //pDestBuff, recordLength, physicalFieldCount);   // two bytes for record length, two for field count
    }
    /**
     * Do I skip this field, based on the m_iFieldsTypes flag?
     * @param field Should I skip this field?
     * @return If true, skip this field.
     */
    public boolean skipField(FieldInfo field)
    {
        boolean bSkipField = true;
        if ((m_iFieldsTypes & DATA_TYPE_MASK) == ALL_FIELDS)
            bSkipField = false;     // Don't skip any
        if ((m_iFieldsTypes & DATA_TYPE_MASK) == SELECTED_FIELDS)
            if (field.isSelected())
                bSkipField = false;     // Don't skip selected
        if ((m_iFieldsTypes & DATA_TYPE_MASK) == PHYSICAL_FIELDS)
            if (!field.isVirtual())
                bSkipField = false;     // Don't skip non-virtuals (Skip virtuals)
        if ((m_iFieldsTypes & DATA_TYPE_MASK) == DATA_FIELDS)  // SELECTED_FIELDS | PHYSICAL_FIELDS
            if (field.isSelected())
                if (!field.isVirtual())
                    bSkipField = false;     // Don't skip selected (Skip virtuals)
        return bSkipField;
    }
    /**
     * Done adding fields, do buffer cleanup.
     */
    public void finishBuffer()
    {
    }
    /**
     * Compare this output buffer to all the fields.
     * This is a utility method that compares the record.
     * @param record The target record.
     * @return True if they are equal.
     */
    public boolean compareToBuffer(FieldList record)
    {
        boolean bBufferEqual = true;
        this.resetPosition(); // Start at the first field
        int iFieldCount = record.getFieldCount();   // Number of fields to read in
        for (int iFieldSeq = Constants.MAIN_FIELD; iFieldSeq <= iFieldCount + Constants.MAIN_FIELD - 1; iFieldSeq++)
        {
            FieldInfo field = record.getField(iFieldSeq);
            if (!this.skipField(field))
                bBufferEqual = this.compareNextToField(field);
            if (!bBufferEqual)
                break;
        }
        return bBufferEqual;
    }
    /**
     * Compare this fields with the next data in the buffer.
     * This is a utility method that compares the record.
     * @param field The target field.
     * @return True if they are equal.
     */
    public boolean compareNextToField(FieldInfo field)   // Must be to call right Get calls
    {
        Object objNext = this.getNextData();
        if (objNext == DATA_ERROR)
            return false;  // EOF
        if (objNext == DATA_EOF)
            return false;  // EOF
        if (objNext == DATA_SKIP)
            return true;  // Don't set this field
        Object objField = field.getData();
        if ((objNext == null) || (objField == null))
        {
            if ((objNext == null) && (objField == null))
                return true;
            return false;
        }
        return objNext.equals(objField);
    }
    /**
     * Get the next header string.
     * @return The next header string.
     */
    public Object getHeader()
    {
        return this.getNextData();
    }
    /**
     * Get the physical field count.
     * @return The number of fields in this buffer.
     */
    public int getPhysicalFieldCount()
    {
        return -1;
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
        Object objNext = this.getNextData();
        if (DATA_ERROR.equals(objNext))
            return Constants.ERROR_RETURN;  // EOF
        if (DATA_EOF.equals(objNext))
            return Constants.ERROR_RETURN;  // EOF
        if (DATA_SKIP.equals(objNext))
            return Constants.NORMAL_RETURN;  // Don't set this field
        return field.setData(objNext, bDisplayOption, iMoveMode);
    }
    /**
     * Get next next string.
     * @param Retrieve the next object from this buffer and return it if it is a string.
     */
    public String getNextString()
    {
        Object data = this.getNextData();
        if (data instanceof String)
            return (String)data;
        return null;    // EOF
    }
    /**
     * Get next next object from this buffer.
     * @return The next data object.
     */
    public Object getNextData()
    {
        return null;    // EOF
    }
    /**
     * Set the position to the start for GetXXXX methods.
     */
    public void resetPosition()
    {
        for (int i = 0; i < m_iHeaderCount; i++)
        {   // First, get past the header(s)
            this.getNextString();
        }
    }
    /**
     * Set this header count.
     * @param iHeaderCount The number of header objects in this buffer.
     */
    public void setHeaderCount(int iHeaderCount)
    {
        m_iHeaderCount = iHeaderCount;
    }
    /**
     * Get this header count.
     * @return The number of header objects in this buffer.
     */
    public int getHeaderCount()
    {
        return m_iHeaderCount;
    }
    /**
     * Get the physical data that this Buffer uses.
     * You must override this method.
     * @return The physical data object.
     */
    public Object getPhysicalData()
    {
        return null;
    }
    /**
     * Set the physical data.
     * You must override this method.
     * @param data The physical data object.
     */
    public void setPhysicalData(Object data)
    {
    }
}
