/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)ReComputeTimeOffset.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.model.DBConstants;

/**
 * Calculate the end date given the difference in time this listener's owner.
 * Typically used to calculate the difference in days (or the number of days from
 * today).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ReComputeEndDateHandler extends ReComputeFieldHandler
{
    /**
     * The other date field to use in calculating the date difference. If null, uses the current time.
     */
    protected NumberField m_fldTimeField = null;

    /**
     * Constructor.
     */
    public ReComputeEndDateHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param iTargetFieldSeq The date field sequence in this owner to use to calc the difference.
     * @param fldOtherDate The other date field to use in calculating the date difference. If null, uses the current time.
     */
    public ReComputeEndDateHandler(int iTargetFieldSeq, NumberField fldTimeField)
    {
        this();
        this.init(null, iTargetFieldSeq, fldTimeField);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param iTargetFieldSeq The date field sequence in this owner to use to calc the difference.
     * @param fldOtherDate The other date field to use in calculating the date difference. If null, uses the current time.
     */
    public void init(BaseField field, int iTargetFieldSeq, NumberField fldTimeField)
    {
        m_fldTimeField = fldTimeField;
        super.init(field, iTargetFieldSeq, null);
    }
    /**
     * Compute the target value.
     * @param srcValue The value of this listener's owner.
     * @return The value to set the target field to.
     */
    public double computeValue(double srcValue)
    {
        double fldTimeField = 0;
        if (m_fldTimeField != null)
            fldTimeField = m_fldTimeField.getValue();
        else
            fldTimeField = 0;
        double dateThisDate = ((DateTimeField)this.getOwner()).getValue();
        double newDateTime = dateThisDate + fldTimeField * DBConstants.KMS_IN_A_DAY;  // Rounded number of days
        return newDateTime;    // Difference between this date and target
    }
}
