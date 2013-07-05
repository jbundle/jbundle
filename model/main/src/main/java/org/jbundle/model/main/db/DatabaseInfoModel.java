/**
 * @(#)DatabaseInfoModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.db.*;

public interface DatabaseInfoModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String NAME = "Name";
    public static final String DESCRIPTION = "Description";
    public static final String VERSION = "Version";
    public static final String START_ID = "StartID";
    public static final String END_ID = "EndID";
    public static final String BASE_DATABASE = "BaseDatabase";
    public static final String PROPERTIES = "Properties";

    public static final String NAME_KEY = "Name";

    public static final String DATABASE_INFO_FILE = "DatabaseInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.DatabaseInfo";
    public static final String THICK_CLASS = "org.jbundle.main.db.DatabaseInfo";
    /**
     * SetDatabaseName Method.
     */
    public void setDatabaseName(String databaseName);

}
