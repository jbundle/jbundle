/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)DateChangedHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.event.FieldRemoveBOnCloseHandler;
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
    protected String mainFilesFieldName = null;
    /**
     * The date changed field in this record.
     */
    protected DateTimeField m_field = null;
    /**
     * Should I change the mod date on a field change?
     */
    protected boolean onFieldChange = false;

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
        this.init(null, null, mainFilesFieldName);
    }
    /**
     * Constructor.
     * @param field The date changed field in this record.
     */
    public DateChangedHandler(DateTimeField field)
    {
        this();
        this.init(null, field, null);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iMainFilesField The sequence of the date changed field in this record.
     * @param field The date changed field in this record.
     */
    public void init(Record record, DateTimeField field, String mainFilesFieldName)
    {
        super.init(record);
        m_field = field;
        if (field != null)
            if (field.getRecord() != record)
                field.addListener(new FieldRemoveBOnCloseHandler(this));
        this.mainFilesFieldName = mainFilesFieldName;
    }
    /**
     * Respond to field change events?
     * This is useful with sub-files that notify me of field changes, but do no change any fields.
     * @param onFieldChange
     */
    public void setOnFieldChange(boolean onFieldChange)
    {
        this.onFieldChange = onFieldChange;
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
            case DBConstants.FIELD_CHANGED_TYPE:
                if (!onFieldChange)
                    break;
            case DBConstants.REFRESH_TYPE:
            case DBConstants.ADD_TYPE:
            case DBConstants.UPDATE_TYPE:
                DateTimeField thisField = m_field;
                if (thisField == null)
                    if (mainFilesFieldName != null)
                    	thisField = (DateTimeField)this.getOwner().getField(mainFilesFieldName);
                boolean[] rgbEnabled = thisField.setEnableListeners(false);
                thisField.setValue(DateTimeField.currentTime(), bDisplayOption, DBConstants.SCREEN_MOVE);   // File written or updated, set the update date
                thisField.setEnableListeners(rgbEnabled);
                break;
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);        // Initialize the record
    }
}
