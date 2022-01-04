/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.firestore;

/**
 * @(#)FirestoreDatabase.java    1.16 19/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.DatabaseException;
import org.jbundle.base.db.PassThruTable;
import org.jbundle.base.db.QueryRecord;
import org.jbundle.base.db.QueryTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Debug;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.Utility;
import org.jbundle.base.util.Environment;
import org.jbundle.main.db.DatabaseInfo;
import org.jbundle.model.DBException;
import org.jbundle.model.db.DatabaseOwner;
import org.jbundle.thin.base.db.Converter;

/**
 * FirestoreDatabase - Implement the JDBC database.
 * <p />FirestoredbDatabases require a few parameters to use non-default behavior:<br />
 * databaseproduct - Set this if you want the setup to be automatic.<br />
 * jdbcdriver - The JDBC Driver class name.<br />
 * jdbcurl - The JDBC URL with &lt;dbname/&ft; in the URL to replace the database name.<br />
 * username - DB Username.<br />
 * password - DB Password.
 * <p />By default all transactions auto-commit. If you want to use transactions, you must:<br />
 * 1. Set the AUTO_COMMIT_PARAM property of your recordowner to FALSE.<br />
 * 2. Open your files<br />
 * 3. Do your transactions<br />
 * 4. Call record.getTable().getDatabase().commit();
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FirestoreDatabase extends BaseDatabase
{
//    /**
//     *  Only used for DB's that can't keep two DB's open, such as pointbase.
//     */
//    private static MongoClient m_mongoClient = null;
//    /**
//     * The JDBC Connection.
//     */
//    protected MongoDatabase m_mongoDatabase = null;
    /**
     * For debugging.
     */
    public static String JDBC_CATEGORY = FirestoreDatabase.class.getName();
    /**
     * Sync
     */
    private static Date firstTime = new Date();

    /**
     * Constructor.
     */
    public FirestoreDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDbName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public FirestoreDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
    {
        this();
        this.init(databaseOwner, strDbName, iDatabaseType, null);
    }
    /**
     * Initialize this database and add it to the databaseOwner.
     * @param databaseOwner My databaseOwner.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     * @param strDbName The database name.
     */
    public void init(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType, Map<String, Object> properties)
    {
        super.init(databaseOwner, strDbName, iDatabaseType, properties);
        // Typically, JDBC databases do not support locking (Change to "true" otherwise on JDBC open)
        this.setProperty(SQLParams.EDIT_DB_PROPERTY, SQLParams.DB_EDIT_NOT_SUPPORTED);

        // The following settings are true because JDBC is always running over a LAN (and the server version should send and receive messages).
        this.setProperty(DBParams.MESSAGES_TO_REMOTE, DBConstants.TRUE);   // Sent by server version
        this.setProperty(DBParams.CREATE_REMOTE_FILTER, DBConstants.TRUE);  // Yes, for remote tables
        this.setProperty(DBParams.UPDATE_REMOTE_FILTER, DBConstants.TRUE); // Updated by server version
    }
    /**
     * Free the objects.
     */
    public void free()
    {
        super.free(); // Do any inherited
    }
    /**
     * Open the physical database.
     * @exception DBException On open errors.
     */
    public void open() throws DBException
    {
//        if (m_mongoDatabase == null)
        {
            this.initConnection();
            this.loadDatabaseProperties();
        }
        
        super.open(); // Do any inherited
    }
    /**
     * Close the physical database.
     */
    public void close()
    {
        super.close();  // Do any inherrited

        // release resources
//?        m_mongoClient.close();

//?        try   {
//?            if (m_mongoDatabase != null)
//?                m_mongoDatabase.close();
//?        } catch (SQLException ex) {
//?                // Ignore error
//?        }
//        m_mongoDatabase = null;
    }
    /**
     * Does this table (collection) exist in this database?
     * @param tableName The table name
     * @return true if it exists
     */
    protected boolean doesTableExist(String tableName) throws DBException {
//        MongoIterable<String> names = this.getMongoDatabase().listCollectionNames();
//        MongoCursor<String> list = names.iterator();
//        while (list.hasNext()) {
//            if (tableName.equals(list.next())) {
//                return true;    // Table exists
//            }
//        }
        return false;
    }

    private boolean m_bFirstErrorCheck = false;
    protected String m_strFileNotFoundErrorText = null;
    protected String m_strFileNotFoundErrorCode = null;
    protected String m_strDBNotFoundErrorText = null;
    protected String m_strDBNotFoundErrorCode = null;
    protected String m_strDuplicateKeyErrorText = null;
    protected String m_strDuplicateKeyErrorCode = null;
    protected String m_strKeyNotFoundErrorText = null;
    protected String m_strKeyNotFoundErrorCode = null;
    protected String m_strRecordLockedErrorText = null;
    protected String m_strRecordLockedErrorCode = null;
    protected String m_strBrokenPipeErrorText = null;
    protected String m_strBrokenPipeErrorCode = null;
    /**
     * Convert this error to a DBException.
     * @param ex The exception to convert (usually a SQL exception).
     * @return DatabaseException The DBException.
     */
    public DatabaseException convertError(Exception ex)
    {
        DatabaseException eDB = null;
        int iError = DBConstants.ERROR_RETURN;
        String strMessage = "Error!";
        if (ex != null)
            strMessage = ex.getMessage();
//        if (ex instanceof MongoException)
        {
//            MongoException sqlex = (MongoException)ex;
            int strState = 0;//sqlex.getCode();
            strMessage += strState;
            if (!m_bFirstErrorCheck)
            {
                m_bFirstErrorCheck = true;
                m_strFileNotFoundErrorText = (String)this.getProperties().get(SQLParams.TABLE_NOT_FOUND_ERROR_TEXT);
                if (m_strFileNotFoundErrorText != null)
                    m_strFileNotFoundErrorText = m_strFileNotFoundErrorText.toLowerCase();
                m_strFileNotFoundErrorCode = (String)this.getProperties().get(SQLParams.TABLE_NOT_FOUND_ERROR_CODE);
                m_strDBNotFoundErrorText = (String)this.getProperties().get(SQLParams.DB_NOT_FOUND_ERROR_TEXT);
                if (m_strDBNotFoundErrorText != null)
                    m_strDBNotFoundErrorText = m_strDBNotFoundErrorText.toLowerCase();
                m_strDBNotFoundErrorCode = (String)this.getProperties().get(SQLParams.DB_NOT_FOUND_ERROR_CODE);
                m_strDuplicateKeyErrorText = (String)this.getProperties().get(SQLParams.DUPLICATE_KEY_ERROR_TEXT);
                if (m_strDuplicateKeyErrorText != null)
                    m_strDuplicateKeyErrorText = m_strDuplicateKeyErrorText.toLowerCase();
                m_strDuplicateKeyErrorCode = (String)this.getProperties().get(SQLParams.DUPLICATE_KEY_ERROR_CODE);
                m_strKeyNotFoundErrorText = (String)this.getProperties().get(SQLParams.KEY_NOT_FOUND_ERROR_TEXT);
                if (m_strKeyNotFoundErrorText != null)
                    m_strKeyNotFoundErrorText = m_strDuplicateKeyErrorText.toLowerCase();
                m_strKeyNotFoundErrorCode = (String)this.getProperties().get(SQLParams.KEY_NOT_FOUND_ERROR_CODE);
                m_strRecordLockedErrorText = (String)this.getProperties().get(SQLParams.RECORD_LOCKED_ERROR_TEXT);
                if (m_strRecordLockedErrorText != null)
                    m_strRecordLockedErrorText = m_strRecordLockedErrorText.toLowerCase();
                m_strRecordLockedErrorCode = (String)this.getProperties().get(SQLParams.RECORD_LOCKED_ERROR_CODE);
                m_strBrokenPipeErrorText = (String)this.getProperties().get(SQLParams.BROKEN_PIPE_ERROR_TEXT);
                if (m_strBrokenPipeErrorText != null)
                    m_strBrokenPipeErrorText = m_strBrokenPipeErrorText.toLowerCase();
                m_strBrokenPipeErrorCode = (String)this.getProperties().get(SQLParams.BROKEN_PIPE_ERROR_CODE);
            }
                // Yeah, I know this is slow, but exceptions are not that common.
            if (this.checkForError(ex, DBConstants.FILE_NOT_FOUND, m_strFileNotFoundErrorText, m_strFileNotFoundErrorCode))
                iError = DBConstants.FILE_NOT_FOUND;
            if (this.checkForError(ex, DBConstants.DB_NOT_FOUND, m_strDBNotFoundErrorText, m_strDBNotFoundErrorCode))
                iError = DBConstants.DB_NOT_FOUND;
            if (this.checkForError(ex, DBConstants.DUPLICATE_KEY, m_strDuplicateKeyErrorText, m_strDuplicateKeyErrorCode))
                iError = DBConstants.DUPLICATE_KEY;
            if (this.checkForError(ex, DBConstants.DUPLICATE_COUNTER, m_strDuplicateKeyErrorText, m_strDuplicateKeyErrorCode))
                iError = DBConstants.DUPLICATE_COUNTER;
            if (this.checkForError(ex, DBConstants.KEY_NOT_FOUND, m_strKeyNotFoundErrorText, m_strKeyNotFoundErrorCode))
                iError = DBConstants.KEY_NOT_FOUND;
            if (this.checkForError(ex, DBConstants.RECORD_LOCKED, m_strRecordLockedErrorText, m_strRecordLockedErrorCode))
                iError = DBConstants.RECORD_LOCKED;
            if (this.checkForError(ex, DBConstants.BROKEN_PIPE, m_strBrokenPipeErrorText, m_strBrokenPipeErrorCode))
                iError = DBConstants.BROKEN_PIPE;
        }
        if (iError == DBConstants.ERROR_RETURN)
        {
            Utility.getLogger().info("Unknown sql exception: " + strMessage);
            eDB = new DatabaseException(strMessage);
        }
        else if (iError == DBConstants.RECORD_LOCKED)
            eDB = new DatabaseException(iError, strMessage);    // On locked, tells user/computer
        else
            eDB = new DatabaseException(iError);    // Standard error
        return eDB;
    }
    /**
     * Check out this error, see if it matches.
     * Note: This is not very efficient code, but error should not happen that often.
     * @param ex exception thrown.
     * @param iErrorType see if error matches.
     * @param strErrorText String to match in the error string (must be lower case).
     * @return true if file was not found and was successfully created.
     */
    public boolean checkForError(Exception ex, int iErrorType, String strErrorText, String strErrorCode)
    {
        boolean bFound = false;
        String strError = ex.getMessage();
        if (strError != null)
            strError = strError.toLowerCase();
        if (iErrorType == DBConstants.FILE_NOT_FOUND)
        {
            if (strError != null)
                if (strError.indexOf("table") != -1)
                if (strError.indexOf("not") != -1)
                if ((strError.indexOf("found") != -1) || (strError.indexOf("find") != -1))
                    bFound = true;
        }
        if (iErrorType == DBConstants.DUPLICATE_KEY)
        {
            if (strError != null)
                if ((strError.indexOf("duplicate") != -1)
                    || (strError.indexOf("already exists") != -1))
                        bFound = true;
        }
        if (iErrorType == DBConstants.DUPLICATE_COUNTER)
        {
            if (strError != null)
                if ((strError.indexOf("duplicate") != -1)
                    || (strError.indexOf("already exists") != -1))
                        if ((strError.indexOf("key 1") != -1)
                        	|| (strError.indexOf("primary") != -1)
                            || (strError.indexOf("id") != -1))
                                bFound = true;
        }
        if (iErrorType == DBConstants.BROKEN_PIPE)
        {
            if (strError != null)
                if ((strError.indexOf("timeout") != -1)
                    || (strError.indexOf("time-out") != -1)
                    || (strError.indexOf("time out") != -1)
                    || ((strError.indexOf("communication") != -1) || (strError.indexOf("link") != -1) || (strError.indexOf("connection") != -1) &&
                            ((strError.indexOf("failure") != -1) || (strError.indexOf("lost") != -1)))
                    || (strError.indexOf("broken") != -1))
                        bFound = true;
        }
        if (!bFound)
            if ((strErrorText != null) && (strErrorText.length() > 0))
                if (strError != null)
                    if (strError.indexOf(strErrorText) != -1)
                        bFound = true;
        if (!bFound)
            if ((strErrorCode != null) && (strErrorCode.length() > 0))
            {
                int iErrorCode = Integer.parseInt(strErrorCode);
//                if (ex instanceof MongoException)
//                    if (((MongoException)ex).getCode() == iErrorCode)
//                        bFound = true;
            }
        return bFound;
    }
    /**
     * Make a table for this record.
     * @param record (look at the database type).
     * @return The new table for this record.
     */
    public BaseTable doMakeTable(Record record)
    {
        BaseTable table = null;
        boolean bIsQueryRecord = record.isQueryRecord();
        boolean bIsQuerySupported = !DBConstants.FALSE.equalsIgnoreCase(this.getProperty(SQLParams.SQL_JOINS_SUPPORTED));
        if ((bIsQueryRecord) && (bIsQuerySupported))
            if (!DBConstants.TRUE.equalsIgnoreCase(this.getProperty(SQLParams.SQL_VIEWS_BUILT)))
        { // If this is a query and there are no SQL Views, make sure this query doesn't have an object (multi) query as one of it's records.
            if (record instanceof QueryRecord)  // Always
                bIsQuerySupported = !((QueryRecord)record).isComplexQuery();
        }
        int iRawDBType = (record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK);
        if (((iRawDBType == DBConstants.LOCAL) || (iRawDBType == DBConstants.TABLE))
                && (!bIsQueryRecord))
            table = new FirestoreTable(this, record);
        else if ((bIsQueryRecord) &&
            ((!bIsQuerySupported)
            || ((record.getDatabaseType() & DBConstants.MANUAL_QUERY) != 0)))
        { // Physical Tables cannot process SQL queries, so use QueryTable!
            PassThruTable passThruTable = new QueryTable(this, record);
            passThruTable.addTable(record.getRecordlistAt(0).getTable());
            table = passThruTable;
        }
        else if ((iRawDBType == DBConstants.LOCAL)
                || (iRawDBType == DBConstants.REMOTE)
                    || (iRawDBType == DBConstants.TABLE))
            table = new FirestoreTable(this, record);
        else
            table = super.doMakeTable(record);
        return table;
    }
