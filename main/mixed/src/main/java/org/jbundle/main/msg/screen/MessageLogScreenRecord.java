/**
 * @(#)MessageLogScreenRecord.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.main.db.*;
import org.jbundle.main.msg.db.base.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.main.user.db.*;

/**
 *  MessageLogScreenRecord - .
 */
public class MessageLogScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kContactTypeID = kScreenRecordLastField + 1;
    public static final int kContactID = kContactTypeID + 1;
    public static final int kMessageInfoTypeID = kContactID + 1;
    public static final int kMessageTypeID = kMessageInfoTypeID + 1;
    public static final int kMessageStatusID = kMessageTypeID + 1;
    public static final int kMessageTransportID = kMessageStatusID + 1;
    public static final int kUserID = kMessageTransportID + 1;
    public static final int kStartDate = kUserID + 1;
    public static final int kEndDate = kStartDate + 1;
    public static final int kSortKey = kEndDate + 1;
    public static final int kReferenceType = kSortKey + 1;
    public static final int kReferenceID = kReferenceType + 1;
    public static final int kMessageLogScreenRecordLastField = kReferenceID;
    public static final int kMessageLogScreenRecordFields = kReferenceID - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public MessageLogScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageLogScreenRecord(RecordOwner screen)
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

    public static final String kMessageLogScreenRecordFile = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kContactTypeID)
            field = new ContactTypeLevelOneField(this, "ContactTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kContactID)
            field = new ContactField(this, "ContactID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageInfoTypeID)
            field = new MessageInfoTypeField(this, "MessageInfoTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageTypeID)
            field = new MessageTypeField(this, "MessageTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageStatusID)
            field = new MessageStatusField(this, "MessageStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageTransportID)
            field = new MessageTransportSelect(this, "MessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kUserID)
            field = new UserField(this, "UserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kStartDate)
            field = new DateField(this, "StartDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEndDate)
            field = new DateField(this, "EndDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kSortKey)
            field = new ShortField(this, "SortKey", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kReferenceType)
            field = new StringField(this, "ReferenceType", 60, null, null);
        if (iFieldSeq == kReferenceID)
            field = new ReferenceField(this, "ReferenceID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageLogScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
