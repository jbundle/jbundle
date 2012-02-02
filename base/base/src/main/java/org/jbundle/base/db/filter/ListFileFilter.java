/*
 *  @(#)ApTrxClassField.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

import java.util.HashSet;
import java.util.Vector;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;


/**
 *  ListFileFilter - Filter the A/P Detail depending on this list of valid objects.
 */
public class ListFileFilter extends FileFilter
{
    /**
     * The field to check.
     */
    protected BaseField m_fldTarget = null;
    /**
     * The field sequence to check.
     */
    protected String m_fsTarget = null;
    /**
     * The list of valid objects.
     */
    protected HashSet<Object> m_hsFilter = null;
    
    /**
     * Default constructor.
     */
    public ListFileFilter()
    {
        super();
    }
    /**
     * FilterApTrxHandler Method.
     */
    public ListFileFilter(BaseField fldTarget)
    {
        this();
        this.init(fldTarget, null);
    }
    /**
     * FilterApTrxHandler Method.
     */
    public ListFileFilter(String fsTarget)
    {
        this();
        this.init(null, fsTarget);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseField fldTarget, String fsTarget)
    {
        m_hsFilter = null;
        m_fldTarget = fldTarget;
        m_fsTarget = fsTarget;
        super.init(null);
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
        if (this.getOwner() != null)
            if (m_fldTarget == null)
                if (m_fsTarget != null)
                    m_fldTarget = this.getOwner().getField(m_fsTarget);
    }
    /**
     * Check the record locally.
     */
    public boolean doLocalCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {
        Object objTargetValue = m_fldTarget.getData();
        if ((m_hsFilter != null) && (!m_hsFilter.isEmpty()) && (!m_hsFilter.contains(objTargetValue)))
            return false;
        return super.doLocalCriteria(strbFilter, bIncludeFileName, vParamList);
    }
    /**
     * ClearFilter Method.
     */
    public void clearFilter()
    {
        m_hsFilter = new HashSet<Object>();
    }
    /**
     * AddTrxStatusID Method.
     */
    public void addFilter(Object objFilterValue)
    {
        if (m_hsFilter == null)
            this.clearFilter();
        m_hsFilter.add(objFilterValue);
    }

}
