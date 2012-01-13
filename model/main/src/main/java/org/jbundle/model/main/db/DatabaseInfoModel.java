/**
 * @(#)DatabaseInfoModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.db.*;

public interface DatabaseInfoModel extends Rec
{

    public static final String DATABASE_INFO_FILE = "DatabaseInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.DatabaseInfo";
    public static final String THICK_CLASS = "org.jbundle.main.db.DatabaseInfo";
    /**
     * SetDatabaseName Method.
     */
    public void setDatabaseName(String databaseName);

}
