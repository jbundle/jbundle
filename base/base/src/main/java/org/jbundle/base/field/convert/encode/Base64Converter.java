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

import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.base64.Base64;


/**
 * Convert number to a percent... and back.
 * This simply shifts the percentage point by 2 positions.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class Base64Converter extends EncoderConverter
{
    protected boolean m_bDoBase64Conversion = true;

    /**
     * Constructor.
     */
    public Base64Converter()
    {
        super();
    }
    /**
     * Constructor.
     * @param numberField The percent field target.
     */
    public Base64Converter(Converter field, boolean bDoBase64Conversion)
    {
        this();
        this.init(field, bDoBase64Conversion);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param iFakeLength The maximum field length to return.
     */
    public void init(Converter converter, boolean bDoBase64Conversion)
    {
        super.init(converter);
        m_bDoBase64Conversion = bDoBase64Conversion;
    }
    /**
     * Decode this array.
     * @param rgbValue
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] decodeBytes(byte[] rgbValue)
        throws NoSuchAlgorithmException
    {
        if (m_bDoBase64Conversion)
            return Base64.decode(rgbValue);
        else
            return rgbValue;
    }
    /**
     * Encode the byte string.
     * @param rgbValue
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] encodeBytes(byte[] rgbValue)
        throws NoSuchAlgorithmException
    {
        if (m_bDoBase64Conversion)
            return Base64.encodeToBytes(rgbValue);
        else
            return rgbValue;
    }
}
