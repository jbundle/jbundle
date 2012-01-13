/**
 * @(#)MessageInfoTypeModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface MessageInfoTypeModel extends Rec
{
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final int REQUEST_ID = 1;
    public static final int REPLY_ID = 2;
    public static final String REQUEST = "REQUEST";
    public static final String REPLY = "REPLY";

    public static final String MESSAGE_INFO_TYPE_FILE = "MessageInfoType";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageInfoType";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageInfoType";

}
