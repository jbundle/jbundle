/**
 * @(#)Packages.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.packages.db;

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
import org.jbundle.main.db.*;
import org.jbundle.app.program.packages.screen.*;
import org.jbundle.app.program.script.scan.*;
import org.jbundle.app.program.manual.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.trx.message.*;
import org.jbundle.base.thread.*;
import org.jbundle.app.program.db.*;

/**
 *  Packages - Packages.
 */
public class Packages extends Folder
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kName = kName;
    //public static final int kParentFolderID = kParentFolderID;
    //public static final int kSequence = kSequence;
    //public static final int kComment = kComment;
    //public static final int kCode = kCode;
    public static final int kClassProjectID = kFolderLastField + 1;
    public static final int kCodeType = kClassProjectID + 1;
    public static final int kPartID = kCodeType + 1;
    public static final int kRecursive = kPartID + 1;
    public static final int kExclude = kRecursive + 1;
    public static final int kManual = kExclude + 1;
    public static final int kLastUpdated = kManual + 1;
    public static final int kPackagesLastField = kLastUpdated;
    public static final int kPackagesFields = kLastUpdated - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kParentFolderIDKey = kIDKey + 1;
    public static final int kCodeKey = kParentFolderIDKey + 1;
    public static final int kNameKey = kCodeKey + 1;
    public static final int kPartIDKey = kNameKey + 1;
    public static final int kPackagesLastKey = kPartIDKey;
    public static final int kPackagesKeys = kPartIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    public static final String SCAN = "Scan";
    /**
     * Default constructor.
     */
    public Packages()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Packages(RecordOwner screen)
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

    public static final String kPackagesFile = "Packages";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kPackagesFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.DOC_MODE_MASK) == ScreenConstants.DETAIL_MODE)
            screen = new PackagesGridScreen(this, null, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = new PackagesScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = new PackagesGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        //if (iFieldSeq == kName)
        //  field = new StringField(this, "Name", 40, null, null);
        if (iFieldSeq == kParentFolderID)
            field = new PackagesField(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kSequence)
        //  field = new ShortField(this, "Sequence", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassProjectID)
            field = new ClassProjectField(this, "ClassProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kCodeType)
        {
            field = new CodeTypeField(this, "CodeType", Constants.DEFAULT_FIELD_LENGTH, null, "BASE");
            field.addListener(new InitOnceFieldHandler(null));
        }
        //if (iFieldSeq == kComment)
        //  field = new MemoField(this, "Comment", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kCode)
        //  field = new StringField(this, "Code", 30, null, null);
        if (iFieldSeq == kPartID)
            field = new PartField(this, "PartID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kRecursive)
            field = new BooleanField(this, "Recursive", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        if (iFieldSeq == kExclude)
            field = new BooleanField(this, "Exclude", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kManual)
            field = new BooleanField(this, "Manual", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        if (iFieldSeq == kLastUpdated)
            field = new DateTimeField(this, "LastUpdated", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kPackagesLastField)
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
            keyArea.addKeyField(kName, DBConstants.ASCENDING);
        }
        if (iKeyArea == kCodeKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Code");
            keyArea.addKeyField(kCode, DBConstants.ASCENDING);
        }
        if (iKeyArea == kNameKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "Name");
            keyArea.addKeyField(kParentFolderID, DBConstants.ASCENDING);
            keyArea.addKeyField(kName, DBConstants.ASCENDING);
            keyArea.addKeyField(kClassProjectID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kPartIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "PartID");
            keyArea.addKeyField(kPartID, DBConstants.ASCENDING);
            keyArea.addKeyField(kSequence, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kPackagesLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kPackagesLastKey)
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
        this.addListener(new DateChangedHandler(Packages.kLastUpdated));
    }

}
