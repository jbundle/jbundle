/**
 * @(#)ClassVars.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

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
 *  ClassVars - .
 */
public class ClassVars extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kClassKey = kScreenRecordLastField + 1;
    public static final int kClassVarsLastField = kClassKey;
    public static final int kClassVarsFields = kClassKey - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public ClassVars()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ClassVars(RecordOwner screen)
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

    public static final String kClassVarsFile = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kClassKey)
            field = new ShortField(this, "ClassKey", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kClassVarsLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
