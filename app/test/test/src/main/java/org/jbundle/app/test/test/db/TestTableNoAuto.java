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

    //public static final int kID = kID;
    public static final int kTestTableNoAutoLastField = kTestTableLastField;
    public static final int kTestTableNoAutoFields = kTestTableLastField - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kTestCodeKey = kIDKey + 1;
    public static final int kTestKeyKey = kTestCodeKey + 1;
    public static final int kTestTableNoAutoLastKey = kTestKeyKey;
    public static final int kTestTableNoAutoKeys = kTestKeyKey - DBConstants.MAIN_KEY_FIELD + 1;
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

    public static final String kTestTableNoAutoFile = "TestTableNoAuto";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kTestTableNoAutoFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        if (iFieldSeq == kID)
        {
            field = new IntegerField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kTestTableNoAutoLastField)
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
        }
        if (keyArea == null) if (iKeyArea < kTestTableNoAutoLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kTestTableNoAutoLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
