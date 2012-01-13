/**
 * @(#)IssueControl.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.app.program.project.db.*;
import org.jbundle.model.app.program.issue.db.*;

/**
 *  IssueControl - .
 */
public class IssueControl extends ControlRecord
     implements IssueControlModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kProjectID = kControlRecordLastField + 1;
    public static final int kProjectVersionID = kProjectID + 1;
    public static final int kIssueTypeID = kProjectVersionID + 1;
    public static final int kIssueStatusID = kIssueTypeID + 1;
    public static final int kIssuePriorityID = kIssueStatusID + 1;
    public static final int kIssueControlLastField = kIssuePriorityID;
    public static final int kIssueControlFields = kIssuePriorityID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kIssueControlLastKey = kIDKey;
    public static final int kIssueControlKeys = kIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public IssueControl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public IssueControl(RecordOwner screen)
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

    public static final String kIssueControlFile = "IssueControl";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kIssueControlFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.LOCAL | DBConstants.USER_DATA;
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
        if (iFieldSeq == kProjectID)
            field = new ProjectField(this, "ProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProjectVersionID)
            field = new ProjectVersionField(this, "ProjectVersionID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIssueTypeID)
            field = new IssueTypeField(this, "IssueTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIssueStatusID)
            field = new IssueStatusField(this, "IssueStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIssuePriorityID)
            field = new IssuePriorityField(this, "IssuePriorityID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kIssueControlLastField)
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
        if (keyArea == null) if (iKeyArea < kIssueControlLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kIssueControlLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
