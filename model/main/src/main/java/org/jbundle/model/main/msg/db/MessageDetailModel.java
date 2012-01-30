/**
 * @(#)MessageDetailModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.main.db.*;

public interface MessageDetailModel extends PropertiesRecordModel
{

    //public static final String ID = ID;
    //public static final String PROPERTIES = PROPERTIES;
    public static final String CONTACT_TYPE_ID = "ContactTypeID";
    public static final String PERSON_ID = "PersonID";
    public static final String MESSAGE_TRANSPORT_ID = "MessageTransportID";
    public static final String MESSAGE_PROCESS_INFO_ID = "MessageProcessInfoID";
    public static final String DESTINATION_SITE = "DestinationSite";
    public static final String DESTINATION_PATH = "DestinationPath";
    public static final String RETURN_SITE = "ReturnSite";
    public static final String RETURN_PATH = "ReturnPath";
    public static final String XSLT_DOCUMENT = "XSLTDocument";
    public static final String DEFAULT_MESSAGE_VERSION_ID = "DefaultMessageVersionID";
    public static final String DEFAULT_MESSAGE_TRANSPORT_ID = "DefaultMessageTransportID";
    public static final String INITIAL_MANUAL_TRANSPORT_STATUS_ID = "InitialManualTransportStatusID";

    public static final String CONTACT_TYPE_ID_KEY = "ContactTypeID";
    public static final String MESSAGE_DETAIL_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageDetailScreen";
    public static final String MESSAGE_DETAIL_GRID_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageDetailGridScreen";

    public static final String MESSAGE_DETAIL_FILE = "MessageDetail";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageDetail";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageDetail";

}
