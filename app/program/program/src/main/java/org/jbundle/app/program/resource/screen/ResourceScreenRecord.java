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
import org.jbundle.base.model.*;
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

    public static final String LANGUAGE = "Language";
    public static final String LOCALE = "Locale";
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

    public static final String RESOURCE_SCREEN_RECORD_FILE = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
        {
            field = new StringField(this, LANGUAGE, 2, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == 1)
        {
            field = new StringField(this, LOCALE, 2, null, null);
            field.setNullable(false);
        }
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
