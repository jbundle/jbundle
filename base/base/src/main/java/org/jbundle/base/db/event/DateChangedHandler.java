/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)DateChangedHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * DateChangedHandler - Set the "Date Changed" field whenever the record is changed.
 * NOTE: There is no Clone method, because this method will always be placed in the addListeners method of the file, which is called on Clone.
 * @version 1.0.0
 * @author    Don Corley
 */
public class DateChangedHandler extends FileListener
{
    /**
     * The sequence of the date changed field in this record.
     */
    protected int m_iMainFilesFieldSeq = -1;
    protected String mainFilesFieldName = null;
    /**
     * The date changed field in this record.
     */
    protected DateTimeField m_field = null;

    /**
     * Constructor.
     */
    public DateChangedHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param iMainFilesField The sequence of the date changed field in this record.
     */
    public DateChangedHandler(String mainFilesFieldName)
    {
        this();
        this.init(null, null, -1, mainFilesFieldName);
    }
    /**
     * Constructor.
     * @param iMainFilesField The sequence of the date changed field in this record.
     */
    public DateChangedHandler(int iMainFilesField)
    {
        this();
        this.init(null, null, iMainFilesField, null);
    }
    /**
     * Constructor.
     * @param field The date changed field in this record.
     */
    public DateChangedHandler(DateTimeField field)
    {
        this();
        this.init(null, field, -1, null);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iMainFilesField The sequence of the date changed field in this record.
     * @param field The date changed field in this record.
     */
    public void init(Record record, DateTimeField field, int iMainFilesField, String mainFilesFieldName)
    {
        super.init(record);
        m_iMainFilesFieldSeq = iMainFilesField;
        m_field = field;
        this.mainFilesFieldName = mainFilesFieldName;
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * This method sets the field to the current time on an add or update.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Write/Update a record
        switch (iChangeType)
        {
            case DBConstants.REFRESH_TYPE:
            case DBConstants.ADD_TYPE:
            case DBConstants.UPDATE_TYPE:
                DateTimeField thisField = m_field;
                if (m_field == null)
                    if (mainFilesFieldName != null)
                    thisField = (DateTimeField)this.getOwner().getField(mainFilesFieldName);
                if (m_field == null)
                    thisField = (DateTimeField)this.getOwner().getField(m_iMainFilesFieldSeq);
                boolean[] rgbEnabled = thisField.setEnableListeners(false);
                thisField.setValue(DateTimeField.currentTime(), bDisplayOption, DBConstants.SCREEN_MOVE);   // File written or updated, set the update date
                thisField.setEnableListeners(rgbEnabled);
                break;
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);        // Initialize the record
    }
}
