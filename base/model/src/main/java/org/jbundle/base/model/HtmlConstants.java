/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.model;

import org.jbundle.thin.base.db.Constants;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */

/**
 * HtmlConstants - Screen constants.
 */
public interface HtmlConstants
{
// HtmlConstants
    public static final String HTML_RESOURCE = "Html";  // Name of the main HTML Resource
    public static final String XML_RESOURCE = "Xml";  // Name of the main XML Resource

    public static final String MENU_TAG = "<menu/>";
    public static final String RECORD_TAG = "<record/>";
    public static final String SCREEN_TAG = "<screen/>";
    public static final String URL_TAG = "<url/>";      // Current URL screen tag
    public static final String MENU_DESC_TAG = "<menudesc/>";
    public static final String HELP_TAG = "<help/>";
    public static final String HOME_TAG = "<home/>";
    public static final String LINK_TAG = "<link/>";
    public static final String ICON_TAG = "<icon/>";
    public static final String TITLE_TAG = "<menutitle/>";  // <menutitle/>
    public static final String USER_NAME_TAG = "<user/>";
    public static final String LOGIN_TAG = "<login/>";
    public static final String CLASS_TAG = "<class/>";
    public static final String SERVLET_PATH = Constants.DEFAULT_SERVLET;   // The servlet name (as aliased in the .ear file)
    public static final String SERVLET_LINK = "";   // The servlet name (as used in links) (= relative to current path)
    public static final String MAIN_MENU_KEY = "Main";  // Key value for default main menu
    public static final String HTML_ROOT = "";             // Prefix all HTML references with this char
    public static final String IMAGE_DIR = "images/"; // Location of images
    public static final String ICON_DIR = IMAGE_DIR + "icons/";   // sub-location of icons
    public static final String HELP_ICON = ICON_DIR + "Help.gif"; // Help icon - run program
    public static final String COOKIE_NAME = DBParams.USER_ID;   // HTML Cookie name

    /**
     * Applet width.
     */
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String ARCHIVE = "archive";
    public static final String ID = "id";
    public static final String CODEBASE = "codebase";
    public static final String JNLPEXTENSIONS = "jnlpextensions";
    public static final String JNLPJARS = "jnlpjars";
    public static final String DEFAULT_CODEBASE = ".";  // Default applet codebase
    public static final String NAME = "name";  // applet name param
    public static final String DEFAULT_NAME = "tourapp";  // Default applet name

    public static final boolean SHARED_SERVER = false;      // If you share a servlet engine, you can't save anything between sessions
        // Html Options
    public static final int PRINT_TOOLBAR_BEFORE = 1;
    public static final int PRINT_TOOLBAR_AFTER = 2;
    public static final int DONT_PRINT_SCREEN = 4;
        // Html control printing options
    public final static int HTML_DISPLAY = 0;
    public final static int HTML_INPUT = 32;        // Binary with HTML_DISPLAY
    public final static int HTML_ADD_DESC_COLUMN = 64;
    public final static int HTML_IN_TABLE_ROW = 128;    // Inside a table row (add the <td> tags).
    // Other
    public static final int HEADING_SCREEN = ScreenConstants.LAST_MODE * 2;
    public static final int FOOTING_SCREEN = ScreenConstants.LAST_MODE * 4;
    public static final int DETAIL_SCREEN = ScreenConstants.LAST_MODE * 32;         // Internal use only
    public static final int MAIN_HEADING_SCREEN = ScreenConstants.LAST_MODE * 8;    // Internal use only
    public static final int MAIN_FOOTING_SCREEN = ScreenConstants.LAST_MODE * 16;   // Internal use only
    public static final int MAIN_SCREEN = ScreenConstants.LAST_MODE * 64;           // Internal use only
    public static final int REPORT_SCREEN = ScreenConstants.LAST_MODE * 128;           // Internal use only
    
    public final static String FORMS = "forms";
    public final static String BOTH = "both";
    public final static String DATA = "data";
    public final static String INPUT = "input";
    public final static String DISPLAY = "display";
    public final static String BOTHIFDATA = "bothifdata";
    // HTML input types
    public final static String BUTTON = "button";
    public final static String SUBMIT = "submit";
}
