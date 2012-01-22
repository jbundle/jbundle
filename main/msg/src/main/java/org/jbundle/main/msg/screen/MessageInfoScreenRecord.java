/**
 * @(#)MessageInfoScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.screen;

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
import org.jbundle.main.db.base.*;

/**
 *  MessageInfoScreenRecord - .
 */
public class MessageInfoScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kMessageInfoCompareID = kScreenRecordLastField + 1;
    public static final int kMessageInfoID = kMessageInfoCompareID + 1;
    public static final int kMessageProcessInfoID = kMessageInfoID + 1;
    public static final int kMessageTypeID = kMessageProcessInfoID + 1;
    public static final int kMessageInfoTypeID = kMessageTypeID + 1;
    public static final int kProcessTypeID = kMessageInfoTypeID + 1;
    public static final int kContactTypeID = kProcessTypeID + 1;
    public static final int kRequestTypeID = kContactTypeID + 1;
    public static final int kMessageInfoScreenRecordLastField = kRequestTypeID;
    public static final int kMessageInfoScreenRecordFields = kRequestTypeID - DBConstants.MAIN_FIELD + 1;
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

    public static final String kMessageInfoScreenRecordFile = null;   // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kMessageInfoCompareID)
            field = new MessageInfoField(this, "MessageInfoCompareID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageInfoID)
            field = new MessageInfoField(this, "MessageInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageProcessInfoID)
            field = new MessageProcessInfoField(this, "MessageProcessInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageTypeID)
            field = new MessageTypeField(this, "MessageTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageInfoTypeID)
            field = new MessageInfoTypeField(this, "MessageInfoTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProcessTypeID)
            field = new ProcessTypeField(this, "ProcessTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kContactTypeID)
            field = new ContactTypeField(this, "ContactTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kRequestTypeID)
            field = new RequestTypeField(this, "RequestTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageInfoScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
