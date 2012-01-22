/*

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)RadioConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * Set field to this target string if radio button is set.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RadioConverter extends FieldDescConverter
{
    /**
     * If the radio button is set, set this converter to this target string.
     */
    protected Object m_objTarget = null;
    /**
     * If true, sets value on setState(true), otherwise sets it to blank.
     */
    protected boolean m_bTrueIfMatch = false;

    /**
     * Constructor.
     */
    public RadioConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param strTarget If the radio button is set, set this converter to this target string.
     * @param bTrueIfMatch If true, sets value on setState(true), otherwise sets it to blank.
     */
    public RadioConverter(Converter converter, Object objTarget, boolean bTrueIfMatch)
    {
        this();
        this.init(converter, objTarget, bTrueIfMatch);
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param strTarget If the radio button is set, set this converter to this target string.
     * @param bTrueIfMatch If true, sets value on setState(true), otherwise sets it to blank.
     */
    public void init(Converter converter, Object objTarget, boolean bTrueIfMatch)
    {
        super.init(converter, null, null);
        m_objTarget = objTarget;
        m_bTrueIfMatch = bTrueIfMatch;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_objTarget = null;
    }
    /**
     * For binary fields, return the current state.
     * @param True is this field is true.
     */
    public boolean getState()
    {
        boolean returnValue = true;
        if (m_objTarget instanceof String)
        {
            String fieldString = this.getString();
            returnValue = m_objTarget.equals(fieldString); // True if == target value
        }
        else
        {
            Object fieldString = this.getData();
            returnValue = m_objTarget.equals(fieldString); // True if == target value
        }
        if (m_bTrueIfMatch)
            return returnValue;
        else
            return !returnValue;
    }
    /**
     * For binary fields, set the current state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setState(boolean bState, boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (bState)      // Only if turned ON (can't turn a radio button off!)
        {
            if (m_objTarget instanceof String)
            {
                if (m_bTrueIfMatch)
                    iErrorCode = this.setString((String)m_objTarget, bDisplayOption, iMoveMode);
                else
                {
                    String thisString = this.getString();
                    if (m_objTarget.equals(thisString)) // If == target, clear
                        iErrorCode = this.setString(Constants.BLANK, bDisplayOption, iMoveMode);
                }
            }
            else
            {
                if (m_bTrueIfMatch)
                    iErrorCode = this.setData(m_objTarget, bDisplayOption, iMoveMode);
                else
                {
                    Object thisString = this.getData();
                    if (m_objTarget.equals(thisString)) // If == target, clear
                        iErrorCode = this.setData(null, bDisplayOption, iMoveMode);
                }
            }
        }
        return iErrorCode;
    }
}
