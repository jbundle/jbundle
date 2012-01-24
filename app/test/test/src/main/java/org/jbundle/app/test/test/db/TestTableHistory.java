/**
 * @(#)TestTableHistory.
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
 *  TestTableHistory - The test table history file.
 */
public class TestTableHistory extends TestTable
     implements TestTableHistoryModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kCounterID = kTestTableLastField + 1;
    public static final int kModDate = kCounterID + 1;
    public static final int kTestTableHistoryLastField = kModDate;
    public static final int kTestTableHistoryFields = kModDate - DBConstants.MAIN_FIELD + 1;

    public static final int kCounterIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kModDateKey = kCounterIDKey + 1;
    public static final int kTestTableHistoryLastKey = kModDateKey;
    public static final int kTestTableHistoryKeys = kModDateKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public TestTableHistory()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TestTableHistory(RecordOwner screen)
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

    public static final String kTestTableHistoryFile = "TestTableHistory";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kTestTableHistoryFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "History";
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
        if (iFieldSeq == kCounterID)
            field = new CounterField(this, "CounterID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kID)
        {
            field = new IntegerField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == kModDate)
            field = new DateTimeField(this, "ModDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kTestTableHistoryLastField)
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
        if (iKeyArea == kCounterIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kCounterID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kModDateKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ModDate");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
            keyArea.addKeyField(kModDate, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kTestTableHistoryLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kTestTableHistoryLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
