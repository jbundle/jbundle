/**
 * @(#)ExportRecordsToXmlProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.process;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.app.program.manual.convert.ConvertCode;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.xmlutil.XmlInOut;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.MainApplication;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.util.Application;

/**
 *  ExportRecordsToXmlProcess - .
 */
public class ExportRecordsToXmlProcess extends BaseProcessRecords
{
    /**
     * Default constructor.
     */
    public ExportRecordsToXmlProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ExportRecordsToXmlProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Standalone support (pass args: record=Rec package=pkg).
     */
    public static void main(String[] args)
    {
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Utility.parseArgs(properties, args);
        }
        Application app = new MainApplication(null, properties, null);
        String strProcess = Utility.addURLParam(null, DBParams.PROCESS, ExportRecordsToXmlProcess.class.getName());
        app.getTaskScheduler().addTask(new ProcessRunnerTask(app, strProcess, null));   //org.jbundle.personal.manual.ExportPictureRecord"));
    }
    /**
     * Process this record.
     * @return true if success
     */
    public boolean processThisRecord(Record record)
    {
        boolean bPhysicalName = true;
        if (DBConstants.TRUE.equalsIgnoreCase(this.getProperty("useDatabaseName")))
            bPhysicalName = false;
        String strFilename = record.getArchiveFilename(bPhysicalName);
        if (this.getProperty(ConvertCode.DIR_PREFIX) != null)
            strFilename = Utility.addToPath(this.getProperty(ConvertCode.DIR_PREFIX), strFilename);
        
        String strMode = this.getProperty("mode");
        boolean bExport = true;
        if (strMode != null) if (strMode.equalsIgnoreCase("import"))
            bExport = false;
        if (bExport)
        {
            if ((record.getOpenMode() & DBConstants.OPEN_DONT_CREATE) == DBConstants.OPEN_DONT_CREATE)
            {   // Make sure the record exists
                try {
                    record.open();
                    record.hasNext();
                    record.close();
                } catch (DBException e) {
                    return false; // Record doesn't exist
                }
                if (this.getProperty("locale") != null)
                {
                    if (!record.getTable().getDatabase().getDatabaseName(false).endsWith("_" + this.getProperty("locale").toString()))
                        return false;     // If locale is set, only do locale tables
                }
            }
        }
        else
        { // Import must have file.
            if (!(new File(strFilename).exists()))
                return false;
                if (this.getProperty("locale") != null)
                {
                    if (!record.getTable().getDatabase().getDatabaseName(false).endsWith("_" + this.getProperty("locale").toString()))
                        return false;     // If locale is set, only do locale tables
                }
        }
        XmlInOut xml = new XmlInOut(this, null, null);    //0 v
        boolean bSuccess = false;
        if (bExport)
            bSuccess = xml.exportXML(record, strFilename);
        else
            bSuccess = xml.importXML(record, strFilename, null);
        xml.free();
        return bSuccess;
    }

}
