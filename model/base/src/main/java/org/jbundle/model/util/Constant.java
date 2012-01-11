/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.util;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */

/**
 * Constants.
 */
public interface Constant
{
    public static final String BLANK = "";  // Blank String
    public static final String EQUALS = "=";
    /**
     * A new line.
     */
    public static final String RETURN = "\n";

    public static final int NORMAL_RETURN = 0;
    public static final int ERROR_RETURN = -1;

    public static final int LOCAL = 0;              // Local user or shared data (typically shared)
    public static final int REMOTE = 1;             // Remote user data (always user data)

    /**
     * No lock type.
     */
    public static final int OPEN_NO_LOCK_TYPE = 0;    
    /**
     * Don't lock, just always write with lastChangedDate=current, if different, refresh, merge and re-write.
     */
    public static final int OPEN_LAST_MOD_LOCK_TYPE = 16384;
    
    // setEditMode()
    public static final int EDIT_NONE = 0;      // Unknown status
    public static final int EDIT_ADD = 1;   // Record is new
    public static final int EDIT_IN_PROGRESS = 2; // Record is locked
    public static final int EDIT_CURRENT = 3; // Record has been read but not locked

    // Log levels
    public static final int DEBUG_INFO = 1;
    public static final int INFORMATION = 2;
    public static final int WAIT = 3; // Cursor.WAIT_CURSOR;  // 3 (Am I lucky or what?)
    public static final int WARNING = 4;
    public static final int ERROR = 5;     // These are copied from log4j
    /**
     * Default URL Encoding.
     */
    public final static String URL_ENCODING = "UTF-8";
    public final static String STRING_ENCODING = "UTF8";
    /**
     * The byte to char and back encoding that I use.
     */
    public static final String OBJECT_ENCODING = "ISO-8859-1";

    public static final String ROOT_PACKAGE = "org.jbundle.";  // Package prefix
    public static final String THIN_SUBPACKAGE = "thin.";   // All thin are in this package
    public static final String RES_SUBPACKAGE = "res.";     // All resources are in this package

    public static final int FIRST_YEAR = 1970;  // 1970
    public static final long KMS_IN_A_DAY = 24 * 60 * 60 * 1000;    // Milliseconds in a day

    public final static String FALSE = "false";     // If not supported (any other = true)
    public final static String TRUE = "true";   // If not supported (any other = true)

    public static final int DATE_ONLY = 3;          // Same as DATE_ONLY_FORMAT in thick
    public static final int TIME_ONLY = 4;          // Same as TIME_ONLY_FORMAT in thick
    // Screen constants
    public static final int SCREEN_MOVE = 0;
    public static final int INIT_MOVE = 1;
    public static final int READ_MOVE = 2;      // BaseField listener move modes
    public static final int DEFAULT_FIELD_LENGTH = -1;

    public static final boolean DISPLAY = true;
    public static final boolean DONT_DISPLAY = false;
}
