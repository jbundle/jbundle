/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.util;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */

/**
 * ScreenConstants - Screen constants.
 */
public interface ScreenConstants
{
// Screen constants
    // Display description constants. Display desc bits 0-2 (values 1-7)
    public static final int DISPLAY_DESC = 1;
    public static final int DISPLAY_FIELD_DESC = 1;
    public static final int DONT_DISPLAY_DESC = 2;
    public static final int DONT_DISPLAY_FIELD_DESC = 2;
    public static final int DEFAULT_DISPLAY = 4;
    // Output medium - Bits 3-4 (values 8,16,24) (For screens only)
    public static final String HTML_SCREEN_TYPE = "html"; // Sets up screen for HTML display
    // Document mode - Bits 8+ (values 256, 512, 1024, 2048, 4096) (For screens only)
    public static final int MENU_MODE = 256;        // Display file as a menu
    public static final int MAINT_MODE = 512;
    public static final int INPUT_TYPE = 512;   // SAME
    public static final int DISPLAY_TYPE = 1024;
    public static final int DISPLAY_MODE = 1024;    // SAME
    public static final int SELECT_MODE = DISPLAY_MODE | 2048;
    public static final int DETAIL_MODE = DISPLAY_MODE | 64;
    public static final int POST_MODE = DISPLAY_MODE | 128;
    public static final int SECURITY_MODE = MAINT_MODE | 2048;  // Special login screen.
    public static final int LAST_MODE = 2048;    // If mode is higher than this it is user defined.
// Command options
	public static final int USE_SAME_WINDOW = DBConstants.USE_SAME_WINDOW;
	public static final int USE_NEW_WINDOW = DBConstants.USE_NEW_WINDOW;
    public static final int PUSH_TO_BROSWER = DBConstants.PUSH_TO_BROSWER;
    public static final int DONT_PUSH_TO_BROSWER = DBConstants.DONT_PUSH_TO_BROSWER;

    public static final int DISPLAY_MASK = 7;
    public static final int SCREEN_TYPE_MASK = ~(DISPLAY_MASK | DONT_PUSH_TO_BROSWER | USE_NEW_WINDOW);
    public static final int DOC_MODE_MASK = ~31;
// GetNextLocation constants
    public static final short NEXT_LOGICAL = 1;
    public static final short RIGHT_OF_LAST = 2;
    public static final short BELOW_LAST = 3;
    public static final short TOP_NEXT = 4;
    public static final short FIELD_DESC = 5;
    public static final short POPUP_DESC = 6;
    public static final short CHECK_BOX_DESC = 7;
    public static final short AT_ANCHOR = 8;
    public static final short FLUSH_LEFT = 10;
    public static final short FLUSH_RIGHT = 11;
    public static final short CENTER = 12;
    public static final short FIRST_LOCATION = 13;
    public static final short FIRST_INPUT_LOCATION = 14;
    public static final short FIRST_DISPLAY_LOCATION = 15;
    public static final short RIGHT_WITH_DESC = 17;
    public static final short FIRST_FIELD_BUTTON_LOCATION = 18;
    public static final short ADD_VIEW_BUFFER = 19;
    public static final short ADD_SCREEN_VIEW_BUFFER = 20;
    public static final short RIGHT_OF_LAST_BUTTON = 21;
    public static final short RIGHT_OF_LAST_BUTTON_WITH_GAP = 22;
    public static final short RIGHT_OF_LAST_CHECKBOX = 27;
    public static final short FIRST_FRAME_LOCATION = 23;
    public static final short FIRST_SCREEN_LOCATION = 24;
    public static final short BELOW_LAST_CONTROL = 25;
    public static final short ADD_GRID_SCREEN_BUFFER = 26;
    public static final short BELOW_LAST_DESC = 29;   	// Below last, aligned with desc rather than control
    public static final short BELOW_LAST_ANCHOR = 31;
    public static final short NEXT_INPUT_LOCATION = 32;  // Below farthest field, at left input location
    public static final short LAST_LOCATION = 33;		// Very last control - typically a total on a screen

    public static final short kFirstColOffset = 4;  // start 5 pixels down on displays
    public static final short kFieldHorizOffset = 1;    // Extra pixels needed in each character cell
    public static final short kExtraColBoxSpacing = kFieldHorizOffset * 2 + 3;  // Extra pixels needed for a box
    public static final short kCellBorderWidth = 2;
    public static final short kShiftTextDown = -6;
    public static final short kShiftTextRight = +0;
    public static final short kShiftEditDown = -6;
    public static final short kShiftEditRight = +4;
//enum eAnchorFlag
    public static final short ANCHOR_DEFAULT = 1;
    public static final short SET_ANCHOR = 2;
    public static final short DONT_SET_ANCHOR = 3;
    public static final short FILL_REMAINDER = 4; // Grow the control to fill the parent screen

    public static final short kMaxSingleLineChars = 65;
    public static final short kMaxSingleChars = 50;
    public static final short kMaxSingleLines = 2;
    public static final short kMaxDoubleLineChars = kMaxSingleLineChars * 2;
    public static final short kMaxDoubleChars = kMaxSingleChars;
    public static final short kMaxDoubleLines = 3;
    public static final short kMaxEditLineChars = 128;  // Max line length before making a scrolling edit box
    
    public static final short kMaxDescLength = 14;
    public static final short kMaxTEChars = 50;
    public static final short kMaxTELines = 6;      // 50 characters by 6 lines
    public static final short kGridButtonOffset = 14; // Dimension of the grid button
    public static final short kInitialControl = -2;
    public static final short kSelectField = 0;
}
