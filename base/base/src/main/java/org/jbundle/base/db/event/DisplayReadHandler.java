/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 * Enable on valid and disable on new.
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.StringField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;


/**
 * When this field is read, read secondary from another file.
 */
public class DisplayReadHandler extends FileListener
{
    protected Record m_FileToRead = null;
    String mainFieldName = null;
    protected BaseField m_fldMain = null;
    protected int m_iFileKeyField = -1;
    String fileKeyFieldName = null;
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
    public DisplayReadHandler(String mainFieldName, Record fileToRead, String fileKeyAreaName)
    {
        this();
        this.init(null, mainFieldName, fileToRead, -1, fileKeyAreaName);
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
        this.init(null, null, fileToRead, DBConstants.MAIN_FIELD, null);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iMainField The field to use as a key in the file to read.
     * @param fileToRead The secondary file to read when I read a record from the owner (onvalidrecord).
     * @param iFileKeyField The key field in the secondary file.
     */
    public void init(Record record, String mainFieldName, Record fileToRead, int iFileKeyField, String fileKeyAreaName)
    {
        m_FileToRead = fileToRead;
        this.mainFieldName = mainFieldName;
        m_iFileKeyField = iFileKeyField;
        this.fileKeyFieldName = fileKeyAreaName;
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
            if (mainFieldName == null)
                m_fldMain = this.getOwner().getReferenceField(m_FileToRead);
            if (fileKeyFieldName != null)
                m_FileToRead.setKeyArea(m_FileToRead.getField(fileKeyFieldName));
            else
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
        listener.init(null, mainFieldName, m_FileToRead, m_iFileKeyField, fileKeyFieldName);
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
            m_fldMain = this.getOwner().getField(mainFieldName);
        if (m_fldMain == null)
            return;     // Error - Field not found?
        if (fileKeyFieldName != null)
            m_FileToRead.getField(fileKeyFieldName).moveFieldToThis(m_fldMain, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);  // SCREEN_MOVE says this is coming from here
        else
            m_FileToRead.getField(m_iFileKeyField).moveFieldToThis(m_fldMain, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);  // SCREEN_MOVE says this is coming from here
        try   {
            boolean bSuccess = m_FileToRead.seek("=");
            if (!bSuccess)
            {
                m_FileToRead.initRecord(bDisplayOption);    // Put's record in an indeterminate state (so this record won't be written) and clears the fields.
                BaseField nextField = m_FileToRead.getField(m_FileToRead.getDefaultDisplayFieldName());
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
