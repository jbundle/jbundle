/**
 * @(#)TestTableNoAuto.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.test.test.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.app.test.test.db.*;
import org.jbundle.model.app.test.test.db.*;

public class TestTableNoAuto extends TestTable
    implements TestTableNoAutoModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String TEST_CODE = TEST_CODE;
    //public static final String TEST_NAME = TEST_NAME;
    //public static final String TEST_MEMO = TEST_MEMO;
    //public static final String TEST_YES_NO = TEST_YES_NO;
    //public static final String TEST_LONG = TEST_LONG;
    //public static final String TEST_SHORT = TEST_SHORT;
    //public static final String TEST_DATE_TIME = TEST_DATE_TIME;
    //public static final String TEST_DATE = TEST_DATE;
    //public static final String TEST_TIME = TEST_TIME;
    //public static final String TEST_FLOAT = TEST_FLOAT;
    //public static final String TEST_DOUBLE = TEST_DOUBLE;
    //public static final String TEST_PERCENT = TEST_PERCENT;
    //public static final String TEST_REAL = TEST_REAL;
    //public static final String TEST_CURRENCY = TEST_CURRENCY;
    //public static final String TEST_PASSWORD = TEST_PASSWORD;
    //public static final String TEST_VIRTUAL = TEST_VIRTUAL;
    //public static final String TEST_KEY = TEST_KEY;
    //public static final String TEST_IMAGE = TEST_IMAGE;
    //public static final String TEST_HTML = TEST_HTML;
    //public static final String TEST_XML = TEST_XML;
    //public static final String TEST_PROPERTIES = TEST_PROPERTIES;
    //public static final String TEST_SECOND = TEST_SECOND;
    //public static final String TEST_ENCRYPTED = TEST_ENCRYPTED;

    public TestTableNoAuto()
    {
        super();
    }
    public TestTableNoAuto(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String TEST_TABLE_NO_AUTO_FILE = "TestTableNoAuto";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? TestTableNoAuto.TEST_TABLE_NO_AUTO_FILE : super.getTableNames(bAddQuotes);
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
    }
    /**
    * This is not an auto-counter record.
    */
    public boolean isAutoSequence()
    {
        return false;
    }

}
