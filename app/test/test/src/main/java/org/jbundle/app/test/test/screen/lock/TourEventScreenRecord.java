/*
 *  @(#)TourEventScreenRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.test.screen.lock;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.thin.base.db.*;


/**
 *  TourEventScreenRecord - .
 */
public class TourEventScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kActionCutoffDate = kScreenRecordLastField + 1;
    public static final int kBookingUpdate = kActionCutoffDate + 1;
    public static final int kTourUpdate = kBookingUpdate + 1;
    public static final int RUN_PROCESS_IN = kTourUpdate + 1;
    public static final int kTourEventScreenRecordLastField = RUN_PROCESS_IN;
    public static final int kTourEventScreenRecordFields = RUN_PROCESS_IN - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public TourEventScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TourEventScreenRecord(RecordOwner screen)
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

    public static final String kTourEventScreenRecordFile = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kActionCutoffDate)
            field = new TourEventScreenRecord_ActionCutoffDate(this, "ActionCutoffDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kBookingUpdate)
            field = new BooleanField(this, "BookingUpdate", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(true));
        if (iFieldSeq == kTourUpdate)
            field = new BooleanField(this, "TourUpdate", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(true));
        if (iFieldSeq == RUN_PROCESS_IN)
            field = new RunProcessInField(this, "RunProcessIn", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kTourEventScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
