/**
 * @(#)ProjectReportScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.report;

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
import org.jbundle.base.screen.model.report.*;
import org.jbundle.app.program.project.db.*;

/**
 *  ProjectReportScreen - .
 */
public class ProjectReportScreen extends ReportScreen
{
    protected Vector<Record> m_rgCurrentLevelInfo = new Vector<Record>();
    /**
     * Default constructor.
     */
    public ProjectReportScreen()
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
    public ProjectReportScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return new ProjectTask(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getMainRecord().setKeyArea(ProjectTask.PARENT_PROJECT_TASK_ID_KEY);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new ProjectTaskScreenRecord(this);
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        return new ProjectReportToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(ProjectTask.PROJECT_TASK_FILE).getField(ProjectTask.START_DATE_TIME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ProjectTask.PROJECT_TASK_FILE).getField(ProjectTask.DURATION).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        Converter converter = this.getRecord(ProjectTask.PROJECT_TASK_FILE).getField(ProjectTask.NAME);
        Converter convIndent = this.getScreenRecord().getField(ProjectTaskScreenRecord.CURRENT_LEVEL);
        converter = new NameIndentConverter(converter, convIndent, +4);
        converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Get the path to the target servlet.
     * @param The servlet type (regular html or xhtml)
     * @return the servlet path.
     */
    public String getServletPath(String strServletParam)
    {
        return super.getServletPath(DBParams.XHTMLSERVLET); // Use cocoon
    }
    /**
     * Get the next grid record.
     * @param bFirstTime If true, I want the first record.
     * @return the next record (or null if EOF).
     */
    public Record getNextGridRecord(boolean bFirstTime) throws DBException
    {
        Record record = this.getMainRecord();
        Record recNew = null;
        if (bFirstTime)
        {
            String mainString = this.getScreenRecord().getField(ProjectTaskScreenRecord.PROJECT_TASK_ID).toString();
            recNew = this.getCurrentLevelInfo(+0, record);
            recNew.addListener(new StringSubFileFilter(mainString, ProjectTask.PARENT_PROJECT_TASK_ID, null, null, null, null));
        }
        else
        { // See if there are any sub-records to the last valid record
            recNew = this.getCurrentLevelInfo(+1, record);
            String mainString = record.getCounterField().toString();
            if (recNew.getListener(StringSubFileFilter.class) == null)
                recNew.addListener(new StringSubFileFilter(mainString, ProjectTask.PARENT_PROJECT_TASK_ID, null, null, null, null));
        }
        boolean bHasNext = recNew.hasNext();
        if (!bHasNext)
        {
            int dLevel = (int)this.getScreenRecord().getField(ProjectTaskScreenRecord.CURRENT_LEVEL).getValue();
            if (dLevel == 0)
                return null;    // All done
            Record recTemp = this.getCurrentLevelInfo(+0, record);
            recTemp.removeListener(recTemp.getListener(StringSubFileFilter.class), true);
            recTemp.close();
            dLevel = dLevel - 2;    // Since it is incremented next time
            this.getScreenRecord().getField(ProjectTaskScreenRecord.CURRENT_LEVEL).setValue(dLevel);
            return this.getNextGridRecord(false);
        }
        Record recNext = (Record)recNew.next();
        record.moveFields(recNext, null, true, DBConstants.SCREEN_MOVE, false, false, false, false);
        return record;
    }
    /**
     * Add to the current level, then
     * get the record at this level
     * If the record doesn't exist, clone a new one and return it.
     * @param record The main record (that I will clone if I need to)
     * @param iOffsetFromCurrentLevel The amount to bump the level
     * @return The record at this (new) level.
     */
    public Record getCurrentLevelInfo(int iOffsetFromCurrentLevel, Record record)
    {
        int dLevel = (int)this.getScreenRecord().getField(ProjectTaskScreenRecord.CURRENT_LEVEL).getValue();
        dLevel = dLevel + iOffsetFromCurrentLevel;
        this.getScreenRecord().getField(ProjectTaskScreenRecord.CURRENT_LEVEL).setValue(dLevel);
        if (m_rgCurrentLevelInfo.size() >= dLevel)
        {
            try {
                m_rgCurrentLevelInfo.add((Record)record.clone());
                m_rgCurrentLevelInfo.elementAt(dLevel).setKeyArea(ProjectTask.PARENT_PROJECT_TASK_ID_KEY);
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
        return m_rgCurrentLevelInfo.elementAt(dLevel);
    }

}
