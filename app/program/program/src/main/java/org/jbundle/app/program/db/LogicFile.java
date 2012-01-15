/**
 * @(#)LogicFile.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

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
import org.jbundle.app.program.screen.*;
import org.jbundle.model.app.program.db.*;

/**
 *  LogicFile - Method Descriptions.
 */
public class LogicFile extends VirtualRecord
     implements LogicFileModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kSequence = kVirtualRecordLastField + 1;
    public static final int kMethodName = kSequence + 1;
    public static final int kLogicDescription = kMethodName + 1;
    public static final int kMethodReturns = kLogicDescription + 1;
    public static final int kMethodInterface = kMethodReturns + 1;
    public static final int kMethodClassName = kMethodInterface + 1;
    public static final int kLogicSource = kMethodClassName + 1;
    public static final int kLogicThrows = kLogicSource + 1;
    public static final int kProtection = kLogicThrows + 1;
    public static final int kCopyFrom = kProtection + 1;
    public static final int kIncludeScope = kCopyFrom + 1;
    public static final int kLogicFileLastField = kIncludeScope;
    public static final int kLogicFileFields = kIncludeScope - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kMethodClassNameKey = kIDKey + 1;
    public static final int kSequenceKey = kMethodClassNameKey + 1;
    public static final int kLogicFileLastKey = kSequenceKey;
    public static final int kLogicFileKeys = kSequenceKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public LogicFile()
    {
        super();
    }
    /**
     * Constructor.
     */
    public LogicFile(RecordOwner screen)
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

    public static final String kLogicFileFile = "LogicFile";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kLogicFileFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Logic";
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
        return DBConstants.REMOTE | DBConstants.SHARED_DATA | DBConstants.HIERARCHICAL;
    }
    /**
     * Make a default screen.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = BaseScreen.makeNewScreen(LOGIC_FILE_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = BaseScreen.makeNewScreen(LOGIC_FILE_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        if (iFieldSeq == kSequence)
            field = new IntegerField(this, "Sequence", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(1000));
        if (iFieldSeq == kMethodName)
        {
            field = new StringField(this, "MethodName", 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kLogicDescription)
            field = new MemoField(this, "LogicDescription", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMethodReturns)
            field = new StringField(this, "MethodReturns", 255, null, null);
        if (iFieldSeq == kMethodInterface)
            field = new StringField(this, "MethodInterface", 255, null, null);
        if (iFieldSeq == kMethodClassName)
            field = new StringField(this, "MethodClassName", 40, null, null);
        if (iFieldSeq == kLogicSource)
            field = new MemoField(this, "LogicSource", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kLogicThrows)
            field = new StringField(this, "LogicThrows", 255, null, null);
        if (iFieldSeq == kProtection)
            field = new StringField(this, "Protection", 60, null, null);
        if (iFieldSeq == kCopyFrom)
            field = new StringField(this, "CopyFrom", 40, null, null);
        if (iFieldSeq == kIncludeScope)
        {
            field = new IncludeScopeField(this, "IncludeScope", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0x001));
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kLogicFileLastField)
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
        if (iKeyArea == kMethodClassNameKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "MethodClassName");
            keyArea.addKeyField(kMethodClassName, DBConstants.ASCENDING);
            keyArea.addKeyField(kMethodName, DBConstants.ASCENDING);
        }
        if (iKeyArea == kSequenceKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Sequence");
            keyArea.addKeyField(kMethodClassName, DBConstants.ASCENDING);
            keyArea.addKeyField(kSequence, DBConstants.ASCENDING);
            keyArea.addKeyField(kMethodName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kLogicFileLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kLogicFileLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
