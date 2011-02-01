package org.jbundle.thin.base.screen.grid.opt;

/**
 * JGridScreen.java:    Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.jbundle.thin.base.screen.JBaseScreen;


/**
 * JGridScreen is a screen which tries to resemble a JTable with a GridLayout.
 * Most of the time, you should NOT use this class,
 * you should use JTable, except where the table is very simple
 * or you need to have different sized rows.
 */
public class JAltGridScreen extends JBaseScreen implements TableModelListener
{
	private static final long serialVersionUID = 1L;

	/**
     * The top-level panel to hold the grid items (placed inside a scroller).
     */
    public JPanel m_panelGrid = null;
    /**
     * The heading labels.
     */
    public JLabel m_rgcompHeadings[] = null;
    /**
     * A vector of the components in each row (the elements have the column components).
     */
    public Vector<ComponentCache> m_vComponentCache = null;
    /**
     * The table model I'm displaying.
     */
    public TableModel m_model = null;

    /**
     * A special class to cache the physical screen components on this row.
     */
    public class ComponentCache extends Object
    {
        /**
         * The row these components are on.
         */
        public int m_iRow = -1;
        /**
         * The screen components on this row.
         */
        public Component m_rgcompoments[] = null;
    
        /**
         * Create a component cache row.
         * @param iRow The row these components are on.
         * @param rgcomponents The screen components on this row.
         */
        public ComponentCache(int iRow, Component rgcomponents[])
        {
            m_iRow = iRow;
            m_rgcompoments = rgcomponents;
        }
    }

