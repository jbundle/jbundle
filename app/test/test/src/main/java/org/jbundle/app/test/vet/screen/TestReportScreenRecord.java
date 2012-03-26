/**
 * @(#)TestReportScreenRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.screen.model.report.*;
import org.jbundle.app.test.vet.db.*;
import org.jbundle.main.db.*;
import org.jbundle.base.screen.model.util.*;

/**
 *  TestReportScreenRecord - Screen Record.
 */
public class TestReportScreenRecord extends ReportScreenRecord
{
    private static final long serialVersionUID = 1L;

    //public static final String REPORT_DATE = REPORT_DATE;
    //public static final String REPORT_TIME = REPORT_TIME;
    //public static final String REPORT_USER_ID = REPORT_USER_ID;
    //public static final String REPORT_PAGE = REPORT_PAGE;
    //public static final String REPORT_TOTAL = REPORT_TOTAL;
    //public static final String REPORT_KEY_AREA = REPORT_KEY_AREA;
    public static final String REPORT_COUNT = "ReportCount";
    public static final String VET_ID = "VetID";
    public static final String TEMPLATE = "template";
    public static final String FILEOUT = "fileout";
    public static final String SEND_MESSAGE_BY = "sendMessageBy";
    public static final String DESTINATION_ADDRESS = "destinationAddress";
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

    public static final String TEST_REPORT_SCREEN_RECORD_FILE = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //  field = new TestReportScreenRecord_ReportDate(this, REPORT_DATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 1)
        //  field = new TestReportScreenRecord_ReportTime(this, REPORT_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 2)
        //  field = new TestReportScreenRecord_ReportUserID(this, REPORT_USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 3)
        //  field = new ShortField(this, REPORT_PAGE, Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)1));
        if (iFieldSeq == 4)
            field = new IntegerField(this, REPORT_COUNT, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0));
        //if (iFieldSeq == 5)
        //  field = new CurrencyField(this, REPORT_TOTAL, Constants.DEFAULT_FIELD_LENGTH, null, new Double(0));
        //if (iFieldSeq == 6)
        //  field = new IntegerField(this, REPORT_KEY_AREA, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new VetPopupField(this, VET_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new StringField(this, TEMPLATE, 60, null, "report");
        if (iFieldSeq == 9)
            field = new StringField(this, FILEOUT, 60, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, SEND_MESSAGE_BY, 10, null, "Email");
        if (iFieldSeq == 11)
            field = new StringField(this, DESTINATION_ADDRESS, 60, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
