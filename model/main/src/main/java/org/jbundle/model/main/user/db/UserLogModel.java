/**
 * @(#)UserLogModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

import org.jbundle.model.db.*;

public interface UserLogModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String USER_ID = "UserID";
    public static final String LOG_TIME = "LogTime";
    public static final String MESSAGE = "Message";
    public static final String USER_LOG_TYPE_ID = "UserLogTypeID";

    public static final String USER_ID_KEY = "UserID";

    public static final String USER_LOG_FILE = "UserLog";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserLog";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserLog";
    /**
     * Add this log entry.
     */
    public void log(int iUserID, int iUserLogTypeID, String strMessage);

}
