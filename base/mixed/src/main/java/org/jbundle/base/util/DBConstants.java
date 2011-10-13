/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.util;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Calendar;

import org.jbundle.thin.base.db.Constants;


/**
 * DBConstants - Database constants.
 */
public interface DBConstants extends Constants
{
    /**
     * Nullable field.
     */
    public static final boolean NULL = true;
    /**
     * Not Nullable field.
     */
    public static final boolean NOT_NULL = false;
    /**
     * The FILE_KEY_AREA is the physical fields that belong to a key.
     * @see TEMP_KEY_AREA.
     */
    public static final int FILE_KEY_AREA = 0;
    /**
     * The START_SELECT_KEY is the starting fields that belong to a key.
     * @see TEMP_KEY_AREA.
     */
    public static final int START_SELECT_KEY = 2;
    /**
     * The END_SELECT_KEY are the ending fields that belong to a key.
     * @see TEMP_KEY_AREA.
     */
    public static final int END_SELECT_KEY = 3;
    // for BaseApplet.setStatusText(xxx)
    public static final int INFORMATION_MESSAGE = INFORMATION;  // Error levels
    public static final int WARNING_MESSAGE = WARNING;      // Default
    public static final int ERROR_MESSAGE = ERROR;
    /**
     * File error codes: (0 normal, -1 = default error, <-2 = use last error string)
     */
    public static final int FILE_NOT_OPEN = 6;
    public static final int FILE_ALREADY_OPEN = 7;
    public static final int FILE_TABLE_FULL = 8;
    public static final int BROKEN_PIPE = 9;
    public static final int FILE_NOT_FOUND = 13;
    public static final int FILE_ALREADY_EXISTS = 15;
    public static final int NULL_FIELD = 19;
    public static final int READ_NOT_NEW = 20;
    public static final int NO_ACTIVE_QUERY = 21;
    public static final int RECORD_NOT_LOCKED = 22;
    public static final int ERROR_STRING = -2;
    public static final int RETRY_ERROR = 24;
    public static final int ERROR_READ_ONLY = 25;
    public static final int ERROR_APPEND_ONLY = 26;
    public static final int DUPLICATE_COUNTER = 27;
    public static final int DB_NOT_FOUND = 28;
    public static final int NEXT_ERROR_CODE = 29;
// Physical record status
    public static final int RECORD_NORMAL = 0;
    public static final int RECORD_INVALID = 1;
    public static final int RECORD_NEW = 2;
    public static final int RECORD_AT_EOF = 4;      // At one greater than the last record
    public static final int RECORD_AT_BOF = 8;      // At one before the first record
    public static final int RECORD_EMPTY = RECORD_AT_EOF | RECORD_AT_BOF;
    public static final int RECORD_NEXT_PENDING = 16; // On a read next, just return (RECORD_NORMAL).
    public static final int RECORD_PREVIOUS_PENDING = 32; // On a read previous, just return (RECORD_NORMAL).
// SQL Constants
    public static final char SQL_START_QUOTE = '\'';
    public static final char SQL_END_QUOTE = '\'';
// setEditMode()
    public static final int EDIT_REFRESHED = 65536;   // Special mode - Refreshed
    public static final int EDIT_CLOSE_IN_FREE = 32768;   // Special mode - Closing for the last time (in .free() method)
// setOpenMode() (continued from thin).
    /**
     * Never cache recently used records.
     */
    public static final int OPEN_DONT_CACHE = 8;
    /*
     * Record constants - normal open.
     */
    public static final int OPEN_EXCLUSIVE = 16;
    /**
     * Don't create if not found.
     */
    public static final int OPEN_DONT_CREATE = 32;
    /**
     * Automatically lock record on data change.
     */
    public static final int OPEN_LOCK_ON_CHANGE_STRATEGY = 64;
    /**
     * Write, refresh and lock on data change.
     * If record current: locks, If the record is new: refreshes
     * the fields before the first field change. (For counter fields).
     */
    public static final int OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY = OPEN_LOCK_ON_CHANGE_STRATEGY | 128;
    /**
     * When you lock, instead of returning a code to do a refresh, throw an exception.
     */
    public static final int OPEN_EXCEPTION_ON_LOCK_TYPE = 32768;
    /**
     * If I lock and the record changed from when I read it, return a RECORD_CHANGED error.
     */
    public static final int OPEN_ERROR_ON_DIRTY_LOCK_TYPE = 65536;
    /**
     * Don't unlock records during the current record calls.
     */
    public static final int OPEN_DONT_CHANGE_CURRENT_LOCK_TYPE = 131072;
    /**
     * Mask for lock properties (and to keep lock properties).
     */
    public static final int LOCK_STRATEGY_MASK = OPEN_NO_LOCK_STRATEGY | OPEN_LOCK_ON_EDIT_STRATEGY | OPEN_LOCK_ON_CHANGE_STRATEGY | OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY | OPEN_MERGE_ON_LOCK_STRATEGY | OPEN_WAIT_FOR_LOCK_STRATEGY;
    /**
     * Mask for lock properties (and to keep lock properties).
     */
    public static final int LOCK_TYPE_MASK = OPEN_NO_LOCK_TYPE | OPEN_LAST_MOD_LOCK_TYPE | OPEN_EXCEPTION_ON_LOCK_TYPE | OPEN_ERROR_ON_DIRTY_LOCK_TYPE | OPEN_DONT_CHANGE_CURRENT_LOCK_TYPE;
    /**
     * Create database if not found
     */
    public static final String CREATE_DB_IF_NOT_FOUND = "createDBIfNotFound";
    /**
     * Load initial data on table create.
     */
    public static final String LOAD_INITIAL_DATA = "loadInitialData";
    /**
     * Load initial data on table create.
     */
    public static final String ARCHIVE_FOLDER = "archiveFolder";
    public static final String USER_ARCHIVE_FOLDER = "userArchiveFolder";
    public static final String SHARED_ARCHIVE_FOLDER = "sharedArchiveFolder";
    /**
     * Load initial data on table create.
     */
    public static final String DEFAULT_ARCHIVE_FOLDER = "{domain}res/data/initial_data/current_initial_data";
    /**
     * Base table access only - don't use hierarchical, etc (For export, import). Set to true.
     */
    public static final String BASE_TABLE_ONLY = "baseTableOnly";
    /**
     * Tables in this database are read-only.
     */
    public static final String READ_ONLY_DB = "readOnly";
    /**
     * Suppress AFTER_REQUERY_TYPE on open.
     */
    public static final int OPEN_SUPPRESS_MESSAGES = 262144;

