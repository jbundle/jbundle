/*
 *  @(#)ApTrxClassField.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

import java.util.Vector;

import org.bson.Document;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BitReferenceField;
import org.jbundle.base.field.ListenerOwner;


/**
 *  ListFileFilter - Filter the A/P Detail depending on this list of valid objects.
 */
public class BitFileFilter extends FileFilter
{
    /**
     * The field to check.
     */
    protected BaseField m_fldToCompare = null;
    /**
     * The field sequence to check.
     */
    protected String m_fsRecordTarget = null;
    /**
     * The field sequence to check.
     */
    protected BaseField m_fldRecordTarget = null;
    /**
     * No filter if all displayed.
     */
    protected boolean m_bNoFilterIfAll = false;
    /**
     * No filter if none displayed.
     */
    protected boolean m_bNoFilterIfNone = false;
    
    /**
     * Default constructor.
     */
    public BitFileFilter()
    {
        super();
    }
    /**
     * FilterApTrxHandler Method.
     */
    public BitFileFilter(String fsTarget, BaseField fldTarget)
    {
        this();
        this.init(null, null, fsTarget, fldTarget);
    }
    /**
     * FilterApTrxHandler Method.
     */
    public BitFileFilter(BaseField fldRecordTarget, BaseField fldTarget)
    {
        this();
        this.init(null, fldRecordTarget, null, fldTarget);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, BaseField fldRecordTarget, String fsTarget, BaseField fldTarget)
    {
        m_fldToCompare = fldTarget;
        m_fsRecordTarget = fsTarget;
        m_fldRecordTarget = fldRecordTarget;
        super.init(record);
    }
    /**
     * Free Method.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
    }
    /**
     * Check the record locally.
     */
    public boolean doLocalCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList, Document doc)
    {
        Integer objTargetValue = (Integer)m_fldToCompare.getData();
        if (m_fldRecordTarget == null)
            m_fldRecordTarget = this.getOwner().getField(m_fsRecordTarget);
        Integer objRecordValue = (Integer)m_fldRecordTarget.getData();
        int iTargetValue = 0;
        if (objTargetValue != null)
            iTargetValue = objTargetValue.intValue();
        int iRecordValue = 0;
        if (objRecordValue != null)
            iRecordValue = objRecordValue.intValue();
        int iMask = BitReferenceField.ALL_TABLES;
        if (m_fldToCompare instanceof BitReferenceField)
             iMask = ((BitReferenceField)m_fldToCompare).getBitsToCheck();
        boolean bCheckForMatch = true;
        if (m_bNoFilterIfAll)
            if (iTargetValue == -1)
                bCheckForMatch = false;    // No filter
        if (m_bNoFilterIfNone)
            if (iTargetValue == ~iMask)
                bCheckForMatch = false;    // No filter
        if (bCheckForMatch)
            if ((iMask & iTargetValue & iRecordValue) == 0)
                return false;   // No match
        return super.doLocalCriteria(strbFilter, bIncludeFileName, vParamList, doc);
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
            return m_fldToCompare;
        return null;    // Override this!
    }
    /**
     * No filter if all displayed.
     */
    public void setNoFilterIfAll(boolean bNoFilterIfAll)
    {
        m_bNoFilterIfAll =  bNoFilterIfAll;
    }
    /**
     * No filter if none displayed.
     */
    public void setNoFilterIfNone(boolean bNoFilterIfNone)
    {
        m_bNoFilterIfNone =  bNoFilterIfNone;
    }
}
