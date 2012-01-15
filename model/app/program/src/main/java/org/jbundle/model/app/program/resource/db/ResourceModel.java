/**
 * @(#)ResourceModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.resource.db;

import org.jbundle.model.db.*;

public interface ResourceModel extends Rec
{
    public static final String RESOURCE_GRID_SCREEN_CLASS = "org.jbundle.app.program.resource.screen.ResourceGridScreen";
    public static final String RESOURCE_SCREEN_CLASS = "org.jbundle.app.program.resource.screen.ResourceScreen";

    public static final String RESOURCE_FILE = "Resource";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.resource.db.Resource";
    public static final String THICK_CLASS = "org.jbundle.app.program.resource.db.Resource";

}
