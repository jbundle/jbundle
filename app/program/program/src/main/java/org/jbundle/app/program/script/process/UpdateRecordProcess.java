/**
 * @(#)UpdateRecordProcess.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.process;

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
import org.jbundle.base.thread.*;

/**
 *  UpdateRecordProcess - .
 */
public class UpdateRecordProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public UpdateRecordProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public UpdateRecordProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
     * Run Method.
     */
    public void run()
    {
        String strClassName = this.getProperty(DBParams.RECORD);
        Record record = Record.makeRecordFromClassName(strClassName, this);
        String strObjectID = this.getProperty(DBConstants.OBJECT_ID);
        
        try {
            record.addNew();
            record.getCounterField().setString(strObjectID);
            if (record.seek(DBConstants.EQUALS))
            {
                String strField = this.getProperty(DBParams.FIELD);
                String strValue = this.getProperty(DBParams.VALUE);
                if ("[random]".equalsIgnoreCase(strValue))
                    strValue = Double.toString(Math.random());
                BaseField field = record.getField(strField);
                if (field != null)
                {
                    record.edit();
                    field.setString(strValue);
                    record.set();
                }
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        
        record.free();
    }

}
