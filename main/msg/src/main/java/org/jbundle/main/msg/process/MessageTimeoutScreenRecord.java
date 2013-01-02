/**
 * @(#)MessageTimeoutScreenRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.process;

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
import org.jbundle.main.msg.db.*;

/**
 *  MessageTimeoutScreenRecord - .
 */
public class MessageTimeoutScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_STATUS_ID = "MessageStatusID";
    public static final String START_TIMEOUT = "StartTimeout";
    public static final String END_TIMEOUT = "EndTimeout";
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

    public static final String MESSAGE_TIMEOUT_SCREEN_RECORD_FILE = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new MessageStatusField(this, MESSAGE_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 1)
            field = new DateTimeField(this, START_TIMEOUT, Constants.DEFAULT_FIELD_LENGTH, null, new Date(0));
        if (iFieldSeq == 2)
            field = new MessageTimeoutScreenRecord_EndTimeout(this, END_TIMEOUT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
