/**
 * @(#)PurgeRecordsProcess.
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
import org.jbundle.app.program.db.*;

/**
 *  PurgeRecordsProcess - Delete all the records in the supplied record.
 */
public class PurgeRecordsProcess extends BaseProcessRecords
{
    /**
     * Default constructor.
     */
    public PurgeRecordsProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public PurgeRecordsProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
     * Process this record.
     */
    public boolean processThisRecord(Record record)
    {
        if (record != null)
        {   // Delete all the records
            this.disableAllListeners(record);
            if ((record.getDatabaseType() & (DBConstants.BASE_TABLE_CLASS | DBConstants.SHARED_TABLE)) == (DBConstants.BASE_TABLE_CLASS | DBConstants.SHARED_TABLE))
                this.initSharedRecord(record);
            record.close();
            try {
                while (record.hasNext())
                {
                    record.next();
                    record.edit();
                    record.remove();
                }
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    /**
     * InitSharedRecord Method.
     */
    public void initSharedRecord(Record record)
    {
        FieldListener listener = null;
        try {
            BaseField field = record.getSharedRecordTypeKey();
            field.addListener(listener = new InitOnceFieldHandler(null));
            field.setData(new Integer(0), true, DBConstants.INIT_MOVE);
            field.setData(new Integer(0), true, DBConstants.INIT_MOVE);
            if (field != null)
                if (field.getDataClass() == Integer.class)
            {
                for (int i = 1; ; i++)
                {
                    Integer intData = new Integer(i);
                    field.setData(intData);
                    record.addNew();
                    Record recShared = record.getTable().getCurrentTable().getRecord();
                    if (recShared == null)
                        break;
                    if (recShared == record)
                        break;
                    if (recShared.getField(field.getFieldName()).getValue() != i)
                        break;
                    this.disableAllListeners(recShared);
                }
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            if (listener != null)
                record.removeListener(listener, true);
        }
    }

}
