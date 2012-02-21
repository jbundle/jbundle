/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import org.jbundle.base.db.GridTable;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.CustomReportScreen;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.screen.AbstractThinTableModel;
import org.jbundle.thin.base.screen.grid.ThinTableModel;
import org.jbundle.thin.base.screen.print.ScreenPrinter;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * This is the base screen for custom reports.
 * You can build a report that prints the controls--as laid out on the report.
 * This class has the ability to set up a custom control to be printed.
 * This is usually used for special format reports, such as labels.
 */
public class VCustomReportScreen extends VBaseReportScreen
{
    /**
     * This is the "Fake" control that will be used to print the screen for each record.
     */
    protected Component m_controlForPrint = null;

    /**
     * Constructor.
     */
    public VCustomReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VCustomReportScreen(ScreenField model, boolean bEditableControl)
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
        m_controlForPrint = null;
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        MyJTable control = new MyJTable();
        control.setPreferredScrollableViewportSize(new Dimension(500, 70));
        control.setShowGrid(false);
        control.setIntercellSpacing(new Dimension(0, 0));
       
        JScrollPane scrollpane = new JScrollPane();
        JViewport vp = scrollpane.getViewport();
        vp.add(control);

        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        scrollpane.setPreferredSize(new Dimension(10,10));
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(scrollpane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        m_controlForPrint = this.setupPrintControl();

        return control;
    }
    /**
     * Get one of the physical components associated with this SField.
     * @param int iLevel    CONTROL_TOP - Parent physical control; CONTROL_BOTTOM - Lowest child control
     * @return The control at this level.
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
        if (iLevel == DBConstants.CONTROL_BOTTOM)
            return m_controlForPrint; // Make sure controls are not directly added to this control
        return super.getControl(iLevel);
    }
    /**
     * Setup this screen's screen layout.
     * Usually, you use JAVA layout managers, but you may also use ScreenLayout.
     * @return The new screen layout.
     */
    public LayoutManager addScreenLayout()
    {
        LayoutManager screenLayout = null;
        if (this.getScreenLayout() == null)   // Only if no parent screens
        {   // EVERY BasePanel gets a ScreenLayout!
        }
        return screenLayout;
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param bUseSameWindow If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand)
    {
        boolean bFlag = false;
        if (strCommand.equalsIgnoreCase(ThinMenuConstants.PRINT))
        {           
            Component control = this.getControl(DBConstants.CONTROL_BOTTOM);
            this.layoutPrintControl(control);
            
            MyJTable table = (MyJTable)this.getControl();
            GridTable gridTable = (GridTable)((CustomReportScreen)this.getScreenField()).getMainRecord().getTable();
            AbstractThinTableModel model = new ThinTableModel(gridTable);
            model.setAppending(false);
            table.setModel(model);
            
            table.setTargetPanel(m_controlForPrint);
            
            return ScreenPrinter.onPrint(table, false); // No headers, footers
        }
        if (bFlag == false)
            bFlag = super.doCommand(strCommand);    // This will send the command to my parent
        return bFlag;
    }

    /**
     * Setup the print control for this custom report screen.
     * Override this if you don't want a JPanel.
     * @return A component
     */
    public Component setupPrintControl()
    {
        JPanel control = new JPanel();   // Set up the "fake" control to render on print
        control.setOpaque(false);
        control.setLayout(null);
        control.setBounds(0, 0, (int)(7.5 * 72), 10 * 72);
        control.setBackground(Color.WHITE); // Just being careful
        return control;
    }
    /**
     * Set up the physical control (that implements Component).
     */
    public void layoutPrintControl(Component control) // Must o/r
    {
        // Override this to do something
    }
    /**
     * Setup the standard attributes of a component to print.
     */
    public void setupComponent(JComponent component, int x, int y, int width, int height)
    {
        component.setBounds(x, y, width, height);
        component.setBorder(null);
        component.setOpaque(false);
        if (component instanceof JScrollPane)
        {
            ((JScrollPane)component).setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            ((JScrollPane)component).setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            ((JScrollPane)component).getViewport().setOpaque(false);
            component = (JComponent)((JScrollPane)component).getViewport().getView();
            this.setupComponent(component, 0, 0, width, height);
        }
        else
        {
            component.setForeground(Color.black);
            component.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (component instanceof JTextComponent)
                ((JTextComponent)component).setText("This is the extra text. This is the extra text. This is the extra text. This is the extra text. This is the extra text. This is more extra text This is more extra text This is more extra text This is more extra text this is the last extra text");
            if (component instanceof JTextArea)
            {
                ((JTextArea)component).setWrapStyleWord(true);
                ((JTextArea)component).setLineWrap(true);
            }
        }
    }

    class MyJTable extends JTable
    {
        private static final long serialVersionUID = 1L;

        protected Component m_targetPanel = null;
        protected int m_iHeight = 0;
        
        public MyJTable()
        {
            super();
            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            this.setAutoCreateColumnsFromModel(false);
        }
        public void setTargetPanel(Component targetPanel)
        {
            m_targetPanel = targetPanel;

            if (this.getColumnModel().getColumnCount() == 0)    // Always
                this.getColumnModel().addColumn(new TableColumn(0, m_targetPanel.getWidth()));
            m_iHeight = m_targetPanel.getHeight();
        }
        public TableCellRenderer getCellRenderer(int row, int column)
        {
            return new MyTableCellRenderer(m_targetPanel);
        }
        public int getRowHeight() {
            return m_iHeight; //m_targetPanel.getHeight();
        }
    }
    
    class MyTableCellRenderer extends Object
        implements TableCellRenderer
    {
        protected Component m_component = null;
        
        public MyTableCellRenderer(Component component)
        {
            super();
            m_component = component;
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            return m_component;
        }
    };
}
