/**
 * @(#)KeyInfo.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

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
import org.jbundle.app.program.screen.*;

/**
 *  KeyInfo - Record Key Information.
 */
public class KeyInfo extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kKeyFilename = kVirtualRecordLastField + 1;
    public static final int kKeyNumber = kKeyFilename + 1;
    public static final int kKeyField1 = kKeyNumber + 1;
    public static final int kKeyField2 = kKeyField1 + 1;
    public static final int kKeyField3 = kKeyField2 + 1;
    public static final int kKeyField4 = kKeyField3 + 1;
    public static final int kKeyField5 = kKeyField4 + 1;
    public static final int kKeyField6 = kKeyField5 + 1;
    public static final int kKeyField7 = kKeyField6 + 1;
    public static final int kKeyField8 = kKeyField7 + 1;
    public static final int kKeyField9 = kKeyField8 + 1;
    public static final int kKeyName = kKeyField9 + 1;
    public static final int kKeyType = kKeyName + 1;
    public static final int kKeyInfoLastField = kKeyType;
    public static final int kKeyInfoFields = kKeyType - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kKeyFilenameKey = kIDKey + 1;
    public static final int kKeyInfoLastKey = kKeyFilenameKey;
    public static final int kKeyInfoKeys = kKeyFilenameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public KeyInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public KeyInfo(RecordOwner screen)
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

    public static final String kKeyInfoFile = "KeyInfo";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kKeyInfoFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Key";
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "program";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.REMOTE | DBConstants.SHARED_DATA | DBConstants.HIERARCHICAL;
    }
    /**
     * Make a default screen.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) != 0)
            screen = new KeyInfoScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new KeyInfoGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
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
        if (iFieldSeq == kKeyFilename)
            field = new StringField(this, "KeyFilename", 40, null, null);
        if (iFieldSeq == kKeyNumber)
            field = new ShortField(this, "KeyNumber", 2, null, null);
        if (iFieldSeq == kKeyField1)
            field = new StringField(this, "KeyField1", 40, null, null);
        if (iFieldSeq == kKeyField2)
            field = new StringField(this, "KeyField2", 40, null, null);
        if (iFieldSeq == kKeyField3)
            field = new StringField(this, "KeyField3", 40, null, null);
        if (iFieldSeq == kKeyField4)
            field = new StringField(this, "KeyField4", 40, null, null);
        if (iFieldSeq == kKeyField5)
            field = new StringField(this, "KeyField5", 40, null, null);
        if (iFieldSeq == kKeyField6)
            field = new StringField(this, "KeyField6", 40, null, null);
        if (iFieldSeq == kKeyField7)
            field = new StringField(this, "KeyField7", 40, null, null);
        if (iFieldSeq == kKeyField8)
            field = new StringField(this, "KeyField8", 40, null, null);
        if (iFieldSeq == kKeyField9)
            field = new StringField(this, "KeyField9", 40, null, null);
        if (iFieldSeq == kKeyName)
            field = new StringField(this, "KeyName", 40, null, null);
        if (iFieldSeq == kKeyType)
            field = new StringField(this, "KeyType", 1, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kKeyInfoLastField)
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
        if (iKeyArea == kKeyFilenameKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "KeyFilename");
            keyArea.addKeyField(kKeyFilename, DBConstants.ASCENDING);
            keyArea.addKeyField(kKeyNumber, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kKeyInfoLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kKeyInfoLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
