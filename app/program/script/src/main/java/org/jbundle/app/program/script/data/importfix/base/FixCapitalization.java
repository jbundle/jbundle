/**
 * @(#)FixCapitalization.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.data.importfix.base;

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

/**
 *  FixCapitalization - .
 */
public class FixCapitalization extends ScanData
{
    /**
     * Default constructor.
     */
    public FixCapitalization()
    {
        super();
    }
    /**
     * Constructor.
     */
    public FixCapitalization(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * FixRecord Method.
     */
    public void fixRecord(Record record)
    {
        super.fixRecord(record);
        if (this.getProperty("field") != null)
        {
            BaseField field = this.getMainRecord().getField(this.getProperty("field").toString());
            if (field != null)
                this.fixCapitalization(field);
        }
    }

}
