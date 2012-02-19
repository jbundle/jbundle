/**
 * @(#)ClassIncludesModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface ClassIncludesModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CLASS_INFO_ID = "ClassInfoID";
    public static final String CLASS_INCLUDES_CLASS = "ClassIncludesClass";

    public static final String CLASS_INFO_ID_KEY = "ClassInfoID";
    public static final String CLASS_INCLUDES_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassIncludesScreen";
    public static final String CLASS_INCLUDES_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassIncludesGridScreen";

    public static final String CLASS_INCLUDES_FILE = "ClassIncludes";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ClassIncludes";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ClassIncludes";

}
