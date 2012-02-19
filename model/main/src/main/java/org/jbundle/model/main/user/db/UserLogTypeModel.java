/**
 * @(#)UserLogTypeModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

import org.jbundle.model.db.*;

public interface UserLogTypeModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";

    public static final String DESCRIPTION_KEY = "Description";
    public static final int LOGIN = 1;
    public static final int SCREEN = 2;

    public static final String USER_LOG_TYPE_FILE = "UserLogType";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserLogType";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserLogType";

}
