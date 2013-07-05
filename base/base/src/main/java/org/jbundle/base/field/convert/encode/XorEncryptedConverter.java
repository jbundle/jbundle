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


/**
 * Convert does a rolling exclusive-or mask ... and back.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class XorEncryptedConverter extends Base64Converter
{

    /**
     * Constructor.
     */
    public XorEncryptedConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param numberField The percent field target.
     */
    public XorEncryptedConverter(Converter converter)
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
     * Encode the byte string - OVERRIDE this.
     * @param rgbValue
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] encodeBytes(byte[] rgbValue)
        throws NoSuchAlgorithmException
    {
        rgbValue = this.encrypt(rgbValue);
        rgbValue = super.encodeBytes(rgbValue); // Base64 encoding
        return rgbValue;
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
        rgbValue = super.decodeBytes(rgbValue); // Base64 encoding
        rgbValue = this.encrypt(rgbValue);
        return rgbValue;
    }

    public byte key = 'G';
    public byte[] encrypt(byte[] rgBytes)
    {
        return XorEncryptedConverter.encrypt(rgBytes, key);
    }
    /**
     * 
     * @param rgBytes
     * @param mask
     * @return
     */
    public static byte[] encrypt(byte[] rgBytes, byte mask)
    {
        for (int i = 0; i < rgBytes.length; i++)
        {
            rgBytes[i] = (byte)(rgBytes[i] ^ mask);
            mask = (byte)(mask + 7);
        }
        return rgBytes;
    }
}
