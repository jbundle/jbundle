/**
 * @(#)ClassResourceModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface ClassResourceModel extends Rec
{
    public static final String CLASS_RESOURCE_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassResourceScreen";
    public static final String CLASS_RESOURCE_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassResourceGridScreen";

    public static final String CLASS_RESOURCE_FILE = "ClassResource";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ClassResource";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ClassResource";

}
