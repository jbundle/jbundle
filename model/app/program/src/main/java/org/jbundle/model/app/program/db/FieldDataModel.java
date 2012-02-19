/**
 * @(#)FieldDataModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface FieldDataModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String FIELD_NAME = "FieldName";
    public static final String FIELD_CLASS = "FieldClass";
    public static final String BASE_FIELD_NAME = "BaseFieldName";
    public static final String DEPENDENT_FIELD_NAME = "DependentFieldName";
    public static final String MINIMUM_LENGTH = "MinimumLength";
    public static final String MAXIMUM_LENGTH = "MaximumLength";
    public static final String DEFAULT_VALUE = "DefaultValue";
    public static final String INITIAL_VALUE = "InitialValue";
    public static final String FIELD_DESCRIPTION = "FieldDescription";
    public static final String FIELD_DESC_VERTICAL = "FieldDescVertical";
    public static final String FIELD_TYPE = "FieldType";
    public static final String FIELD_DIMENSION = "FieldDimension";
    public static final String FIELD_FILE_NAME = "FieldFileName";
    public static final String FIELD_SEQ_NO = "FieldSeqNo";
    public static final String FIELD_NOT_NULL = "FieldNotNull";
    public static final String DATA_CLASS = "DataClass";
    public static final String HIDDEN = "Hidden";
    public static final String INCLUDE_SCOPE = "IncludeScope";

    public static final String FIELD_FILE_NAME_KEY = "FieldFileName";

    public static final String FIELD_NAME_KEY = "FieldName";
    public static final String FIELD_DATA_SCREEN_CLASS = "org.jbundle.app.program.screen.FieldDataScreen";
    public static final String FIELD_DATA_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.FieldDataGridScreen";

    public static final String FIELD_DATA_FILE = "FieldData";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.FieldData";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.FieldData";

}
