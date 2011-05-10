package org.jbundle.thin.base.db;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * Constants.
 */
public interface Constant
{
    public static final String BLANK = "";  // Blank String
    public static final String EQUALS = "=";

    public static final int NORMAL_RETURN = 0;
    public static final int ERROR_RETURN = -1;
    /**
     * Default URL Encoding.
     */
    public final static String URL_ENCODING = "UTF-8";
    public final static String STRING_ENCODING = "UTF8";

    public static final String ROOT_PACKAGE = "org.jbundle.";  // Package prefix
    public static final String THIN_SUBPACKAGE = "thin.";   // All thin are in this package
    public static final String RES_SUBPACKAGE = "res.";     // All resources are in this package
}
