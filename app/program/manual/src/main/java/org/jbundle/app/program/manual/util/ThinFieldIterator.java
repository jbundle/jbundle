package org.jbundle.app.program.manual.util;

import org.jbundle.base.db.Record;

/**
 *
 */
 public class ThinFieldIterator extends FieldIterator
{
    /**
     *
     */
    public ThinFieldIterator()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ThinFieldIterator(Record recFileHdr, Record recClassInfo, Record recFieldData)
    {
        this();
        this.init(recFileHdr, recClassInfo, recFieldData);
    }
    /**
     * Constructor.
     */
    public void init(Record recFileHdr, Record recClassInfo, Record recFieldData)
    {
        super.init(recFileHdr, recClassInfo, recFieldData);
        
        m_bSharedOnly = false;
    }
    /**
     * Constructor.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Go through the fields in this class and add them to the field list.
     * @param strClassName The record class to read through.
     */
    protected void scanRecordsFields(String[] strClassNames)
    {
        for (int i = 1; i < strClassNames.length; i++)
        {
            this.scanRecordFields(strClassNames, i, false);
        }
    }
}
