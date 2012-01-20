/**
 * @(#)Layout.
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
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.app.program.screen.*;
import org.jbundle.model.app.program.db.*;

/**
 *  Layout - Record layouts.
 */
public class Layout extends Folder
     implements LayoutModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kName = kName;
    //public static final int kParentFolderID = kParentFolderID;
    //public static final int kSequence = kSequence;
    //public static final int kComment = kComment;
    //public static final int kCode = kCode;
    public static final int kType = kFolderLastField + 1;
    public static final int kFieldValue = kType + 1;
    public static final int kReturnsValue = kFieldValue + 1;
    public static final int kMax = kReturnsValue + 1;
    public static final int kSystem = kMax + 1;
    public static final int kComments = kSystem + 1;
    public static final int kLayoutLastField = kComments;
    public static final int kLayoutFields = kComments - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kParentFolderIDKey = kIDKey + 1;
    public static final int kLayoutLastKey = kParentFolderIDKey;
    public static final int kLayoutKeys = kParentFolderIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public Layout()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Layout(RecordOwner screen)
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

    public static final String kLayoutFile = "Layout";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kLayoutFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Layout";
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
        if ((iDocMode & ScreenConstants.DOC_MODE_MASK) == ScreenConstants.DETAIL_MODE)
            screen = new LayoutGridScreen(this, null, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = new LayoutScreen(this, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else
            screen = new LayoutGridScreen(this, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
            field = new StringField(this, "Name", 50, null, null);
        if (iFieldSeq == kType)
            field = new StringField(this, "Type", 50, null, null);
        if (iFieldSeq == kFieldValue)
            field = new StringField(this, "FieldValue", 255, null, null);
        if (iFieldSeq == kReturnsValue)
            field = new StringField(this, "ReturnsValue", 50, null, null);
        if (iFieldSeq == kMax)
            field = new IntegerField(this, "Max", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kParentFolderID)
        //  field = new FolderField(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kSequence)
        //  field = new ShortField(this, "Sequence", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kComment)
            field = new MemoField(this, "Comment", 255, null, null);
        if (iFieldSeq == kSystem)
            field = new StringField(this, "System", 30, null, null);
        if (iFieldSeq == kComments)
            field = new BooleanField(this, "Comments", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kCode)
        //  field = new StringField(this, "Code", 30, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kLayoutLastField)
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
        if (iKeyArea == kParentFolderIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ParentFolderID");
            keyArea.addKeyField(kParentFolderID, DBConstants.ASCENDING);
            keyArea.addKeyField(kSequence, DBConstants.ASCENDING);
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kLayoutLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kLayoutLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
