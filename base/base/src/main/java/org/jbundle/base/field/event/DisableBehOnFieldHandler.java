/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)DisableBehOnFieldHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BaseListener;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;

/**
 * Disable the listener when this field is set to the target value.
 * You will have to manually do the garbage collection... Be careful!!!
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DisableBehOnFieldHandler extends FieldListener
{
    /**
     * Listener to disable on string match.
     */
    protected BaseListener m_listenerToDisable = null;
    /**
     * String to match to determine whether to disable the listener.
     */
    protected String m_strDisableOnMatch = null;
    /**
     * If true disable if match, else disable if not matched.
     */
    protected boolean m_bDisableIfMatch = true;

    /**
     * Constructor.
     */
    public DisableBehOnFieldHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param listenerToDisable Listener to disable on string match.
     * @param strDisableOnMatch String to match to determine whether to disable the listener.
     * @param bDisableIfMatch If true disable if match, else disable if not matched.
     */
    public DisableBehOnFieldHandler(BaseListener listenerToDisable, String strDisableOnMatch, boolean bDisableIfMatch)
    {
        this();
        this.init(null, listenerToDisable, strDisableOnMatch, bDisableIfMatch);
    }
    /**
     * Constructor.
     * @param listenerToDisable Listener to disable on string match.
     * @param strDisableOnMatch String to match to determine whether to disable the listener.
     * @param bDisableIfMatch If true disable if match, else disable if not matched.
     */
    public DisableBehOnFieldHandler(BaseListener listenerToDisable, String strDisableOnMatch)
    {
        this();
        this.init(null, listenerToDisable, strDisableOnMatch, true);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field, BaseListener listenerToDisable,  String strDisableOnMatch,  boolean bDisableIfMatch)
    {
        super.init(field);
        m_listenerToDisable = listenerToDisable;
        m_strDisableOnMatch = strDisableOnMatch;
        m_bDisableIfMatch = bDisableIfMatch;
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
            this.fieldChanged(DBConstants.DONT_DISPLAY, DBConstants.INIT_MOVE);
    }
    /**
     * Creates a new object of the same class as this object.
     * @param field The field to add the new cloned behavior to.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone(BaseField field) throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException(); // For now
    }
    /**
     * The Field has Changed.
     * Enable/disable the target listener if this field now matches the target string.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int moveMode)
    {
        boolean flag = !(this.getOwner().getString().equals(m_strDisableOnMatch));
        if (!m_bDisableIfMatch)
            flag = !flag;
        m_listenerToDisable.setEnabledListener(flag);
        return DBConstants.NORMAL_RETURN;
    }
}
