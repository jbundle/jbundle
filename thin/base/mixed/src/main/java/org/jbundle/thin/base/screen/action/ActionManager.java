/*
 * ActionManager.java
 *
 * Created on August 30, 2005, 10:45 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseToolbar;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 *
 * @author don
 */
public class ActionManager extends Hashtable<String,Action>
{
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new instance of ActionManager
     */
    private ActionManager()
    {
        super();
    }
    /**
     * Creates a new instance of ActionManager
     */
    private ActionManager(Object obj)
    {
        this();
        this.init(obj);
    }
    /**
     * Creates a new instance of ActionManager
     */
    private void init(Object obj)
    {
    }

    private static ActionManager actionManager = null;
    /**
     *
     */
    public static ActionManager getActionManager()
    {
        if (actionManager == null)
            actionManager = new ActionManager(null);
        return actionManager;
    }
    
    /**
     * Setup the standard menu items.
     */
    public JMenuBar setupStandardMenu(ActionListener targetAction)
    {
        return this.setupStandardMenu(targetAction, true);
    }
    /**
     * Setup the standard menu items.
     */
    public JMenuBar setupStandardMenu(ActionListener targetAction, boolean bAddHelpMenu)
    {
        Application application = BaseApplet.getSharedInstance().getApplication();
        ResourceBundle oldResources = application.getResourceBundle();
        application.getResources(null, true);

        this.setupActions(targetAction);

        JMenuBar menuBar = new JMenuBar()
        {
        	private static final long serialVersionUID = 1L;
            public Dimension getMaximumSize()
            {   // HACK - Not sure why menu takes up 1/2 of screen...?
                return new Dimension(super.getMaximumSize().width, super.getPreferredSize().height);
            }
        };
        menuBar.setOpaque(false);
        JMenu menu;
        char[] rgchItemShortcuts = new char[20];
        
        menu = this.addMenu(menuBar,ThinMenuConstants.FILE);
        this.addMenuItem(menu, ThinMenuConstants.PRINT, rgchItemShortcuts);
        menu.addSeparator();
        this.addMenuItem(menu, ThinMenuConstants.LOGON, rgchItemShortcuts);
        this.addMenuItem(menu, ThinMenuConstants.LOGOUT, rgchItemShortcuts);
        this.addMenuItem(menu, ThinMenuConstants.CHANGE_PASSWORD, rgchItemShortcuts);
        menu.addSeparator();
        this.addMenuItem(menu, ThinMenuConstants.CLOSE, rgchItemShortcuts);
        
        rgchItemShortcuts = new char[20];
        menu = this.addMenu(menuBar,ThinMenuConstants.EDIT);

//        this.addMenuItem(menu, UNDO);
//        menu.addSeparator();
        this.addMenuItem(menu, ThinMenuConstants.CUT, rgchItemShortcuts);
        this.addMenuItem(menu, ThinMenuConstants.COPY, rgchItemShortcuts);
        this.addMenuItem(menu, ThinMenuConstants.PASTE, rgchItemShortcuts);
        menu.addSeparator();
        
        this.addMenuItem(menu, ThinMenuConstants.PREFERENCES, rgchItemShortcuts);
        
        if (oldResources != null)
            application.setResourceBundle(oldResources);

        if (bAddHelpMenu)
            menu = this.addHelpMenu(menuBar);

        return menuBar;
    }
    /**
     * Add a standard help menu to this menu bar
     * @param menuBar
     * @return
     */
    public JMenu addHelpMenu(JMenuBar menuBar)
    {
        Application application = BaseApplet.getSharedInstance().getApplication();
        ResourceBundle oldResources = application.getResourceBundle();
        application.getResources(null, true);

        char[] rgchItemShortcuts = new char[20];
        JMenu menu = this.addMenu(menuBar,ThinMenuConstants.HELP_MENU);

        this.addMenuItem(menu, ThinMenuConstants.ABOUT, rgchItemShortcuts);
        menu.addSeparator();
        this.addMenuItem(menu, ThinMenuConstants.HELP, rgchItemShortcuts);
        
        if (oldResources != null)
            application.setResourceBundle(oldResources);

        return menu;
    }
    /**
     * Add the menu items to this frame.
     */
    public JMenu addMenu(JMenuBar menuBar, String strAction)
    {
        return this.addMenu(menuBar, strAction, JBaseToolbar.LAST);
    }
    /**
     * Add the menu items to this frame.
     */
    public JMenu addMenu(JMenuBar menuBar, String strAction, int iLocation)
    {
        JMenu menu = null;
        Application application = BaseApplet.getSharedInstance().getApplication();
        ResourceBundle oldResources = application.getResourceBundle();
        application.getResources(null, true);

        if (iLocation == JBaseToolbar.BEFORE_HELP)
            menuBar.add(menu = new JMenu(application.getString(strAction)), menuBar.getComponentCount() - 1);
        else
            menuBar.add(menu = new JMenu(application.getString(strAction)));
        menu.setMnemonic(menu.getText().charAt(0));

        if (oldResources != null)
            application.setResourceBundle(oldResources);

        return menu;
    }
    /**
     * Add the menu items to this frame.
     */
    public JMenuItem addMenuItem(JMenu menu, String actionKey, char[] rgChars)
    {
        JMenuItem menuItem;
        Action action = (Action)this.get(actionKey);
        if (action == null)
            return null;
        menu.add(menuItem = new JMenuItem(action));
        char firstChar = ActionManager.getFirstChar((String)menuItem.getAction().getValue(Action.NAME), rgChars, false);
        if (firstChar != NO_CHAR)
            menuItem.setMnemonic(firstChar);
        menuItem.setHorizontalTextPosition(JButton.RIGHT);

        return menuItem;
    }
    /**
     * Add this button to this window.
     * @param strIcon The command and Icon name.
     * @param strLink The button name.
     */
    public JMenuItem addMenuItem(JMenu menu, String strCommand, ActionListener targetListener, KeyStroke keyStroke)
    {
        BaseAction action = new BaseAction(strCommand, targetListener);
        if (keyStroke != null)
            action.putValue(Action.ACCELERATOR_KEY, keyStroke);
        return this.addMenuItem(menu, strCommand, null);
    }
    /**
     *
     */
    public void setupActions(ActionListener targetAction)
    {
        Action action;
        ImageIcon icon;
        
        action = new BaseAction(ThinMenuConstants.CLOSE, targetAction);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

        action = new BaseAction(ThinMenuConstants.LOGON, targetAction);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        action = new BaseAction(ThinMenuConstants.LOGOUT, targetAction);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        action = new BaseAction(ThinMenuConstants.CHANGE_PASSWORD, targetAction);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        icon = BaseApplet.getSharedInstance().loadImageIcon(ThinMenuConstants.CHANGE_PASSWORD);
        if (icon != null)
            action.putValue(AbstractAction.SMALL_ICON, icon);
        
        action = new BaseAction(ThinMenuConstants.PRINT, targetAction);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));

        action = new BaseAction(ThinMenuConstants.UNDO, targetAction);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));

        action = new javax.swing.text.DefaultEditorKit.CutAction();
        ActionManager.getActionManager().put(ThinMenuConstants.CUT, action);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        icon = BaseApplet.getSharedInstance().loadImageIcon(ThinMenuConstants.CUT);
        if (icon != null)
            action.putValue(AbstractAction.SMALL_ICON, icon);

        action = new javax.swing.text.DefaultEditorKit.CopyAction();
        ActionManager.getActionManager().put(ThinMenuConstants.COPY, action);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        icon = BaseApplet.getSharedInstance().loadImageIcon(ThinMenuConstants.COPY);
        if (icon != null)
            action.putValue(AbstractAction.SMALL_ICON, icon);

        action = new javax.swing.text.DefaultEditorKit.PasteAction();
        ActionManager.getActionManager().put(ThinMenuConstants.PASTE, action);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        icon = BaseApplet.getSharedInstance().loadImageIcon(ThinMenuConstants.PASTE);
        if (icon != null)
            action.putValue(AbstractAction.SMALL_ICON, icon);

        action = new BaseAction(ThinMenuConstants.PREFERENCES, targetAction);
        
        action = new BaseAction(ThinMenuConstants.ABOUT, targetAction);
        
        action = new BaseAction(ThinMenuConstants.HELP, targetAction);
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

    }
    /**
     *
     */
    public Action getActionFromCommand(String strMessage)
    {
        for (Action action : this.values())
        {
            if (strMessage.equalsIgnoreCase((String)action.getValue(Action.NAME)))
                return action;
        }
        return null;    // Not found
    }
    public static final char NO_CHAR = 0;
    /**
     * Get the appropriate keyboard shortcut character (that hasn't been used in this group).
     * @param strMenu Menu string (try to use first character).
     * @param rgChars Currently used characters.
     * @param bResetCache First item on menu, reset rgChars.
     */
    public static char getFirstChar(String strMenu, char[] rgChars, boolean bResetCache)
    {
        if (bResetCache) if (rgChars != null)
        {
            for (int i = 0; i < rgChars.length; i++)
                rgChars[i] = 0;
        }
        if (strMenu != null)
        {
            for (int iCharPosition = 0; iCharPosition < strMenu.length(); iCharPosition++)
            {
                char chShortcut = strMenu.charAt(iCharPosition);
                char chShortcutCompare = Character.toUpperCase(chShortcut);
                if (rgChars != null)    // Make sure this shortcut has not been used
                    for (int i = 0; i < rgChars.length; i++)
                    {
                        if (rgChars[i] == 0)
                        {
                            rgChars[i] = chShortcutCompare;
                            return chShortcut;  // End of cache (success)
                        }
                        if (rgChars[i] == chShortcutCompare)
                            break;  // Continue search (This char already used)
                    }
            }
        }
        return NO_CHAR; // No first char found
    }
}
