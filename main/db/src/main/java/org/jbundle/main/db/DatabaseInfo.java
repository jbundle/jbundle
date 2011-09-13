/**
 * @(#)DatabaseInfo.
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
 *  DatabaseInfo - Database information.
 */
public class DatabaseInfo extends ControlRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kName = kControlRecordLastField + 1;
    public static final int kDescription = kName + 1;
    public static final int kVersion = kDescription + 1;
    public static final int kStartID = kVersion + 1;
    public static final int kEndID = kStartID + 1;
    public static final int kBaseDatabase = kEndID + 1;
    public static final int kProperties = kBaseDatabase + 1;
    public static final int kDatabaseInfoLastField = kProperties;
    public static final int kDatabaseInfoFields = kProperties - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kNameKey = kIDKey + 1;
    public static final int kDatabaseInfoLastKey = kNameKey;
    public static final int kDatabaseInfoKeys = kNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    protected String m_strDatabaseName;
    protected int m_iDatabaseType = 0;
    /**
     * Default constructor.
     */
    public DatabaseInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public DatabaseInfo(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialization.
     */
    public void init(RecordOwner screen)
    {
        // Don't auto-init local fields
        super.init(screen);
    }

    public static final String kDatabaseInfoFile = "DatabaseInfo";
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
            field = new StringField(this, "Name", 30, null, null);
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 30, null, null);
        if (iFieldSeq == kVersion)
            field = new StringField(this, "Version", 20, null, null);
        if (iFieldSeq == kStartID)
            field = new IntegerField(this, "StartID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEndID)
            field = new IntegerField(this, "EndID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kBaseDatabase)
            field = new StringField(this, "BaseDatabase", 30, null, null);
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kDatabaseInfoLastField)
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
        if (iKeyArea == kNameKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Name");
            keyArea.addKeyField(kName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kDatabaseInfoLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kDatabaseInfoLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Get the name of this table.
     * Override this to supply the name of the table.
     * Note: This is almost always overidden (except for mapped files)
     * @param bAddQuotes if the table name contains spaces, add quotes.
     * @return The name of this table.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        if (m_tableName == null)
        {
            String strDatabaseName = "";
            if (this.getDatabaseName() != null)
                if (!this.getDatabaseName().endsWith("_" + this.getDatabaseName()))
                    strDatabaseName = "_" + this.getDatabaseName();
            return Record.formatTableNames(kDatabaseInfoFile, bAddQuotes) + strDatabaseName;
        }
        return super.getTableNames(bAddQuotes);
    }
    /**
     * SetDatabaseType Method.
     */
    public void setDatabaseType(int iDatabaseType)
    {
        m_iDatabaseType = iDatabaseType;
    }
    /**
     * GetDatabaseType Method.
     */
    public int getDatabaseType()
    {
        if (m_iDatabaseType == 0)
            return DBConstants.REMOTE | DBConstants.SHARED_DATA;
        else
            return m_iDatabaseType;
    }
    /**
     * GetDatabaseName Method.
     */
    public String getDatabaseName()
    {
        return m_strDatabaseName;
    }
    /**
     * SetDatabaseName Method.
     */
    public void setDatabaseName(String strDatabaseName)
    {
        m_strDatabaseName = strDatabaseName;
    }

}
