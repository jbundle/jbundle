/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.model;

/**
 * @(#)Resources.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;




/**
 * Resources - Wrapper around ResourceBundle.
 */
public class Resources extends ResourceBundle
{
    /**
     * The resource bundle associated with this "Resources".
     */
    protected ResourceBundle m_resourceBundle = null;
    /**
     * The name of this resource.
     */
    protected String m_strResourceName = null;
    /**
     * Return the key if the resource doesn't exist? else return null.
     */
    protected boolean m_bReturnKeyOnMissing = true;
    /**
     * Constuctor.
     */   
    public Resources()
    {
        super();
    }
    /**
     * Constuctor.
     * @param resourceBundle The resource bundle to do lookups in.
     * @param strResourceName The name of this resource.
     * @param bReturnKeyOnMissing Return the key if it is not in the resource bundle.
     */   
    public Resources(ResourceBundle resourceBundle, String strResourceName, boolean bReturnKeyOnMissing)
    {
        this();
        this.init(resourceBundle, strResourceName, bReturnKeyOnMissing);
    }
    /**
     * Constuctor.
     * @param resourceBundle The resource bundle to do lookups in.
     * @param strResourceName The name of this resource.
     * @param bReturnKeyOnMissing Return the key if it is not in the resource bundle.
     */   
    public void init(ResourceBundle resourceBundle, String strResourceName, boolean bReturnKeyOnMissing)
    {
        m_resourceBundle = resourceBundle;
        m_strResourceName = strResourceName;
        m_bReturnKeyOnMissing = bReturnKeyOnMissing;
    }
    /**
     * Get the resource for this key (Move this to Task).
     * @return This resource name.
     */
    public String getResourceName()
    {
        return m_strResourceName;
    }
    /**
     * Get the resource for this key.
     * NOTE: This is an overriden method from ResourceBundle.
     * @return The local string for this key.
     */
    public Object handleGetObject(String strKey)
    {
        String strValue = null;
        try   {
            strValue = m_resourceBundle.getString(strKey);
        } catch (MissingResourceException ex) {
            strValue = DBConstants.BLANK;
        } catch (NullPointerException ex) {
            strValue = DBConstants.BLANK;
        }
        if ((strValue == null) || (strValue == DBConstants.BLANK))
            if (m_bReturnKeyOnMissing)
                if (strKey != null)
                    strValue = strKey;
        return strValue;
    }
    /**
     * Get the keys for this resource.
     * NOTE: This is an overriden method from ResourceBundle.
     */
    public Enumeration<String> getKeys()
    {
        return m_resourceBundle.getKeys();
    }
    /**
     * From the value, find the key.
     * Does a reverse-lookup for the key from this value.
     * This is useful for foreign-language buttons that do a command using their english name.
     * @param strValue The value to search for.
     * @returns The key associated with this value (or this value if not found).
     */   
    public String getKey(String strValue)
    {
        for (String key : m_resourceBundle.keySet())
        {
            String strKeyValue = m_resourceBundle.getString(key);
            if (strValue.startsWith(strKeyValue))
                return key;            
        }
        return strValue;    // Can't find it, try the value
    }
    /**
     * Set flag.
     * @param returnKeyOnMissing
     */
    public void setReturnKeyOnMissing(boolean returnKeyOnMissing)
    {
    	m_bReturnKeyOnMissing = returnKeyOnMissing;
    }
}
