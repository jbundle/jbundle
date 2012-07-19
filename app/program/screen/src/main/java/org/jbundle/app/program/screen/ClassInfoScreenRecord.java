/**
 * @(#)ClassInfoScreenRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.db.*;

/**
 *  ClassInfoScreenRecord - .
 */
public class ClassInfoScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String CLASS_PROJECT_ID = "ClassProjectID";
    public static final String NAME = "Name";
    public static final String PACKAGE = "Package";
    /**
     * Default constructor.
     */
    public ClassInfoScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ClassInfoScreenRecord(RecordOwner screen)
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

    public static final String CLASS_INFO_SCREEN_RECORD_FILE = null;    // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new ClassProjectField(this, CLASS_PROJECT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 1)
            field = new StringField(this, NAME, 10, null, null);
        if (iFieldSeq == 2)
            field = new StringField(this, PACKAGE, 20, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
