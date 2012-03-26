/**
 * @(#)DatabaseInfo.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.main.db.*;

/**
 *  DatabaseInfo - Database information.
 */
public class DatabaseInfo extends ControlRecord
     implements DatabaseInfoModel
{
    private static final long serialVersionUID = 1L;

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
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
            field = new StringField(this, NAME, 30, null, null);
        if (iFieldSeq == 4)
            field = new StringField(this, DESCRIPTION, 30, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, VERSION, 20, null, null);
        if (iFieldSeq == 6)
            field = new IntegerField(this, START_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new IntegerField(this, END_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new StringField(this, BASE_DATABASE, 30, null, null);
        if (iFieldSeq == 9)
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ID");
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Name");
            keyArea.addKeyField(NAME, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
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
            return Record.formatTableNames(DATABASE_INFO_FILE, bAddQuotes) + strDatabaseName;
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
    public void setDatabaseName(String databaseName)
    {
        m_strDatabaseName = databaseName;
    }

}
