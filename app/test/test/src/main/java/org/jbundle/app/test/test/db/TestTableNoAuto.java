/**
 * @(#)TestTableNoAuto.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.test.db;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.app.test.test.db.*;

/**
 *  TestTableNoAuto - .
 */
public class TestTableNoAuto extends TestTable
     implements TestTableNoAutoModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public TestTableNoAuto()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TestTableNoAuto(RecordOwner screen)
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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(TEST_TABLE_NO_AUTO_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "test";
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
        if (iFieldSeq == 0)
        {
            field = new IntegerField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
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
        //if (iFieldSeq == 3)
        //  field = new StringField(this, TEST_CODE, 10, null, null);
        //if (iFieldSeq == 4)
        //  field = new StringField(this, TEST_NAME, 30, null, null);
        //if (iFieldSeq == 5)
        //  field = new MemoField(this, TEST_MEMO, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 6)
        //  field = new BooleanField(this, TEST_YES_NO, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(true));
        //if (iFieldSeq == 7)
        //  field = new IntegerField(this, TEST_LONG, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(1234567));
        //if (iFieldSeq == 8)
        //  field = new ShortField(this, TEST_SHORT, Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)1234));
        //if (iFieldSeq == 9)
        //  field = new TestTableNoAuto_TestDateTime(this, TEST_DATE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 10)
        //  field = new DateField(this, TEST_DATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 11)
        //  field = new TestTableNoAuto_TestTime(this, TEST_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 12)
        //  field = new FloatField(this, TEST_FLOAT, Constants.DEFAULT_FIELD_LENGTH, null, new Float(12345.67));
        //if (iFieldSeq == 13)
        //  field = new DoubleField(this, TEST_DOUBLE, Constants.DEFAULT_FIELD_LENGTH, null, new Double(123456789.01));
        //if (iFieldSeq == 14)
        //  field = new PercentField(this, TEST_PERCENT, Constants.DEFAULT_FIELD_LENGTH, null, new Float(0.32));
        //if (iFieldSeq == 15)
        //{
        //  field = new RealField(this, TEST_REAL, Constants.DEFAULT_FIELD_LENGTH, null, new Double(12345.67));
        //  field.addListener(new InitOnceFieldHandler(null));
        //}
        //if (iFieldSeq == 16)
        //{
        //  field = new CurrencyField(this, TEST_CURRENCY, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.addListener(new InitOnceFieldHandler(null));
        //}
        //if (iFieldSeq == 17)
        //  field = new PasswordField(this, TEST_PASSWORD, 10, null, null);
        if (iFieldSeq == 18)
        {
            field = new CurrencyField(this, TEST_VIRTUAL, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        //if (iFieldSeq == 19)
        //{
        //  field = new StringField(this, TEST_KEY, 1, null, null);
        //  field.setMinimumLength(1);
        //}
        //if (iFieldSeq == 20)
        //  field = new ImageField(this, TEST_IMAGE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 21)
        //  field = new HtmlField(this, TEST_HTML, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 22)
        //  field = new XmlField(this, TEST_XML, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 23)
        //  field = new PropertiesField(this, TEST_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 24)
        //  field = new TestSecondField(this, TEST_SECOND, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 25)
        //  field = new RsaEncryptedPropertyField(this, TEST_ENCRYPTED, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "TestCode");
            keyArea.addKeyField(TEST_CODE, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "TestKey");
            keyArea.addKeyField(TEST_KEY, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }

}
