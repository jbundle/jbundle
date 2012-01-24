/**
 * @(#)TestScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.test.screen;

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
 *  TestScreenRecord - Screen record for the TestGridScreen.
 */
public class TestScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kNameSort = kScreenRecordLastField + 1;
    public static final int kCodeSort = kNameSort + 1;
    public static final int kSortKey = kCodeSort + 1;
    public static final int kTestScreenRecordLastField = kSortKey;
    public static final int kTestScreenRecordFields = kSortKey - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public TestScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TestScreenRecord(RecordOwner screen)
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

    public static final String kTestScreenRecordFile = null;    // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kNameSort)
            field = new StringField(this, "NameSort", 10, null, null);
        if (iFieldSeq == kCodeSort)
            field = new StringField(this, "CodeSort", 10, null, null);
        if (iFieldSeq == kSortKey)
            field = new ShortField(this, "SortKey", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kTestScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
