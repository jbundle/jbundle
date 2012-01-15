/**
 * @(#)JnlpFileModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.packages.db;

import org.jbundle.model.db.*;

public interface JnlpFileModel extends Rec
{
    public static final String JNLP_FILE_SCREEN_CLASS = "org.jbundle.app.program.packages.screen.JnlpFileScreen";
    public static final String JNLP_FILE_GRID_SCREEN_CLASS = "org.jbundle.app.program.packages.screen.JnlpFileGridScreen";

    public static final String JNLP_FILE_FILE = "JnlpFile";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.packages.db.JnlpFile";
    public static final String THICK_CLASS = "org.jbundle.app.program.packages.db.JnlpFile";

}
