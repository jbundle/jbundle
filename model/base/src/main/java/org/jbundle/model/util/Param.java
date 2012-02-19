/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.util;

/**
 * @(#)Params.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */

/**
 * Parameter keys to set for an application or applet.
 */
public interface Param
{
    /**
     * Task
     */
    public static final String TASK = "task";
    /**
     * Applet page
     */
    public static final String APPLET = "applet";
    /**
     * The current language.
     */
    public static final String LANGUAGE = "language";
    /**
     * The Application name (for name lookup).
     */
    public static final String APP_NAME = "appname";
    /**
     * The remote application name I should connect to.
     */
    public static final String REMOTE_APP_NAME = "remoteappname";
}
