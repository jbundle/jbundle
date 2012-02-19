/*
 * RecordMessageConstants.java
 *
 * Created on September 20, 2001, 10:18 PM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.record;

import org.jbundle.base.model.DBConstants;

/**
 *
 * @author  don
 * @version 
 */
public interface RecordMessageConstants
{
    public static final String DB_NAME = "dbname";
    public static final String TABLE_NAME = "tablename";
    public static final String BOOKMARK = DBConstants.STRING_OBJECT_ID_HANDLE;

    public static final String DB_TYPE = "dbtype";

    /**
     * A bookmark can never be "0".
     */
    public final static String NO_BOOKMARK = "0";
}

