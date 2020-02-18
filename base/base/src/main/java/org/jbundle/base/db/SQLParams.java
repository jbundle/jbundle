/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */

import org.jbundle.base.model.DBConstants;


/**
 * DBConstants - Database constants.
 */
public interface SQLParams
{
    /**
     * Static flag: false: UPDATE SET X=? ; true: UPDATE SET X=4.2;.
     */
    public final static boolean USE_INSERT_UPDATE_LITERALS = false;
    /**
     * Static flag: false: UPDATE SET X=? ; true: UPDATE SET X=4.2.
     */
    public final static boolean USE_SELECT_LITERALS = false;
    /**
     * Is edit supported.
     */
    public static final String EDIT_DB_PROPERTY = "EditSupport";
    /**
     * If not supported (any other = true).
     */
    public static final String DB_EDIT_NOT_SUPPORTED = DBConstants.FALSE;
    /**
     * Auto sequence enabled.
     */
    public static final String AUTO_SEQUENCE_ENABLED = "AUTO_SEQUENCE_ENABLED";
    /**
     * ie., COUNTER (on Access).
     */
    public static final String AUTO_SEQUENCE_TYPE = "AUTO_SEQUENCE_TYPE";
    /**
     * ie., AUTOSEQUENCE (on other DBs).
     */
    public static final String AUTO_SEQUENCE_EXTRA = "AUTO_SEQUENCE_EXTRA";
    /**
     * ie., CONSTRAINT (on other DBs).
     */
    public static final String AUTO_SEQUENCE_PRIMARY_KEY = "AUTO_SEQUENCE_PRIMARY_KEY";
    /**
     * Counter java class (default = "java.lang.Integer")
     */
    public static final String COUNTER_OBJECT_CLASS = "COUNTER_OBJECT_CLASS";
    /**
     * Counter field name in external database (default = "ID")
     */
    public static final String ALTERNATE_COUNTER_NAME = "ALTERNATE_COUNTER_NAME";
    /**
     * ie., NOT NULL (on other DBs).
     */
    public static final String COUNTER_EXTRA = "COUNTER_EXTRA";
    /**
     * Set the new auto-sequence start value.
     */
    public static final String RESET_AUTO_SEQUENCE = "RESET_AUTO_SEQUENCE";
    /**
     * Use built in queries instead of complex sql queries?
     */
    public static final String USE_BUILT_IN_QUERIES = "USE_BUILT_IN_QUERIES";
    /**
     * Do BLOB transfers for large strings.
     */
    public static final String USE_BLOB_ON_LARGE_STRINGS = "USE_BLOB_ON_LARGE_STRINGS";
    /**
     * Error code for table not found (so I know when to create a new table).
     */
    public static final String TABLE_NOT_FOUND_ERROR_CODE = "TABLE_NOT_FOUND_ERROR_CODE";
    /**
     * Error text for table not found.
     */
    public static final String TABLE_NOT_FOUND_ERROR_TEXT = "TABLE_NOT_FOUND_ERROR_TEXT";
    /**
     * Error text for database not found.
     */
    public static final String DB_NOT_FOUND_ERROR_TEXT = "DB_NOT_FOUND_ERROR_TEXT";
    /**
     * Error code for table not found (so I know when to create a new table).
     */
    public static final String DB_NOT_FOUND_ERROR_CODE = "DB_NOT_FOUND_ERROR_CODE";
    /**
     * Error code for duplicate key.
     */
    public static final String DUPLICATE_KEY_ERROR_CODE = "DUPLICATE_KEY_ERROR_CODE";
    /**
     * Error text for duplicate key.
     */
    public static final String DUPLICATE_KEY_ERROR_TEXT = "DUPLICATE_KEY_ERROR_TEXT";
    /**
     * Error code for key not found.
     */
    public static final String KEY_NOT_FOUND_ERROR_CODE = "KEY_NOT_FOUND_ERROR_CODE";
    /**
     * Error text for key not found.
     */
    public static final String KEY_NOT_FOUND_ERROR_TEXT = "KEY_NOT_FOUND_ERROR_TEXT";
    /**
     * Error code for record locked.
     */
    public static final String RECORD_LOCKED_ERROR_CODE = "RECORD_LOCKED_ERROR_CODE";
    /**
     * Error test for record locked.
     */
    public static final String RECORD_LOCKED_ERROR_TEXT = "RECORD_LOCKED_ERROR_TEXT";
    /**
     * Error code for broken pipe (so I can try to create a new pipe).
     */
    public static final String BROKEN_PIPE_ERROR_CODE = "BROKEN_PIPE_ERROR_CODE";
    /**
     * Error Text for broken pipe.
     */
    public static final String BROKEN_PIPE_ERROR_TEXT = "BROKEN_PIPE_ERROR_TEXT";
    /**
     * Is SQL Bit type supported.
     */
    public static final String BIT_TYPE_SUPPORTED = "BIT_TYPE_SUPPORTED";
    /**
     * HACK for MS Access.
     */
    public static final String FLOAT_XFER_SUPPORTED = "FLOAT_XFER_SUPPORTED";
    /**
     * HACK for MS Access.
     */
    public static final String NULL_TIMESTAMP_SUPPORTED = "NULL_TIMESTAMP_SUPPORTED";
    /**
     * True if key names must be unique in a database.
     */
    public static final String NO_DUPLICATE_KEY_NAMES = "NO_DUPLICATE_KEY_NAMES";
    /**
     * True if key names must be unique in a database.
     */
    public static final String MAX_KEY_NAME_LENGTH = "MAX_KEY_NAME_LENGTH";
    /**
     * Can't use prepared statements on create.
     */
    public static final String NO_PREPARED_STATEMENTS_ON_CREATE = "NO_PREPARED_STATEMENTS_ON_CREATE";
    /**
     * True if there is no support for null keys.
     */
    public static final String NO_NULL_KEY_SUPPORT = "NO_NULL_KEY_SUPPORT";
    /**
     * Fields are not allowed to be (SQL) not null.
     * This is rare, but is used when creating locale tables, since most of the fields can be null to indicate use of the main field data.
     */
    public static final String NO_NULL_FIELD_SUPPORT = "NO_NULL_FIELD_SUPPORT";
    /**
     * True if a null value in a unique key will produce an duplicate key error.
     * Got that, unique key with just a code in the key will produce and error if there are two null codes.
     * So, if this is true, I will make the key not-unique. Remember to always check the code key on write!
     */
    public static final String NO_NULL_UNIQUE_KEYS = "NO_NULL_UNIQUE_KEYS";
    /**
     * Are joins supported (default = true)
     */
    public static final String SQL_JOINS_SUPPORTED = "SQL_JOINS_SUPPORTED";
    /**
     * Are The multi-table (object) sql views built?
     */
    public static final String SQL_VIEWS_BUILT = "SQL_VIEWS_BUILT";
    /**
     * SQL Memo field.
     */
    public static final String SQL_MEMO_FIELD = "SQL_MEMO_FIELD";
    /**
     * SQL Date format.
     */
    public static final String SQL_DATE_FORMAT = "SQL_DATE_FORMAT";
    /**
     * SQL Time format.
     */
    public static final String SQL_TIME_FORMAT = "SQL_TIME_FORMAT";
    /**
     * SQL DateTime format.
     */
    public static final String SQL_DATETIME_FORMAT = "SQL_DATETIME_FORMAT";
    /**
     * SQL Date quote (if different from #).
     */
    public static final String SQL_DATE_QUOTE = "SQL_DATE_QUOTE";
    /**
     * SQL create primary index string.
     */
    public static final String CREATE_PRIMARY_INDEX = "CREATE_PRIMARY_INDEX";
    /**
     * SQL create other index format string.
     */
    public static final String CREATE_INDEX = "CREATE_INDEX";
    /**
     * SQL alternate key field definition key (if create multipe=key index isn't supported).
     */
    public static final String ALT_SECONDARY_INDEX = "ALT_SECONDARY_INDEX";
    /**
     * Share the JDBC Connection - HACK for Pointbase.
     */
    public static final String SHARE_JDBC_CONNECTION = "SHARE_JDBC_CONNECTION";
    /**
     * Auto commit JDBC transactions (defaults to true).
     */
    public static final String AUTO_COMMIT_PARAM = "autoCommit";
    /**
     * Name of the JDBC database.
     */
    public static final String DATABASE_PRODUCT_PARAM = "databaseproduct";
    /**
     * The internal database name - do not set this param.
     */
    public static final String INTERNAL_DB_NAME = "internaldbname";
    /**
     * The internal database name - do not set this param.
     */
    public static final String CREATE_DATABASE_SUPPORTED = "createDBSupported";
    /**
     * Can I rename a table (using RENAME TABLE X TO Y;)
     */
    public static final String RENAME_TABLE_SUPPORT = "renameTableSupport";
    /**
     * The JDBC driver class.
     */
    public static final String JDBC_DRIVER_PARAM = "jdbcdriver";
    /**
     * The DataSource for the JDBC connection.
     */
    public static final String DATASOURCE_PARAM = "datasource";
    /**
     * The URL for the JDBC connection.
     */
    public static final String DB_SERVER_PARAM = "dbserver";
    /**
     * The URL for the JDBC connection.
     */
    public static final String JDBC_URL_PARAM = "jdbcurl";
    /**
     * The db username.
     */
    public static final String USERNAME_PARAM = "username";
    /**
     * The db password.
     */
    public static final String PASSWORD_PARAM = "password";
    /**
     * The DataSource factory class name.
     */
    public static final String DATASOURCE_FACTORY = "datasourceFactory";
    /**
     * The DataSource for the JDBC connection.
     */
    public static final String DEFAULT_DATASOURCE_PARAM = "defaultdatasource";
    /**
     * The URL for the JDBC connection.
     */
    public static final String DEFAULT_DB_SERVER_PARAM = "defaultdbserver";
    /**
     * The URL for the JDBC connection.
     */
    public static final String DEFAULT_JDBC_URL_PARAM = "defaultjdbcurl";
    /**
     * The db username.
     */
    public static final String DEFAULT_USERNAME_PARAM = "defaultusername";
    /**
     * The db password.
     */
    public static final String DEFAULT_PASSWORD_PARAM = "defaultpassword";
    /**
     * The DataSource for the JDBC connection.
     */
    public static final String DEFAULT_DATASOURCE = "jdbc/{dbname}";
}
