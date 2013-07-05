/**
 * @(#)MessageInfoScreenRecord.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.screen;

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
import org.jbundle.main.db.base.*;

/**
 *  MessageInfoScreenRecord - .
 */
public class MessageInfoScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_INFO_COMPARE_ID = "MessageInfoCompareID";
    public static final String MESSAGE_INFO_ID = "MessageInfoID";
    public static final String MESSAGE_PROCESS_INFO_ID = "MessageProcessInfoID";
    public static final String MESSAGE_TYPE_ID = "MessageTypeID";
    public static final String MESSAGE_INFO_TYPE_ID = "MessageInfoTypeID";
    public static final String PROCESS_TYPE_ID = "ProcessTypeID";
    public static final String CONTACT_TYPE_ID = "ContactTypeID";
    public static final String REQUEST_TYPE_ID = "RequestTypeID";
    /**
     * Default constructor.
     */
    public MessageInfoScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageInfoScreenRecord(RecordOwner screen)
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

    public static final String MESSAGE_INFO_SCREEN_RECORD_FILE = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new MessageInfoField(this, MESSAGE_INFO_COMPARE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 1)
            field = new MessageInfoField(this, MESSAGE_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 2)
            field = new MessageProcessInfoField(this, MESSAGE_PROCESS_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 3)
            field = new MessageTypeField(this, MESSAGE_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new MessageInfoTypeField(this, MESSAGE_INFO_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new ProcessTypeField(this, PROCESS_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 6)
            field = new ContactTypeField(this, CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new RequestTypeField(this, REQUEST_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
