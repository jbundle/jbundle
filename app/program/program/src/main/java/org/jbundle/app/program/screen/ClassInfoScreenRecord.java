/**
 * @(#)ClassInfoScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
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

    public static final int kClassProjectID = kScreenRecordLastField + 1;
    public static final int kName = kClassProjectID + 1;
    public static final int kPackage = kName + 1;
    public static final int kClassInfoScreenRecordLastField = kPackage;
    public static final int kClassInfoScreenRecordFields = kPackage - DBConstants.MAIN_FIELD + 1;
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

    public static final String kClassInfoScreenRecordFile = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kClassProjectID)
            field = new ClassProjectField(this, "ClassProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kName)
            field = new StringField(this, "Name", 10, null, null);
        if (iFieldSeq == kPackage)
            field = new StringField(this, "Package", 20, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kClassInfoScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
