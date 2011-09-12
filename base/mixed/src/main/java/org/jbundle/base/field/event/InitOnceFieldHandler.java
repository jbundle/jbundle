/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)InitOnceFieldHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.util.DBConstants;

/**
 * Initialize this field the first time, but leave it unchanged after that.
 * Unless you initialize to a new value.
 * WARNING: To work, this must be the FIRST listener in the chain.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class InitOnceFieldHandler extends FieldListener
{
    /**
     * First time flag.
     */
    protected boolean m_bFirstTime = true;

    /**
     * Constructor.
     */
    public InitOnceFieldHandler()
    {
        super();
        m_bScreenMove = false;
        m_bInitMove = true;     // Only respond to init
        m_bReadMove = false;
        m_bFirstTime = true;
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public InitOnceFieldHandler(BaseField field)
    {
        this();
        this.init(field, true);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public InitOnceFieldHandler(boolean bFirstTime)
    {
        this();
        this.init(null, bFirstTime);
    }
    /**
     * Constructor.
     * Only responds to init changes.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field, boolean bFirstTime)
    {
        super.init(field);
        m_bScreenMove = true;
        m_bInitMove = true;     // Only respond to init
        m_bReadMove = true;
        m_bFirstTime = bFirstTime;
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
        bInitCalled = super.syncClonedListener(field, listener, bInitCalled);
        ((InitOnceFieldHandler)listener).setFirstTime(m_bFirstTime);
        return bInitCalled;
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
            InitFieldHandler behavior = (InitFieldHandler)this.getOwner().getListener(InitFieldHandler.class.getName());
            if (this.getOwner().getRecord().getEditMode() == DBConstants.EDIT_CURRENT)
                behavior = null;    // Special case - if there is a current record, make sure initonce works.
            if (behavior != null)
                this.setFirstTime(false);  // If InitFieldHandler already set the initial value, don't set the first time.
        }
    }
    /**
     * Is this listener enabled flag set?
     * @return true if enabled.
     */
    public boolean isEnabled()
    {
//        if (m_bFirstTime)
//            return super.isEnabled();
        return true;    // Ignore... always enabled!
    }
    /**
     * Move the physical binary data to this field.
     * If this is an init set, only does it until the first change.
     * @param objData the raw data to set the basefield to.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int doSetData(Object objData, boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if ((m_bFirstTime) || (iMoveMode == DBConstants.READ_MOVE) || (iMoveMode == DBConstants.SCREEN_MOVE))
            iErrorCode = super.doSetData(objData, bDisplayOption, iMoveMode);
        m_bFirstTime = false; // No more Inits allowed
        return iErrorCode;
    }
    /**
     * Manually set the first time flag.
     * @param bFirstTime If true, this is the first time.
     */
    public void setFirstTime(boolean bFirstTime)
    {
        m_bFirstTime = bFirstTime;
    }
}
