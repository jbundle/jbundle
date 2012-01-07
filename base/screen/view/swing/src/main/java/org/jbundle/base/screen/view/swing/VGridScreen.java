/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.view.swing.grid.GridTableModel;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.model.db.Convert;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.screen.AbstractThinTableModel;


/**
 * The window for displaying several records at once.
 */
public class VGridScreen extends VBaseGridTableScreen
    implements TableColumnModelListener
{

    /**
     * Constructor.
     */
    public VGridScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VGridScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenField model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        JTable table = (JTable)this.getControl();
        if (table.isEditing())
            table.getCellEditor().stopCellEditing();	// Validate any cell being edited.
        table.getColumnModel().removeColumnModelListener(this);
        super.free();
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new (JTable) control.
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        JTable control = new JTable();
        control.setColumnSelectionAllowed(false); // By default, don't allow column selections
        control.setSurrendersFocusOnKeystroke(true);
        JScrollPane scrollpane = new JScrollPane(control);
        scrollpane.setPreferredSize(new Dimension(10,10));
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        control.setOpaque(false);
        panel.add(scrollpane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        control.getColumnModel().addColumnModelListener(this);

        return control;
    }
    /**
     * Calculate the physical screen size of this control.
     * <p/>For a GridWindow, survey all of the sub-controls and multiply by the rows and columns.
     * @param The location of this control in the parent.
     * @return The recommended control bounds.
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {
        Rectangle rect = super.calcBoxShape(ptLocation);    // Force the sub-controls to size
        rect.height = Math.min(450, rect.height * 10);
        JTable control = (JTable)this.getControl();
        if (control != null)
        {
            rect.width = control.getColumnModel().getTotalColumnWidth() + 3;
            rect.height = Math.min(450, Math.max(control.getRowHeight() * 10, 500));
        }
        return rect;
    }
    /**
     * Change the input focus to me!
     * @param sField The sub-field to focus.
     */
    public boolean requestFocus(ScreenField sField)
    {
        int iColumn = 0;
        for (int iIndex = 0; iIndex < ((GridScreen)this.getScreenField()).getSFieldCount(); iIndex++)
        {
            if (((GridScreen)this.getScreenField()).getSField(iIndex) instanceof ToolScreen)
                continue;
            if (sField == ((GridScreen)this.getScreenField()).getSField(iIndex))
                if (iColumn >= ((GridScreen)this.getScreenField()).getNavCount()) // Don't edit a nav button
            {
                int iRow = ((JTable)this.getControl()).getSelectedRow();
                return ((JTable)this.getControl()).editCellAt(iRow, iColumn);
            }
            iColumn++;
        }
        return false; // Not found
    }
    /**
     * Manually setup the JTable from this model.
     * Used in VGridScreen.
     */
    public void setupTableFromModel()
    {
        JTable control = (JTable)this.getControl();
        if (control != null)
        {
            m_gridTableModel = new GridTableModel((GridScreen)this.getScreenField());
            this.setupTableFromModel(control, m_gridTableModel);
        }
    }
    /**
     * Manually setup the JTable from this model.
     * ie., Don't take the default widths, set the column widths according to the field widths.
     * @param control The JTable.
     * @param model The table model.
     */
    public void setupTableFromModel(JTable control, GridTableModel model)
    {   // Create the table
        control.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        control.setAutoCreateColumnsFromModel(false);
        control.setColumnSelectionAllowed(false); // Don't allow column selections
        int iMaxHeight = 10;
        control.setModel(model);
        model.setGridScreen(control, false);
        GridScreen gridScreen = (GridScreen)this.getScreenField();
        for (int iIndex = 0; iIndex < gridScreen.getSFieldCount(); iIndex++)
        {
            ScreenField sField = gridScreen.getSField(iIndex);
            if (sField instanceof ToolScreen)
                continue;
            if (sField.getScreenFieldView().getControl(DBConstants.CONTROL_BOTTOM) != null)
                ((JComponent)sField.getScreenFieldView().getControl(DBConstants.CONTROL_BOTTOM)).setOpaque(true);   // No see-thru control for grids
            Rectangle rect = ((VScreenField)sField.getScreenFieldView()).getControlExtent();
            if (rect == null)
                rect = ((VScreenField)sField.getScreenFieldView()).calcBoxShape(new Point(0, 0));
            iMaxHeight = Math.max(iMaxHeight, rect.height);
            TableColumn newColumn = new TableColumn(iIndex, rect.width, (VScreenField)sField.getScreenFieldView(), (VScreenField)sField.getScreenFieldView());
            newColumn.setIdentifier(sField);    // So I can look up the sField

            if (sField.getConverter() != null)
            {
                String strFieldName = sField.getConverter().getFieldDesc();
                newColumn.setHeaderValue(strFieldName);
                if (this.getScreenInfo() != null)
                    if (rect.width < this.getScreenInfo().getFontMetrics().stringWidth(strFieldName))
                        if (newColumn.getHeaderRenderer() instanceof DefaultTableCellRenderer)
                            ((DefaultTableCellRenderer)newColumn.getHeaderRenderer()).setToolTipText(strFieldName);
            }
            else
                newColumn.setHeaderValue(Constants.BLANK);
            control.addColumn(newColumn);
        }
        control.setRowHeight(iMaxHeight);
        model.addMouseListenerToHeaderInTable(control);     // Notify model of row order changed (clicks in the header bar)
        if (this.getScreenField() != null)
            if (this.getScreenField().getConverter() != null)
                this.setComponentState(control, this.getScreenField().getConverter().getValue());
    }
    /**
     * The table changed on this row, update the JTable GUI.
     * @param iSelection The row to update.
     */
    public void tableChanged(int iSelection)
    {
        if (iSelection == -1)
            return;
        JTable jtable = (JTable)this.getControl();
        int iColumnSelection = -1;
        if (iSelection == jtable.getEditingRow())
        {   // Refreshing the data in the current row... stop editing
            iColumnSelection = jtable.getEditingColumn();
            jtable.removeEditor();
        }
        int rh = jtable.getRowHeight();
        Rectangle drawRect = new Rectangle(0, iSelection * rh,
                jtable.getColumnModel().getTotalColumnWidth(),
                   (jtable.getRowCount()-iSelection) * rh);
        jtable.revalidate();
        jtable.repaint(drawRect);
        
        if (iColumnSelection != -1)
            jtable.editCellAt(iSelection, iColumnSelection);
    }
    /**
     * Get the currently selected row.
     * @return The row number of the current row.
     */
    public int getSelectedRow()
    {
        int iSelection = ((JTable)this.getControl()).getSelectedRow();
        return iSelection;
    }
    /**
     * Update these records on the screen.
     * @param iIndexStart The starting row.
     * @param iIndexEnd The ending row.
     */
    public void fireTableRowsUpdated(int iIndexStart, int iIndexEnd)
    {
        m_gridTableModel.fireTableRowsUpdated(iIndexStart, iIndexEnd);
    }
    /**
     * The column selection changed - display the correct tooltip.
     * @param e The listselection event.
     */
    public void columnSelectionChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            int iColumn = ((JTable)this.getControl()).getSelectedColumn();
            if (iColumn != -1)
            {
                ScreenField sField = this.getSFieldAtColumn(iColumn);
                Convert converter = sField.getConverter();
                org.jbundle.model.Task baseApplet = ((GridScreen)this.getScreenField()).getTask();
                if (baseApplet != null)
                {
                    if (converter instanceof FieldInfo)
                        baseApplet.setStatusText(((FieldInfo)converter).getFieldTip());
                    else
                    {
                        String strTip = null;
                        if (sField instanceof SCannedBox)
                        {
                            strTip = ((SCannedBox)sField).getToolTip();
                            if (strTip == null)
                                strTip = ((SCannedBox)sField).getButtonDesc();
                            if (strTip == null)
                            {
                                strTip = ((SCannedBox)sField).getButtonCommand();
                                if (strTip != null)
                                {
                                    String strKey = strTip + Constants.TIP;
                                    strTip = baseApplet.getApplication().getResources(ResourceConstants.MENU_RESOURCE, true).getString(strKey);
                                    if (strTip == strKey)
                                        strTip = ((SCannedBox)sField).getButtonCommand();
                                }
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
     * Get the screen field at this column.
     * @param sField The sub-field to focus.
     */
    public ScreenField getSFieldAtColumn(int iTargetColumn)
    {
        int iColumn = 0;
        for (int iIndex = 0; iIndex < ((GridScreen)this.getScreenField()).getSFieldCount(); iIndex++)
        {
            if (((GridScreen)this.getScreenField()).getSField(iIndex) instanceof ToolScreen)
                continue;
            ScreenField sField = ((GridScreen)this.getScreenField()).getSField(iIndex);
            if (iTargetColumn == iColumn)
                return sField;
            iColumn++;
        }
        return null;    // Not found
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
     * Set the component to this state. State is defined by the component.
     * NOTE: The only component that would be hooked to a VGridScreen is a sort column value.
     *       Just calculate the correct columns that this is ALREADY sorted and display the order in the header.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        super.setComponentState(control, objValue);
        int iColumn = 0;
        if (objValue instanceof Number)
            iColumn = ((Number)objValue).intValue();
        else if (objValue != null)
        {
            try {
                iColumn = Integer.parseInt(objValue.toString());
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        boolean bOrder = Constants.ASCENDING;
        if (iColumn < 0)
        {
            bOrder = Constants.DESCENDING;
            iColumn = -iColumn;
        }
        this.setSortedByColumn(iColumn, bOrder);
    }
    public void setSortedByColumn(int iColumn, boolean bOrder)
    {
        JTable table = (JTable)this.getControl();
        TableModel model = table.getModel();
        if (model instanceof AbstractThinTableModel)
        {   // Always
            JTableHeader tableHeader = table.getTableHeader();
            iColumn = ((AbstractThinTableModel)model).columnToViewColumn(iColumn);  // Convert to view column
            ((AbstractThinTableModel)model).setSortedByColumn(tableHeader, iColumn, bOrder);
        }
    }
}
