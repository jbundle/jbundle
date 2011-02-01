package org.jbundle.base.services;

import org.jbundle.base.db.Record;

public interface AnalysisLogService {
    /**
     * Call this from the end of record.init
     * @param record
     */
    public void logAddRecord(Record record, int iSystemID);
    /**
     * Call this from before record.free.
     * @param record
     */
    public void logRemoveRecord(Record record, int iSystemID);

}
