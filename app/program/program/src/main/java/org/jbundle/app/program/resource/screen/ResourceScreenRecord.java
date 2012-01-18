/**
 * @(#)ResourceScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.resource.screen;

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

/**
 *  ResourceScreenRecord - .
 */
public class ResourceScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kLanguage = kScreenRecordLastField + 1;
    public static final int kLocale = kLanguage + 1;
    public static final int kResourceScreenRecordLastField = kLocale;
    public static final int kResourceScreenRecordFields = kLocale - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public ResourceScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ResourceScreenRecord(RecordOwner screen)
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

    public static final String kResourceScreenRecordFile = null;    // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kLanguage)
        {
            field = new StringField(this, "Language", 2, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kLocale)
        {
            field = new StringField(this, "Locale", 2, null, null);
            field.setNullable(false);
        }
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kResourceScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
