/**
 * @(#)BaseFolderModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.db.*;

public interface BaseFolderModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    public static final String NAME = "Name";
    public static final String PARENT_FOLDER_ID = "ParentFolderID";

    public static final String PARENT_FOLDER_ID_KEY = "ParentFolderID";

    public static final String BASE_FOLDER_FILE = "Folder";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.BaseFolder";
    public static final String THICK_CLASS = "org.jbundle.main.db.BaseFolder";

}
