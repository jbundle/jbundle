/**
 * @(#)LayoutsModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.main.db.*;

public interface LayoutsModel extends FolderModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String NAME = NAME;
    //public static final String PARENT_FOLDER_ID = PARENT_FOLDER_ID;
    //public static final String SEQUENCE = SEQUENCE;
    //public static final String COMMENT = COMMENT;
    //public static final String CODE = CODE;
    public static final String TYPE = "Type";
    public static final String FIELD_VALUE = "FieldValue";
    public static final String RETURNS_VALUE = "ReturnsValue";
    public static final String MAX = "Max";
    public static final String SYSTEM = "System";
    public static final String COMMENTS = "Comments";
    public static final String LAYOUT_SCREEN_CLASS = "org.jbundle.app.program.screen.LayoutScreen";
    public static final String LAYOUT_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.LayoutGridScreen";

    public static final String LAYOUTS_FILE = "Layouts";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.Layouts";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.Layouts";

}
