/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;

/**
 * @(#)VAppletScreen.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jbundle.base.screen.control.swing.SApplet;
import org.jbundle.base.screen.control.swing.util.ScreenInfo;
import org.jbundle.base.screen.model.AppletScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.HelpScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.screen.view.swing.report.VDualReportScreen;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.UserProperties;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.landf.ScreenDialog;
import org.jbundle.thin.base.screen.landf.ScreenUtil;
import org.jbundle.thin.base.screen.print.ScreenPrinter;
import org.jbundle.thin.base.screen.util.html.JHtmlEditor;
import org.jbundle.thin.base.screen.util.html.JHtmlView;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinMenuConstants;



/**
 * AppletScreen - Set up Application Screen.
 * <p/>Contains the main screen, toolbars, status bar(s), etc.
 */
public class VAppletScreen extends VBasePanel
{
    /**
     * The screen info such as fonts, sizes, colors, etc.
     */
    protected ScreenInfo m_ScreenInfo = null;

    /**
     * Constructor.
     */
    public VAppletScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VAppletScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenField model, boolean bEditableControl)
    {
        m_ScreenInfo = new ScreenInfo(null);    // One per applet screen
        super.init(model, bEditableControl);
        m_ScreenInfo.setControl((BaseApplet)this.getControl());
        if (this.getScreenField().getParentScreen() == null)
        {
            this.setControlExtent(new Rectangle(new Point(0, 0), ScreenConstants.PREFERRED_SCREEN_SIZE));
        }
    }
    /**
     * Free.
     */
    public void free()
    {
        if (m_ScreenInfo != null)
            m_ScreenInfo.free();
        m_ScreenInfo = null;
        if (this.getControl() != null)
        {
            SApplet applet = (SApplet)this.getControl();
            this.setControl(null);
            if (((AppletScreen)this.getScreenField()).getTask() == applet)
            {	// Always
            	applet.removeRecordOwner((AppletScreen)this.getScreenField());	// Make sure the applet doesn't free me again.
            	((AppletScreen)this.getScreenField()).setTask(null);
            }
            applet.free();  // Remove yourself!
        }
        super.free();
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control (The SApplet - which is a JApplet).
     */
    public Component setupControl(boolean bEditableControl)
    {
        SApplet control = null;
        if ((this.getScreenField() != null)
            && (((AppletScreen)this.getScreenField()).getTask() != null))
        {
            control = (SApplet)((AppletScreen)this.getScreenField()).getTask();
        }
        else
        {
            control = new SApplet();
            control.setScreenField((AppletScreen)this.getScreenField());
        }
        return control;
    }
    /**
     * Get one of the physical components associated with this SField.
     * @param int iLevel    CONTROL_TOP - Parent physical control; CONTROL_BOTTOM - Lowest child control
     * NOTE: This method is used for complex controls such as a scroll box, where this control must be
     * added to the parent, but sub-controls must be added to a lower level physical control.
     * @param iLevel The level for this control (top/bottom/etc).
     * @return The control for this view.
     */
    public Component getControl(int iLevel)
    {
        if (iLevel == DBConstants.CONTROL_BOTTOM)
            return ((SApplet)this.getControl()).getBottomPane();    // Make sure controls are not directly added to this JTable
        return super.getControl(iLevel);
    }
    /**
     * Setup this screen's screen layout.
     * Usually, you use JAVA layout managers, but you may also use ScreenLayout.
     * @return The new (Box) layout.
     */
    public LayoutManager addScreenLayout()
    {
        LayoutManager screenLayout = null;
        if (this.getScreenLayout() == null)   // Only if no parent screens
        {   // EVERY BasePanel gets a ScreenLayout!
            Container panel = (Container)this.getControl(DBConstants.CONTROL_BOTTOM);
            screenLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
            if ((panel != null) && (screenLayout != null))
                panel.setLayout(screenLayout);
        }
        return screenLayout;
    }
    /**
     * Get the screen information.
     * @return The screen info.
     */
    public ScreenInfo getScreenInfo()
    {
        return m_ScreenInfo;
    }
    /**
     * Get the current screen properties (in a properties table).
     * @return The properties (note: some of these are in native (non-string) form).
     */
    public Map<String,Object> getScreenProperties()
    {
        ScreenInfo screenInfo = this.getScreenInfo();
        if (screenInfo == null)
            return null;
        Map<String,Object> properties = new Hashtable<String,Object>();
        Font font = screenInfo.getFont();
        Color colorControl = screenInfo.getControlColor();
        Color colorText = screenInfo.getTextColor();
        BaseApplet applet = (BaseApplet)this.getControl();
        Color colorBackground = null;
        if (applet != null)
            colorBackground = applet.getBackgroundColor();
        ScreenUtil.setColor(ScreenUtil.BACKGROUND_COLOR, (ColorUIResource)colorBackground, null, properties);
        String lookAndFeelClassName = UIManager.getLookAndFeel().getClass().getName();
        String strTheme = null;
        if (MetalLookAndFeel.class.getName().equalsIgnoreCase(lookAndFeelClassName))
            strTheme = MetalLookAndFeel.getCurrentTheme().getName();
        
        ScreenUtil.setFont(font, null, properties);
        if (colorControl != null)
            colorControl = new ColorUIResource(colorControl);
        ScreenUtil.setColor(ScreenUtil.CONTROL_COLOR, (ColorUIResource)colorControl, null, properties);
        if (colorText != null)
            colorText = new ColorUIResource(colorText);
        ScreenUtil.setColor(ScreenUtil.TEXT_COLOR, (ColorUIResource)colorText, null, properties);
        if (lookAndFeelClassName != null)
            if ((!MetalLookAndFeel.class.getName().equalsIgnoreCase(lookAndFeelClassName)) || (strTheme != null))
                properties.put(ScreenUtil.LOOK_AND_FEEL, lookAndFeelClassName);
        if (strTheme != null)
            properties.put(ScreenUtil.THEME, strTheme);

        return properties;
    }
    /**
     * Change the screen properties to these properties.
     * @param propertyOwner The properties to change to.
     */
    public void setScreenProperties(PropertyOwner propertyOwner, Map<String,Object> properties)
    {
        ScreenInfo screenInfo = this.getScreenInfo();
        if (screenInfo != null)
            screenInfo.setScreenProperties(propertyOwner, properties);
        Frame frame = ScreenUtil.getFrame(this.getControl());
        ScreenUtil.updateLookAndFeel((Frame)frame, propertyOwner, properties);
        Color colorBackgroundNew = ScreenUtil.getColor(ScreenUtil.BACKGROUND_COLOR, propertyOwner, properties);
        if (colorBackgroundNew != null)
            ((BaseApplet)this.getControl()).setBackgroundColor(colorBackgroundNew);
        this.setControlAttributes(this.getControl(DBConstants.CONTROL_BOTTOM), true, false, false);

        ((AppletScreen)this.getScreenField()).resizeToContent(null);    // Re-layout this frame/screen
    }
    /**
     *  Throw up a dialog box to change the font.
     * @return True if successful.
     */
    public boolean onSetFont()
    {
        PropertyOwner propertyOwner = ((AppletScreen)this.getScreenField()).retrieveUserProperties(Params.SCREEN); // Global colors
        Map<String,Object> properties = null;
        if (propertyOwner instanceof UserProperties)
            properties = ((UserProperties)propertyOwner).getProperties();
        Frame frame = ScreenUtil.getFrame(this.getControl());
        ScreenDialog dialog = new ScreenDialog((Frame)frame, properties);
        ScreenUtil.centerDialogInFrame(dialog, (Frame)frame);
        
        dialog.setVisible(true);
        BaseApplication application = (BaseApplication)((AppletScreen)this.getScreenField()).getTask().getApplication();
        String strPreferences = application.getResources(ResourceConstants.MENU_RESOURCE, true).getString(MenuConstants.PREFERENCES);
        if (strPreferences != null)
            ;   // Do something!
        if (dialog.getReturnStatus() == JOptionPane.OK_OPTION)
        {
            properties = dialog.getProperties();
            propertyOwner.setProperties(properties);
            
            if (propertyOwner instanceof UserProperties)
            {   // Always
                // Now change any other screens to match.
                Application app = (Application)this.getScreenField().getRootScreen().getTask().getApplication();
                for (Task task : app.getTaskList().keySet())
                {
                    if (task instanceof BaseApplet)
                        ((BaseApplet)task).setScreenProperties(propertyOwner, null);                    
                }
                
                UserProperties registration = (UserProperties)propertyOwner;
                registration.free();    // Write the changes
                registration = null;
            }
        }
        return true;    // Command handled
    }
    /**
     * Process the command using this view.
     * @param strCommand The command to process.
     * @return True if processed.
     */
    public boolean doCommand(String strCommand)
    {
        if (strCommand.equalsIgnoreCase(ThinMenuConstants.PREFERENCES))
            return this.onSetFont();
        if (strCommand.equalsIgnoreCase(ThinMenuConstants.ABOUT))
        {
            SApplet applet = null;
            if (this.getScreenField() != null)
                applet = (SApplet)((AppletScreen)this.getScreenField()).getTask();
            if (applet != null)
                return applet.onAbout();
        }
        if (strCommand.equalsIgnoreCase(ThinMenuConstants.PRINT))
            return this.onPrint();
        return false; // Not processed, BasePanels and above will override
    }
    /**
     * Print the current screen.
     * @return true.
     */
    public boolean onPrint()
    {
        return ScreenPrinter.onPrint(this.getControl());
    }
    /**
     * Resurvey the child control(s) and resize frame.
     * @param strTitle The window title.
     */
    public void resizeToContent(String strTitle)
    {
        SApplet embeddedApplet = null;
        if (this.getScreenField() != null)
            embeddedApplet = (SApplet)((AppletScreen)this.getScreenField()).getTask();
        if ((embeddedApplet != null) && (BaseApplet.getSharedInstance().getApplet() == embeddedApplet))
        {   // Don't resize an applet, only applets in frames
            if (!embeddedApplet.isValid())
            {
                embeddedApplet.validate();  // Applet: force layout to run now that panels are correct
                embeddedApplet.repaint();
            }
            if (strTitle != null)
                embeddedApplet.setStatusText(strTitle);
        }
        else
            super.resizeToContent(strTitle);
        if (DBConstants.TRUE.equalsIgnoreCase(((AppletScreen)this.getScreenField()).getProperty("swingResizeHack")))
            SwingUtilities.invokeLater(new SwingResizeHack(embeddedApplet));
    }
    /**
     * Resize the applet after doing everything (for some weird reason when all the edit controls are disabled, layout doesn't work).
     * @author don
     */
    class SwingResizeHack extends Thread
    {
        BaseApplet m_frame = null;
        public SwingResizeHack(BaseApplet frame)
        {
            super();
            m_frame = frame;
        }
        public void run()
        {
            m_frame.invalidate();
            m_frame.validate();
            m_frame.repaint();
        }
    }
    /**
     * Setup a default task for this screen.
     * @param application The application to create the task under (if null, guesses).
     */
    public void setupDefaultTask(Application application)
    {
        SApplet applet = (SApplet)this.getControl();
        if (application == null)
            application = SApplet.getSharedInstance().getApplication();
        applet.setApplication(application);
        applet.init(null);
    }
    /**
     * This is a utility method to show an HTML page.
     * @param strURL The URL to display.
     */
    public boolean showDocument(String strURL, int iOptions)
    {
        SApplet applet = (SApplet)this.getControl();
        
        if ((iOptions & MenuConstants.HELP_WINDOW_CHANGE) != 0)
        {
        	if ((applet.getHelpView() != null)
//        		&& (applet.getHelpView().getBaseApplet() == this.getControl())
        			&& (applet.getHelpView().getHelpPane() != null)
            			&& (applet.getHelpView().getHelpPane().isVisible()))
				iOptions = MenuConstants.HELP_PANE_OPTION | MenuConstants.HELP_WINDOW_CHANGE;
        	else
        		return false;	// Don't refresh help if no current pane
        }
        
    	boolean bUseSameWindow = false;	// For now ((iOptions & 1) == MenuConstants.HELP_SAME_WINDOW);
        if ((MenuConstants.HELP_PANE_OPTION & iOptions) == MenuConstants.HELP_PANE_OPTION)
        {
            URL url = null;
            try {
            	url = new URL(strURL);
            } catch (MalformedURLException e) {
            	url = JHtmlView.getURLFromPath(strURL, applet);
            }
            if (url != null)
            {
            	applet.getHelpView().linkActivated(url, applet, Constants.DONT_PUSH_HISTORY);
            }
        }
        else if ((MenuConstants.HELP_WINDOW_OPTION & iOptions) == MenuConstants.HELP_WINDOW_OPTION)
        {	// Note: Move this to view, should not access SApplet in model.
            ScreenLocation itsLocation = null;
            if (applet.getHelpView() != null)
                if (applet.getHelpView().getBaseApplet() != applet)
            {	// Good a standalone help window already exists
                if (applet.getHelpView().getBaseApplet() instanceof SApplet)
                {
	            	SApplet helpApplet = (SApplet)applet.getHelpView().getBaseApplet();
	        		AppletScreen screen = helpApplet.getScreenField();
	        		for (int i = 0; i < screen.getSFieldCount(); i++)
	        		{
	        			ScreenField sField = screen.getSField(i);
	        			if (sField instanceof HelpScreen)
	        			{
	        	            URL url = null;
	        	            try {
	        	            	url = new URL(strURL);
	        	            } catch (MalformedURLException e) {
	        	            	url = JHtmlView.getURLFromPath(strURL, applet);
	        	            }
	        	            ((JHtmlEditor)sField.getScreenFieldView().getControl()).linkActivated(url, null, Constants.DONT_PUSH_HISTORY);
	        				return true	;	// Handled!
	        			}
	        		}
	        		// Hmmm, can't happen.
                }
                else
                {	// A thin help panel exists (can't happen?)
                }
        		// The help screen is the side pane, which I don't want to use, so create a new help screen.
            }
            BasePanel parentScreen = (AppletScreen)this.getScreenField();
            if (bUseSameWindow)
            {
                itsLocation = parentScreen.getScreenLocation();
                if (parentScreen != null)
                    this.free();    // Warning, this also closes "this" record.
            }
            else
                parentScreen = Screen.makeWindow(parentScreen.getTask().getApplication());
            HelpScreen screen = new HelpScreen();
            screen.setURL(strURL);
            try   {
                screen.init(null, itsLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
            } catch (Exception ex)  {
                ex.printStackTrace();
            }
            if (!bUseSameWindow)
            	applet.setHelpView((JHtmlEditor)((VDualReportScreen)screen.getScreenFieldView()).getControl());
        }
        else
        	return applet.showTheDocument(strURL, iOptions);
        return true;	// Handled
    }
    /**
     * Load this image.
     * @param filename The image file name.
     * @param description The image description.
     * @return The image icon.
     */
    public ImageIcon loadImageIcon(String filename, String description)
    {
        SApplet applet = (SApplet)this.getControl();
        return applet.loadImageIcon(filename, description);
    }
}
