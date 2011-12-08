/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.grid;

/**
 * JScreen.java:    Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.AbstractThinTableModel;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseGridScreen;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.thin.base.screen.action.ActionManager;


/**
 * A Basic data entry screen.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public class JGridScreen extends JBaseGridScreen
    implements TableColumnModelListener
{
	private static final long serialVersionUID = 1L;

	/**
     * The model for the table.
     */
    protected AbstractThinTableModel m_thinTableModel = null;
    /**
     * The model's JTable screen.
     */
    protected JTable m_jTableScreen = null;

    /**
     *  JScreen Class Constructor.
     */
    public JGridScreen()
    {
        super();
    }
    /**
     *  JScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JGridScreen(Object parent, Object record)
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

        this.addSubPanels(this);
    }
    /**
     * Free.
     */
    public void free()
    {
    	if (getJTable() != null)
    		if (getJTable().isEditing())
    			getJTable().getCellEditor().stopCellEditing();	// Validate any cell being edited.
        if (m_thinTableModel != null)
        	m_thinTableModel.free();
        m_thinTableModel = null;
    	super.free();
    }
    /**
     * Get the grid model for this screen's JTable.
     * @return This screen's table model.
     */
    public AbstractThinTableModel getGridModel()
    {
        return m_thinTableModel;
    }
    /**
     * Get the this screen's JTable.
     * @return This screen's JTable.
     */
    public JTable getJTable()
    {
        return m_jTableScreen;
    }
    /**
     * Build the list of fields that make up the screen.
     * Override this to create a new record.
     * @return The fieldlist for this screen.
     * <pre>
     * return new TestGridModel(record.getFieldTable());
     * </pre>
     */
    public AbstractThinTableModel createGridModel(FieldList record)
    {
        return null;
    }
    /**
     * Add any applet sub-panel(s) now.
     * @return true if success
     */
    public boolean addSubPanels(Container parent)
    {
        parent.setLayout(new BorderLayout());
        FieldList record = this.getFieldList();
        if (m_thinTableModel == null)
            m_thinTableModel = this.createGridModel(record);
        m_jTableScreen = new JTable();
        m_jTableScreen.setOpaque(false);
        m_jTableScreen.setSurrendersFocusOnKeystroke(true);

        m_thinTableModel.setGridScreen(m_jTableScreen, true);
        for (int iIndex = 0; iIndex < m_thinTableModel.getColumnCount(); iIndex++)
        {
            TableColumn newColumn = m_jTableScreen.getColumnModel().getColumn(iIndex);
            if (newColumn.getCellEditor() instanceof JCellButton)
                ((JCellButton)newColumn.getCellEditor()).addActionListener(this);
        }
        m_jTableScreen.getColumnModel().addColumnModelListener(this);

        JScrollPane scrollpane = new JScrollPane(m_jTableScreen);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);

        parent.add(scrollpane);
        return true;
    }
    /**
     * Process this action.
     * @param strAction The action to process.
     * By default, this method handles RESET, SUBMIT, and DELETE.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        FieldList record = this.getFieldList();
        FieldTable fieldTable = null;
        if (record != null)
            fieldTable = record.getTable();
        boolean bFlag = false;
        try   {
            bFlag = super.doAction(strAction, iOptions);
            if (bFlag)
                return bFlag;
            if (Constants.FORM.equalsIgnoreCase(strAction))
            {
                record = this.makeSelectedRowCurrent();
                JBaseScreen screen = this.createMaintScreen(record);
                if (screen != null)
                {
                    Container parent = null;
                    this.getFieldList().setOwner(screen);    // The record belongs to the new screen
                    if (screen.getParentObject() instanceof Container)
                        parent = (Container)screen.getParentObject();
                    this.getBaseApplet().changeSubScreen(parent, screen, null);
                    bFlag = true;
                }
            }
            if (Constants.SUBMIT.equalsIgnoreCase(strAction))
            {
                if (m_jTableScreen.isEditing())
                    m_jTableScreen.editCellAt(m_jTableScreen.getEditingRow(), m_jTableScreen.getEditingColumn()); // Validate current
                m_thinTableModel.updateIfNewRow(-1);    // Update any locked record.
                this.repaintCurrentRow();
                bFlag = true;
            }
            if (Constants.DELETE.equalsIgnoreCase(strAction))
            {
                record = this.makeSelectedRowCurrent();
                if (record != null)
                {
                    if ((record.getEditMode() == Constants.EDIT_IN_PROGRESS) ||
                        (record.getEditMode() == Constants.EDIT_CURRENT))
                    {
                        fieldTable.remove();
                    }
                    fieldTable.addNew();
                    this.repaintCurrentRow();
                    bFlag = true;
                }
            }
            if (Constants.RESET.equalsIgnoreCase(strAction))
            {
                int iRow = m_jTableScreen.getSelectedRow();
                if (iRow != -1)
                {
                    if (m_jTableScreen.isEditing())
                        m_jTableScreen.removeEditor();
                    m_thinTableModel.makeRowCurrent(-1, false);   // Invalidate current row
                    m_thinTableModel.makeRowCurrent(iRow, false); // Re-read selected row
                    this.repaintCurrentRow();
                }
                bFlag = true;   // Handled by this screen
            }
        } catch (DBException ex)  {
            this.getBaseApplet().setStatusText(ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        return bFlag;
    }
    /**
     * Repaint the current row.
     */
    public void repaintCurrentRow()
    {
        m_jTableScreen.removeEditor();  // This is needed to make sure editors for the delete button are removed.
        int iRow = m_jTableScreen.getSelectedRow();
        int iColumn = m_jTableScreen.getColumnCount() - 1;
        Rectangle rect = m_jTableScreen.getCellRect(iRow, iColumn, false);
        rect.width = rect.width + rect.x;
        rect.x = 0;
        m_jTableScreen.repaint(rect);
    }
    /**
     * Make the currently selected row current, or create a newrecord is no current selection.
     * @return The fieldlist with the currently selected record or a new record.
     */
    public FieldList makeSelectedRowCurrent()
    {
        FieldList record = null;
        int iRow = m_jTableScreen.getSelectedRow();
        if (iRow != -1)
            record = m_thinTableModel.makeRowCurrent(iRow, false);
        if (record == null)
        {
            record = this.getFieldList();
            try   {
                record.getTable().addNew();
            } catch (DBException ex)    {
                ex.printStackTrace(); // Never.
            }
        }
        return record;
    }
    /**
     * Create a maint screen for this form.
     * @param record the (optional) record for this screen.
     * @return The new grid screen.
     */
    public JBaseScreen createMaintScreen(FieldList record)
    {
        return null;
    }
    /**
     * Add the scrollbars?
     * For grid screens, default to false, since the scrollbars are above the JTable.
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
        return new JGridScreenToolbar(this, null);
    }
    /**
     * Required for TableColumnModelListener interface.
     */
    public void columnAdded(TableColumnModelEvent e)
    {
    }
    /**
     * Required for TableColumnModelListener interface.
     */
    public void columnMarginChanged(ChangeEvent e)
    {
    }
    /**
     * Required for TableColumnModelListener interface.
     */
    public void columnMoved(TableColumnModelEvent e)
    {
    }
    /**
     * Required for TableColumnModelListener interface.
     */
    public void columnRemoved(TableColumnModelEvent e)
    {
    }
    /**
     * The column selection changed - display the correct tooltip.
     * @param e The listselection event.
     */
    public void columnSelectionChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            int iColumn = m_jTableScreen.getSelectedColumn();
            if (iColumn != -1)
                if (m_thinTableModel != null)   // In case this was freed before I got the message
            {
                Converter converter = m_thinTableModel.getFieldInfo(iColumn);
                BaseApplet baseApplet = this.getBaseApplet();
                if (baseApplet != null)
                {
                    if (converter instanceof FieldInfo)
                        baseApplet.setStatusText(((FieldInfo)converter).getFieldTip());
                    else
                    {
                        TableColumnModel columnModel = m_jTableScreen.getTableHeader().getColumnModel();
                        TableColumn tableColumn = columnModel.getColumn(iColumn);
                        TableCellEditor editor = tableColumn.getCellEditor();
                        String strTip = null;
                        if (editor instanceof JComponent)
                        {
                            String strName = ((JComponent)editor).getName();
                            if (strName != null)
                            {
                                strName += Constants.TIP;
                                strTip = baseApplet.getString(strName);
                                if (strTip == strName)
                                    strTip = baseApplet.getString(((JComponent)editor).getName());
                            }
                        }
                        baseApplet.setStatusText(strTip);
                    }
                    String strLastError = baseApplet.getLastError(0);
                    if ((strLastError != null) && (strLastError.length() > 0))
                        baseApplet.setStatusText(strLastError);
                }
            }
        }
    }
    /**
     * Add the menubars?
     * @return The newly created menubar or null.
     */
    public JMenuBar createMenubar()
    {
        JMenuBar menuBar = ActionManager.getActionManager().setupStandardMenu(this);
        
        return menuBar;
    }
}
