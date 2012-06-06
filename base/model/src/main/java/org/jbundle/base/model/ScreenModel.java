package org.jbundle.base.model;

import java.io.PrintWriter;

import org.jbundle.model.DBException;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.screen.ComponentParent;

public interface ScreenModel extends ComponentParent {
    public static final String VIEW_TYPE = "viewType";    
    public static final String HTML_TYPE = "html";
    public static final String XML_TYPE = "xml";
    public static final String SWING_TYPE = "swing";

    public static final String DOJO_TYPE = "dojo";
    public static final String BASE_PACKAGE = "org.jbundle.base.screen.model.";

    public static final String CALENDAR_SCREEN = "CalendarScreen";
    public static final String DETAIL_GRID_SCREEN = "DetailGridScreen";
    public static final String FRAME_SCREEN = "FrameScreen";
    public static final String GRID_SCREEN = "GridScreen";
    public static final String BASE_MENU_SCREEN = "BaseMenuScreen";
    public static final String MENU_SCREEN = "MenuScreen";
    public static final String BUTTON_BOX = "SButtonBox";
    public static final String CANNED_BOX = "SCannedBox";
    public static final String CHECK_BOX = "SCheckBox";
    public static final String COMBO_BOX = "SComboBox";
    public static final String SCREEN = "Screen";
    public static final String EDIT_TEXT = "SEditText";
    public static final String FAKE_SCREEN_FIELD = "SFakeScreenField";
    public static final String HTML_VIEW = "SHtmlView";
    public static final String IMAGE_VIEW = "SImageView";
    public static final String MENU_BUTTON = "SMenuButton";
    public static final String NUMBER_TEXT = "SNumberText";
    public static final String PASSWORD_FIELD = "SPasswordField";
    public static final String POPUP_BOX = "SPopupBox";
    public static final String RADIO_BUTTON = "SRadioButton";
    public static final String STATIC_STRING = "SStaticString";
    public static final String STATIC_TEXT = "SStaticText";
    public static final String TE_VIEW = "STEView";
    public static final String THREE_STATE_CHECK_BOX = "SThreeStateCheckBox";
    public static final String TOGGLE_BUTTON = "SToggleButton";
    public static final String TREE_CTRL = "STreeCtrl";
    public static final String TOOL_SCREEN = "ToolScreen";
    public static final String TOP_SCREEN = "TopScreen";
    
    public static final String LOCATION = "location";
    public static final String DISPLAY = "display";
    
    public static final String DISPLAY_STRING = "displayString";
    public static final String RECORD = "record";
    public static final String COMMAND = "command";
    public static final String IMAGE = "image";
    public static final String VALUE = "value";
    public static final String DESC = "description";
    public static final String TOOLTIP = "tooltip";
    public static final String FIELD = "field";
    public static final String NEVER_DISABLE = "neverDisable";    
    /**
     * Constants.
     */
    public static final String MAIL = "Mail";
    public static final String EMAIL = "EMail";
    public static final String PHONE = "Phone";
    public static final String FAX = "Fax";
    public static final String URL = "URL";
    /**
     * The name of the open button.
     */
    public static final String OPEN = "Open";
    public static final String EDIT = "Edit";
    public static final String CLEAR = "Clear";
    public static final String NONE = "None";

    /**
     * Output this complete report to the output file.
     * Display the headers, etc. then:
     * <ol>
     * - Parse any parameters passed in and set the field values.
     * - Process any command (such as move=Next).
     * - Render this screen using the view (by calling printxxmlScreen()).
     * </ol>
     * @param out The output stream.
     * @exception DBException File exception.
     */
    public void printReport(PrintWriter out) throws DBException;
    /**
     * Get the screen model for this view.
     * @return The screen view.
     */
    public ScreenFieldView getScreenFieldView();
    /**
     * Make sure I am allowed access to this screen.
     * @param strClassResource
     * @return
     */
    public ScreenModel checkSecurity(ScreenModel screen, ScreenModel parentScreen);
    /**
     * From these parameters, create the correct new screen.
     * @param screen The current sub-screen
     * @param propertyOwner The property owner
     * @return The new screen
     */
    public ScreenModel getScreen(ScreenModel screen, PropertyOwner propertyOwner);
    /**
     * Do the special HTML command.
     * This gives the screen a chance to change screens for special HTML commands.
     * You have a chance to change two things:
     * 1. The information display line (this will display on the next screen... ie., submit was successful)
     * 2. The error display line (if there was an error)
     * @return this or the new screen to display.
     */
    public ScreenModel doServletCommand(ScreenModel screenParent);
    
}
