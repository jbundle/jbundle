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

/**
 * When this field is changed, init the target field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class InitOnChangeHandler extends FieldListener
{
    /**
     * Field to initialize on change (to the owner's field).
     */
    protected BaseField m_fldTarget = null;

    /**
     * Constructor.
     */
    public InitOnChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldTarget Field to initialize on change (to the owner's field).
     */
    public InitOnChangeHandler(BaseField fldTarget)
    {
        this();
        this.init(null, fldTarget);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fldTarget Field to initialize on change (to the owner's field).
     */
    public void init(BaseField field, BaseField fldTarget)
    {
        super.init(field);
        m_fldTarget = fldTarget;
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
        {
            BaseField fldTarget = this.getSyncedListenersField(m_fldTarget, listener);
            ((InitOnChangeHandler)listener).init(null, fldTarget);
        }
        return super.syncClonedListener(field, listener, true);
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
     * The Field has Changed.
     * Initialize the target field also.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * Field Changed, init the field.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        return m_fldTarget.initField(bDisplayOption); // init dependent field
    }
}
