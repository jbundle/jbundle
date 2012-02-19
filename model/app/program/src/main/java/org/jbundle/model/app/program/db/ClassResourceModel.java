/**
 * @(#)ClassResourceModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface ClassResourceModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CLASS_NAME = "ClassName";
    public static final String SEQUENCE_NO = "SequenceNo";
    public static final String KEY_NAME = "KeyName";
    public static final String VALUE_NAME = "ValueName";

    public static final String CLASS_NAME_KEY = "ClassName";
    public static final String CLASS_RESOURCE_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassResourceScreen";
    public static final String CLASS_RESOURCE_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassResourceGridScreen";

    public static final String CLASS_RESOURCE_FILE = "ClassResource";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ClassResource";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ClassResource";

}
