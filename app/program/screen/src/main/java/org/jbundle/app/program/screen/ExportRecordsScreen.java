/**
 * @(#)ExportRecordsScreen.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.db.*;
import org.jbundle.app.program.script.process.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.base.thread.*;
import java.io.*;

/**
 *  ExportRecordsScreen - .
 */
public class ExportRecordsScreen extends BaseScreen
{
    public static final String SYSTEM_PACKAGE_FILTER = "org\\.jbundle\\.app\\.program\\..*|org\\.jbundle\\.main\\..*";
    public static final String NON_SYSTEM_PACKAGE_FILTER = "^(?:(?!" + SYSTEM_PACKAGE_FILTER + ").).*";
    /**
     * Default constructor.
     */
    public ExportRecordsScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties Addition properties to pass to the screen.
     */
    public ExportRecordsScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new ClassInfoScreenRecord(this);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.CLASS_PACKAGE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.CLASS_PROJECT_ID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ClassInfoScreenRecord.CLASS_INFO_SCREEN_RECORD_FILE).getField(ClassInfoScreenRecord.INCLUDE_EMPTY_FILES).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        String strProcess = copyProcessParams();
        strProcess = Utility.addURLParam(strProcess, DBParams.PROCESS, StandaloneProcessRunnerProcess.class.getName());
        strProcess = Utility.addURLParam(strProcess, StandaloneProcessRunnerProcess.STANDALONE_PROCESS, ExportRecordsToXmlProcess.class.getName());
        strProcess = Utility.addURLParam(strProcess, DBParams.TASK, ProcessRunnerTask.class.getName()); // Screen class
        strProcess = Utility.addURLParam(strProcess, ClassInfoScreenRecord.INCLUDE_EMPTY_FILES, this.getRecord(ClassInfoScreenRecord.CLASS_INFO_SCREEN_RECORD_FILE).getField(ClassInfoScreenRecord.INCLUDE_EMPTY_FILES).toString());
        
        String basePath = ((ProgramControl)this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE)).getBasePath();
        String path = this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE).getField(ProgramControl.ARCHIVE_DIRECTORY).toString();
        if ((!path.startsWith("/")) && (!path.startsWith(File.separator)))
            path = Utility.addToPath(basePath, path);
        String strJob = Utility.addURLParam(strProcess, ConvertCode.DIR_PREFIX, path);
        strJob = Utility.addURLParam(strJob, "package", NON_SYSTEM_PACKAGE_FILTER);
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, null, ScreenConstants.DEFAULT_DISPLAY, null, "Export", "Export", strJob, null);
        strJob = Utility.addURLParam(strJob, ExportRecordsToXmlProcess.TRANSFER_MODE, ExportRecordsToXmlProcess.IMPORT);
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, null, ScreenConstants.DEFAULT_DISPLAY, null, "Import", "Import", strJob, null);
        
        path = this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE).getField(ProgramControl.DEV_ARCHIVE_DIRECTORY).toString();
        if ((!path.startsWith("/")) && (!path.startsWith(File.separator)))
            path = Utility.addToPath(basePath, path);
        strJob = Utility.addURLParam(strProcess, ConvertCode.DIR_PREFIX, path);
        strJob = Utility.addURLParam(strJob, "package", SYSTEM_PACKAGE_FILTER);
        strJob = Utility.addURLParam(strJob, ClassInfoScreenRecord.INCLUDE_EMPTY_FILES, this.getRecord(ClassInfoScreenRecord.CLASS_INFO_SCREEN_RECORD_FILE).getField(ClassInfoScreenRecord.INCLUDE_EMPTY_FILES).toString());
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, null, ScreenConstants.DEFAULT_DISPLAY, null, "Export System Files", "Export", strJob, null);
        strJob = Utility.addURLParam(strJob, ExportRecordsToXmlProcess.TRANSFER_MODE, ExportRecordsToXmlProcess.IMPORT);
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, null, ScreenConstants.DEFAULT_DISPLAY, null, "Import System Files", "Import", strJob, null);
    }
    /**
     * CopyProcessParams Method.
     */
    public String copyProcessParams()
    {
        String strProcess = null;
        strProcess = Utility.addURLParam(strProcess, DBParams.LOCAL, this.getProperty(DBParams.LOCAL));
        strProcess = Utility.addURLParam(strProcess, DBParams.REMOTE, this.getProperty(DBParams.REMOTE));
        strProcess = Utility.addURLParam(strProcess, DBParams.TABLE, this.getProperty(DBParams.TABLE));
        strProcess = Utility.addURLParam(strProcess, DBParams.MESSAGE_SERVER, this.getProperty(DBParams.MESSAGE_SERVER));
        strProcess = Utility.addURLParam(strProcess, DBParams.CONNECTION_TYPE, this.getProperty(DBParams.CONNECTION_TYPE));
        strProcess = Utility.addURLParam(strProcess, DBParams.REMOTE_HOST, this.getProperty(DBParams.REMOTE_HOST));
        strProcess = Utility.addURLParam(strProcess, DBParams.CODEBASE, this.getProperty(DBParams.CODEBASE));
        strProcess = Utility.addURLParam(strProcess, SQLParams.DATABASE_PRODUCT_PARAM, this.getProperty(SQLParams.DATABASE_PRODUCT_PARAM));
        strProcess = Utility.addURLParam(strProcess, DBConstants.SYSTEM_NAME, this.getProperty(DBConstants.SYSTEM_NAME), false);
        return strProcess;
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        return null;
    }

}