    public final static int UNKNOWN_RECORD_COUNT = -1;

    public final static int LEFT_INNER = 1;     // Do not include records that do not have matches
    public final static int LEFT_OUTER = 2;     // Include null keys
//enum eChangeType {
    public static final int ADD_TYPE = AFTER_ADD_TYPE + 1;
    public static final int UPDATE_TYPE = ADD_TYPE + 1;
    public static final int DELETE_TYPE = UPDATE_TYPE + 1;
    public static final int LOCK_TYPE = DELETE_TYPE + 1;
    public static final int DESELECT_TYPE = LOCK_TYPE + 1;
    public static final int MOVE_NEXT_TYPE = DESELECT_TYPE + 1;
    public static final int SELECT_EOF_TYPE = MOVE_NEXT_TYPE + 1;
    public static final int FIELD_CHANGED_TYPE = SELECT_EOF_TYPE + 1;    // First field changed
    public static final int AFTER_REQUERY_TYPE = FIELD_CHANGED_TYPE + 1; // After a requery (new open)
    public static final int CONTROL_BREAK_TYPE = AFTER_REQUERY_TYPE + 1; // After a control break
    public static final int REFRESH_TYPE = CONTROL_BREAK_TYPE + 1;       // Before a record refresh
    public static final int AFTER_REFRESH_TYPE = REFRESH_TYPE + 1;       // After a record refresh
    public static final int BEFORE_FREE_TYPE = AFTER_REFRESH_TYPE + 1;   // Before a record free
    public static final int USER_DEFINED_TYPE = BEFORE_FREE_TYPE + 1;    // User defined
// Cool constants
    public static final short DATE_FORMAT = 0;
    public static final short TIME_FORMAT = 1;
    public static final short DATE_TIME_FORMAT = 2;
    public static final short DATE_ONLY_FORMAT = 3;
    public static final short TIME_ONLY_FORMAT = 4;
    public static final short SHORT_DATE_FORMAT = 5;
    public static final short SHORT_TIME_FORMAT = TIME_FORMAT;  // Time format is short
    public static final short SHORT_DATE_TIME_FORMAT = 7;
    public static final short SHORT_DATE_ONLY_FORMAT = 8;
    public static final short SHORT_TIME_ONLY_FORMAT = 9;
    public static final short SQL_TIME_FORMAT = 10;
    public static final short SQL_DATE_FORMAT = 11;
    public static final short SQL_DATE_TIME_FORMAT = 12;
    public static final short LONG_DATE_TIME_FORMAT = 13;
    public static final short LONG_TIME_ONLY_FORMAT = 14;
    public static final short HYBRID_DATE_TIME_FORMAT = 15;
    public static final short HYBRID_TIME_ONLY_FORMAT = 16;

