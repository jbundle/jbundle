/**
 * @(#)ClassFieldsModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface ClassFieldsModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CLASS_INFO_CLASS_NAME = "ClassInfoClassName";
    public static final String CLASS_INFO_ID = "ClassInfoID";
    public static final String CLASS_FIELD_CLASS = "ClassFieldClass";
    public static final String CLASS_FIELD_SEQUENCE = "ClassFieldSequence";
    public static final String CLASS_FIELD_NAME = "ClassFieldName";
    public static final String CLASS_FIELD_DESC = "ClassFieldDesc";
    public static final String CLASS_FIELD_PROTECT = "ClassFieldProtect";
    public static final String CLASS_FIELD_INITIAL = "ClassFieldInitial";
    public static final String CLASS_FIELD_INITIAL_VALUE = "ClassFieldInitialValue";
    public static final String CLASS_FIELDS_TYPE = "ClassFieldsType";
    public static final String INCLUDE_SCOPE = "IncludeScope";

    public static final String CLASS_INFO_CLASS_NAME_KEY = "ClassInfoClassName";
    public static final String CLASS_FIELDS_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassFieldsScreen";
    public static final String CLASS_FIELDS_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassFieldsGridScreen";

    public static final String CLASS_FIELDS_FILE = "ClassFields";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ClassFields";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ClassFields";

}
