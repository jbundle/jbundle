/*
 *  @(#)PurgeRecordsProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.backup;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.util.log.BackupConstants;
import org.jbundle.base.db.util.log.FileList;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.DBConstants;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;
import org.jbundle.thin.base.remote.proxy.ProxyConstants;


/**
 *  RestoreBackupProcess - Restore records from the following files or directories.
 *  java -Xms256m -Xmx512m org.jbundle.base.db.util.log.RestoreBackupApp filename=ftp://download:donwpp@www.donandann.com/backup/trxlog7_11_09_11_38_27_PM_PDT.txt
 *  java -Xms256m -Xmx512m org.jbundle.base.db.util.log.RestoreBackupApp folder=ftp://download:donwpp@www.donandann.com/backup
 */
public class RestoreBackupProcess extends BaseProcess
    implements BackupConstants
{
    protected ObjectInputStream m_reader = null;

    /**
     * Default constructor.
     */
    public RestoreBackupProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public RestoreBackupProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
        return new ClassInfo(this);
    }
    /**
     * Open the other files.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
    }
    /**
     * Add this data to the backup stream.
     * @param strMessage the data to log.
     */
    public void run()
    {
        Object objMessage = null;
        String[] files = this.getFileList();
        if (files == null)
            return;
        
        for (String strFile : files)
        {
            m_reader = this.getReader(strFile);
        
            try {
                while (true)
                {
                    objMessage = m_reader.readObject();
                    if (objMessage == null)
                        break;  // EOF = Done
                    
                    BaseBuffer buffer = new VectorBuffer((Vector)objMessage, BaseBuffer.PHYSICAL_FIELDS | BaseBuffer.MODIFIED_ONLY);
                    buffer.setHeaderCount(3);
                    String strTrxType = buffer.getHeader().toString();
                    String strTableName = buffer.getHeader().toString();
                    String strKey = buffer.getHeader().toString();
                    
                    try {
                        Record record = this.getRecord(strTableName);
                        if (record == null)
                        {
                        	ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
                            recClassInfo.addNew();
                            recClassInfo.setKeyArea(ClassInfo.kClassNameKey);
                            recClassInfo.getField(ClassInfo.kClassName).setString(strTableName);
                            if (recClassInfo.seek(DBConstants.EQUALS))
                            {
                                strTableName = recClassInfo.getPackageName() + '.' + strTableName;
                                record = Record.makeRecordFromClassName(strTableName, this);
                                this.disableAllListeners(record);
                                record.setAutoSequence(false);
                            }
                            else
                            {
                                if (!NOTRX.equalsIgnoreCase(strTrxType))
                                    System.out.println("Error - table not found: " + strTableName);
                                continue;
                            }
                        }
                        record.addNew();
                        record.getCounterField().setString(strKey);
                        if (ProxyConstants.ADD.equalsIgnoreCase(strTrxType))
                        {
                            buffer.bufferToFields(record, DBConstants.DISPLAY, DBConstants.READ_MOVE);
                            record.add();
                        }
                        else if (ProxyConstants.SET.equalsIgnoreCase(strTrxType))
                        {
                            if (record.seek(DBConstants.EQUALS))
                            {
                                record.edit();
                                buffer.bufferToFields(record, DBConstants.DISPLAY, DBConstants.READ_MOVE);
                                record.set();
                            }
                            else
                                System.out.println("Error - record not found: " + strTableName + ", key: " + strKey);
                        }
                        else if (ProxyConstants.REMOVE.equalsIgnoreCase(strTrxType))
                        {
                            if (record.seek(DBConstants.EQUALS))
                            {
                                record.edit();
                                record.remove();
                            }
                            else
                                System.out.println("Error - record not found: " + strTableName + ", key: " + strKey);
                        }
                    } catch (DBException e) {
                        e.printStackTrace();
                        System.out.println("Error - record: " + strTableName + ", key: " + strKey + " trx type: " + strTrxType);
                    }
                    
                    System.out.println("trxType: " + strTrxType + " record: " + strTableName + " key: " + strKey);
                }
                m_reader.close();
                m_reader = null;
            
            } catch (EOFException e) {
                // Ok - Done
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Get the list of files to read thru and import.
     * @return
     */
    public String[] getFileList()
    {
        String[] files = null;
        String strFilename = this.getProperty("filename");
        if (strFilename != null)
        {
            files = new String[1];
            files[0] = strFilename;
        }
        String strDirname = this.getProperty("folder");
        if (strDirname != null)
        {
            FileList list = new FileList(strDirname);
            files = list.getFileNames();
            if (!strDirname.endsWith("/"))
                strDirname += "/";
            for (int i = 0; i < files.length; i++)
            {
                files[i] = strDirname + files[i];   // Full pathname
            }
        }
        return files;
    }
    /**
     * Get the backup stream writer.
     * @return
     */
    public ObjectInputStream getReader(String strFilename)
    {
        ObjectInputStream reader = null;
        try {
            InputStream is = null;
            if (strFilename.indexOf(':') == -1)
                is = new FileInputStream(strFilename);
            else
            {
                URL url = new URL(strFilename);
                URLConnection urlc = url.openConnection();
                is = urlc.getInputStream(); // To download
            }
            reader = new ObjectInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }
    /**
     * DisableAllListeners Method.
     */
    public void disableAllListeners(Record record)
    {
        record.setEnableListeners(false);   // Disable all file behaviors
        record.setEnableFieldListeners(false);
    }

}
