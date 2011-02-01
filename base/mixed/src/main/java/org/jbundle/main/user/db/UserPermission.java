/**
 *  @(#)UserPermission.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.main.user.db;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;

/**
 *  UserPermission - User permissions to resources.
 */
public class UserPermission extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kUserGroupID = kVirtualRecordLastField + 1;
    public static final int kUserResourceID = kUserGroupID + 1;
    public static final int kLoginLevel = kUserResourceID + 1;
    public static final int kAccessLevel = kLoginLevel + 1;
    public static final int kUserPermissionLastField = kAccessLevel;
    public static final int kUserPermissionFields = kAccessLevel - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kUserGroupIDKey = kIDKey + 1;
    public static final int kUserResourceIDKey = kUserGroupIDKey + 1;
    public static final int kUserPermissionLastKey = kUserResourceIDKey;
    public static final int kUserPermissionKeys = kUserResourceIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public UserPermission()
    {
        super();
    }
    /**
     * Constructor.
     */
    public UserPermission(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    public static final String kUserPermissionFile = "UserPermission";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kUserPermissionFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.REMOTE | DBConstants.USER_DATA;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kUserGroupID)
        {
            field = new UserGroupField(this, "UserGroupID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kUserResourceID)
            field = new UserResourceField(this, "UserResourceID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kLoginLevel)
            field = new LoginLevelField(this, "LoginLevel", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kAccessLevel)
            field = new AccessLevelField(this, "AccessLevel", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kUserPermissionLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == kIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kUserGroupIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "UserGroupID");
            keyArea.addKeyField(kUserGroupID, DBConstants.ASCENDING);
            keyArea.addKeyField(kUserResourceID, DBConstants.ASCENDING);
            keyArea.addKeyField(kLoginLevel, DBConstants.ASCENDING);
        }
        if (iKeyArea == kUserResourceIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "UserResourceID");
            keyArea.addKeyField(kUserResourceID, DBConstants.ASCENDING);
            keyArea.addKeyField(kUserGroupID, DBConstants.ASCENDING);
            keyArea.addKeyField(kLoginLevel, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kUserPermissionLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kUserPermissionLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addListeners()
    {
        super.addListeners();
        this.addListener(new UpdateGroupPermissionHandler(null));
    }

}
