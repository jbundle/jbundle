/**
 * @(#)Script.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.db;

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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(SCRIPT_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            screen = Record.makeNewScreen(SCRIPT_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(SCRIPT_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = Record.makeNewScreen(SCRIPT_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MENU_MODE) != 0)
            screen = Record.makeNewScreen(SCRIPT_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        //if (iFieldSeq == 3)
        //  field = new StringField(this, NAME, 40, null, null);
        //if (iFieldSeq == 4)
        //  field = new FolderField(this, PARENT_FOLDER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 5)
        //  field = new ShortField(this, SEQUENCE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 6)
        //  field = new MemoField(this, COMMENT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 7)
        //  field = new StringField(this, CODE, 30, null, null);
        if (iFieldSeq == 8)
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 9)
            field = new ScriptCommandField(this, COMMAND, Constants.DEFAULT_FIELD_LENGTH, null, "true");
        if (iFieldSeq == 10)
        {
            field = new StringField(this, SOURCE, 128, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 11)
        {
            field = new StringField(this, DESTINATION, 128, null, null);
            field.setVirtual(true);
        }
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
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, PARENT_FOLDER_ID_KEY);
            keyArea.addKeyField(PARENT_FOLDER_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(SEQUENCE, DBConstants.ASCENDING);
            keyArea.addKeyField(NAME, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, CODE_KEY);
            keyArea.addKeyField(CODE, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
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
        PropertiesField fldProperties = (PropertiesField)this.getField(Script.PROPERTIES);
        fldProperties.addPropertiesFieldBehavior(this.getField(Script.SOURCE_PARAM), SOURCE_PARAM);
        fldProperties.addPropertiesFieldBehavior(this.getField(Script.DESTINATION_PARAM), DESTINATION_PARAM);
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
                record = (Record)this.getRecordOwner().getRecord(strTableName);
            if (record != null)
                return record;    // Already open
            if (strRecordName.indexOf('.') == -1)
                if (properties.get("package") != null)
                    strRecordName = (String)properties.get("package") + '.' + strRecordName;
            if (strRecordName.indexOf('.') == -1)
            {
                ClassInfo recClassInfo = new ClassInfo(this.getRecordOwner());
                try {
                    recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strRecordName);
                    recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
                    if (recClassInfo.seek(null))
                        strRecordName = recClassInfo.getPackageName(null) + '.' + strRecordName;
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
                    record.init(this.findRecordOwner());
            }
        }
        return record;
    }
    /**
     * GetProperty Method.
     */
    public String getProperty(String strKey)
    {
        return ((PropertiesField)this.getField(Script.PROPERTIES)).getProperty(strKey);
    }
    /**
     * Create a record to read through this script's children.
     */
    public Script getSubScript()
    {
        if (m_recSubScript == null)
        {
            RecordOwner recordOwner = this.findRecordOwner();
            m_recSubScript = new Script(recordOwner);
            recordOwner.removeRecord(m_recSubScript);
            m_recSubScript.addListener(new SubFileFilter(this));
        }
        return m_recSubScript;
    }

}
