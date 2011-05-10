package org.jbundle.base.util;

/**
 * @(#)Environment.java   1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.model.DBException;
import org.jbundle.model.util.Util;


/**
 * DatabaseCollection is a helper class for the object that has to keep track of databases.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DatabaseCollection extends Object
{
    /**
     * This collection's owner.
     */
    protected DatabaseOwner m_databaseOwner = null;
    /**
     * The list of databases.
     */
    private Hashtable<String, BaseDatabase> m_htDatabaseList[] = new Hashtable[DBConstants.TABLE_MASK + 1];    // List of database lists (Each list is by DB name)

    /**
     * Constructor
     */
    public DatabaseCollection()
    {
        super();
    }
    /**
     * Constructor.
     * Constructs a new Environment (The one and only).
     * @param args The initial arguments. local=the Local Database prefix.
     * remote=the Remote Database prefix.
     */
    public DatabaseCollection(DatabaseOwner databaseOwner)
    {
        this();
        this.init(databaseOwner);
    }
    /**
     * Initialize this object.
     * @param args The initial arguments. local=the Local Database prefix.
     * remote=the Remote Database prefix.
     */
    public void init(DatabaseOwner databaseOwner)
    {
        m_databaseOwner = databaseOwner;
    }
    /**
     * Free this object.
     */
    public void free()
    {
        for (int i = 0; i < m_htDatabaseList.length; i++)
        {
            if (m_htDatabaseList[i] != null)
            {
                for (Enumeration<BaseDatabase> e = m_htDatabaseList[i].elements() ; e.hasMoreElements() ;)
                { 
                    BaseDatabase database = e.nextElement();
                    database.setDatabaseOwner(null);    // Otherwise the enumerator is messed up
                    database.free();
                } 
                m_htDatabaseList[i].clear();
                m_htDatabaseList[i] = null;
            } 
        }
        m_htDatabaseList = null;

        m_databaseOwner = null;
    }
    /**
     * Given the name, either get the open database, or open a new one.
     * @param strDBName The name of the database.
     * @param iDatabaseType The type of database/table.
     * @return The database (new or current).
     */
    public BaseDatabase getDatabase(String strDBName, int iDatabaseType, Map<String, Object> properties)
    {
        if ((strDBName == null) || (strDBName.length() == 0))
            return null;
        BaseDatabase database = this.getDatabaseList(iDatabaseType).get(strDBName);
        if (database == null)
        {
            if ((properties != null)
            	&& (DBConstants.FALSE.equals(properties.get(DBConstants.CREATE_DB_IF_NOT_FOUND)))
            	&& (Environment.DATABASE_DOESNT_EXIST == m_databaseOwner.getEnvironment().getCachedDatabaseProperties(strDBName)))
            		return null;	// Hey, if the database doesn't exist, don't try again.
            if (iDatabaseType != DBConstants.SCREEN)
                database = this.makeDatabase(iDatabaseType);    // BaseDatabase not found, open it!
            else
                database = new BaseDatabase();                  // Special case: A screen database
            if (database != null)
                database.init(m_databaseOwner, strDBName, iDatabaseType & DBConstants.TABLE_MASK, properties); // init it!
            if (database != null)
            {
                try   {
                    database.open();    // open it!
                } catch (DBException ex) {
                    database.free();
                    database = null;    // Failure on db open
                    if ((ex.getErrorCode() == DBConstants.DB_NOT_FOUND)
                		&& (properties != null)
                        && (DBConstants.FALSE.equals(properties.get(DBConstants.CREATE_DB_IF_NOT_FOUND))))
                    {	// If the db doesn't exist, cache the name so I won't try this (expensive) operation again
                    	m_databaseOwner.getEnvironment().cacheDatabaseProperties(strDBName, Environment.DATABASE_DOESNT_EXIST);
                    }
                }
            }
        }
        return database;
    }
    /**
     * Get the table that holds this type of database.
     * @param iDatabaseType The DB Type.
     * @return The database table.
     */
    public Hashtable<String,BaseDatabase> getDatabaseList(int iDatabaseType)
    {
        String strDbPrefix = this.getDatabasePrefix(iDatabaseType);
        iDatabaseType = iDatabaseType & DBConstants.TABLE_MASK;
        if ((iDatabaseType & DBConstants.TABLE_TYPE_MASK) == DBConstants.REMOTE)
            if (this.getProperty(DBParams.LOCAL) != null)
                if (this.getProperty(DBParams.LOCAL).equals(this.getProperty(DBParams.REMOTE)))
                    iDatabaseType = (iDatabaseType & ~DBConstants.TABLE_TYPE_MASK) | DBConstants.LOCAL;  // Local and Remote use the same database
        if ((iDatabaseType & DBConstants.TABLE_TYPE_MASK) == DBConstants.TABLE)
            if (this.getProperty(DBParams.LOCAL) != null)
                if (this.getProperty(DBParams.LOCAL).equals(this.getProperty(DBParams.TABLE)))
                    iDatabaseType = (iDatabaseType & ~DBConstants.TABLE_TYPE_MASK) | DBConstants.LOCAL;  // Local and Table use the same database
        if (iDatabaseType == DBConstants.SCREEN)
            iDatabaseType = (iDatabaseType & ~DBConstants.TABLE_TYPE_MASK) | DBConstants.LOCAL;  // Local and Screen use the same database
        if ("Mapped".equalsIgnoreCase(strDbPrefix))
            iDatabaseType = DBConstants.INTERNAL_MAPPED;
        
        if (m_htDatabaseList[iDatabaseType] == null)
            m_htDatabaseList[iDatabaseType] = new Hashtable<String,BaseDatabase>();
        return m_htDatabaseList[iDatabaseType];
    }
    /**
     * Is this DB type directly accessing the data?
     * @param strDbPrefix
     * @return
     */
    public boolean isConcreteDBClass(String strDbPrefix)
    {
        if ((DBParams.JDBC.equalsIgnoreCase(strDbPrefix))
                || ("Serial".equalsIgnoreCase(strDbPrefix))
                || ("Soap".equalsIgnoreCase(strDbPrefix))
                || ("Xml".equalsIgnoreCase(strDbPrefix)))
            return true;
        return false;
    }
    /**
     * Instantiate a new Database object.<br />
     * Warning - Remember to call the init(name) method on the returned database object.
     * @param iDBType The DB Type.
     * @return The database.
     */
    public BaseDatabase makeDatabase(int iDatabaseType)
    {
        BaseDatabase database = null;
        String strDbPrefix = this.getDatabasePrefix(iDatabaseType);

        if (strDbPrefix != null) if (strDbPrefix.indexOf('.') == -1)
            strDbPrefix = DBConstants.ROOT_PACKAGE + "base.db." + strDbPrefix.toLowerCase() + "." + strDbPrefix + "Database";
        database = (BaseDatabase)Util.makeObjectFromClassName(strDbPrefix);
        if (database == null)
            database = new BaseDatabase();  // default
        return database;
    }
    /**
     * Get Database prefix.
     * @param iDBType The DB Type.
     * @return The database.
     */
    public String getDatabasePrefix(int iDatabaseType)
    {
        int iDBType = (iDatabaseType & DBConstants.TABLE_TYPE_MASK);
        String strDbPrefix = null;
        if (iDBType == DBConstants.LOCAL)
            strDbPrefix = this.getProperty(DBParams.LOCAL);
        else if (iDBType == DBConstants.REMOTE)
            strDbPrefix = this.getProperty(DBParams.REMOTE);
        else if (iDBType == DBConstants.TABLE)
            strDbPrefix = this.getProperty(DBParams.TABLE);
        else if (iDBType == DBConstants.MEMORY)
            strDbPrefix = DBParams.MEMORY;
        else if (iDBType == DBConstants.UNSHAREABLE_MEMORY)
            strDbPrefix = DBParams.MEMORY;
        else if (iDBType == DBConstants.SYSTEM_DATABASE)
            strDbPrefix = this.getProperty(DBParams.REMOTE);
        else if (iDBType == DBConstants.REMOTE_MEMORY)
        {
            strDbPrefix = this.getProperty(DBParams.REMOTE);    // Client for remote, memory for concrete db
            if ((strDbPrefix == null) || (this.isConcreteDBClass(strDbPrefix)))
                strDbPrefix = DBParams.MEMORY;
        }
        if ((iDatabaseType & DBConstants.MAPPED) != 0)
            if (this.isConcreteDBClass(strDbPrefix))
                strDbPrefix = "Mapped";
        return strDbPrefix;
    }
    /**
     * Add this database to my database list.<br />
     * Do not call these directly, used in database init.
     * @param database The database to add.
     */
    public void addDatabase(BaseDatabase database)
    {
        this.getDatabaseList((database.getDatabaseType() & DBConstants.TABLE_MASK)).put(database.getDatabaseName(false), database);
    }
    /**
     * Remove this database from my database list.
     * Do not call these directly, used in database free.
     * @param database The database to free.
     * @return true if successful.
     */
    public boolean removeDatabase(BaseDatabase database)
    {
        database = (BaseDatabase)this.getDatabaseList((database.getDatabaseType() & DBConstants.TABLE_MASK)).remove(database.getDatabaseName(false));
        if (database == null)
            return false;
        return true;
    }
    /**
     * This is just a utility method to get the properties from the DB Owner.
     * There is a slight difference between this getProperties and the one in the
     * recordOwner. The recordowner getProperty method only checks up to the application.
     * This method also checks the Environment properties.
     * @param strProperty The key to look up the property.
     * @return The value associated with this key.
     */
    private String getProperty(String strProperty)
    {
        String strValue = m_databaseOwner.getProperty(strProperty);
        if (strValue == null)
            if (!(m_databaseOwner instanceof Environment))
                if (m_databaseOwner.getEnvironment() != null)
                    strValue = m_databaseOwner.getEnvironment().getProperty(strProperty);
        return strValue;
    }
}
