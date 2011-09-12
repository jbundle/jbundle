/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SPopupBox.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * A screen popup box.
 * Note: When the user changes the popup box, the converter is changed to match the record's reference.
 * Because of buffering and caching, there is no guarantee of what record is current in the referenced
 * record if this is a ReferenceField. ie., If you want:
 * <pre>
 * To have the reference in the record match the reference in the field, add new MoveOnChangeBehavavior().
 * To have the referenced record the current record, add ReadSecondaryHandler().
 * If you want a change in the currently referenced record to change this field, then add MoveOnChangeHandler().
 * </pre>
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SPopupBox extends ScreenField
{
    public static final int MAX_POPUP_ITEMS = 100; // ** HACK ** Increase this to 250

    /**
     * Constructor.
     */
    public SPopupBox()
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
    public SPopupBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Retrieve (in HTML format) from this field.
     * (Only for XML/HTML fields).
     * @param bDisplayFormat Display (with html codes) or Input format?
     * @param bRawData If true return the Raw data (not through the converters)?
     * @return The HTML string.
     */
    public String getSFieldValue(boolean bDisplayFormat, boolean bRawData)
    {
        if ((bDisplayFormat == false) && (bRawData == false))
            bRawData = true;    // If you want the input format, don't go through the converters.
//        if (bRawData)
            return super.getSFieldValue(bDisplayFormat, bRawData);
/*
        Converter converter = this.getConverter();
        if (converter == null)
            return Constants.BLANK;
        if (m_vDisplays == null)
        {
            String strField = null;
            if (converter != null) if (converter.getField() != null)
                strField = converter.getField().getString();
            this.scanTableItems();
            if (converter != null) if (converter.getField() != null)
                converter.getField().setString(strField);
        }
        String string = DBConstants.BLANK;
        int iIndex = 0;
        if (converter != null)
        {   // This is required for the display or popup fields because displayField() is never called to get the value.
            iIndex = converter.convertFieldToIndex();
            if (iIndex == -1)
                return super.getSFieldValue(bDisplayFormat, bRawData);
            try {
                string = (String)m_vDisplays.get(iIndex);
            } catch (ArrayIndexOutOfBoundsException ex) {
                string = " ";
            }
        }
        return string;
 */
    }
    /**
     * Requery the recordset.
     */
    public void reSelectRecords()
    {
        this.getScreenFieldView().reSelectRecords();
    }
}
