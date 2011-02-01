package org.jbundle.base.field.convert;

/**
 * @(#)PercentConverter.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.NumberField;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;


/**
 * Convert number to a percent... and back.
 * This simply shifts the percentage point by 2 positions.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class PercentConverter extends FieldConverter
{

    /**
     * Constructor.
     */
    public PercentConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param numberField The percent field target.
     */
    public PercentConverter(NumberField numberField)
    {
        this();
        this.init(numberField);
    }
    /**
     * Retrieve (in string format) from this field.
     * @return This field as a percent string.
     */
    public String getString() 
    {
        String string = Constants.BLANK;
        if (this.getNextConverter().getData() != null)
        {
            double doubleValue = this.getValue();
            synchronized (gPercentFormat)
            {
                string = gPercentFormat.format(doubleValue);
            }
        }
        return string;
    }
    /**
     * Convert and move string to this field.
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String strValue, boolean bDisplayOption, int iMoveMode)
    {
        NumberField numberField = (NumberField)this.getField();
        if ((strValue == null) || (strValue.length() == 0))
            return super.setString(strValue, bDisplayOption, iMoveMode);  // Don't trip change or display
        Number tempBinary = null;
        try   {
            tempBinary = (Number)numberField.stringToBinary(strValue);
        } catch (Exception ex)  {
            Task task = numberField.getRecord().getRecordOwner().getTask();
            return task.setLastError(ex.getMessage());
        }
        double dValue = tempBinary.doubleValue();
        if (dValue != 0)
            dValue = dValue / 100;
        return this.setValue(dValue, bDisplayOption, iMoveMode);
    }
}
