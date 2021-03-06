/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)ReComputeTimeOffset.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.model.DBConstants;

/**
 * Calculate the difference in days between a date and this listener's owner.
 * Typically used to calculate the difference in days (or the number of days from
 * today).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ReComputeTimeOffsetHandler extends ReComputeFieldHandler
{
    /**
     * The other date field to use in calculating the date difference. If null, uses the current time.
     */
    protected DateTimeField m_fldOtherDate = null;

    /**
     * Constructor.
     */
    public ReComputeTimeOffsetHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param targetFieldName The date field sequence in this owner to use to calc the difference.
     * @param fldOtherDate The other date field to use in calculating the date difference. If null, uses the current time.
     */
    public ReComputeTimeOffsetHandler(String targetFieldName, DateTimeField fldOtherDate)
    {
        this();
        this.init(null, targetFieldName, fldOtherDate);
    }
    /**
     * Constructor - Using the current time as the offset.
     * @param targetFieldName The date field sequence in this owner to use to calc the difference.
     */
    public ReComputeTimeOffsetHandler(String targetFieldName)
    {
        this();
        this.init(null, targetFieldName, (DateTimeField)null);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param targetFieldName The date field sequence in this owner to use to calc the difference.
     * @param fldOtherDate The other date field to use in calculating the date difference. If null, uses the current time.
     */
    public void init(BaseField field, String targetFieldName, DateTimeField fldOtherDate)
    {
        m_fldOtherDate = fldOtherDate;
        super.init(field, targetFieldName, null);
    }
    /**
     * Compute the target value.
     * @param srcValue The value of this listener's owner.
     * @return The value to set the target field to.
     */
    public double computeValue(double srcValue)
    {
        double dateOtherDate = 0;
        double dateThisDate = 0;
        if (m_fldOtherDate != null)
            dateOtherDate = m_fldOtherDate.getValue();
        else
            dateOtherDate = DateField.todaysDate();
        dateThisDate = ((DateTimeField)this.getOwner()).getValue();
        double tsDifference = (Math.floor((dateThisDate - dateOtherDate + 0.5)) / DBConstants.KMS_IN_A_DAY);  // Rounded number of days
        return tsDifference;    // Difference between this date and target
    }
}
