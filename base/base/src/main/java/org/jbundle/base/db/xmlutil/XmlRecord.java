/*
 *  @(#)XmlRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.xmlutil;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.thin.base.db.Constants;


/**
 *  XmlRecord - This is a Temporary record that is used when parsing an XML file.
 */
public class XmlRecord extends Record
{
	private static final long serialVersionUID = 1L;

	/**
     *  Default constructor.
     */
    public XmlRecord()
    {
        super();
    }
    /**
     *  Constructor.
     */
    public XmlRecord(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     *  Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    protected String m_strRecord = DBConstants.BLANK;
    /**
     *  Get the table name..
     */
    public void setTableName(String strRecord)
    {
        m_strRecord = strRecord;
    }
    /**
     *  Get the table name..
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return Record.formatTableNames(m_strRecord, bAddQuotes);
    }
    protected String m_strDatabaseName = "test";
    /**
     *  Get the Database Name..
     */
    public String getDatabaseName()
    {
        return m_strDatabaseName;
    }
    /**
     *  Get the Database Name..
     */
    public void setDatabaseName(String strDatabaseName)
    {
        m_strDatabaseName = strDatabaseName;
    }
    protected int m_dbType = DBConstants.MEMORY;        // Always memory based.
    /**
     *  Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return m_dbType;        // Always memory based.
    }
    /**
     *  Is this a local (vs remote) file?.
     */
    public void setDatabaseType(int iDbType)
    {
        m_dbType = iDbType;     // Always memory based.
    }
    /**
     *  Add this field in the Record's field sequence..
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == DBConstants.MAIN_FIELD)
            field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        return field;
    }
    /**
     *  Add this key area description to the Record..
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == DBConstants.MAIN_KEY_FIELD)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, DBConstants.PRIMARY_KEY);
            keyArea.addKeyField(DBConstants.MAIN_FIELD, DBConstants.ASCENDING);
        }
        return keyArea;
    }

}
