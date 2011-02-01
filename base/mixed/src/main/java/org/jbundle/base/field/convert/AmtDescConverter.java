package org.jbundle.base.field.convert;

/**
 * @(#)AmtDescConverter.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.ResourceBundle;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * Convert iAmount to a description.
 * 23 would be converted to "Twenty Three".
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class AmtDescConverter extends FieldConverter
{
    public String[] NUMBERS = null;
    public String[] TEENS = null;
    public String[] TENS = null;
    public String HUNDRED = null;
    public String THOUSAND = null;
    public String MILLION = null;
    public String BILLION = null;
    public String AND = null;
    
    public String DOLLAR = null;
    public String CENT = null;
    public String PLURAL = null;

    /**
     * Constructor.
     */
    public AmtDescConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the chain (should be a number field).
     */
    public AmtDescConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the maximum length of this field.
     * @return The max length (86 characters).
     */
    public int getMaxLength() 
    {
        return 86;      // 86 characters
    }
    /**
     * Retrieve (in string format) from this field.
     * @return The description of the field.
     */
    public String getString() 
    {
        String string = null;
        if (this.getData() == null)
            return Constants.BLANK;
        double dAmount = this.getValue();

        this.initConstants();

        int iAmount, iHundreds, iTens, iOnes;
        string = Constants.BLANK;
        int iThousands = (int)((dAmount + 0.005) / 1000);
        int iRest = (int)(dAmount - iThousands * 1000 + 0.005);

        string = Constants.BLANK;
        if ((int)dAmount == 0)
            string = NUMBERS[0] + ' ';  // Zero
        else
        {
            for (int setup = 1; setup <= 2; setup++)
            {
                if (setup == 1)
                    iAmount = iThousands;
                else
                    iAmount = iRest;
                iHundreds = iAmount / 100;
                iTens = (iAmount - iHundreds * 100) / 10;
                iOnes = iAmount - iHundreds * 100 - iTens * 10;
                if (iHundreds != 0)
                    string += NUMBERS[iHundreds] + ' ' + HUNDRED + ' ';
                if (iTens * 10 + iOnes > 19)
                    string += TENS[iTens] + " ";
                else
                {
                    if (iTens == 1)
                    {
                        string += TEENS[iOnes] + " ";
                        iOnes = 0;
                    }
                }
                if (iOnes != 0)
                    string += NUMBERS[iOnes] + " ";
                if (setup == 1) if (iAmount != 0)
                    string += THOUSAND + ' ';
            }
        }
        if ((string.length() > 54) || (dAmount >= 1000000))
        {
            int strBin = (int)dAmount;
            string = Integer.toString(strBin);
            string += " ";
        }
        String centsString;
        int iCents = ((int)(dAmount - ((int)dAmount) + .001) * 100);
        centsString = Integer.toString(iCents);
        if (centsString.length() == 1)
            centsString = "0" + centsString;
        
        this.initCurrency();
        
        string = Constants.BLANK + string + DOLLAR;
        if ((dAmount > 1) || (dAmount == 0))
            string = string + PLURAL;
        string = string + ' ' + AND + ' ' + centsString + ' ' + CENT;
        if ((iCents > 1) || (iCents == 0))
            string = string + PLURAL;
        return string;
    }
    /**
     * Init the currency constants.
     */
    public void initCurrency()
    {
        if (DOLLAR != null)
            return;
        DOLLAR = "Dollar";
        CENT = "Cent";
        PLURAL = "s";
    }
    /**
     * Init the number constants.
     */
    public void initConstants()
    {
        if (NUMBERS != null)
            return;

        if (((BaseField)this.getField()).getRecord().getRecordOwner() != null)
        {
            org.jbundle.model.Task task = ((BaseField)this.getField()).getRecord().getRecordOwner().getTask();
            ResourceBundle resources = ((BaseApplication)task.getApplication()).getResources(ResourceConstants.AMOUNT_RESOURCE, true);
            NUMBERS = new String[10];
            for (int i = 0; i < 10; i++)
            {
                NUMBERS[i] = resources.getString(Integer.toString(i));
            }

            TEENS = new String[10];
            for (int i = 10; i < 20; i++)
            {
                TEENS[i - 10] = resources.getString(Integer.toString(i));
            }

            TENS = new String[10];
            for (int i = 2; i < 10; i++)
            {
                TENS[i] = resources.getString(Integer.toString(i * 10));
            }

            HUNDRED = resources.getString("100");
            THOUSAND = resources.getString("1000");
            MILLION = resources.getString("1000000");
            BILLION = resources.getString("1000000000");
            AND = resources.getString("&");
        }
        else
        {
            NUMBERS[0] = "Zero";NUMBERS[1] = "One";NUMBERS[2] = "Two";NUMBERS[3] = "Three";NUMBERS[4] = "Four";
            NUMBERS[5] = "Five";NUMBERS[6] = "Six";NUMBERS[7] = "Seven";NUMBERS[8] = "Eight";NUMBERS[9] = "Nine";
            TEENS[0] = "ten";TEENS[1] = "Eleven";TEENS[2] = "Twelve";TEENS[3] = "Thirteen";TEENS[4] = "Fourteen";
            TEENS[5] = "Fifteen";TEENS[6] = "Sixteen";TEENS[7] = "Seventeen";TEENS[8] = "Eighteen";TEENS[9] = "Nineteen";
            TENS[0] = Constants.BLANK;TENS[1] = Constants.BLANK;TENS[2] = "Twenty";TENS[3] = "Thirty";TENS[4] = "Forty";
            TENS[5] = "Fifty";TENS[6] = "Sixty";TENS[7] = "Seventy";TENS[8] = "Eighty";TENS[9] = "Ninety";
            HUNDRED = "Hundred";
            THOUSAND = "Thousand";
            MILLION = "Million";
            BILLION = "Billion";
            AND = "and";
        }
    }
}
