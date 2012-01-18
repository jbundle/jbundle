/**
 * @(#)Script.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.db;

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
import org.jbundle.app.program.script.screen.*;
import org.jbundle.app.program.script.data.importfix.base.*;
import org.jbundle.app.program.db.*;
import org.jbundle.util.osgi.finder.*;
import org.jbundle.model.app.program.script.db.*;

/**
 *  Script - Script maintenance.
 */
public class Script extends Folder
     implements ScriptModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kName = kName;
    //public static final int kParentFolderID = kParentFolderID;
    //public static final int kSequence = kSequence;
    //public static final int kComment = kComment;
    //public static final int kCode = kCode;
    public static final int kProperties = kFolderLastField + 1;
    public static final int kCommand = kProperties + 1;
    public static final int kSource = kCommand + 1;
    public static final int kDestination = kSource + 1;
    public static final int kScriptLastField = kDestination;
    public static final int kScriptFields = kDestination - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kParentFolderIDKey = kIDKey + 1;
    public static final int kCodeKey = kParentFolderIDKey + 1;
    public static final int kScriptLastKey = kCodeKey;
    public static final int kScriptKeys = kCodeKey - DBConstants.MAIN_KEY_FIELD + 1;
    protected Script m_recSubScript = null;
    /**
     * Default constructor.
     */
    public Script()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Script(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        m_recSubScript = null;
        super.init(screen);
    }

    public static final String kScriptFile = "Script";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kScriptFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            screen = new ScriptGridScreen(this, null, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = new ScriptScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new ScriptGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MENU_MODE) != 0)
            screen = new ScriptScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        //if (iFieldSeq == kName)
        //  field = new StringField(this, "Name", 40, null, null);
        //if (iFieldSeq == kParentFolderID)
        //  field = new FolderField(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kSequence)
        //  field = new ShortField(this, "Sequence", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kComment)
        //  field = new MemoField(this, "Comment", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kCode)
        //  field = new StringField(this, "Code", 30, null, null);
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kCommand)
            field = new ScriptCommandField(this, "Command", Constants.DEFAULT_FIELD_LENGTH, null, "true");
        if (iFieldSeq == kSource)
        {
            field = new StringField(this, "Source", 128, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kDestination)
        {
            field = new StringField(this, "Destination", 128, null, null);
            field.setVirtual(true);
        }
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kScriptLastField)
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
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Code");
            keyArea.addKeyField(kCode, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kScriptLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kScriptLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Free Method.
     */
    public void free()
    {
        if (m_recSubScript != null)
            m_recSubScript.free();
        super.free();
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        PropertiesField fldProperties = (PropertiesField)this.getField(Script.kProperties);
        fldProperties.addPropertiesFieldBehavior(this.getField(Script.kSource), SOURCE);
        fldProperties.addPropertiesFieldBehavior(this.getField(Script.kDestination), DESTINATION);
    }
    /**
     * Execute Method.
     */
    public boolean execute(Properties properties)
    {
        if (this.getEditMode() == DBConstants.EDIT_IN_PROGRESS)
        {
            try {
                this.writeAndRefresh();
            } catch (DBException ex) {
                ex.printStackTrace();
            }
        }
        if (this.getEditMode() != DBConstants.EDIT_CURRENT)
            return false;
        RunScriptProcess process = new RunScriptProcess(this.getTask(), null, null);
        boolean bSuccess = (process.doCommand(this, null) == DBConstants.NORMAL_RETURN);
        process.free();
        return bSuccess;
    }
    /**
     * GetTargetRecord Method.
     */
    public Record getTargetRecord(Map<String,Object> properties, String strParam)
    {
        String strRecordName = (String)properties.get(strParam);
        Record record = null;
        if ((strRecordName != null) && (strRecordName.length() > 0))
        {
            String strTableName = strRecordName;
            if (strTableName.indexOf('.') != -1)
                strTableName = strTableName.substring(strTableName.lastIndexOf('.') + 1);
            if (this.getRecordOwner() != null)  // Always
                record = this.getRecordOwner().getRecord(strTableName);
            if (record != null)
                return record;    // Already open
            if (strRecordName.indexOf('.') == -1)
                if (properties.get("package") != null)
                    strRecordName = (String)properties.get("package") + '.' + strRecordName;
            if (strRecordName.indexOf('.') == -1)
            {
                ClassInfo recClassInfo = new ClassInfo(this.getRecordOwner());
                try {
                    recClassInfo.getField(ClassInfo.kClassName).setString(strRecordName);
                    recClassInfo.setKeyArea(ClassInfo.kClassNameKey);
                    if (recClassInfo.seek(null))
                        strRecordName = recClassInfo.getPackageName() + '.' + strRecordName;
                } catch (DBException ex) {
                    ex.printStackTrace();
                } finally {
                    recClassInfo.free();
                }
            }
            if (strRecordName.indexOf('.') != -1)
            {
               record = (Record)ClassServiceUtility.getClassService().makeObjectFromClassName(strRecordName);
                if (record != null)
                    record.init(Utility.getRecordOwner(this));
            }
        }
        return record;
    }
    /**
     * GetProperty Method.
     */
    public String getProperty(String strKey)
    {
        return ((PropertiesField)this.getField(Script.kProperties)).getProperty(strKey);
    }
    /**
     * Create a record to read through this script's children.
     */
    public Script getSubScript()
    {
        if (m_recSubScript == null)
        {
            RecordOwner recordOwner = Utility.getRecordOwner(this);
            m_recSubScript = new Script(recordOwner);
            recordOwner.removeRecord(m_recSubScript);
            m_recSubScript.addListener(new SubFileFilter(this));
        }
        return m_recSubScript;
    }

}
