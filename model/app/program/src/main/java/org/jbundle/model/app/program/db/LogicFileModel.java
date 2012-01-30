/**
 * @(#)LogicFileModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface LogicFileModel extends Rec
{

    //public static final String ID = ID;
    public static final String SEQUENCE = "Sequence";
    public static final String METHOD_NAME = "MethodName";
    public static final String LOGIC_DESCRIPTION = "LogicDescription";
    public static final String METHOD_RETURNS = "MethodReturns";
    public static final String METHOD_INTERFACE = "MethodInterface";
    public static final String METHOD_CLASS_NAME = "MethodClassName";
    public static final String LOGIC_SOURCE = "LogicSource";
    public static final String LOGIC_THROWS = "LogicThrows";
    public static final String PROTECTION = "Protection";
    public static final String COPY_FROM = "CopyFrom";
    public static final String INCLUDE_SCOPE = "IncludeScope";

    public static final String METHOD_CLASS_NAME_KEY = "MethodClassName";

    public static final String SEQUENCE_KEY = "Sequence";
    public static final int INCLUDE_THICK = 0x0001;
    public static final int INCLUDE_THIN = 0x002;
    public static final int INCLUDE_INTERFACE = 0x004;
    public static final int INCLUDE_ALL = 0xFFF;
    public static final String LOGIC_FILE_SCREEN_CLASS = "org.jbundle.app.program.screen.LogicFileScreen";
    public static final String LOGIC_FILE_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.LogicFileGridScreen";

    public static final String LOGIC_FILE_FILE = "LogicFile";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.LogicFile";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.LogicFile";

}
