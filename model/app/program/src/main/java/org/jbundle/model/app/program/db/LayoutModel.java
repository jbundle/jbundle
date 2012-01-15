/**
 * @(#)LayoutModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.main.db.*;

public interface LayoutModel extends FolderModel
{
    public static final String LAYOUT_SCREEN_CLASS = "org.jbundle.app.program.screen.LayoutScreen";
    public static final String LAYOUT_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.LayoutGridScreen";

    public static final String LAYOUT_FILE = "Layout";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.Layout";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.Layout";

}
