/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.HashMap;

import org.jbundle.thin.base.remote.RemoteBaseSession;


/**
 * ServletTask.
 * 
 * This servlet is the main servlet.
 * <p>
 * The possible params are:
 * <pre>
 *  record - Create a default HTML screen for this record (Display unless "move" param)
 *  screen - Create this HTML screen
 *  limit - For Displays, limit the records displayed
 *  form - If "yes" display the imput form above the record display
 *  move - HTML Input screen - First/Prev/Next/Last/New/Refresh/Delete
 *  applet - applet, screen=applet screen
 *              applet params: archive/id/width/height/cabbase
 *  menu - Display this menu page
 * </pre>
 */
public class MapList extends HashMap<String,BaseHolder>
{
    private static final long serialVersionUID = 1L;

    /**
     * Unique ID to map this task to.
     */
    protected int m_iNextUniqueID = 0;

    /**
     * Constructor.
     */
    public MapList()
    {
        super();
    }
    /**
     *
     */
    public String add(BaseHolder obj)
    {
        if (m_iNextUniqueID >= Integer.MAX_VALUE)
            m_iNextUniqueID = 0;
        m_iNextUniqueID++;
        String strUniqueID = Integer.toString(m_iNextUniqueID);
        this.put(strUniqueID, obj);
        return strUniqueID;
    }
    /**
     * Find the key for the baseholder that holds this session.
     */
    public String find(RemoteBaseSession obj)
    {
        for (String strKey : this.keySet())
        {
            BaseHolder value = this.get(strKey);
            if (value.getRemoteObject() == obj)
                return strKey;            
        }
        return null;    // Not found.
    }
    /**
     * Find the key for this BaseHolder
     */
    public String find(BaseHolder obj)
    {
        for (String strKey : this.keySet())
        {
            BaseHolder value = this.get(strKey);
            if (value == obj)
                return (String)strKey;
        }
        return null;    // Not found.
    }
    /**
     *
     */
    public boolean remove(BaseHolder obj)
    {
        String strID = this.find(obj);
        if (strID != null)
            return (this.remove(strID) != null);
        return false;    // Not found.
    }
}
