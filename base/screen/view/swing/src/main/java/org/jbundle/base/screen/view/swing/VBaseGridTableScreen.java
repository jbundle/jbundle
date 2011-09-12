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
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.message.record.RecordMessageHeader;
import org.jbundle.base.screen.model.BaseGridTableScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.ScreenFieldView;
import org.jbundle.base.screen.view.swing.grid.GridTableModel;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.util.Application;


/**
 * The window for displaying several records at once.
 */
public class VBaseGridTableScreen extends VBaseGridScreen
{
    /**
     * Table model for this grid.
     */
    protected GridTableModel m_gridTableModel = null;

    /**
     * Constructor.
     */
    public VBaseGridTableScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VBaseGridTableScreen(ScreenField model, boolean bEditableControl)
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
        Component table = this.getControl();

        JScrollPane scrollPane = (JScrollPane)table.getParent().getParent();
        table.getParent().remove(table);
        scrollPane.getParent().remove(scrollPane);
        this.setControl(null);
        if (m_gridTableModel != null)
            m_gridTableModel.free();
        m_gridTableModel = null;
        super.free();
    }
    /**
     * Get the grid model.
     * @return The grid table model.
     */
    public GridTableModel getModel()
    {
        return m_gridTableModel;
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
        if (iLevel == DBConstants.CONTROL_BOTTOM)
            return null;    // Make sure controls are not directly added to this JTable
        return super.getControl(iLevel);
    }
    /**
     * Get the size of this screen.
     * @return The screen size.
     */
    public Rectangle getControlExtent()
    {
        Rectangle rect = super.getControlExtent();
        if (rect != null)
        {
            rect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
        }
        return rect;
    } // Current size of the control
    /**
     * Calculate the physical screen size of this control.
     * <p/>For a GridWindow, survey all of the sub-controls and multiply by the rows and columns.
     * @param The location of this control in the parent.
     * @return The recommended control bounds.
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {
        Rectangle rect = super.calcBoxShape(ptLocation);    // Force the sub-controls to size
        return rect;
    }
    /**
     * Setup this screen's screen layout.
     * Don't set it up for a grid screen.
     * @return The screen layout (none here).
     */
    public LayoutManager addScreenLayout()
    {
        return null;
    }
    /**
     * User double-clicked the mouse button.
     * If use double-clicks a row, open a new window with the record.
     * @param evt The mouse event.
     */
    public void onDoubleClick(MouseEvent evt)
    {
        super.onDoubleClick(evt);
        int iSelection = this.getSelectedRow();
        if (iSelection > 0)
        {
            Record record = ((BaseGridTableScreen)this.getScreenField()).getMainRecord();
            boolean bReadSameRecord = (iSelection != -1);
            BasePanel parentScreen = Screen.makeWindow(((BaseGridTableScreen)this.getScreenField()).getTask().getApplication());
            record.makeScreen(null, parentScreen, ScreenConstants.MAINT_MODE, true, bReadSameRecord, true, true, null);
        }
    }
    /**
     * Requery the recordset.
     */
    public void reSelectRecords()
    {
        Record record = ((BaseGridTableScreen)this.getScreenField()).getMainRecord();
        if (record != null)
        {
            if (m_gridTableModel != null)
            {
                SwingUtilities.invokeLater(new HandleResetTableModel(this));
            }
        }
    }
    /**
     * Process this RecordMessage.
     */
    class HandleResetTableModel extends Object
        implements Runnable
    {

        /**
         * Constructor.
         */
        public HandleResetTableModel()
        {
            super();
        }
        /**
         * Constructor.
         */
        public HandleResetTableModel(ScreenFieldView screenFieldView)
        {
            this();
            this.init(screenFieldView);
        }
        /**
         * Constructor.
         */
        public void init(ScreenFieldView screenFieldView)
        {
//            m_screenFieldView = screenFieldView;
        }
        /**
         * Update the fields on the screen.
         */
        public void run()
        {
            Record record = null;
            if (getScreenField() != null)
                record = ((BaseGridTableScreen)getScreenField()).getMainRecord();
            if (record != null)
            {
                if (m_gridTableModel != null)
                {
                    try {
                        m_gridTableModel.updateIfNewRow(-1);    // Update any locked record.
                    } catch (DBException ex)    {
                        ex.printStackTrace();
                    }                    
                }
                record.close();
                if (m_gridTableModel != null)
                    m_gridTableModel.resetTheModel();
            }
        }
    }
    /**
     * Process the command using this view.
     * @param strCommand The command to process.
     * @return True if processed.
     */
    public boolean doCommand(String strCommand)
    {
        if (MenuConstants.DELETE.equalsIgnoreCase(strCommand))
            return this.onDelete();
        if (MenuConstants.REQUERY.equalsIgnoreCase(strCommand))
        {
            this.reSelectRecords();
            return true;    // Success
        }
        if ((MenuConstants.SELECT.equalsIgnoreCase(strCommand)) || (MenuConstants.SELECT_DONT_CLOSE.equalsIgnoreCase(strCommand)))
        {
            int iRow = this.getSelectedRow();
            if (iRow != -1)
            {
                BaseGridTableScreen gridScreen = (BaseGridTableScreen)this.getScreenField();
                Record recGrid = gridScreen.getMainRecord();
                if (recGrid != null)
                {   // Close this screen (optionally), then select the record.
                    this.getModel().makeRowCurrent(iRow, false);
                    recGrid.handleRecordChange(DBConstants.USER_DEFINED_TYPE);
                }
                if (MenuConstants.SELECT.equalsIgnoreCase(strCommand))
                    gridScreen.getRootScreen().free();
                return true;
            }
        }
        return super.doCommand(strCommand);
    }
    /**
     * Validate the current field, update the current grid record.
     */
    public void finalizeThisScreen()
    {
        try {
           m_gridTableModel.updateIfNewRow(-1);    // Make sure current is updated
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
       super.finalizeThisScreen();
    }
    /**
     * Delete the currently selected row.
     * @return true if successful.
     */
    public boolean onDelete()
    {
        boolean bFlag = false;
        Record recMain = ((BaseGridTableScreen)this.getScreenField()).getMainRecord();
        int iSelection = this.getSelectedRow();
        if (iSelection != -1)
        {
            if (((BaseGridTableScreen)this.getScreenField()).getEditing() == false)
            {   // Can't delete on a disabled grid.
                String strError = "Can't Delete from disabled grid";
                if (((BaseGridTableScreen)this.getScreenField()).getTask() != null)
                    if (((BaseGridTableScreen)this.getScreenField()).getTask().getApplication() != null)
                        strError = ((BaseApplication)((BaseGridTableScreen)this.getScreenField()).getTask().getApplication()).getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strError);
                ((BaseGridTableScreen)this.getScreenField()).displayError(strError);
                return false;
            }
            Record recTarget = null;
            try   {
                recTarget = (Record)((GridTable)recMain.getTable()).get(iSelection);
            } catch (DBException ex)    {
                // Ignore
            }
            if (recTarget != null)
            {
                if (recTarget.getEditMode() == Constants.EDIT_ADD)
                {   // If they're adding, can't delete nothing!
                    bFlag = true;
                }
                try
                {
                    if (recTarget.getEditMode() != Constants.EDIT_IN_PROGRESS)
                        recMain.edit();   // I edit the main record's table so I am sure to go through the grid table.
                    recMain.remove();
                    recMain.addNew();
                }
                catch(DBException ex)
                {
                    ((BaseGridTableScreen)this.getScreenField()).displayError(ex);
                }
            }
            this.tableChanged(iSelection);  // Update GUI
            bFlag = true;
        }
        return bFlag;
    }
    /**
     * The table changed on this row, update the JTable GUI.
     * @param iSelection The row to update.
     */
    public void tableChanged(int iSelection)
    {
        // Override this
    }
    /**
     * Get the currently selected row.
     * @return The row number of the current row.
     */
    public int getSelectedRow()
    {
        return -1;  // Override this
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
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(BaseMessage message)
    {
        Record record = this.getScreenField().getMainRecord(); // Record changed
        if (record.getTable() instanceof GridTable)     // Always except HTML
            if (message.getMessageHeader() != null)
                if (message.getMessageHeader() instanceof RecordMessageHeader)
        {
            SwingUtilities.invokeLater(new HandleBaseGridTableScreenUpdate(this, message));
        }
        return DBConstants.NORMAL_RETURN;
    }
}
