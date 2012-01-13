/**
 * @(#)LogicFileModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface LogicFileModel extends Rec
{
    public static final int INCLUDE_THICK = 0x0001;
    public static final int INCLUDE_THIN = 0x002;
    public static final int INCLUDE_INTERFACE = 0x004;
    public static final int INCLUDE_ALL = 0xFFF;

    public static final String LOGIC_FILE_FILE = "LogicFile";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.LogicFile";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.LogicFile";

}
