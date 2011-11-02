/**
 * @(#)ImportXMLScanListener.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.scan;

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
import org.jbundle.base.thread.*;
import java.io.*;
import org.jbundle.main.db.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.util.osgi.finder.*;
import org.jbundle.base.db.xmlutil.*;

/**
 *  ImportXMLScanListener - ImportXMLScanListener - Import XML from directories to new tables..
 */
public class ImportXMLScanListener extends BaseScanListener
{
    protected XmlInOut inout = null;
    /**
     * Default constructor.
     */
    public ImportXMLScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ImportXMLScanListener(RecordOwnerParent parent, String strSourcePrefix)
    {
        this();
        this.init(parent, strSourcePrefix);
    }
    /**
     * Init Method.
     */
    public void init(RecordOwnerParent parent, String strSourcePrefix)
    {
        super.init(parent, strSourcePrefix);
        inout = new XmlInOut((RecordOwner)parent, null, null);
    }
    /**
     * Main Method.
     */
    public static void main(String[] args)
    {
        if ((args == null) || (args.length < 1))
            System.exit(0);
        String strDirIn = args[0];
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(ConvertCode.SOURCE_DIR, strDirIn);
        properties.put(DBParams.TABLE, DBParams.JDBC);
        properties.put(DBParams.LOCAL, DBParams.JDBC);
        properties.put(DBParams.REMOTE, DBParams.JDBC);
        Environment env = new Environment(properties);
        MainApplication app = new MainApplication(env, properties, null);
        ProcessRunnerTask task = new ProcessRunnerTask(app, null, properties);
        ConvertCode convert = new ConvertCode(task, null, properties);
        convert.setScanListener(new ImportXMLScanListener(convert, null));
        convert.run();
    }
    /**
     * If this file should be processed, return true.
     */
    public boolean filterFile(File file)
    {
        String strName = file.getName();
        if (!strName.endsWith(".xml"))
            return false;
        return super.filterFile(file);
    }
    /**
     * Do whatever processing that needs to be done on this file.
     */
    public void moveThisFile(File fileSource, File fileDestDir, String strDestName)
    {
        // Step 1 - Find the class name for this file:
        String className = fileSource.getPath().replace('/', '.');
        if (className.endsWith(".xml"))
            className = className.substring(0, className.length() - 4);
        boolean classFound = false;
        Record record = null;
        String databaseName = null;
        Map<String,String> oldProperties = new HashMap<String,String>();
        
        if (className.endsWith("DatabaseInfo"))
        {
            record = new DatabaseInfo();
            databaseName = this.getDatabaseInfoDatabaseName(className);
            ((DatabaseInfo)record).setDatabaseName(databaseName);
            if (databaseName.indexOf('_') != -1)
            {
                String tableName = "DatabaseInfo_" + databaseName.substring(0, databaseName.indexOf('_'));
                record.setTableNames(tableName);
            }
            record.init(this.m_parent);
            this.saveOldProperties(oldProperties, record);
        }
        else
        {
            while (!classFound)
            {
               record = (Record)ClassServiceUtility.getClassService().makeObjectFromClassName(className);
               if (record != null)
               {
                    record.init(this.m_parent);
                    classFound = true;
               }
               else
               {
                   if (className.indexOf('.') == -1)
                   {
                       System.out.println("Class not found: " + fileSource.toString());
                       return;
                   }
                   databaseName = className.substring(0, className.indexOf('.'));
                   className = className.substring(className.indexOf('.') + 1);
                }
            }
            this.saveOldProperties(oldProperties, record);
            databaseName = this.fixDatabaseName(databaseName, record, oldProperties);
        }
        
        String recordDBName = record.getDatabaseName();
        System.out.println("Process import: " + className + " (" + databaseName + ") to " + record.getRecordName() + " (" + recordDBName + ")");
        if (!inout.importXML(record.getTable(), fileSource.getPath(), null))
            System.exit(1);
        
        this.restoreOldProperties(oldProperties, record);
        record.free();
    }
    /**
     * Clean up the database name;
     * @param databaseName
     * @return.
     */
    private String fixDatabaseName(String databaseName, Record record, Map<String,String> oldProperties)
    {
        if (databaseName.endsWith(BaseDatabase.SHARED_SUFFIX))
            databaseName = databaseName.substring(0, databaseName.length() - BaseDatabase.SHARED_SUFFIX.length());
        else if (databaseName.endsWith(BaseDatabase.USER_SUFFIX))
            databaseName = databaseName.substring(0, databaseName.length() - BaseDatabase.USER_SUFFIX.length());
        String recordDBName = record.getDatabaseName();
        if (record instanceof DatabaseInfo)
            recordDBName = "DatabaseInfo";
        if (!databaseName.startsWith(recordDBName))
        { // Typically user name
            this.getTask().setProperty(DBConstants.DB_USER_PREFIX, databaseName.substring(0, databaseName.indexOf('_')));
            databaseName = databaseName.substring(databaseName.indexOf('_') + 1);
        }
        if (!databaseName.endsWith(recordDBName))
        {
            String suffix = databaseName.substring(databaseName.lastIndexOf('_') + 1);
            if (suffix.length() == 2)
            { // Typically language
                this.getTask().setProperty(DBParams.LANGUAGE, suffix);
                databaseName = databaseName.substring(0, databaseName.lastIndexOf('_'));
                suffix = databaseName.substring(databaseName.lastIndexOf('_') + 1);             
            }
            if (!databaseName.endsWith(recordDBName))
            {
                databaseName = databaseName.substring(0, databaseName.lastIndexOf('_'));
                if ((record.getDatabaseType() & DBConstants.USER_DATA) != 0)
                    this.getTask().setProperty(record.getDatabaseName() + BaseDatabase.DBUSER_PARAM_SUFFIX, suffix);
                else
                    this.getTask().setProperty(record.getDatabaseName() + BaseDatabase.DBSHARED_PARAM_SUFFIX, suffix);
                suffix = databaseName.substring(databaseName.lastIndexOf('_') + 1); //?         
            }
            if (!databaseName.endsWith(recordDBName))
            {
                this.getTask().setProperty(DBConstants.SUB_SYSTEM_LN_SUFFIX, suffix);
                databaseName = databaseName.substring(0, databaseName.lastIndexOf('_'));
            }
        }
        return databaseName;
    }
    /**
     * GetDatabaseInfoDatabaseName Method.
     */
    private String getDatabaseInfoDatabaseName(String className)
    {
        String databaseName = className.substring(0, className.length() - 1 - "DatabaseInfo".length());
        databaseName = databaseName.substring(databaseName.lastIndexOf('.') + 1);
        if (databaseName.endsWith(BaseDatabase.SHARED_SUFFIX))
            databaseName = databaseName.substring(0, databaseName.length() - BaseDatabase.SHARED_SUFFIX.length());
        if (databaseName.endsWith(BaseDatabase.USER_SUFFIX))
            databaseName = databaseName.substring(0, databaseName.length() - BaseDatabase.USER_SUFFIX.length());
        return databaseName;
    }
    /**
     * SaveOldProperties Method.
     */
    public void saveOldProperties(Map<String,String> oldProperties, Record record)
    {
        this.saveOldProperty(oldProperties, record.getDatabaseName() + BaseDatabase.DBSHARED_PARAM_SUFFIX);
        this.saveOldProperty(oldProperties, record.getDatabaseName() + BaseDatabase.DBUSER_PARAM_SUFFIX);
        this.saveOldProperty(oldProperties, DBConstants.DB_USER_PREFIX);
        this.saveOldProperty(oldProperties, DBConstants.SUB_SYSTEM_LN_SUFFIX);
        this.saveOldProperty(oldProperties, DBParams.LANGUAGE);
    }
    /**
     * SaveOldProperty Method.
     */
    public void saveOldProperty(Map<String,String> oldProperties, String param)
    {
        oldProperties.put(param, this.getTask().getProperty(param));
    }
    /**
     * RestoreOldProperties Method.
     */
    public void restoreOldProperties(Map<String,String> oldProperties, Record record)
    {
        this.restoreOldProperty(oldProperties, record.getDatabaseName() + BaseDatabase.DBSHARED_PARAM_SUFFIX);
        this.restoreOldProperty(oldProperties, record.getDatabaseName() + BaseDatabase.DBUSER_PARAM_SUFFIX);
        this.restoreOldProperty(oldProperties, DBConstants.DB_USER_PREFIX);
        this.restoreOldProperty(oldProperties, DBConstants.SUB_SYSTEM_LN_SUFFIX);
        this.restoreOldProperty(oldProperties, DBParams.LANGUAGE);
    }
    /**
     * RestoreOldProperty Method.
     */
    private void restoreOldProperty(Map<String,String> oldProperties, String param)
    {
        this.getTask().setProperty(param, oldProperties.get(param));
    }

}
