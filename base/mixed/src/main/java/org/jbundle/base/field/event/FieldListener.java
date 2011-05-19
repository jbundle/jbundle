package org.jbundle.base.field.event;

/**
 * @(#)FieldListener.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BaseListener;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.util.DBConstants;
import org.jbundle.util.osgi.finder.ClassServiceUtility;

/**
 * BaseField definitions.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldListener extends BaseListener
{
    /**
     * Does this listener respond to screen moves?
     */
    protected boolean m_bScreenMove = true;
    /**
     * Does this listener respond to init moves?
     */
    protected boolean m_bInitMove = true;
    /**
     * Does this listener respond to read moves?
     */
    protected boolean m_bReadMove = true;

    /**
     * Constructor.
     */
    public FieldListener()
    {
        super();
    }
    /**
     * Constructor.
     * @param owner The basefield owner of this listener (usually null and set on setOwner()).
     */
    public FieldListener(BaseField owner)
    {
        this();
        this.init(owner);
    }
    /**
     * Constructor.
     * @param owner The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField owner)
    {
        m_bScreenMove = true;
        m_bInitMove = true;
        m_bReadMove = true;
        super.init(owner);
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
    }
    /**
     * Get this listener's owner.
     * @return My owner.
     */
    public BaseField getOwner()
    {
        return (BaseField)this.getListenerOwner();
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
        FieldListener listener = (FieldListener)ClassServiceUtility.getClassService().makeObjectFromClassName(this.getClass().getName());
        boolean bInitCalled = this.syncClonedListener(field, listener, false);
        if (!bInitCalled)
            listener.init(null);
        if (listener != null)
            if (field != null)
                if (listener.getOwner() == null)    // Being careful
                    field.addListener(listener);
        return listener;
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
            listener.init(null);
        listener.setRespondsToMode(DBConstants.INIT_MOVE, m_bInitMove);
        listener.setRespondsToMode(DBConstants.READ_MOVE, m_bReadMove);
        listener.setRespondsToMode(DBConstants.SCREEN_MOVE, m_bScreenMove);
        return true;
    }
    /**
     * When cloning a listener, if the field is contained in the source record, get the same field is the new record.
     * @param field
     * @param listener
     * @return
     */
    public BaseField getSyncedListenersField(BaseField field, FieldListener listener)
    {
        if (field != null)
            if (field.getRecord() == this.getOwner().getRecord())
                field = listener.getOwner().getRecord().getField(field.getFieldName());
        return field;
    }
    /**
     * Fake this listener into believing that the field was changed so its status reflects the record state.
     * ie., If the record is new, call fieldChanged(INIT), if the record is current,
     * call fieldChanged(READ) for this listener's owner field.
     * <p>This is almostly always used in the setOwner method to do the listener behavior once.
     * <pre>
     * super.setOwner(owner);
     * if (this.getOwner() != null)
     *  this.syncBehaviorToRecord();
     * </pre>
     * Note: Even though the data may change, the field is flaged as not changed, and the
     * recordChanged methods are not called, so if this behaviors changes other fields, you
     * should be careful and set the field to handleFieldChange(false) and after setModified(false).
     * @param fldDependent This optional field will also be tagged as not modified.
     */
    public void syncBehaviorToRecord(BaseField fldDependent)
    {
        if (this.getOwner() != null)
        {
            Record record = this.getOwner().getRecord();
            if (record != null)
            {   // Trip this listener, but do not trip the field changed behaviors
                boolean bModified = this.getOwner().isModified();
                boolean[] rgbEnabled = this.getOwner().setEnableListeners(false);
                boolean[] rgbDependentEnabled = null;
                boolean bOldDepModified = false;
                if (fldDependent != null)
                {
                    bOldDepModified = fldDependent.isModified();
                    rgbDependentEnabled = fldDependent.setEnableListeners(false);
                }
                if (((record.getEditMode() == DBConstants.EDIT_ADD)
                    || (record.getEditMode() == DBConstants.EDIT_NONE))
                    && (this.respondsToMode(DBConstants.INIT_MOVE)))
                        this.fieldChanged(DBConstants.DISPLAY, DBConstants.INIT_MOVE);
                else if (((record.getEditMode() == DBConstants.EDIT_IN_PROGRESS)
                    || (record.getEditMode() == DBConstants.EDIT_CURRENT))
                    && (this.respondsToMode(DBConstants.READ_MOVE)))
                        this.fieldChanged(DBConstants.DISPLAY, DBConstants.READ_MOVE);
                this.getOwner().setEnableListeners(rgbEnabled);
                this.getOwner().setModified(bModified);
                if (fldDependent != null)
                {
                    fldDependent.setEnableListeners(rgbDependentEnabled);
                    fldDependent.setModified(bOldDepModified);
                }
            }
        }
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
        int iErrorCode = DBConstants.NORMAL_RETURN;
        FieldListener nextListener = (FieldListener)this.getNextValidListener(iMoveMode);
        if (nextListener != null)
        {
            boolean bOldState = nextListener.setEnabledListener(false);      // Disable the listener to eliminate echos
            iErrorCode = nextListener.doSetData(objData, bDisplayOption, iMoveMode);
            nextListener.setEnabledListener(bOldState);   // Reenable
        }
        else if (m_owner != null)
            iErrorCode = this.getOwner().doSetData(objData, bDisplayOption, iMoveMode);
        return iErrorCode;
    }
    /**
     * Merge my changed data back into field that I just restored from disk.
     * @param objData The value this field held before I refreshed from disk.
     * @return The setData error code.
     */
    public int doMergeData(Object objData)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        FieldListener nextListener = (FieldListener)this.getNextListener();
        if (nextListener != null)
            iErrorCode = nextListener.doMergeData(objData);
        else if (m_owner != null)
            iErrorCode = this.getOwner().doMergeData(objData);
        return iErrorCode;
    }
    /**
     * Get the physical binary data from this field.
     * Behaviors are often used to initiate a complicated action only when the system asks for this data.
     * @return The field's raw data.
     */
    public Object doGetData()
    {
        Object objData = null;
        FieldListener nextListener = (FieldListener)this.getNextValidListener(DBConstants.SCREEN_MOVE);
        if (nextListener != null)
        {
            boolean bOldState = nextListener.setEnabledListener(false);      // Disable the listener to eliminate echos
            objData = nextListener.doGetData();
            nextListener.setEnabledListener(bOldState);   // Reenable
        }
        else if (m_owner != null)
            objData = this.getOwner().doGetData();
        return objData;
    }
    /**
     * The Field has Changed.
     * Don't need to call inherited.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    { // Override to do something!
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Remove this listener from the chain.
     * This is a utility method which is overidden to call the field of record method to unchain.
     * @param bDeleteFlag If true, free this listener.
     */
    public void removeListener(boolean bDeleteFlag)
    {
        super.removeListener(bDeleteFlag);
    }
    /**
     * Get the next enabled listener on the chain.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The next listener that responds to this type of move (or null if end of chain).
     */
    public BaseListener getNextValidListener(int iMoveMode)
    {
        if (m_nextListener != null)
        {
            if ((m_nextListener.isEnabled()) & (((FieldListener)m_nextListener).respondsToMode(iMoveMode)))
                return m_nextListener;
            else
                return ((FieldListener)m_nextListener).getNextValidListener(iMoveMode);
        }
        else
            return null;
    }
    /**
     * Does this listener respond to this mode?
     * @return true if this listener responds to this kind of move.
     */
    public boolean respondsToMode(int iMoveMode)
    {
        if (iMoveMode == DBConstants.SCREEN_MOVE)
            return m_bScreenMove;
        if (iMoveMode == DBConstants.INIT_MOVE)
            return m_bInitMove;
        if (iMoveMode == DBConstants.READ_MOVE)
            return m_bReadMove;
        return false;
    }
    /**
     * Set the mode this listener responds/doesn't respond to.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @param flag True If this listener should respond to this kind of move.
     */
    public void setRespondsToMode(int iMoveMode, boolean flag)
    {
        if (iMoveMode == DBConstants.SCREEN_MOVE)
            m_bScreenMove = flag;
        if (iMoveMode == DBConstants.INIT_MOVE)
            m_bInitMove = flag;
        if (iMoveMode == DBConstants.READ_MOVE)
            m_bReadMove = flag;
    }
    /**
     * Enable state is dependent on this listener.
     * @param dependentStateListener The listener to get the enabled state from.
     */
    public void setDependentStateListener(BaseListener dependentStateListener)
    {
        if (!(dependentStateListener instanceof FieldListener))
            dependentStateListener = null;
        else if ((this.getOwner() == null)
            || (((FieldListener)dependentStateListener).getOwner() == null)
            || (this.getOwner().getRecord() != ((FieldListener)dependentStateListener).getOwner().getRecord()))
                dependentStateListener = null;
        super.setDependentStateListener(dependentStateListener);
    }
}
