/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.menu;

/**
 * @(#)SGridMenuBar.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.model.MenuConstants;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * The menus for a grid screen.
 */
public class SGridMenuBar extends SBaseMenuBar
{

    /**
     * Constructor.
     */
    public SGridMenuBar()
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
    public SGridMenuBar(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
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
     * @param sfView The screenfield to add the menu to.
     * @return The new menu.
     */
    public Object addMenu(ScreenFieldView sfView)
    {
        char rgchShortcuts[] = new char[30];
        Object menubar = sfView.createMenu();

        sfView.addStandardMenu(menubar, MenuConstants.FILE, rgchShortcuts);
        sfView.addStandardMenu(menubar, MenuConstants.EDIT, rgchShortcuts);
        Object menu = null;
        menu = sfView.addStandardMenu(menubar, MenuConstants.RECORD, rgchShortcuts);
        //?menu.addSeparator();
        sfView.addMenuItem(menu, this.getString(MenuConstants.REQUERY));
//+        menuItem.setIcon(this.loadImageIcon(MenuConstants.REQUERY, null));

        sfView.addStandardMenu(menubar, MenuConstants.HELP, rgchShortcuts);
        return menubar;
    }
}
