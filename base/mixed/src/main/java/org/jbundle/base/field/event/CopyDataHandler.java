package org.jbundle.base.field.event;

/**
 * @(#)CopyStringHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * When this field changes, copy this string to a new field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CopyDataHandler extends CopyFieldHandler
{
    /**
     * The string to set the destination field to.
     */
    protected Object m_objValue = null;

    /**
     * Constructor.
     */
    public CopyDataHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldDest The destination field.
     * @param stringValue The string to set the destination field to.
     * @param convconvCheckMark If this evaluates to false, don't do the move.
     */
    public CopyDataHandler(BaseField fldDest, Object objValue, Converter convCheckMark)
    {
        this();
        this.init(null, fldDest, objValue, convCheckMark);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fldDest The destination field.
     * @param stringValue The string to set the destination field to.
     * @param convconvCheckMark If this evaluates to false, don't do the move.
     */
    public void init(BaseField field, BaseField fldDest, Object objValue, Converter convCheckMark)
    {
        super.init(field, fldDest, convCheckMark, -1);
        m_bClearIfThisNull = false; // Must be for this to work
        this.setRespondsToMode(DBConstants.READ_MOVE, false);   // Usually, you only want to move a string on screen change
        this.setRespondsToMode(DBConstants.INIT_MOVE, false);   // Usually, you only want to move a string on screen change
        m_objValue = objValue;
    }
    /**
     * Do the physical move operation.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int moveSourceToDest(boolean bDisplayOption, int iMoveMode)
    {
        if (m_objValue instanceof String)
            return m_fldDest.setString((String)m_objValue, bDisplayOption, iMoveMode);
        else
            return m_fldDest.setData(m_objValue, bDisplayOption, iMoveMode);
    }
}
