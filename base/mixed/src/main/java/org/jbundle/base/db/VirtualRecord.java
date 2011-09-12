/*
 *  @(#)VirtualRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BooleanField;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.field.EmptyField;
import org.jbundle.base.field.RecordChangedField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Constants;


/**
 *  VirtualRecord - A record with a last changed date and a "deleted" field.
Can be used where soft deletes, snapshots, and incremental
updates are needed.
Remember.. this record does not specify the key; typically an
extra key is needed which starts with the LastModified da.
 */
public class VirtualRecord extends Record
{
	private static final long serialVersionUID = 1L;

    public static final int kID = kRecordLastField + 1;
    public static final int kLastChanged = kID + 1;
    public static final int kDeleted = kLastChanged + 1;
    public static final int kVirtualRecordLastField = kDeleted;
    public static final int kVirtualRecordFields = kDeleted - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public VirtualRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public VirtualRecord(RecordOwner screen)
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

    public static final String kVirtualRecordFile = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kID)
            field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kLastChanged)
        {
            field = new RecordChangedField(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == kDeleted)
        {
            field = new BooleanField(this, "Deleted", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
            field.setHidden(true);
        }
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kVirtualRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
    }

}
