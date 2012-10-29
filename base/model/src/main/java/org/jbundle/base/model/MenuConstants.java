/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.model;
import org.jbundle.thin.base.util.ThinMenuConstants;

/**
 * Constants to use in menus and buttons.
 */
public interface MenuConstants extends ThinMenuConstants
{
    //  The Following are Menu Items
    public static final String REFRESH = "Refresh";
    public static final String FORMLINK = "FormLink";
    public static final String LOOKUPCLONE = "LookupClone";
    public static final String FORMCLONE = "FormClone";
    public static final String POST = "Post";
    public static final String RUN = "Run";
    public static final String REQUERY = "Requery";
    public static final String MENUREC = "Menurec";     // Menu Record
    public static final String DISPLAY = "Display";
    public static final String DISTRIBUTION = "Distribution";
    public static final String GROUP = "Group";

    public static final String CLONE = "Clone";
    public static final String NEW_WINDOW = "NewWindow";
    
    public static final String REGSEPARATOR = "\\";     // Separator used for registration (change to ".")

    public static final int HELP_SAME_WINDOW = ScreenConstants.USE_SAME_WINDOW;	// Default
    public static final int HELP_NEW_WINDOW = ScreenConstants.USE_NEW_WINDOW;
}
