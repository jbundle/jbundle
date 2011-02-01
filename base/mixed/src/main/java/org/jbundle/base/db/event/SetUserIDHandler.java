/**
 * @(#)SetUserIDHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
package org.jbundle.base.db.event;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.IntegerField;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Change this user ID on a new or changing record.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SetUserIDHandler extends FileListener
{
    /**
     * Sequence of the user id field.
     */
    protected int m_iFieldSeq = -1;
    /**
     * Set it on the an add (not on update) only?
     */
    protected boolean m_bFirstTimeOnly = false;

    /**
     * Constructor.
     */
    public SetUserIDHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param iFieldSeq Sequence of the user id field.
     * @param iFirstTimeOnly Set it on the an add (not on update) only?
     */
    public SetUserIDHandler(int iFieldSeq, boolean bFirstTimeOnly)
    {
        this();
        this.init(null, iFieldSeq, bFirstTimeOnly);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iFieldSeq Sequence of the user id field.
     * @param iFirstTimeOnly Set it on the first time only?
     */
    public void init(Record thisFile, int iFieldSeq, boolean bFirstTimeOnly)
    {
        super.init(thisFile);
        m_iFieldSeq = iFieldSeq;
        m_bFirstTimeOnly = bFirstTimeOnly;
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)   // init this field override for other value
    {
        this.setUserID(DBConstants.INIT_MOVE, bDisplayOption);
        super.doNewRecord(bDisplayOption);
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Write/Update a record
        int iErrorCode = DBConstants.NORMAL_RETURN;
        switch (iChangeType)
        {
            case DBConstants.REFRESH_TYPE:
            case DBConstants.UPDATE_TYPE:
                if (m_bFirstTimeOnly)
                    break;
            case DBConstants.ADD_TYPE:
                iErrorCode = this.setUserID(iChangeType, bDisplayOption);
                if (iErrorCode != DBConstants.NORMAL_RETURN)
                    return iErrorCode;
                break;
            case DBConstants.FIELD_CHANGED_TYPE:
                if ((this.getOwner().getEditMode() == DBConstants.EDIT_ADD)
                        && (this.getOwner().getField(m_iFieldSeq).getValue() == -1))
            {   // Special case - Init Record did not have record owner, but I probably do now.
                iErrorCode = this.setUserID(iChangeType, bDisplayOption);
                if (iErrorCode != DBConstants.NORMAL_RETURN)
                    return iErrorCode;
            }            
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);        // Initialize the record
    }
    /**
     * Set the user ID.
     * @param iChangeType
     * @param bDisplayOption The display option.
     * @return The error code.
     */
    public int setUserID(int iChangeType, boolean bDisplayOption)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        IntegerField thisField = (IntegerField)this.getOwner().getField(m_iFieldSeq);
        int iUserID = -1;
        if (this.getOwner().getRecordOwner() != null)
            if (((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()) != null)
            if (((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()).getUserID() != null)
            if (((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()).getUserID().length() > 0)
                iUserID = Integer.parseInt(((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()).getUserID());
        boolean bOldModified = thisField.isModified();
        boolean[] rgbEnabled = null; 
        if (iChangeType == DBConstants.INIT_MOVE)
            rgbEnabled = thisField.setEnableListeners(false);
        iErrorCode = thisField.setValue(iUserID, bDisplayOption, iChangeType);
        if (iChangeType == DBConstants.INIT_MOVE)
        {   // Don't change the record on an init
            thisField.setEnableListeners(rgbEnabled);
            thisField.setModified(bOldModified);
        }
        return iErrorCode;
    }
}
