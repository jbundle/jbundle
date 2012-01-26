/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.model;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.thin.base.db.Params;

/**
 * Parameters - Keys used to retrieve values.
 */
public interface DBParams extends Params
{

    /**
     * This tells the Environment to free if the only apps left are system apps.
     */
    public static final String FREEIFDONE = "freeifdone";
    /**
     * A table type file is a small read-only lookup table that is usually read entirely. Such as a account type.
     */
    public static final String TABLE = Params.TABLE_PARAM;
    /**
     * A local type file is a (usually) read-only table that can be cached locally (and synced periodically) without problems.
     */
    public static final String LOCAL = "local";
    /**
     * A remote type file contains live data that should always be read from the source.
     */
    public static final String REMOTE = "remote";
    /**
     * JDBC Source type.
     */
    public static final String JDBC = "Jdbc";
    /**
     * Client db type.
     */
    public static final String CLIENT = "Client";
    /**
     * Net db type.
     */
    public static final String NET = "Net";
    /**
     * Net over proxy db type.
     */
    public static final String PROXY = "Proxy";
    /**
     * Net db type.
     */
    public static final String MEMORY = "Memory";
    /**
     * 
     */
    public static final String SERVLET = "servlet";
    /**
     * The default RMI resource name of the lock server.
     */
    public static final String DEFAULT_REMOTE_LOCK = "lockapp";

    public static final String BROWSER = "browser";
    public static final String OS = "os";
    public static final String COLUMNS = "columns";
    public static final String DATABASE = "database";
    public static final String RECORD = "record";
    public static final String CLASS = "class";
    public static final String VALUE = "value";
    public static final String MENUDESC = "menudesc";
    public static final String LINK = "link";         // Link to an outside site
    public static final String STYLESHEET = "stylesheet";
    public static final String XML = "xml";           // Html page
    public static final String BANNERS = "banners";
    public static final String FRAMES = "frames";
    public static final String JAVA = "java";
    public static final String HOME = "home";         // MenuConstants.HOME.toLowerCase();
    public static final String COMMAND = "command";   // Form, Grid, or screen type number
    public static final String JOB = "job";           // Command passed from last form next/prev/etc.
    public static final String PROCESS = "process";   // Process
    public static final String HEADER_OBJECT_ID = "headerObjectID"; // Header reference ID
    public static final String TIMESTAMP = "timestamp";
    public static final String HEADER_RECORD = "headerRecord";	// Name of the header record
    /**
     * An XSL template filename for cocoon.
     */
    public static final String TEMPLATE = "template";
    /**
     * An output filename for alternate printing destination.
     */
    public static final String FILEOUT = "fileout";
    /**
     * The raw data type requested (either JPG or table).
     */
    public static final String DATATYPE = "datatype";
    /**
     * Base URL.
     */
    public static final String BASE_URL = "baseURL";
    /**
     * Change the preferences.
     */
    public static final String PREFERENCES = "preferences";
    /**
     * Don't send table message back up to the client (Should be on for ClientTable).
     */
    public static final String SUPRESSREMOTEDBMESSAGES = "nodbmessages";
    /**
     * Should I send my record message to remote.
     */
    public static final String MESSAGES_TO_REMOTE = "MESSAGES_TO_REMOTE";
    /**
     * Should I create the remote filter?
     */
    public static final String CREATE_REMOTE_FILTER = "CREATE_REMOTE_FILTER";
    /**
     * Should I update the remote filter?
     */
    public static final String UPDATE_REMOTE_FILTER = "UPDATE_REMOTE_FILTER";
    /**
     * Should I be logging transactions?
     */
    public static final String LOG_TRXS = "logTrxs";
    
    public static final String BASE_APPLET = org.jbundle.thin.base.db.Constants.ROOT_PACKAGE + "thin.base.screen.BaseApplet";
    public static final String APPLICATION = "application";
}
