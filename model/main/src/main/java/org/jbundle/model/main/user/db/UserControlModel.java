/**
 * @(#)UserControlModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

public interface UserControlModel extends org.jbundle.model.db.Rec
{
    public static final String ANON_USER_GROUP_ID = "AnonUserGroupID";
    public static final String ANON_USER_INFO_ID = "AnonUserInfoID";
    public static final String TEMPLATE_USER_ID = "TemplateUserID";

    public static final String USER_CONTROL_FILE = "UserControl";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserControl";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserControl";

}
