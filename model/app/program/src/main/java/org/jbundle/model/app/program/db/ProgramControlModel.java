/**
 * @(#)ProgramControlModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface ProgramControlModel extends Rec
{

    //public static final String ID = ID;
    public static final String PROJECT_NAME = "ProjectName";
    public static final String BASE_DIRECTORY = "BaseDirectory";
    public static final String SOURCE_DIRECTORY = "SourceDirectory";
    public static final String CLASS_DIRECTORY = "ClassDirectory";
    public static final String ARCHIVE_DIRECTORY = "ArchiveDirectory";
    public static final String RESOURCE_TYPE = "ResourceType";
    public static final String CLASS_RESOURCE_TYPE = "ClassResourceType";
    public static final String PACKAGE_NAME = "PackageName";
    public static final String INTERFACE_PACKAGE = "InterfacePackage";
    public static final String THIN_PACKAGE = "ThinPackage";
    public static final String RESOURCE_PACKAGE = "ResourcePackage";
    public static final String LAST_PACKAGE_UPDATE = "LastPackageUpdate";
    public static final String PACKAGES_BASE_PATH = "PackagesBasePath";
    public static final String PACKAGES_PATH = "PackagesPath";

    public static final String PROGRAM_CONTROL_FILE = "ProgramControl";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ProgramControl";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ProgramControl";

}
