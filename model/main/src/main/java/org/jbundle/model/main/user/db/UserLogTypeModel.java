/**
 * @(#)UserLogTypeModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

import org.jbundle.model.db.*;

public interface UserLogTypeModel extends Rec
{
    public static final int LOGIN = 1;
    public static final int SCREEN = 2;

    public static final String USER_LOG_TYPE_FILE = "UserLogType";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserLogType";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserLogType";

}
