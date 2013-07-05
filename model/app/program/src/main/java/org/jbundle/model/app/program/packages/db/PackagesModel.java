/**
 * @(#)PackagesModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.packages.db;

import org.jbundle.model.main.db.*;

public interface PackagesModel extends FolderModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String NAME = NAME;
    //public static final String PARENT_FOLDER_ID = PARENT_FOLDER_ID;
    //public static final String SEQUENCE = SEQUENCE;
    //public static final String COMMENT = COMMENT;
    //public static final String CODE = CODE;
    public static final String CLASS_PROJECT_ID = "ClassProjectID";
    public static final String CODE_TYPE = "CodeType";
    public static final String PART_ID = "PartID";
    public static final String RECURSIVE = "Recursive";
    public static final String EXCLUDE = "Exclude";
    public static final String MANUAL = "Manual";
    public static final String LAST_UPDATED = "LastUpdated";

    public static final String NAME_KEY = "Name";

    public static final String PART_ID_KEY = "PartID";
    public static final String SCAN = "Scan";
    public static final String PACKAGES_SCREEN_CLASS = "org.jbundle.app.program.packages.screen.PackagesScreen";
    public static final String PACKAGES_GRID_SCREEN_CLASS = "org.jbundle.app.program.packages.screen.PackagesGridScreen";

    public static final String PACKAGES_FILE = "Packages";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.packages.db.Packages";
    public static final String THICK_CLASS = "org.jbundle.app.program.packages.db.Packages";

}
