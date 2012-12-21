/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * @(#)BaseDatabase.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.Utility;
import org.jbundle.base.util.Environment;
import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.db.Database;
import org.jbundle.model.db.DatabaseOwner;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * Database.
 * NOTE: These methods MUST be thread-safe since this is shared by an application.
 * Override getDatabase().
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class BaseDatabase extends Object
    implements Database, PropertyOwner
{
    /**
     * The database's DatabaseOwner.
     */
    protected DatabaseOwner m_databaseOwner = null;
    /**
     * The database name.
     */
    protected String m_strDbName = null;
    /**
     * The database type.
     */
    protected int m_iDatabaseType = DBConstants.LOCAL;
    /**
     * Is this database in the master or client space.
     */
    protected int m_iMasterSlave = RecordOwner.MASTER | RecordOwner.SLAVE;
    /**
     * List of tables belonging to this database.
     */
    protected Vector<BaseTable> m_vTableList = null;
    /**
     * Database properties.
     */
    protected Map<String,Object> m_properties = null;
    /**
     * Does this database support autosequence natively?
     */
    protected boolean m_bAutosequenceSupport = true;
    /**
     * The first time this database is opened, an attempt is made to open
     * the language-specific version.
     * If it doesn't exist, this variable is set to this database.
     */
    protected BaseDatabase m_databaseLocale = null;
    /**
     * The base database for hierarchical tables.
     */
    protected BaseDatabase m_databaseBase = null;

    public static final String ENGLISH = "en";
    public static final String BASE_DATABASE = "BaseDatabase";  // Same as field in DatabaseInfo file

    /**
     * Constructor (Don't call this one).
     */
    public BaseDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public BaseDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
    {
        this();
        this.init(databaseOwner, strDbName, iDatabaseType, null);
    }
    /**
     * Init this database and add it to the databaseOwner.
     * @param databaseOwner My databaseOwner.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     * @param properties Initial database properties
     * @param strDBName The database name.
     */
    public void init(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType, Map<String, Object> properties)
    {
        m_databaseOwner = databaseOwner;
        m_strDbName = strDbName;
        m_iDatabaseType = iDatabaseType;
        m_vTableList = new Vector<BaseTable>();
        m_iMasterSlave = RecordOwner.MASTER | RecordOwner.SLAVE;
        m_bAutosequenceSupport = true;
        if (m_properties == null)
            m_properties = new Hashtable<String,Object>();
        if (properties != null)
        	m_properties.putAll(properties);

        m_databaseOwner.addDatabase(this);
    }
    /**
     * Free this database object.
     */
    public void free()
    {
        this.close();
        while (m_vTableList.size() > 0)
        {
            BaseTable table = m_vTableList.elementAt(0);
            table.free();
        } 
        m_vTableList.removeAllElements();
        m_vTableList = null;

        if (m_databaseOwner != null)
            m_databaseOwner.removeDatabase(this);
        m_databaseOwner = null;
        m_strDbName = null;
    }
    /**
     * Add this Table to my Tablelist.
     * @param table The table to add.
     */
    public void addTable(BaseTable table)
    {
        m_vTableList.addElement(table);
    }
    /**
     * Remove this table from my table list.
     * @param table The table to remove.
     * @return true if successful.
     */
    public boolean removeTable(BaseTable table)
    {
        return m_vTableList.removeElement(table);
    }
    /**
     * Get the count from the table list.
     * @return The table count.
     */
    public int getTableCount()
    {
        return m_vTableList.size();
    }
    /**
     * Set the m_databaseOwner.
     * @param databaseOwner The databaseOwner to set.
     */
    public void setDatabaseOwner(DatabaseOwner databaseOwner)
    {
        m_databaseOwner = databaseOwner;
    }
    /**
     * Get the databaseOwner.
     * @return The databaseOwner.
     */
    public DatabaseOwner getDatabaseOwner()
    {
        return m_databaseOwner;
    }
    /**
     * Convert this error to a DBException.
     * Override this to provide a system-specific description.
     * @param ex The exception to convert (ie., a SQL Exception).
     * @return The appropriate DBException.
     */
    public DatabaseException convertError(Exception ex)
    {
        return DatabaseException.toDatabaseException(ex); // Standard conversion
    }
    public static final String DBSHARED_PARAM_SUFFIX = "SharedDBName";
    public static final String DBUSER_PARAM_SUFFIX = "UserDBName";
    /**
     * Get the Database Name.
     * @param bPhysicalName Return the full physical name of the database
     * @return The db name.
     */
    public String getDatabaseName(boolean bPhysicalName)
    {
        String strDbName = m_strDbName;
        if (bPhysicalName)
        {
            if ((this.getDatabaseType() & DBConstants.SYSTEM_DATABASE) != DBConstants.SYSTEM_DATABASE)
            {
                if ((this.getProperty(strDbName + DBSHARED_PARAM_SUFFIX) != null)
                    && (!m_strDbName.equalsIgnoreCase(this.getProperty(SQLParams.INTERNAL_DB_NAME)))
                    && ((this.getDatabaseType() & DBConstants.TABLE_DATA_TYPE_MASK) == DBConstants.SHARED_DATA))
                        strDbName = this.getProperty(strDbName + DBSHARED_PARAM_SUFFIX);	// Special processing - db name supplied
                else if ((this.getProperty(strDbName + DBUSER_PARAM_SUFFIX) != null)
                    && (!m_strDbName.equalsIgnoreCase(this.getProperty(SQLParams.INTERNAL_DB_NAME)))
                    && ((this.getDatabaseType() & DBConstants.TABLE_DATA_TYPE_MASK) == DBConstants.USER_DATA))
                        strDbName = this.getProperty(strDbName + DBUSER_PARAM_SUFFIX);	// Special processing - db name supplied
                else
                {
	                if (this.getProperty(DBConstants.DB_USER_PREFIX) != null)
	                    if (!m_strDbName.equalsIgnoreCase(this.getProperty(SQLParams.INTERNAL_DB_NAME)))
	                        if ((this.getDatabaseType() & DBConstants.TABLE_DATA_TYPE_MASK) == DBConstants.USER_DATA)
	                    		strDbName = Utility.addToPath(this.getProperty(DBConstants.DB_USER_PREFIX), strDbName, DB_NAME_SEPARATOR);    // User prefix - Only for user data
	                if (getSystemSuffix(this.getProperty(DBConstants.SYSTEM_NAME)) != null)
	                    if (!m_strDbName.equalsIgnoreCase(this.getProperty(SQLParams.INTERNAL_DB_NAME)))
	                    {
	                        if ((this.getDatabaseType() & DBConstants.TABLE_DATA_TYPE_MASK) == DBConstants.USER_DATA)
	                        	strDbName = Utility.addToPath(strDbName, getSystemSuffix(this.getProperty(DBConstants.SYSTEM_NAME)), DB_NAME_SEPARATOR);	 // System suffix
	                        else if ((this.getDatabaseType() & DBConstants.TABLE_DATA_TYPE_MASK) == DBConstants.SHARED_DATA)
	                        {  // The shared database name is never changed, EXCEPT if the mode is set. Then there are a few exceptions
	                            if ((this.getProperty(DBConstants.MODE) == null) || (this.getProperty(DBConstants.MODE).equalsIgnoreCase(RUN_MODE)))
                                {  // Default = Regular (run) mode
                                    if ((DEV_DATABASE.equalsIgnoreCase(m_strDbName)) || (MAIN_DATABASE.equalsIgnoreCase(m_strDbName)))
                                        strDbName = Utility.addToPath(strDbName, getSystemSuffix(this.getProperty(DBConstants.SYSTEM_NAME)), DB_NAME_SEPARATOR);  // Add System suffix
                                }
                                else
                                {   // Development mode
                                    if (this.getProperty(DBConstants.MODE).length() >= 3)
	                                    if (this.getProperty(DBConstants.MODE).substring(0, 3).equalsIgnoreCase(DEVELOPMENT_MODE))
	                                {
	                                    if (DEV_DATABASE.equalsIgnoreCase(m_strDbName))
                                            strDbName = Utility.addToPath(strDbName, getSystemSuffix(this.getProperty(DBConstants.SYSTEM_NAME)), DB_NAME_SEPARATOR);  // System suffix	                                        
	                                    if (MAIN_DATABASE.equalsIgnoreCase(m_strDbName))
	                                        strDbName = Utility.addToPath(strDbName, DEV_MAIN_SUFFIX, DB_NAME_SEPARATOR);  // Development menus
	                                }
                                }
	                        }
	                    }
                }
//x                if (this.getLocale() != null) //**THIS IS DONE IN makeDBLocale**
//x                	if ((this.getDatabaseType() & DBConstants.TABLE_DATA_TYPE_MASK) == DBConstants.SHARED_DATA)
//x                		if (this.getLocale() != this.getProperty(DBConstants.SYSTEM_NAME))
//x                			strDbName = strDbName + '_' + this.getLocale();
                if ((this.getDatabaseType() & DBConstants.TABLE_DATA_TYPE_MASK) == DBConstants.USER_DATA)
                    strDbName = Utility.addToPath(strDbName, USER_SUFFIX, DB_NAME_SEPARATOR);
                else if ((this.getDatabaseType() & DBConstants.TABLE_DATA_TYPE_MASK) == DBConstants.SHARED_DATA)
                    strDbName = Utility.addToPath(strDbName, SHARED_SUFFIX, DB_NAME_SEPARATOR);
            }
        }
        return strDbName;
    }
    public static final String USER_SUFFIX = "_user";
    public static final String SHARED_SUFFIX = "_shared";
    public static final String DEFAULT_SYSTEM_SUFFIX = "_tour";
    public static final String DEVELOPMENT_MODE = "dev";    // Development
    //public static final String OVERRIDE_MODE = "ove";     // Override
    public static final String RUN_MODE = "run";            // Run
    public static final String DEV_DATABASE = "program";
    public static final String MAIN_DATABASE = "main";
    public static final String DEV_MAIN_SUFFIX = "_base";
    public static final char DB_NAME_SEPARATOR = '_';
    /**
     * Get the system suffix, fix it and return it.
     * @return
     */
    public static String getSystemSuffix(String suffix)
    {
        if (suffix == null)
            suffix = DEFAULT_SYSTEM_SUFFIX;
        for (int i = suffix.length() - 2; i > 0; i--)
        {   // Only use last word
            if (!Character.isLetterOrDigit(suffix.charAt(i)))
            {
                suffix = suffix.substring(i + 1); // Typical to pass groupId
                break;
            }
        }
        if (suffix.length() > 0)
            if (Character.isLetterOrDigit(suffix.charAt(0)))
                suffix = "_" + suffix;
        return suffix;
    }
    /**
     * Get the database locale.
     * @return
     */
//x    public String getLocale()
//x    {
//x        return this.getProperty("locale");
//x    }
    /**
     * Get the database type.
     * @return The db type (REMOTE/LOCAL).
     */
    public int getDatabaseType()
    {
        return m_iDatabaseType;
    }
    /**
     * Does this database server masters or clients?
     * @return The master/client flag(s) from RecordOwner.
     */
    public int getMasterSlave()
    {
        return m_iMasterSlave;
    }
    /**
     * This database in the server space? (As opposed to the client space).
     * @param bIsServer Turning this off, keeps server only behaviors from being executed.
     */
    public void setMasterSlave(int iMasterSlave)
    {
        m_iMasterSlave = iMasterSlave;
    }
    /**
     * Make a table for this database.
     * <p />Don't override this method, override doMakeTable.
     * @param record The record to make a table for.
     * @return BaseTable The new table.
     */
    public final BaseTable makeTable(Record record)
    {
        BaseTable table = this.doMakeTable(record);
        
        if (DBConstants.TRUE.equalsIgnoreCase(this.getProperty(DBConstants.BASE_TABLE_ONLY)))
        	return this.returnBaseTable(table);
        int iRawDBType = (record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK);
        if ((iRawDBType == DBConstants.LOCAL)
                || (iRawDBType == DBConstants.REMOTE) || (iRawDBType == DBConstants.TABLE))
            if ((this.getMasterSlave() & RecordOwner.SLAVE) == RecordOwner.SLAVE) // Do this at the server only
        {
            if ((record.getDatabaseType() & DBConstants.LOCALIZABLE) == DBConstants.LOCALIZABLE)
            {
                m_databaseLocale = this.makeDBLocale(record, m_databaseLocale); // Check the local database to make sure it is correct
                if (m_databaseLocale != this)
                    table = this.makeResourceTable(record, table, m_databaseLocale, false);   // Add the locale capability if it exists
            }
            if (m_databaseBase != null)
                if ((record.getDatabaseType() & DBConstants.HIERARCHICAL) == DBConstants.HIERARCHICAL)
                    table = this.makeResourceTable(record, table, m_databaseBase, true);   // Add the base database if it exists
        }
        return table;
    }
    /**
     * You must override this to make a table for this database.
     * Always override this method.
     * @param record The record to make a table for.
     * @return BaseTable The new table.
     */
    public BaseTable doMakeTable(Record record)
    {
        if (((record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK) == DBConstants.MEMORY)
            || ((record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK) == DBConstants.UNSHAREABLE_MEMORY))
        {
        	BaseDatabase database = (BaseDatabase)this.getDatabaseOwner().getDatabase(DBParams.MEMORY, record.getDatabaseType(), null);
        	return database.doMakeTable(record);
        }
        else
            return null;
    }
    /**
     * Open the physical database
     * Typically override this method.
     */
    public void open() throws DBException
    {
        this.setupDatabaseProperties(); // Override this to do more
    }
    /**
     * Close the physical database (usually overridden).
     * Always override this method.
     */
    public void close()
    {
        //****Override this******
    }
    /**
     * If one exists, set up the Local version of this record.
     * Do this by opening a local version of this database and attaching a ResourceTable
     * to the record.
     * @param record The record to set up.
     * @param table The table for the record.
     * @return The new locale-sensitive table that has been setup for this record.
     */
    public BaseDatabase makeDBLocale(Record record, BaseDatabase databaseLocale)
    {
        String strLanguage = DBConstants.BLANK;
        if (record.getRecordOwner() != null) if (record.getRecordOwner().getTask() != null)
        {
            strLanguage = record.getRecordOwner().getTask().getProperty(DBParams.LANGUAGE);
            if ((strLanguage == null) || (strLanguage.length() == 0))
                strLanguage = record.getRecordOwner().getTask().getApplication().getLanguage(true);
        }
        if ((strLanguage == null) || (strLanguage.length() == 0))
            strLanguage = ENGLISH;
        String strCurrentLanguage = ENGLISH;
        if (databaseLocale != null)
        {   // Unlikely, but if locale changes, need to change locale database
            strCurrentLanguage = ENGLISH;
            if (databaseLocale.getProperties() != null)
                strCurrentLanguage = (String)databaseLocale.getProperties().get(DBParams.LANGUAGE);
            if ((strCurrentLanguage == null) || (strCurrentLanguage.length() == 0))
                strCurrentLanguage = ENGLISH;
            if (!strCurrentLanguage.equalsIgnoreCase(strLanguage))
            {
                if (databaseLocale != this)
                    databaseLocale.free();
                databaseLocale = null;
            }
        }
        if (databaseLocale == null)
        { // First time
            if (strLanguage != null)
                if (strLanguage.length() > 0) 
                    if (!ENGLISH.equals(strLanguage))
            {
                String strLocaleDBName = record.getDatabaseName() + "_" + strLanguage;
                if (!strLocaleDBName.equals(this.getDatabaseName(false)))
                {	// Must create the locale database manually
                	Object strOldCreate = this.getProperties().get(DBConstants.CREATE_DB_IF_NOT_FOUND);
                	if (this.getProperty("createLocaleDatabase") == null)
                	{
                		if ((strLanguage.length() == 2) || (strLanguage.length() == 5))		// Don't create database for locales (es or en_EN) only for special locales
                			this.getProperties().put(DBConstants.CREATE_DB_IF_NOT_FOUND, DBConstants.FALSE);
                	}
                    databaseLocale = (BaseDatabase)m_databaseOwner.getDatabase(strLocaleDBName, record.getDatabaseType() & DBConstants.TABLE_MASK, this.getProperties());
                    if (databaseLocale != null)
                    {
                    	databaseLocale.setProperty(DBParams.LANGUAGE, strLanguage);
                    	databaseLocale.setProperty(SQLParams.NO_NULL_FIELD_SUPPORT, DBConstants.TRUE);	// Since null means use data from main record
                    }
                    if (strOldCreate == null)
                    	this.getProperties().remove(DBConstants.CREATE_DB_IF_NOT_FOUND);
                    else
                    	this.getProperties().put(DBConstants.CREATE_DB_IF_NOT_FOUND, strOldCreate);
                }
            }
        }
        if (databaseLocale == null)
            databaseLocale = this;    // If not found the first time through, don't keep looking.
        return databaseLocale;
    }
    /**
     * If one exists, set up the Local version of this record.
     * Do this by opening a local version of this database and attaching a ResourceTable
     * to the record.
     * @param record The record to set up.
     * @param table The table for the record.
     * @return The new locale-sensitive table that has been setup for this record.
     */
    public BaseTable makeResourceTable(Record record, BaseTable table, BaseDatabase databaseBase, boolean bHierarchicalTable)
    {   // Create a mirrored record in the locale database
        Record record2 = (Record)ClassServiceUtility.getClassService().makeObjectFromClassName(record.getClass().getName());
        if (record2 != null)
        {
            BaseTable table2 = databaseBase.makeTable(record2);
            record2.setTable(table2);

            RecordOwner recordOwner = Record.findRecordOwner(record);
            record2.init(recordOwner);
            recordOwner.removeRecord(record2);  // This is okay as ResourceTable will remove this table on close.

            record.setTable(table);     // This is necessary to link-up ResourceTable
            if (!bHierarchicalTable)
                table = new org.jbundle.base.db.util.ResourceTable(null, record);
            else
                table = new org.jbundle.base.db.util.HierarchicalTable(null, record);
            table.addTable(table2);
        }
        return table;
    }
    /**
     * Does this database support autosequence numbers?
     * @return true if this db has autosequence support.
     */
    public boolean isAutosequenceSupport()
    {
        return m_bAutosequenceSupport;
    }
    /**
     * Get this property.
     * @param strProperty The key to lookup.
     * @return The return value.
     */
    public void setProperty(String strProperty, String strValue)
    {
        if (m_properties == null)
            m_properties = new Hashtable<String,Object>();
        if (strValue != null)
            m_properties.put(strProperty, strValue);
        else
            m_properties.remove(strProperty);
    }
    /**
     * Get this property.
     * @param strProperty The key to lookup.
     * @return The return value.
     */
    public String getProperty(String strProperty)
    {
        String strValue = null;
        if (m_properties != null)
            strValue = (String)m_properties.get(strProperty);
        if (strValue == null)	// || (strValue.length() == 0)) [db must be able to set to BLANK to override systemname property]
            strValue = m_databaseOwner.getProperty(strProperty);
        return strValue;
    }
    /**
     * Get the property object.
     * @return The property object.
     */
    public Map<String, Object> getProperties()
    {
        return m_properties;
    }
    /**
     * Get the owner of this property key.
     * @param strPropertyCode The key I'm looking for the owner to.
     * @return The owner of this property key.
     */
    public PropertyOwner retrieveUserProperties(String strRegistrationKey)
    {
        if (m_properties != null)
            if (m_properties.get(strRegistrationKey) != null)
                return this;
        return m_databaseOwner.retrieveUserProperties(strRegistrationKey);
    }
    /**
     * Set the properties.
     * @param strProperties The properties to set.
     */
    public void setProperties(Map<String, Object> properties)
    {
        m_properties = properties;
    }
    /**
     * Commit the transactions since the last commit.
     * Override this for SQL implementations.
     * @exception DBException An exception.
     */
    public void commit() throws DBException
    {
        if (DBConstants.FALSE.equalsIgnoreCase(this.getProperty(SQLParams.AUTO_COMMIT_PARAM)))
        {
            if (!(this.getDatabaseOwner() instanceof RecordOwner))
                throw new DatabaseException("Transactions requires DB RecordOwnership");
        }
        else
            throw new DatabaseException("Auto-commit enabled");
    }
    /**
     * Rollback the transactions since the last commit.
     * Override this for SQL implementations.
     * @exception DBException An exception.
     */
    public void rollback() throws DBException
    {
        if (DBConstants.FALSE.equalsIgnoreCase(this.getProperty(SQLParams.AUTO_COMMIT_PARAM)))
        {
            if (!(this.getDatabaseOwner() instanceof RecordOwner))
                throw new DatabaseException("Transactions requires DB RecordOwnership");
        }
        else
            throw new DatabaseException("Auto-commit enabled");
    }
    /**
     * Create a new empty database using the definition in the record.
     * @exception DBException Open errors passed from SQL.
     * @return true if successful.
     */
    public boolean create(String strDatabaseName) throws DBException
    {
        return false;
    }
    /**
     * Since only the DB properties are passed down to the server,
     * make sure any global properties that pertain to databases are passed down also.
     * @param properties The properties object to add these properties to.
     * @param propertyOwner Look here for db properties
     * @param otherProperties to look for db properties in
     */
    public static Map<String,Object> addDBProperties(Map<String,Object> properties, PropertyOwner propertyOwner, Map<String,Object> otherProperties)
    {
        if (properties == null)
            properties = new Hashtable<String,Object>();
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.CREATE_DB_IF_NOT_FOUND);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.DB_USER_PREFIX);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.SYSTEM_NAME);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.MODE);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.LOAD_INITIAL_DATA);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.ARCHIVE_FOLDER);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.USER_ARCHIVE_FOLDER);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.SHARED_ARCHIVE_FOLDER);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, DBConstants.BASE_TABLE_ONLY);
        BaseDatabase.addDBProperty(properties, propertyOwner, otherProperties, SQLParams.AUTO_COMMIT_PARAM);
        if (propertyOwner != null)
        {
        	BaseDatabase.addOtherDBProperties(properties, propertyOwner.getProperties());
        	if (propertyOwner instanceof RecordOwnerParent)
        	{
        		Task task = ((RecordOwnerParent)propertyOwner).getTask();
        		if (task != null)
    			{
        			BaseDatabase.addOtherDBProperties(properties, task.getProperties());
        			App app = task.getApplication();
    				if (app != null)
    				{
    					BaseDatabase.addOtherDBProperties(properties, app.getProperties());
    					// Note: db properties should not be in the environment.. so don't go farther
    				}
    			}
        	}
        }
        if (otherProperties != null)
        	BaseDatabase.addOtherDBProperties(properties, otherProperties);        	
        return properties;
    }
    public static void addOtherDBProperties(Map<String,Object> properties, Map<String,Object> otherProperties)
    {
    	if (otherProperties != null)
		    for (String strKey : otherProperties.keySet())
		    {
		    	if ((strKey.endsWith(DBSHARED_PARAM_SUFFIX)) || (strKey.endsWith(DBUSER_PARAM_SUFFIX)))
		            BaseDatabase.addDBProperty(properties, null, otherProperties, strKey);
		    }
    }
    /**
     * 
     */
    public static void addDBProperty(Map<String,Object> properties, PropertyOwner propertyOwner, Map<String,Object> otherProperties, String strProperty)
    {
    	if (propertyOwner != null)
    		if (propertyOwner.getProperty(strProperty) != null)
    			properties.put(strProperty, propertyOwner.getProperty(strProperty));
    	if (otherProperties != null)
    		if (otherProperties.get(strProperty) != null)
    			properties.put(strProperty, otherProperties.get(strProperty));
    }
    /**
     * Get the starting ID for this table.
     * Override this for different behavior.
     * @return The starting id
     */
    public int getStartingID()
    {
        int iStartingID = 1;   // (default)
        if (this.getProperty(STARTING_ID) != null)
        {
            try {
                iStartingID = Integer.parseInt(this.getProperty(STARTING_ID));
            } catch (NumberFormatException e) {
                iStartingID = 1;
            }
        }
        return iStartingID;
    }
    /**
     * Get the starting ID for this table.
     * Override this for different behavior.
     * @return The starting id
     */
    public int getEndingID()
    {
        int iEndingID = Integer.MAX_VALUE;   // (default)
        if (this.getProperty(ENDING_ID) != null)
        {
            try {
                iEndingID = Integer.parseInt(this.getProperty(ENDING_ID));
            } catch (NumberFormatException e) {
                iEndingID = Integer.MAX_VALUE;
            }
        }
        return iEndingID;
    }
    public static final String STARTING_ID = "StartID"; // Same as DatabaseInfo field name
    public static final String ENDING_ID = "EndID";
    public static final String DB_PROPERTIES_LOADED = "dbPropertiesLoaded";
    /**
     * Given the name of the database, get the properties file and optionally merge them with my current properties.
     * @param strDatabaseName The name of the database engine (as returned in the meta call).
     * @return The ResourceBundle with all the database properties.
     */
    public boolean setupDatabaseProperties()
    {
        if (this.getProperty(DB_PROPERTIES_LOADED) != null)
            return true;    // Already setup

        String strDatabaseName = this.getDatabaseName(true);
        Environment env = (Environment)this.getDatabaseOwner().getEnvironment();
        Map<String,String> map = env.getCachedDatabaseProperties(strDatabaseName);
        if ((map == null) || (map == Environment.DATABASE_DOESNT_EXIST))
        {
            map = this.getDatabaseProperties();
            if (map == null)
                return false;   // Was not set up
            env.cacheDatabaseProperties(strDatabaseName, map);  // Update the cache
        }
        for (String key : map.keySet())
        {
            this.setProperty(key, map.get(key));
        }
        
        if (m_databaseBase == null) // Always
            if (this.getProperty(BASE_DATABASE) != null)
        {
            Map<String,Object> properties = new HashMap<String,Object>();
            properties.putAll(this.getProperties());
            properties.remove(BASE_DATABASE);
            properties.remove(SQLParams.JDBC_DRIVER_PARAM);
            properties.put(DBConstants.DB_USER_PREFIX, DBConstants.BLANK);
            properties.put(DBConstants.SYSTEM_NAME, DBConstants.BLANK);
            properties.put(DBConstants.MODE, DBConstants.BLANK);
            m_databaseBase = (BaseDatabase)m_databaseOwner.getDatabase(this.getProperty(BASE_DATABASE), this.getDatabaseType() & DBConstants.TABLE_MASK, properties);
        }

        this.setProperty(DB_PROPERTIES_LOADED, DBConstants.TRUE); // Flag properties as read
        
        return true;    // Success
    }
    /**
     * Get the initial DB properties (null means no properties).
     * Override this.
     * @param objDatabaseParam database specific parameter (ie., for jdbc this is the db name)
     * @return The initial properties.
     */
    public Map<String,String> getDatabaseProperties()
    {
        return null;    // Override this.
    }
    /**
     * Return the base table for this record.
     * This is very specialized logic for exporting and importing only the base file.
     * Further, this logic will return the localized version of the file if language=xxx is set for the client.
     * @param table The record's initial table
     * @return The base table.
     */
    public BaseTable returnBaseTable(BaseTable table)
    {
    	Record record = table.getRecord();
        int iRawDBType = (record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK);
        if ((iRawDBType == DBConstants.LOCAL)
                || (iRawDBType == DBConstants.REMOTE) || (iRawDBType == DBConstants.TABLE))
            if ((this.getMasterSlave() & RecordOwner.SLAVE) == RecordOwner.SLAVE) // Do this at the server only
        {
            if ((record.getDatabaseType() & DBConstants.LOCALIZABLE) == DBConstants.LOCALIZABLE)
            {
                m_databaseLocale = this.makeDBLocale(record, m_databaseLocale); // Check the local database to make sure it is correct
                if (m_databaseLocale != this)
                {
                	table.setRecord(null);
                	table.free();	// Don't need this for base access
                	table = m_databaseLocale.makeTable(record);
                }
            }
        }
    	return table;	// Base table (special) access
    }
}
