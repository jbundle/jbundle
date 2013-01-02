/**
 * @(#)BaseProcessRecords.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.process;

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
import org.jbundle.base.thread.*;
import org.jbundle.app.program.db.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.main.db.*;

/**
 *  BaseProcessRecords - .
 */
public class BaseProcessRecords extends BaseProcess
{
    protected Map<String,String> classProjectPackages = null;
    protected Map<String,String> classProjectNames = null;
    protected Map<String,String> classProjectIDs = null;
    /**
     * Default constructor.
     */
    public BaseProcessRecords()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseProcessRecords(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
        return new FileHdr(this);
    }
    /**
     * Open the other files.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new ClassInfo(this);
        new ClassProject(this);
    }
    /**
     *  Run Method.
     *  Process the files (import/export)
     *  Params:
     *   - archiveFolder - Target dir
     *     or
     *   - sourcePrefix + sourceDir = Target dir
     *   Database mods (standard):
     *   - dbPrefix - User file prefix
     *   - systemname - User file suffix
     *   - locale - Localization filter (language)
     *  Filters:
     *    - package - Package regex
     *    - type - File type (SHARED, USER, or null is ALL)
     *    - project - Project name regex or ID.
     */
    public void run()
    {
        String packageName = this.getProperty("package");
        ClassProject classProject = (ClassProject)this.getRecord(ClassProject.CLASS_PROJECT_FILE);
        if (packageName != null) if (packageName.length() > 0)
        {
            String projectID = this.getProperty("project");
            if (projectID != null) if (projectID.length() > 0)
            { // Usually
                classProject.getField(ClassProject.ID).setString(projectID);
                try {
                    if (classProject.seek(DBConstants.EQUALS))
                        packageName = classProject.getFullPackage(ClassProject.CodeType.THICK, packageName);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        }
        // Note: I need to scan the classproject file now, because I will be turning on base table access in a second
        try {
            classProjectPackages = new HashMap<String,String>();
            classProjectNames = new HashMap<String,String>();
            classProjectIDs = new HashMap<String,String>();
            classProject.close();
            while (classProject.hasNext())
            {
                classProject.next();
                String classPackageName = classProject.getFullPackage(ClassProject.CodeType.THICK, DBConstants.BLANK);
                classProjectPackages.put(classProject.getField(ClassProject.ID).toString(), classPackageName);
                classProjectNames.put(classProject.getField(ClassProject.NAME).toString(), classProject.getField(ClassProject.ID).toString());
                classProjectIDs.put(classProject.getField(ClassProject.ID).toString(), classProject.getField(ClassProject.NAME).toString());
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        
        // String archivePrefix = this.getProperty(ConvertCode.DIR_PREFIX); // This is added automatically in getArchiveFilename
        String archiveFolder = this.getProperty(DBConstants.ARCHIVE_FOLDER);
        if (archiveFolder == null)
            archiveFolder = this.getProperty(ConvertCode.SOURCE_DIR);
        
        String strPrefix = this.getProperty(DBConstants.DB_USER_PREFIX);
        String strSuffix = this.getProperty(DBConstants.SYSTEM_NAME);
        String strLocale = this.getProperty("locale");  // DBParams.LANGUAGE);  // Don't use language, must specify locale
        
        Map<String,Object> appProperties = new HashMap<String,Object>();
        appProperties.putAll(this.getTask().getApplication().getProperties());
        
        if (archiveFolder != null)
            this.getTask().getApplication().setProperty(DBConstants.ARCHIVE_FOLDER, archiveFolder);
        if (strPrefix != null)
            this.getTask().getApplication().setProperty(DBConstants.DB_USER_PREFIX, strPrefix);
        if (strSuffix != null)
            this.getTask().getApplication().setProperty(DBConstants.SYSTEM_NAME, strSuffix);
        if (strLocale != null)
            this.getTask().getApplication().setProperty(DBParams.LANGUAGE, strLocale);
        for (String key : this.getProperties().keySet())
        {
            if (key.endsWith(BaseDatabase.DBSHARED_PARAM_SUFFIX))
                this.getTask().getApplication().setProperty(key, this.getProperty(key));
            if (key.endsWith(BaseDatabase.DBUSER_PARAM_SUFFIX))
                this.getTask().getApplication().setProperty(key, this.getProperty(key));
        }
        // Base table access only
        String oldBaseTableProperty = this.getTask().getApplication().getProperty(DBConstants.BASE_TABLE_ONLY);
        this.getTask().getApplication().setProperty(DBConstants.BASE_TABLE_ONLY, DBConstants.TRUE);
        
        String strRecord = this.getProperty(DBParams.RECORD);
        if ((strRecord == null) || (strRecord.length() == 0))
            this.processAllRecords(packageName);
        else
            this.processThisRecord(this.getThisRecord(strRecord, packageName, null));        // Import the data
        // Restore properties
        this.getTask().getApplication().setProperties(appProperties);
        this.getTask().getApplication().setProperty(DBConstants.BASE_TABLE_ONLY, oldBaseTableProperty);   // Make sure system record owner property is reset also
    }
    /**
     * Process this record.
     */
    public boolean processThisRecord(Record record)
    {
        return false; // Override this!
    }
    /**
     * Export/import/process the records in this record class.
     */
    public void processAllRecords(String strPackage)
    {
        Map<String,String> mapDatabaseList = new HashMap<String,String>();
        FileHdr recFileHdr = (FileHdr)this.getMainRecord();
        recFileHdr.setKeyArea(FileHdr.FILE_NAME_KEY);
        recFileHdr.close();
        try   {
            Record recClassInfo = this.getRecord(ClassInfo.CLASS_INFO_FILE);
            while (recFileHdr.hasNext())
            {
                recFileHdr.next();
                String strRecord = recFileHdr.getField(FileHdr.FILE_NAME).toString();
                if ((strRecord == null) || (strRecord.length() == 0))
                    continue;
                if (!recFileHdr.isPhysicalFile())
                    continue;
                //xif (recFileHdr.getField(FileHdr.DATABASE_NAME).isNull())
                //x    continue;
                if (DatabaseInfo.DATABASE_INFO_FILE.equalsIgnoreCase(recFileHdr.getField(FileHdr.DATABASE_NAME).toString()))
                    continue;
        
                recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
                recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strRecord);
                if (recClassInfo.seek(null))
                {                    
                    String strClassPackage = this.getFullPackageName(recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID).toString(), recClassInfo.getField(ClassInfo.CLASS_PACKAGE).toString());
                    if (this.includeRecord(recFileHdr, recClassInfo, strPackage))
                            {
                               Record record = this.getThisRecord(strRecord, strClassPackage, null);
                                boolean success = this.processThisRecord(record);                        
                                if (success)
                                {
                                       String databaseName = record.getTable().getDatabase().getDatabaseName(true);
                                       if (databaseName.lastIndexOf('_') != -1)        // always
                                               databaseName = databaseName.substring(0, databaseName.lastIndexOf('_'));
                                       mapDatabaseList.put(databaseName, strClassPackage);
                                }
                            }
                }
            }
            // Now export any control records
            recFileHdr.close();
            while (recFileHdr.hasNext())
            {
                recFileHdr.next();
                String strRecord = recFileHdr.getField(FileHdr.FILE_NAME).toString();
                if ((strRecord == null) || (strRecord.length() == 0))
                    continue;
                if (!recFileHdr.isPhysicalFile())
                    continue;
                if (!recFileHdr.getField(FileHdr.DATABASE_NAME).isNull())
                    continue;
        
                recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
                recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strRecord);
                if (recClassInfo.seek(null))
                {
                    if (recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString().equalsIgnoreCase("QueryRecord"))
                        continue;
                    if (recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString().indexOf("Query") != -1)
                        continue;
                    String strClassPackage = this.getFullPackageName(recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID).toString(), recClassInfo.getField(ClassInfo.CLASS_PACKAGE).toString());
                    if (strPackage != null)
                        if (strClassPackage != null)
                            if (!strClassPackage.matches(strPackage))
                                continue;
                    if (!DatabaseInfo.DATABASE_INFO_FILE.equalsIgnoreCase(strRecord))
                        continue;   // Hack
                    if ("USER_DATA".equalsIgnoreCase(this.getProperty("type")))
                       continue;       // User data doesn't have database info
                    String databaseName = this.getProperty("database");
                    strRecord = strClassPackage + "." + strRecord;
                    for (String strDBName : mapDatabaseList.keySet())
                    {
                        String strClassPkg = mapDatabaseList.get(strDBName);
                        if (strClassPkg != null)
                        {
                            Record record = this.getThisRecord(strRecord, strClassPkg, strDBName);
                            if (record != null)
                            {
                                if (databaseName != null)
                                       if (!strDBName.startsWith(databaseName))
                                               continue;
                                 this.processThisRecord(record);
                            }
                        }
                    }
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return;
    }
    /**
     * Get this record.
     */
    public Record getThisRecord(String strRecord, String strPackage, String strDBName)
    {
        String strRecordClass = strRecord;
        if (!strRecordClass.contains("."))
            strRecordClass = strPackage + '.' + strRecordClass;
        Record record = Record.makeRecordFromClassName(strRecordClass, this, false, true);
        if (record == null)
            return null;
        
        String strMode = this.getProperty(ExportRecordsToXmlProcess.TRANSFER_MODE);
        boolean bExport = false;
        if (strMode != null) if (strMode.equalsIgnoreCase(ExportRecordsToXmlProcess.EXPORT))
            bExport = true;
        
        if (bExport)
            record.setOpenMode(record.getOpenMode() | DBConstants.OPEN_DONT_CREATE);
        
        //if (this.getProperty(ExportRecordsToXmlProcess.LOCALE) != null)
        //    if (this.getRecord(FileHdr.FILE_HDR_FILE).getEditMode() == DBConstants.EDIT_CURRENT)
        //        strDBName = this.getRecord(FileHdr.FILE_HDR_FILE).getField(FileHdr.DATABASE_NAME).toString() + '_' + this.getProperty("locale");
        if (strDBName != null)
            if (strPackage != null)
                if (!strRecordClass.matches(strPackage))
                    if (record instanceof DatabaseInfo) // Always
        {
            ((DatabaseInfo)record).setDatabaseName(strDBName);
            record.setOpenMode(record.getOpenMode() | DBConstants.OPEN_DONT_CREATE);
        }
        record.init(this);
        
        return record;
    }
    /**
     * Include this record?.
     */
    public boolean includeRecord(Record recFileHdr, Record recClassInfo, String strPackage)
    {
        String strProject = this.getProperty("project");
        String strType = this.getProperty("type");
        String[] classLists = null;
        String classList = this.getProperty("classList");
        if (classList != null)
            classLists = classList.split(",");
        String database = this.getProperty("database");
        
        if (recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString().equalsIgnoreCase("QueryRecord"))
            return false;
        if (recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString().indexOf("Query") != -1)
            return false;
        
        String strClassPackage = this.getFullPackageName(recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID).toString(), recClassInfo.getField(ClassInfo.CLASS_PACKAGE).toString());
        String strClassProject = classProjectIDs.get(recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID).toString());
        String strClassType = recFileHdr.getField(FileHdr.TYPE).toString();
        
        if (strClassPackage != null)
            if (strPackage != null)
                if (!strClassPackage.matches(this.patternToRegex(strPackage)))
                    return false;
        
        if (strClassType != null)
            if (strType != null)
                if (!strClassType.toUpperCase().contains(strType.toUpperCase()))
                    return false;
        
        if (classLists != null)
        {
            String className = recClassInfo.getField(ClassInfo.CLASS_NAME).toString();
            boolean match = false;
            for (String classMatch : classLists)
            {
                if (className.equalsIgnoreCase(classMatch))
                    match = true;
            }
            if (!match)
                return false;
        }
        
        if (strClassProject == null)
            return false;   // Never
        if (strProject != null)
            return strClassProject.matches(this.patternToRegex(strProject));   // Does the project name pattern match?
        
        if (database != null)
           if (!database.equalsIgnoreCase(recFileHdr.getField(FileHdr.DATABASE_NAME).toString()))
               return false;
        
        return true;
    }
    /**
     * Kind of convert file filter to regex.
     */
    public String patternToRegex(String string)
    {
        if (string != null)
            if (!string.contains("["))
                 if (!string.contains("{"))  // If it has one of these, it probably is a regex already.
                     if (!string.contains("\\."))
        {
            string = string.replace(".", "\\.");
            string = string.replace("*", ".*");
        }
        return string;
    }
    /**
     * DisableAllListeners Method.
     */
    public void disableAllListeners(Record record)
    {
        record.setEnableListeners(false);   // Disable all file behaviors
        record.setEnableFieldListeners(false);
    }
    /**
     * GetFullPackageName Method.
     */
    public String getFullPackageName(String classProjectID, String classPackageName)
    {
        if (classProjectID == null)
            return DBConstants.BLANK;
        String packageName = classProjectPackages.get(classProjectID);
        if (packageName == null)
            return null;    // This class is not accessible
        if (packageName.startsWith("."))
            packageName = Constants.ROOT_PACKAGE.substring(0, Constants.ROOT_PACKAGE.length() - 1) + packageName;
        if (classPackageName == null)
            classPackageName = DBConstants.BLANK;
        if (classPackageName.startsWith("."))
            packageName = packageName + classPackageName;
        else if (classPackageName.length() > 0)
            packageName = classPackageName;
        return packageName;
    }

}
