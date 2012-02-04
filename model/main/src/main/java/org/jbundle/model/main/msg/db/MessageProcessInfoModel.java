/**
 * @(#)MessageProcessInfoModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.message.*;
import org.jbundle.model.db.*;
import org.jbundle.model.db.*;

public interface MessageProcessInfoModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CODE = "Code";
    public static final String DESCRIPTION = "Description";
    public static final String QUEUE_NAME_ID = "QueueNameID";
    public static final String REPLY_MESSAGE_PROCESS_INFO_ID = "ReplyMessageProcessInfoID";
    public static final String LOCAL_MESSAGE_PROCESS_INFO_ID = "LocalMessageProcessInfoID";
    public static final String MESSAGE_INFO_ID = "MessageInfoID";
    public static final String MESSAGE_TYPE_ID = "MessageTypeID";
    public static final String PROCESS_TYPE_ID = "ProcessTypeID";
    public static final String PROCESSOR_CLASS = "ProcessorClass";
    public static final String PROPERTIES = "Properties";
    public static final String DEFAULT_MESSAGE_TRANSPORT_ID = "DefaultMessageTransportID";
    public static final String INITIAL_MESSAGE_STATUS_ID = "InitialMessageStatusID";

    public static final String MESSAGE_INFO_ID_KEY = "MessageInfoID";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CODE_KEY = "Code";
    public static final String SAFE = "ota.safe";
    public static final String MESSAGE_PROCESS_INFO_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageProcessInfoScreen";
    public static final String MESSAGE_PROCESS_INFO_GRID_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageProcessInfoGridScreen";

    public static final String MESSAGE_PROCESS_INFO_FILE = "MessageProcessInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageProcessInfo";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageProcessInfo";
    /**
     * GetMessageProcessInfo Method.
     */
    public MessageProcessInfoModel getMessageProcessInfo(String strMessageKey);
    /**
     * GetMessageProcessInfo Method.
     */
    public MessageProcessInfoModel getMessageProcessInfo(String strMessageInfoType, String strContactType, String strRequestType, String strMessageProcessType, String strProcessType);
    /**
     * SetupMessageHeaderFromCode Method.
     */
    public boolean setupMessageHeaderFromCode(Message trxMessage, String strMessageCode, String strVersion);
    /**
     * Create the response message for this message.
     * @return the response message (or null if none).
     */
    public Message createReplyMessage(Message message);

}
