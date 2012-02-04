/**
 * @(#)UserRegistrationModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

import org.jbundle.model.db.*;

public interface UserRegistrationModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String USER_ID = "UserID";
    public static final String CODE = "Code";
    public static final String PROPERTIES = "Properties";

    public static final String USER_ID_KEY = "UserID";
    public static final String USER_REGISTRATION_GRID_SCREEN_CLASS = "org.jbundle.main.user.screen.UserRegistrationGridScreen";

    public static final String USER_REGISTRATION_FILE = "UserRegistration";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserRegistration";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserRegistration";

}
