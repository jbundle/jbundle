/**
 * @(#)Issue.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.issue.db;

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
import org.jbundle.app.program.issue.screen.*;
import org.jbundle.app.program.project.db.*;
import org.jbundle.main.user.db.*;
import org.jbundle.app.program.db.*;
import org.jbundle.model.app.program.issue.db.*;

/**
 *  Issue - Bug or Issue record.
 */
public class Issue extends VirtualRecord
     implements IssueModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kDescription = kVirtualRecordLastField + 1;
    public static final int kProjectID = kDescription + 1;
    public static final int kProjectVersionID = kProjectID + 1;
    public static final int kIssueTypeID = kProjectVersionID + 1;
    public static final int kIssueStatusID = kIssueTypeID + 1;
    public static final int kAssignedUserID = kIssueStatusID + 1;
    public static final int kIssuePriorityID = kAssignedUserID + 1;
    public static final int kIssueSequence = kIssuePriorityID + 1;
    public static final int kEnteredDate = kIssueSequence + 1;
    public static final int kEnteredByUserID = kEnteredDate + 1;
    public static final int kChangedDate = kEnteredByUserID + 1;
    public static final int kChangedByUserID = kChangedDate + 1;
    public static final int kClassInfoID = kChangedByUserID + 1;
    public static final int kIssueLastField = kClassInfoID;
    public static final int kIssueFields = kClassInfoID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kDescriptionKey = kIDKey + 1;
    public static final int kIssuePriorityIDKey = kDescriptionKey + 1;
    public static final int kClassInfoIDKey = kIssuePriorityIDKey + 1;
    public static final int kIssueLastKey = kClassInfoIDKey;
    public static final int kIssueKeys = kClassInfoIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public Issue()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Issue(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    public static final String kIssueFile = "Issue";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kIssueFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Issue";
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
        if ((iDocMode & ScreenConstants.DETAIL_MODE) == ScreenConstants.DETAIL_MODE)
            screen = new IssueHistoryGridScreen(this, null, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = new IssueScreen(this, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else
            screen = new IssueGridScreen(this, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 120, null, null);
        if (iFieldSeq == kProjectID)
        {
            field = new ProjectFilter(this, "ProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kProjectVersionID)
        {
            field = new ProjectVersionField(this, "ProjectVersionID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kIssueTypeID)
        {
            field = new IssueTypeField(this, "IssueTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kIssueStatusID)
        {
            field = new IssueStatusField(this, "IssueStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kAssignedUserID)
            field = new Issue_AssignedUserID(this, "AssignedUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIssuePriorityID)
        {
            field = new IssuePriorityField(this, "IssuePriorityID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kIssueSequence)
            field = new IntegerField(this, "IssueSequence", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEnteredDate)
            field = new Issue_EnteredDate(this, "EnteredDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEnteredByUserID)
            field = new UserField(this, "EnteredByUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kChangedDate)
            field = new DateTimeField(this, "ChangedDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kChangedByUserID)
            field = new UserField(this, "ChangedByUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassInfoID)
            field = new ClassInfoField(this, "ClassInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kIssueLastField)
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
        if (iKeyArea == kDescriptionKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Description");
            keyArea.addKeyField(kDescription, DBConstants.ASCENDING);
        }
        if (iKeyArea == kIssuePriorityIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "IssuePriorityID");
            keyArea.addKeyField(kIssuePriorityID, DBConstants.ASCENDING);
            keyArea.addKeyField(kIssueSequence, DBConstants.ASCENDING);
            keyArea.addKeyField(kDescription, DBConstants.ASCENDING);
        }
        if (iKeyArea == kClassInfoIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ClassInfoID");
            keyArea.addKeyField(kClassInfoID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kIssueLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kIssueLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        this.addListener(new SetUserIDHandler(Issue.ENTERED_BY_USER_ID, true));
        this.addListener(new SetUserIDHandler(Issue.CHANGED_BY_USER_ID, true));
        this.addListener(new DateChangedHandler(Issue.CHANGED_DATE));
    }

}
