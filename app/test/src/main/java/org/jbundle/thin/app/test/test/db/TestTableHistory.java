/**
 *  @(#)TestTableHistory.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.thin.app.test.test.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class TestTableHistory extends org.jbundle.thin.app.test.test.db.TestTable
{

    public TestTableHistory()
    {
        super();
    }
    public TestTableHistory(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String TEST_TABLE_HISTORY_FILE = "TestTableHistory";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? TestTableHistory.TEST_TABLE_HISTORY_FILE : super.getTableNames(bAddQuotes);
    }
    /**
     *  Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "test";
    }
    /**
     *  Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return Constants.REMOTE | Constants.USER_DATA;
    }
    /**
    * Set up the screen input fields.
    */
    public void setupFields()
    {
        FieldInfo field = null;
        field = new FieldInfo(this, "ID", 10, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "TestCode", 10, null, null);
        field = new FieldInfo(this, "TestName", 30, null, null);
        field = new FieldInfo(this, "TestMemo", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "TestYesNo", 10, null, new Boolean(true));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "TestLong", 10, null, new Integer(1234567));
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "TestShort", 5, null, new Short((short)1234));
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "TestDateTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "TestDate", 12, null, null);
        field.setDataClass(Date.class);
        field.setScale(Constants.DATE_ONLY);
        field = new FieldInfo(this, "TestTime", 10, null, null);
        field.setDataClass(Date.class);
        field.setScale(Constants.TIME_ONLY);
        field = new FieldInfo(this, "TestFloat", 8, null, null);
        field.setDataClass(Float.class);
        field = new FieldInfo(this, "TestDouble", 18, null, null);
        field.setDataClass(Double.class);
        field = new FieldInfo(this, "TestPercent", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Float.class);
        field = new FieldInfo(this, "TestReal", 15, null, null);
        field.setDataClass(Double.class);
        field.setScale(-1);
        field = new FieldInfo(this, "TestCurrency", 18, null, null);
        field.setDataClass(Double.class);
        field = new FieldInfo(this, "TestPassword", 10, null, null);
        //field = new FieldInfo(this, "TestVirtual", 18, null, null);
        //field.setDataClass(Double.class);
        field = new FieldInfo(this, "TestKey", 1, null, null);
        field = new FieldInfo(this, "TestImage", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "TestHtml", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "TestXml", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "TestProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "TestSecond", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "TestEncrypted", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "CounterID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ModDate", 25, null, null);
        field.setDataClass(Date.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("CounterID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ModDate");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea.addKeyField("ModDate", Constants.ASCENDING);
    }

}
