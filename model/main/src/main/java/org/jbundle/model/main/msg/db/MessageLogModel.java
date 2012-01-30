/**
 * @(#)MessageLogModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.message.*;
import org.jbundle.model.db.*;

public interface MessageLogModel extends Rec
{

    //public static final String ID = ID;
    public static final String MESSAGE_INFO_TYPE_ID = "MessageInfoTypeID";
    public static final String MESSAGE_TYPE_ID = "MessageTypeID";
    public static final String MESSAGE_STATUS_ID = "MessageStatusID";
    public static final String MESSAGE_TRANSPORT_ID = "MessageTransportID";
    public static final String MESSAGE_PROCESS_INFO_ID = "MessageProcessInfoID";
    public static final String CONTACT_TYPE_ID = "ContactTypeID";
    public static final String CONTACT_ID = "ContactID";
    public static final String DESCRIPTION = "Description";
    public static final String MESSAGE_TIME = "MessageTime";
    public static final String TIMEOUT_SECONDS = "TimeoutSeconds";
    public static final String TIMEOUT_TIME = "TimeoutTime";
    public static final String USER_ID = "UserID";
    public static final String REFERENCE_TYPE = "ReferenceType";
    public static final String REFERENCE_ID = "ReferenceID";
    public static final String RESPONSE_MESSAGE_LOG_ID = "ResponseMessageLogID";
    public static final String MESSAGE_HEADER_PROPERTIES = "MessageHeaderProperties";
    public static final String MESSAGE_INFO_PROPERTIES = "MessageInfoProperties";
    public static final String MESSAGE_TRANSPORT_PROPERTIES = "MessageTransportProperties";
    public static final String MESSAGE_CLASS_NAME = "MessageClassName";
    public static final String MESSAGE_HEADER_CLASS_NAME = "MessageHeaderClassName";
    public static final String MESSAGE_DATA_CLASS_NAME = "MessageDataClassName";
    public static final String EXTERNAL_MESSAGE_CLASS_NAME = "ExternalMessageClassName";
    public static final String MESSAGE_QUEUE_NAME = "MessageQueueName";
    public static final String MESSAGE_QUEUE_TYPE = "MessageQueueType";
    public static final String MESSAGE_DATA_TYPE = "MessageDataType";
    public static final String XML_MESSAGE_DATA = "XMLMessageData";
    public static final String MESSAGE_DATA = "MessageData";
    public static final String ERROR_TEXT = "ErrorText";

    public static final String REFERENCE_ID_KEY = "ReferenceID";

    public static final String CONTACT_TYPE_ID_KEY = "ContactTypeID";

    public static final String MESSAGE_TIME_KEY = "MessageTime";

    public static final String TIMEOUT_KEY = "Timeout";
    public static final String MESSAGE_ICON = "Transaction";
    public static final String SOURCE_ICON = "Source";
    public static final String MESSAGE_SCREEN = "Message Screen";
    public static final String SOURCE_SCREEN = "Source";
    public static final String MESSAGE_LOG_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageLogScreen";
    public static final String MESSAGE_LOG_GRID_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageLogGridScreen";

    public static final String MESSAGE_LOG_FILE = "MessageLog";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageLog";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageLog";
    /**
     * CreateMessage Method.
     */
    public Message createMessage(String strTrxID);
    /**
     * Get this property from this record.
     */
    public String getProperty(String strKey);
    /**
     * Get this record.
     */
    public MessageLogModel getMessageLog(String ID);

}
