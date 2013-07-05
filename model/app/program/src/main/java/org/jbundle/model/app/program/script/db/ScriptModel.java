/**
 * @(#)ScriptModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.script.db;

import org.jbundle.model.main.db.*;

public interface ScriptModel extends FolderModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String NAME = NAME;
    //public static final String PARENT_FOLDER_ID = PARENT_FOLDER_ID;
    //public static final String SEQUENCE = SEQUENCE;
    //public static final String COMMENT = COMMENT;
    //public static final String CODE = CODE;
    public static final String PROPERTIES = "Properties";
    public static final String COMMAND = "Command";
    public static final String SOURCE = "Source";
    public static final String DESTINATION = "Destination";
    public static final String RUN = "R";
    public static final String RUN_REMOTE = "Z";
    public static final String SEEK = "S";
    public static final String COPY_FIELDS = "F";
    public static final String COPY_RECORDS = "Z";
    public static final String COPY_DATA = "D";
    public static final String DESTINATION_RECORD = "destinationRecord";
    public static final String SOURCE_PARAM = "source";
    public static final String DESTINATION_PARAM = "destination";
    public static final String SCRIPT_GRID_SCREEN_CLASS = "org.jbundle.app.program.script.screen.ScriptGridScreen";
    public static final String SCRIPT_SCREEN_CLASS = "org.jbundle.app.program.script.screen.ScriptScreen";

    public static final String SCRIPT_FILE = "Script";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.script.db.Script";
    public static final String THICK_CLASS = "org.jbundle.app.program.script.db.Script";

}
