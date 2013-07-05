/**
 * @(#)PropertiesRecordModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.db.*;

public interface PropertiesRecordModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String PROPERTIES = "Properties";

    public static final String PROPERTIES_RECORD_FILE = "PropertiesRecord";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.PropertiesRecord";
    public static final String THICK_CLASS = "org.jbundle.main.db.PropertiesRecord";

}
