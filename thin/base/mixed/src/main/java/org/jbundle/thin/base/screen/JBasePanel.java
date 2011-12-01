/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jbundle.model.Freeable;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.action.ActionManager;
import org.jbundle.thin.base.screen.action.BaseAction;
import org.jbundle.thin.base.screen.landf.ScreenUtil;
import org.jbundle.thin.base.screen.print.ScreenPrinter;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinMenuConstants;

/**
 * My base screen.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public class JBasePanel extends JPanel
    implements Freeable, ActionListener
{
	private static final long serialVersionUID = 1L;

	/**
     * My parent object (a container).
     */
    protected Object m_parent = null;

    /**
     * JBaseScreen Class Constructor.
     */
    public JBasePanel()
    {
        super();
    }
    /**
     * JBasePanel Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public JBasePanel(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object record)
    {
        m_parent = parent;
        this.setOpaque(false);
        
        if (this.getBaseApplet() != null)
        {
            String strScreenDesc = this.getScreenDesc();
            this.getBaseApplet().setStatusText(strScreenDesc);   // Display screen desc or clear screen desc.
            if (strScreenDesc != null)
                if (this.getTargetScreen(JBaseFrame.class) != null)
                    ((JBaseFrame)this.getTargetScreen(JBaseFrame.class)).setTitle(strScreenDesc);
        }
    }
    /**
     * Check the access rights for this screen.
     * (Override this to allow or deny fine-grained access)
     */
    public int checkSecurity(Application application)
    {
        return application.checkSecurity(this.getClass().getName());
    }        
    /**
     * Get the screen description to display in the status bar.
     */
    public String getScreenDesc()
    {
        return null;    // Override this!
    }
    /**
     * Free the resources held by this object.
     * This method calls freeSubComponents for this.
     */
    public void free()
    {
        if (this.getTargetScreen(JBaseFrame.class) != null)
            ((JBaseFrame)this.getTargetScreen(JBaseFrame.class)).setTitle(null);
        this.freeSubComponents(this);
    }
    /**
     * Initialize this class.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public Object getParentObject()
    {
        return m_parent;
    }
    /**
     * Call free for all the Freeable sub-components of the target container.
     * Note: This method is EXACTLY the same as the freeSubComponents method in JBaseApplet.
     * @param container The parent component to look through for freeable components.
     */
    public void freeSubComponents(Container container)
    {
        for (int i = 0; i < container.getComponentCount(); i++)
        {
            Component component = container.getComponent(i);
            if (component instanceof Freeable)
                ((Freeable)component).free();
            else if (component instanceof Container)
                this.freeSubComponents((Container)component);
        }
    }
    /**
     * Add the scrollbars?
     * @return true if this screen should have scrollbars.
     */
    public boolean isAddScrollbars()
    {
        return false;
    }
    /**
     * Add the toolbars?
     * @return The newly created toolbar or null.
     */
    public JComponent createToolbar()
    {
        return null;
    }
    /**
     * Add the menubars?
     * @return The newly created menubar or null.
     */
    public JMenuBar createMenubar()
    {
        return null;
    }
    /**
     * This may seem a waste, but firePropertyChange is protected, so I make it public.
     * @param propertyName The property name.
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }
    /**
     * Get the command string that can be used to create this screen.
     * @return The screen command (defaults to ?applet=&screen=xxx.xxx.xxxx).
     */
    public String getScreenCommand()
    {
        return "?" + Params.APPLET + "=&" + Params.SCREEN + '=' + this.getClass().getName();
    }
    /**
     * Focus to the first field.
     * Override this to set the focus to the first field.
     */
    public void resetFocus()
    {
    }
    /**
     * Clicked on a button, notify the screen.
     * (ActionListener implementation).
     * Calls doAction with the name of the component as the message.
     * @param evt The event.
     */
    public void actionPerformed(ActionEvent evt)
    {
        Component component = (Component)evt.getSource();
        String strMessage = component.getName();
        if (strMessage == null)
        {
            strMessage = evt.getActionCommand();
            Action action = ActionManager.getActionManager().getActionFromCommand(strMessage);
            if (action instanceof BaseAction)
                strMessage = ((BaseAction)action).getActionKey();
        }
        if ((strMessage != null) && (strMessage.length() > 0))
            this.handleAction(strMessage, this, 0);
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
     * @param source The source component
     * @param iOptions TODO
     * @param parent The parent to start the sub-search from (non-inclusive).
     * @return true If handled.
     */
    public boolean handleAction(String strAction, Component source, int iOptions)
    {
        // First see if I can handle this action
        if (this.doAction(strAction, iOptions))
            return true;
        if (source == null)
            source = this;
        // Next, see if my children can handle this action
        for (int iIndex = 0; iIndex < this.getComponentCount(); iIndex++)
        {
            Component component = this.getComponent(iIndex);
            if (component != source)
            {
                if (component instanceof JBasePanel)
                {   // This is a panel, handle the action.
                        if (((JBasePanel)component).handleAction(strAction, this, iOptions))  // and make sure this method is not called again for this object
                            return true;
                }
                else if (component instanceof Container)
                {   // Call this for all the sub-panels for this parent
                    if (BaseApplet.handleAction(strAction, (Container)component, this, iOptions))   // Continue down thru the tree
                        return true;
                }
            }
        }
        // Last, see if my parents can handle this action
        Container parent = this.getParent();
        if (parent != source)
        {
            if (parent instanceof JBasePanel)
            {
                return ((JBasePanel)parent).handleAction(strAction, this, iOptions); // Continue up the chain.
            }
            else if (parent != null)
            {
                return BaseApplet.handleAction(strAction, parent, this, iOptions);  // and make sure this method is not called again for this object
            }
        }
        return false;
    }
    /**
     * Process this action.
     * Override this for functionality.
     * @param strAction The action command or message.
     * @param iOptions TODO
     * @return true if handled.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (Constants.CLOSE.equalsIgnoreCase(strAction))
        {
            Frame frame = ScreenUtil.getFrame(this);
            if (frame != null)
                ((JBaseFrame)frame).free();
            return true;
        }
        else if (ThinMenuConstants.PRINT.equalsIgnoreCase(strAction))
        {
            return ScreenPrinter.onPrint(this);
        }
        else if (ThinMenuConstants.ABOUT.equalsIgnoreCase(strAction))
        {
            return this.getBaseApplet().onAbout();
        }
        else if (ThinMenuConstants.LOGON.equalsIgnoreCase(strAction))
        {
            int iErrorCode = this.getBaseApplet().onLogonDialog();
            if (iErrorCode == JOptionPane.CANCEL_OPTION)
                return true;    // User clicked the cancel button
            if (iErrorCode == Constants.NORMAL_RETURN)
                return true;    // Success
            // Display the error message and return!
            String strDisplay = this.getBaseApplet().getLastError(iErrorCode);
            JOptionPane.showConfirmDialog(this, strDisplay, "Error", JOptionPane.OK_CANCEL_OPTION);
            return true;   // Handled
        }
        else if (ThinMenuConstants.LOGOUT.equalsIgnoreCase(strAction))
        {
            int iErrorCode = this.getBaseApplet().getApplication().login(this.getBaseApplet(), null, null, null); // Logout
            if (iErrorCode == Constants.NORMAL_RETURN)
                return true;    // Success, handled
            // Display the error message and return!
            String strDisplay = this.getBaseApplet().getLastError(iErrorCode);
            JOptionPane.showConfirmDialog(this, strDisplay, "Error", JOptionPane.OK_CANCEL_OPTION);
            return true;   // Handled
        }
        else if (ThinMenuConstants.CHANGE_PASSWORD.equalsIgnoreCase(strAction))
        {
            int iErrorCode = this.getBaseApplet().onChangePassword();
            if (iErrorCode == JOptionPane.CANCEL_OPTION)
                return true;    // User clicked the cancel button
            if (iErrorCode == Constants.NORMAL_RETURN)
                return true;    // Success
            // Display the error message and return!
            String strDisplay = this.getBaseApplet().getLastError(iErrorCode);
            JOptionPane.showConfirmDialog(this, strDisplay, "Error", JOptionPane.OK_CANCEL_OPTION);
            return true;   // Handled
        }
        else if (ThinMenuConstants.PREFERENCES.equalsIgnoreCase(strAction))
        {
            return this.getBaseApplet().onSetFont();
        }
        return false;
    }
    /**
     * Get the the base applet that is the parent of this screen.
     * @return The parent BaseApplet (or null).
     */
    public BaseApplet getBaseApplet()
    {
        return (BaseApplet)this.getTargetScreen(BaseApplet.class);
    }
    /**
     * Climb up through my panel hierarchy until you find a component of this class.
     * @param targetClass The class type to find (or inherited).
     * @return The first parent up the tree to match this class (or null).
     */
    public Container getTargetScreen(Class<?> targetClass)
    {
        return this.getTargetScreen(this, targetClass);
    }
    /**
     * Climb up through the panel hierarchy until you find a component of this class.
     * @param component The (non-inclusive) component to start climbing up to find this class.
     * @param targetClass The class type to find (or inherited).
     * @return The first parent up the tree to match this class (or null).
     */
    public Container getTargetScreen(Container component, Class<?> targetClass)
    {
        Container container = this.getTargetScreen(component, targetClass, true);
        if (container == null)
            if (component instanceof JBasePanel)
                if (component != this)  // Special case, need to look from the component up, not from this up.
                    return ((JBasePanel)component).getTargetScreen(component, targetClass, true);
        return container;
    }
    /**
     * Climb up through the panel hierarchy until you find a component of this class.
     * @param component The (non-inclusive) component to start climbing up to find this class.
     * @param targetClass The class type to find (or inherited).
     * @param bComponent If false, don't climb the m_parent component. (To keep from unending recursion)
     * @param bComponent If true, climb both m_parent and the component's parent.
     * @return The first parent up the tree to match this class (or null).
     */
    public Container getTargetScreen(Container component, Class<?> targetClass, boolean bComponent)
    {
        Container container = null;
        if (bComponent)
            if (component == this)
                if (m_parent instanceof Container)
        {
            bComponent = false;
            container = (Container)m_parent;
        }
        if (container == null)
            container = component.getParent();
        if (container == null)
            return null;
        if (targetClass.isAssignableFrom(container.getClass()))
            return container;
        if (container instanceof JBasePanel)
            return ((JBasePanel)container).getTargetScreen(container, targetClass, true);
        return this.getTargetScreen(container, targetClass, bComponent);
    }
    /**
     * Climb down through the panel hierarchy until you find a component of this class.
     * @param component The (non-inclusive) component to start climbing down to find this class.
     * @param targetClass The class type to find (or inherited).
     * @return The first parent down the tree to match this class (or null).
     */
    public static Component getSubScreen(Container container, Class<?> targetClass)
    {
        for (int i = container.getComponentCount() - 1; i >= 0; i--)
        {   // Go backwards to do toolbars, menubars last.
            Component component = container.getComponent(i);
            if (targetClass.isAssignableFrom(component.getClass()))
                return component;
            if (component instanceof Container)
            {
                component = JBasePanel.getSubScreen((Container)component, targetClass);
                if (component != null)
                    return component;
            }
        }
        return null;
    }
    /**
     * Get the sub-component with this name.
     * @param strName The name I'm looking for.
     * @return The component with this name (or null).
     */
    public Component getComponentByName(String strName)
    {
        return this.getComponentByName(strName, this);
    }
    /**
     * Get the sub-component with this name.
     * @param strName The name I'm looking for.
     * @param parent The container to start the search from.
     * @return The component with this name (or null).
     */
    public Component getComponentByName(String strName, Container parent)
    {
        for (int i = 0; i < parent.getComponentCount(); i++)
        {
            Component component = parent.getComponent(i);
            if (strName.equals(component.getName()))
                return component;
            if (component instanceof Container)
            {
                component = this.getComponentByName(strName, (Container)component);
                if (component != null)
                    return component;
            }
        }
        return null;    // Not found
    }
    /**
     * Add the optional scrollers and toolbars to this screen.
     * @param baseScreen The new screen (which has information on scrollers, toolbars, etc).
     */
    public JComponent setupNewScreen(JBasePanel baseScreen)
    {
        JComponent screen = baseScreen;
        if (baseScreen.isAddScrollbars()) // Add scrollbars?
            screen = this.addScrollbars(screen);
        return this.setupMenuAndToolbar(baseScreen, screen);
    }
    /**
     * Add the optional scrollers and toolbars to this screen.
     * @param baseScreen The new screen (which has information on scrollers, toolbars, etc).
     */
    public JComponent setupMenuAndToolbar(JBasePanel baseScreen, JComponent screen)
    {
        if (screen == null)
            screen = baseScreen;
        JBasePanel toolbarParent = this.getToolbarParent();
        JComponent toolbar = baseScreen.createToolbar();
        JMenuBar menubar = baseScreen.createMenubar();
        if (toolbarParent == this)
        {
            if (toolbar != null)    // Add toolbars?
                screen = this.addToolbar(screen, toolbar);        
            if (menubar != null)    // Add menubars?
                screen = this.addToolbar(screen, menubar);
        }
        else
        {
            if (toolbar != null)    // Add toolbars?
                this.switchToolbar(toolbarParent, toolbar);
            if (menubar != null)    // Add menubars?
                this.switchToolbar(toolbarParent, menubar);
        }
        return screen;
    }
    /**
     * Add a scrollpane to this component and return the component with
     * the scroller and this screen in it.
     * @param screen The screen to add a scroller around.
     */
    public JPanel addScrollbars(JComponent screen)
    {
        Dimension dimension = screen.getPreferredSize();
        if ((dimension.height == 0) && (dimension.width == 0))
            dimension = JScreenConstants.PREFERRED_SCREEN_SIZE;
        else if ((screen.getBounds().width != 0) && (screen.getBounds().height != 0))
        {
            dimension.width = screen.getBounds().width;
            dimension.height = screen.getBounds().height;
        }
        dimension.width = Math.max(JScreenConstants.MIN_SCREEN_SIZE.width, Math.min(dimension.width + 20, JScreenConstants.PREFERRED_SCREEN_SIZE.width));
        dimension.height = Math.max(JScreenConstants.MIN_SCREEN_SIZE.height, Math.min(dimension.height + 20, JScreenConstants.PREFERRED_SCREEN_SIZE.height));
        JScrollPane scrollpane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        if (dimension != null)
            scrollpane.setPreferredSize(dimension);
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollpane.getViewport().add(screen);
        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollpane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        screen.setOpaque(false);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        return panel;
    }
    /**
     * Get the highest level parent that is a JBasePanel.
     * This is where a toolbar or menubar should be added.
     * @return The top level parent (if none, return this).
     */
    public JBasePanel getToolbarParent()
    {
        JBasePanel parent = (JBasePanel)this.getTargetScreen(JBasePanel.class);
        if (parent == null)
            if (m_parent instanceof JBasePanel)
                parent = (JBasePanel)m_parent;
        if (parent == null)
            return this;
        return parent.getToolbarParent();
    }
    /**
     * 
     * @param screen
     * @param toolbar
     */
    public void switchToolbar(JBasePanel toolbarParentScreen, JComponent toolbar)
    {
        Container toolbarParent = toolbarParentScreen;
        Component oldToolbar = null;
        while (toolbarParent != null)
        {
            oldToolbar = toolbarParent.getComponent(0);
            if ((toolbar instanceof JMenuBar) && (oldToolbar instanceof JMenuBar))
                break;  // Found the parent
            if ((toolbar instanceof JBaseToolbar) && (oldToolbar instanceof JBaseToolbar))
                break;  // Found the parent
            if (toolbarParent == toolbarParentScreen.getParentObject())
            {   // Too far... didn't find it.
                toolbarParent = null;
                break;
            }
            toolbarParent = toolbarParent.getParent();
        }
        if (toolbarParent == null)
        {   // No old toolbar found, add it to the toolbar parent
            toolbarParent = toolbarParentScreen.getParent();
            if (toolbarParent != null)  // Never?
                toolbarParent.remove(toolbarParentScreen);
            JPanel newScreen = toolbarParentScreen.addToolbar(toolbarParentScreen, toolbar);
            if (toolbarParent != null)
                toolbarParent.add(newScreen);
            return;
        }
        else
        {   // Replace it!
            toolbarParent.remove(oldToolbar);
            toolbar.setAlignmentX(LEFT_ALIGNMENT);
            toolbarParent.add(toolbar, 0);
            if (toolbar instanceof JBaseToolbar)
                toolbar.add(Box.createHorizontalStrut(300));    // Hack - Otherwise the old toolbar shows through
        }
    }
    /**
     * Add this toolbar to this panel and return the new main panel.
     * @param screen The screen to add a toolbar to.
     * @param toolbar The toolbar to add.
     * @return The new panel with these two components in them.
     */
    public JPanel addToolbar(JComponent screen, JComponent toolbar)
    {
        JPanel panelMain = new JPanel();
        panelMain.setOpaque(false);
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
        panelMain.add(toolbar);
        toolbar.setAlignmentX(0);
        panelMain.add(screen);
        screen.setAlignmentX(0);
        return panelMain;
    }
}
