/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CopyFieldHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * When this field changes, copy it to a destination field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CopyFieldHandler extends MoveOnChangeHandler
{
    /**
     * The field sequence in this record to move this listener's field to.
     */
	protected int m_iFieldSeq = -1;
	protected String fieldName = null;
    /**
     * Only move if this field evaluates to true.
     */
	protected Converter m_convCheckMark = null;

    /**
     * Constructor.
     */
    public CopyFieldHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param iFieldSeq The field sequence in this record to move this listener's field to.
     */
    public CopyFieldHandler(int iFieldSeq)
    {
        this();
        this.init(null, null, null, iFieldSeq, null);
    }
    /**
     * Constructor.
     * @param iFieldSeq The field sequence in this record to move this listener's field to.
     */
    public CopyFieldHandler(String fieldName)
    {
        this();
        this.init(null, null, null, -1, fieldName);
    }
    /**
     * Constructor.
     * @param field The field to move this listener's field to.
     * @param fldCheckMark Only move if this field evaluates to true.
     */
    public CopyFieldHandler(BaseField field)
    {
        this();
        this.init(null, field, null, -1, null);
    }
    /**
     * Constructor.
     * @param field The field to move this listener's field to.
     * @param fldCheckMark Only move if this field evaluates to true.
     */
    public CopyFieldHandler(BaseField field, Converter fldCheckMark)
    {
        this();
        this.init(null, field, fldCheckMark, -1, null);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param iFieldSeq The field sequence in this record to move this listener's field to.
     * @param field The field to move this listener's field to.
     * @param fldCheckMark Only move if this field evaluates to true.
     */
    public void init(BaseField field, BaseField fldDest, Converter fldCheckMark, int iFieldSeq, String fieldName)
    {
        BaseField fldSource = null;
        boolean bClearIfThisNull = true;
        boolean bOnlyIfDestNull = false;
        boolean bDontMoveNull = false;
        super.init(field, fldDest, fldSource, bClearIfThisNull, bOnlyIfDestNull, bDontMoveNull);
        this.setRespondsToMode(DBConstants.INIT_MOVE, false); // By default, only respond to screen and init moves
        this.setRespondsToMode(DBConstants.READ_MOVE, false);   // Usually, you only want to move a string on screen change

        m_iFieldSeq = iFieldSeq;
        this.fieldName = fieldName;
        m_convCheckMark = fldCheckMark;
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        if (owner != null)
        if (m_fldDest == null)
        {
            if (m_iFieldSeq != -1)
                m_fldDest = ((BaseField)owner).getRecord().getField(m_iFieldSeq);
            if (fieldName != null)
                m_fldDest = ((BaseField)owner).getRecord().getField(fieldName);
        }
        super.setOwner(owner);
    }
    /**
     * Do the physical move operation.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int moveIt(boolean bDisplayOption, int iMoveMode)
    {
        if (m_convCheckMark != null) if (m_convCheckMark.getState() == false)
            return DBConstants.NORMAL_RETURN;       // If check mark is false (or no check mark), don't move
        return super.moveIt(bDisplayOption, iMoveMode);
    }
}
