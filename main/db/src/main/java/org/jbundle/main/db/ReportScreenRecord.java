/**
 * @(#)ReportScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db;

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
 *  ReportScreenRecord - The base record for screens.
 */
public class ReportScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kReportDate = kScreenRecordLastField + 1;
    public static final int kReportTime = kReportDate + 1;
    public static final int kReportUserID = kReportTime + 1;
    public static final int kReportPage = kReportUserID + 1;
    public static final int kReportCount = kReportPage + 1;
    public static final int kReportTotal = kReportCount + 1;
    public static final int kReportKeyArea = kReportTotal + 1;
    public static final int kReportScreenRecordLastField = kReportKeyArea;
    public static final int kReportScreenRecordFields = kReportKeyArea - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public ReportScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ReportScreenRecord(RecordOwner screen)
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

    public static final String kReportScreenRecordFile = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kReportDate)
            field = new ReportScreenRecord_ReportDate(this, "ReportDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kReportTime)
            field = new ReportScreenRecord_ReportTime(this, "ReportTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kReportUserID)
            field = new ReportScreenRecord_ReportUserID(this, "ReportUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kReportPage)
            field = new ShortField(this, "ReportPage", Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)1));
        if (iFieldSeq == kReportCount)
            field = new IntegerField(this, "ReportCount", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0));
        if (iFieldSeq == kReportTotal)
            field = new CurrencyField(this, "ReportTotal", Constants.DEFAULT_FIELD_LENGTH, null, new Double(0));
        if (iFieldSeq == kReportKeyArea)
            field = new IntegerField(this, "ReportKeyArea", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kReportScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
