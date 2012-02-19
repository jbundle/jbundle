/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BaseMenuScreen;
import org.jbundle.base.screen.model.MenuScreen;
import org.jbundle.base.screen.model.SMenuButton;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.util.Util;


/**
* Menu screen.
 */
public class VBaseMenuScreen extends VBaseScreen
{
    /**
     * The current grid bag constraint.
     */
    protected GridBagConstraints m_gbconstraints = null;

    /**
     * Constructor.
     */
    public VBaseMenuScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VBaseMenuScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
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
        JPanel panel = (JPanel)this.getControl();
        super.free();
        JScrollPane scrollPane = (JScrollPane)panel.getParent().getParent();
        panel.getParent().remove(panel);
        scrollPane.getParent().remove(scrollPane);
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new (JPanel) control.
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        JPanel screen = new JPanel();
        this.setControlExtent(new Rectangle(0, 0, ScreenConstants.PREFERRED_SCREEN_SIZE.width, ScreenConstants.PREFERRED_SCREEN_SIZE.height));
        JScrollPane scrollpane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setPreferredSize(new Dimension(10,10));
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollpane.getViewport().add(screen);
        JPanel panel = new JPanel();
        panel.add(scrollpane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setOpaque(false);
        screen.setOpaque(false);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        return screen;
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
        if (iLevel == DBConstants.CONTROL_TOP)
        {
            Container parent = this.getControl().getParent();
            while (!(parent instanceof JScrollPane))
            {
                parent = parent.getParent();
            }
            if (parent != null)
                return parent.getParent();  // scrollpane->JPanel
        }
        return super.getControl(iLevel);
    }
    /**
     * Setup this screen's screen layout.
     * Usually, you use JAVA layout managers, but you may also use ScreenLayout.
     * @return The new GridBagLayout.
     */
    public Object addScreenLayout()
    {
        GridBagLayout screenLayout = null;
        if (this.getScreenLayout() == null)   // Only if no parent screens
        {   // EVERY BasePanel gets a ScreenLayout!
            JPanel control = (JPanel)this.getControl();
            if (control != null)
            {
                screenLayout = new GridBagLayout();   // My LayoutManager
                control.setLayout(screenLayout);

                String strMenu = ((BaseMenuScreen)this.getScreenField()).getProperty(DBParams.MENU);
                this.setupGrid(strMenu);        // Initial menu
            }
        }
        return screenLayout;
    }
    /**
     * Code to display a Menu.
     * Add all the MenuButtons to this grid screen.
     * @param strMenu The name of the menu to display.
     */
    public void setupGrid(String strMenu)
    {
        JPanel control = (JPanel)this.getControl();
        GridBagLayout screenLayout = (GridBagLayout)control.getLayout();    // Only if no parent screens
        BaseMenuScreen menuScreen = (BaseMenuScreen)this.getScreenField();
        Record record = menuScreen.getMainRecord();
        menuScreen.preSetupGrid(strMenu);
        String strDescription, strType, strLink, strIcon;
        String strText = null;
        boolean bAddDescription = true;
        String strAddDescription = menuScreen.getProperty(DBParams.MENUDESC);
        if (DBConstants.FALSE.equalsIgnoreCase(strAddDescription))
            bAddDescription = false;
        
        try
        {
            int iRowCount = 0;
            int iColumnCount = 0;
            record.close();
            while (record.hasNext())
            {
                record.next();
                strDescription = menuScreen.getMenuName(record);
                strType = menuScreen.getMenuType(record);
                strIcon = menuScreen.getMenuIcon(record);
                if (bAddDescription)
                    strText = menuScreen.getMenuDesc(record);
                strLink = menuScreen.getMenuLink(record);

                if (strIcon.length() == 0)
                {
                    strIcon = strType;
                    if (strIcon.length() > 0)
                        strIcon = strIcon.substring(0, 1).toUpperCase() + strIcon.substring(1);
                }
                if (strIcon.indexOf('/') == -1)
                    strIcon = HtmlConstants.ICON_DIR + strIcon;
                if (strIcon.indexOf('.') == -1)
                    strIcon = strIcon + ".gif";

                GridBagConstraints gbConstraints = this.getGBConstraints();
                gbConstraints.gridx = iRowCount;
                gbConstraints.gridy = iColumnCount;
                gbConstraints.anchor = GridBagConstraints.NORTHWEST;

                String strCommand = strLink;
                String strToolTip = null;
                ScreenLocation loc = menuScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR);
                ScreenField sbutton = new SMenuButton(loc, menuScreen, null, ScreenConstants.DISPLAY_DESC, null, strDescription, strIcon, strCommand, strToolTip, strText);
                Component button = (Component)sbutton.getScreenFieldView().getControl(DBConstants.CONTROL_TOP);
                screenLayout.setConstraints(button, gbConstraints);

                iRowCount++;
                if (iRowCount == 3)
                {
                    iRowCount = 0;
                    iColumnCount++;
                }
            }
        } catch (DBException e)   {
        }
        menuScreen.postSetupGrid();
    }
    /**
     * Get the current GridBagConstraints.
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
            m_gbconstraints.insets.bottom = 5;
            m_gbconstraints.insets.left = 5;
            m_gbconstraints.insets.right = 5;
            m_gbconstraints.insets.top = 5;
            m_gbconstraints.ipadx = 0;
            m_gbconstraints.ipady = 0;
        }
        return m_gbconstraints;
    }
    /**
     * Process the command using this view.
     * @param strCommand The command to process.
     * @return True if processed.
     */
    public boolean doCommand(String strCommand)
    {
        Map<String,Object> properties = new Hashtable<String,Object>();
        Util.parseArgs(properties, strCommand);
        if (properties.get(MenuScreen.SAME_WINDOW_PARAM) != null)
        {
            String strParam = (String)properties.get(DBParams.MENU);
            boolean bUseSameWindow = true;
            if ((strParam != null) && (bUseSameWindow))
                if (properties.get(DBParams.SCREEN) == null)    // Not a menu if there is a new screen
            {
                this.setupGrid(strParam);   // Initial menu
                return true;
            }
        }
        return false;
    }
}
