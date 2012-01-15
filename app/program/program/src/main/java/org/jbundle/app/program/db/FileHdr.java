/**
 * @(#)FileHdr.
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
import org.jbundle.model.app.program.db.*;

/**
 *  FileHdr - File Information Record.
 */
public class FileHdr extends VirtualRecord
     implements FileHdrModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kFileName = kVirtualRecordLastField + 1;
    public static final int kFileDesc = kFileName + 1;
    public static final int kFileMainFilename = kFileDesc + 1;
    public static final int kType = kFileMainFilename + 1;
    public static final int kFileNotes = kType + 1;
    public static final int kDatabaseName = kFileNotes + 1;
    public static final int kFileRecCalled = kDatabaseName + 1;
    public static final int kDisplayClass = kFileRecCalled + 1;
    public static final int kMaintClass = kDisplayClass + 1;
    public static final int kFileHdrLastField = kMaintClass;
    public static final int kFileHdrFields = kMaintClass - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kFileNameKey = kIDKey + 1;
    public static final int kFileHdrLastKey = kFileNameKey;
    public static final int kFileHdrKeys = kFileNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public FileHdr()
    {
        super();
    }
    /**
     * Constructor.
     */
    public FileHdr(RecordOwner screen)
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

    public static final String kFileHdrFile = "FileHdr";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kFileHdrFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "File";
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
            screen = BaseScreen.makeNewScreen(FILE_HDR_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        if (iFieldSeq == kFileName)
        {
            field = new StringField(this, "FileName", 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kFileDesc)
            field = new StringField(this, "FileDesc", 40, null, null);
        if (iFieldSeq == kFileMainFilename)
            field = new StringField(this, "FileMainFilename", 40, null, null);
        if (iFieldSeq == kType)
            field = new StringField(this, "Type", 60, null, null);
        if (iFieldSeq == kFileNotes)
            field = new MemoField(this, "FileNotes", 9999, null, null);
        if (iFieldSeq == kDatabaseName)
            field = new StringField(this, "DatabaseName", 20, null, null);
        if (iFieldSeq == kFileRecCalled)
            field = new StringField(this, "FileRecCalled", 40, null, null);
        if (iFieldSeq == kDisplayClass)
            field = new StringField(this, "DisplayClass", 40, null, null);
        if (iFieldSeq == kMaintClass)
            field = new StringField(this, "MaintClass", 40, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kFileHdrLastField)
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
        if (iKeyArea == kFileNameKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "FileName");
            keyArea.addKeyField(kFileName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kFileHdrLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kFileHdrLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Is this the physical file that can be imported/exported, etc?.
     */
    public boolean isPhysicalFile()
    {
        String strFileType = this.getField(FileHdr.kType).toString();
        if (strFileType == null)
            return false;
        strFileType = strFileType.toUpperCase();
        boolean bPhysicalFile = false;
        if (strFileType.indexOf(DBParams.LOCAL.toUpperCase()) != -1)
            bPhysicalFile = true;
        if (strFileType.indexOf(DBParams.REMOTE.toUpperCase()) != -1)
           bPhysicalFile = true;
        if ((" " + strFileType).indexOf(" " + DBParams.TABLE.toUpperCase()) != -1)
           bPhysicalFile = true;
        if (bPhysicalFile)
            if (strFileType.indexOf("SHARED_TABLE") != -1)
                if (strFileType.indexOf("BASE_TABLE_CLASS") == -1)
                    bPhysicalFile = false;  // Only the base table is considered physical
        if (bPhysicalFile)
            if (strFileType.indexOf("MAPPED") != -1)
                bPhysicalFile = false;  // Only the base table is considered physical
        return bPhysicalFile;
    }

}
