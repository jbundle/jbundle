/**
 * RemoteComboBox.java
 *
 * Created on October 29, 2000, 4:46 AM
 */
 
package org.jbundle.thin.base.screen.util;

import javax.swing.JComboBox;

import org.jbundle.model.Freeable;
import org.jbundle.model.db.FieldComponent;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.BaseApplet;


/** 
 * RemoteComboBox This is a combobox that gets its entries from a remote record.
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JRemoteComboBox extends JComboBox
    implements FieldComponent, Freeable
{
	private static final long serialVersionUID = 1L;

	/**
     * The record to use for this popup.
     */
    protected FieldList m_record = null;
    /**
     *
     */
    protected String m_strIndexValue = null;
    
    /**
     * Creates new RemoteComboBox
     */
    public JRemoteComboBox()
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
     */
    public JRemoteComboBox(BaseApplet applet, RemoteSession remoteSession, FieldList record, String strDesc, String strFieldName, String strComponentName, boolean bCacheTable, String strIndexValue, String strKeyArea)
    {
        this();
        this.init(applet, remoteSession, record, strDesc, strFieldName, strComponentName, bCacheTable, strIndexValue, strKeyArea);
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
     */
    public void init(BaseApplet applet, RemoteSession remoteSession, FieldList record, String strDesc, String strFieldName, String strComponentName, boolean bCacheTable, String strIndexValue, String strKeyArea)
    {
        this.setOpaque(false);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        
        m_strIndexValue = strIndexValue;
        m_record = record;
        
        this.addItem(strDesc);
        try   {
            FieldTable table = null;
            if (record.getTable() == null)
                table = applet.linkRemoteSessionTable(remoteSession, record, bCacheTable);
            else
                table = record.getTable();
            if (strKeyArea != null)
                record.setKeyArea(strKeyArea);
            for (int iIndex = 0; ; iIndex++)
            {
                if (table.get(iIndex) == null)  // I use get, so the index matches the index of JComboBox
                    break;
                FieldInfo field = record.getField(strFieldName);
                if (field == null)
                    field = record.getField(1);
                String strDescription = field.getString();
                if ((strDescription != null) && (strDescription.length() > 0))
                    this.addItem(strDescription);
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        this.setName(strComponentName);
    }
    /**
     * Free this object's resources.
     */
    public void free()
    {
        if (m_record != null)
            if ((m_record.getOwner() == null) || (m_record.getOwner() == this))
                m_record.free();    // This will release the remote session (if there is one).
        m_record = null;
    }
    /**
     * Set the record for this combo box.
     * @param fieldInfo The record to set.
     */
    public void setRecord(FieldList fieldInfo)
    {
        m_record = fieldInfo;
    }
    /**
     * Get the record for this combo box.
     * @return fieldInfo The record to set.
     */
    public FieldList getRecord()
    {
        return m_record;
    }
    /**
     * Get this component's value as an object that FieldInfo can use.
     * @return This component's raw data.
     */
    public Object getControlValue()
    {
        int iIndex = this.getSelectedIndex();
        try   {
            FieldTable table = m_record.getTable();
            if (m_strIndexValue != null)
                if (iIndex != 0)
                    if (table.get(iIndex - 1) != null)  // I use get, so the index matches the index of JComboBox
            {
                FieldInfo field = m_record.getField(m_strIndexValue);
                if (field == null)
                    field = m_record.getField(0);
                return field.getData();
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        if (iIndex == 0)
            return null;    // None selected.
        return this.getSelectedItem();
    }
    /**
     * Here is the field's value (data), set the component to match.
     * @param objValue Set this component to this raw-data value.
     */
    public void setControlValue(Object objValue)
    {
        this.setSelectedIndex(this.valueToIndex(objValue));
    }
    /**
     * Convert this value to a selection index.
     * @param value The raw-data to convert.
     * @return The index in this popup of this value (will return 0 [the first value] if it doesn't exist).
     */
    public int valueToIndex(Object value)
    {
        if (value != null)
            if (!(value instanceof String))
                value = value.toString();
        if ((m_record != null) && (m_strIndexValue != null))
        {
            try   {
                FieldTable table = m_record.getTable();
                for (int iIndex = 0; ; iIndex++)
                {
                    if (table.get(iIndex) == null)  // I use get, so the index matches the index of JComboBox
                        break;
                    FieldInfo field = m_record.getField(m_strIndexValue);
                    if (field == null)
                        field = m_record.getField(0);
                    String strValue = field.getString();
                    if ((strValue != null) && (strValue.length() > 0))
                        if (strValue.equals(value))
                            return iIndex + 1;
                }
            } catch (Exception ex)  {
                ex.printStackTrace();
            }
        }
        else
        {
            this.setSelectedItem(value);
            if (this.getSelectedIndex() != -1)
                return this.getSelectedIndex();
        }
        return 0;
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Converter getConverter()
    {
        return null;
    }
}
