/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing.menu;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.jbundle.base.model.MenuConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.menu.SBaseMenuBar;
import org.jbundle.base.screen.model.menu.SGridMenuBar;
import org.jbundle.base.screen.model.menu.SMenuBar;
import org.jbundle.base.screen.view.swing.VBasePanel;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.screen.action.ActionManager;



/**
 * The menu bar at the top of each frame.
 */
public class VBaseMenuBar extends VBasePanel
{

    /**
     * Constructor.
     */
    public VBaseMenuBar()
    {
        super();
    }
    /**
     * Constructor.
     */
    public VBaseMenuBar(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Setup the physical control.
     * Make sure the toolbar is at the top of the screen.
     */
    public Component setupControl(boolean bEditableControl)
    {
        if (this.getScreenField().getParentScreen() == null)
            return null;
        boolean bSuccess = this.getScreenField().getParentScreen().removeSField(this.getScreenField());   // Note: doesn't remove me because m_Control = null
        int iSFieldCount = this.getScreenField().getParentScreen().getSFieldCount();
        int iToolbarOrder = 0;
        for (iToolbarOrder = 0; iToolbarOrder < iSFieldCount; iToolbarOrder++)
        {
            if (!(this.getScreenField().getParentScreen().getSField(iToolbarOrder) instanceof SBaseMenuBar))
                break;  // Last toolbar
        }
        if (bSuccess)
            this.getScreenField().getParentScreen().addSField(this.getScreenField(), iToolbarOrder);    // Add this control to the beginning
    
        JMenuBar control = ((SBaseMenuBar)this.getScreenField()).addMenu(this);
        control.setAlignmentX(Component.LEFT_ALIGNMENT);
        control.setAlignmentY(Component.TOP_ALIGNMENT);
        control.setOpaque(false);
    
        return control;
    }
    /**
     * Add a standard top-level menu item and the standard detail actions.
     */
    public JMenu addStandardMenu(String strMenuName, char rgchShortcuts[])
    {
        if (MenuConstants.FILE.equalsIgnoreCase(strMenuName))
            return this.addFileMenu(rgchShortcuts);
        if (MenuConstants.EDIT.equalsIgnoreCase(strMenuName))
            return this.addEditMenu(rgchShortcuts);
        if (MenuConstants.RECORD.equalsIgnoreCase(strMenuName))
            return this.addRecordMenu(rgchShortcuts);
        if (MenuConstants.HELP.equalsIgnoreCase(strMenuName))
            return this.addHelpMenu(rgchShortcuts);
        return null;
    }
    /**
     * Add the standard file items to a menu item.
     */
    public JMenu addFileMenu(char rgchShortcuts[])
    {
        JMenu menu = null;
        JMenuItem menuItem = null;
        String strMenu = null;
        char rgchItemShortcuts[] = new char[10];
        menu = new JMenu(strMenu = this.getString(MenuConstants.FILE));
        menu.setMnemonic(ActionManager.getFirstChar(strMenu, rgchShortcuts, true));
        ActionManager.getFirstChar(strMenu, rgchItemShortcuts, true);
        menu.setOpaque(false);

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.PRINT));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.PRINT, null));    // Get this image, then redisplay me when you're done
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menu.addSeparator();

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.HOME));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.HOME, null));   // Get this image, then redisplay me when you're done
        menuItem.setHorizontalTextPosition(JButton.RIGHT);

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.NEW_WINDOW));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.NEW_WINDOW, null));   // Get this image, then redisplay me when you're done
        menuItem.setHorizontalTextPosition(JButton.RIGHT);

        if (this.getScreenField() instanceof SGridMenuBar)
        {
            menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.FORMCLONE));
            menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
            menuItem.setHorizontalTextPosition(JButton.RIGHT);
        }
        if (this.getScreenField() instanceof SMenuBar)
        {
            menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.LOOKUPCLONE));
            menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
            menuItem.setHorizontalTextPosition(JButton.RIGHT);
        }
        menu.addSeparator();

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.LOGON));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.LOGON, null));   // Get this image, then redisplay me when you're done
        menuItem.setHorizontalTextPosition(JButton.RIGHT);

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.LOGOUT));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.LOGOUT, null));   // Get this image, then redisplay me when you're done
        menuItem.setHorizontalTextPosition(JButton.RIGHT);

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.SETTINGS));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.ALT_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.SETTINGS, null));   // Get this image, then redisplay me when you're done
        menuItem.setHorizontalTextPosition(JButton.RIGHT);

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.CHANGE_PASSWORD));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.CHANGE_PASSWORD, null));   // Get this image, then redisplay me when you're done
        menuItem.setHorizontalTextPosition(JButton.RIGHT);

        menu.addSeparator();
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.CLOSE));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.CLOSE, null));    // Get this image, then redisplay me when you're done
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        return menu;
    }
    /**
     * Add the standard edit items to a menu item.
     */
    public JMenu addEditMenu(char rgchShortcuts[])
    {
        JMenu menu = null;
        JMenuItem menuItem = null;
        String strMenu = null;
        char rgchItemShortcuts[] = new char[10];
        menu = new JMenu(strMenu = this.getString(MenuConstants.EDIT));
        menu.setMnemonic(ActionManager.getFirstChar(strMenu, rgchShortcuts, false));
        ActionManager.getFirstChar(strMenu, rgchItemShortcuts, true);
        menu.setOpaque(false);

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.UNDO));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.UNDO, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menu.addSeparator();
        
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.CUT));
        menuItem.addActionListener(new javax.swing.text.DefaultEditorKit.CutAction());
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.CUT, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.COPY));
        menuItem.addActionListener(new javax.swing.text.DefaultEditorKit.CopyAction());
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.COPY, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.PASTE));
        menuItem.addActionListener(new javax.swing.text.DefaultEditorKit.PasteAction());
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.PASTE, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menu.addSeparator();
        
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.PREFERENCES));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.PREFERENCES, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        return menu;
    }
    /**
     * Add the standard record items to a menu item.
     */
    public JMenu addRecordMenu(char rgchShortcuts[])
    {
        JMenu menu = null;
        JMenuItem menuItem = null;
        String strMenu = null;
        char rgchItemShortcuts[] = new char[10];
        menu = new JMenu(strMenu = this.getString(MenuConstants.RECORD));
        menu.setMnemonic(ActionManager.getFirstChar(strMenu, rgchShortcuts, false));
        ActionManager.getFirstChar(strMenu, rgchItemShortcuts, true);
        menu.setOpaque(false);
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.FIRST));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, true));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.FIRST, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.PREVIOUS));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.PREVIOUS, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);

        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.NEXT));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.NEXT, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.LAST));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.LAST, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menu.addSeparator();
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.SUBMIT));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.SUBMIT, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.DELETE));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.DELETE, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.RESET));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, ActionEvent.CTRL_MASK));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.RESET, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        return menu;
    }
    /**
     * Add the standard record items to a menu item.
     */
    public JMenu addHelpMenu(char rgchShortcuts[])
    {
        JMenu menu = null;
        JMenuItem menuItem = null;
        String strMenu = null;
        char rgchItemShortcuts[] = new char[10];
        menu = new JMenu(strMenu = this.getString(MenuConstants.HELP_MENU));
        menu.setMnemonic(ActionManager.getFirstChar(strMenu, rgchShortcuts, false));
        ActionManager.getFirstChar(strMenu, rgchItemShortcuts, true);
        menu.setOpaque(false);
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.ABOUT));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.ABOUT, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        menu.addSeparator();
        menuItem = this.addMenuItem(menu, strMenu = this.getString(MenuConstants.HELP));
        menuItem.setMnemonic(ActionManager.getFirstChar(strMenu, rgchItemShortcuts, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuItem.setIcon(this.loadImageIcon(MenuConstants.HELP, null));
        menuItem.setHorizontalTextPosition(JButton.RIGHT);
        return menu;
    }
    /**
     * Add the menu items to this frame.
     */
    public JMenuItem addMenuItem(JMenu menu, String strMenuDesc)
    {
        JMenuItem menuItem;
        menu.add(menuItem = new JMenuItem(strMenuDesc));
        menuItem.setOpaque(false);
        menuItem.addActionListener(this);
        return menuItem;
    }
    /**
     * Get the Menu resource object
     */
    public String getString(String strKey)
    {
        return ((SBaseMenuBar)this.getScreenField()).getString(strKey);
    }
}
