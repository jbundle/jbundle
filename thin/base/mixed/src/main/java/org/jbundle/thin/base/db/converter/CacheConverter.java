package org.jbundle.thin.base.db.converter;

/**
 * OrderEntry.java:   Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.util.Date;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.screen.util.LinkedConverter;


/**
 * Main Class for applet OrderEntry.
 * Note: This extends the CalendarThinTableModel rather then the ThinTableModel, so I have the
 * use of the MySelectionListener.
 */
public class CacheConverter extends LinkedConverter
{
    /**
     *
     */
    protected HashMap<Object,Object> m_hmCache = null;
    /**
     * Creates new JCellButton.
     */
    public CacheConverter()
    {
        super();
    }
    /**
     * Creates new JCellButton.
     * @param icon The button icon.
     */
    public CacheConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Creates new JCellButton. The icon and text are reversed, because of a conflicting method in JButton.
     * @param text the button text.
     * @param icon The button icon.
     */
    public void init(Converter converter)
    {
        super.init(converter);
    }
    /**
     * Get the data on the end of this converter chain.
     * @return The raw data.
     */
    public Object getData() 
    {
        Object objKey = this.getField().getData();
        Object objValue = null;
        if (this.isCacheValue(objKey))
            objValue = this.getCacheValue(objKey);
        else
        {
            objValue = super.getData();
            this.cacheValue(objKey, objValue);
        }
        return objValue;
    }
    /**
     * Is this key cached?
     * @param objKey The raw key value.
     * @return True if this key is in the cache.
     */
    public boolean isCacheValue(Object objKey)
    {
        if (m_hmCache == null)
            return false;
        if (objKey == null)
            return false;
        Class<?> classKey = this.getField().getDataClass();
        objKey = this.convertKey(objKey, classKey);
        return m_hmCache.containsKey(objKey);
    }
    /**
     * Get the cache value.
     * @param objKey The raw key value.
     * @return The value associated with this key.
     */
    public ImageIcon getCacheValue(Object objKey)
    {
        if (m_hmCache == null)
            return null;
        if (objKey == null)
            return null;
        Class<?> classKey = this.getField().getDataClass();
        objKey = this.convertKey(objKey, classKey);
        return (ImageIcon)m_hmCache.get(objKey);
    }
    /**
     * Add this key and value to the cache.
     * @param objKey The raw key value.
     * @param objValue The data value to associate with this key.
     */
    public void cacheValue(Object objKey, Object objValue)
    {
//x        if ((objKey == null) || (objValue == null))
        if (objKey == null)
            return;
        Class<?> classKey = this.getField().getDataClass();
        objKey = this.convertKey(objKey, classKey);
        if (m_hmCache == null)
            m_hmCache = new HashMap<Object,Object>();
        m_hmCache.put(objKey, objValue);
    }
    /**
     *
     */
    public Object convertKey(Object objKey, Class<?> classKey)
    {
        if ((classKey != null) && (objKey.getClass() != classKey))
        {   // Convert the key to the correct class.
            if (objKey instanceof Number)
            {
                double dValue = ((Number)objKey).doubleValue();
                if (classKey == Short.class)
                    objKey = new Short((short)dValue);
                else if (classKey == Integer.class)
                    objKey = new Integer((int)dValue);
                else if (classKey == Float.class)
                    objKey = new Float((float)dValue);
                else if (classKey == Double.class)
                    objKey = new Double(dValue);
                else if (classKey == java.util.Date.class)
                    objKey = new Date((long)dValue);
                else if (classKey == Boolean.class)
                    objKey = new Boolean(dValue == 0 ? false : true);
                else if (classKey == String.class)
                    objKey = new Double(dValue).toString();
            }
            else
            {
                objKey = objKey.toString();
                if (Number.class.isAssignableFrom(classKey))
                    objKey = FieldInfo.stripNonNumber((String)objKey);
                if (classKey == Short.class)
                    objKey = new Short((String)objKey);
                else if (classKey == Integer.class)
                    objKey = new Integer((String)objKey);
                else if (classKey == Float.class)
                    objKey = new Float((String)objKey);
                else if (classKey == Double.class)
                    objKey = new Double((String)objKey);
//?                else if (classKey == java.util.Date.class)
//?                    objKey = new Date((String)objKey);
//?                else if (classKey == Boolean.class)
//?                    objKey = new Boolean((String)objKey == 0 ? false : true);
            }
        }
        return objKey;
    }
}
