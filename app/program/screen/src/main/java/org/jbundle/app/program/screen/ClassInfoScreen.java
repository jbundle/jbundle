/**
 * @(#)ClassInfoScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.db.*;
import org.jbundle.main.screen.*;
import org.jbundle.app.program.script.process.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.thin.base.thread.*;
import org.jbundle.thin.base.screen.*;

/**
 *  ClassInfoScreen - .
 */
public class ClassInfoScreen extends DetailScreen
{
    /**
     * Default constructor.
     */
    public ClassInfoScreen()
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
    public ClassInfoScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new ClassInfo(this);
    }
    /**
     * Open the header record.
     * @return The new header record.
     */
    public Record openHeaderRecord()
    {
        return new ClassProject(this);
    }
    /**
     * If there is a header record, return it, otherwise, return the main record.
     * The header record is the (optional) main record on gridscreens and is sometimes used
     * to enter data in a sub-record when a header is required.
     * @return The header record.
     */
    public Record getHeaderRecord()
    {
        return this.getRecord(ClassProject.CLASS_PROJECT_FILE);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new ClassVars(this);
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new ProgramControl(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.addMainKeyBehavior();
        this.getMainRecord().getField(ClassInfo.CLASS_NAME).addListener(new MainFieldHandler(ClassInfo.CLASS_NAME_KEY));
        this.getMainRecord().addListener(new EnableOnPhysicalHandler(null));
        this.getMainRecord().getField(ClassInfo.CLASS_NAME).addListener(new MoveOnChangeHandler(this.getMainRecord().getField(ClassInfo.CLASS_SOURCE_FILE), this.getMainRecord().getField(ClassInfo.CLASS_NAME), false, true));
        
        this.getMainRecord().addListener(new FileListener(null)
        {
            public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
            {
                int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
                if ((iChangeType == DBConstants.AFTER_UPDATE_TYPE) || (iChangeType == DBConstants.AFTER_UPDATE_TYPE))
                {
                    Record classInfo = getMainRecord();
                    TaskScheduler js = BaseApplet.getSharedInstance().getApplication().getTaskScheduler();
                    String strJob = Utility.addURLParam(null, DBParams.PROCESS, ".app.program.manual.util.WriteClasses");
                    strJob = Utility.addURLParam(strJob, "fileName", classInfo.getField(ClassInfo.CLASS_SOURCE_FILE).toString());
                    strJob = Utility.addURLParam(strJob, "package", classInfo.getField(ClassInfo.CLASS_PACKAGE).toString());
                    strJob = Utility.addURLParam(strJob, "project", Converter.stripNonNumber(classInfo.getField(ClassInfo.CLASS_PROJECT_ID).toString()));
                    strJob = Utility.addURLParam(strJob, DBParams.TASK, ProcessRunnerTask.class.getName()); // Screen class
                    js.addTask(strJob);                    
                }
                return iErrorCode;
            };
            
        });
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        super.addToolbars();
        
        ToolScreen toolbar = new EmptyToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
        
        Record mainFile = this.getMainRecord();
        
        BaseField field = this.getScreenRecord().getField(ClassVars.CLASS_KEY);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.FLUSH_LEFT, ScreenConstants.SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Description", MenuConstants.FORM, "5", null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Logic", MenuConstants.GROUP, "0", null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Fields", MenuConstants.DISTRIBUTION, "1", null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Keys", MenuConstants.GROUP, "2", null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Members", MenuConstants.DISTRIBUTION, "3", null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Screen", MenuConstants.GROUP, "4", null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "File header", MenuConstants.FORMLINK, "9", null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Resources", MenuConstants.DISTRIBUTION, "7", null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Help Desc", MenuConstants.HELP, "6", null);
        //new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, field, ScreenConstants.DEFAULT_DISPLAY, null, "Issues", MenuConstants.CLONE, "8", null);
        
        JavaButton pJavaButton = new JavaButton(toolbar.getNextLocation(ScreenConstants.FLUSH_LEFT, ScreenConstants.SET_ANCHOR), toolbar, null, ScreenConstants.DISPLAY_FIELD_DESC);
        pJavaButton.setClassInfo((ClassInfo)mainFile);
        //new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, null, ScreenConstants.DEFAULT_DISPLAY, null, "File header", MenuConstants.FORM, "?screen=" + FileHdrScreen.class.getName(), null);
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, null, ScreenConstants.DEFAULT_DISPLAY, null, "Database info", MenuConstants.FORM, "?screen=" + DatabaseInfoScreen.class.getName(), null);
        //new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, null, ScreenConstants.DEFAULT_DISPLAY, null, "Layout", MenuConstants.FORM, "?screen=" + LayoutScreen.class.getName(), null);
        String strJob = null;
        strJob = Utility.addURLParam(strJob, DBParams.TASK, DBConstants.SAPPLET); // Screen class
        strJob = Utility.addURLParam(strJob, DBParams.SCREEN, ExportRecordsToXmlScreen.class.getName());    // Screen class
        strJob = Utility.addURLParam(strJob, "newwindow", DBConstants.TRUE);    // Screen class
        strJob = Utility.addURLParam(strJob, ConvertCode.DIR_PREFIX, Utility.addToPath(((ProgramControl)this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE)).getBasePath(), this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE).getField(ProgramControl.ARCHIVE_DIRECTORY).toString()));    // Screen class
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, null, ScreenConstants.DEFAULT_DISPLAY, null, "Export", "Export", strJob, null);
        strJob = Utility.addURLParam(strJob, "mode", "import");
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, null, ScreenConstants.DEFAULT_DISPLAY, null, "Import", "Import", strJob, null);
        
        //new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, null, ScreenConstants.DEFAULT_DISPLAY, null, "Maintenance", MenuConstants.FORM, "?screen=" + org.jbundle.app.program.access.AccessGridScreen.class.getName(), null);
        strJob = null;
        strJob = Utility.addURLParam(strJob, DBParams.TASK, DBConstants.SAPPLET); // Screen class
        strJob = Utility.addURLParam(strJob, DBParams.SCREEN, ".app.program.manual.util.process.CopyHelpInfo");    // Screen class
        new SCannedBox(toolbar.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), toolbar, null, ScreenConstants.DEFAULT_DISPLAY, null, "Scan Help", MenuConstants.RUN, strJob, null);
        
        return toolbar;
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.CLASS_NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.BASE_CLASS_NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        SwitchClassSub listener = new SwitchClassSub(null, null, null);
        this.getScreenRecord().getField(ClassVars.CLASS_KEY).addListener(listener);
        this.getScreenRecord().getField(ClassVars.CLASS_KEY).setValue(5, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
        return (BasePanel)listener.getSubScreen();

    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        boolean bFlag = false;
        
        if ((strCommand.indexOf("FileHdrScreen") != -1)
            || (strCommand.indexOf("LayoutScreen") != -1))
                iCommandOptions = ScreenConstants.USE_NEW_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER;
        if ((strCommand.indexOf("ExportRecordsToXml") != -1)
            || (strCommand.indexOf("AccessGridScreen") != -1))
        {
            iCommandOptions = ScreenConstants.USE_NEW_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER;
            strCommand = Utility.addURLParam(strCommand, DBParams.RECORD, this.getMainRecord().getField(ClassInfo.CLASS_NAME).toString());
            String packageName = ((ClassInfo)this.getMainRecord()).getPackageName(null);
            strCommand = Utility.addURLParam(strCommand, "package", packageName);
            strCommand = Utility.addURLParam(strCommand, "project", Converter.stripNonNumber(this.getMainRecord().getField(ClassInfo.CLASS_PROJECT_ID).toString()));
        }
        
        if (!DBConstants.RESET.equalsIgnoreCase(strCommand))
        if ((this.getMainRecord().getEditMode() == DBConstants.EDIT_ADD)
            || (this.getMainRecord().getEditMode() == DBConstants.EDIT_IN_PROGRESS))
        {
            try {
                this.getMainRecord().writeAndRefresh(false);    // Make sure data is current before doing any command.
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
        
        if (bFlag == false)
            bFlag = super.doCommand(strCommand, sourceSField, iCommandOptions);  // This will send the command to my parent
        return bFlag;
    }

}
