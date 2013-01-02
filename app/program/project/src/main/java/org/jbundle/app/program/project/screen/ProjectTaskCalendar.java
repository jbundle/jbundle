/**
 * @(#)ProjectTaskCalendar.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.screen;

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
import org.jbundle.app.program.project.db.*;
import org.jbundle.base.screen.model.calendar.*;
import javax.swing.*;
import org.jbundle.util.calendarpanel.model.*;

/**
 *  ProjectTaskCalendar - .
 */
public class ProjectTaskCalendar extends CalendarScreen
{
    protected ProjectTask m_recHeader = null;
    /**
     * Default constructor.
     */
    public ProjectTaskCalendar()
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
    public ProjectTaskCalendar(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        m_recHeader = null;
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * ProjectTaskCalendar Method.
     */
    public ProjectTaskCalendar(ProjectTask recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        this();
        this.init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(ProjectTask recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        m_recHeader = null;
        m_recHeader = recHeader;
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
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new ProjectControl(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
        if (m_recHeader != null)
            this.getMainRecord().addListener(new ProjectTaskParentFilter(m_recHeader));
        
        this.setEditing(true);
        this.getMainRecord().addListener(new FileFilter(null)
        {
            public boolean doLocalCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
            {   // Between start and end dates? (Defaults to Currentdate thru +1 year)
                boolean bDontSkip = true;
                if (this.getOwner().getField(ProjectTask.START_DATE_TIME).isNull())
                    bDontSkip = false;
                if (bDontSkip)
                    return super.doLocalCriteria(strbFilter, bIncludeFileName, vParamList);    // Dont skip this record
                else
                    return false;   // Skip this one
            }
                });
    }
    /**
     * Overidden to supply the title for this screen.
     * @return the screen title.
     */
    public String getTitle()
    {
        return "Project calendar";
    }
    /**
     * Get the CalendarItem for this record.
     */
    public CalendarItem getCalendarItem(Rec fieldList)
    {
        return new CalendarRecordItem(this, -1, 0, 1, 2, -1)
        {
            public Object getIcon(int iIconType)
            {
                Record recProjectControl = getRecord(ProjectControl.PROJECT_CONTROL_FILE);
                ImageField field = null;
                String fieldSeq = (iIconType == CalendarConstants.START_ICON) ? ProjectControl.START_PARENT_ICON : ProjectControl.END_PARENT_ICON;
                if (this.isParentTask())
                    field = (ImageField)recProjectControl.getField(fieldSeq);
                fieldSeq = (iIconType == CalendarConstants.START_ICON) ? ProjectControl.START_ICON : ProjectControl.END_ICON;
                if ((field == null) || (field.isNull()))
                    field = (ImageField)recProjectControl.getField(fieldSeq);
                if (field.isNull())
                    return super.getIcon(iIconType);
                return field.getImage().getImage();
            }
            public int getHighlightColor()
            {
                Record recProjectControl = getRecord(ProjectControl.PROJECT_CONTROL_FILE);
                ColorField field = null;
                if (this.isParentTask())
                    field = (ColorField)recProjectControl.getField(ProjectControl.PARENT_TASK_COLOR);
                if ((field == null) || (field.isNull()))
                    field = (ColorField)recProjectControl.getField(ProjectControl.TASK_COLOR);
                if (field.isNull())
                    return super.getHighlightColor();
                return field.getColor();
            }
            public int getSelectColor()
            {
                Record recProjectControl = getRecord(ProjectControl.PROJECT_CONTROL_FILE);
                ColorField field = null;
                if (this.isParentTask())
                    field = (ColorField)recProjectControl.getField(ProjectControl.PARENT_TASK_SELECT_COLOR);
                if ((field == null) || (field.isNull()))
                    field = (ColorField)recProjectControl.getField(ProjectControl.TASK_SELECT_COLOR);
                if (field.isNull())
                    return super.getSelectColor();
                return field.getColor();
            }
            public boolean isParentTask()
            {
                return ((ProjectTask)getMainRecord()).isParentTask();
            }
        };
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(ProjectTask.PROJECT_TASK_FILE).getField(ProjectTask.START_DATE_TIME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ProjectTask.PROJECT_TASK_FILE).getField(ProjectTask.END_DATE_TIME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ProjectTask.PROJECT_TASK_FILE).getField(ProjectTask.NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
