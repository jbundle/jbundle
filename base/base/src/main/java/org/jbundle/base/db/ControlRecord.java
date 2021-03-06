/*
 *  @(#)ControlRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

import java.util.Map;

import org.jbundle.base.db.event.ControlFileHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.EmptyField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.screen.ScreenParent;


/**
 *  ControlRecord - Control file record.
 */
public class ControlRecord extends VirtualRecord
{
	private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kControlRecordLastField = kVirtualRecordLastField;
    public static final int kControlRecordFields = kVirtualRecordLastField - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public ControlRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ControlRecord(RecordOwner screen)
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

    public static final String kControlRecordFile = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == kID)
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kControlRecordLastField)
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
        this.addListener(new ControlFileHandler(null));
    }
    /**
     * MakeScreen Method.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String, Object> properties)
    {
        iDocMode = iDocMode | ScreenConstants.MAINT_MODE;   // Control files are always forms.
        return super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
    }
    /**
     * Get the starting ID for this table.
     * Override this for different behavior.
     * @return The starting id
     */
    public int getStartingID()
    {
        return 1;   // Always for a control record
    }

}
