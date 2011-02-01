package org.jbundle.thin.base.db.converter;

/**
 * OrderEntry.java:   Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.util.LinkedConverter;

/**
 * Main Class for applet OrderEntry.
 * Note: This extends the CalendarThinTableModel rather then the ThinTableModel, so I have the
 * use of the MySelectionListener.
 */
public class SecondaryRecordConverter extends LinkedConverter
{
    /**
     * The record to use for this popup.
     */
    protected FieldList m_record = null;
    /**
     * The key field.
     */
    protected FieldInfo m_fieldKey = null;
    /**
     * The data field.
     */
    protected FieldInfo m_fieldData = null;
    /**
     * The key value to use if the value is null.
     */
    protected String m_strNullValue = null;
    
    /**
     * Creates new RecordFieldConverter.
     */
    public SecondaryRecordConverter()
    {
        super();
    }
    /**
     * Build a popup box using a remote fieldlist.
     * If the remote session doesn't exist, create it.
     * @param applet The top-level applet.
     * @param remoteSession The remote parent session for this record's new table session.
     * @param record The record to display.
     * @param strDesc The description for this combo-box.
     * @param strFieldName The name of the field to display in the pop-up.
     * @param strComponentName The name of this component.
     * @param bCacheTable If a table session is build, should I add a CacheTable?
     * @param strIndexValue The field name of the index for this table (ie., ID).
     * @param strKeyArea The (optional) key area.
     * @param strNullValue The key value to use if the value is null.
     */
    public SecondaryRecordConverter(Converter converter, RemoteSession remoteSession, FieldList record, String strFieldName, boolean bCacheTable, String strIndexValue, String strKeyArea, String strNullValue)
    {
        this();
        this.init(converter, remoteSession, record, strFieldName, bCacheTable, strIndexValue, strKeyArea, strNullValue);
    }
    /**
     * Build a popup box using a remote fieldlist.
     * If the remote session doesn't exist, create it.
     * @param applet The top-level applet.
     * @param remoteSession The remote parent session for this record's new table session.
     * @param record The record to display.
     * @param strDesc The description for this combo-box.
     * @param strFieldName The name of the field to display in the pop-up.
     * @param strComponentName The name of this component.
     * @param bCacheTable If a table session is build, should I add a CacheTable?
     * @param strIndexValue The field name of the index for this table (ie., ID).
     * @param strKeyArea The (optional) key area.
     * @param strNullValue The key value to use if the value is null.
     */
    public void init(Converter converter, RemoteSession remoteSession, FieldList record, String strFieldName, boolean bCacheTable, String strIndexValue, String strKeyArea, String strNullValue)
    {
        super.init(converter);

        m_record = record;
        
        m_strNullValue = strNullValue;
        
        try   {
            if (record.getTable() == null)
                BaseApplet.getSharedInstance().linkRemoteSessionTable(remoteSession, record, bCacheTable);
            if (strKeyArea != null)
                record.setKeyArea(strKeyArea);
            m_fieldKey = record.getField(strIndexValue);
            if (m_fieldKey == null)
                m_fieldKey = record.getField(0);
            m_fieldData = record.getField(strFieldName);
            if (m_fieldData == null)
                m_fieldData = record.getField(1);
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * Free this object's resources.
     */
    public void free()
    {
        if (m_record != null)
            m_record.free();    // This will release the remote session (if there is one).
        m_record = null;
        super.free();
    }
    /**
     * Get the data on the end of this converter chain.
     * @return The raw data.
     */
    public Object getData() 
    {
        try {
            Object objValue = super.getData();    // Get the actual key data.
            if (objValue == null)
                objValue = m_strNullValue;
            if (objValue == null)
                return null;
            if (objValue.getClass() == m_fieldKey.getDataClass())
                m_fieldKey.setData(objValue);
            else
                m_fieldKey.setString(objValue.toString());
            if (m_record.getTable().seek(null) == true)  // I use get, so the index matches the index of JComboBox
                return m_fieldData.getData();
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Get the maximum length of this field.
     * Usually overidden.
     * @return The maximum length of this field (0 if unknown).
     */
    public int getMaxLength() 
    {
        if (m_fieldData != null)
            return m_fieldData.getMaxLength();
        return super.getMaxLength();
    }
}
