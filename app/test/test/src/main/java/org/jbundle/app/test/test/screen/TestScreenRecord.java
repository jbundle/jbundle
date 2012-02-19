/**
 * @(#)TestScreenRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
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

    public static final String NAME_SORT = "NameSort";
    public static final String CODE_SORT = "CodeSort";
    public static final String SORT_KEY = "SortKey";
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

    public static final String TEST_SCREEN_RECORD_FILE = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new StringField(this, NAME_SORT, 10, null, null);
        if (iFieldSeq == 1)
            field = new StringField(this, CODE_SORT, 10, null, null);
        if (iFieldSeq == 2)
            field = new ShortField(this, SORT_KEY, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
