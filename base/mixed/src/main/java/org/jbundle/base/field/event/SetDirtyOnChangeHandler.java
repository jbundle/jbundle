/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)SetDirtyOnChangeHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Constants;


/**
 * SetDirtyOnChangeHandler - Change the screen focus.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SetDirtyOnChangeHandler extends FieldListener
{
    /**
     * The field to set to modified if this field changes.
     */
    protected BaseField m_fldTarget = null;
    /**
     * Only set to dirty if the target field's record is new.
     */
    protected boolean m_bIfNewRecord = false;
    /**
     * Only set to dirty if the target field's record is current.
     */
    protected boolean m_bIfCurrentRecord = false;

    /**
     * Constructor.
     */
    public SetDirtyOnChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldTarget The field to set to modified if this field changes.
     * @param bIfNewRecord Only set to dirty if the target field's record is new.
     * @param bIfCurrentRecord Only set to dirty if the target field's record is current.
     */
    public SetDirtyOnChangeHandler(BaseField fldTarget, boolean bIfNewRecord, boolean bIfCurrentRecord)
    {
        this();
        this.init(null, fldTarget, bIfNewRecord, bIfCurrentRecord);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fldTarget The field to set to modified if this field changes.
     * @param bIfNewRecord Only set to dirty if the target field's record is new.
     * @param bIfCurrentRecord Only set to dirty if the target field's record is current.
     */
    public void init(BaseField field, BaseField fldTarget, boolean bIfNewRecord, boolean bIfCurrentRecord)
    {
        m_fldTarget = fldTarget;
        m_bIfNewRecord = bIfNewRecord;
        m_bIfCurrentRecord = bIfCurrentRecord;

        super.init(field);

        m_bScreenMove = true;   // Only respond to user change
        m_bInitMove = false;
        m_bReadMove = false;
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
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        int result = DBConstants.NORMAL_RETURN;
        Record record = m_fldTarget.getRecord();
        boolean bSetDirty = true;
        if (m_bIfNewRecord) if (record.getEditMode() == Constants.EDIT_ADD)
            bSetDirty = true;
        if (m_bIfCurrentRecord) if ((record.getEditMode() == Constants.EDIT_CURRENT) || (record.getEditMode() == Constants.EDIT_IN_PROGRESS))
            bSetDirty = true;
        if (bSetDirty)
        {
            m_fldTarget.setModified(true);
            result = record.handleRecordChange(m_fldTarget, DBConstants.FIELD_CHANGED_TYPE, bDisplayOption);    // Tell table that I'm getting changed (if not locked)
        }
        return result;
    } 
}