    public static final int LAST_YEAR = 2069; // 2069 **?**
    public static final int FIRST_MONTH = Calendar.JANUARY;   // According to Java
    public static final int FIRST_DAY = 1;      // According to Java
    public static final long m_lTimeOffset = 0;   // For dates < 1970 offset to get after 1970
    public static final int m_iHours = 12;          // Default hour of the day
    public static final int m_iMinutes = 0;     // Default minute of the day
    public static final long ONE_MINUTE = 60 * 1000;
    public static final int HOUR_DATE_ONLY = 0; // Internal hour for date only fields
    public static final long FOUR_YEARS = (4 * 365 + 1) * KMS_IN_A_DAY;
    public static final int SPACE_CHAR = ' '; // ASCII Space
    public static final int COLON_CHAR = ':'; // ASCII Colon

    public static final boolean MOVE_TO_DEPENDENT = true;
    public static final boolean DONT_MOVE_TO_DEPENDENT = false;
    public static final boolean MOVE_DEPENDENT_BACK = true;
    public static final boolean DONT_MOVE_DEPENDENT_BACK = false;
    public static final boolean CLOSE_ON_FREE = true;
    public static final boolean DONT_CLOSE_ON_FREE = false;
    public static final boolean RESELECT_ON_CHANGE = true;
    public static final boolean DONT_RESELECT_ON_CHANGE = false;

    public static final int SELECT_FIRST_FIELD = 0;
    public static final int SELECT_NEXT_FIELD = +1;
    public static final int SELECT_PREV_FIELD = -1;
    
    // DBConstants
    /**
     * A bookmark handle is a  Native long bookmark, usually the counter field's data.
     * @see OBJECT_ID_HANDLE.
     * @see DATA_SOURCE_HANDLE.
     * @see OBJECT_SOURCE_HANDLE.
     * @see FULL_OBJECT_HANDLE.
     */
    public static final int BOOKMARK_HANDLE = 0;
    /**
     * A bookmark handle is a Handle in persistent store, usually the counter field's data.
     * @see BOOKMARK_HANDLE.
     * @see DATA_SOURCE_HANDLE.
     * @see OBJECT_SOURCE_HANDLE.
     * @see FULL_OBJECT_HANDLE.
     */
    public static final int OBJECT_ID_HANDLE = 1;
    /**
     * A bookmark handle is a Remote object reference, usually the data reference.
     * @see BOOKMARK_HANDLE.
     * @see OBJECT_ID_HANDLE.
     * @see OBJECT_SOURCE_HANDLE.
     * @see FULL_OBJECT_HANDLE.
     */
    public static final int DATA_SOURCE_HANDLE = 2;
    /**
     * A bookmark handle is a  Remote path; Can be used with kHandleObjectID, always serializable.
     * @see BOOKMARK_HANDLE.
     * @see OBJECT_ID_HANDLE.
     * @see DATA_SOURCE_HANDLE.
     * @see FULL_OBJECT_HANDLE.
     */
    public static final int OBJECT_SOURCE_HANDLE = 4;
    /**
     * A bookmark handle is a  Native long bookmark, usually the counter field's data.
     * @see BOOKMARK_HANDLE.
     * @see OBJECT_ID_HANDLE.
     * @see DATA_SOURCE_HANDLE.
     * @see OBJECT_SOURCE_HANDLE.
     */
    public static final int FULL_OBJECT_HANDLE = OBJECT_SOURCE_HANDLE | OBJECT_ID_HANDLE;
    public static final String STRING_OBJECT_ID_HANDLE = OBJECT_ID;     // Handle in persistent store
    public static final String STRING_OBJECT_SOURCE_HANDLE = "objectSource";    // Remote path; Can be used with kHandleObjectID
    public static final String STRING_FULL_OBJECT_HANDLE = "fullObjectID";
        // SQL update types
    public static final int SQL_SELECT_TYPE = 0;
    public static final int SQL_UPDATE_TYPE = 1;
    public static final int SQL_INSERT_TABLE_TYPE = 2;
    public static final int SQL_INSERT_VALUE_TYPE = 3;
    public static final int SQL_DELETE_TYPE = 4;
    public static final int SQL_CREATE_TYPE = 5;
    public static final int SQL_AUTOSEQUENCE_TYPE = 6;
    public static final int SQL_SEEK_TYPE = 7;
        // value for getControl(xxx) call
    public final static int CONTROL_BOTTOM = 0;  // Lowest child control
    public final static int CONTROL_TOP = 1;     // Parent physical control
    public final static int CONTROL_TO_FREE = 2; // Control to release the listeners on.

    public final String YES = "yes";    // If not supported (any other = true)
    public final String NO = "no";      // If not supported (any other = true)
    
    public static final String ANON_USER_ID = "1"; // Anonymous user ID
    /**
     * The default help (xml) file name.
     */
    public static final String DEFAULT_HELP_FILE = "docs/help/user/basic/index.xml";
    
    public static final String SAPPLET = ROOT_PACKAGE + "base.screen.control.swing.SApplet";  //org.jbundle.base.screen.control.swing.SApplet.class.getName();
}
