/**
 * @(#)MessageInfoModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface MessageInfoModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final String MESSAGE_CLASS = "MessageClass";
    public static final String MESSAGE_PROPERTIES = "MessageProperties";
    public static final String MESSAGE_INFO_TYPE_ID = "MessageInfoTypeID";
    public static final String REVERSE_MESSAGE_INFO_ID = "ReverseMessageInfoID";
    public static final String CONTACT_TYPE_ID = "ContactTypeID";
    public static final String REQUEST_TYPE_ID = "RequestTypeID";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CODE_KEY = "Code";

    public static final String MESSAGE_INFO_TYPE_ID_KEY = "MessageInfoTypeID";
    public static final String PROCESS_DETAIL_SCREEN = "Process detail";
    public static final String THICK_APPLICATION = "org.jbundle.main.msg.app.MessageInfoApplication";
    public static final String MESSAGE_INFO_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageInfoScreen";
    public static final String MESSAGE_INFO_GRID_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageInfoGridScreen";

    public static final String MESSAGE_INFO_FILE = "MessageInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageInfo";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageInfo";

}
