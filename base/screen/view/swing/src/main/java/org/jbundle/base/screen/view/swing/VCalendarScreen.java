package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.CalendarScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.swing.calendar.CalendarTableModel;
import org.jbundle.base.screen.view.swing.grid.GridTableModel;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.cal.opt.TaskCalendarStatusHandler;
import org.jbundle.util.calendarpanel.CalendarPanel;
import org.jbundle.util.calendarpanel.event.MyListSelectionEvent;
import org.jbundle.util.calendarpanel.event.MyListSelectionListener;
import org.jbundle.util.calendarpanel.model.CalendarModel;
import org.jbundle.util.calendarpanel.util.CalendarCache;


/**
 * A Swing calendar screen.
 */
public class VCalendarScreen extends VBaseGridTableScreen
    implements MyListSelectionListener
{

    /**
     * Constructor.
     */
    public VCalendarScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VCalendarScreen(ScreenField model, boolean bEditableControl)
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
        CalendarModel model = (CalendarModel)this.getModel();
        model.removeMySelectionListener(this);     // Listen for (selection) changes.
        CalendarPanel control = (CalendarPanel)this.getControl();
        if (control != null)
            control.free();
        super.free();
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        Record gridRecord = this.getScreenField().getMainRecord();
        BaseTable gridTable = gridRecord.getTable();
        if (!(gridTable instanceof GridTable))
            gridTable = new GridTable(null, gridRecord);

        ImageIcon backgroundImage = null;
        BaseApplet applet = null;
        if (this.getScreenField() != null)
        	if (this.getScreenField().getParentScreen() != null)
        		if (this.getScreenField().getParentScreen().getAppletScreen().getScreenFieldView() != null)
        			applet = (BaseApplet)this.getScreenField().getParentScreen().getAppletScreen().getScreenFieldView().getControl();
        if (applet != null)
        	backgroundImage = applet.getBackgroundImage();
        CalendarPanel control = new CalendarPanel((CalendarModel)this.getModel(), true, backgroundImage);
        control.addPropertyChangeListener(control);
        control.setStatusListener(new TaskCalendarStatusHandler(applet));

        JScrollPane scrollpane = new JScrollPane(control);
        scrollpane.setPreferredSize(new Dimension(10,10));
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollpane.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        control.setOpaque(false);
        panel.add(scrollpane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        return control;
    }
    /**
     * Change the input focus to me!
     * @sField The component to change the focus to.
     * @return True if successful.
     */
    public boolean requestFocus(ScreenField sField)
    {
        return true;    //?
    }
    /**
     * Get the grid model.
     * @return The grid table model.
     */
    public GridTableModel getModel()
    {
        GridTableModel model = super.getModel();
        if (model == null)
            model = m_gridTableModel = (CalendarTableModel)this.setupCalendarModel();
        return model;
    }
    /**
     * Get the calendar model from the overriding class.
     * @return The (new) calendar model.
     */
    public CalendarModel setupCalendarModel()
    {
        CalendarScreen calendarScreen = (CalendarScreen)this.getScreenField();
        CalendarModel model = null;
            model = calendarScreen.setupCalendarModel();
        if (model == null)
            model = new CalendarTableModel(calendarScreen);
        model.addMySelectionListener(this);     // Listen for (selection) changes.
        return model;
    }
    /**
     * Get the currently selected row.
     * @return The row number of the current row.
     */
    public int getSelectedRow()
    {
        CalendarCache cache = ((CalendarPanel)this.getControl()).getSelectedItem();
        int iSelection = -1;
        if (cache != null)
            iSelection = cache.getIndex();
        return iSelection;
    }
    /**
     * Get the calendar model from the overriding class.
     * @return The (new) calendar model.
     */
    public void selectionChanged(MyListSelectionEvent evt)
    {
//?        int iRecordRow = evt.getRow();
        int iType = evt.getType();
        if (iType == MyListSelectionEvent.CONTENT_CLICK)    // Select + click
        {
            String strCommand = MenuConstants.FORMLINK;
            ScreenField sourceSField = this.getScreenField();
            int bUseSameWindow = ScreenConstants.USE_SAME_WINDOW | DBConstants.PUSH_TO_BROSWER;  // Should check for shift
            this.getScreenField().handleCommand(strCommand, sourceSField, bUseSameWindow);
        }
    }
    /**
     * The table changed on this row, update the JTable GUI.
     * @param iSelection The row to update.
     */
    public void tableChanged(int iSelection)
    {
        if (iSelection == -1)
            return;
//?        CalendarPanel control = (CalendarPanel)this.getControl();
        this.fireTableRowsUpdated(iSelection, iSelection);  // Notify the model of this change
    }
}
