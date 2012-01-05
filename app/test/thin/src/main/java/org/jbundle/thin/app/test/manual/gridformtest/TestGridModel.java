/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.gridformtest;

/**
 * OrderEntry.java:   Applet
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.screen.grid.ThinTableModel;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.grid.JCellButton;
import org.jbundle.thin.base.screen.grid.JCellCalendarButton;
import org.jbundle.thin.base.screen.grid.JCellCheckBox;
import org.jbundle.thin.base.screen.grid.JCellRemoteComboBox;
import org.jbundle.thin.base.screen.grid.JCellThreeStateCheckBox;


/**
 * Main Class for applet OrderEntry.
 * Note: This extends the CalendarThinTableModel rather
 * than the ThinTableModel, so I have the
 * use of the MySelectionListener.
 */
public class TestGridModel extends ThinTableModel
{
    private static final long serialVersionUID = 1L;

    public static final int ADD_BUTTON_COLUMN = 0;
    public static final int DELETE_BUTTON_COLUMN = ADD_BUTTON_COLUMN + 1;
    public static final int DESC_COLUMN = DELETE_BUTTON_COLUMN + 1;
    public static final int PRICE = DESC_COLUMN + 1;
    public static final int CHECKBOX = PRICE + 1;
    public static final int THREESTATE = CHECKBOX + 1;
    public static final int POPUP = THREESTATE + 1;
    public static final int DATE = POPUP + 1;
    public static final int DATE_BUTTON = DATE + 1;
    public static final int COLUMN_COUNT = DATE_BUTTON + 1;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public TestGridModel()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public TestGridModel(FieldTable table)
    {
        this();
        this.init(table);
    }
    /**
     * The init() method is called by the AWT when an applet is first loaded or
     * reloaded.  Override this method to perform whatever initialization your
     * applet needs, such as initializing data structures, loading images or
     * fonts, creating frame windows, setting the layout manager, or adding UI
     * components.
     */
    public void init(FieldTable table)
    {
        super.init(table);
    }
    /**
     * Get the row count.
     */
    public int getColumnCount()
    {
        return COLUMN_COUNT;
    }
    /**
     * Get this field (or return null if this field doesn't belong on the screen).
     * This is the method to use to filter the items to display on the screen.
     */
    public Converter getFieldInfo(int iIndex)
    {
        FieldList fieldList = m_table.getRecord();
        switch (iIndex)
        {
            case ADD_BUTTON_COLUMN:
            case DELETE_BUTTON_COLUMN:
                return null;
            case DESC_COLUMN:
                return fieldList.getField("TestName");
            case PRICE:
                return fieldList.getField("TestCurrency");
            case CHECKBOX:
            case THREESTATE:
                return fieldList.getField("TestYesNo");
            case POPUP:
                return fieldList.getField("TestLong");
            case DATE:
            case DATE_BUTTON:
                return fieldList.getField("TestDate");
        }
        return super.getFieldInfo(iIndex);
    }
    /**
     * Get the column class.
     * Returns String by default, override to supply a different class.
     */
    public Class<?> getColumnClass(int iColumnIndex)
    {
        switch (iColumnIndex)
        {
            case ADD_BUTTON_COLUMN:
            case DELETE_BUTTON_COLUMN:
            case DATE_BUTTON:
                return ImageIcon.class;
        }
        return super.getColumnClass(iColumnIndex);
    }
    /**
     * Get the value (this method returns the RAW data rather than Thin's String.
     */
    public Object getColumnValue(int iColumnIndex, int iEditMode)
    {
        switch (iColumnIndex) // RequestInputID
        {
            case ADD_BUTTON_COLUMN:
                if (iEditMode == Constants.EDIT_NONE)
                    return null;
                return BaseApplet.getSharedInstance().loadImageIcon(Constants.FILE_ROOT + Constants.FORM, Constants.BLANK);
            case DELETE_BUTTON_COLUMN:
                if (iEditMode == Constants.EDIT_NONE)
                    return null;
                return BaseApplet.getSharedInstance().loadImageIcon(Constants.FILE_ROOT + Constants.DELETE, Constants.BLANK);
        }
        return super.getColumnValue(iColumnIndex, iEditMode);
    }
    /**
     * Get the cell editor for this column.
     * @param The column to get the cell editor for.
     * @return The cell editor or null to use the default.
     */
    public TableCellEditor createColumnCellEditor(int iColumnIndex)
    {
        switch (iColumnIndex)
        {
        case ADD_BUTTON_COLUMN:
            ImageIcon icon = (ImageIcon)this.getValueAt(0, iColumnIndex);
            JCellButton button = new JCellButton(icon);
            button.setOpaque(false);
            button.setName(Constants.FORM);
            return button;
        case DELETE_BUTTON_COLUMN:
            ImageIcon icon2 = (ImageIcon)this.getValueAt(0, iColumnIndex);
            JCellButton button3 = new JCellButton(icon2);
            button3.setOpaque(false);
            button3.setName(Constants.DELETE);
            return button3;
        case CHECKBOX:
            return new JCellCheckBox(null);
        case THREESTATE:
            return new JCellThreeStateCheckBox(null);
        case POPUP:
            BaseApplet applet = BaseApplet.getSharedInstance();
            FieldList record = new org.jbundle.thin.app.test.vet.db.Vet(this);

            RemoteTable remoteTable = null;
            try   {
                synchronized (applet.getRemoteTask())
                {   // In case this is called from another task
                    RemoteTask server = (RemoteTask)applet.getRemoteTask();
                    Map<String, Object> dbProperties = applet.getApplication().getProperties();
                    remoteTable = server.makeRemoteTable(record.getRemoteClassName(), null, null, dbProperties);
                }
            } catch (RemoteException ex)    {
                ex.printStackTrace();
            } catch (Exception ex)  {
                ex.printStackTrace();
            }
            RemoteSession remoteSession = remoteTable;

            String strDesc = "Vet";
            String strFieldName = "Name";
            String strComponentName = "TestLong";
            boolean bCacheTable = true;
            String strIndexValue = "ID";
            return new JCellRemoteComboBox(applet, remoteSession, record, strDesc, strFieldName, strComponentName, bCacheTable, strIndexValue, null);
        case DATE_BUTTON:
            JCellCalendarButton button2 = new JCellCalendarButton(this.getFieldInfo(iColumnIndex));
            return button2;
        }
        return super.createColumnCellEditor(iColumnIndex);
    }
    /**
     * Get the cell renderer for this column.
     * @param The column to get the cell renderer for.
     * @return The cell renderer or null to use the default.
     */
    public TableCellRenderer createColumnCellRenderer(int iColumnIndex)
    {
        switch (iColumnIndex)
        {
        case ADD_BUTTON_COLUMN:
        case DELETE_BUTTON_COLUMN:
            ImageIcon icon = (ImageIcon)this.getValueAt(0, iColumnIndex);
            JCellButton button = new JCellButton(icon);
            return button;
        case CHECKBOX:
            return new JCellCheckBox(null);
        case THREESTATE:
            return new JCellThreeStateCheckBox(null);
        case DATE_BUTTON:
            JCellCalendarButton button2 = new JCellCalendarButton(this.getFieldInfo(iColumnIndex));
            return button2;
        case POPUP:
            BaseApplet applet = BaseApplet.getSharedInstance();
            FieldList record = new org.jbundle.thin.app.test.vet.db.Vet(this);

            RemoteTable remoteTable = null;
            try   {
                synchronized (applet.getRemoteTask())
                {   // In case this is called from another task
                    RemoteTask server = (RemoteTask)applet.getRemoteTask();
                    Map<String, Object> dbProperties = applet.getApplication().getProperties();
                    remoteTable = server.makeRemoteTable(record.getRemoteClassName(), null, null, dbProperties);
                }
            } catch (RemoteException ex)    {
                ex.printStackTrace();
            } catch (Exception ex)  {
                ex.printStackTrace();
            }
            RemoteSession remoteSession = remoteTable;

            String strDesc = "Vet";
            String strFieldName = "Name";
            String strComponentName = "TestLong";
            boolean bCacheTable = true;
            String strIndexValue = "ID";
            return new JCellRemoteComboBox(applet, remoteSession, record, strDesc, strFieldName, strComponentName, bCacheTable, strIndexValue, null);
        }
        return super.createColumnCellRenderer(iColumnIndex);
    }
    /**
     * Don't allow appending.
     */
//  public boolean isAppending()
//  {
//      return false;
//  }
    /**
     * Returns the name of the column at columnIndex.
     */
    public String getColumnName(int iColumnIndex)
    {
        switch (iColumnIndex)
        {
            case ADD_BUTTON_COLUMN:
                return "+";
            case DELETE_BUTTON_COLUMN:
                return "x";
        }
        return super.getColumnName(iColumnIndex);
    }
}
