/**
 * @(#)ClassProjectModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.main.db.*;

public interface ClassProjectModel extends FolderModel
{
    public static enum CodeType {THICK, THIN, RESOURCE_PROPERTIES, RESOURCE_CODE, INTERFACE};
    public static final String CLASS_DETAIL_SCREEN = "ClassDetail";
    public static final String RESOURCE_DETAIL_SCREEN = "ResourceDetail";

    public static final String CLASS_PROJECT_FILE = "ClassProject";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ClassProject";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ClassProject";

}
