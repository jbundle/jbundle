/**
 * @(#)ProjectTask.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.db;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.app.program.project.screen.*;
import org.jbundle.app.program.issue.db.*;
import org.jbundle.main.user.db.*;
import org.jbundle.model.app.program.project.db.*;

/**
 *  ProjectTask - .
 */
public class ProjectTask extends Folder
     implements ProjectTaskModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kName = kName;
    public static final int kParentProjectTaskID = kParentFolderID;
    //public static final int kSequence = kSequence;
    //public static final int kComment = kComment;
    public static final int kStartDateTime = kFolderLastField + 1;
    public static final int kDuration = kStartDateTime + 1;
    public static final int kEndDateTime = kDuration + 1;
    public static final int kProgress = kEndDateTime + 1;
    public static final int kProjectID = kProgress + 1;
    public static final int kProjectVersionID = kProjectID + 1;
    public static final int kProjectTypeID = kProjectVersionID + 1;
    public static final int kProjectStatusID = kProjectTypeID + 1;
    public static final int kAssignedUserID = kProjectStatusID + 1;
    public static final int kProjectPriorityID = kAssignedUserID + 1;
    public static final int kEnteredDate = kProjectPriorityID + 1;
    public static final int kEnteredByUserID = kEnteredDate + 1;
    public static final int kChangedDate = kEnteredByUserID + 1;
    public static final int kChangedByUserID = kChangedDate + 1;
    public static final int kHasChildren = kChangedByUserID + 1;
    public static final int kProjectTaskLastField = kHasChildren;
    public static final int kProjectTaskFields = kHasChildren - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kParentProjectTaskIDKey = kIDKey + 1;
    public static final int kProjectTaskLastKey = kParentProjectTaskIDKey;
    public static final int kProjectTaskKeys = kParentProjectTaskIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    protected ProjectTask m_recDetail = null;
    protected ProjectTask m_recDetailChildren = null;
    protected ProjectTaskPredecessor m_recProjectTaskPredecessor = null;
    public static final int PROJECT_PREDECESSOR_DETAIL_MODE = ScreenConstants.LAST_MODE * 2;
    public static final int PROJECT_TASK_CALENDAR_MODE = ScreenConstants.LAST_MODE * 4;
    /**
     * Default constructor.
     */
    public ProjectTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProjectTask(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        m_recDetail = null;
        m_recDetailChildren = null;
        m_recProjectTaskPredecessor = null;
        super.init(screen);
    }

    public static final String kProjectTaskFile = "ProjectTask";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kProjectTaskFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "program";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.REMOTE | DBConstants.USER_DATA;
    }
    /**
     * MakeScreen Method.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ProjectTask.PROJECT_TASK_CALENDAR_MODE) == ProjectTask.PROJECT_TASK_CALENDAR_MODE)
            screen = new ProjectTaskCalendar(this, null, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ProjectTask.PROJECT_PREDECESSOR_DETAIL_MODE) == ProjectTask.PROJECT_PREDECESSOR_DETAIL_MODE)
            screen = new ProjectTaskPredecessorGridScreen(this, null, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DETAIL_MODE) == ScreenConstants.DETAIL_MODE)
            screen = new ProjectTaskGridScreen(this, null, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = new ProjectTaskScreen(this, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new ProjectTaskGridScreen(this, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MENU_MODE) != 0)
            screen = new ProjectTaskScreen(this, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else
            screen = super.makeScreen((ScreenLocation)itsLocation, (BasePanel)parentScreen, iDocMode, properties);
        return screen;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kName)
            field = new StringField(this, "Name", 120, null, null);
        if (iFieldSeq == kParentProjectTaskID)
            field = new ProjectTaskField(this, "ParentProjectTaskID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kSequence)
        {
            field = new ShortField(this, "Sequence", Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)0));
            field.setNullable(false);
        }
        if (iFieldSeq == kStartDateTime)
            field = new DateTimeField(this, "StartDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kDuration)
        {
            field = new RealField(this, "Duration", Constants.DEFAULT_FIELD_LENGTH, null, new Double(1));
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kEndDateTime)
        {
            field = new DateTimeField(this, "EndDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kProgress)
            field = new PercentField(this, "Progress", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProjectID)
            field = new ProjectFilter(this, "ProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProjectVersionID)
            field = new ProjectVersionField(this, "ProjectVersionID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProjectTypeID)
            field = new IssueStatusField(this, "ProjectTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProjectStatusID)
            field = new IssueStatusField(this, "ProjectStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kAssignedUserID)
            field = new UserField(this, "AssignedUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProjectPriorityID)
            field = new IssuePriorityField(this, "ProjectPriorityID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEnteredDate)
            field = new ProjectTask_EnteredDate(this, "EnteredDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEnteredByUserID)
            field = new UserField(this, "EnteredByUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kChangedDate)
            field = new DateTimeField(this, "ChangedDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kChangedByUserID)
            field = new UserField(this, "ChangedByUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kHasChildren)
            field = new BooleanField(this, "HasChildren", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //if (iFieldSeq == kComment)
        //  field = new MemoField(this, "Comment", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kProjectTaskLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == kIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kParentProjectTaskIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ParentProjectTaskID");
            keyArea.addKeyField(kParentProjectTaskID, DBConstants.ASCENDING);
            keyArea.addKeyField(kStartDateTime, DBConstants.ASCENDING);
            keyArea.addKeyField(kSequence, DBConstants.ASCENDING);
            keyArea.addKeyField(kName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kProjectTaskLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kProjectTaskLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Free Method.
     */
    public void free()
    {
        if (m_recDetail != null)
            m_recDetail.free();
        m_recDetail = null;
        if (m_recDetailChildren != null)
            m_recDetailChildren.free();
        m_recDetailChildren = null;
        if (m_recProjectTaskPredecessor != null)
            m_recProjectTaskPredecessor.free();
        m_recProjectTaskPredecessor = null;
        super.free();
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        
        this.addListener(new SetUserIDHandler(ProjectTask.ENTERED_BY_USER_ID, true));
        this.addListener(new SetUserIDHandler(ProjectTask.CHANGED_BY_USER_ID, true));
        this.addListener(new DateChangedHandler(ProjectTask.CHANGED_DATE));
        
        FieldListener listener = null;
        this.getField(ProjectTask.END_DATE_TIME).addListener(listener = new InitDateOffsetHandler(this.getField(ProjectTask.DURATION), (DateTimeField)this.getField(ProjectTask.START_DATE_TIME)));
        listener.setRespondsToMode(DBConstants.READ_MOVE, true);
        
        this.getField(ProjectTask.END_DATE_TIME).addListener(listener = new ReComputeTimeOffsetHandler(ProjectTask.DURATION, (DateTimeField)this.getField(ProjectTask.START_DATE_TIME)));
        
        this.getField(ProjectTask.START_DATE_TIME).addListener(listener = new ReComputeEndDateHandler(ProjectTask.END_DATE_TIME, (NumberField)this.getField(ProjectTask.DURATION)));
        
        this.getField(ProjectTask.DURATION).addListener(listener = new ChangeOnChangeHandler(this.getField(ProjectTask.START_DATE_TIME)));
        listener.setRespondsToMode(DBConstants.INIT_MOVE, false);
        
        
        this.getField(ProjectTask.START_DATE_TIME).addListener(new InitFieldHandler((BaseField)null)
        {
            public int fieldChanged(boolean bDisplayOption, int iMoveMode)
            {
                if (!getField(ProjectTask.PARENT_PROJECT_TASK_ID).isNull())
                {
                    Record recParent = ((ReferenceField)getField(ProjectTask.PARENT_PROJECT_TASK_ID)).getReference();
                    if (recParent != null)
                        if ((recParent.getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (recParent.getEditMode() == DBConstants.EDIT_CURRENT))
                        {
                            if (recParent.getField(ProjectTask.HAS_CHILDREN).getState() == true)
                                return this.getOwner().moveFieldToThis(recParent.getField(ProjectTask.END_DATE_TIME), bDisplayOption, iMoveMode);
                            else
                                return this.getOwner().moveFieldToThis(recParent.getField(ProjectTask.START_DATE_TIME), bDisplayOption, iMoveMode);
                        }
                }
                return super.fieldChanged(bDisplayOption, iMoveMode);
            }
        });
        
        this.addListener(new UpdateChildrenHandler(null));
        this.addListener(new UpdateDependenciesHandler(null));
        this.addListener(new SurveyDatesHandler(null));
        
        
        // todo (don) Move these to the server environment
        this.addListener(new SubFileIntegrityHandler(ProjectTask.class.getName(), true));
        
        this.addListener(new SubFileIntegrityHandler(ProjectTaskPredecessor.class.getName(), true));
        
        this.addListener(new SubFileIntegrityHandler(ProjectTaskPredecessor.class.getName(), true)
        {
            public Record getSubRecord()
            {
                if (m_recDependent == null)
                    m_recDependent = this.createSubRecord();
                if (m_recDependent != null)
                {
                    if (m_recDependent.getListener(SubFileFilter.class.getName()) == null)
                    {
                        m_recDependent.addListener(new SubFileFilter(getField(ProjectTask.ID), ProjectTaskPredecessor.PROJECT_TASK_ID, null, null, null, null));
                        m_recDependent.setKeyArea(ProjectTaskPredecessor.PROJECT_TASK_ID_KEY);
                    }
                }
                return m_recDependent;
            }
        });
    }
    /**
     * Convert the command to the screen document type.
     * @param strCommand The command text.
     * @param The standard document type (MAINT/DISPLAY/SELECT/MENU/etc).
     */
    public int commandToDocType(String strCommand)
    {
        if (ProjectTask.PROJECT_PREDECESSOR_DETAIL_SCREEN.equalsIgnoreCase(strCommand))
            return ProjectTask.PROJECT_PREDECESSOR_DETAIL_MODE;
        if (ProjectTask.PROJECT_TASK_CALENDAR_SCREEN.equalsIgnoreCase(strCommand))
            return ProjectTask.PROJECT_TASK_CALENDAR_MODE;
        return super.commandToDocType(strCommand);
    }
    /**
     * LinkLastPredecessor Method.
     */
    public boolean linkLastPredecessor(Record recNewProjectTask, boolean bDisplayOption)
    {
        if ((recNewProjectTask == null)
            || (recNewProjectTask.getEditMode() != DBConstants.EDIT_ADD))
                return false; // Being careful
        Object bookmark = recNewProjectTask.getLastModified(DBConstants.BOOKMARK_HANDLE);
        if (bookmark == null)
            return false; // Never
        if (m_recDetail == null)
        {
            RecordOwner recordOwner = this.getRecordOwner();
            m_recDetail = new ProjectTask(recordOwner);
            if (recordOwner != null)
                recordOwner.removeRecord(m_recDetail);
            m_recDetail.addListener(new SubFileFilter(this));
        }
        int iMoveMode = DBConstants.SCREEN_MOVE;
        m_recDetail.getKeyArea().setKeyOrder(DBConstants.DESCENDING);
        try {
            m_recDetail.close();
            while (m_recDetail.hasNext())
            {   // Always
                m_recDetail.next();
                if (m_recDetail.getHandle(DBConstants.BOOKMARK_HANDLE).equals(bookmark))
                    continue;   // Don't use the one you just added
                if ((!m_recDetail.getField(ProjectTask.END_DATE_TIME).equals(this.getField(ProjectTask.END_DATE_TIME)))
                    || (!m_recDetail.getField(ProjectTask.END_DATE_TIME).equals(recNewProjectTask.getField(ProjectTask.START_DATE_TIME))))
                        return false;   // Never
        
                ProjectTaskPredecessor recProjectTaskPredecessor = this.getProjectTaskPredecessor();
                recProjectTaskPredecessor.addNew();
                recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PROJECT_TASK_PREDECESSOR_ID).moveFieldToThis(m_recDetail.getField(ProjectTask.ID));
                recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PROJECT_TASK_ID).setData(bookmark);
                recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PREDECESSOR_TYPE).setString(PredecessorTypeField.FINISH_START);
                recProjectTaskPredecessor.add();
        
                this.getListener(UpdateChildrenHandler.class).setEnabledListener(false);
        
                this.edit();
                ((DateTimeField)this.getField(ProjectTask.END_DATE_TIME)).moveFieldToThis(recNewProjectTask.getField(ProjectTask.END_DATE_TIME), bDisplayOption, iMoveMode);
                bookmark = this.getHandle(DBConstants.BOOKMARK_HANDLE);
                this.set();
                this.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                return true;    // Success - Survey unnecessary
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            this.getListener(UpdateChildrenHandler.class).setEnabledListener(true);
            m_recDetail.getKeyArea().setKeyOrder(DBConstants.ASCENDING);
        }
        return false;
    }
    /**
     * UpdateChildren Method.
     */
    public boolean updateChildren(boolean bDisplayOption)
    {
        int iMoveMode = DBConstants.SCREEN_MOVE;
        ProjectTask recDetailChildren = this.getDetailChildren();
        Date startDate = ((DateTimeField)this.getField(ProjectTask.START_DATE_TIME)).getDateTime();
        Date endDate = ((DateTimeField)this.getField(ProjectTask.END_DATE_TIME)).getDateTime();
        try {
            boolean bFirstRecord = true;
            double dOffset = 0;
            recDetailChildren.getListener(SurveyDatesHandler.class).setEnabledListener(false);
            ((UpdateDependenciesHandler)recDetailChildren.getListener(UpdateDependenciesHandler.class)).setMoveSiblingDependents(false);
            recDetailChildren.close();
            while (recDetailChildren.hasNext())
            {
                recDetailChildren.next();
                Date thisStartDate = ((DateTimeField)recDetailChildren.getField(ProjectTask.START_DATE_TIME)).getDateTime();
                Date thisEndDate = ((DateTimeField)recDetailChildren.getField(ProjectTask.END_DATE_TIME)).getDateTime();
                if (bFirstRecord)
                {
                    bFirstRecord = false;
                    if ((thisStartDate == null) || (thisEndDate == null) || (startDate == null) || (endDate == null))
                        break;
                    if (thisStartDate.equals(startDate))
                        break;
                    dOffset = startDate.getTime() - thisStartDate.getTime();
                    if ((dOffset >= -1000) && (dOffset <= 1000))
                        break;
                }
                recDetailChildren.edit();
                Converter.gCalendar = ((DateTimeField)recDetailChildren.getField(ProjectTask.START_DATE_TIME)).getCalendar();
                Converter.gCalendar.add(Calendar.MILLISECOND, (int)dOffset);
                ((DateTimeField)recDetailChildren.getField(ProjectTask.START_DATE_TIME)).setCalendar(Converter.gCalendar, bDisplayOption, iMoveMode);
                recDetailChildren.set();
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            recDetailChildren.getListener(SurveyDatesHandler.class).setEnabledListener(true);
            ((UpdateDependenciesHandler)recDetailChildren.getListener(UpdateDependenciesHandler.class)).setMoveSiblingDependents(true);
        }
        return false;   // No update required
    }
    /**
     * UpdateDependencies Method.
     */
    public void updateDependencies(boolean bMoveSiblings, boolean bDisplayOption)
    {
        ProjectTaskPredecessor recProjectTaskPredecessor = this.getProjectTaskPredecessor();
        Converter.initGlobals();
        int iMoveMode = DBConstants.SCREEN_MOVE;
        BaseListener listener = null;
        try {
        
            DateTimeField fldSource = (DateTimeField)this.getField(ProjectTask.END_DATE_TIME);
            if (fldSource.isModified())
            {
                recProjectTaskPredecessor.addListener(listener = new SubFileFilter(this.getField(ProjectTask.ID), ProjectTaskPredecessor.PROJECT_TASK_PREDECESSOR_ID, null, null, null, null));
                recProjectTaskPredecessor.setKeyArea(ProjectTaskPredecessor.PROJECT_TASK_PREDECESSOR_ID_KEY);
                recProjectTaskPredecessor.close();
                while (recProjectTaskPredecessor.hasNext())
                {
                    recProjectTaskPredecessor.next();
                    String strPredecessorType = recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PREDECESSOR_TYPE).toString();
                    if ((PredecessorTypeField.FINISH_START.equals(strPredecessorType))
                        || (PredecessorTypeField.FINISH_FINISH.equals(strPredecessorType)))
                    {
                        Record recProjectTask = ((ReferenceField)recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PROJECT_TASK_ID)).getReference();
                        if (recProjectTask != null)
                            if ((recProjectTask.getEditMode() == DBConstants.EDIT_CURRENT)
                                || (recProjectTask.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                        {
                            if ((bMoveSiblings == true)
                                || (!recProjectTask.getField(ProjectTask.PARENT_FOLDER_ID).equals(this.getField(ProjectTask.PARENT_FOLDER_ID))))
                            {
                                double iAdditionalOffset = 0;
                                if (PredecessorTypeField.FINISH_FINISH.equals(strPredecessorType))
                                    iAdditionalOffset = recProjectTask.getField(ProjectTask.DURATION).getValue();
                                recProjectTask.getListener(SurveyDatesHandler.class).setEnabledListener(false);
                                recProjectTask.edit();
                                Converter.gCalendar = fldSource.getCalendar();
                                if ((recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PREDECESSOR_DELAY).getValue() + iAdditionalOffset) != 0)
                                    Converter.gCalendar.add(Calendar.SECOND, (int)((recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PREDECESSOR_DELAY).getValue() + iAdditionalOffset) * 24 * 60 * 60));
            
                                Calendar lowerCalendarValue = ((DateTimeField)recProjectTask.getField(ProjectTask.START_DATE_TIME)).getCalendar();
                                lowerCalendarValue.add(Calendar.MINUTE, -1);
                                Calendar upperCalendarValue = ((DateTimeField)recProjectTask.getField(ProjectTask.START_DATE_TIME)).getCalendar();
                                upperCalendarValue.add(Calendar.MINUTE, 1);
                                if ((Converter.gCalendar.before(lowerCalendarValue))
                                    || (Converter.gCalendar.after(upperCalendarValue)))
                                        ((DateTimeField)recProjectTask.getField(ProjectTask.START_DATE_TIME)).setCalendar(Converter.gCalendar, bDisplayOption, iMoveMode);
                                recProjectTask.set();
                                recProjectTask.getListener(SurveyDatesHandler.class).setEnabledListener(true);
                            }
                        }
                    }
                }
            }
            if (listener != null)
                recProjectTaskPredecessor.removeListener(listener, true);
        
            fldSource = (DateTimeField)this.getField(ProjectTask.START_DATE_TIME);
            if (fldSource.isModified())
            {
                recProjectTaskPredecessor.addListener(listener = new SubFileFilter(this.getField(ProjectTask.ID), ProjectTaskPredecessor.PROJECT_TASK_ID, null, null, null, null));
                recProjectTaskPredecessor.setKeyArea(ProjectTaskPredecessor.PROJECT_TASK_ID_KEY);
                recProjectTaskPredecessor.close();
                while (recProjectTaskPredecessor.hasNext())
                {
                    recProjectTaskPredecessor.next();
                    String strPredecessorType = recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PREDECESSOR_TYPE).toString();
                    if ((PredecessorTypeField.FINISH_START.equals(strPredecessorType))
                        || (PredecessorTypeField.START_START.equals(strPredecessorType)))
                    {
                        Record recProjectTask = ((ReferenceField)recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PROJECT_TASK_PREDECESSOR_ID)).getReference();
                        if (recProjectTask != null)
                            if ((recProjectTask.getEditMode() == DBConstants.EDIT_CURRENT)
                                || (recProjectTask.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                        {
                            if ((bMoveSiblings == true)
                                    || (!recProjectTask.getField(ProjectTask.PARENT_FOLDER_ID).equals(this.getField(ProjectTask.PARENT_FOLDER_ID))))
                            {
                                double iAdditionalOffset = 0;
                                if (PredecessorTypeField.FINISH_START.equals(strPredecessorType))
                                    iAdditionalOffset = -recProjectTask.getField(ProjectTask.DURATION).getValue();
                                recProjectTask.getListener(SurveyDatesHandler.class).setEnabledListener(false);
                                recProjectTask.edit();
                                Converter.gCalendar = fldSource.getCalendar();
                                if ((recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PREDECESSOR_DELAY).getValue() + iAdditionalOffset) != 0)
                                    Converter.gCalendar.add(Calendar.SECOND, (int)((recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PREDECESSOR_DELAY).getValue() + iAdditionalOffset) * 24 * 60 * 60));
                                Calendar lowerCalendarValue = ((DateTimeField)recProjectTask.getField(ProjectTask.START_DATE_TIME)).getCalendar();
                                lowerCalendarValue.add(Calendar.MINUTE, -1);
                                Calendar upperCalendarValue = ((DateTimeField)recProjectTask.getField(ProjectTask.START_DATE_TIME)).getCalendar();
                                upperCalendarValue.add(Calendar.MINUTE, 1);
                                if ((Converter.gCalendar.before(lowerCalendarValue))
                                    || (Converter.gCalendar.after(upperCalendarValue)))
                                        ((DateTimeField)recProjectTask.getField(ProjectTask.START_DATE_TIME)).setCalendar(Converter.gCalendar, bDisplayOption, iMoveMode);
                                recProjectTask.set();
                                recProjectTask.getListener(SurveyDatesHandler.class).setEnabledListener(true);
                            }
                        }
                    }
                }
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            if (listener != null)
                recProjectTaskPredecessor.removeListener(listener, true);
        }
    }
    /**
     * SurveyDates Method.
     */
    public boolean surveyDates(boolean bDisplayOption)
    {
        if (m_recDetail == null)
        {
            RecordOwner recordOwner = this.getRecordOwner();
            m_recDetail = new ProjectTask(recordOwner);
            if (recordOwner != null)
                recordOwner.removeRecord(m_recDetail);
            m_recDetail.addListener(new SubFileFilter(this, true));
        }
        Date startDate = null;
        Date endDate = null;
        try {
            m_recDetail.close();
            while (m_recDetail.hasNext())
            {
                m_recDetail.next();
                Date thisStartDate = null;
                if (!m_recDetail.getField(ProjectTask.START_DATE_TIME).isNull())
                    thisStartDate = ((DateTimeField)m_recDetail.getField(ProjectTask.START_DATE_TIME)).getDateTime();
                if (thisStartDate != null)
                    if ((startDate == null)
                        || (thisStartDate.before(startDate)))
                            startDate = thisStartDate;
                Date thisEndDate = null;
                if (!m_recDetail.getField(ProjectTask.END_DATE_TIME).isNull())
                    thisEndDate = ((DateTimeField)m_recDetail.getField(ProjectTask.END_DATE_TIME)).getDateTime();
                if (thisEndDate != null)
                    if ((endDate == null)
                        || (thisEndDate.after(endDate)))
                            endDate = thisEndDate;
            }
            this.getListener(UpdateChildrenHandler.class).setEnabledListener(false);
            this.edit();
            int iMoveMode = DBConstants.SCREEN_MOVE;
            if (startDate != null)
                ((DateTimeField)this.getField(ProjectTask.START_DATE_TIME)).setDateTime(startDate, bDisplayOption, iMoveMode);
            if (endDate != null)
                ((DateTimeField)this.getField(ProjectTask.END_DATE_TIME)).setDateTime(endDate, bDisplayOption, iMoveMode);
            boolean bHasChildren = true;
            if ((startDate == null) && (endDate == null))
                bHasChildren = false;
            this.getField(ProjectTask.HAS_CHILDREN).setState(bHasChildren);
            if (this.isModified())
            {
                Object bookmark = this.getHandle(DBConstants.BOOKMARK_HANDLE);
                this.set();
                this.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                return true;    // Record updated
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            this.getListener(UpdateChildrenHandler.class).setEnabledListener(true);
        }
        return false;   // No update required
    }
    /**
     * GetProjectTaskPredecessor Method.
     */
    public ProjectTaskPredecessor getProjectTaskPredecessor()
    {
        if (m_recProjectTaskPredecessor == null)
        {
            m_recProjectTaskPredecessor = new ProjectTaskPredecessor(this.getRecordOwner());
            // Must have the sub-records set up in advance to EEP them from mixing with this record.
            ((ReferenceField)m_recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PROJECT_TASK_ID)).getReferenceRecord();
            ((ReferenceField)m_recProjectTaskPredecessor.getField(ProjectTaskPredecessor.PROJECT_TASK_PREDECESSOR_ID)).getReferenceRecord();
        }
        return m_recProjectTaskPredecessor;
    }
    /**
     * Does this project have sub-tasks?.
     */
    public boolean isParentTask()
    {
        return this.getField(ProjectTask.HAS_CHILDREN).getState();
    }
    /**
     * GetDetailChildren Method.
     */
    public ProjectTask getDetailChildren()
    {
        if (m_recDetailChildren == null)
        {
            RecordOwner recordOwner = this.getRecordOwner();
            m_recDetailChildren = new ProjectTask(recordOwner);
            if (recordOwner != null)
                recordOwner.removeRecord(m_recDetailChildren);
            m_recDetailChildren.addListener(new SubFileFilter(this, true));
        }
        return m_recDetailChildren;
    }

}
