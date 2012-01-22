/**
 * @(#)ProgramControl.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.resource.db.*;
import org.jbundle.model.app.program.db.*;

/**
 *  ProgramControl - Program control.
 */
public class ProgramControl extends ControlRecord
     implements ProgramControlModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kProjectName = kControlRecordLastField + 1;
    public static final int kBaseDirectory = kProjectName + 1;
    public static final int kSourceDirectory = kBaseDirectory + 1;
    public static final int kClassDirectory = kSourceDirectory + 1;
    public static final int kArchiveDirectory = kClassDirectory + 1;
    public static final int kResourceType = kArchiveDirectory + 1;
    public static final int kClassResourceType = kResourceType + 1;
    public static final int kPackageName = kClassResourceType + 1;
    public static final int kInterfacePackage = kPackageName + 1;
    public static final int kThinPackage = kInterfacePackage + 1;
    public static final int kResourcePackage = kThinPackage + 1;
    public static final int kLastPackageUpdate = kResourcePackage + 1;
    public static final int kPackagesBasePath = kLastPackageUpdate + 1;
    public static final int kPackagesPath = kPackagesBasePath + 1;
    public static final int kProgramControlLastField = kPackagesPath;
    public static final int kProgramControlFields = kPackagesPath - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kProgramControlLastKey = kIDKey;
    public static final int kProgramControlKeys = kIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public ProgramControl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProgramControl(RecordOwner screen)
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

    public static final String kProgramControlFile = "ProgramControl";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kProgramControlFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        if (iFieldSeq == kProjectName)
            field = new StringField(this, "ProjectName", 30, null, null);
        if (iFieldSeq == kBaseDirectory)
        {
            field = new StringField(this, "BaseDirectory", 127, null, "/home/don/workspace/tour/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kSourceDirectory)
        {
            field = new StringField(this, "SourceDirectory", 127, null, "src/main/java/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kClassDirectory)
        {
            field = new StringField(this, "ClassDirectory", 127, null, "target/classes/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kArchiveDirectory)
        {
            field = new StringField(this, "ArchiveDirectory", 127, null, "data/archive/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kResourceType)
            field = new ResourceTypeField(this, "ResourceType", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassResourceType)
            field = new ResourceTypeField(this, "ClassResourceType", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kPackageName)
            field = new StringField(this, "PackageName", 40, null, null);
        if (iFieldSeq == kInterfacePackage)
        {
            field = new StringField(this, "InterfacePackage", Constants.DEFAULT_FIELD_LENGTH, null, ".rec");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kThinPackage)
        {
            field = new StringField(this, "ThinPackage", 40, null, ".thin");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kResourcePackage)
        {
            field = new StringField(this, "ResourcePackage", 40, null, ".res");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kLastPackageUpdate)
            field = new DateTimeField(this, "LastPackageUpdate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kPackagesBasePath)
            field = new StringField(this, "PackagesBasePath", 128, null, null);
        if (iFieldSeq == kPackagesPath)
            field = new StringField(this, "PackagesPath", 128, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kProgramControlLastField)
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
        if (keyArea == null) if (iKeyArea < kProgramControlLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kProgramControlLastKey)
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
        this.getField(ProgramControl.kLastPackageUpdate).setEnabled(false);
    }

}
