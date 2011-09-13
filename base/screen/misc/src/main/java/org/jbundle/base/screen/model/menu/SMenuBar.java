/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.menu;

/**
 * @(#)SMenuScreen.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Dimension;

import javax.swing.JMenuBar;

import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.screen.view.ScreenFieldView;
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
    public SMenuBar(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Add the menu items to this frame.
     * @param sfView The screen to add these menu items to.
     * @return The new menu.
     */
    public JMenuBar addMenu(ScreenFieldView sfView)
    {
        char rgchShortcuts[] = new char[10];
        JMenuBar menubar = new JMenuBar()
        {
        	private static final long serialVersionUID = 1L;
            public Dimension getMaximumSize()
            {   // HACK - Not sure why menu takes up 1/2 of screen...?
                return new Dimension(super.getMaximumSize().width, super.getPreferredSize().height);
            }
        };
        menubar.setBorderPainted(false);
        menubar.setOpaque(false);
        menubar.add(sfView.addStandardMenu(ThinMenuConstants.FILE, rgchShortcuts));
        menubar.add(sfView.addStandardMenu(ThinMenuConstants.EDIT, rgchShortcuts));
        menubar.add(sfView.addStandardMenu(ThinMenuConstants.RECORD, rgchShortcuts));
        menubar.add(sfView.addStandardMenu(ThinMenuConstants.HELP, rgchShortcuts));
        return menubar;
    }
}
