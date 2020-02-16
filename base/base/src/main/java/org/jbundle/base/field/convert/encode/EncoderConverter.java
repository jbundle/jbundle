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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.convert.FieldConverter;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.base64.Base64;


/**
 * EncoderConverter - Base class for encoders.
 * @version 1.0.0
 * @author    Don Corley
 */
public class EncoderConverter extends FieldConverter
{

    /**
     * Constructor.
     */
    public EncoderConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The percent field target.
     */
    public EncoderConverter(Converter field)
    {
        this();
        this.init(field);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     */
    public void init(Converter converter)
    {
        super.init(converter);
    }
    /**
     * Retrieve (in string format) from this field.
     * @return This field as a percent string.
     */
    public String getString() 
    {
        String string = super.getString();
        if ((string != null) && (string.length() > 0))
            string = this.decodeString(string);
        return string;
    }
    public String decodeString(String string)
    {
        if (string == null)
            return null;
        byte[] bytes = null;
        try {
          bytes = string.getBytes(Base64.DEFAULT_ENCODING);
        
          bytes = this.decodeBytes(bytes);
            
          return new String(bytes, Base64.DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException ignored) { return null; }        
        catch (NoSuchAlgorithmException ignored) { return null; }        
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
        return rgbValue;
    }
    /**
     * Convert and move string to this field.
     * @param strValue the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String strValue, boolean bDisplayOption, int iMoveMode)
    {        
        try {
            strValue = this.encodeString(strValue);
        } catch (Exception ex) {
            Task task = ((BaseField)this.getField()).getRecord().getRecordOwner().getTask();
            return task.setLastError(ex.getMessage());
        }
        return super.setString(strValue, bDisplayOption, iMoveMode);
    }
    /**
     * 
     * @param unencoded
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String encodeString(String unencoded)
        throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if ((unencoded != null) && (unencoded.length() > 0))
        {
            byte[] rgbValue = unencoded.getBytes();
            rgbValue = this.encodeBytes(rgbValue);
            unencoded = new String(rgbValue, Base64.DEFAULT_ENCODING);
        }
        return unencoded;
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
        return rgbValue;
    }
}
