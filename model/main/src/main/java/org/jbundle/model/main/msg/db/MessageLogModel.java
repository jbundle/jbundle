/**
 * @(#)MessageLogModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

public interface MessageLogModel extends org.jbundle.model.base.db.VirtualRecordModel
{

    public static final String MESSAGE_LOG_FILE = "MessageLog";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageLog";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageLog";

}
