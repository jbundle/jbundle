/**
 * @(#)FieldDataModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface FieldDataModel extends Rec
{
    public static final String FIELD_DATA_SCREEN_CLASS = "org.jbundle.app.program.screen.FieldDataScreen";
    public static final String FIELD_DATA_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.FieldDataGridScreen";

    public static final String FIELD_DATA_FILE = "FieldData";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.FieldData";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.FieldData";

}
