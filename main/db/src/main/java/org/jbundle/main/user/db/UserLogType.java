/**
 * @(#)UserLogType.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.main.user.db.*;

/**
 *  UserLogType - .
 */
public class UserLogType extends VirtualRecord
     implements UserLogTypeModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kDescription = kVirtualRecordLastField + 1;
    public static final int kUserLogTypeLastField = kDescription;
    public static final int kUserLogTypeFields = kDescription - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kDescriptionKey = kIDKey + 1;
    public static final int kUserLogTypeLastKey = kDescriptionKey;
    public static final int kUserLogTypeKeys = kDescriptionKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public UserLogType()
    {
        super();
    }
    /**
     * Constructor.
     */
    public UserLogType(RecordOwner screen)
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

    public static final String kUserLogTypeFile = "UserLogType";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kUserLogTypeFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.TABLE | DBConstants.SHARED_DATA | DBConstants.LOCALIZABLE;
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
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 30, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kUserLogTypeLastField)
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
        if (iKeyArea == kDescriptionKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Description");
            keyArea.addKeyField(kDescription, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kUserLogTypeLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kUserLogTypeLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
