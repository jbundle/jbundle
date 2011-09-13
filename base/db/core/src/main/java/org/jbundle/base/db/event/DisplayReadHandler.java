/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 * Enable on valid and disable on new.
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.StringField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;


/**
 * When this field is read, read secondary from another file.
 */
public class DisplayReadHandler extends FileListener
{
    protected Record m_FileToRead = null;
    protected int m_iMainField = -1;
    protected BaseField m_fldMain = null;
    protected int m_iFileKeyField = -1;
    public static final String RECORD_NOT_FOUND_MESSAGE = "***Record not found***";

    /**
     * Constructor.
     */
    public DisplayReadHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param iMainField The field to use as a key in the file to read.
     * @param fileToRead The secondary file to read when I read a record from the owner (onvalidrecord).
     * @param iFileKeyField The key field in the secondary file.
     */
    public DisplayReadHandler(int iMainField, Record fileToRead, int iFileKeyField)
    {
        this();
        this.init(null, iMainField, fileToRead, iFileKeyField);
    }
    /**
     * Constructor.
     * @param iMainField The field to use as a key in the file to read.
     * @param fileToRead The secondary file to read when I read a record from the owner (onvalidrecord).
     * @param iFileKeyField The key field in the secondary file.
     */
    public DisplayReadHandler(Record fileToRead)
    {
        this();
        this.init(null, -1, fileToRead, DBConstants.MAIN_FIELD);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iMainField The field to use as a key in the file to read.
     * @param fileToRead The secondary file to read when I read a record from the owner (onvalidrecord).
     * @param iFileKeyField The key field in the secondary file.
     */
    public void init(Record record, int iMainField, Record fileToRead, int iFileKeyField)
    {
        m_FileToRead = fileToRead;
        m_iMainField = iMainField;
        m_iFileKeyField = iFileKeyField;
        super.init(record);
        FileListener listener = new FileRemoveBOnCloseHandler(this);    // If this closes first, this will remove FileListener
        fileToRead.addListener(listener); // Remove this if you close the file first
    }
    /**
     * Set the record that owns this listener.
     * If a record is passed in, this method makes sure the correct key area is set on this record.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
        {
            if (m_iMainField == -1)
                m_fldMain = this.getOwner().getReferenceField(m_FileToRead);
            m_FileToRead.setKeyArea(m_FileToRead.getField(m_iFileKeyField));
        }
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        DisplayReadHandler listener = new DisplayReadHandler();
        listener.init(null, m_iMainField, m_FileToRead, m_iFileKeyField);
        return listener;
    }
    /**
     * Called when a valid record is read from the table/query.
     * Reads the secondary record and set's the record not found message if not found.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        if (m_fldMain == null)
            m_fldMain = this.getOwner().getField(m_iMainField);
        if (m_fldMain == null)
            return;     // Error - Field not found?
        m_FileToRead.getField(m_iFileKeyField).moveFieldToThis(m_fldMain, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);  // SCREEN_MOVE says this is coming from here
        try   {
            boolean bSuccess = m_FileToRead.seek("=");
            if (!bSuccess)
            {
                m_FileToRead.initRecord(bDisplayOption);    // Put's record in an indeterminate state (so this record won't be written) and clears the fields.
                BaseField nextField = m_FileToRead.getField(m_iFileKeyField+1);
                String strRecNotFound = this.getOwner().getTable().getString(RECORD_NOT_FOUND_MESSAGE);
                if (nextField instanceof StringField)
                    nextField.setString(strRecNotFound, bDisplayOption, DBConstants.SCREEN_MOVE);
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        super.doValidRecord(bDisplayOption);
    }
    /**
     * Called when a new blank record is required for the table/query.
     * Make sure the secondary record is inited.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        m_FileToRead.initRecord(bDisplayOption);    // Clear the fields!
        super.doNewRecord(bDisplayOption);
    }
}
