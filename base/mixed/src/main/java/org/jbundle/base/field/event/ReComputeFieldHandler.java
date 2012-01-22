/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)ReComputeField.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.ScreenRecord;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.model.DBConstants;

/**
 * This is a base class that recomputes the target field when this field changes.
 * Just override the compute value method to re-calculate the target field's value.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ReComputeFieldHandler extends FieldListener
{
    /**
     * The target field to recompute on field change.
     */
    protected BaseField m_fldTarget = null;
    /**
     * The target field sequence to recompute on field change.
     */
    protected int m_iTargetFieldSeq = -1;
    /**
     * Disable the target field's behaviors before moving?
     */
    protected boolean m_bDisableTarget = false;

    /**
     * Constructor.
     */
    public ReComputeFieldHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param iTargetFieldSeq The target field sequence to recompute on field change.
     */
    public ReComputeFieldHandler(int iTargetFieldSeq)
    {
        this();
        this.init(null, iTargetFieldSeq, null);
    }
    /**
     * Constructor.
     * @param fldTarget The target field to recompute on field change.
     */
    public ReComputeFieldHandler(BaseField fldTarget)
    {
        this();
        this.init(null, -1, fldTarget);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param iTargetFieldSeq The target field sequence to recompute on field change.
     * @param fldTarget The target field to recompute on field change.
     */
    public void init(BaseField field, int iTargetFieldSeq, BaseField fldTarget)
    {
        super.init(field);
        

        m_bDisableTarget = false;
        m_iTargetFieldSeq = iTargetFieldSeq;
        m_fldTarget = fldTarget;
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
        throw new CloneNotSupportedException(); // For now
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
        {
            if (m_fldTarget != null)
                if (m_fldTarget.getRecord() != this.getOwner().getRecord())
                    m_fldTarget.addListener(new FieldRemoveBOnCloseHandler(this));      // Not same file, if target file closes, remove this listener!
            if (this.getFieldTarget() != null)
                if (!(this.getFieldTarget().isVirtual()) && (!(this.getFieldTarget().getRecord() instanceof ScreenRecord)))
                    if (this.respondsToMode(DBConstants.READ_MOVE) == true)
                        if (this.respondsToMode(DBConstants.INIT_MOVE) == true)
            {   // This is a performance issue if the field I'm updating is connected to a file
                this.setRespondsToMode(DBConstants.READ_MOVE, false);   // Usually, you only want to recompute on screen change
                this.setRespondsToMode(DBConstants.INIT_MOVE, false);   // Usually, you only want to recompute on screen change
            }
        }
    }
    /**
     * Disable the target field before moving?
     */
    public void setDisableTarget(boolean bDisableTarget)
    {
        m_bDisableTarget = bDisableTarget;
    }
    /**
     * The Field has Changed.
     * Get the value of this listener's owner, pass it to the computeValue method and
     * set the returned value to the target field.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * Field changed, re-compute the value in this field.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        double srcValue = ((NumberField)this.getOwner()).getValue();
        BaseField fldTarget = this.getFieldTarget();
        if (this.getOwner().isNull())   // If null, set the target to null
            return fldTarget.moveFieldToThis(this.getOwner(), bDisplayOption, iMoveMode);                    // zero out the field
        boolean[] rgbListeners = null;
        if (m_bDisableTarget)
            rgbListeners = fldTarget.setEnableListeners(false);
        int iErrorCode = fldTarget.setValue(this.computeValue(srcValue), bDisplayOption, iMoveMode);
        if (m_bDisableTarget)
            fldTarget.setEnableListeners(rgbListeners);
        return iErrorCode;
    }
    /**
     * Get the field target.
     */
    public BaseField getFieldTarget()
    {
        BaseField fldTarget = m_fldTarget;
        if (fldTarget == null)
            if (m_iTargetFieldSeq != -1)
                fldTarget = (NumberField)(this.getOwner().getRecord().getField(m_iTargetFieldSeq));
        if (fldTarget == null)
            fldTarget = this.getOwner();
        return fldTarget;
    }
    /**
     * Compute the target value.
     * @param srcValue The value of this listener's owner.
     * @return The value to set the target field to.
     */
    public double computeValue(double srcValue)
    {
        return srcValue;    // By default, return given
    }
}
