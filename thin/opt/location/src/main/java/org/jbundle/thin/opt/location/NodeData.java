/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.opt.location;
/**
 * @(#)SampleData.java  1.3 99/04/23
 */

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.screen.BaseApplet;


/**
  * @version 1.0.0
  * @author Scott Violet
  */

public class NodeData extends Object
{

    /** Value to display. */
    protected BaseApplet m_baseApplet = null;

    /** Value to display. */
    protected RemoteSession m_remoteSession = null;

    /** Value to display. */
    protected String m_strDescription = null;

    /** The record that this tree is filled with **/
    protected String m_strRecordName = null;

    /** The record that this tree is filled with **/
    protected String m_objID = null;

    /**
      * Constructs a new instance of SampleData with the passed in arguments.
      */
    public NodeData(BaseApplet baseApplet, RemoteSession remoteSession, String strDescription, String objID, String strRecordName)
    {
        super();
        this.init(baseApplet, remoteSession, strDescription, objID, strRecordName);
    }
    /**
      * Constructs a new instance of SampleData with the passed in arguments.
      */
    public void init(BaseApplet baseApplet, RemoteSession remoteSession, String strDescription, String objID, String strRecordName)
    {
        m_baseApplet = baseApplet;
        m_remoteSession = remoteSession;
        m_strDescription = strDescription;
        m_objID = objID;
        m_strRecordName = strRecordName;
    }
    /**
     * Sets the string to display for this object.
     */
    public void setString(String strDescription)
    {
        m_strDescription = strDescription;
    }
    /**
     * Returns the string to display for this object.
     */
    public String string()
    {
        return m_strDescription;
    }
    /**
     * Returns the string to display for this object.
     */
    public String toString()
    {
        return m_strDescription;
    }
    /**
     * Returns the string to display for this object.
     */
    public String getID()
    {
        return m_objID;
    }
    /**
     * Returns the string to display for this object.
     */
    public String getRecordName()
    {
        return m_strRecordName;
    }
    /**
     * Returns the field list.
     */
    public FieldList makeRecord()
    {
        // Make record from desc name
//x     FieldList fieldList = new org.jbundle.thin.tour.product.Continent(null);
//x     m_baseApplet.linkNewRemoteTable(null, fieldList);
//x     return fieldList;
        FieldList record = null;
        try   {
            Map<String,Object> properties = new Hashtable<String,Object>();
            properties.put("description", m_strDescription);
            if (m_objID != null)
                properties.put("id", m_objID);
            String strSubRecordName = this.getSubRecordClassName();
            if (strSubRecordName != null)
                properties.put("record", strSubRecordName);
            m_remoteSession.doRemoteAction("requery", properties);
            RemoteTable remoteTable = m_remoteSession.getRemoteTable(strSubRecordName);
            record = remoteTable.makeFieldList(null); // NO!
            new org.jbundle.thin.base.db.client.RemoteFieldTable(record, remoteTable, m_baseApplet);
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        return record;
    }
    /**
     * Returns the field list.
     */
    public RemoteSession getRemoteSession()
    {
        return m_remoteSession;
    }
    public BaseApplet getBaseApplet()
    {
        return m_baseApplet;
    }
    protected static final String[] m_rgstrRecordHierarchy = {
        "root",
        "Continent",
        "Region",
        "Country",
//      "State",
        "City"
    };
    /**
     * Get the next logical sub-record class, given this record's class.
     */
    public String getSubRecordClassName()
    {
        for (int i = 0; i < m_rgstrRecordHierarchy.length - 1; i++)
        {
            if (m_rgstrRecordHierarchy[i].equalsIgnoreCase(m_strRecordName))
                return m_rgstrRecordHierarchy[i+1];
        }
        return null;    // No further leafs //m_rgstrRecordHierarchy[0];
    }
    /**
     * Fake the caller into believing this is a leaf, when I don't really know yet.
     */
    public boolean isLeaf()
    {
        if ((m_strRecordName == null)
            || (m_strRecordName.equalsIgnoreCase(m_rgstrRecordHierarchy[m_rgstrRecordHierarchy.length - 1])))
                return true;    // Last line
        return false;
    }
}
