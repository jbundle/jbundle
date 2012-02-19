/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)QueryBitmapConverter.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.MenuConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;


/**
 * QueryBitmapConverter - return the bitmap ID for this table.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class QueryBitmapConverter extends FieldConverter
{
    /**
     * The record to get the bitmap from.
     */
    protected Record m_recTarget = null;

    /**
     * Constructor.
     */
    public QueryBitmapConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param recTarget The record to get the bitmap from.
     */
    public QueryBitmapConverter(Record recTarget)
    {
        this();
        this.init(recTarget);
    }
    /**
     * Initialize this object.
     * @param recTarget The record to get the bitmap from.
     */
    public void init(Record recTarget)
    {
        super.init(null);
        m_recTarget = recTarget;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_recTarget = null;
    }
    /**
     * Get the image for Buttons and Bitmaps and drag cursors.
     * (Previously getBitmap).
     * Return The bitmap for the current table.
     */
    public String getBitmap()
    {
        if (m_recTarget != null)
        {
            Record recTarget = null;
            if (m_recTarget.getBaseRecord().getTable().getCurrentTable() != null)
                recTarget = m_recTarget.getBaseRecord().getTable().getCurrentTable().getRecord();
            if (recTarget != null)
                return recTarget.getBitmap();   // Bitmap for this table
        }
        return super.getBitmap(); // Default value
    }
    /**
     * Retrieve (in string format) from this field.
     * Never called in this class.
     */
    public String getString()
    {
        return null;
    }
    /**
     * Convert and move string to this field.
     * Never called in this class.
     */
    public int setString(String fieldPtr, boolean bDisplayOption, int moveMode)                 // init this field override for other value
    {
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Set up the default control for this field.
     * A SCannedBox for a query bitmap converter.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        properties = new HashMap<String,Object>();
        properties.put(ScreenModel.COMMAND, MenuConstants.FORMLINK);
        properties.put(ScreenModel.IMAGE, MenuConstants.FORM);
        return BaseField.createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, iDisplayFieldDesc, properties);
    }
}
