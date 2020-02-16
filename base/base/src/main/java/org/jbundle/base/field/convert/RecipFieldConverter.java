/*

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)RecipFieldConverter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.text.NumberFormat;

import org.jbundle.base.field.NumberField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Constants;


/**
 * RecipFieldConverter - Convert number to recriprical.
 * 10 is converted to 0.10, and vis-a-versa.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RecipFieldConverter extends FieldConverter
{
    protected static NumberFormat m_mfRecip = null;

    /**
     * Constructor.
     */
    public RecipFieldConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param numberField The converter to display the recriprical of.
     */
    public RecipFieldConverter(NumberField numberField)
    {
        this();
        this.init(numberField);
    }
    /**
     * Retrieve (in string format) from this field.
     * @return The string of the recriprical of this field's value.
     */
    public String getString() 
    {
        String string = Constants.BLANK;
        NumberField numberField = (NumberField)this.getField();
        if (this.getNextConverter().getLength() != 0)
        {
            double dValue = this.getValue();
            if (dValue != 0)
                dValue = 1 / dValue;
            if (m_mfRecip == null)
                m_mfRecip = NumberFormat.getInstance();
            m_mfRecip.setMinimumFractionDigits(numberField.getScale());
            m_mfRecip.setMaximumFractionDigits(numberField.getScale());
            string = m_mfRecip.format(dValue);
        }
        return string;
    }
    /**
     * Convert and move string to this field.
     * Get the recriprical of this string and set the string.
     * @param strField the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString( String strField, boolean bDisplayOption, int iMoveMode)
    {
        NumberField numberField = (NumberField)this.getNextConverter();
        int iErrorCode = super.setString(strField, DBConstants.DONT_DISPLAY, iMoveMode);
        if (strField.length() == 0)
            numberField.displayField();     // Special Case (because we return immediately)
        if ((iErrorCode != DBConstants.NORMAL_RETURN) || strField.length() == 0)
            return iErrorCode;
        double doubleValue = this.getValue();
        if (doubleValue != 0)
            doubleValue = 1 / doubleValue;
        iErrorCode = this.setValue(doubleValue, bDisplayOption, DBConstants.SCREEN_MOVE);
        return iErrorCode;
    }
}
