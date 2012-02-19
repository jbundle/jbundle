/**
 * @(#)UserResourceModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.user.db;

import org.jbundle.model.db.*;

public interface UserResourceModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String RESOURCE_CLASS = "ResourceClass";

    public static final String DESCRIPTION_KEY = "Description";
    public static final String USER_RESOURCE_SCREEN_CLASS = "org.jbundle.main.user.screen.UserResourceScreen";
    public static final String USER_RESOURCE_GRID_SCREEN_CLASS = "org.jbundle.main.user.screen.UserResourceGridScreen";

    public static final String USER_RESOURCE_FILE = "UserResource";
    public static final String THIN_CLASS = "org.jbundle.thin.main.user.db.UserResource";
    public static final String THICK_CLASS = "org.jbundle.main.user.db.UserResource";

}
