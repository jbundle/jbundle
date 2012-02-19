/*
 * @(#)SetUserIDHandler.java    0.00 12-Feb-97 Don Corley
 *
 *      don@tourgeek.com
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.util.BaseApplication;
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
    protected int fieldSeq = -1;
    /**
     * Sequence of the user id field.
     */
    protected String userIdFieldName = null;
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
        this.init(null, null, iFieldSeq, bFirstTimeOnly);
    }
    /**
     * Constructor.
     * @param iFieldSeq Sequence of the user id field.
     * @param iFirstTimeOnly Set it on the an add (not on update) only?
     */
    public SetUserIDHandler(String userIdFieldName, boolean bFirstTimeOnly)
    {
        this();
        this.init(null, userIdFieldName, -1, bFirstTimeOnly);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iFieldSeq Sequence of the user id field.
     * @param iFirstTimeOnly Set it on the first time only?
     */
    public void init(Record thisFile, String userIdFieldName, int iFieldSeq, boolean bFirstTimeOnly)
    {
        super.init(thisFile);
        this.fieldSeq = iFieldSeq;
        this.userIdFieldName = userIdFieldName;
        m_bFirstTimeOnly = bFirstTimeOnly;
    }
    /**
     * Set the field or file that owns this listener.
     * Note: There is no getOwner() method here... This is because
     * the specific listeners return the correct owner class on getOwner().
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (this.getOwner() != null)
            if (userIdFieldName == null)
                userIdFieldName = this.getOwner().getField(fieldSeq).getFieldName();
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
                        && (this.getOwner().getField(userIdFieldName).getValue() == -1))
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
        int iUserID = -1;
        if (this.getOwner().getRecordOwner() != null)
            if (((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()) != null)
            if (((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()).getUserID() != null)
            if (((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()).getUserID().length() > 0)
                iUserID = Integer.parseInt(((BaseApplication)this.getOwner().getRecordOwner().getTask().getApplication()).getUserID());
        boolean bOldModified = this.getOwner().getField(userIdFieldName).isModified();
        boolean[] rgbEnabled = null; 
        if (iChangeType == DBConstants.INIT_MOVE)
            rgbEnabled = this.getOwner().getField(userIdFieldName).setEnableListeners(false);
        iErrorCode = this.getOwner().getField(userIdFieldName).setValue(iUserID, bDisplayOption, iChangeType);
        if (iChangeType == DBConstants.INIT_MOVE)
        {   // Don't change the record on an init
            this.getOwner().getField(userIdFieldName).setEnableListeners(rgbEnabled);
            this.getOwner().getField(userIdFieldName).setModified(bOldModified);
        }
        return iErrorCode;
    }
}
