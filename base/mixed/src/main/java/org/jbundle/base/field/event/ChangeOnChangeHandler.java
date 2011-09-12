/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)InitOnChangeHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.event.FileRemoveBOnCloseHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.util.DBConstants;

/**
 * When this field is changed, trigger change on the dest field.
 * This class is typically used to trigger a listener on another field when
 * this field changes.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ChangeOnChangeHandler extends FieldListener
{
    /**
     * Field to initialize on change (to the owner's field).
     */
    protected BaseField m_fldTarget = null;
    /**
     * 
     */
    protected boolean m_bSetModified = false;

    /**
     * Constructor.
     */
    public ChangeOnChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldTarget Field to initialize on change (to the owner's field).
     */
    public ChangeOnChangeHandler(BaseField fldTarget)
    {
        this();
        this.init(null, fldTarget, false);
    }
    /**
     * Constructor.
     * @param fldTarget Field to initialize on change (to the owner's field).
     */
    public ChangeOnChangeHandler(BaseField fldTarget, boolean bSetModified)
    {
        this();
        this.init(null, fldTarget, bSetModified);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fldTarget Field to initialize on change (to the owner's field).
     */
    public void init(BaseField field, BaseField fldTarget, boolean bSetModified)
    {
        super.init(field);
        m_fldTarget = fldTarget;
        m_bSetModified = bSetModified;

        this.setRespondsToMode(DBConstants.INIT_MOVE, false);
        this.setRespondsToMode(DBConstants.READ_MOVE, false);
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (this.getOwner() != null)
        {
            if (m_fldTarget.getRecord() != this.getOwner().getRecord())
                m_fldTarget.getRecord().addListener(new FileRemoveBOnCloseHandler(this));   // Not same file, if target file closes, remove this listener!
        }
    }
    /**
     * Set this cloned listener to the same state at this listener.
     * @param field The field this new listener will be added to.
     * @param The new listener to sync to this.
     * @param Has the init method been called?
     * @return True if I called init.
     */
    public boolean syncClonedListener(BaseField field, FieldListener listener, boolean bInitCalled)
    {
        if (!bInitCalled)
            ((ChangeOnChangeHandler)listener).init(null, m_fldTarget, m_bSetModified);
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * The Field has Changed.
     * Initialize the target field also.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * Field Changed, init the field.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        if (m_bSetModified)
            m_fldTarget.setModified(true);
        return m_fldTarget.handleFieldChanged(bDisplayOption, iMoveMode); // init dependent field
    }
}
