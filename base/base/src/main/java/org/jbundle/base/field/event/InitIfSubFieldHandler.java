/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)InitOnceFieldHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;

/**
 * If there is no SubFileFilter, then don't allow the field to be inited.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class InitIfSubFieldHandler extends FieldListener
{

    /**
     * Constructor.
     */
    public InitIfSubFieldHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public InitIfSubFieldHandler(BaseField field)
    {
        this();
        this.init(field);
    }
    /**
     * Constructor.
     * Only responds to init changes.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field)
    {
        super.init(field);
        m_bScreenMove = false;
        m_bInitMove = true;     // Only respond to init
        m_bReadMove = false;
    }
    /**
     * Move the physical binary data to this field.
     * If there is not SubFileFilter, then don't allow the field to be inited.
     * @param objData the raw data to set the basefield to.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int doSetData(Object fieldPtr, boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        boolean bSubExists = (this.getOwner().getRecord().getListener(SubFileFilter.class.getName()) != null);
        if (bSubExists)
            iErrorCode = super.doSetData(fieldPtr, bDisplayOption, iMoveMode);
        return iErrorCode;
    }
}
