/**
 * @(#)ProjectTaskPredecessor.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.app.program.project.screen.*;
import org.jbundle.model.app.program.project.db.*;

/**
 *  ProjectTaskPredecessor - .
 */
public class ProjectTaskPredecessor extends VirtualRecord
     implements ProjectTaskPredecessorModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kProjectTaskID = kVirtualRecordLastField + 1;
    public static final int kProjectTaskPredecessorID = kProjectTaskID + 1;
    public static final int kPredecessorType = kProjectTaskPredecessorID + 1;
    public static final int kPredecessorDelay = kPredecessorType + 1;
    public static final int kProjectTaskPredecessorLastField = kPredecessorDelay;
    public static final int kProjectTaskPredecessorFields = kPredecessorDelay - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kProjectTaskIDKey = kIDKey + 1;
    public static final int kProjectTaskPredecessorIDKey = kProjectTaskIDKey + 1;
    public static final int kProjectTaskPredecessorLastKey = kProjectTaskPredecessorIDKey;
    public static final int kProjectTaskPredecessorKeys = kProjectTaskPredecessorIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public ProjectTaskPredecessor()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProjectTaskPredecessor(RecordOwner screen)
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

    public static final String kProjectTaskPredecessorFile = "ProjectTaskPredecessor";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kProjectTaskPredecessorFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
     * Make a default screen.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = BaseScreen.makeNewScreen(PROJECT_TASK_PREDECESSOR_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = BaseScreen.makeNewScreen(PROJECT_TASK_PREDECESSOR_GRID_SCREEN_CLA, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
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
        if (iFieldSeq == kProjectTaskID)
            field = new ProjectTaskField(this, "ProjectTaskID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProjectTaskPredecessorID)
            field = new ProjectTaskField(this, "ProjectTaskPredecessorID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kPredecessorType)
        {
            field = new PredecessorTypeField(this, "PredecessorType", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kPredecessorDelay)
            field = new IntegerField(this, "PredecessorDelay", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kProjectTaskPredecessorLastField)
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
        if (iKeyArea == kProjectTaskIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ProjectTaskID");
            keyArea.addKeyField(kProjectTaskID, DBConstants.ASCENDING);
            keyArea.addKeyField(kProjectTaskPredecessorID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kProjectTaskPredecessorIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ProjectTaskPredecessorID");
            keyArea.addKeyField(kProjectTaskPredecessorID, DBConstants.ASCENDING);
            keyArea.addKeyField(kProjectTaskID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kProjectTaskPredecessorLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kProjectTaskPredecessorLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