//    /**
//     * Get the Physical database connection.
//     * <p />Note: The JDBC Driver must be loaded before calling this.
//     * @return The JDBC Connection.
//     */
//    public MongoDatabase getMongoDatabase() throws DBException
//    {
//        if (m_mongoClient == null)
//        {
//            synchronized (firstTime) {
//                if (m_mongoClient == null)
//                    m_mongoClient = setupClientConnection();
//            }
//        }
//        if (m_mongoDatabase == null)	// Always
//            m_mongoDatabase = this.setupDBConnection();
//        return m_mongoDatabase;
//    }
//    /**
//     * Setup the Database connection.
//     * This method requires the jdbcurl= param with <dbname/> specified.
//     * Usually, you will have to supply a username and password.
//     */
//    public MongoClient setupClientConnection() throws DBException {
//        String strURL = this.getProperty(SQLParams.JDBC_URL_PARAM);
//        if ((strURL == null) || (strURL.length() == 0))
//            strURL = this.getProperty(SQLParams.DEFAULT_JDBC_URL_PARAM);    // Default
//        String strServer = this.getProperty(SQLParams.DB_SERVER_PARAM);
//        if ((strServer == null) || (strServer.length() == 0))
//            strServer = this.getProperty(SQLParams.DEFAULT_DB_SERVER_PARAM);    // Default
//        if ((strServer == null) || (strServer.length() == 0))
//            strServer = "localhost"; //this.getProperty(DBParams.SERVER);    // ??
//        if (strServer != null)
//            strURL = Utility.replace(strURL, "${dbserver}", strServer);
//        String strUsername = this.getProperty(SQLParams.USERNAME_PARAM);
//        if ((strUsername == null) || (strUsername.length() == 0))
//            strUsername = this.getProperty(SQLParams.DEFAULT_USERNAME_PARAM); // Default
//        if (strUsername != null)
//            strURL = Utility.replace(strURL, "${username}", strUsername);
//        String strPassword = this.getProperty(SQLParams.PASSWORD_PARAM);
//        if ((strPassword == null) || (strPassword.length() == 0))
//            strPassword = this.getProperty(SQLParams.DEFAULT_PASSWORD_PARAM); // Default
//        if (strPassword != null)
//            strURL = Utility.replace(strURL, "${password}", strPassword);
//        // Open the database for non-exclusive access
//        try {
//            Utility.getLogger().info("Connecting: " + strURL + " User: " + strUsername);
//            if ((strURL == null) || (strURL.length() == 0)) {
//                // connect to the local database server
//                m_mongoClient = MongoClients.create();
//            } else{
//                m_mongoClient = MongoClients.create(strURL);
//            }
//            Utility.getLogger().info("Connection okay: " + (m_mongoClient != null));
//        }
//        catch (java.lang.Exception ex) // IllegalArgumentException
//        {
//            ex.printStackTrace();
//            Debug.print(ex, "Error on client connection");
//            System.exit(0);
//        }
//        return m_mongoClient;
//    }
    /**
     * Setup the Database connection.
     * This method requires the jdbcurl= param with <dbname/> specified.
     * Usually, you will have to supply a username and password.
     */
