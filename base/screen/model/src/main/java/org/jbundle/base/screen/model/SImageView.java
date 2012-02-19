/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SImageView.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 jbundle.org. All Rights Reserved.
 *      don@tourgeek.com
 */


import java.util.Map;

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * Image display.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SImageView extends ScreenField
{
    /**
     * The image dimensions.
     */
    protected int m_widthImage = 0;
    protected int m_heightImage = 0;
    /**
     * If you want to use this as a button, set a command!
     */
    protected String m_strCommand = null;

    /**
     * Constructor.
     */
    public SImageView()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public SImageView(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties TODO
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set the size of all images (If they are known and are the same).
     */
    public void setImageWidth(int dimImage)
    {
        m_widthImage = dimImage;
    }
    /**
     * Set the size of all images (If they are known and are the same).
     */
    public void setImageHeight(int dimImage)
    {
        m_heightImage = dimImage;
    }
    /**
     * Set the size of all images (If they are know and are the same).
     */
    public int getImageWidth()
    {
        return m_widthImage;
    }
    /**
     * Set the size of all images (If they are know and are the same).
     */
    public int getImageHeight()
    {
        return m_heightImage;
    }
    /**
     * Set the size of all images (If they are know and are the same).
     */
    public void setButtonCommand(String strCommand)
    {
        m_strCommand = strCommand;
    }
    /**
     * Set the size of all images (If they are know and are the same).
     */
    public String getButtonCommand()
    {
        return m_strCommand;
    }
}
