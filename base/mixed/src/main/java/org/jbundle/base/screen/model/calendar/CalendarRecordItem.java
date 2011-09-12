/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.calendar;

import java.awt.Color;
import java.util.Date;

import javax.swing.ImageIcon;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.thin.base.screen.util.SerializableImage;


/**
 * A class to return a CalendarItem from a record.
 */
public class CalendarRecordItem extends Object
    implements CalendarItem
{
    protected BaseScreen m_gridScreen = null;

    protected int m_iDescriptionField = 3;
    protected int m_iStartDateTimeField = 1;
    protected int m_iEndDateTimeField = 2;
    protected int m_iStatusField = 4;
    protected int m_iIconField = 0;

    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public CalendarRecordItem()
    {
        super();
    }
    /**
     * Constructor.
     * @param gridScreen The screen.
     * @param iIconField The location of the icon field.
     * @param iStartDateTimeField The location of the start time.
     * @param iEndDateTimeField The location of the end time.
     * @param iDescriptionField The location of the description.
     * @param iStatusField The location of the status.
     */
    public CalendarRecordItem(BaseScreen gridScreen, int iIconField, int iStartDateTimeField, int iEndDateTimeField, int iDescriptionField, int iStatusField)
    {
        this();
        this.init(gridScreen, iIconField, iStartDateTimeField, iEndDateTimeField, iDescriptionField, iStatusField);
    }
    /**
     * Constructor using default field locations.
     * @param gridScreen The screen.
     */
    public CalendarRecordItem(BaseScreen gridScreen)
    {
        this();
        this.init(gridScreen, 0, 1, 2, 3, 4);
    }
    /**
     * Constructor.
     * @param gridScreen The screen.
     * @param iIconField The location of the icon field.
     * @param iStartDateTimeField The location of the start time.
     * @param iEndDateTimeField The location of the end time.
     * @param iDescriptionField The location of the description.
     * @param iStatusField The location of the status.
     */
    public void init(BaseScreen gridScreen, int iIconField, int iStartDateTimeField, int iEndDateTimeField, int iDescriptionField, int iStatusField)
    {
        m_gridScreen = gridScreen;
        m_iDescriptionField = iDescriptionField;
        m_iStartDateTimeField = iStartDateTimeField;
        m_iEndDateTimeField = iEndDateTimeField; 
        m_iStatusField = iStatusField;
        m_iIconField = iIconField;
    }
    /**
     * I'm done with this item, free the resources.
     */
    public void free()
    {
    }
    /**
     * Delete this item.
     */
    public boolean remove()
    {
        boolean bSuccess = false;
        try   {
            Record recGrid = this.getMainRecord();
            if (recGrid.getEditMode() == Constants.EDIT_CURRENT)
                recGrid.edit();
            if (recGrid.getEditMode() == Constants.EDIT_IN_PROGRESS)
                recGrid.remove();
            bSuccess = true;
        } catch (DBException ex)  {
            ex.printStackTrace();
            bSuccess = false;
        }
        return bSuccess;
    }
    /**
     * Get the description.
     * @return The description
     */
    public String getDescription()
    {
        return (String)this.getFieldData(m_iDescriptionField);
    }
    /**
     * Get the start time of this service.
     * @return The start date.
     */
    public Date getStartDate()
    {
        return (Date)this.getFieldData(m_iStartDateTimeField);
    }
    /**
     * Get the ending time of this service.
     * @return The end date.
     */
    public Date getEndDate()
    {
        return (Date)this.getFieldData(m_iEndDateTimeField);
    }
    /**
     * Get the meal description on this date.
     * @return The meal description.
     */
    public String getMealDesc(Date date)
    {
        return null;
    }
    /**
     * Get the icon (opt). Not implemented.
     * @return The icon.
     */
    public ImageIcon getIcon(int iIconType)
    {
        if (m_iIconField == -1)
            return null;
        Object data = this.getFieldData(m_iIconField);
        if (data instanceof SerializableImage)
            return new ImageIcon(((SerializableImage)data).getImage());     // Make sure you cache this
        else if (data instanceof ImageIcon)
            return (ImageIcon)this.getFieldData(m_iIconField);
        else
            return null;
    }
    /**
     * Highlight color (optional).
     * @return The highlight color (green).
     */
    public Color getHighlightColor()
    {
        return DEFAULT_HIGHTLIGHT_COLOR;
    }
    public static final Color DEFAULT_HIGHTLIGHT_COLOR = new Color(0xDDDDFF);
    /**
     * Highlight color (optional).
     * @return The select color (red).
     */
    public Color getSelectColor()
    {
        return DEFAULT_SELECTION_COLOR;
    }
    public static final Color DEFAULT_SELECTION_COLOR = new Color(0xDDDDDD);
    /**
     * Change the start time of this service.
     * @param time The new start date.
     */
    public Date setStartDate(Date time)
    {
        this.updateField(m_iStartDateTimeField, time);
        return this.getStartDate();
    }
    /**
     * Change the ending time of this service.
     * @param time The new end date.
     */
    public Date setEndDate(Date time)
    {
        this.updateField(m_iEndDateTimeField, time);
        return this.getEndDate();
    }
    /**
     * Set the icon (opt).
     * Not implemented.
     */
    public void setIcon(ImageIcon icon, int iIconType)
    {
    }
    /**
     * Get the display window for this object.
     * Not implemented.
     */
    public Object getVisualJavaBean(int iPanelType)
    {
        return null;
    }
    /**
     * Get the status of this object.
     * @return The status.
     */
    public int getStatus()
    {
        Integer intStatus = (Integer)this.getFieldData(m_iStatusField);
        if (intStatus != null)
            return intStatus.intValue();
        return 0;
    }
    /**
     * Set the status of this item.
     * @param iStatus The new status for this item.
     */
    public int setStatus(int iStatus)
    {
        this.updateField(m_iStatusField, new Integer(iStatus));
        return this.getStatus();
    }
    /**
     * Get the status of this object.
     * @param iFieldSeq The field sequence to retrieve.
     * @return The data.
     */
    public Object getFieldData(int iSFieldSeq)
    {
        ScreenField sField = null;
        if (iSFieldSeq != -1)
            sField = m_gridScreen.getSField(this.getRelativeSField(iSFieldSeq));
        if (sField != null)
            return sField.getConverter().getData();
        return null;
    }
    /**
     * Change the data in this field.
     * @param iFieldSeq The field sequence to set.
     * @param objData The new data.
     */
    public void updateField(int iSFieldSeq, Object objData)
    {
        try   {
            ScreenField sField = null;
            if (iSFieldSeq != -1)
                sField = m_gridScreen.getSField(this.getRelativeSField(iSFieldSeq));
            if (sField != null)
            {
                this.editTargetRecord();
                sField.getConverter().setData(objData);
                this.updateTargetRecord();   // Read this record
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * Change the data in this field.
     * @param iFieldSeq The field sequence to set.
     * @param objData The new data.
     */
    public void editTargetRecord()
    {
        try   {
            BaseTable table = this.getMainRecord().getTable();
            table.edit();
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * Change the data in this field.
     * @param iFieldSeq The field sequence to set.
     * @param objData The new data.
     */
    public void updateTargetRecord()
    {
        try {
            BaseTable table = this.getMainRecord().getTable();
            table.set(table.getRecord());
            table.seek(null);   // Read this record
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * This is lame.
     * @param iSFieldSeq The screen field sequence.
     * @return The relative sequence.
     */
    public int getRelativeSField(int iSFieldSeq)
    {
        Converter lastField = null;
        for (int i = 0; i < m_gridScreen.getSFieldCount(); i++)
        {
            Converter field = m_gridScreen.getSField(i).getConverter();
            if (field != null)
                field = field.getField();
            if (m_gridScreen.getSField(i) instanceof ToolScreen)
                field = null;
            
            if ((field != null) && (field != lastField))
                iSFieldSeq--;       // Skip controls that belong to the previous field
            lastField = field;
            
            if (iSFieldSeq < 0)
                return i;
        }
        return m_gridScreen.getSFieldCount() - 1;   // Never
    }
    /**
     * Utility method to get the grid's record.
     */
    public Record getMainRecord()
    {
        return m_gridScreen.getMainRecord();
    }
}
