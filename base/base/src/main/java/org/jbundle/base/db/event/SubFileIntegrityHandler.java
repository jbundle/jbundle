/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)SubFileFilter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.ResourceConstants;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * A make sure that any sub-records are deleted if this record is deleted.
 * If you set the delete? flag, the sub records will be deleted, otherwise
 * the sub-file will be checked to make sure there are no sub-records.
 * @version 1.0.0
 * @author    Don Corley
 */
public class SubFileIntegrityHandler extends FreeOnFreeHandler
{
    /**
     * The class name of the sub-file.
     */
    protected String m_strSubFile = null;
    /**
     * Remove sub-records on delete main?
     */
    protected boolean m_bRemoveSubRecords = false;

    /**
     * Constructor.
     */
    public SubFileIntegrityHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public SubFileIntegrityHandler(Record recSubFile)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, recSubFile, (String)null, false);
    }
    /**
     * Constructor.
     * @param strSubFile The class name of the sub-record to check.
     */
    public SubFileIntegrityHandler(String strSubFile)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, null, strSubFile, false);
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public SubFileIntegrityHandler(Record recSubFile, boolean bRemoveSubRecords)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, recSubFile, (String)null, bRemoveSubRecords);
    }
    /**
     * Constructor.
     * @param strSubFile The class name of the sub-record to check.
     */
    public SubFileIntegrityHandler(String strSubFile, boolean bRemoveSubRecords)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, null, strSubFile, bRemoveSubRecords);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recordMain The main record to create a sub-query for.
     * @param bRemoteSubRecords Remove sub-records on delete main?
     */
    public void init(Record record, Record recSubFile, String strSubFile, boolean bRemoveSubRecords)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        super.init(record, null, recSubFile, true);

        m_strSubFile = strSubFile;
        m_bRemoveSubRecords = bRemoveSubRecords;
    }
    /**
     * Set the record that owns this listener.
     * If a record is passed in, this method makes sure the correct key area is set on this record.
     * @param owner My owner.
     */
    public void free()
    {
        m_strSubFile = null;
        super.free();
    }
    /**
     * Set the record that owns this listener.
     * If a record is passed in, this method makes sure the correct key area is set on this record.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
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
        int iErrorCode = DBConstants.NORMAL_RETURN;
        switch (iChangeType)
        {
            case DBConstants.DELETE_TYPE:
                m_recDependent = this.getSubRecord();
                if (m_recDependent != null)
                {
                    try {
                        if (!m_bRemoveSubRecords)
                        {
                            m_recDependent.close();
                            if (m_recDependent.hasNext())
                            {   // Error - Can't delete the main record if there are sub-records.
                                org.jbundle.model.Task task = null;
                                if (this.getOwner() != null)
                                    if (this.getOwner().getRecordOwner() != null)
                                        task = this.getOwner().getRecordOwner().getTask();
                                String strError = "Sub-File Not Empty";
                                if (task != null)
                                {
                                    strError = ((BaseApplication)task.getApplication()).getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strError);
                                    return task.setLastError(strError);
                                }
                                return DBConstants.ERROR_RETURN;
                            }
                        }
                        else
                        {
                            m_recDependent.close();
                            while (m_recDependent.hasNext())
                            {
                                m_recDependent.next();
                                m_recDependent.edit();
                                m_recDependent.remove();
                            }
                        }
                    } catch (DBException ex)    {
                        ex.printStackTrace();
                    }
                }
                break;
        }
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        return super.doRecordChange(field, iChangeType, bDisplayOption);   // Initialize the record
    }
    /**
     * Get the sub-record.
     */
    public Record getSubRecord()
    {
        if (m_recDependent == null)
            m_recDependent = this.createSubRecord();
        if (m_recDependent != null)
        {
            if (m_recDependent.getListener(SubFileFilter.class.getName()) == null)
                m_recDependent.addListener(new SubFileFilter(this.getOwner()));
        }
        return m_recDependent;
    }
    /**
     * Create the sub-record.
     * Override this method to create a sub-record.
     */
    public Record createSubRecord()
    {
        Record record = (Record)ClassServiceUtility.getClassService().makeObjectFromClassName(m_strSubFile);
        if (record != null)
        {
            RecordOwner recordOwner = Record.findRecordOwner(this.getOwner());
            record.init(recordOwner);
            if (recordOwner != null)
                recordOwner.removeRecord(record);
            this.getOwner().addListener(new FreeOnFreeHandler(record));
        }
        return record;
    }
}
