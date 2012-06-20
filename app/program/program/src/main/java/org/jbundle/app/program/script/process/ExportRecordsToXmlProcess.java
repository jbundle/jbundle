/**
 * @(#)ExportRecordsToXmlProcess.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.process;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.db.*;
import org.jbundle.main.db.*;
import java.io.*;
import org.jbundle.base.thread.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.base.db.xmlutil.*;

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
     */
    public boolean processThisRecord(Record record)
    {
        if (record == null)
            return false;
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
            bSuccess = xml.exportXML(record.getTable(), strFilename);
        else
            bSuccess = xml.importXML(record.getTable(), strFilename, null);
        xml.free();
        return bSuccess;
    }

}
