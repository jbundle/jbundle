/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 * ExtractRangeFilter - Behaviors for this file.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.bson.Document;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.StringField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.remote.proxy.ProxyConstants;


/**
 * The ExtractRangeFilter extracts a range of records from the owner file.
 * The range is not a key area, so the doremotecriteria method is overidden
 * instead of doinitial/doendkey.
 * Note: This a server only listener since the range must be passed down to
 * the server on file open. This is handled in the stub/skel code.
 */
public class ExtractRangeFilter extends FileFilter
{
    /**
     * Start of range is in this field.
     */
    protected String startFieldName = null;
    /**
     * End of range is in this field.
     */
    protected String endFieldName = null;
    /**
     * Start of range is in this field.
     */
    protected BaseField m_fldStart = null;      // Target field
    /**
     * End of range is in this field.
     */
    protected BaseField m_fldEnd = null;
    /**
     * Should I pad the end string field with high characters?
     */
    protected int m_iPadfldEnd = PAD_DEFAULT;
    
    public static final int PAD_DEFAULT = -1;
    public static final int DONT_PAD_END_FIELD = 0;
    public static final int PAD_END_FIELD = -2;

    /**
     * Constructor.
     */
    public ExtractRangeFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param iStartFieldSeq The field in this record to start the range.
     * @param fldStart The range starts at this field's value.
     * @param iEndFieldSeq The field in this record to end the range (ie., and end date of a record date range).
     * @param fldEnd The range ends at this field's value.
     * @param iPadfldEnd Should I pad the end string field with high characters?
     */
    public ExtractRangeFilter(String startFieldName, BaseField fldStart, String endFieldName, BaseField fldEnd, int iPadfldEnd)
    {
        this();
        this.init(null, startFieldName, fldStart, endFieldName, fldEnd, iPadfldEnd);
    }
    /**
     * Constructor.
     * @param iFieldSeq The field in this record to compare to the range.
     * @param fldStart The range starts at this field's value.
     * @param fldEnd The range ends at this field's value.
     * @param iPadfldEnd Should I pad the end string field with high characters?
     */
    public ExtractRangeFilter(String startFieldName, BaseField fldStart, BaseField fldEnd, int iPadfldEnd)
    {
        this();
        this.init(null, startFieldName, fldStart, null, fldEnd, iPadfldEnd);
    }
    /**
     * Constructor.
     * @param iFieldSeq The field in this record to compare to the range.
     * @param fldStart The range starts at this field's value.
     * @param iPadfldEnd Should I pad the end string field with high characters?
     */
    public ExtractRangeFilter(String startFieldName, BaseField fldStart, int iPadfldEnd)
    {
        this();
        this.init(null, startFieldName, fldStart, null, null, iPadfldEnd);
    }
    /**
     * Constructor (using the default pad).
     * @param iFieldSeq The field in this record to compare to the range.
     * @param fldStart The range starts at this field's value.
     */
    public ExtractRangeFilter(String startFieldName, BaseField fldStart)
    {
        this();
        this.init(null, startFieldName, fldStart, null, null, PAD_DEFAULT);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iStartFieldSeq The field in this record to start the range.
     * @param fldStart The range starts at this field's value.
     * @param iEndFieldSeq The field in this record to end the range (ie., and end date of a record date range).
     * @param fldEnd The range ends at this field's value.
     * @param iPadfldEnd Should I pad the end string field with high characters?
     */
    public void init(Record record, String startFieldName, BaseField fldStart, String endFieldName, BaseField fldEnd, int iPadfldEnd)
    {
        super.init(record);
        this.setMasterSlaveFlag(FileListener.RUN_IN_SLAVE);   // This runs on the slave (if there is a slave)

        m_fldStart = fldStart;
        m_fldEnd = fldEnd;
        
        this.startFieldName = startFieldName;
        this.endFieldName = endFieldName;

        if (this.endFieldName == null)
            this.endFieldName = this.startFieldName;
        if (m_fldEnd == null)
            m_fldEnd = m_fldStart;
        m_iPadfldEnd = iPadfldEnd;
        if (iPadfldEnd == PAD_DEFAULT)
        { // Default to false, unless start = end, then default to true
            m_iPadfldEnd = DONT_PAD_END_FIELD;
            if (m_fldEnd == m_fldStart)
                m_iPadfldEnd = PAD_END_FIELD;
        }
        if (m_fldEnd != null) if (!(m_fldEnd instanceof StringField))
            m_iPadfldEnd = DONT_PAD_END_FIELD;  // Can't pad these types
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        ExtractRangeFilter listener = new ExtractRangeFilter();
        listener.init(null, startFieldName, m_fldStart, endFieldName, m_fldEnd, m_iPadfldEnd);
        return listener;
    }
    /**
     * Set up/do the remote criteria.
     * The range is start >= targetfield and end <= targetfield.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @param doc
     * @return True if you should not skip this record (does a check on the local data).
     */
    public boolean doRemoteCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList, Document doc)
    {   // Between start and end dates? (Defaults to Currentdate thru +1 year)
        boolean bDontSkip2 = true;
        if (!m_fldStart.isNull())
        {
            BaseField endField = this.getOwner().getField(endFieldName);
            bDontSkip2 = this.fieldCompare(endField, m_fldStart, GREATER_THAN_EQUAL, strbFilter, bIncludeFileName, vParamList, doc);
        }
        BaseField pfldHigh = m_fldEnd;
        if (m_iPadfldEnd != DONT_PAD_END_FIELD)
        {
            try   {
                pfldHigh = (BaseField)m_fldEnd.clone();
                pfldHigh.setToLimit(DBConstants.END_SELECT_KEY);
                if (!m_fldEnd.isNull())
                {
                    String strHigh = m_fldEnd.toString() + pfldHigh.toString();
                    pfldHigh.setString(strHigh);
                }
            } catch (CloneNotSupportedException ex)   {
            }
        }
        boolean bDontSkip = true;
        if (!m_fldEnd.isNull())
        {
            BaseField startField = this.getOwner().getField(startFieldName);
            bDontSkip = this.fieldCompare(startField, pfldHigh, LESS_THAN_EQUAL, strbFilter, bIncludeFileName, vParamList, doc);
        }
        if (m_iPadfldEnd != DONT_PAD_END_FIELD)
        {
            pfldHigh.free();
            pfldHigh = null;
        }
        if (strbFilter != null) if (!strbFilter.equals(Constants.BLANK))
        {
            bDontSkip = true; // Don't need to compare, if I'm creating a filter to pass to SQL 
            bDontSkip2 = true;
        }
        if (bDontSkip && bDontSkip2)
            return super.doRemoteCriteria(strbFilter, bIncludeFileName, vParamList, doc);    // Dont skip this record
        else
            return false;   // Skip this one
    }
    /**
     * Marshall all of this listener's params to pass to mirror copy to initialize a new object.
     * Marshall all the method variables to the output stream.
     * @param daOut The output stream to marshal the data to.
     */
    public void initRemoteStub(ObjectOutputStream daOut)
    {
        try   {
            daOut.writeUTF(startFieldName != null ? startFieldName : ProxyConstants.NULL);
            daOut.writeUTF(endFieldName != null ? endFieldName : ProxyConstants.NULL);
            this.writeField(daOut, m_fldStart);
            this.writeField(daOut, m_fldEnd);
            daOut.writeInt(m_iPadfldEnd);
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Use these marshalled params to initialize this object.
     * Unmarshall the objects and call init.
     * @param daIn The input stream to unmarshal the data from.
     */
    public void initRemoteSkel(ObjectInputStream daIn)
    {
        try   {
            String startFieldName = daIn.readUTF();
            if (ProxyConstants.NULL.equals(startFieldName))
                startFieldName = null;
            String endFieldName = daIn.readUTF();
            if (ProxyConstants.NULL.equals(endFieldName))
                endFieldName = null;
            BaseField fldStart = this.readField(daIn, (BaseField)m_fldStart);
            BaseField fldEnd = this.readField(daIn, (BaseField)m_fldEnd);
            int iPadfldEnd = daIn.readInt();

            this.init(null, startFieldName, fldStart, endFieldName, fldEnd, iPadfldEnd);
        } catch (IOException ex)    {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     * Get the foreign field that references this record.
     * There can be more than one, so supply an index until you get a null.
     * @param iCount The index of the reference to retrieve
     * @return The referenced field
     */
    public BaseField getReferencedField(int iIndex)
    {
        if (iIndex == 0)
        {
            if (m_fldStart != null)
                return m_fldStart;
            else
                return m_fldEnd;
        }
        if (iIndex == 1)
            if (m_fldStart != null) // Because it would have been returned at index 0.
                return m_fldEnd;
        return null;
    }
}
