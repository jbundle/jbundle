/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)PropertiesField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.event.FileRemoveBOnCloseHandler;
import org.jbundle.base.field.convert.PropertiesConverter;
import org.jbundle.base.field.event.CopyConvertersHandler;
import org.jbundle.base.field.event.FieldDataScratchHandler;
import org.jbundle.base.field.event.FieldListener;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.model.Utility;
import org.jbundle.model.db.Convert;
import org.jbundle.model.main.properties.db.PropertiesInputModel;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.GridScreenParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.main.properties.db.PropertiesInput;


/**
 * A field which holds a Java Properties object.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class PropertiesField extends MemoField
{
	private static final long serialVersionUID = 1L;

	/**
     * The cached properties, so I don't have to keep parsing and writing the properties.
     */
    protected Map<String,Object> m_propertiesCache = null;
    
    protected Map<String,Object> m_mapKeyDescriptions = null;

    /**
     * Constructor.
     */
    public PropertiesField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public PropertiesField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize this object.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iMaxLength = BIG_DEFAULT_LENGTH;
        if (this.getListener(FieldDataScratchHandler.class.getName()) == null)
            this.addListener(new FieldDataScratchHandler(null));    // This is needed for any merge operation
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new PropertiesField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Get this property in the user's property area.
     * @param strProperty The property key to retrieve.
     * @return The property.
     */
    public String getProperty(String strProperty)
    {
        if ((strProperty == null) || (strProperty.length() == 0))
            return null;
        if (m_propertiesCache == null)
            m_propertiesCache = this.loadProperties();
        return (String)m_propertiesCache.get(strProperty);  // Add code here 
    }
    /**
     * Set this property in the user's property area.
     * @param strProperty The property key.
     * @param strValue The property value.
     */
    public void setProperty(String strProperty, String strValue)
    {
        this.setProperty(strProperty, strValue, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Set this property in the user's property area.
     * @param strProperty The property key.
     * @param strValue The property value.
     * @param iDisplayOption If true, display the new field.
     * @param iMoveMove The move mode.
     * @return An error code (NORMAL_RETURN for success).
     */
    public int setProperty(String strProperty, String strValue, boolean bDisplayOption, int iMoveMode)
    {
        if (m_propertiesCache == null)
            m_propertiesCache = this.loadProperties();
        boolean bChanged = false;
        // If strValue == null, delete; if = '', it's okay (key='') [a blank property]!
        if (strValue == null)
        {
        	if (m_propertiesCache.get(strProperty) != null)
        	{
        		m_propertiesCache.remove(strProperty);
        		bChanged = true;
        	}
        }
        else
        {
        	if (!strValue.equals(m_propertiesCache.get(strProperty)))
        	{
        		m_propertiesCache.put(strProperty, strValue); // Add this param
        		bChanged = true;
        	}
        }
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (bChanged)
        {	// Only change if there is a change of properties (not just a change in the #date line in properties)
	        String strProperties = this.propertiesToInternalString(m_propertiesCache);
	        Map<String,Object> propertiesSave = m_propertiesCache;
	        iErrorCode = this.setString(strProperties, bDisplayOption, iMoveMode);
	        m_propertiesCache = propertiesSave;   // Zeroed out in set String
        }
        return iErrorCode;
    }
    /**
     * Set this property in the user's property area.
     * @param strProperty The property key.
     * @param strValue The property value.
     */
    public int setProperties(Map<String,Object> properties)
    {
        return this.setProperties(properties, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Set this property in the user's property area.
     * @param strProperty The property key.
     * @param strValue The property value.
     * @param iDisplayOption If true, display the new field.
     * @param iMoveMove The move mode.
     * @return An error code (NORMAL_RETURN for success).
     */
    public int setProperties(Map<String,Object> properties, boolean bDisplayOption, int iMoveMode)
    {
        m_propertiesCache = properties;
        if ((properties == null) || (properties.size() == 0))
            m_propertiesCache = null;
        String strProperties = null;
        if (m_propertiesCache != null)
            strProperties = this.propertiesToInternalString(m_propertiesCache);
        Map<String,Object> propertiesSave = m_propertiesCache;
        int iErrorCode = this.setString(strProperties, bDisplayOption, iMoveMode);
        m_propertiesCache = propertiesSave;   // Zeroed out in set String
        return iErrorCode;
    }
    /**
     * Load the properties from the field and parse them.
     * @return The java properties.
     */
    public Map<String,Object> loadProperties()
    {
        return this.getProperties();
    }
    /**
     * Load the properties from the field and parse them.
     * @return The java properties.
     */
    public Map<String,Object> getProperties()
    {
        String strProperties = this.toString();
        return this.internalStringToProperties(strProperties);
    }
    /**
     * Load the properties from the string and parse them.
     * @return The java properties.
     */
    public Map<String,Object> internalStringToProperties(String strProperties)
    {
        return PropertiesField.stringToProperties(strProperties);
    }
    /**
     * Load the properties from the string and parse them.
     * @return The java properties.
     */
    public static Map<String,Object> stringToProperties(String strProperties)
    {
        Properties properties = new Properties();
        if ((strProperties != null) && (strProperties.length() > 0))
        {
            ByteArrayOutputStream baOut = new ByteArrayOutputStream();
            OutputStreamWriter osOut = new OutputStreamWriter(baOut);
            try   {
                osOut.write(strProperties);     // Char->Byte
                osOut.flush();
                baOut.flush();
                ByteArrayInputStream baIn = new ByteArrayInputStream(baOut.toByteArray());
                properties.load(baIn);
            } catch (IOException ex)    {
                ex.printStackTrace();
            }
        }
        Map<String,Object> map = Utility.propertiesToMap(properties);    // This is okay.
        return map;
    }
    /**
     * Convert these java properties to a string.
     * @param properties The java properties.
     * @return The properties string.
     */
    public String propertiesToInternalString(Map<String,Object> properties)
    {
        return PropertiesField.propertiesToString(properties);
    }
    public static final String PROPERTIES_COMMENT = "Field properties";
    /**
     * Convert these java properties to a string.
     * @param properties The java properties.
     * @return The properties string.
     */
    public static String propertiesToString(Map<String,Object> map)
    {
        String strProperties = null;
        Properties properties = Utility.mapToProperties(map);
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        try   {
            properties.store(baOut, PROPERTIES_COMMENT);
            byte[] rgBytes = baOut.toByteArray();
            ByteArrayInputStream baIn = new ByteArrayInputStream(rgBytes);
            InputStreamReader isIn = new InputStreamReader(baIn); // byte -> char
            char[] cbuf = new char[rgBytes.length];
            isIn.read(cbuf, 0, rgBytes.length);
            if (cbuf.length == rgBytes.length)
                strProperties = new String(cbuf);
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
        return strProperties;
    }
    /**
     * Move this physical binary data to this field.
     * @param data The physical data to move to this field (must be the correct raw data class).
     * @param bDisplayOption If true, display after setting the data.
     * @param iMoveMode The type of move.
     * @return an error code (0 if success).
     */
    public int doSetData(Object data, boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = super.doSetData(data, bDisplayOption, iMoveMode);
        if (this.isJustModified())
            m_propertiesCache = null; // Cache is no longer valid
        return iErrorCode;
    }
    /**
     * Set up the default screen control for this field.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        ScreenComponent screenField = null;
        if (ScreenModel.SWING_TYPE.equalsIgnoreCase(targetScreen.getViewType()))
        {
            Record recPropertiesInput = Record.makeRecordFromClassName(PropertiesInput.THICK_CLASS, this.getRecord().getRecordOwner());
            ((PropertiesInputModel)recPropertiesInput).setPropertiesField(this);
            screenField = recPropertiesInput.makeScreen(itsLocation, targetScreen, iDisplayFieldDesc | ScreenConstants.DISPLAY_MODE, this.getMapKeyDescriptions());
            boolean bAllowAppending = this.getMapKeyDescriptions() == null;
            ((GridScreenParent)screenField).setAppending(bAllowAppending);
            this.addListener(new SyncFieldToPropertiesRecord(recPropertiesInput));
            recPropertiesInput.addListener(new SyncPropertiesRecordToField(this));
            // No need to add FreeOnFree Handler, since PropertiesInput is owned by the new screen
            ScreenLoc descLocation = targetScreen.getNextLocation(ScreenConstants.FIELD_DESC, ScreenConstants.DONT_SET_ANCHOR);
            String strDisplay = converter.getFieldDesc();
            if ((strDisplay != null) && (strDisplay.length() > 0))
            {
                properties = new HashMap<String,Object>();
                properties.put(ScreenModel.DISPLAY_STRING, strDisplay);
                createScreenComponent(ScreenModel.STATIC_STRING, descLocation, targetScreen, converter, iDisplayFieldDesc, properties);
            }
        }
        else
        {
            screenField = super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
            properties = new HashMap<String,Object>();
            properties.put(ScreenModel.FIELD, this);
            properties.put(ScreenModel.COMMAND, ScreenModel.EDIT);
            properties.put(ScreenModel.IMAGE, ScreenModel.EDIT);
            ScreenComponent sScreenField = createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, iDisplayFieldDesc, properties);
            sScreenField.setRequestFocusEnabled(false);
        }
        return screenField;
    }
    /**
     * When this record changes, update this field.
     * @author don
     *
     */
    public class SyncPropertiesRecordToField extends FileListener
    {
        PropertiesField m_recPropertiesField = null;
        /**
         * Constructor.
         */
        public SyncPropertiesRecordToField()
        {
            super();
        }
        /**
         * Constructor.
         * @param record My owner (usually passed as null, and set on addListener in setOwner()).
         */
        public SyncPropertiesRecordToField(PropertiesField recPropertiesField)
        {
            this();
            this.init(recPropertiesField);
        }
        /**
         * Constructor.
         * @param record My owner (usually passed as null, and set on addListener in setOwner()).
         */
        public void init(PropertiesField recPropertiesField)
        {
            super.init(null);
            m_recPropertiesField = recPropertiesField;
        }
        /**
         * Called when a change to the record status is about to happen/has happened.
         * @param field If this file change is due to a field, this is the field.
         * @param iChangeType The type of change that occurred.
         * @param bDisplayOption If true, display any changes.
         * @return an error code.
         * ADD_TYPE - Before a write.
         * UPDATE_TYPE - Before an update.
         * DELETE_TYPE - Before a delete.
         * AFTER_UPDATE_TYPE - After a write or update.
         * LOCK_TYPE - Before a lock.
         * SELECT_TYPE - After a select.
         * DESELECT_TYPE - After a deselect.
         * MOVE_NEXT_TYPE - After a move.
         * AFTER_REQUERY_TYPE - Record opened.
         * SELECT_EOF_TYPE - EOF Hit.
         */
        public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
        {   // Return an error to stop the change
            if ((iChangeType == DBConstants.ADD_TYPE)
                    || (iChangeType == DBConstants.UPDATE_TYPE)
                    || (iChangeType == DBConstants.DELETE_TYPE))
            {
                FieldListener listener = m_recPropertiesField.getListener(SyncFieldToPropertiesRecord.class);
                boolean bOldState = true;
                if (listener != null)
                    bOldState = listener.setEnabledListener(false);
                
                String strKey = this.getOwner().getField(PropertiesInputModel.KEY).toString();
                String strValue = this.getOwner().getField(PropertiesInputModel.VALUE).toString();
                int oldKeyArea = this.getOwner().getDefaultOrder();
                KeyArea keyArea = this.getOwner().setKeyArea(PropertiesInputModel.KEY_KEY);
                String strOldKey = keyArea.getKeyField(0).getField(DBConstants.TEMP_KEY_AREA).toString();
                this.getOwner().setKeyArea(oldKeyArea);
                if (iChangeType == DBConstants.ADD_TYPE)
                    m_recPropertiesField.setProperty(strKey, strValue);
                if (iChangeType == DBConstants.UPDATE_TYPE)
                {
                    if (!strKey.equals(strOldKey))
                        m_recPropertiesField.setProperty(strOldKey, null);
                    m_recPropertiesField.setProperty(strKey, strValue);
                }
                if (iChangeType == DBConstants.DELETE_TYPE)
                    m_recPropertiesField.setProperty(strOldKey, null);
                
                if (listener != null)
                    listener.setEnabledListener(bOldState);
            }
            return super.doRecordChange(field, iChangeType, bDisplayOption);
        }
    }
    /**
     * When this field changes, update the PropertiesInput file.
     * @author don
     */
    public class SyncFieldToPropertiesRecord extends FieldListener
    {
        protected Record m_recPropertiesInput = null;
        
        public SyncFieldToPropertiesRecord()
        {
            super();
        }
        /**
         * Constructor.
         * @param owner The basefield owner of this listener (usually null and set on setOwner()).
         */
        public SyncFieldToPropertiesRecord(Record recPropertiesInput)
        {
            this();
            this.init(recPropertiesInput);
        }
        /**
         * Constructor.
         * @param owner The basefield owner of this listener (usually null and set on setOwner()).
         */
        public void init(Record recPropertiesInput)
        {
            super.init(null);
            m_recPropertiesInput = recPropertiesInput;
        }
        /**
         * Set the field that owns this listener.
         * @owner The field that this listener is being added to (if null, this listener is being removed).
         */
        public void setOwner(ListenerOwner owner)
        {
            super.setOwner(owner);
            if (owner != null)
            {
                if (m_recPropertiesInput != null)
                    m_recPropertiesInput.addListener(new FileRemoveBOnCloseHandler(this));      // Not same file, if target file closes, remove this listener!
            }
        }
        /**
         * The Field has Changed.
         * Don't need to call inherited.
         * @param bDisplayOption If true, display the change.
         * @param iMoveMode The type of move being done (init/read/screen).
         * @return The error code (or NORMAL_RETURN if okay).
         */
        public int fieldChanged(boolean bDisplayOption, int iMoveMode)
        {
            ((PropertiesInputModel)m_recPropertiesInput).loadFieldProperties((PropertiesField)this.getOwner());
            return super.fieldChanged(bDisplayOption, iMoveMode);
        }

    }
    /**
     * Override this to supply the allowed keys.
     * @return
     */
    public Map<String,Object> getMapKeyDescriptions()
    {
        return m_mapKeyDescriptions;
    }
    /**
     * Override this to supply the allowed keys.
     * @return
     */
    public void setMapKeyDescriptions(Map<String,Object> mapKeyDescriptions)
    {
        m_mapKeyDescriptions = mapKeyDescriptions;
    }
    /**
     * Merge my changed data back into field that I just restored from disk.
     * @param objData The value this field held before I refreshed from disk.
     * @return The setData error code.
     */
    public int doMergeData(Object objData)
    {
    	FieldDataScratchHandler listener = (FieldDataScratchHandler)this.getListener(FieldDataScratchHandler.class, false);
    	if (listener == null)
    		return super.doMergeData(objData);
    	String strOrigProperties = (String)listener.getOriginalData();
    	Map<String,Object> propRead = this.getProperties();                                // Disk image (with updates from others)
    	Map<String,Object> propOrig = this.internalStringToProperties(strOrigProperties);  // Before I made any changes
    	Map<String,Object> propCurrent = this.internalStringToProperties((String)objData);     // Current image (with my changes)
        Map<String,Object> propNew = new Hashtable<String,Object>();     // New image (with my changes)
        Utility.getLogger().info("propRead:    " + propRead);
        Utility.getLogger().info("propOrig:    " + propOrig);
        Utility.getLogger().info("propCurrent: " + propCurrent);
    	// Step 1 Move all current values to the new map, unless the value didn't change, then use the read value
        boolean bOrigChange = false;
    	for (String strKey : propCurrent.keySet())
    	{
            String strCurrentValue = (String)propCurrent.get(strKey);
    		String strOrigValue = (String)propOrig.get(strKey);
    		String strReadValue = (String)propRead.get(strKey);
    		String strNewValue = this.getNewValue(strKey, strReadValue, strOrigValue, strCurrentValue);
            if (strNewValue != null)
                propNew.put(strKey, strNewValue);
            if (strNewValue == strReadValue)
                bOrigChange = this.updateOrigKey(propOrig, strKey, strOrigValue, strNewValue, bOrigChange);
    	}
    	// Step 2 Delete any values that were deleted (changed) from the read version
        for (String strKey : propRead.keySet())
        {
            String strCurrentValue = (String)propCurrent.get(strKey);
            if (strCurrentValue == null)
            {   // Only looking at the ones that have been deleted since the read. (since I already handled the current ones)
                String strOrigValue = (String)propOrig.get(strKey);
                String strReadValue = (String)propRead.get(strKey);
                String strNewValue = this.getNewValue(strKey, strReadValue, strOrigValue, strCurrentValue);
                if (strNewValue != null)
                    propNew.put(strKey, strNewValue);
                if (strNewValue == strReadValue)
                    bOrigChange = this.updateOrigKey(propOrig, strKey, strOrigValue, strNewValue, bOrigChange);
            }
    	}
        if (bOrigChange)
            listener.setOriginalData(this.propertiesToInternalString(propOrig));
        Utility.getLogger().info("propNew:     " + propNew);
    	return this.setProperties(propNew);
    }
    /**
     * Given the read, original, and current values for this key, decide which to use.
     * @param strKey
     * @param strReadValue
     * @param strOrigValue
     * @param strCurrentValue
     * @return
     */
    public String getNewValue(String strKey, String strReadValue, String strOrigValue, String strCurrentValue)
    {
        String strNewValue = null;
        if (((strCurrentValue != null) && (strCurrentValue.equals(strOrigValue)))
            || ((strCurrentValue == null) && (strOrigValue == null)))
        {   // I have't changed it, so use the read value
            strNewValue = strReadValue;
        }
        else if (((strReadValue != null) && (strReadValue.equals(strOrigValue)))
                || ((strReadValue == null) && (strOrigValue == null)))
        {   // Someone else didn't change it, use current value
            strNewValue = strCurrentValue;
        }
        else if (((strReadValue != null) && (strReadValue.equals(strCurrentValue)))
                || ((strReadValue == null) && (strCurrentValue == null)))
        {   // The read value and my value are the same... good, use it
            strNewValue = strCurrentValue;
        }
        else
        {   // All three values are different... figure out which to use
            strNewValue = this.mergeKey(strKey, strReadValue, strCurrentValue); // I HAVE changed it, so I need to figure out which one is better
        }
        return strNewValue;
    }
    /**
     * Given the read, original, and current values for this key, update the original key value.
     * @param propOrig
     * @param strKey
     * @param strReadValue
     * @param strOrigValue
     * @param strNewValue
     * @param bOrigChange
     * @return
     */
    public boolean updateOrigKey(Map<String,Object> propOrig, String strKey, String strOrigValue, String strNewValue, boolean bOrigChange)
    {
            if (((strNewValue != null) && (!strNewValue.equals(strOrigValue)))
                || ((strNewValue == null) && (strOrigValue != null)))
        {   // If I am using the read value now (and it has changed from the orig value) change the orig value to the read value.
            bOrigChange = true;
            if (strNewValue != null)    
                propOrig.put(strKey, strNewValue);  // Original must reflect the read value in case it changes again.
            else
                propOrig.remove(strKey);  // Original must reflect the read value in case it changes again.
        }
        return bOrigChange;
    }
    /**
     * Merge my changed data back into field that I just restored from disk.
     * @param objData The value this field held before I refreshed from disk.
     * @return The setData error code.
     */
    public String mergeKey(String strKey, String strReadValue, String strCurrentValue)
    {
        return strReadValue;    // By default, use my value (Override this for a different behavior)
    }
    /**
     * Add the behaviors to sync this property to this virtual field.
     */
    public void addPropertiesFieldBehavior(BaseField fldDisplay, String strProperty)
    {
        FieldListener listener = new CopyConvertersHandler(new PropertiesConverter(this, strProperty));
        listener.setRespondsToMode(DBConstants.INIT_MOVE, false);
        listener.setRespondsToMode(DBConstants.READ_MOVE, false);
        fldDisplay.addListener(listener);
        listener = new CopyConvertersHandler(fldDisplay, new PropertiesConverter(this, strProperty));
        this.addListener(listener);
    }
} 
