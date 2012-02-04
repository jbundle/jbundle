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

    //public static final String ID = ID;
    public static final String TEST_CODE = "TestCode";
    public static final String TEST_KEY = "TestKey";
    public static final String TEST_COUNT = "TestCount";
    public static final String TEST_SHORT = "TestShort";
    public static final String TEST_DOUBLE = "TestDouble";
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

    public static final String TEST_TABLE_SUMMARY_FILE = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //  field = new (this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 1)
            field = new StringField(this, TEST_CODE, 10, null, null);
        if (iFieldSeq == 2)
            field = new StringField(this, TEST_KEY, 1, null, null);
        if (iFieldSeq == 3)
            field = new IntegerField(this, TEST_COUNT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new ShortField(this, TEST_SHORT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new DoubleField(this, TEST_DOUBLE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
