/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CheckMoveHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;

/**
 * When this listener's check mark is set,
 * move the source to the destination field.
 * Also disables the destination field when this listener's field
 * is set to true.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CheckMoveHandler extends MoveOnChangeHandler
{

    /**
     * Constructor.
     */
    public CheckMoveHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldDest The destination field.
     * @param fldSource The source field.
     */
    public CheckMoveHandler(BaseField fldDest, BaseField fldSource)
    {
        this();
        this.init(null, fldDest, fldSource);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fldDest The destination field.
     * @param fldSource The source field.
     */
    public void init(BaseField field, BaseField fldDest, BaseField fldSource)
    {
        super.init(field, fldDest, fldSource, false, false, false);
    }
    /**
     * The Field has Changed.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int moveIt(boolean bDisplayOption, int iMoveMode)
    {
        ((BaseField)m_fldDest.getField()).setEnabled(!this.getOwner().getState());
        if (iMoveMode != DBConstants.SCREEN_MOVE)
            return DBConstants.NORMAL_RETURN;
        if (this.getOwner().getState() == false)
        {
            if (bDisplayOption)
                ((BaseField)m_fldDest.getField()).displayField();   // Redisplay
            return DBConstants.NORMAL_RETURN;
        }
        else
            return super.moveIt(bDisplayOption, iMoveMode); // Move it!
    }
}
