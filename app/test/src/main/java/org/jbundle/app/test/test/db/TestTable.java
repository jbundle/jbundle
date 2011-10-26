/**
 * @(#)TestTable.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;

/**
 *  TestTable - Test.
 */
public class TestTable extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kTestCode = kVirtualRecordLastField + 1;
    public static final int kTestName = kTestCode + 1;
    public static final int kTestMemo = kTestName + 1;
    public static final int kTestYesNo = kTestMemo + 1;
    public static final int kTestLong = kTestYesNo + 1;
    public static final int kTestShort = kTestLong + 1;
    public static final int kTestDateTime = kTestShort + 1;
    public static final int kTestDate = kTestDateTime + 1;
    public static final int kTestTime = kTestDate + 1;
    public static final int kTestFloat = kTestTime + 1;
    public static final int kTestDouble = kTestFloat + 1;
    public static final int kTestPercent = kTestDouble + 1;
    public static final int kTestReal = kTestPercent + 1;
    public static final int kTestCurrency = kTestReal + 1;
    public static final int kTestPassword = kTestCurrency + 1;
    public static final int kTestVirtual = kTestPassword + 1;
    public static final int kTestKey = kTestVirtual + 1;
    public static final int kTestImage = kTestKey + 1;
    public static final int kTestHtml = kTestImage + 1;
    public static final int kTestXml = kTestHtml + 1;
    public static final int kTestProperties = kTestXml + 1;
    public static final int kTestSecond = kTestProperties + 1;
    public static final int kTestEncrypted = kTestSecond + 1;
    public static final int kTestTableLastField = kTestEncrypted;
    public static final int kTestTableFields = kTestEncrypted - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kTestCodeKey = kIDKey + 1;
    public static final int kTestKeyKey = kTestCodeKey + 1;
    public static final int kTestTableLastKey = kTestKeyKey;
    public static final int kTestTableKeys = kTestKeyKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public TestTable()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TestTable(RecordOwner screen)
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

    public static final String kTestTableFile = "TestTable";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kTestTableFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Test";
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kTestCode)
            field = new StringField(this, "TestCode", 10, null, null);
        if (iFieldSeq == kTestName)
            field = new StringField(this, "TestName", 30, null, null);
        if (iFieldSeq == kTestMemo)
            field = new MemoField(this, "TestMemo", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestYesNo)
            field = new BooleanField(this, "TestYesNo", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(true));
        if (iFieldSeq == kTestLong)
            field = new IntegerField(this, "TestLong", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(1234567));
        if (iFieldSeq == kTestShort)
            field = new ShortField(this, "TestShort", Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)1234));
        if (iFieldSeq == kTestDateTime)
            field = new TestTable_TestDateTime(this, "TestDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestDate)
            field = new DateField(this, "TestDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestTime)
            field = new TestTable_TestTime(this, "TestTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestFloat)
            field = new FloatField(this, "TestFloat", Constants.DEFAULT_FIELD_LENGTH, null, new Float(12345.67));
        if (iFieldSeq == kTestDouble)
            field = new DoubleField(this, "TestDouble", Constants.DEFAULT_FIELD_LENGTH, null, new Double(123456789.01));
        if (iFieldSeq == kTestPercent)
            field = new PercentField(this, "TestPercent", Constants.DEFAULT_FIELD_LENGTH, null, new Float(0.32));
        if (iFieldSeq == kTestReal)
        {
            field = new RealField(this, "TestReal", Constants.DEFAULT_FIELD_LENGTH, null, new Double(12345.67));
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kTestCurrency)
        {
            field = new CurrencyField(this, "TestCurrency", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kTestPassword)
            field = new PasswordField(this, "TestPassword", 10, null, null);
        if (iFieldSeq == kTestVirtual)
        {
            field = new CurrencyField(this, "TestVirtual", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kTestKey)
        {
            field = new StringField(this, "TestKey", 1, null, null);
            field.setMinimumLength(1);
        }
        if (iFieldSeq == kTestImage)
            field = new ImageField(this, "TestImage", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestHtml)
            field = new HtmlField(this, "TestHtml", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestXml)
            field = new XmlField(this, "TestXml", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestProperties)
            field = new PropertiesField(this, "TestProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestSecond)
            field = new TestSecondField(this, "TestSecond", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestEncrypted)
            field = new RsaEncryptedPropertyField(this, "TestEncrypted", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kTestTableLastField)
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
        if (iKeyArea == kTestCodeKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "TestCode");
            keyArea.addKeyField(kTestCode, DBConstants.ASCENDING);
        }
        if (iKeyArea == kTestKeyKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "TestKey");
            keyArea.addKeyField(kTestKey, DBConstants.ASCENDING);
            keyArea.addKeyField(kTestCode, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kTestTableLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kTestTableLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
