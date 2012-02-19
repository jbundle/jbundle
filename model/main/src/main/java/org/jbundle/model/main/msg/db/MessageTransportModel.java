/**
 * @(#)MessageTransportModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.*;
import org.jbundle.model.db.*;

public interface MessageTransportModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final String PROPERTIES = "Properties";
    public static final String MESSAGE_TRANSPORT_TYPE = "MessageTransportType";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CODE_KEY = "Code";
    public static final String DIRECT = "Direct";
    public static final String EMAIL = "Email";
    public static final String FAX = "Fax";
    public static final String HTML = "HTML";
    public static final String LOCAL = "Local";
    public static final String MAIL = "Mail";
    public static final String SCREEN = "Screen";
    public static final String SOAP = "SOAP";
    public static final String XML = "XML";
    public static final String UPS = "UPS";
    public static final String MANUAL = "Manual";
    public static final String CLIENT = "Client";
    public static final String SERVER = "Server";
    public static final String DEFAULT = MessageTransportModel.CLIENT;
    public static final String SEND_MESSAGE_BY_PARAM = "sendMessageBy";
    public static final String TRANSPORT_ID_PARAM = "transportID";
    public static final String TRANSPORT_CLASS_NAME_PARAM = "transportClassName";
    public static final String TRANSPORT_TYPE_PARAM = "transportType";
    public static final String REQUEST_TYPE_PARAM = "requestType";
    public static final String MANUAL_RESPONSE_PARAM = "Manual";

    public static final String MESSAGE_TRANSPORT_FILE = "MessageTransport";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageTransport";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageTransport";
    /**
     * Get the message transport for this type
     * @param messageTransportType
     * @returns The concrete BaseMessageTransport implementation.
     */
    public Object createMessageTransport(String messageTransportType, Task task);

}
