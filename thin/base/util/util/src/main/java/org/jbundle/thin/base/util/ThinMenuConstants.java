/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.util;

import org.jbundle.thin.base.db.Constants;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */

/**
 * Constants to use in menus and buttons.
 */
public interface ThinMenuConstants
{
    //  The Following are LynxFrame Menu Items
    public static final String CUT = "Cut";
    public static final String COPY = "Copy";
    public static final String PASTE = "Paste";
    public static final String UNDO = "Undo";
    public static final String PREFERENCES = "Preferences";   // Screen preferences
    public static final String FIRST = "First";
    public static final String PREVIOUS = "Prev";
    public static final String NEXT = "Next";
    public static final String LAST = "Last";
    public static final String SUBMIT = Constants.SUBMIT;
    public static final String RESET = Constants.RESET;
    public static final String CANCEL = "Cancel";
    public static final String DELETE = Constants.DELETE;
    public static final String LOOKUP = "Lookup";
    public static final String FORM = Constants.FORM;
    public static final String FORMDETAIL = "Detail";
    public static final String GRID = Constants.GRID;   // Menu Record
    public static final String BACK = Constants.BACK;
    public static final String PRINT = "Print";
    public static final String CLOSE = Constants.CLOSE;
    public static final String HELP = Constants.HELP;
    public static final String SELECT = "Select";
    public static final String SELECT_DONT_CLOSE = SELECT + "dontClose";
    public static final String LOGIN = "Login";	// Login using these params
    public static final String LOGON = "Logon";    // Display the logon screen.
    public static final String LOGOUT = "Logout";
    public static final String CHANGE_PASSWORD = "Change";
    public static final String CREATE_NEW_USER = "CreateNewUser";
    public static final String SETTINGS = "Settings";
    public static final String HOME = "Home";

    public static final String WARNING = "Warning";
    public static final String INFORMATION = "Information";
    public static final String ERROR = "Error";
    public static final String WAIT = "Wait";

    public static final String FILE = "File";
    public static final String EDIT = "Edit";
    public static final String RECORD = "Record";
    public static final String ABOUT = "About";
    public static final String HELP_MENU = "HelpMenu";

    public static final String CALENDAR = "Calendar";
    public static final String DATE = "Date";
    public static final String TIME = "Time";
    // These are preferences for displaying the help screen
    public static final String HELP_PANE = "pane";
    public static final String HELP_WEB = "web";
    public static final String HELP_WINDOW = "window";
    public static final String USER_HELP_DISPLAY = "userHelpDisplay";
    public static final String HELP_DISPLAY = "helpDisplay";
    public static final int HELP_PANE_OPTION = 4;
    public static final int HELP_WEB_OPTION = 8;
    public static final int HELP_WINDOW_OPTION = 16;
    public static final int HELP_WINDOW_CHANGE = 32;	// Window change event
    public static final int EXTERNAL_LINK = 64;         // Browser change
    public static final int CHANGE_BROWSER_SCREEN = 128;         // Browser change
}
