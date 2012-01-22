/**
 * @(#)MessageTimeoutScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.process;

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
import org.jbundle.main.msg.db.*;

/**
 *  MessageTimeoutScreenRecord - .
 */
public class MessageTimeoutScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kMessageStatusID = kScreenRecordLastField + 1;
    public static final int kStartTimeout = kMessageStatusID + 1;
    public static final int kEndTimeout = kStartTimeout + 1;
    public static final int kMessageTimeoutScreenRecordLastField = kEndTimeout;
    public static final int kMessageTimeoutScreenRecordFields = kEndTimeout - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public MessageTimeoutScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageTimeoutScreenRecord(RecordOwner screen)
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

    public static final String kMessageTimeoutScreenRecordFile = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kMessageStatusID)
            field = new MessageStatusField(this, "MessageStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kStartTimeout)
            field = new DateTimeField(this, "StartTimeout", Constants.DEFAULT_FIELD_LENGTH, null, new Date(0));
        if (iFieldSeq == kEndTimeout)
            field = new MessageTimeoutScreenRecord_EndTimeout(this, "EndTimeout", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageTimeoutScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
