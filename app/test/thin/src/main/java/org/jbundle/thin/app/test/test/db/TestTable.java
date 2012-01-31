/**
 * @(#)TestTable.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.test.test.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.test.test.db.*;

public class TestTable extends FieldList
    implements TestTableModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;

    public TestTable()
    {
        super();
    }
    public TestTable(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String TEST_TABLE_FILE = "TestTable";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? TestTable.TEST_TABLE_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
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
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, "TestCode");
        keyArea.addKeyField("TestCode", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "TestKey");
        keyArea.addKeyField("TestKey", Constants.ASCENDING);
        keyArea.addKeyField("TestCode", Constants.ASCENDING);
    }

}
