/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.menu;

/**
 * @(#)SMenuScreen.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * SMenuScreen - Set up a menu control.
 */
public class SMenuBar extends SBaseMenuBar
{

    /**
     * Constructor.
     */
    public SMenuBar()
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
    public SMenuBar(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
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
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Add the menu items to this frame.
     * @param sfView The screen to add these menu items to.
     * @return The new menu.
     */
    public Object addMenu(ScreenFieldView sfView)
    {
        char rgchShortcuts[] = new char[10];
        Object menubar = sfView.createMenu();

        sfView.addStandardMenu(menubar, ThinMenuConstants.FILE, rgchShortcuts);
        sfView.addStandardMenu(menubar, ThinMenuConstants.EDIT, rgchShortcuts);
        sfView.addStandardMenu(menubar, ThinMenuConstants.RECORD, rgchShortcuts);
        sfView.addStandardMenu(menubar, ThinMenuConstants.HELP, rgchShortcuts);
        return menubar;
    }
}
