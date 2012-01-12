/**
 * @(#)MessageStatusModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

public interface MessageStatusModel extends org.jbundle.model.db.Rec
{
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";

    public static final String SENT = "Description";
    public static final String SENTOK = "Description";
    public static final String IGNORED = "Code";
    public static final String ERROR = "Code";
    public static final String TRX_ID_HOLD = "Code";
    public static final String UNKNOWN = "Code";
    public static final String RECEIVED = "Code";
    
    public static final String MESSAGE_STATUS_FILE = "MessageStatus";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageStatus";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageStatus";

}
