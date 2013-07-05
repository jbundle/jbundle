/**
 * @(#)MergeData.
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
 *  MergeData - .
 */
public class MergeData extends BaseFixData
{
    protected Record m_recMerge = null;
    /**
     * Default constructor.
     */
    public MergeData()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MergeData(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        m_recMerge = null;
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        Record recDest = this.getMainRecord();
        Iterator<Record> source = this.getSource();
        
        while (source.hasNext())
        {
            Record recSource = source.next();
            
            this.mergeSourceRecord(recSource, recDest);
        }
    }
    /**
     * MergeSourceRecord Method.
     */
    public void mergeSourceRecord(Record recSource, Record recDest)
    {
        try {
            boolean bFound = this.readDestRecord(recSource, recDest);
            if (!bFound)
                recDest.addNew();
            else
                recDest.edit();
            this.mergeSourceData(recSource, recDest, bFound);
            if (recDest.isModified())
            {
                if (recDest.getEditMode() == DBConstants.EDIT_ADD)
                {
                    boolean bAutoSequence = true;
                    if (!recDest.getCounterField().isNull())
                        bAutoSequence = false;
                    recDest.setAutoSequence(bAutoSequence);
                    recDest.add();
                    recDest.setAutoSequence(true);
                }
                else if (recDest.getEditMode() == DBConstants.EDIT_IN_PROGRESS)
                    recDest.set();
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }
    /**
     * Get the record to fill with the merge data.
     */
    public Record getMergeRecord()
    {
        if (m_recMerge == null)
            m_recMerge = this.openMergeRecord();
        return m_recMerge;
    }
    /**
     * OpenMergeRecord Method.
     */
    public Record openMergeRecord()
    {
        String strClassName = this.getProperty("mergeRecordClass");
        if (strClassName != null)
            return Record.makeRecordFromClassName(strClassName, this);
        return null;    // Or override this
    }
    /**
     * Merge this source record with the destination record.
     * @param recSource
     * @param recDest.
     */
    public void mergeSourceData(Record recSource, Record recDest, boolean bFound)
    {
        for (int iFieldSeq = 0; iFieldSeq < recSource.getFieldCount(); iFieldSeq++)
        {
            BaseField fldSource = recSource.getField(iFieldSeq);
            BaseField fldDest = recDest.getField(fldSource.getFieldName());
            if (fldDest != null)
                if (!fldSource.isNull())
                    fldDest.moveFieldToThis(fldSource);
        }
    }
    /**
     * Given this source record, read the destination record.
     * @param recSource The source record
     * @param recDest The destination record
     * @return True if found.
     */
    public boolean readDestRecord(FieldList recSource, Record recDest)
    {
        FieldInfo fldSecond = recSource.getField("Name");
        if (fldSecond == null)
            fldSecond = recSource.getField("Description");
        if (fldSecond == null)
            return false;
        recDest = BaseFixData.getRecordFromDescription(fldSecond.toString(), fldSecond.getFieldName(), recDest);
        return (recDest != null);
    }
    /**
     * GetSource Method.
     */
    public Iterator<Record> getSource()
    {
        // Override typically
        return new RecordSource(this.getMergeRecord());
    }

}
