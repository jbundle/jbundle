/**
 * @(#)PropertiesInputModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.properties.db;

import java.util.Map;

import org.jbundle.model.db.Field;
import org.jbundle.model.db.ScreenComponent;

public interface PropertiesInputModel extends org.jbundle.model.db.Rec
{
    public static final String KEY = "Key";
    public static final String VALUE = "Value";
    public static final String COMMENT = "Comment";
    public static final String KEY_KEY = "Key";

    public static final String PROPERTIES_INPUT_FILE = "PropertiesInput";
    public static final String THIN_CLASS = "org.jbundle.thin.main.properties.db.PropertiesInput";
    public static final String THICK_CLASS = "org.jbundle.main.properties.db.PropertiesInput";
    /**
     * SetPropertiesField Method.
     */
    public void setPropertiesField(Field fldProperties);
    /**
     * LoadFieldProperties Method.
     */
    public void loadFieldProperties(Field fldProperties);
    public ScreenComponent startEditor(Field fldProperties, boolean bAllowAppending, Map<String,Object> mapKeyDescriptions);

}