//    public MongoDatabase setupDBConnection() throws DBException
//    {
//        String strDatabaseName = this.getDatabaseName(true);
//        try   {
//            Utility.getLogger().info("Opening DB: " + strDatabaseName);
//            m_mongoDatabase = m_mongoClient.getDatabase(strDatabaseName);
//            Utility.getLogger().info("Connection okay: " + (m_mongoDatabase != null));
//        }
////?        catch (SQLException e)
////?        {
////?            DBException ex = this.convertError(e);    // Probably "DB Not found"
////?            if ((ex.getErrorCode() == DBConstants.DB_NOT_FOUND)
////?                    && (!DBConstants.FALSE.equalsIgnoreCase(this.getProperty(DBConstants.CREATE_DB_IF_NOT_FOUND)))
////?                    && (DBConstants.TRUE.equals(this.getProperty(SQLParams.CREATE_DATABASE_SUPPORTED))))
////?            {
////?                if (this.create(strDatabaseName)) // Database not found, create it.
////?                    return;     // Success
////?                e.printStackTrace();
////?            }
////?            if (ex.getErrorCode() != DBConstants.DB_NOT_FOUND)
////?                e.printStackTrace();	// Ignore if create is turned off
////?            throw ex;
////?        }
//        catch (java.lang.Exception ex)
//        {
//            ex.printStackTrace();
//            Debug.print(ex, "Error on open db");
//            System.exit(0);
//        }
//        return m_mongoDatabase;
//    }
    /**
     * Setup the new connection.
     */
    public void initConnection()
    {
        try   {
            boolean bAutoCommit = true;
            if (DBConstants.FALSE.equalsIgnoreCase(this.getProperty(SQLParams.AUTO_COMMIT_PARAM)))
                bAutoCommit = false;
//?            m_mongoDatabase.setAutoCommit(bAutoCommit);
//?            DatabaseMetaData dma = m_mongoDatabase.getMetaData();
//?            String strDatabaseProductName = dma.getDatabaseProductName();
//?            Utility.getLogger().info("DB Name: " + strDatabaseProductName);
//?            this.setProperty(SQLParams.INTERNAL_DB_NAME, strDatabaseProductName);
            this.setupDatabaseProperties(); // If I haven't read the db properties, read them now!
            String strDateQuote = this.getProperty(SQLParams.SQL_DATE_QUOTE);
            if ((strDateQuote != null) && (strDateQuote.length() > 0))
                DateTimeField.setSQLQuote(strDateQuote.charAt(0));
            Converter.initGlobals();
            String strFormat = this.getProperty(SQLParams.SQL_DATE_FORMAT);
            if (strFormat != null)
                Converter.gDateSqlFormat = new java.text.SimpleDateFormat(strFormat);
            strFormat = this.getProperty(SQLParams.SQL_TIME_FORMAT);
            if (strFormat != null)
                Converter.gTimeSqlFormat = new java.text.SimpleDateFormat(strFormat);
            strFormat = this.getProperty(SQLParams.SQL_DATETIME_FORMAT);
            if (strFormat != null)
                Converter.gDateTimeSqlFormat = new java.text.SimpleDateFormat(strFormat);
//?            if (DBConstants.TRUE.equals(this.getProperty(SQLParams.SHARE_JDBC_CONNECTION)))
//?                m_mongoClient = m_mongoDatabase;  // HACK - Only one DB open at a time (pointbase)!
            Utility.getLogger().info("Connected to db: " + this.getDatabaseName(true));
        }
        catch (Exception ex)
        {
            // Ignore errors to these non-essential calls;  // Probably "DB Not found"
        }
    }
    /**
     * Get the initial DB properties (null means no properties).
     * @return The initial properties.
     */
    public Map<String,String> getDatabaseProperties()
    {
        Map<String,String> map = super.getDatabaseProperties();
        ResourceBundle dbProperties = new DBProperties_firestore();
        // Merge the properties with my properties.
        if (dbProperties != null)
        {
            if (map == null)
                map = new HashMap<String,String>();
            for (Enumeration<String> e = dbProperties.getKeys() ; e.hasMoreElements() ;)
            {
                String key = e.nextElement();
                String value = dbProperties.getString(key);
                map.put(key, value);
            }
        }

        return map;
    }
    /**
	 * Load and set the database properties from this database's (database) info table.
	 */
    public void loadDatabaseProperties()
    {
        if (this.getProperty(STARTING_ID) != null)
            return; // Already read (This value will be cached in the environment database cache)
        int iStartID = 1;
        /* This code works, but I prefer to use the DatabaseInfo record.
        try {
            Statement queryStatement = this.getJDBCConnection().createStatement();

            String strSQL = "select StartID from DatabaseInfo where ID=1;";
            queryStatement.execute(strSQL);

            ResultSet resultset = queryStatement.getResultSet();
            boolean more = resultset.next();
            if (more)
            {
                iStartID = resultset.getInt(1);
            }
            resultset.close();
            queryStatement.close();
        } catch (SQLException e) {
            // Ignore table not found error.
        }
        */
    	if ((this.getDatabaseType() & (DBConstants.SHARED_DATA | DBConstants.USER_DATA)) != DBConstants.SHARED_DATA)
    		return;		// DatabaseInfo would only exist for shared databases

    	RecordOwner recordOwner = null; // ((BaseApplication)this.getDatabaseOwner().getEnvironment().getDefaultApplication()).getMainTask().; // For now
        DatabaseInfo recDatabaseInfo = new DatabaseInfo();
        recDatabaseInfo.setTable(this.doMakeTable(recDatabaseInfo));    // This makes a jdbc table
        String dbName = this.getDatabaseName(true);
        if (dbName.endsWith(SHARED_SUFFIX))
        	dbName = dbName.substring(0, dbName.length() - SHARED_SUFFIX.length());	// Always
        else if (dbName.endsWith(USER_SUFFIX))
        	dbName = dbName.substring(0, dbName.length() - USER_SUFFIX.length());	// Never
        recDatabaseInfo.setDatabaseName(dbName);

        int iOldOpenMode = recDatabaseInfo.getOpenMode();
        if ((DBConstants.FALSE.equalsIgnoreCase(this.getProperty(DBConstants.LOAD_INITIAL_DATA)))
            || ((this.getDatabaseOwner() != null) && (DBConstants.FALSE.equalsIgnoreCase(this.getDatabaseOwner().getProperty(DBConstants.LOAD_INITIAL_DATA)))))   // Global switch
                recDatabaseInfo.setOpenMode(recDatabaseInfo.getOpenMode() | DBConstants.OPEN_DONT_CREATE); // Don't create dbinfo automatically

        recDatabaseInfo.init(recordOwner);

        recDatabaseInfo.setOpenMode(iOldOpenMode);

        if (recDatabaseInfo.getEditMode() == DBConstants.EDIT_CURRENT)
        {
            iStartID = (int)recDatabaseInfo.getField(DatabaseInfo.START_ID).getValue();
            if (!recDatabaseInfo.getField(DatabaseInfo.END_ID).isNull())
                this.addDatabaseProperty(ENDING_ID, Integer.toString((int)recDatabaseInfo.getField(DatabaseInfo.END_ID).getValue()));
            if (!recDatabaseInfo.getField(DatabaseInfo.BASE_DATABASE).isNull())
                this.addDatabaseProperty(BaseDatabase.BASE_DATABASE, recDatabaseInfo.getField(DatabaseInfo.BASE_DATABASE).toString());
        }
        if (iStartID <= 0)
            iStartID = 1;
        recDatabaseInfo.free();
        this.addDatabaseProperty(STARTING_ID, Integer.toString(iStartID));

        if (m_databaseBase == null) // Always
            if (this.getProperty(BASE_DATABASE) != null)
        {
            Map<String,Object> properties = new HashMap<String,Object>();
            properties.putAll(this.getProperties());
            properties.remove(BASE_DATABASE);
            properties.put(DBConstants.DB_USER_PREFIX, DBConstants.BLANK);
            properties.remove(DBConstants.SYSTEM_NAME);
            properties.put(DBConstants.MODE, DBConstants.BLANK);
            m_databaseBase = (BaseDatabase)m_databaseOwner.getDatabase(this.getProperty(BASE_DATABASE), this.getDatabaseType() & DBConstants.TABLE_MASK, properties);
        }
    }
    /**
     * Add this database property.
     * @param strKey
     * @param strValue
     */
    public void addDatabaseProperty(String strKey, String strValue)
    {
        this.setProperty(strKey, strValue);
        Environment env = (Environment)this.getDatabaseOwner().getEnvironment();
        if (env.getCachedDatabaseProperties(this.getDatabaseName(true)) != null)  // Always
            if (env.getCachedDatabaseProperties(this.getDatabaseName(true)) != Environment.DATABASE_DOESNT_EXIST)  // Always
                env.getCachedDatabaseProperties(this.getDatabaseName(true)).put(strKey, strValue);
    }
}
