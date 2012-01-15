/**
 * @(#)ScriptModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.script.db;

import org.jbundle.model.main.db.*;

public interface ScriptModel extends FolderModel
{
    public static final String RUN = "R";
    public static final String RUN_REMOTE = "Z";
    public static final String SEEK = "S";
    public static final String COPY_FIELDS = "F";
    public static final String COPY_RECORDS = "Z";
    public static final String COPY_DATA = "D";
    public static final String DESTINATION_RECORD = "destinationRecord";
    public static final String SOURCE = "source";
    public static final String DESTINATION = "destination";
    public static final String SCRIPT_GRID_SCREEN_CLASS = "org.jbundle.app.program.script.screen.ScriptGridScreen";
    public static final String SCRIPT_SCREEN_CLASS = "org.jbundle.app.program.script.screen.ScriptScreen";

    public static final String SCRIPT_FILE = "Script";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.script.db.Script";
    public static final String THICK_CLASS = "org.jbundle.app.program.script.db.Script";

}
