/**
 * @(#)BaseFolder.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db;

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
 *  BaseFolder - This is a base class for a record that represents a hierarchial folder.
 */
public class BaseFolder extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kLastChanged = kLastChanged;
    public static final int kName = kVirtualRecordLastField + 1;
    public static final int kParentFolderID = kName + 1;
    public static final int kBaseFolderLastField = kParentFolderID;
    public static final int kBaseFolderFields = kParentFolderID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kParentFolderIDKey = kIDKey + 1;
    public static final int kBaseFolderLastKey = kParentFolderIDKey;
    public static final int kBaseFolderKeys = kParentFolderIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public BaseFolder()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseFolder(RecordOwner screen)
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

    public static final String kBaseFolderFile = "Folder";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kBaseFolderFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        if (iFieldSeq == kName)
            field = new StringField(this, "Name", 40, null, null);
        if (iFieldSeq == kParentFolderID)
            field = new ReferenceField(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kLastChanged)
        //{
        //  field = new RecordChangedField(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kBaseFolderLastField)
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
        if (iKeyArea == kParentFolderIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ParentFolderID");
            keyArea.addKeyField(kParentFolderID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kBaseFolderLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kBaseFolderLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
