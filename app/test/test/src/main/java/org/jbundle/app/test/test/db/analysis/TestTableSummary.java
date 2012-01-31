/**
 * @(#)TestTableSummary.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.test.db.analysis;

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

/**
 *  TestTableSummary - Summary test file.
 */
public class TestTableSummary extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final String TEST_CODE = "TestCode";
    public static final int kTestCode = kScreenRecordLastField + 1;
    public static final String TEST_KEY = "TestKey";
    public static final int kTestKey = kTestCode + 1;
    public static final String TEST_COUNT = "TestCount";
    public static final int kTestCount = kTestKey + 1;
    public static final String TEST_SHORT = "TestShort";
    public static final int kTestShort = kTestCount + 1;
    public static final String TEST_DOUBLE = "TestDouble";
    public static final int kTestDouble = kTestShort + 1;
    public static final int kTestTableSummaryLastField = kTestDouble;
    public static final int kTestTableSummaryFields = kTestDouble - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public TestTableSummary()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TestTableSummary(RecordOwner screen)
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

    public static final String kTestTableSummaryFile = null;    // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == kID)
        //  field = new (this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestCode)
            field = new StringField(this, "TestCode", 10, null, null);
        if (iFieldSeq == kTestKey)
            field = new StringField(this, "TestKey", 1, null, null);
        if (iFieldSeq == kTestCount)
            field = new IntegerField(this, "TestCount", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestShort)
            field = new ShortField(this, "TestShort", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTestDouble)
            field = new DoubleField(this, "TestDouble", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kTestTableSummaryLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
