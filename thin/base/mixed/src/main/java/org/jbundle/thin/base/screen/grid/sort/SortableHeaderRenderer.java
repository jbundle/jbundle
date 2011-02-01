/*
 * SortableHeaderRenderer.java
 *
 * Created on March 26, 2006, 10:06 PM
 */

package org.jbundle.thin.base.screen.grid.sort;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.jbundle.thin.base.db.Constants;


/**
 * A table header renderer that displays the ascending/descending icon on the sort column.
 * @author don
 */
public class SortableHeaderRenderer implements TableCellRenderer {
    private TableCellRenderer tableCellRenderer;
    private int m_iCurrentSortedColumn = NO_SORTED_COLUMN;
    private boolean m_bCurrentOrder = Constants.ASCENDING;
    
    public static final int NO_SORTED_COLUMN = -1;
    
    public static final String ASCENDING_ICON_NAME = "Ascending";
    public static final String DESCENDING_ICON_NAME = "Descending";
    public static Icon ASCENDING_ICON = null;
    public static Icon DESCENDING_ICON = null;

    /**
     * Constructor.
     */
    public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
        this.tableCellRenderer = tableCellRenderer;
    }
    /**
     * Tweek the column label if this column is sorted.
     */
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        Component c = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (c instanceof JLabel)
        {
            JLabel l = (JLabel) c;
            l.setHorizontalTextPosition(JLabel.LEFT);
            if (column == m_iCurrentSortedColumn)
            {
                if (l.getIcon() == null)
                    l.setIcon(getHeaderRendererIcon(m_bCurrentOrder));
            }
            else
            {
                if ((l.getIcon() == ASCENDING_ICON)
                    || (l.getIcon() == DESCENDING_ICON))
                        l.setIcon(null);
            }
        }
        return c;
    }
    /**
     * Get the ascending/descending icon.
     */
    protected Icon getHeaderRendererIcon(boolean bCurrentOrder) {
        // Get current classloader
        ClassLoader cl = this.getClass().getClassLoader();
        // Create icons
        try   {
            if (ASCENDING_ICON == null)
            {
                ASCENDING_ICON  = new ImageIcon(cl.getResource("images/buttons/" + ASCENDING_ICON_NAME + ".gif"));
                DESCENDING_ICON  = new ImageIcon(cl.getResource("images/buttons/" + DESCENDING_ICON_NAME + ".gif"));
            }
            return bCurrentOrder ? ASCENDING_ICON : DESCENDING_ICON;
        } catch (Exception ex)  {
        }
        return null;
    }
    /**
     * Sort this table by this column.
     * Override this to sort by column.
     * @param iColumn The column to sort by.
     * @param bAscending True if ascending.
     */
    public void setSortedByColumn(JTableHeader tableHeader, int iColumn, boolean bOrder)
    {
        m_iCurrentSortedColumn = iColumn;
        m_bCurrentOrder = bOrder;
//?        tableHeader.repaint();
    }
    /**
     * Sort this table by this column.
     * Override this to sort by column.
     * @param iColumn The column to sort by.
     * @param bAscending True if ascending.
     */
    public int getSortedByColumn()
    {
        return m_iCurrentSortedColumn;
    }
    /**
     * Sort this table by this column.
     * Override this to sort by column.
     * @param iColumn The column to sort by.
     * @param bAscending True if ascending.
     */
    public boolean getSortedOrder()
    {
        return m_bCurrentOrder;
    }
}
