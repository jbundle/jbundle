/*
 *  @(#)CalcBalanceHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;

/**
 *  Recompute a target field if this listener's field changes.
 * You can also supply a arithmetic function to use in the calculation.
 */
public class CalcBalanceHandler extends ReComputeFieldHandler
{
    public final static String MINUS = "-";
    public final static String PLUS = "+";
    public final static String MULTIPLY = "*";
    public final static String DIVIDE = "/";
    /**
     * Source value one.
     */
    protected BaseField m_fldValue1 = null;
    /**
     * Source value two.
     */
    protected BaseField m_fldValue2 = null;
    /**
     * If true, calc the absolute value.
     */
    protected boolean m_bUseAbsoluteValue = false;
    /**
     * The operator to use.
     */
    protected String m_strOperator = null;

    /**
     *  Default constructor.
     */
    public CalcBalanceHandler()
    {
        super();
    }
    /**
     * Constructor (calculates the difference between field1 and field2).
     * @param fldTargetValue The destination field.
     * @param fldValue1 Source field 1.
     * @param fldValue2 Source field 2.
     */
    public CalcBalanceHandler(BaseField fldTargetValue, BaseField fldValue1, BaseField fldValue2)
    {
        this();
        this.init(fldTargetValue, fldValue1, fldValue2, null, false);
    }
    /**
     * Constructor (calculates the absolute value of the difference between field1 and field2).
     * @param fldTargetValue The destination field.
     * @param fldValue1 Source field 1.
     * @param fldValue2 Source field 2.
     * @param bUseAbsoluteValue Use the absolute value of the two values.
     */
    public CalcBalanceHandler(BaseField fldTargetValue, BaseField fldValue1, BaseField fldValue2, boolean bUseAbsoluteValue)
    {
        this();
        this.init(fldTargetValue, fldValue1, fldValue2, null, bUseAbsoluteValue);
    }
    /**
     * Constructor (calculates the arithmetic operation between field1 and field2).
     * @param fldTargetValue The destination field.
     * @param fldValue1 Source field 1.
     * @param fldValue2 Source field 2.
     * @param strOperator The arithmetic operator to use to calculate the destination field.
     * @param bUseAbsoluteValue Use the absolute value of the two values.
     */
    public CalcBalanceHandler(BaseField fldTargetValue, BaseField fldValue1, BaseField fldValue2, String strOperator, boolean bUseAbsoluteValue)
    {
        this();
        this.init(fldTargetValue, fldValue1, fldValue2, strOperator, bUseAbsoluteValue);
    }
    /**
     *  Initialize class fields.
     * @param fldTargetValue The destination field.
     * @param fldValue1 Source field 1.
     * @param fldValue2 Source field 2.
     * @param strOperator The arithmetic operator to use to calculate the destination field.
     * @param bUseAbsoluteValue Use the absolute value of the two values.
     */
    public void init(BaseField fldTargetValue, BaseField fldValue1, BaseField fldValue2, String strOperator, boolean bUseAbsoluteValue)
    {
        m_fldValue1 = fldValue1;
        m_fldValue2 = fldValue2;
        m_bUseAbsoluteValue = bUseAbsoluteValue;
        m_strOperator = strOperator;
        super.init(null, -1, null, fldTargetValue);
        this.setRespondsToMode(DBConstants.READ_MOVE, false);
        this.setRespondsToMode(DBConstants.INIT_MOVE, true);
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
            if (m_fldValue1 != null)
                if (m_fldValue1.getRecord() != this.getOwner().getRecord())
                    m_fldValue1.addListener(new FieldRemoveBOnCloseHandler(this));      // Not same file, if target file closes, remove this listener!
            if (m_fldValue2 != null)
                if (m_fldValue2.getRecord() != this.getOwner().getRecord())
                if (m_fldValue2.getRecord() != m_fldValue1.getRecord())
                    m_fldValue2.addListener(new FieldRemoveBOnCloseHandler(this));      // Not same file, if target file closes, remove this listener!
        }
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
     * Compute the target value.
     * @param dSrcValue The value of this listener's owner.
     * @return The value to set the target field to.
     */
    public double computeValue(double dSrcValue)
    {
        double dStartValue = m_fldValue1.getValue();
        if (m_bUseAbsoluteValue)
            dStartValue = Math.abs(dStartValue);
        double dEndValue = m_fldValue2.getValue();
        if (m_bUseAbsoluteValue)
            dEndValue = Math.abs(dEndValue);
        if (m_strOperator == null)
            dEndValue = dStartValue - dEndValue;
        else if (m_strOperator.equals(MINUS))
            dEndValue = dStartValue - dEndValue;
        else if (m_strOperator.equals(PLUS))
            dEndValue = dStartValue + dEndValue;
        else if (m_strOperator.equals(MULTIPLY))
            dEndValue = dStartValue * dEndValue;
        else if (m_strOperator.equals(DIVIDE))
        {
            if (dEndValue != 0)
                dEndValue = dStartValue / dEndValue;
        }
        if (m_bUseAbsoluteValue)
            dEndValue = Math.abs(dEndValue);
        return dEndValue;
    }
}
