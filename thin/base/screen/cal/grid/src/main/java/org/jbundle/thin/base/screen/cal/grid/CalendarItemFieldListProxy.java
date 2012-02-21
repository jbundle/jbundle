/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.grid;

import java.util.Date;

import javax.swing.ImageIcon;

import org.jbundle.model.util.Colors;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.util.calendarpanel.model.CalendarItem;


public class CalendarItemFieldListProxy extends Object
    implements CalendarItem
{
    protected FieldTable m_table = null;

    protected String m_strDescriptionField = "Description";
    protected String m_strStartDateTimeField = "StartDateTime";
    protected String m_strEndDateTimeField = "EndDateTime";
    protected String m_strStatusField = "Status";

    /**
     * Constructor.
     */
    public CalendarItemFieldListProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CalendarItemFieldListProxy(FieldList record)
    {
        this();
        this.init(record, null, null, null, null);
    }
    /**
     * Constructor.
     */
    public CalendarItemFieldListProxy(FieldList record ,String strStartDateTimeField,String strEndDateTimeField, String strDescriptionField, String strStatusField)
    {
        this();
        this.init(record, strStartDateTimeField, strEndDateTimeField, strDescriptionField, strStatusField);
    }
    /**
     * Constructor.
     */
    public void init(FieldList record, String strStartDateTimeField, String strEndDateTimeField, String strDescriptionField, String strStatusField)
    {
        m_table = record.getTable();
        m_strDescriptionField = strDescriptionField;
        m_strStartDateTimeField = strStartDateTimeField;
        m_strEndDateTimeField = strEndDateTimeField; 
        m_strStatusField = strStatusField;
    }
    /**
     * I'm done with this item, free the resources.
     */
    public void free()
    {
        m_table = null;
    }
    /**
     * Delete this item.
     */
    public boolean remove()
    {
        boolean bSuccess = false;
        try   {
//?         if ((m_table.getFieldInfo().getEditMode() == Constants.EDIT_IN_PROGRESS)
//?             || (m_table.getFieldInfo().getEditMode() == Constants.EDIT_CURRENT))
                    m_table.remove();
        } catch (Exception ex)  {
            ex.printStackTrace();
            bSuccess = false;
        }
        return bSuccess;
    }
    /**
     * Get the description.
     */
    public String getDescription()
    {
        return (String)this.getFieldData(m_strDescriptionField);
    }
    /**
     * Get the start time of this service.
     */
    public Date getStartDate()
    {
        return (Date)this.getFieldData(m_strStartDateTimeField);
    }
    /**
     * Get the ending time of this service.
     */
    public Date getEndDate()
    {
        return (Date)this.getFieldData(m_strEndDateTimeField);
    }
    /**
     * Get the meal description on this date.
     */
    public String getMealDesc(Date date)
    {
        return null;
    }
    /**
     * Get the icon (opt).
     */
    public ImageIcon getIcon(int iIconType)
    {
        return null;
    }
    /**
     * Highlight color (optional).
     */
    public int getHighlightColor()
    {
        return Colors.GREEN;
    }
    /**
     * Highlight color (optional).
     */
    public int getSelectColor()
    {
        return Colors.RED;
    }
    /**
     * Change the start time of this service.
     */
    public Date setStartDate(Date time)
    {
        this.updateField(m_strStartDateTimeField, time);
        return this.getStartDate();
    }
    /**
     * Change the ending time of this service.
     */
    public Date setEndDate(Date time)
    {
        this.updateField(m_strEndDateTimeField, time);
        return this.getEndDate();
    }
    /**
     * Set the icon (opt).
     */
    public void setIcon(Object icon, int iIconType)
    {
    }
    /**
     * Get the display window for this object.
     */
    public Object getVisualJavaBean(int iPanelType)
    {
        return null;
    }
    /**
     * Get the status of this object.
     */
    public int getStatus()
    {
        Integer intStatus = (Integer)this.getFieldData(m_strStatusField);
        if (intStatus != null)
            return intStatus.intValue();
        return 0;
    }
    /**
     * Set the status of this item.
     */
    public int setStatus(int iStatus)
    {
        this.updateField(m_strStatusField, new Integer(iStatus));
        return this.getStatus();
    }
    /**
     * Get the status of this object.
     */
    public Object getFieldData(String strFieldName)
    {
        FieldInfo fieldInfo = m_table.getRecord().getField(strFieldName);
        if (fieldInfo != null)
            return fieldInfo.getData();
        return null;
    }
    /**
     * Change the start time of this service.
     */
    public void updateField(String strFieldName, Object objData)
    {
        try   {
            FieldInfo fieldInfo = m_table.getRecord().getField(strFieldName);
            if (fieldInfo != null)
            {
                m_table.edit();
                fieldInfo.setData(objData);
                m_table.set(m_table.getRecord());
                m_table.seek(null);   // Read this record
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }
}
