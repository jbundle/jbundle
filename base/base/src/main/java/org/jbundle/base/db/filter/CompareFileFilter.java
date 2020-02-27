/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

/**
 * @(#)CompareFileFilter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.bson.Document;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.event.FieldRemoveBOnCloseHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.remote.proxy.ProxyConstants;


/**
 * A CompareFileFilter creates a filter to do some kind of field comparison.
 * You can compare to a field or string and do a comparison if a flag is on.
 * You can decide on the comparison type (=,<,>, etc).
 * Note: This a server only listener since the params must be passed down to
 * the server on file open. This is handled in the stub/skel code.
 * @version 1.0.0
 * @author    Don Corley
 */
public class CompareFileFilter extends FileFilter
{
    /**
     * The field sequence in this record to compare (see m_fldToCheck).
     */
    protected String fieldNameToCheck = null;
    /**
     * The field in this record to compare.
     */
    protected BaseField m_fldToCheck = null;
    /**
     * The comparison sign.
     */
    protected String m_strSeekSign = null;
    /**
     * If this field is non-null and the state is false, don't do this comparison.
     */
    protected Converter m_convFlag = null;
    /**
     * The field to compare the target field to.
     */
    protected BaseField m_fldToCompare = null;
    /**
     * The string to compare the target field to.
     */
    protected String m_strToCompare = DBConstants.BLANK;
    /**
     * If false, this will filter if the compare field is null (usually a empty query set).
     */
    protected boolean m_bDontFilterIfNullCompare = false;

