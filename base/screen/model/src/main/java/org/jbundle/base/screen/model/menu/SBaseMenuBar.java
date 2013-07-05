/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.menu;

/**
 * @(#)SBaseMenuBar.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;
import java.util.ResourceBundle;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ResourceConstants;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.screen.model.AppletScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * The basic menu control.
 */
public class SBaseMenuBar extends BasePanel
{
    /**
     * The resources for local string lookup.
     */
    protected ResourceBundle m_resources = null;
    
    /**
     * Constructor.
     */
    public SBaseMenuBar()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public SBaseMenuBar(ScreenLocation itsLocation,BasePanel parentScreen,Converter fieldConverter,int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        m_resources = null;
        BasePanel myParentScreen = parentScreen;
        while ((!(myParentScreen instanceof AppletScreen)) && (myParentScreen != null))
            myParentScreen = myParentScreen.getParentScreen();      // Always add toolbars to AppletScreen
        if (myParentScreen == null)
            myParentScreen = parentScreen;  // Only in HTML screens

        super.init(itsLocation, myParentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * free.
     */
    public void free()
    {
        m_resources = null;
        super.free();
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        int iErrorCode = DBConstants.NORMAL_RETURN; 
        return iErrorCode;
    }
    /**
     * Throw up a Toolbar if no toolbars yet.
     * @return The new toolbar screen (none for a BaseMenuBar).
     */
    public ToolScreen addToolbars()
    {   // Obviously, a menu shouldn't add a toolbar!
        return null;
    }
    /**
     * Create the description for this control.
     * (None on Toolbars)
     */   
    public void setupControlDesc()
    {
    }
    /**
     * Setup Screen Fields - Override this to add your tool buttons.
     */
    public void setupSFields()
    {
    }
    /**
     * Add the menu items to this frame.
     * @return The new menu.
     */
    public Object addMenu(ScreenFieldView sfView)
    {
        char rgchShortcuts[] = new char[10];
        Object menubar = sfView.createMenu();

        sfView.addStandardMenu(menubar, ThinMenuConstants.FILE, rgchShortcuts);
        sfView.addStandardMenu(menubar, ThinMenuConstants.EDIT, rgchShortcuts);
        sfView.addStandardMenu(menubar, ThinMenuConstants.HELP, rgchShortcuts);

        return menubar;
    }
    /**
     * Get the Menu resource object.
     * @param strKey The key to lookup.
     * @return The local string.
     */
    public String getString(String strKey)
    {
        if (m_resources == null)
        {
            BaseApplication application = (BaseApplication)this.getTask().getApplication();
            m_resources = application.getResources(ResourceConstants.MENU_RESOURCE, true);
        }
        return m_resources.getString(strKey);
    }
}
