/**
 * @(#)PartModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.packages.db;

import org.jbundle.model.db.*;

public interface PartModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String SEQUENCE = "Sequence";
    public static final String KIND = "Kind";
    public static final String PART_TYPE = "PartType";
    public static final String JNLP_FILE_ID = "JnlpFileID";
    public static final String PATH = "Path";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String JNLP_FILE_ID_KEY = "JnlpFileID";

    public static final String PART_FILE = "Part";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.packages.db.Part";
    public static final String THICK_CLASS = "org.jbundle.app.program.packages.db.Part";

}
