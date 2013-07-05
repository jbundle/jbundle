/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 * FileListener - File Behaviors.
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * MoveCodeToIDHandler - Copy the ID to the code field.
 */
public class MoveIDToCodeHandler extends FileListener
{
    /**
     * 
     */
    protected String m_iCodeField = null;

    /**
     * Constructor.
     */
    public MoveIDToCodeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public MoveIDToCodeHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public MoveIDToCodeHandler(String iCodeField)
    {
        this();
        this.init(null, iCodeField);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(Record record, String iCodeField)
    {
        super.init(record);
        m_iCodeField = iCodeField;
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
        
        if (iChangeType == DBConstants.AFTER_REFRESH_TYPE)
                iErrorCode = this.moveIDToCodeField();
        else if ((iChangeType == DBConstants.AFTER_ADD_TYPE)
            && (this.getCodeField().isNull()))
        {
            try {
                Record record = this.getOwner();
                Object bookmark = record.getLastModified(DBConstants.BOOKMARK_HANDLE);
                record.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                record.edit();
                iErrorCode = this.moveIDToCodeField();
                record.set();
            } catch (DBException ex) {
                ex.printStackTrace();
            }
        }
        return iErrorCode;
    }
    /**
     * Get the code field.
     * @return The code field (or the index field if not found)
     */
    public BaseField getCodeField()
    {
        Record record = this.getOwner();
        if (m_iCodeField != null)
            return record.getField(m_iCodeField);
        else
            return record.getKeyArea(record.getCodeKeyArea()).getField(DBConstants.MAIN_KEY_FIELD);
    }
    /**
     * Move the ID field to the code field.
     * @return
     */
    public int moveIDToCodeField()
    {
        if (this.getCodeField().isNull())
            return getCodeField().moveFieldToThis((BaseField)this.getOwner().getCounterField());
        else
            return DBConstants.NORMAL_RETURN;
    }
}
