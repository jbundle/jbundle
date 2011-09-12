/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.grid;

/**
 * OrderEntry.java:   Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Component;
import java.util.EventObject;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.model.Freeable;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.util.JFSImage;


/**
 * A image with a few extra capabilities.
 * This image works in grids and links to a converter.
 */
public class JCellImage extends JFSImage
    implements TableCellRenderer, TableCellEditor, Freeable
{
	private static final long serialVersionUID = 1L;

	protected Converter m_converter = null;

    /**
     * Creates new JCellButton.
     */
    public JCellImage()
    {
        super();
    }
    /**
     * Creates new JCellButton.
     * @param icon The button icon.
     */
    public JCellImage(ImageIcon image)
    {
        this();
        this.init(image, null);
    }
    /**
     * Creates new JCellButton.
     * @param icon The button icon.
     */
    public JCellImage(Converter converter)
    {
        this();
        this.init(null, converter);
    }
    /**
     * Creates new JCellButton. The icon and text are reversed, because of a conflicting method in JButton.
     * @param text the button text.
     * @param icon The button icon.
     */
    public void init(ImageIcon image, Converter converter)
    {
        super.init(image);
        m_converter = converter;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        m_converter = null;
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Converter getConverter()
    {
        return m_converter;
    }
    /**
     * Get the renderer for this location in the table.
     * From the TableCellRenderer interface.
     * @return this.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
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
        this.setControlValue(value);
        return this;
    }
    /**
     * Get the editor for this location in the table.
     * From the TableCellEditor interface.
     * @return this.
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        return this;    // Never (since editing is OFF).
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
        return false;//true;
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
