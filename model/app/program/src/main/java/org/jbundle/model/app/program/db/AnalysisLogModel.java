/**
 * @(#)AnalysisLogModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface AnalysisLogModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String SYSTEM_ID = "SystemID";
    public static final String OBJECT_ID = "ObjectID";
    public static final String CLASS_NAME = "ClassName";
    public static final String DATABASE_NAME = "DatabaseName";
    public static final String INIT_TIME = "InitTime";
    public static final String FREE_TIME = "FreeTime";
    public static final String RECORD_OWNER = "RecordOwner";
    public static final String STACK_TRACE = "StackTrace";

    public static final String OBJECT_ID_KEY = "ObjectID";

    public static final String ANALYSIS_LOG_FILE = "AnalysisLog";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.AnalysisLog";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.AnalysisLog";
    /**
     * Log that this record has been added.
     * Call this from the end of record.init
     * @param record the record that is being added.
     */
    public void logAddRecord(Rec record, int iSystemID);
    /**
     * Log that this record has been freed.
     * Call this from the end of record.free
     * @param record the record that is being added.
     */
    public void logRemoveRecord(Rec record, int iSystemID);

}
