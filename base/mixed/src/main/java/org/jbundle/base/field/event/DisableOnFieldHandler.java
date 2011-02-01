package org.jbundle.base.field.event;

/**
 * @(#)DisableOnFieldHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.util.DBConstants;

/**
 * Disable the supplied field when this field is set to the target value.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DisableOnFieldHandler extends FieldListener
{
    /*
     * The field to disable when this listener's owner matches the target string.
     */
    protected BaseField m_fldToDisable = null;
    /*
     * The string to compare to this listener's owner.
     */
    protected String m_strCompareString = null;
    /*
     * If true, disables if the string matches, if false, enables on match.
     */
    protected boolean m_bDisableIfMatch = true;

    /**
     * Constructor.
     */
    public DisableOnFieldHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fieldToDisable The field to disable when this listener's owner matches the target string.
     * @param strCompareString The string to compare to this listener's owner.
     * @param bDisableIfMatch If true, disables if the string matches, if false, enables on match.
     */
    public DisableOnFieldHandler(BaseField fieldToDisable, String strCompareString, boolean bDisableIfMatch)
    {
        this();
        this.init(null, fieldToDisable, strCompareString, bDisableIfMatch);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fieldToDisable The field to disable when this listener's owner matches the target string.
     * @param strCompareString The string to compare to this listener's owner.
     * @param bDisableIfMatch If true, disables if the string matches, if false, enables on match.
     */
    public void init(BaseField field, BaseField fieldToDisable, String strCompareString, boolean bDisableIfMatch)
    {
        super.init(field);
        m_fldToDisable = fieldToDisable;
        m_strCompareString = strCompareString;
        m_bDisableIfMatch = bDisableIfMatch;
        if (m_fldToDisable != null)
            m_fldToDisable.addListener(new FieldRemoveBOnCloseHandler(this)); // Remove this listener if closed
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
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null) if (m_fldToDisable != null)
            if (this.getOwner().getRecord() != m_fldToDisable.getRecord())
                m_fldToDisable.addListener(new FieldRemoveBOnCloseHandler(this)); // Make sure you remove this listener if m_fldToDisable is gone.
        if (owner != null)
            if (m_fldToDisable == null)
                m_fldToDisable = this.getOwner();
        if (owner != null)
            this.fieldChanged(DBConstants.DONT_DISPLAY, DBConstants.INIT_MOVE);
        if (owner == null)
            m_fldToDisable.setEnabled(true);    // Remove the behavior = reenable the field
    }
    /**
     * The Field has Changed.
     * If the target string matches this field, disable the target field.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * Disable field if criteria met.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        boolean bFlag = this.compareFieldToString();
        if (m_bDisableIfMatch)
            bFlag = !bFlag;
        m_fldToDisable.setEnabled(bFlag);
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Compare the field to the string.
     * @return true if match.
     */
    public boolean compareFieldToString()
    {
        if (m_strCompareString == null)
            return (this.getOwner().isNull());
        else
            return (this.getOwner().getString().equals(m_strCompareString));
    }
}
