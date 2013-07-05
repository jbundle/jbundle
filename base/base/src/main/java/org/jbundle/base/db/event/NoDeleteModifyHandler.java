/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)NoDeleteHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ResourceConstants;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Can't delete any of these record (unless you explicitly remove this behavior).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class NoDeleteModifyHandler extends FileListener
{
    protected boolean m_bNoDelete = true;
    protected boolean m_bNoModify = false;
    
    /**
     * Constructor.
     */
    public NoDeleteModifyHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public NoDeleteModifyHandler(Record record)
    {
        this();
        this.init(record, true, false);
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public NoDeleteModifyHandler(boolean bNoDelete, boolean bNoModify)
    {
        this();
        this.init(null, bNoDelete, bNoModify);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(Record record, boolean bNoDelete, boolean bNoModify)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        m_bNoDelete = bNoDelete;
        m_bNoModify = bNoModify;
        super.init(record);
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null;        // DO NOT clone this behavior as it is almost always explicitly added in record.addListeners().
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * On an add or lock, makes sure the main key field is set to the current main target field
     * so it will be a child of the current main record.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * Before an add, set the key back to the original value.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        if (((iChangeType == DBConstants.DELETE_TYPE) && m_bNoDelete)
            || ((iChangeType == DBConstants.FIELD_CHANGED_TYPE) && m_bNoModify))
            if (((this.getOwner().getEditMode() & DBConstants.EDIT_ADD) != DBConstants.EDIT_ADD)
                && (!this.getOwner().isRefreshedRecord()))
        {
            String strError = "Can't delete records";
            if (iChangeType == DBConstants.FIELD_CHANGED_TYPE)
                strError = "Can't modify records";
            org.jbundle.model.Task task = null;
            if (this.getOwner().getRecordOwner() != null)
                task = this.getOwner().getRecordOwner().getTask();
            if (task != null)
                strError = ((BaseApplication)task.getApplication()).getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strError);
            if (task != null)
                return this.getOwner().getRecordOwner().getTask().setLastError(strError);
            return DBConstants.ERROR_RETURN;
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);   // Initialize the record
    }
}
