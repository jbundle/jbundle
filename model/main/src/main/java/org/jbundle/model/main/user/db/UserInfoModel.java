/**
 * @(#)UserInfoModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

public interface UserInfoModel extends org.jbundle.model.main.db.PropertiesRecordModel
{
    public static final String USER_NAME = "UserName";
    public static final String PASSWORD = "Password";
    public static final String USER_GROUP_ID = "UserGroupID";

    public static final String USER_INFO_FILE = "UserInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserInfo";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserInfo";

}
