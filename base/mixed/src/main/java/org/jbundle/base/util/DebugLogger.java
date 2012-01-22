/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.util;

/**
 * @(#)TableException.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.logging.Level;

import org.jbundle.base.model.DBConstants;

/**
 * Debug utilities (comment out in production version).
 */
public class DebugLogger extends Object
{
    
    /**
     * Constructor for standard errors.
     */
    public DebugLogger(int iError)
    {
        super();    // Get the error text, or make it up
    }
    /**
     * Print error.
     */
    public static void pl(String strError, String strDebugType, int iPriority)
    {
        Level level = Level.INFO;  //+ config
        if (iPriority == DBConstants.INFORMATION_MESSAGE)
            level = Level.INFO;
        else if (iPriority == DBConstants.WARNING_MESSAGE)
            level = Level.WARNING;
        else if (iPriority == DBConstants.ERROR_MESSAGE)
            level = Level.SEVERE;
        Utility.getLogger().log(level, strError);
    }
}
