/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)NewOnChangeHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;


/**
 * Do an "addNew" if this field changes.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class NewOnChangeHandler extends FieldListener
{
    /**
     * The target record.
     */
    protected Record m_recTarget = null;

    /**
     * Constructor.
     */
    public NewOnChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     */
    public NewOnChangeHandler(Record record)
    {
        this();
        this.init(null, record);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param record The target record.
     */
    public void init(BaseField field, Record record)
    {
        super.init(field);
        m_recTarget = record;
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
     * Do an addNew() on the target record.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * Field Changed, do an addNew on this record.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        try   {
            m_recTarget.addNew();
        } catch (DBException e)   {
            return e.getErrorCode();
        }
        return DBConstants.NORMAL_RETURN;
    }
}
