/**
 * @(#)FolderModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.main.db.*;

public interface FolderModel extends BaseFolderModel
{
    public static final String COMMENT = "Comment";
    public static final String CODE = "Code";
    public static final String FOLDER_SCREEN_CLASS = "org.jbundle.main.screen.FolderScreen";
    public static final String FOLDER_GRID_SCREEN_CLASS = "org.jbundle.main.screen.FolderGridScreen";

    public static final String FOLDER_FILE = "Folder";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.Folder";
    public static final String THICK_CLASS = "org.jbundle.main.db.Folder";

}