    /**
     *  JGridScreen Class Constructor.
     */
    public JAltGridScreen()
    {
        super();
    }
    /**
     *  JGridScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JAltGridScreen(Object parent, TableModel model)
    {
        this();
        this.init(parent, model);
    }
    /**
     * Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, TableModel model)
    {
        super.init(parent, model);
        this.setModel(model);
    }
    /**
     * Setup a new panel.
     */
    public void setupPanel()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.addGridHeading();

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setMinimumSize(new Dimension(20, 20));
        panel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane);
        this.add(panel);
        m_panelGrid = new JPanel();
        JPanel panelAligner = new JPanel(); // This panel keeps the panelgrid in the upper left hand of the scrollpane.
        panelAligner.add(m_panelGrid);
        panelAligner.setLayout(new FlowLayout(FlowLayout.LEADING));
        scrollPane.getViewport().add(panelAligner);
        panelAligner.setOpaque(false);
        m_panelGrid.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        this.addGridDetail();
    }
    /**
     * Get the model.
     * @return The table model.
     */
    public TableModel getModel()
    {
        return m_model;
    }
    /**
     * Set a (new) model.
     * @param model The table model.
     */
    public void setModel(TableModel model)
    {
// Column 3 and 4: (Scroll Pane Headings)
        if (m_model != null)
            m_model.removeTableModelListener(this);
        m_model = model;
        if (model != null)
            model.addTableModelListener(this);
        this.setupPanel();      // setup a new panel
    }
    /**
     * Add the column <quantity> <description> headings to the grid panel.
     */
    public void addGridHeading()
    {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setAlignmentY(TOP_ALIGNMENT);
        this.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        m_rgcompHeadings = new JLabel[this.getModel().getColumnCount()];
        for (int iColumnIndex = 0; iColumnIndex < m_rgcompHeadings.length; iColumnIndex++)
        {
            panel.add(m_rgcompHeadings[iColumnIndex] = new JLabel(this.getModel().getColumnName(iColumnIndex) + " "));
        }
    }
    /**
     * Read through the table model and add add the items to the grid panel.
     */
    public void addGridDetail()
    {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        m_panelGrid.setLayout(gridbag);

        this.addGridDetailItems(this.getModel(), gridbag, c);
    }
    /**
     * Read through the table model and add add the items to the grid panel.
     * @param model The table model to read through.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public void addGridDetailItems(TableModel model, GridBagLayout gridbag, GridBagConstraints c)
    {
        m_vComponentCache = new Vector<ComponentCache>();

        for (int iRow = 0; iRow < model.getRowCount(); iRow++)
        {
            c.weightx = 0.0;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;     // Edit boxes right justified
            c.insets.right = 5;                     // Add a few pixels of white space to the right
            this.addGridDetailItem(model, iRow, gridbag, c);
        }
    }
    /**
     * Add this item to the grid detail at this row.
     * @param model The table model to read through.
     * @param iRowIndex The row to add this item.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public void addGridDetailItem(TableModel model, int iRowIndex, GridBagLayout gridbag, GridBagConstraints c)
    {
        JComponent rgcompoments[] = new JComponent[model.getColumnCount()];
        for (int iColumnIndex = 0; iColumnIndex < rgcompoments.length; iColumnIndex++)
        {
            Object obj = model.getValueAt(iRowIndex, iColumnIndex);
            if ((obj == null) || (model.getRowCount() <= iRowIndex))
                return;     // EOF
            if (iColumnIndex == rgcompoments.length - 1)
            {   // Last column - take remainder
                c.weightx = 1.0;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                c.anchor = GridBagConstraints.WEST;     // Edit boxes left justified
                c.insets.right = 5;
            }
            Component component = this.addDetailComponent(model, obj, iRowIndex, iColumnIndex, c);
            if (component == null)
                continue;   // Skip this column
            gridbag.setConstraints(component, c);
            m_panelGrid.add(component);
            rgcompoments[iColumnIndex] = (JComponent)component;
        }
            // Set up a table to lookup the item<->textfield link
        m_vComponentCache.addElement(new ComponentCache(iRowIndex, rgcompoments));
    }
    /**
     * Create the appropriate component and add it to the grid detail at this location.
     * @param model The table model to read through.
     * @param iRowIndex The row to add this item.
     * @param iColumnIndex The column index of this component.
     * @param c The constraint to use.
     */
    public Component addDetailComponent(TableModel model, Object aValue, int iRowIndex, int iColumnIndex, GridBagConstraints c)
    {
        JComponent component = null;
        String string = "";
        if (aValue instanceof ImageIcon)
        {
            component = new JLabel((ImageIcon)aValue);
        }
        else if (aValue != null)
        {
            string = aValue.toString();
            if (model.isCellEditable(iRowIndex, iColumnIndex))
            {
                if (string.length() == 0)
                    component = new JTextField(3);
                else
                    component = new JTextField(string);
            }
            else
                component = new JLabel(string);
        }
        return component;
    }
    /**
     * Clear all the fields to their default values.
     */
    public void resetFields()
    {
        super.resetFields();
        for (int iRow = 0; iRow < m_vComponentCache.size(); iRow++)
        {
            ComponentCache componentCache = (ComponentCache)m_vComponentCache.elementAt(iRow);
            if (componentCache != null)
            {
                for (int iColumnIndex = 0; iColumnIndex < componentCache.m_rgcompoments.length; iColumnIndex++)
                {
                    if (componentCache.m_rgcompoments[iColumnIndex] != null)
                    {
                        if (componentCache.m_rgcompoments[iColumnIndex] instanceof JTextComponent)
                            ((JTextComponent)componentCache.m_rgcompoments[iColumnIndex]).setText("");
                        else if (componentCache.m_rgcompoments[iColumnIndex] instanceof JCheckBox)
                            ((JCheckBox)componentCache.m_rgcompoments[iColumnIndex]).setSelected(false);
                    }
                }
            }
        }
    }
    /**
     * Move the controls to the data record(s).
     */
    public void controlsToFields()
    {
        super.controlsToFields();
        for (int iRowIndex = 0; iRowIndex < m_vComponentCache.size(); iRowIndex++)
        {
            ComponentCache componentCache = (ComponentCache)m_vComponentCache.elementAt(iRowIndex);
            if (componentCache != null)
            {   // Move the data to the model
                for (int iColumnIndex = 0; iColumnIndex < componentCache.m_rgcompoments.length; iColumnIndex++)
                {
                    this.dataToField(componentCache, iRowIndex, iColumnIndex);
                }
            }
        }
    }
    /**
     * Change the table by setting this field to the value in the component at this location.
     * @param componentCache The cached components on this row.
     * @param iRowIndex The row to set.
     * @param ColumnIndex The column to set.
     */
    public void dataToField(ComponentCache componentCache, int iRowIndex, int iColumnIndex)
    {
        if (componentCache.m_rgcompoments[iColumnIndex] != null)
        {
            String string = null;
            if (componentCache.m_rgcompoments[iColumnIndex] instanceof JTextComponent)
                string = ((JTextComponent)componentCache.m_rgcompoments[iColumnIndex]).getText();
            if (componentCache.m_rgcompoments[iColumnIndex] instanceof JCheckBox)
                if (((JCheckBox)componentCache.m_rgcompoments[iColumnIndex]).isSelected())
                    string = "1";
            //xif (string != null) if (string.length() > 0)
            this.getModel().setValueAt(string, iRowIndex, iColumnIndex);
        }
    }
    /**
     * Move the the data record(s) to the screen controls.
     */
    public void fieldsToControls()
    {
        super.fieldsToControls();
        for (int iRowIndex = 0; iRowIndex < m_vComponentCache.size(); iRowIndex++)
        {
            ComponentCache componentCache = (ComponentCache)m_vComponentCache.elementAt(iRowIndex);
            if (componentCache != null)
            {   // Move the data to the model
                for (int iColumnIndex = 0; iColumnIndex < componentCache.m_rgcompoments.length; iColumnIndex++)
                {
                    this.fieldToData(componentCache, iRowIndex, iColumnIndex);
                }
            }
        }
    }
    /**
     * Move the data in this row and column in the table to this component.
     * @param componentCache The cached components on this row.
     * @param iRowIndex The row to set.
     * @param ColumnIndex The column to set.
     */
    public void fieldToData(ComponentCache componentCache, int iRowIndex, int iColumnIndex)
    {
        if (componentCache.m_rgcompoments[iColumnIndex] != null)
        {
            Object object = this.getModel().getValueAt(iRowIndex, iColumnIndex);
            String string = "";
            if (object != null)
                string = object.toString();
            if (componentCache.m_rgcompoments[iColumnIndex] instanceof JTextComponent)
                ((JTextComponent)componentCache.m_rgcompoments[iColumnIndex]).setText(string);
            else if (componentCache.m_rgcompoments[iColumnIndex] instanceof JCheckBox)
            {
                boolean bIsSelected = false;
                if (string != null) if (string.equals("1"))
                    bIsSelected = true;
                ((JCheckBox)componentCache.m_rgcompoments[iColumnIndex]).setSelected(bIsSelected);
            }
        }
    }
    /**
     * The table has changed, update the grid.
     * @param e The TableModelEvent.
     */
    public void tableChanged(TableModelEvent e)
    {
        if ((e.getFirstRow() == TableModelEvent.HEADER_ROW) ||
                (e.getLastRow() >= m_vComponentCache.size()))
        { // Entire table changed, rebuild table
            this.removeAll();
            this.setupPanel();
            this.validate();
        }
        else if (e.getType() == TableModelEvent.UPDATE)
        {
            int iFirstRow = e.getFirstRow();
            int iLastRow = e.getLastRow();
            for (int iRowIndex = iFirstRow; iRowIndex <= iLastRow; iRowIndex++)
            {
                if (iRowIndex < m_vComponentCache.size())
                {
                    ComponentCache componentCache = (ComponentCache)m_vComponentCache.elementAt(iRowIndex);
                    if (componentCache != null)
                    {   // Move the data to the model
                        for (int iColumnIndex = 0; iColumnIndex < componentCache.m_rgcompoments.length; iColumnIndex++)
                        {
                            this.fieldToData(componentCache, iRowIndex, iColumnIndex);
                        }
                    }
                }
                else
                    break;
            }
        }
    }
}
