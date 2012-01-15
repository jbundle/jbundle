/**
 * @(#)PackagesModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.packages.db;

import org.jbundle.model.main.db.*;

public interface PackagesModel extends FolderModel
{
    public static final String SCAN = "Scan";
    public static final String PACKAGES_SCREEN_CLASS = "org.jbundle.app.program.packages.screen.PackagesScreen";
    public static final String PACKAGES_GRID_SCREEN_CLASS = "org.jbundle.app.program.packages.screen.PackagesGridScreen";

    public static final String PACKAGES_FILE = "Packages";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.packages.db.Packages";
    public static final String THICK_CLASS = "org.jbundle.app.program.packages.db.Packages";

}
