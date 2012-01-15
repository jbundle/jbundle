/**
 * @(#)UserInfoModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

import org.jbundle.model.main.db.*;

public interface UserInfoModel extends PropertiesRecordModel
{
    public static final String USER_NAME = "UserName";
    public static final String PASSWORD = "Password";
    public static final String USER_GROUP_ID = "UserGroupID";
    public static final String ENTRY_SCREEN = "Entry";
    public static final String VERBOSE_MAINT_SCREEN = "Verbose maintenance";
    public static final String LOGIN_SCREEN = "Login";
    public static final String PREFERENCES_SCREEN = "Preferences";
    public static final String PASSWORD_CHANGE_SCREEN = "Password change";
    public static final String DEFAULT = "";
    public static final String YES_DEFAULT = "";
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String ENGLISH = "en";
    public static final String SPANISH = "es";
    public static final String HOME_PAGE_ONLY = "";
    public static final String WEBSTART = "WebStart";
    public static final String PLUG_IN = "Plug-in";
    public static final String FULL_MENU = "";
    public static final String ICONS_ONLY = "IconsOnly";
    public static final String NO_ICONS = "No";
    public static final String MAINT_SCREEN_CLASS = "org.jbundle.main.user.screen.UserEntryScreen";
    public static final String GRID_SCREEN_CLASS = "org.jbundle.main.user.screen.UserInfoGridScreen";
    public static final String DETAIL_SCREEN_CLASS = "org.jbundle.main.user.screen.UserRegistrationGridScreen";
    public static final String VERBOSE_SCREEN_CLASS = "org.jbundle.main.user.screen.UserInfoScreen";
    public static final String LOGIN_SCREEN_CLASS = "org.jbundle.main.user.screen.UserLoginScreen";
    public static final String PREFERENCES_SCREEN_CLASS = "org.jbundle.main.user.screen.UserPreferenceScreen";
    public static final String PASSWORD_SCREEN_CLASS = "org.jbundle.main.user.screen.UserPasswordChange";
    public static final String USER_ENTRY_SCREEN_CLASS = "org.jbundle.main.user.screen.UserEntryScreen";
    public static final String USER_INFO_GRID_SCREEN_CLASS = "org.jbundle.main.user.screen.UserInfoGridScreen";

    public static final String USER_INFO_FILE = "UserInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserInfo";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserInfo";
    /**
     * GetUserInfo Method.
     */
    public boolean getUserInfo(String strUser, boolean bForceRead);
    /**
     * SetupNewUserHandler Method.
     */
    public void setupNewUserHandler();

}
