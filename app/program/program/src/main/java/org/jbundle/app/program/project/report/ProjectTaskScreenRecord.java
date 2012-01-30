/**
 * @(#)ProjectTaskScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.report;

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
import org.jbundle.app.program.project.db.*;

/**
 *  ProjectTaskScreenRecord - .
 */
public class ProjectTaskScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String PROJECT_TASK_ID = "ProjectTaskID";
    public static final int kProjectTaskID = kScreenRecordLastField + 1;
    public static final String CURRENT_LEVEL = "CurrentLevel";
    public static final int kCurrentLevel = kProjectTaskID + 1;
    public static final int kProjectTaskScreenRecordLastField = kCurrentLevel;
    public static final int kProjectTaskScreenRecordFields = kCurrentLevel - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public ProjectTaskScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProjectTaskScreenRecord(RecordOwner screen)
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

    public static final String kProjectTaskScreenRecordFile = null;   // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kProjectTaskID)
            field = new ProjectTaskField(this, "ProjectTaskID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kCurrentLevel)
            field = new IntegerField(this, "CurrentLevel", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0));
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kProjectTaskScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
