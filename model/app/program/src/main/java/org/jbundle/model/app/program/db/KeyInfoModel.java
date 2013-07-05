/**
 * @(#)KeyInfoModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface KeyInfoModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String KEY_FILENAME = "KeyFilename";
    public static final String KEY_NUMBER = "KeyNumber";
    public static final String KEY_FIELD_1 = "KeyField1";
    public static final String KEY_FIELD_2 = "KeyField2";
    public static final String KEY_FIELD_3 = "KeyField3";
    public static final String KEY_FIELD_4 = "KeyField4";
    public static final String KEY_FIELD_5 = "KeyField5";
    public static final String KEY_FIELD_6 = "KeyField6";
    public static final String KEY_FIELD_7 = "KeyField7";
    public static final String KEY_FIELD_8 = "KeyField8";
    public static final String KEY_FIELD_9 = "KeyField9";
    public static final String KEY_NAME = "KeyName";
    public static final String KEY_TYPE = "KeyType";
    public static final String INCLUDE_SCOPE = "IncludeScope";

    public static final String KEY_FILENAME_KEY = "KeyFilename";
    public static final String KEY_INFO_SCREEN_CLASS = "org.jbundle.app.program.screen.KeyInfoScreen";
    public static final String KEY_INFO_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.KeyInfoGridScreen";

    public static final String KEY_INFO_FILE = "KeyInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.KeyInfo";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.KeyInfo";

}
