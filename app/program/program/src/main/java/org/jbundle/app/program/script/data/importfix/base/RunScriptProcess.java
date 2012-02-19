/**
 * @(#)RunScriptProcess.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.data.importfix.base;

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
import org.jbundle.base.thread.*;
import org.jbundle.app.program.script.db.*;
import java.io.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.core.trx.*;

/**
 *  RunScriptProcess - Transfer data from one table to another.
 */
public class RunScriptProcess extends BaseProcess
{
    public static final String CODE = "code";
    public static final String PARENT_RECORD = "parentRecord";
    public static final int DONT_READ_SUB_SCRIPT = 99;
    /**
     * Default constructor.
     */
    public RunScriptProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public RunScriptProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        return null;    // Don't auto-open, in case this is run stand-alone (to run script from properties)
    }
    /**
     * Get the main record for this screen.
     * @return The main record (or null if none).
     */
    public Record getMainRecord()
    {
        Record record = super.getMainRecord();
        if (record == null)
            record = new Script(this);
        return record;
    }
    /**
     * Lookup this record for this recordowner.
     * @param The record's name.
     * @return The record with this name (or null if not found).
     */
    public Record getRecord(String strFileName)
    {
        Record record = super.getRecord(strFileName);
        if (record == null)
            if (Script.SCRIPT_FILE.equalsIgnoreCase(strFileName))
                record = new Script(this);
        return record;
    }
    /**
     * Run Method.
     */
    public void run()
    {
        String strID = null;
        String strCode = this.getProperty(CODE);
        if ((strCode == null) || (strCode.length() == 0))
        {
            strID = this.getProperty(DBParams.ID);
            if ((strID == null) || (strID.length() == 0))
                return;
        }
        Script recScript = (Script)this.getMainRecord();
        if ((strCode == null) || (strCode.length() == 0))
        {
            recScript.setKeyArea(Script.CODE_KEY);
            recScript.getField(Script.CODE).setString(strCode);
        }
        else
        {
            recScript.setKeyArea(Script.ID_KEY);
            recScript.getField(Script.ID).setString(strID);
        }
        try {
            if (recScript.seek(null) == false)
                return;
            this.doCommand(recScript, null);
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * DoCommand Method.
     */
    public int doCommand(Script recScript, Map<String,Object> properties)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (recScript.getEditMode() != DBConstants.EDIT_CURRENT)
            return DBConstants.ERROR_RETURN;
        if (properties == null)
        {   // First time, climb the tree and set up the properties.
            properties = new Hashtable<String,Object>();
            Script recTempScript = new Script(this);
            try {
                int iParentScriptID = (int)recScript.getField(Script.PARENT_FOLDER_ID).getValue();
                recTempScript.getField(Script.ID).setValue(iParentScriptID);
                while ((iParentScriptID > 0) && (recTempScript.seek(null) == true))
                {
                    if (!recTempScript.getField(Script.PROPERTIES).isNull())
                    {   // Execute this script
                        Map<String,Object> propRecord = ((PropertiesField)recTempScript.getField(Script.PROPERTIES)).getProperties();
                        propRecord.putAll(properties);  // These properties override parent properties
                        properties = propRecord;
                    }
                    iParentScriptID = (int)recTempScript.getField(Script.PARENT_FOLDER_ID).getValue();
                    recTempScript.addNew();
                    recTempScript.getField(Script.ID).setValue(iParentScriptID);
                }
            } catch (DBException ex) {
                ex.printStackTrace();
            } finally {
                recTempScript.free();
                recTempScript = null;
            }
        }
        if (!recScript.getField(Script.PROPERTIES).isNull())
        {   // Execute this script
            Map<String,Object> propRecord = ((PropertiesField)recScript.getField(Script.PROPERTIES)).getProperties();
            properties = new Hashtable<String,Object>(properties);    // Create a copy, so you don't mess up the original
            properties.putAll(propRecord);
        }
        
        String strCommand = recScript.getField(Script.COMMAND).toString();
        if (Script.RUN.equalsIgnoreCase(strCommand))
            iErrorCode = this.doRunCommand(recScript, properties);
        else if (Script.RUN_REMOTE.equalsIgnoreCase(strCommand))
        {
            BaseMessage message = new MapMessage(new TrxMessageHeader(MessageConstants.TRX_SEND_QUEUE, MessageConstants.INTRANET_QUEUE, properties), properties);
            ((Application)this.getTask().getApplication()).getMessageManager().sendMessage(message);
        }
        else if (Script.SEEK.equalsIgnoreCase(strCommand))
            iErrorCode = this.doSeekCommand(recScript, properties);
        else if (Script.COPY_RECORDS.equalsIgnoreCase(strCommand))
            iErrorCode = this.doCopyRecordsCommand(recScript, properties);
        else if (Script.COPY_FIELDS.equalsIgnoreCase(strCommand))
            iErrorCode = this.doCopyFieldsCommand(recScript, properties);
        else if (Script.COPY_DATA.equalsIgnoreCase(strCommand))
            iErrorCode = this.doCopyDataCommand(recScript, properties);
        if (iErrorCode != DONT_READ_SUB_SCRIPT)
            if (iErrorCode != DBConstants.NORMAL_RETURN)
                return iErrorCode;
        
        if (iErrorCode != DONT_READ_SUB_SCRIPT)
            iErrorCode = this.doSubScriptCommands(recScript, properties);
        else
            iErrorCode = DBConstants.NORMAL_RETURN;
        
        return iErrorCode;
    }
    /**
     * DoSubScriptCommands Method.
     */
    public int doSubScriptCommands(Script recScript, Map<String,Object> properties)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        Script recSubScript = recScript.getSubScript();
        try {
            recSubScript.close();
            while (recSubScript.hasNext())
            {
                recSubScript.next();
                iErrorCode = this.doCommand(recSubScript, properties);
                if (iErrorCode != DBConstants.NORMAL_RETURN)
                    return iErrorCode;
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
        return iErrorCode;
    }
    /**
     * DoRunCommand Method.
     */
    public int doRunCommand(Script recScript, Map<String,Object> properties)
    {
        ProcessRunnerTask processRunner = new ProcessRunnerTask(this.getTask().getApplication(), null, null);
        processRunner.setProperties(properties);
        processRunner.run();    // Run and free when you are done
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * DoSeekCommand Method.
     */
    public int doSeekCommand(Script recScript, Map<String,Object> properties)
    {
        Record record = recScript.getTargetRecord(properties, DBParams.RECORD);
        if (record == null)
            return DBConstants.ERROR_RETURN;
        for (int iKeySeq = 0; iKeySeq < record.getKeyAreaCount(); iKeySeq++)
        {
            String strKeyFieldName = record.getKeyArea(iKeySeq).getKeyField(0).getField(DBConstants.FILE_KEY_AREA).getFieldName(false, false);
            if (recScript.getProperty(strKeyFieldName) != null)
            {
                record.setKeyArea(iKeySeq);
                record.getField(strKeyFieldName).setString(recScript.getProperty(strKeyFieldName));
                try {
                    if (record.seek(null))
                        return DBConstants.NORMAL_RETURN;
                } catch (DBException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return DBConstants.ERROR_RETURN;
    }
    /**
     * DoCopyRecordsCommand Method.
     */
    public int doCopyRecordsCommand(Script recScript, Map<String,Object> properties)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        Record record = recScript.getTargetRecord(properties, DBParams.RECORD);
        if (record == null)
            return DBConstants.ERROR_RETURN;
        if (properties.get(PARENT_RECORD) != null)
        {
            Record recParent = recScript.getTargetRecord(properties, PARENT_RECORD);
            if (recParent != null)
                record.addListener(new SubFileFilter(recParent));
        }
        Record recDestination = recScript.getTargetRecord(properties, Script.DESTINATION_RECORD);
        try {
            record.close();
            while (record.hasNext())
            {
                record.next();
                recDestination.addNew();
                recDestination.setAutoSequence(false);
                iErrorCode = this.doSubScriptCommands(recScript, properties);
                if (iErrorCode != DBConstants.NORMAL_RETURN)
                    return iErrorCode;
                if (recDestination.getCounterField().isNull())
                    recDestination.setAutoSequence(true);
                try {
                    recDestination.add();
                } catch (DBException ex) {
                    // Ignore duplicate records
                }
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
        return DONT_READ_SUB_SCRIPT;
    }
    /**
     * DoCopyFieldsCommand Method.
     */
    public int doCopyFieldsCommand(Script recScript, Map<String,Object> properties)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        Record recSource = recScript.getTargetRecord(properties, DBParams.RECORD);
        if (recSource == null)
            return DBConstants.ERROR_RETURN;
        Record recDestination = recScript.getTargetRecord(properties, Script.DESTINATION_RECORD);
        if (recDestination == null)
            return DBConstants.ERROR_RETURN;
        String strSourceField = (String)properties.get(Script.SOURCE_PARAM);
        String strDestField = (String)properties.get(Script.DESTINATION_PARAM);
        if (strDestField == null)
            strDestField = strSourceField;
        if (strSourceField == null)
            return DBConstants.ERROR_RETURN;
        BaseField fldSource = recSource.getField(strSourceField);
        BaseField fldDest = recDestination.getField(strDestField);
        if ((fldSource == null) || (fldDest == null))
            return DBConstants.ERROR_RETURN;
        iErrorCode = fldDest.moveFieldToThis(fldSource);
        return iErrorCode;
    }
    /**
     * DoCopyFilesCommand Method.
     */
    public int doCopyFilesCommand(Script recScript, Map<String,Object> properties)
    {
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * DoCopyDataCommand Method.
     */
    public int doCopyDataCommand(Script recScript, Map<String,Object> properties)
    {
        String strURL = (String)properties.get(Script.SOURCE_PARAM);
        String strDest = (String)properties.get(Script.DESTINATION_PARAM);
        if ((strURL != null) && (strDest != null))
            Utility.transferURLStream(strURL, strDest);
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * RunDetail Method.
     */
    public void runDetail()
    {
        Record recReplication = this.getMainRecord();
        if ((recReplication.getEditMode() == DBConstants.EDIT_NONE)
            || (recReplication.getEditMode() == DBConstants.EDIT_ADD))
                recReplication.getField(Script.ID).setValue(0);
        String strSourcePath = "";
        String strDestPath = "";
        this.processDetail(recReplication, strSourcePath, strDestPath);
    }
    /**
     * ProcessDetail Method.
     */
    public boolean processDetail(Record parent, String strSourcePath, String strDestPath)
    {
        boolean bSubsExist = false;
        String strName;
        Script recReplication = new Script(this);
        recReplication.setKeyArea(Script.PARENT_FOLDER_ID_KEY);
        recReplication.addListener(new SubFileFilter(parent));
        try   {
            strName = parent.getField(Script.NAME).toString();
                    while (recReplication.hasNext())
            { // Read through the pictures and create an index
                recReplication.next();
                bSubsExist = true;
                strName = recReplication.getField(Script.NAME).toString();
                String strSource = recReplication.getField(Script.SOURCE_PARAM).toString();
                String strDestination = recReplication.getField(Script.DESTINATION_PARAM).toString();
                strSource = strSourcePath + strSource;
                strDestination = strDestPath + strDestination;
                this.processDetail(recReplication, strSource, strDestination);
            }
            recReplication.close();
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        if (strSourcePath.length() > 0)
            if (Character.isLetterOrDigit(strSourcePath.charAt(strSourcePath.length() - 1)))
        {
            System.out.println("From: " + strSourcePath + " To: " + strDestPath);
            File fileSource = new File(strSourcePath);
            File fileDest = new File(strDestPath);
            if (fileSource.exists())
            {
                if (fileDest.exists())
                    fileDest.delete();
                else
                    System.out.println("Target doesn't exist: " + strSourcePath);
                try   {
                    FileInputStream inStream = new FileInputStream(fileSource);
                    FileOutputStream outStream = new FileOutputStream(fileDest);
                    org.jbundle.jbackup.util.Util.copyStream(inStream, outStream);
                } catch (FileNotFoundException ex)  {
                    ex.printStackTrace();
                } catch (IOException ex)    {
                    ex.printStackTrace();
                }
            }
        }
        return bSubsExist;
    }

}
