/**
 * @(#)UserPermissionModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

import org.jbundle.model.db.*;

public interface UserPermissionModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String USER_GROUP_ID = "UserGroupID";
    public static final String USER_RESOURCE_ID = "UserResourceID";
    public static final String LOGIN_LEVEL = "LoginLevel";
    public static final String ACCESS_LEVEL = "AccessLevel";

    public static final String USER_GROUP_ID_KEY = "UserGroupID";

    public static final String USER_RESOURCE_ID_KEY = "UserResourceID";
    public static final String USER_PERMISSION_GRID_SCREEN_CLASS = "org.jbundle.main.user.screen.UserPermissionGridScreen";
    public static final String USER_PERMISSION_SCREEN_CLASS = "org.jbundle.main.user.screen.UserPermissionScreen";

    public static final String USER_PERMISSION_FILE = "UserPermission";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserPermission";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserPermission";

}
