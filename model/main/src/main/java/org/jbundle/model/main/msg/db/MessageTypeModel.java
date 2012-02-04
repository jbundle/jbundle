/**
 * @(#)MessageTypeModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface MessageTypeModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CODE_KEY = "Code";
    public static final int MESSAGE_IN_ID = 2;
    public static final int MESSAGE_OUT_ID = 1;
    public static final String MESSAGE_IN = "MESSAGE_IN";
    public static final String MESSAGE_OUT = "MESSAGE_OUT";

    public static final String MESSAGE_TYPE_FILE = "MessageType";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageType";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageType";

}
