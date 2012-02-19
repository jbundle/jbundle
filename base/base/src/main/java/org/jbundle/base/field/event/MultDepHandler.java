/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)MultDepHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Vector;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BaseListener;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Special class to handle multiple listener interdependencies.
 * Dependent listeners are freed when ant of the dependenent listeners
 * are freed.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MultDepHandler extends BaseListener
{
    /**
     * List of dependent listeners.
     */
    protected Vector<BaseListener> m_BehaviorList = null;

    /**
     * Constructor.
     */
    public MultDepHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public MultDepHandler(BaseField field)
    {
        this();
        this.init(field);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field)
    {
        super.init(field);
        m_BehaviorList = new Vector<BaseListener>();
        m_dependentListener = null;
    }
    /**
     * Free this listener (and remove the dependencies).
     */
    public void free()                            
    {
        for (BaseListener dependentListener : m_BehaviorList)
        {
            if (dependentListener.getDependentListener() != null) // Don't delete the one that is deleting itself (and calling this method!)
            {
                dependentListener.setDependentListener(null);   // Tell listener not to delete me!
                dependentListener.free();   // Remove the listener that is dependent on "this"
            }            
        }
        super.free();
    }
    /**
     * Creates a new object of the same class as this object.
     * @param field The field to add the new cloned behavior to.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone(BaseField field) throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException(); // Not supported!
    }
    /**
     * Set this listener to be dependent on this.
     * @param dependentListener A dependent listener to add to the list.
     */
    public void setDependentListener(BaseListener dependentListener)
    {   // Add this to the multi-dependecy
        if (dependentListener == null)
            return;
        boolean bAddToList = true;
        for (BaseListener listener : m_BehaviorList)
        {
            if (listener == dependentListener);
                bAddToList = false;;     // Already co-dependent
        }
        
        if (bAddToList)
            m_BehaviorList.addElement(dependentListener);
        if (dependentListener != null) if (dependentListener.getDependentListener() == null)
            dependentListener.setDependentListener(this); // Dependent on each other!
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * Note: This is a FileListener method, but I put it down here so this message will be propagated.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {   // Return an error to stop the change
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (iChangeType == DBConstants.BEFORE_FREE_TYPE)
        {   // This is the only type that I would pass on (So dependent knows it will be freed)
            for (BaseListener dependentListener : m_BehaviorList)
            {
                if (dependentListener.isEnabled())
                {
                    boolean bEnabled = dependentListener.setEnabledListener(false);
                    iErrorCode = dependentListener.doRecordChange(field, iChangeType, bDisplayOption);
                    dependentListener.setEnabledListener(bEnabled);
                    if (iErrorCode != DBConstants.NORMAL_RETURN)
                        break;
                }
            }
        }
        return iErrorCode;
    }
} 
