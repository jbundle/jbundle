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
import java.awt.MediaTracker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.field.XmlField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.screen.control.swing.SApplet;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.swing.grid.GridTableModel;
import org.jbundle.model.main.properties.db.PropertiesInputModel;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.util.SerializableImage;
import org.jbundle.util.jcalendarbutton.JCalendarPopup;
import org.jbundle.util.jcalendarbutton.JTimePopup;
import org.jbundle.util.osgi.finder.ClassServiceUtility;

/**
 * Implements a button to carry out some standard functions.
 */
public class VCannedBox extends VButtonBox
    implements PropertyChangeListener
{
    /**
     * The name of the calendar button.
     */
    public static final String CALENDAR = JCalendarPopup.CALENDAR_ICON;
    /**
     * The name of the calendar button.
     */
    public static final String TIME = JTimePopup.TIME_ICON;
    /**
     * Date property for calendar popups in grids.
     */
    protected Date m_date = null;

    /**
     * Constructor.
     */
    public VCannedBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VCannedBox(ScreenField model, boolean bEditableControl)
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
     * Set the field to this state. State is defined by the component.
     * In this class, just send the command.
     * @param objValue The value to set the field to (class of object depends on the control).
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return Error code.
     */
    public int setFieldState(Object objValue, boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        String strCommand = ((SCannedBox)this.getScreenField()).getButtonCommand();
        if (!(this.getScreenField().getParentScreen() instanceof GridScreen))
        {   // For all except a grid screen (grid screen will cause an endless echo because it would be in getColumnValue())
            boolean bProcessed = this.getScreenField().handleCommand(strCommand, this.getScreenField(), ScreenConstants.USE_SAME_WINDOW | DBConstants.PUSH_TO_BROSWER);
            if (bProcessed)
                iErrorCode = DBConstants.NORMAL_RETURN;
        }
        else
        {   // Special grid processing
            if (this.getScreenField().getConverter() != null)
            {       // This control is hooked to a control, send the data (such as calendar data or lookup bookmark).
                if (CALENDAR.equals(((SCannedBox)this.getScreenField()).getButtonCommand()))
                {
                    if (objValue != null)
                        return ((DateTimeField)this.getScreenField().getConverter().getField()).setDateTime((Date)objValue, bDisplayOption, iMoveMode);
                }
                if (TIME.equals(((SCannedBox)this.getScreenField()).getButtonCommand()))
                {
                    if (objValue != null)
                        return ((DateTimeField)this.getScreenField().getConverter().getField()).setDateTime((Date)objValue, bDisplayOption, iMoveMode);
                }
                if (!(objValue instanceof String))
                    return this.getScreenField().getConverter().setData(objValue, bDisplayOption, iMoveMode);
                else
                    return this.getScreenField().getConverter().setString((String)objValue, bDisplayOption, iMoveMode);
            }
        }
        return iErrorCode;
    }
    /**
     * Process the command using this view.
     * Typically canned commands are processed in the model. A few view
     * specific commands must be processed here.
     * @param strCommand The command to process.
     * @return True if processed.
     */
    public boolean doCommand(String strCommand)
    {
        if (ScreenModel.OPEN.equalsIgnoreCase(strCommand))
            return this.openFile();
        if (ScreenModel.EDIT.equalsIgnoreCase(strCommand))
            return this.editFile();
        if (CALENDAR.equalsIgnoreCase(strCommand))
        {
            this.selectCurrentRow(false);
            DateTimeField field = (DateTimeField)((SCannedBox)this.getScreenField()).getField();
            Date date = ((DateTimeField)field).getDateTime();
            Component button = this.getControl();
            if (button == null)
                button = m_componentEditor; // Do not call getComponentEdit... as it will create if not found.
            if (button != null)
            {
                JComponent cal = JCalendarPopup.createCalendarPopup(date, button);
                cal.addPropertyChangeListener(this);
                return true;
            }
            m_date = null;
        }
        if (TIME.equalsIgnoreCase(strCommand))
        {
            this.selectCurrentRow(false);
            DateTimeField field = (DateTimeField)((SCannedBox)this.getScreenField()).getField();
            Date date = ((DateTimeField)field).getDateTime();
            Component button = this.getControl();
            if (button == null)
                button = m_componentEditor; // Do not call getComponentEdit... as it will create if not found.
            if (button != null)
            {
                JComponent cal = JTimePopup.createTimePopup(date, button);
                cal.addPropertyChangeListener(this);
                return true;
            }
            m_date = null;
        }
        return false; // Not processed, BasePanels and above will override
    }
    /**
     * Select the current grid row.
     * @param bLockRecord
     * @return
     */
    public int selectCurrentRow(boolean bLockRecord)
    {
        if (!(this.getScreenField().getParentScreen() instanceof GridScreen))
            return -1;
        GridScreen gridScreen = (GridScreen)this.getScreenField().getParentScreen();
        VGridScreen vGridScreen = (VGridScreen)gridScreen.getScreenFieldView();
        GridTableModel gridModel = vGridScreen.getModel();
        int iRowIndex = vGridScreen.getSelectedRow();
        gridModel.makeRowCurrent(iRowIndex, bLockRecord);
        return iRowIndex;
    }
    /**
     * This method gets called when a bound property is changed.
     * This is required to listen to changes by the date popup control.
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (JCalendarPopup.DATE_PARAM.equalsIgnoreCase(evt.getPropertyName()))
            if (evt.getNewValue() instanceof java.util.Date)
        {
            DateTimeField field = (DateTimeField)((SCannedBox)this.getScreenField()).getField();
            m_date = (Date)evt.getNewValue();   // I'll need this in a sec.
            if (!(this.getScreenField().getParentScreen() instanceof GridScreen))
            {
                field.setDateTime((java.util.Date)evt.getNewValue(), true, DBConstants.SCREEN_MOVE);
            }
            else
            {
                GridScreen gridScreen = (GridScreen)this.getScreenField().getParentScreen();
                VGridScreen vGridScreen = (VGridScreen)gridScreen.getScreenFieldView();
                JTable jtable = (JTable)vGridScreen.getControl();
                int row = jtable.getEditingRow();
                int column = jtable.getEditingColumn();
                jtable.setValueAt(evt.getNewValue(), row, column);
            }
        }
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value (Boolean).
     */
    public Object getComponentState(Component control)
    {
        if (CALENDAR.equals(((SCannedBox)this.getScreenField()).getButtonCommand()))
            return m_date;    // This will set the value to true for buttons linked to components
        else if (TIME.equals(((SCannedBox)this.getScreenField()).getButtonCommand()))
            return m_date;    // This will set the value to true for buttons linked to components
        else
            return super.getComponentState(control);
    }
    /**
     * A view specific function - open a file.
     * @return True if successful.
     */
    public boolean openFile()
    {
        JFileChooser chooser = new JFileChooser();
//+     ExtensionFileFilter filter = new ExtensionFileFilter();
//+     filter.addExtension("jpg");
//+     filter.addExtension("gif");
//+     filter.setDescription("JPG & GIF Images");
//+     chooser.setFileFilter(filter);
        BaseApplet parent = (BaseApplet)SApplet.getSharedInstance().getApplet();
        int returnVal = chooser.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            String strPath = chooser.getSelectedFile().getPath();
            ImageIcon imageIcon = new ImageIcon(strPath);
            SerializableImage data = new SerializableImage(imageIcon.getImage());
            int i = 0;
            while (imageIcon.getImageLoadStatus() != MediaTracker.COMPLETE)
            {
                i++;
                try   {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                if (i == 10)
                    return false; // ten sec - too long
            }
            this.getScreenField().getConverter().setData(data, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
        }
        return true;    // Success
    }
    /**
     * A view specific function - open a file.
     * @return True if successful.
     */
    public boolean editFile()
    {
        String string = this.getScreenField().getConverter().getString();
        String schema = null;
        if (this.getScreenField().getConverter().getField() instanceof PropertiesField)
        {
            RecordOwner recordOwner = null;
            ScreenField screenField = this.getScreenField();
            while (screenField != null)
            {
                screenField = screenField.getParentScreen();
                if (screenField instanceof RecordOwner)
                {
                    recordOwner = (RecordOwner)screenField;
                    break;
                }
            }
        	Record recProperties = Record.makeRecordFromClassName(PropertiesInputModel.THICK_CLASS, recordOwner);
            return (((PropertiesInputModel)recProperties).startEditor((PropertiesField)this.getScreenField().getConverter().getField(), true, null) != null);
        }
        if (this.getScreenField().getConverter().getField() instanceof XmlField)
            schema = ((XmlField)this.getScreenField().getConverter().getField()).getSchema();
        String strScreenClass = JaxeReference.JAXE_EDITOR_CLASS;
        Editor screen = (Editor)ClassServiceUtility.getClassService().makeObjectFromClassName(strScreenClass);
        if (screen != null)
        	return screen.startEditor(this, string, schema);
        return true;    // Success
    }
    public interface Editor
    {
        public boolean startEditor(VCannedBox control, String text, String schema);
    }
    /**
     * A view specific function - open a file.
     * @return True if successful.
     */
    public boolean setEditFile(String string)
    {
        if (this.getScreenField() != null)  // Make sure this wasn't freed first
            this.getScreenField().getConverter().setString(string, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
        return true;    // Success
    }

}
