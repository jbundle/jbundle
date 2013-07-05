/**
 * @(#)ClassProjectModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.main.db.*;

public interface ClassProjectModel extends FolderModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String NAME = NAME;
    //public static final String PARENT_FOLDER_ID = PARENT_FOLDER_ID;
    //public static final String SEQUENCE = SEQUENCE;
    //public static final String COMMENT = COMMENT;
    //public static final String CODE = CODE;
    public static final String DESCRIPTION = "Description";
    public static final String SYSTEM_CLASSES = "SystemClasses";
    public static final String PACKAGE_NAME = "PackageName";
    public static final String PROJECT_PATH = "ProjectPath";
    public static final String INTERFACE_PACKAGE = "InterfacePackage";
    public static final String INTERFACE_PROJECT_PATH = "InterfaceProjectPath";
    public static final String THIN_PACKAGE = "ThinPackage";
    public static final String THIN_PROJECT_PATH = "ThinProjectPath";
    public static final String RESOURCE_PACKAGE = "ResourcePackage";
    public static final String RES_PROJECT_PATH = "ResProjectPath";
    public static final String PROPERTIES = "Properties";
    public static final String ARTIFACT_ID = "ArtifactId";
    public static final String GROUP_ID = "GroupId";

    public static final String NAME_KEY = "Name";
    public static enum CodeType {THICK, THIN, RESOURCE_PROPERTIES, RESOURCE_CODE, INTERFACE};
    public static final String CLASS_DETAIL_SCREEN = "ClassDetail";
    public static final String RESOURCE_DETAIL_SCREEN = "ResourceDetail";
    public static final String CLASS_PROJECT_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassProjectScreen";
    public static final String CLASS_PROJECT_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassProjectGridScreen";

    public static final String CLASS_PROJECT_FILE = "ClassProject";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ClassProject";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ClassProject";

}
