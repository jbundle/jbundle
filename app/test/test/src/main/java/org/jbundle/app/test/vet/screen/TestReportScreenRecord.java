/**
 * @(#)TestReportScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.screen;

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
import org.jbundle.base.screen.model.report.*;
import org.jbundle.app.test.vet.db.*;
import org.jbundle.main.db.*;

/**
 *  TestReportScreenRecord - Screen Record.
 */
public class TestReportScreenRecord extends ReportScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String VET_ID = "VetID";
    public static final int kVetID = kReportScreenRecordLastField + 1;
    public static final String REPORT_COUNT = "ReportCount";
    public static final int kReportCount = kVetID + 1;
    public static final String TEMPLATE = "template";
    public static final int ktemplate = kReportCount + 1;
    public static final String FILEOUT = "fileout";
    public static final int kfileout = ktemplate + 1;
    public static final String SEND_MESSAGE_BY = "sendMessageBy";
    public static final int ksendMessageBy = kfileout + 1;
    public static final String DESTINATION_ADDRESS = "destinationAddress";
    public static final int kdestinationAddress = ksendMessageBy + 1;
    public static final int kTestReportScreenRecordLastField = kdestinationAddress;
    public static final int kTestReportScreenRecordFields = kdestinationAddress - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public TestReportScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TestReportScreenRecord(RecordOwner screen)
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

    public static final String kTestReportScreenRecordFile = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kVetID)
            field = new VetPopupField(this, "VetID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kReportCount)
            field = new IntegerField(this, "ReportCount", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0));
        if (iFieldSeq == ktemplate)
            field = new StringField(this, "template", 60, null, "report");
        if (iFieldSeq == kfileout)
            field = new StringField(this, "fileout", 60, null, null);
        if (iFieldSeq == ksendMessageBy)
            field = new StringField(this, "sendMessageBy", 10, null, "Email");
        if (iFieldSeq == kdestinationAddress)
            field = new StringField(this, "destinationAddress", 60, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kTestReportScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
