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

    // setEditMode()
    public static final int EDIT_NONE = 0;      // Unknown status
    public static final int EDIT_ADD = 1;   // Record is new
    public static final int EDIT_IN_PROGRESS = 2; // Record is locked
    public static final int EDIT_CURRENT = 3; // Record has been read but not locked

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
}
