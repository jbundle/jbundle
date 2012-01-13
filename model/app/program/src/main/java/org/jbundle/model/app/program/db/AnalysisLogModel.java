/**
 * @(#)AnalysisLogModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface AnalysisLogModel extends Rec
{

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
