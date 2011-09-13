/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.grid;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.JScreen;


/** 
 * A JCellButton is a button that works in a JTable.
 * Add this code to make it work:
 * <pre>
 *  JCellButton button = new JCellButton(icon);
 *  newColumn.setCellEditor(button);
 *  button.addActionListener(this);
 *  button = new JCellButton(icon);
 *  newColumn.setCellRenderer(button);
 * </pre>
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JCellButton extends JButton
    implements TableCellRenderer, TableCellEditor
{
	private static final long serialVersionUID = 1L;

	/**
     * Creates new JCellButton.
     */
    public JCellButton()
    {
        super();
    }
    /**
     * Creates new JCellButton.
     * @param icon The button icon.
     */
    public JCellButton(Icon icon)
    {
        this();
        this.init(icon, null);
    }
    /**
     * Creates new JCellButton.
     * @param text the button text.
     */
    public JCellButton(String text)
    {
        this();
        this.init(null, text);
    }
    /**
     * Creates new JCellButton.
     * @param text the button text.
     * @param icon The button icon.
     */
    public JCellButton(String text, Icon icon)
    {
        this();
        this.init(icon, text);
    }
    /**
     * Creates new JCellButton. The icon and text are reversed, because of a conflicting method in JButton.
     * @param text the button text.
     * @param icon The button icon.
     */
    public void init(Icon icon, String text)
    {
        this.setText(text);
        this.setIcon(icon);
        this.setMargin(Constants.NO_INSETS);
    }
    /**
     * Get the renderer for this location in the table.
     * From the TableCellRenderer interface.
     * @return this.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value == null)
            this.setEnabled(false);
        else
            this.setEnabled(true);
        if (hasFocus)
        {
            this.setOpaque(false);
            this.setBorder(JScreen.m_borderLine);
        }
        else
        {
            this.setOpaque(isSelected);
            if (isSelected)
                this.setBackground(table.getSelectionBackground());
            this.setBorder(null);
        }
        return this;
    }
    /**
     * Get the editor for this location in the table.
     * From the TableCellEditor interface.
     * @return this.
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        return this;
    }
    /**
     * Add a cell editor listener.
     * From the TableCellEditor interface.
     * Empty method.
     */
    public void addCellEditorListener(CellEditorListener l)
    {
    }
    /**
     * Cancel editing.
     * From the TableCellEditor interface.
     * Empty method.
     */
    public void cancelCellEditing()
    {
    }
    /**
     * Get the value.
     * From the TableCellEditor interface.
     * @return null A button doesn't have a value.
     */
    public Object getCellEditorValue()
    {
        return null;
    }
    /**
     * Is this cell editable.
     * From the TableCellEditor interface.
     * @return true So you can click the button.
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        return true;
    }
    /**
     * Remove the editor listener.
     * From the TableCellEditor interface.
     * Empty method.
     */
    public void removeCellEditorListener(CellEditorListener l) 
    {
    }
    /**
     * Should I select this cell.
     * From the TableCellEditor interface.
     * @return true for a button.
     */
    public boolean shouldSelectCell(EventObject anEvent)
    {
        return true;
    }
    /**
     * Stop cell editing.
     * From the TableCellEditor interface.
     * @return true for a button.
     */
    public boolean stopCellEditing()
    {
        return true;
    }
}
