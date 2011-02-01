package org.jbundle.thin.base.screen.util;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Component;
import java.awt.Container;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JTable;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.JBasePanel;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.thin.base.screen.grid.ThinTableModel;


/**
 * A JMainScreen is a holder screen for a Grid/Form screen.
 * This screen is useful where a sub-screen changes between a grid and maint view.
 * This screen has it's own history stack and logic to switch sub-screens.
 * Just Override the createNewScreen method to supply the correct grid or form screen.
 * Optionally, you can override and add a sub screen with summary data in the init method.
 * (but remember to call super!).
 * This class can also be used for any sub-screen that requires a history (such as a menu
 * in a sub-screen).
 */
public class JMainScreen extends JBaseScreen
{
	private static final long serialVersionUID = 1L;

	/**
     * My top-level sub-screen.
     */
    protected JComponent m_topPane = null;
    /**
     *  History of sub-screens contained in this Applet Screen (ie., for the optional 'back' command).
     */
    protected Vector<String> m_vHistory = null;
    
    /**
     * JMainScreen Class Constructor.
     */
    public JMainScreen()
    {
        super();
    }
    /**
     * JMainScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JMainScreen(Object parent, Object obj)
    {
        this();
        this.init(parent, obj);
    }
    /**
     * JMainScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object applet)
    {
        super.init(parent, applet);

        this.addSubPanels(this);
    }
    /**
     * Add any screen sub-panel(s) now.
     * @param parent The parent screen to add sub-screens to.
     * @return TODO
     */
    public boolean addSubPanels(Container parent)
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.doAction(Constants.GRID, 0);  // Display the Grid screen
        
        return true;
    }
    /**
     * Process this action.
     * @param strAction The command to process.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        Container parent = null;
        FieldList record = null;
        if (m_topPane != null)
            parent = m_topPane.getParent();
        if (Constants.BACK.equalsIgnoreCase(strAction))
        {
            strAction = this.popHistory();  // Pop this screen
            strAction = this.popHistory();  // Last screen
        }
        if (Constants.FORM.equalsIgnoreCase(strAction))
        {
            JTable table = (JTable)JBasePanel.getSubScreen(this, JTable.class);
            ThinTableModel model = this.getModel(table);
            if (model != null)
            {
                int iSelectedRow = model.getSelectedRow();
                if (iSelectedRow != -1)
                    record = model.makeRowCurrent(iSelectedRow, false);
                if (record == null)
                    record = model.getFieldTable().getRecord();
                JBasePanel subScreen = null;
                if (table != null)
                    subScreen = (JBasePanel)this.getTargetScreen(table, JBasePanel.class);
                else
                    subScreen = (JBasePanel)JBasePanel.getSubScreen(this, JBasePanel.class);
                if (subScreen != null)
                    subScreen.free();
            
                parent.remove(m_topPane);
            }

            m_topPane = this.createNewScreen(strAction, record);
            m_topPane = this.setupNewScreen((JBaseScreen)m_topPane);
            m_topPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            m_topPane.setAlignmentY(Component.TOP_ALIGNMENT);
            parent.add(m_topPane, 0);
            this.pushHistory(strAction);

            parent.validate();
            return true;
        }
        else if (Constants.GRID.equalsIgnoreCase(strAction))
        {
//x            JScreen screen = (JScreen)JBasePanel.getSubScreen(this, JScreen.class);
            if (m_topPane != null)
            {
                if (m_topPane instanceof JBaseScreen)
                {
                    record = ((JBaseScreen)m_topPane).getFieldList();
                    ((JBaseScreen)m_topPane).free();              
                }
                parent.remove(m_topPane);
            }

            m_topPane = this.createNewScreen(strAction, record);
            m_topPane = this.setupNewScreen((JBaseScreen)m_topPane);
            if (parent == null)
                parent = this;
            m_topPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            m_topPane.setAlignmentY(Component.TOP_ALIGNMENT);
            parent.add(m_topPane, 0);
            this.pushHistory(strAction);

            parent.validate();
            return true;
        }
        return super.doAction(strAction, iOptions);
    }
    /**
     * Get the model in this screen's sub-screen.
     * Override if the model is not in the sub-screen.
     * @param table
     * @return
     */
    public ThinTableModel getModel(JTable table)
    {
        ThinTableModel model = null;
        if (table != null)
            model = (ThinTableModel)table.getModel();
        return model;
    }
    /**
     * Create a screen of this type.
     * NOTE: You must override this method and handle a GRID and FORM type!
     * @param strType The type of screen to create (GRID or FORM).
     * @param record The main record for the screen.
     */
    public JBaseScreen createNewScreen(String strType, Object record)
    {
        return null;
    }
    /**
     * Push this command onto the history stack.
     * NOTE: Do not use this method in most cases, use the method in BaseApplet.
     * @param strHistory The history command to push onto the stack.
     */
    public void pushHistory(String strHistory)
    {
        if (m_vHistory == null)
            m_vHistory = new Vector<String>();
        m_vHistory.addElement(strHistory);
    }
    /**
     * Pop this command off the history stack.
     * NOTE: Do not use this method in most cases, use the method in BaseApplet.
     * @return The history command on top of the stack.
     */
    public String popHistory()
    {
        String strHistory = null;
        if (m_vHistory != null) if (m_vHistory.size() > 0)
            strHistory = (String)m_vHistory.remove(m_vHistory.size() - 1);
        return strHistory;
    }
    /**
     * Add the toolbars?
     * @return The newly created toolbar or null.
     */
    public JComponent createToolbar()
    {
        if (m_topPane instanceof JBasePanel)
            return ((JBasePanel)m_topPane).createToolbar();
        return super.createToolbar();
    }
    /**
     * Add the toolbars?
     * @return The newly created toolbar or null.
     */
    public JMenuBar createMenubar()
    {
        if (m_topPane instanceof JBasePanel)
            return ((JBasePanel)m_topPane).createMenubar();
        return super.createMenubar();
    }
}
