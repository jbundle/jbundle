/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.model;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.thin.base.db.Params;

/**
 * Standard SQL Types.
 */
public interface DBSQLTypes extends Params
{
    public static final String BOOLEAN = "BOOLEAN";
    public static final String BYTE = "BYTE";
    public static final String MEMO = "MEMO";
    public static final String CURRENCY = "CURRENCY";
    public static final String FLOAT = "FLOAT";

    public static final String INTEGER = "INTEGER";
    public static final String SHORT = "SHORT";
    public static final String DOUBLE = "DOUBLE";
    public static final String STRING = "STRING";
    public static final String SMALLINT = "SMALLINT";

    public static final String DATETIME = "DATETIME";
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";

    public static final String OBJECT = "OBJECT";
}
