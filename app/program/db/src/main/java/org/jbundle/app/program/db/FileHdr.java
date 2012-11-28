/**
 * @(#)FileHdr.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.app.program.db.*;

/**
 *  FileHdr - File Information Record.
 */
public class FileHdr extends VirtualRecord
     implements FileHdrModel
{
    private static final long serialVersionUID = 1L;

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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(FILE_HDR_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(FILE_HDR_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
        {
            field = new StringField(this, FILE_NAME, 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == 4)
            field = new StringField(this, FILE_DESC, 40, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, FILE_MAIN_FILENAME, 40, null, null);
        if (iFieldSeq == 6)
            field = new FileTypeField(this, TYPE, 60, null, null);
        if (iFieldSeq == 7)
            field = new MemoField(this, FILE_NOTES, 9999, null, null);
        if (iFieldSeq == 8)
            field = new StringField(this, DATABASE_NAME, 20, null, null);
        if (iFieldSeq == 9)
            field = new StringField(this, FILE_REC_CALLED, 40, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, DISPLAY_CLASS, 40, null, null);
        if (iFieldSeq == 11)
            field = new StringField(this, MAINT_CLASS, 40, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, ID_KEY);
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, FILE_NAME_KEY);
            keyArea.addKeyField(FILE_NAME, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * Is this the physical file that can be imported/exported, etc?.
     */
    public boolean isPhysicalFile()
    {
        String strFileType = this.getField(FileHdr.TYPE).toString();
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
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        this.getField(FileHdr.FILE_NAME).addListener(new MoveOnChangeHandler(this.getField(FileHdr.FILE_MAIN_FILENAME), null, false, true));
    }

}
