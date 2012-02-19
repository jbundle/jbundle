/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)MoveOnChangeHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.event.FileRemoveBOnCloseHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * When this field is changed, move source to dest.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MoveOnChangeHandler extends FieldListener
{
    /**
     * The destination field.
     */
    protected Converter m_fldDest = null;
    /**
     * The source field (If null, use this field).
     */
    protected Converter m_fldSource = null;
    /**
     * If this listener's owner is set to null, set the destination field to null.
     */
    protected boolean m_bClearIfThisNull = true;
    /**
     * Move only if the destination field is null.
     */
    protected boolean m_bOnlyIfDestNull = false;
    /**
     * Don't move if the source is null.
     */
    protected boolean m_bDontMoveNull = false;

    /**
     * Constructor.
     */
    public MoveOnChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldDest The destination field.
     * @param fldSource The source field.
     */
    public MoveOnChangeHandler(Converter fldDest)
    {
        this();
        this.init(null, fldDest, null, true, false, false);
    }
    /**
     * Constructor.
     * @param fldDest The destination field.
     * @param fldSource The source field.
     */
    public MoveOnChangeHandler(Converter fldDest, Converter fldSource)
    {
        this();
        this.init(null, fldDest, fldSource, true, false, false);
    }
    /**
     * Constructor.
     * @param fldDest The destination field.
     * @param fldSource The source field.
     * @param bClearIfThisNull If this listener's owner is set to null, set the destination field to null.
     * @param bOnlyIfDestNull Move only if the destination field is null.
     */
    public MoveOnChangeHandler(Converter fldDest, Converter fldSource, boolean bClearIfThisNull, boolean bOnlyIfDestNull)
    {
        this();
        this.init(null, fldDest, fldSource, bClearIfThisNull, bOnlyIfDestNull, false);
    }
    /**
     * Constructor.
     * @param fldDest The destination field.
     * @param fldSource The source field.
     * @param bClearIfThisNull If this listener's owner is set to null, set the destination field to null.
     * @param bOnlyIfDestNull Move only if the destination field is null.
     * @param bDontMoveNull Don't move if the source is null.
     */
    public MoveOnChangeHandler(Converter fldDest, Converter fldSource, boolean bClearIfThisNull, boolean bOnlyIfDestNull, boolean bDontMoveNull)
    {
        this();
        this.init(null, fldDest, fldSource, bClearIfThisNull, bOnlyIfDestNull, bDontMoveNull);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fldDest The destination field.
     * @param fldSource The source field.
     * @param bClearIfThisNull If this listener's owner is set to null, set the destination field to null.
     * @param bOnlyIfDestNull Move only if the destination field is null.
     */
    public void init(BaseField field, Converter fldDest, Converter fldSource, boolean bClearIfThisNull, boolean bOnlyIfDestNull, boolean bDontMoveNull)
    {
        super.init(field);
        this.setRespondsToMode(DBConstants.INIT_MOVE, false);
        this.setRespondsToMode(DBConstants.READ_MOVE, false); // By default, only respond to screen moves
        m_fldDest = fldDest;
        m_fldSource = fldSource;
        m_bClearIfThisNull = bClearIfThisNull;
        m_bOnlyIfDestNull = bOnlyIfDestNull;
        m_bDontMoveNull = bDontMoveNull;
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
        if (owner != null)
        {
            if (m_fldSource == null)
                m_fldSource = this.getOwner();
    
            if ((m_fldDest != null)
                && (((BaseField)m_fldDest.getField()).getRecord() != null)
                && (((BaseField)m_fldDest.getField()).getRecord() != this.getOwner().getRecord()))
                    ((BaseField)m_fldDest.getField()).getRecord().addListener(new FileRemoveBOnCloseHandler(this));     // Not same file, if target file closes, remove this listener!
            else if ((m_fldSource.getField() != null)
                && (((BaseField)m_fldSource.getField()).getRecord() != null)
                && (((BaseField)m_fldSource.getField()).getRecord() != this.getOwner().getRecord()))
                    ((BaseField)m_fldSource.getField()).getRecord().addListener(new FileRemoveBOnCloseHandler(this)); // Not same file, if target file closes, remove this listener!
        }
    }
    /**
     * The Field has Changed.
     * Move the source field to the destination field.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = this.moveIt(bDisplayOption, iMoveMode);
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            if (this.getOwner() != m_fldSource)
                if (this.getOwner() != m_fldDest)
                    iErrorCode = DBConstants.NORMAL_RETURN; // If the source and dest are unrelated this this, don't return an error (and revert this field)
        if (iErrorCode == DBConstants.NORMAL_RETURN)
            iErrorCode = super.fieldChanged(bDisplayOption, iMoveMode);
        return iErrorCode;
    }
    /**
     * Do the physical move operation.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int moveIt(boolean bDisplayOption, int iMoveMode)
    {
        if (m_bDontMoveNull) if (m_fldSource.getLength() == 0)      // Only move if dest is null
            return DBConstants.NORMAL_RETURN;
        if (m_bClearIfThisNull) if (this.getOwner().getLength() == 0) // If this is null, clear the dest field
            return ((BaseField)m_fldDest.getField()).moveFieldToThis(this.getOwner(), bDisplayOption, iMoveMode); // Move dependent field to here
        if (m_bOnlyIfDestNull) if (m_fldDest.getLength() != 0)      // Only move if dest is null
            return DBConstants.NORMAL_RETURN;
        int iErrorCode = this.moveSourceToDest(bDisplayOption, iMoveMode);
        if (iErrorCode == DBConstants.NORMAL_RETURN) if (m_fldDest == m_fldSource)
            return ((BaseField)m_fldDest.getField()).handleFieldChanged(bDisplayOption, iMoveMode);   // Special case, move to yourself (Just want to call .FieldChanged())
        return iErrorCode;
    }
    /**
     * Do the physical move operation.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int moveSourceToDest(boolean bDisplayOption, int iMoveMode)
    {
        if (m_fldSource instanceof BaseField)
            return ((BaseField)m_fldDest.getField()).moveFieldToThis((BaseField)m_fldSource, bDisplayOption, iMoveMode);   // Move dependent field to here
        else
            return m_fldDest.setString(m_fldSource.getString(), bDisplayOption, iMoveMode);   // Move dependent field to here
    }
    /**
     * Get the destination field.
     * @return The destination field.
     */
    public Converter getDestField()
    {
        return m_fldDest;
    }
    /**
     * Get the destination field.
     * @return The destination field.
     */
    public Converter getSourceField()
    {
        return m_fldSource;
    }
    /**
     * Get the destination field.
     * @return The destination field.
     */
    public void setSourceField(Converter fldSource)
    {
        m_fldSource = fldSource;
    }
    /**
     * Clear if this is null?
     * @param bCleanIfThisNull if true.
     */
    public void setClearIfThisNull(boolean bClearIfThisNull)
    {
        m_bClearIfThisNull = bClearIfThisNull;
    }
    /**
     * Only if dest null?
     * @param bOnlyIfDestNull if true.
     */
    public void setOnlyIfDestNull(boolean bOnlyIfDestNull)
    {
        m_bOnlyIfDestNull = bOnlyIfDestNull;
    }
    /**
     * Dont move null?
     * @param bDontMoveNull if true.
     */
    public void setDontMoveNull(boolean bDontMoveNull)
    {
        m_bDontMoveNull = bDontMoveNull;
    }

}
