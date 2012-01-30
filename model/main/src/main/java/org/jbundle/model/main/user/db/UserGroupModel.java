/**
 * @(#)UserGroupModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

import org.jbundle.model.main.db.*;

public interface UserGroupModel extends PropertiesRecordModel
{

    //public static final String ID = ID;
    public static final String DESCRIPTION = "Description";
    public static final String ACCESS_MAP = "AccessMap";

    public static final String DESCRIPTION_KEY = "Description";
    public static final int RES_USER = 1;
    public static final String USER_DETAIL = "User detail";
    public static final String USER_ICON = "Distribution";
    public static final String USER_GROUP_SCREEN_CLASS = "org.jbundle.main.user.screen.UserGroupScreen";
    public static final String USER_GROUP_GRID_SCREEN_CLASS = "org.jbundle.main.user.screen.UserGroupGridScreen";

    public static final String USER_GROUP_FILE = "UserGroup";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserGroup";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserGroup";

}
