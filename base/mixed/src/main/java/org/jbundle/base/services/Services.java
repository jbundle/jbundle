package org.jbundle.base.services;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.model.DBException;


public class Services {

    /**
     * Get the ClassInfoService and (optionally) read in the class.
     * @param recordOwner The record owner to use to create the this record AND to optionally get the classinfo.
     * @param className if non-null read this class name, if null, use the recordowner properties to figure out the class.
     * @param getRecord If true, read the record.
     * @exception DBException File exception.
     */
    public static ClassInfoService getClassInfo(RecordOwner recordOwner, String className, boolean getRecord)
    {
    	ClassInfoService classInfo = (ClassInfoService)recordOwner.getRecord("ClassInfo");
    	if (getRecord)
    		if (classInfo != null)
    			classInfo = classInfo.readClassInfo(recordOwner, className);
    	return classInfo;
    }
    /**
     * Create the ClassInfoService and (optionally) read in the class.
     * @param recordOwner The record owner to use to create the this record AND to optionally get the classinfo.
     * @param className if non-null read this class name, if null, use the recordowner properties to figure out the class.
     * @param getRecord If true, read the record.
     * @exception DBException File exception.
     */
    public static ClassInfoService createClassInfo(RecordOwner recordOwner, String className, boolean getRecord)
    {
    	ClassInfoService classInfo = (ClassInfoService)Record.makeRecordFromClassName(".app.program.db.ClassInfo", recordOwner);
    	if (getRecord)
    		if (classInfo != null)
    			classInfo = classInfo.readClassInfo(recordOwner, className);
    	return classInfo;
    }
    /**
     * Get the ClassInfoService and (optionally) read in the class.
     * @param recordOwner The record owner to use to create the this record AND to optionally get the classinfo.
     * @param className if non-null read this class name, if null, use the recordowner properties to figure out the class.
     * @param getRecord If true, read the record.
     * @exception DBException File exception.
     */
    public static AnalysisLogService createAnalysisLog(Record record)
    {
        if (record.getTable() == null)
            return null;
        if (record.getTable().getDatabase() == null)
            return null;
        if (record.getTable().getDatabase().getDatabaseOwner() == null)
            return null;
        if (record.getTable().getDatabase().getDatabaseOwner().getEnvironment() == null)
            return null;
        if (record.getTable().getDatabase().getDatabaseOwner().getEnvironment().getMessageApplication(false, null) == null)
            return null;
        if (record.getTable().getDatabase().getDatabaseOwner().getEnvironment().getDefaultApplication() == null)
            return null;
        if (record.getTable().getDatabase().getDatabaseOwner().getEnvironment().getDefaultApplication().getSystemRecordOwner() == null)
            return null;
        RecordOwner recordOwner = (RecordOwner)record.getTable().getDatabase().getDatabaseOwner().getEnvironment().getDefaultApplication().getSystemRecordOwner();

        AnalysisLogService analysisLog = (AnalysisLogService)Record.makeRecordFromClassName(".app.program.db.AnalysisLog", recordOwner);

        ((Record)analysisLog).getTable().setProperty(DBParams.SUPRESSREMOTEDBMESSAGES, DBConstants.TRUE);
        ((Record)analysisLog).getTable().getDatabase().setProperty(DBParams.MESSAGES_TO_REMOTE, DBConstants.FALSE);

    	return analysisLog;
    }
}
