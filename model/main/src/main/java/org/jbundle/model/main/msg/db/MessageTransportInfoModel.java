/**
 * @(#)MessageTransportInfoModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface MessageTransportInfoModel extends Rec
{

    //public static final String ID = ID;
    public static final String CODE = "Code";
    public static final String MESSAGE_PROCESS_INFO_ID = "MessageProcessInfoID";
    public static final String MESSAGE_TRANSPORT_ID = "MessageTransportID";
    public static final String MESSAGE_VERSION_ID = "MessageVersionID";
    public static final String ACTIVE = "Active";
    public static final String DEFAULT_TRANSPORT = "DefaultTransport";
    public static final String PROPERTIES = "Properties";

    public static final String MESSAGE_PROCESS_INFO_ID_KEY = "MessageProcessInfoID";
    public static final String MESSAGE_TRANSPORT_INFO_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageTransportInfoScreen";
    public static final String MESSAGE_TRANSPORT_INFO_GRID_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageTransportInfoGridScreen";

    public static final String MESSAGE_TRANSPORT_INFO_FILE = "MessageTransportInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageTransportInfo";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageTransportInfo";

}
