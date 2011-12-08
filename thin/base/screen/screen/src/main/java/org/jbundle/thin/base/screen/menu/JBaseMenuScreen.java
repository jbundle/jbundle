/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.menu;

/**
 * JMenuScreen.java:    Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.JScreenConstants;

/**
 * A base menu screen for displaying menu items in a grid.
 * This screen is made of a panel with a GridBagLayout.
 * Menu items are added in a 3 x h grid.
 * Usually, you will use JRemoteMenuScreen, as it has all the
 * logic for handling a remote menu record.
 * You may want to override this to display your own items
 * (such as tours for selection).
 */
public class JBaseMenuScreen extends JScreen
{
	private static final long serialVersionUID = 1L;

    /**
     * JMenuScreen Class Constructor.
     */
    public JBaseMenuScreen()
    {
        super();
    }
    /**
     * JMenuScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JBaseMenuScreen(Object parent,Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object record)
    {
        super.init(parent, record);
        this.setBounds(0, 0, JScreenConstants.PREFERRED_SCREEN_SIZE.width, JScreenConstants.PREFERRED_SCREEN_SIZE.height);
    }
    /**
     * Get standard GridBagConstraints.
     * The grid bag constrain is reset to the original value in this method.
     * @return The grid bag constraints.
     */
    public GridBagConstraints getGBConstraints()
    {
        if (m_gbconstraints == null)
            m_gbconstraints = new GridBagConstraints();
        else
        { // Set back to default values
            m_gbconstraints.gridx = GridBagConstraints.RELATIVE;
            m_gbconstraints.gridy = GridBagConstraints.RELATIVE;
            m_gbconstraints.gridwidth = 1;
            m_gbconstraints.gridheight = 1;
            m_gbconstraints.weightx = 0;
            m_gbconstraints.weighty = 0;
            m_gbconstraints.anchor = GridBagConstraints.CENTER;
            m_gbconstraints.fill = GridBagConstraints.NONE;
            m_gbconstraints.insets.bottom = 0;
            m_gbconstraints.insets.left = 0;
            m_gbconstraints.insets.right = 0;
            m_gbconstraints.insets.top = 0;
            m_gbconstraints.ipadx = 0;
            m_gbconstraints.ipady = 0;
        }
        return m_gbconstraints;
    }
    /**
     * Add the description labels to the first column of the grid.
     * Not used for menus.
     */
    public void addScreenLabels(Container parent)
    {
    }
    /**
     * Add the screen controls to the second column of the grid.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public void addScreenControls(Container parent)
    {
        FieldList record = this.getFieldList();
        FieldTable table = record.getTable();
        
        try
        {
            int iRowCount = 0;
            int iColumnCount = 0;
            table.close();
            while (table.hasNext())
            {
                table.next();
                GridBagConstraints gbConstraints = this.getGBConstraints();
                gbConstraints.gridx = iRowCount;
                gbConstraints.gridy = iColumnCount;
                gbConstraints.anchor = GridBagConstraints.NORTHWEST;

                JComponent button = this.makeMenuButton(record);
                GridBagLayout gridbag = (GridBagLayout)this.getScreenLayout();
                gridbag.setConstraints(button, gbConstraints);
                parent.add(button);

                iRowCount++;
                if (iRowCount == 3)
                {
                    iRowCount = 0;
                    iColumnCount++;
                }
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * Get the menu name.
     * @param record The menu record.
     * @return The name of this menu item.
     */
    public String getMenuName(FieldList record)
    {
        return record.getField("Name").toString();
    }
    /**
     * Get the menu icon.
     * @param record The menu record.
     * @return The icon filename for this menu item.
     */
    public String getMenuIcon(FieldList record)
    {
        FieldInfo field = record.getField("IconResource");
        String strIcon = null;
        if (field != null)
        {
            strIcon = field.toString();
            if ((strIcon != null) && (strIcon.length() > 0))
                return strIcon;
        }
        field = record.getField("Type");
        if ((field != null) && (!field.isNull()))
        {
            strIcon = field.toString();
            if ((strIcon != null) && (strIcon.length() > 0))
            {
                strIcon = strIcon.substring(0, 1).toUpperCase() + strIcon.substring(1);
                return strIcon;
            }
        }
        return Constants.BLANK;
    }
    /**
     * Get the menu command to send to handle command.
     * @param record The menu record.
     * @return The command for this menu item.
     */
    public String getMenuLink(FieldList record)
    {
        FieldInfo field = record.getField("Type");
        if ((field != null) && (!field.isNull()))
        {
            String strType = field.toString();
            String strParams = record.getField("Params").toString();
            if (strParams == null)
                strParams = Constants.BLANK;
            else if (strParams.length() > 0)
                strParams = '&' + strParams;
            if ((strType != null) && (strType.length() > 0))
            {
                if (strType.equalsIgnoreCase(Params.MENU))
                {
                    field = record.getField("ID");
                    String strID = field.toString();
                    return '?' + strType + '=' + strID + strParams;
                }
                else if (strType.equalsIgnoreCase("applet"))
                {
                    String strProgram = record.getField("Program").toString();
                    return '?' + strType + '=' + strProgram + strParams;
                }
            }
        }
        return this.getMenuName(record);            // Command = name
    }
    /**
     * Create the button/panel for this menu item.
     * @param record The menu record.
     * @return The component to add to this panel.
     */
    public JComponent makeMenuButton(FieldList record)
    {
        String strDescription = this.getMenuName(record);
        String strLink = this.getMenuLink(record);

        String strIcon = this.getMenuIcon(record);
        strIcon = BaseApplet.getSharedInstance().getImageFilename(strIcon, "icons");

        JUnderlinedButton button = null;
        if (strIcon == null)
            button = new JUnderlinedButton(strDescription);
        else
        {
            Icon icon = BaseApplet.getSharedInstance().loadImageIcon(strIcon, "Icon");
            button = new JUnderlinedButton(strDescription, icon);
        }
        button.setName(strLink);
        button.setOpaque(false);
        String strBackgroundColor = this.getBaseApplet().getProperty(Params.BACKGROUNDCOLOR);
        Color colorBackground = null;
        if ((strBackgroundColor != null) && (strBackgroundColor.length() > 0))
            colorBackground = BaseApplet.nameToColor(strBackgroundColor);
        if (colorBackground == null)
            colorBackground = Color.white;
        button.setBackground(colorBackground); // Since the button is opaque, this is only needed for those look and feels that want their own background color.
        button.setBorderPainted(false);
        button.addActionListener(this);
        return button;
    }
    /**
     * Move the screen controls to the data record(s).
     * This is usually not used for MenuScreens.
     */
    public void controlsToFields()
    {
    }
    /**
     * Move the data record(s) to the screen controls.
     * This is usually not used for MenuScreens.
     */
    public void fieldsToControls()
    {
    }
    /**
     * Clear all the fields to their default values.
     * This is usually not used for MenuScreens.
     */
    public void resetFields()
    {
    }
    /**
     * Do some applet-wide action.
     * For example, submit or reset.
     * Here are how actions are handled:
     * When a BasePanel receives a command it calls it's doAction method. If the doAction
     * doesn't handle the action, it is passed to the parent's doAction method, until it
     * hits the (this) applet method. This applet method tries to pass the command to all
     * sub-BasPanels until one does the action.
     * For example, submit or reset. Pass this action down to all the JBaseScreens.
     * Do not override this method, override the handleAction method in the JBasePanel.
     * @param strAction The command to pass to all the sub-JBasePanels.
     * @param parent The parent to start the sub-search from (non-inclusive).
     * @return true If handled.
     */
    public boolean handleAction(String strAction, Component source, int iOptions)
    {
        BaseApplet applet = this.getBaseApplet();
        Cursor oldCursor = null;
        if (applet != null)
        	oldCursor = applet.setStatus(Cursor.WAIT_CURSOR, applet, null);
        boolean bFlag = super.handleAction(strAction, source, iOptions);
        if (applet != null)
            applet.setStatus(0, applet, oldCursor);
        return bFlag;
    }
    /**
     * Process this action.
     * You need to override this to do something.
     * @param strAction The command to execute.
     * @return True if handled.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        return super.doAction(strAction, iOptions);
    }
    /**
     * Build the list of fields that make up the screen.
     * Override this to supply the record to use for menus.
     * Must be an override of Folder record.
     * (ie., return new Menus(null);).
     * @return The record.
     */
    public FieldList buildFieldList()
    {
        return null;    //FieldList record = new org.jbundle.thin.main.Menus(this);    // If overriding class didn't set this
    }
    /**
     * Add the scrollbars?
     * @return True, as menu's have scrollbars by default.
     */
    public boolean isAddScrollbars()
    {
        return true;
    }
    /**
     * Add the toolbars?
     * @return The standard menu toolbar, JMenuToolbar.
     */
    public JComponent createToolbar()
    {
        return new JMenuToolbar(this, null);
    }
}