    /**
     * Constructor.
     */
    public CompareFileFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param fieldNameToCheck The field sequence in this record to compare (see m_fldToCheck).
     * @param strSeekSign The comparison sign.
     * @param pconvFlag If this field is non-null and the state is false, don't do this comparison.
     * @param bDontFilterIfNullCompare Is this record valid if the compare field is null?
     */
    public CompareFileFilter(String fieldNameToCheck, String strToCompare, String strSeekSign, Converter pconvFlag, boolean bDontFilterIfNullCompare)
    {
        this();
        this.init(null, fieldNameToCheck, strToCompare, strSeekSign, pconvFlag, bDontFilterIfNullCompare, null, null);
    }
    /**
     * Constructor.
     * @param fieldNameToCheck The field sequence in this record to compare (see m_fldToCheck).
     * @param strSeekSign The comparison sign.
     * @param pconvFlag If this field is non-null and the state is false, don't do this comparison.
     * @param bDontFilterIfNullCompare Is this record valid if the compare field is null?
     */
    public CompareFileFilter(String fieldNameToCheck, BaseField fldToCompare, String strSeekSign, Converter pconvFlag, boolean bDontFilterIfNullCompare)
    {
        this();
        this.init(null, fieldNameToCheck, null, strSeekSign, pconvFlag, bDontFilterIfNullCompare, null, fldToCompare);
    }
    /**
     * Constructor.
     * @param fieldToCheck The field sequence in this record to compare (see m_fldToCheck).
     * @param strSeekSign The comparison sign.
     * @param pconvFlag If this field is non-null and the state is false, don't do this comparison.
     * @param bDontFilterIfNullCompare Is this record valid if the compare field is null?
     */
    public CompareFileFilter(BaseField fieldToCheck, String strToCompare, String strSeekSign, Converter pconvFlag, boolean bDontFilterIfNullCompare)
    {
        this();
        this.init(null, null, strToCompare, strSeekSign, pconvFlag, bDontFilterIfNullCompare, fieldToCheck, null);
    }
    /**
     * Constructor.
     * @param fldToCheck The field in this record to compare.
     * @param strSeekSign The comparison sign.
     * @param fldToCompare The field to compare the target field to.
     */
    public CompareFileFilter(BaseField fldToCheck, BaseField fldToCompare, String strSeekSign)
    {
        this();
        this.init(null, null, null, strSeekSign, null, true, fldToCheck, fldToCompare);
    }
    /**
     * Constructor.
     * @param fldToCheck The field in this record to compare.
     * @param strSeekSign The comparison sign.
     * @param pconvFlag If this field is non-null and the state is false, don't do this comparison.
     * @param fldToCompare The field to compare the target field to.
     * @param bDontFilterIfNullCompare Is this record valid if the compare field is null?
     */
    public CompareFileFilter(BaseField fldToCheck, BaseField fldToCompare, String strSeekSign, Converter pconvFlag, boolean bDontFilterIfNullCompare)
    {
        this();
        this.init(null, null, null, strSeekSign, pconvFlag, bDontFilterIfNullCompare, fldToCheck, fldToCompare);
    }
    /**
     * Constructor.
     * @param fieldNameToCheck The field sequence in this record to compare (see m_fldToCheck).
     * @param fldToCheck The field in this record to compare.
     * @param strSeekSign The comparison sign.
     * @param pconvFlag If this field is non-null and the state is false, don't do this comparison.
     * @param fldToCompare The field to compare the target field to.
     * @param strToCompare The string to compare the target field to.
     * @param bDontFilterIfNullCompare Is this record valid if the compare field is null?
     */
    public void init(Record record, String fieldNameToCheck, String strToCompare, String strSeekSign, Converter pconvFlag, boolean bDontFilterIfNullCompare, BaseField fldToCheck, BaseField fldToCompare)
    {
        super.init(record);
        this.setMasterSlaveFlag(FileListener.RUN_IN_SLAVE);   // This runs on the slave (if there is a slave)

        this.fieldNameToCheck = fieldNameToCheck;
        m_fldToCheck = fldToCheck;
        m_fldToCompare = fldToCompare;
        m_strSeekSign = strSeekSign;
        m_convFlag = pconvFlag;
        m_bDontFilterIfNullCompare = bDontFilterIfNullCompare;
        m_strToCompare = strToCompare;

        if ((m_strSeekSign == null) || (m_strSeekSign.length() == 0))
            m_strSeekSign = DBConstants.EQUALS;        // Default comparison
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        CompareFileFilter listener = new CompareFileFilter();
        listener.init(null, fieldNameToCheck, m_strToCompare, m_strSeekSign, m_convFlag, m_bDontFilterIfNullCompare, m_fldToCheck, m_fldToCompare);
        return listener;
    }
    /**
     * Free this filter.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set the field or file that owns this listener.
     * Besides inherited, this method closes the owner record.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner == null)
            return;
        if ((m_strFieldNameToCheck != null)
            && (m_strFieldNameToCheck.length() > 0))
            if ((m_fldToCheck == null)
                || (m_fldToCheck.getFieldName() == null)
                || (m_fldToCheck.getFieldName().length() == 0))
                    m_fldToCheck = this.getOwner().getField(m_strFieldNameToCheck); // If you have the fieldname, but not the field, get the field.
        if (m_fldToCompare != null) if (m_fldToCompare.getRecord() != this.getOwner())
            m_fldToCompare.addListener(new FieldRemoveBOnCloseHandler(this));
        if (m_fldToCheck != null) if (m_fldToCheck.getRecord() != this.getOwner())  // If field is not in this file, remember to remove it
            m_fldToCheck.addListener(new FieldRemoveBOnCloseHandler(this));
//x        this.getOwner().close();    // Must requery after setting dependent fields!
    }
    /**
     * Set up/do the remote criteria.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @param doc
     * @return True if you should not skip this record (does a check on the local data).
     */
    public boolean doRemoteCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList, Document doc)
    {
        if (m_convFlag != null) if (!m_convFlag.getState())
            return super.doRemoteCriteria(strbFilter, bIncludeFileName, vParamList, doc);    // Flag not set, don't process it
        BaseField recordField = m_fldToCheck;
        if (m_fldToCheck == null)
            if (fieldNameToCheck != null)
                recordField = this.getOwner().getField(fieldNameToCheck);   //.getText(recordString);
        if (m_fldToCompare != null)
            if (m_bDontFilterIfNullCompare)
                if ((m_fldToCompare.isNull()) && (m_fldToCompare.isNullable()))    // Null field
                    return super.doRemoteCriteria(strbFilter, bIncludeFileName, vParamList, doc);    // Dont skip this record
        boolean bDontSkip;
        if (m_fldToCompare != null)
            bDontSkip = this.fieldCompare(recordField, m_fldToCompare, m_strSeekSign, strbFilter, bIncludeFileName, vParamList, doc);
        else
            bDontSkip = this.fieldCompare(recordField, m_strToCompare, m_strSeekSign, strbFilter, bIncludeFileName, vParamList, doc);
        if (strbFilter != null)
            bDontSkip = true; // Don't need to compare, if I'm creating a filter to pass to SQL 
        if (bDontSkip)
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
            daOut.writeUTF(fieldNameToCheck != null ? fieldNameToCheck : ProxyConstants.NULL);
            daOut.writeObject(m_strToCompare);
            daOut.writeUTF(m_strSeekSign);
            this.writeField(daOut, m_convFlag);
            daOut.writeBoolean(m_bDontFilterIfNullCompare);
            this.writeField(daOut, m_fldToCheck);
            this.writeField(daOut, m_fldToCompare);
            String strFieldNameToCheck = DBConstants.BLANK;
            if (m_fldToCheck != null)
                strFieldNameToCheck = m_fldToCheck.getFieldName(false, true, false);   // Full file.fieldname
            daOut.writeUTF(strFieldNameToCheck);
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Temporary holder of the field name to check from the client.
     */
    protected String m_strFieldNameToCheck = null;
    /**
     * Use these marshalled params to initialize this object.
     * Unmarshall the objects and call init.
     * @param daIn The input stream to unmarshall the data from.
     */
    public void initRemoteSkel(ObjectInputStream daIn)
    {
        try   {
            String fieldNameToCheck = daIn.readUTF();
            if (ProxyConstants.NULL.equals(fieldNameToCheck))
                fieldNameToCheck = null;
            String strToCompare = (String)daIn.readObject();
            String strSeekSign = daIn.readUTF();
            BaseField convFlag = this.readField(daIn, (BaseField)m_convFlag);
            boolean bDontFilterIfNullCompare = daIn.readBoolean();
            BaseField fldToCheck = this.readField(daIn, m_fldToCheck);
            BaseField fldToCompare = this.readField(daIn, m_fldToCompare);
            m_strFieldNameToCheck = daIn.readUTF();

            this.init(null, fieldNameToCheck, strToCompare, strSeekSign, convFlag, bDontFilterIfNullCompare, fldToCheck, fldToCompare);
        } catch (IOException ex)    {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     * Get the foreign field that references this record.
     * There can be more than one, so supply an index until you get a null.
     * @param iIndex The index of the reference to retrieve
     * @return The referenced field
     */
    public BaseField getReferencedField(int iIndex)
    {
        if (iIndex == 0)
            return m_fldToCompare;
        return null;    // Override this!
    }
}
