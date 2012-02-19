/**
 * @(#)ScreenInModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface ScreenInModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String SCREEN_IN_PROG_NAME = "ScreenInProgName";
    public static final String SCREEN_OUT_NUMBER = "ScreenOutNumber";
    public static final String SCREEN_ITEM_NUMBER = "ScreenItemNumber";
    public static final String SCREEN_FILE_NAME = "ScreenFileName";
    public static final String SCREEN_FIELD_NAME = "ScreenFieldName";
    public static final String SCREEN_ROW = "ScreenRow";
    public static final String SCREEN_COL = "ScreenCol";
    public static final String SCREEN_GROUP = "ScreenGroup";
    public static final String SCREEN_PHYSICAL_NUM = "ScreenPhysicalNum";
    public static final String SCREEN_LOCATION = "ScreenLocation";
    public static final String SCREEN_FIELD_DESC = "ScreenFieldDesc";
    public static final String SCREEN_TEXT = "ScreenText";
    public static final String SCREEN_ANCHOR = "ScreenAnchor";
    public static final String SCREEN_CONTROL_TYPE = "ScreenControlType";

    public static final String SCREEN_IN_PROG_NAME_KEY = "ScreenInProgName";
    public static final String SCREEN_IN_SCREEN_CLASS = "org.jbundle.app.program.screen.ScreenInScreen";
    public static final String SCREEN_IN_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.ScreenInGridScreen";

    public static final String SCREEN_IN_FILE = "ScreenIn";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ScreenIn";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ScreenIn";

}
