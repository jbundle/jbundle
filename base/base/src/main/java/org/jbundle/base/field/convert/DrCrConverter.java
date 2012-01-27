/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)DrCrConverter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 * Convert this numberfield to a debit or a credit.
 * If you specify debit, only positive numbers are displayed, if credit
 * the (absolute) value is displayed if it is negative.
 * Note: The next converter must be a NumberField.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DrCrConverter extends FieldConverter
{
    /**
     * True means format this field as a debit.
     */
    protected boolean m_bDebit = false;
    /**
     * Debit description.
     */
    protected String m_strDebit = "Debit";
    /**
     * Credit description.
     */
    protected String m_strCredit = "Credit";

    /**
     * Constructor.
     */
    public DrCrConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param bDebit If true, output this field if it is positive.
     */
    public DrCrConverter(Converter converter, boolean bDebit)
    {
        this();
        this.init(converter, bDebit);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param bDebit If true, output this field if it is positive.
     */
    public void init(Converter converter, boolean bDebit)
    {
        m_bDebit = bDebit;
        super.init(converter);
    }
    /**
     * Get the field description.
     * Returns Debit or Credit.
     * @return The field description.
     */
    public String getFieldDesc() 
    {
        if (m_bDebit)
            return m_strDebit;
        else
            return m_strCredit;
    }
    /**
     * Retrieve (in string format) from this field.
     * @return The string.
     */
    public String getString() 
    {
        String string = super.getString();  // By default, get the data as-is
        if (this.getField() instanceof NumberField)
        {
            double dValue = this.getField().getValue();
            if (m_bDebit)
            {
                if (dValue < 0)
                    string = Constants.BLANK; // For debit/negative, return a null
            }
            else
            {   // Credit
                if (dValue >= 0)
                    string = Constants.BLANK; // For credit/positive, return a null
                else
                    string = ((NumberField)this.getField()).binaryToString(new Double(-dValue));  // Positive representation
            }
        }
        return string;
    }
    /**
     * Convert and move string to this field.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setString(String string, boolean bDisplayOption, int moveMode)               // init this field override for other value
    {
        NumberField field = null;
        if (this.getField() != null) if (this.getField() instanceof NumberField)
            field = (NumberField)this.getField();
        if (field == null)
            return super.setString(string, bDisplayOption, moveMode); // Convert to internal rep and return
        if ((string == null) || (string.length() == 0))
        {   // Don't accept null as input
            if (bDisplayOption)
                field.displayField();
            return DBConstants.NORMAL_RETURN;   // Must type a zero, if you want a zero
        }
        if (m_bDebit)
            return super.setString(string, bDisplayOption, moveMode); // Convert to internal rep and return
        else
        {   // Credit
            try   {
                Object tempBinary = field.stringToBinary(string);
                double dValue = ((Double)tempBinary).doubleValue();
                tempBinary = new Double(-dValue); // Positive representation
                return field.setData(tempBinary, bDisplayOption, moveMode);
            } catch (Exception ex)  {
                String strError = ex.getMessage();
                if (strError == null)
                    strError = ex.getClass().getName();
                Task task = null;
                if (this.getField() != null)
                    if (((BaseField)this.getField()).getRecord() != null)
                        if (((BaseField)this.getField()).getRecord().getRecordOwner() != null)
                            task = ((BaseField)this.getField()).getRecord().getRecordOwner().getTask();
                if (task == null)
                    task = BaseApplet.getSharedInstance();
                return task.setLastError(strError);
            }
        }
    }
    /**
     * Set the Debit description.
     */
    public void setDebitDesc(String strDebit)
    {
        m_strDebit = strDebit;
    }
    /**
     * Set the Credit description.
     */
    public void setCreditDesc(String strCredit)
    {
        m_strCredit = strCredit;
    }
}
