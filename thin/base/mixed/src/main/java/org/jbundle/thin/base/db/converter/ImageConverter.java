package org.jbundle.thin.base.db.converter;

/**
 * @(#)FieldConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import javax.swing.ImageIcon;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.util.LinkedConverter;
import org.jbundle.thin.base.screen.util.SerializableImage;

/**
 * The base converter for fields.
 * The class maintains the converter chain.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ImageConverter extends LinkedConverter
{

    /**
     * Constructor.
     */
    public ImageConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     */
    public ImageConverter(Converter converter)
    {
        this();
        this.init(converter);
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
     * Free this converter.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the data on the end of this converter chain.
     * @return The raw data.
     */
    public Object getData() 
    {
        Object objValue = super.getData();
        ImageIcon image = this.convertValueToImage(objValue);
        return image;
    }
    /**
     * For binary fields, set the current state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setData(Object objValue, boolean bDisplayOption, int iMoveMode)
    {       // Must be overidden
        return Constants.NORMAL_RETURN;
//x        return super.setData(objValue, bDisplayOption, iMoveMode);
    }
    /**
     * Set the value.
     * @param objValue The raw data (a Boolean).
     */
    public ImageIcon convertValueToImage(Object objValue)
    {
        ImageIcon image = null;
        if (objValue instanceof ImageIcon)
            image = (ImageIcon)objValue;
        else if (objValue instanceof SerializableImage)
            image = new ImageIcon(((SerializableImage)objValue).getImage());
        else
        {
            String strImage = this.convertValueToImageName(objValue);
            if (strImage != null)
                image = BaseApplet.getSharedInstance().loadImageIcon(strImage, null);
        }
        return image;
    }
    /**
     * Set the value.
     * Typically, you override this.
     * @param objValue The raw data (a Boolean).
     */
    public String convertValueToImageName(Object objValue)
    {
        if (objValue instanceof String)
            return (String)objValue;
        return null;
    }
}
