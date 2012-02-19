/**
 * @(#)TestTableNoAuto.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, 10, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, TEST_CODE, 10, null, null);
        field = new FieldInfo(this, TEST_NAME, 30, null, null);
        field = new FieldInfo(this, TEST_MEMO, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, TEST_YES_NO, 10, null, new Boolean(true));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, TEST_LONG, 10, null, new Integer(1234567));
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, TEST_SHORT, 5, null, new Short((short)1234));
        field.setDataClass(Short.class);
        field = new FieldInfo(this, TEST_DATE_TIME, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, TEST_DATE, 12, null, null);
        field.setDataClass(Date.class);
        field.setScale(Constants.DATE_ONLY);
        field = new FieldInfo(this, TEST_TIME, 10, null, null);
        field.setDataClass(Date.class);
        field.setScale(Constants.TIME_ONLY);
        field = new FieldInfo(this, TEST_FLOAT, 8, null, null);
        field.setDataClass(Float.class);
        field = new FieldInfo(this, TEST_DOUBLE, 18, null, null);
        field.setDataClass(Double.class);
        field = new FieldInfo(this, TEST_PERCENT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Float.class);
        field = new FieldInfo(this, TEST_REAL, 15, null, null);
        field.setDataClass(Double.class);
        field.setScale(-1);
        field = new FieldInfo(this, TEST_CURRENCY, 18, null, null);
        field.setDataClass(Double.class);
        field = new FieldInfo(this, TEST_PASSWORD, 10, null, null);
        //field = new FieldInfo(this, TEST_VIRTUAL, 18, null, null);
        //field.setDataClass(Double.class);
        field = new FieldInfo(this, TEST_KEY, 1, null, null);
        field = new FieldInfo(this, TEST_IMAGE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, TEST_HTML, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, TEST_XML, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, TEST_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, TEST_SECOND, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, TEST_ENCRYPTED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
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
