package org.jbundle.base.field.event;

/**
 * @(#)FieldToUpperHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.field.BaseField;

/**
 * Make sure this field remains uppercase.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldToUpperHandler extends FieldListener
{

    /**
     * Constructor.
     */
    public FieldToUpperHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public FieldToUpperHandler(BaseField field)
    {
        this();
        this.init(field);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field)
    {
        super.init(field);
        m_bReadMove = false;  // Don't move on read!
    }
    /**
     * Move the physical binary data to this field.
     * @param objData the raw data to set the basefield to.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int doSetData(Object objData, boolean bDisplayOption, int iMoveMode)
    {
        if (objData instanceof String)
            objData = ((String)objData).toUpperCase();
        return super.doSetData(objData, bDisplayOption, iMoveMode);
    }
}
