/**
 * @(#)PropertiesInputModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.properties.db;

import org.jbundle.model.db.*;
import java.util.*;
import org.jbundle.model.db.*;

public interface PropertiesInputModel extends Rec
{
    public static final String KEY = "Key";
    public static final String VALUE = "Value";
    public static final String COMMENT = "Comment";

    public static final String KEY_KEY = "Key";
    public static final String PROPERTIES_INPUT_SCREEN_CLASS = "org.jbundle.main.properties.screen.PropertiesInputGridScreen";
    public static final String PROPERTIES_INPUT_GRID_SCREEN_CLASS = "org.jbundle.main.properties.screen.PropertiesInputGridScreen";

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
    /**
     * StartEditor Method.
     */
    public ScreenComponent startEditor(Field fldProperties, boolean bAllowAppending, Map<String,Object> mapKeyDescriptions);

}
