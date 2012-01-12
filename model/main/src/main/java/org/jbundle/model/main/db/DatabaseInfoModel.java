/**
 * @(#)DatabaseInfoModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

public interface DatabaseInfoModel extends org.jbundle.model.db.Rec
{

    public static final String DATABASE_INFO_FILE = "DatabaseInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.DatabaseInfo";
    public static final String THICK_CLASS = "org.jbundle.main.db.DatabaseInfo";
    /**
     * SetDatabaseName Method.
     */
    public void setDatabaseName(String strDatabaseName);

}
