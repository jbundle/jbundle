/**
 * @(#)MessageLogScreenRecord.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.main.db.*;
import org.jbundle.main.db.base.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.main.user.db.*;

/**
 *  MessageLogScreenRecord - .
 */
public class MessageLogScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String CONTACT_TYPE_ID = "ContactTypeID";
    public static final String CONTACT_ID = "ContactID";
    public static final String MESSAGE_INFO_TYPE_ID = "MessageInfoTypeID";
    public static final String MESSAGE_TYPE_ID = "MessageTypeID";
    public static final String MESSAGE_STATUS_ID = "MessageStatusID";
    public static final String MESSAGE_TRANSPORT_ID = "MessageTransportID";
    public static final String USER_ID = "UserID";
    public static final String START_DATE = "StartDate";
    public static final String END_DATE = "EndDate";
    public static final String SORT_KEY = "SortKey";
    public static final String REFERENCE_TYPE = "ReferenceType";
    public static final String REFERENCE_ID = "ReferenceID";
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

    public static final String MESSAGE_LOG_SCREEN_RECORD_FILE = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new ContactTypeLevelOneField(this, CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 1)
            field = new ContactField(this, CONTACT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 2)
            field = new MessageInfoTypeField(this, MESSAGE_INFO_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 3)
            field = new MessageTypeField(this, MESSAGE_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new MessageStatusField(this, MESSAGE_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new MessageTransportSelect(this, MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 6)
            field = new UserField(this, USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new DateField(this, START_DATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new DateField(this, END_DATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 9)
            field = new ShortField(this, SORT_KEY, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, REFERENCE_TYPE, 60, null, null);
        if (iFieldSeq == 11)
            field = new ReferenceField(this, REFERENCE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
