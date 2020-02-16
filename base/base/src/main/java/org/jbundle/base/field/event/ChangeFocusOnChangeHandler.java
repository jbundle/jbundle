/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)ChangeFocusOnChangeHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.screen.ScreenComponent;

/**
 * If this field changes, Change the screen focus to another field's screen control.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ChangeFocusOnChangeHandler extends FieldListener
{
    /**
     * The screen field to change the focus to if this field changes.
     */
    protected ScreenComponent m_screenField = null;
    /**
     * The field to change to focus to is this field changes.
     */
    protected BaseField m_fldTarget = null;
    /**
     * When to change focus (true = if null, false = if not null, null = always).
     */
    protected Boolean m_bChangeIfNull = null;

    /**
     * Constructor.
     */
    public ChangeFocusOnChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldTarget The field to change the focus to on change to this field.
     */
    public ChangeFocusOnChangeHandler(BaseField fldTarget)
    {
        this();
        this.init(null, null, fldTarget);
    }
    /**
     * Constructor.
     * @param screenField The screen field to change the focus to on change to this field.
     */
    public ChangeFocusOnChangeHandler(ScreenComponent screenField)
    {
        this();
        this.init(null, screenField, null);
    }
    /**
     * Constructor.
     * This listener only responds to screen moves by default.
     * @param field The field to change the focus to on change to this field.
     * @param screenField The screen field to change the focus to on change to this field.
     */
    public void init(BaseField field, ScreenComponent screenField, BaseField fldTarget)
    {
        super.init(field);
        m_screenField = screenField;
        m_fldTarget = fldTarget;

        m_bScreenMove = true;   // Only respond to user change
        m_bInitMove = false;
        m_bReadMove = false;
        if (screenField == null)
            this.lookupSField();
    }
    /**
     * Set this cloned listener to the same state at this listener.
     * @param field The field this new listener will be added to.
     * @param listener The new listener to sync to this.
     * @param bInitCalled Has the init method been called?
     * @return True if I called init.
     */
    public boolean syncClonedListener(BaseField field, FieldListener listener, boolean bInitCalled)
    {
        if (!bInitCalled)
            ((ChangeFocusOnChangeHandler)listener).init(null, m_screenField, m_fldTarget);
        bInitCalled = super.syncClonedListener(field, listener, true);
        ((ChangeFocusOnChangeHandler)listener).setChangeFocusIfNull(m_bChangeIfNull);
        return bInitCalled;
    }
    /**
     * The Field has Changed.
     * Change to focus to the target field.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        if (m_screenField == null)
            this.lookupSField();
        if (m_bChangeIfNull != null)
        {
            if ((m_bChangeIfNull.booleanValue()) && (!this.getOwner().isNull()))
                return DBConstants.NORMAL_RETURN;
            if ((!m_bChangeIfNull.booleanValue()) && (this.getOwner().isNull()))
                return DBConstants.NORMAL_RETURN;
        }
        if (m_screenField != null)
            m_screenField.requestFocus();
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Lookup the screen field from the field passed in the constructor.
     */
    public void lookupSField()
    {
        m_screenField = m_fldTarget.getComponent(0); // See if you can get the first screen field
    }
    /**
     * When to change focus.
     * param bChangeIfNull (true = if null, false = if not null.
     */
    public void setChangeFocusIfNull(boolean bChangeIfNull)
    {
        m_bChangeIfNull = new Boolean(bChangeIfNull);
    }
}
