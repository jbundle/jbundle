/**
 * @(#)UserControl.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.main.user.db.*;

/**
 *  UserControl - User Control file.
 */
public class UserControl extends ControlRecord
     implements UserControlModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kAnonUserGroupID = kControlRecordLastField + 1;
    public static final int kAnonUserInfoID = kAnonUserGroupID + 1;
    public static final int kTemplateUserID = kAnonUserInfoID + 1;
    public static final int kUserControlLastField = kTemplateUserID;
    public static final int kUserControlFields = kTemplateUserID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kUserControlLastKey = kIDKey;
    public static final int kUserControlKeys = kIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public UserControl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public UserControl(RecordOwner screen)
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

    public static final String kUserControlFile = "UserControl";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kUserControlFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.LOCAL | DBConstants.USER_DATA;
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
        if (iFieldSeq == kAnonUserGroupID)
            field = new UserGroupField(this, "AnonUserGroupID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kAnonUserInfoID)
            field = new UserField(this, "AnonUserInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTemplateUserID)
            field = new UserField(this, "TemplateUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kUserControlLastField)
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
        if (keyArea == null) if (iKeyArea < kUserControlLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kUserControlLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
