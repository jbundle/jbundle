/**
 * @(#)ResourceModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.resource.db;

import org.jbundle.model.db.*;

public interface ResourceModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CODE = "Code";
    public static final String DESCRIPTION = "Description";
    public static final String LOCATION = "Location";
    public static final String TYPE = "Type";
    public static final String BASE_RESOURCE_ID = "BaseResourceID";
    public static final String CLASS_PROJECT_ID = "ClassProjectID";

    public static final String CODE_KEY = "Code";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CLASS_PROJECT_ID_KEY = "ClassProjectID";
    public static final String RESOURCE_GRID_SCREEN_CLASS = "org.jbundle.app.program.resource.screen.ResourceGridScreen";
    public static final String RESOURCE_SCREEN_CLASS = "org.jbundle.app.program.resource.screen.ResourceScreen";

    public static final String RESOURCE_FILE = "Resource";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.resource.db.Resource";
    public static final String THICK_CLASS = "org.jbundle.app.program.resource.db.Resource";

}
