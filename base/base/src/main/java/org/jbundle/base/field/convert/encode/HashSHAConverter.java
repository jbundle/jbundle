/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert.encode;

/**
 * @(#)PercentConverter.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.security.NoSuchAlgorithmException;

import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.base64.Base64;


/**
 * Convert number to a SHA hash... but not back.
 * This simply displays 10 asterisks on convert back.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class HashSHAConverter extends Base64Converter
{

    /**
     * Constructor.
     */
    public HashSHAConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param numberField The percent field target.
     */
    public HashSHAConverter(Converter converter)
    {
        this();
        this.init(converter, true);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param iFakeLength The maximum field length to return.
     */
    public void init(Converter converter, boolean bDoBase64Conversion)
    {
        super.init(converter, bDoBase64Conversion);
    }
    /**
     * Retrieve (in string format) from this field.
     * @return This field as a percent string.
     */
    public String getString() 
    {
        String string = super.getString();
        if ((string != null) && (string.length() > 0))
            string = TEN_SPACES;
        return string;
    }
    public static final String TEN_SPACES = "          ";
    /**
     * Convert and move string to this field.
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String strValue, boolean bDisplayOption, int iMoveMode)
    {
        if ((strValue == null) || (strValue.length() == 0))
            return super.setString(strValue, bDisplayOption, iMoveMode);  // Don't trip change or display
        if (TEN_SPACES.equals(strValue))
            return DBConstants.NORMAL_RETURN;
        return super.setString(strValue, bDisplayOption, iMoveMode);
    }
    /**
     * Encode the byte string - OVERRIDE this.
     * @param rgbValue
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] encodeBytes(byte[] rgbValue)
        throws NoSuchAlgorithmException
    {
        rgbValue = Base64.encodeSHA(rgbValue);
        rgbValue = super.encodeBytes(rgbValue); // Base64 encoding
        return rgbValue;
    }
}
