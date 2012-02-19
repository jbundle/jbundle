/**
 * @(#)MessageStatusModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface MessageStatusModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final String ICON = "Icon";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CODE_KEY = "Code";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String TRX_ID_HOLD = "HOLD";
    public static final String SENT = "SENT";
    public static final String SENTOK = "SENTOK";
    public static final String RECEIVED = "RECEIVED";
    public static final String ERROR = "ERROR";
    public static final String IGNORED = "IGNORED";

    public static final String MESSAGE_STATUS_FILE = "MessageStatus";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageStatus";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageStatus";

}
