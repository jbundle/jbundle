/**
 * NameValue.java
 *
 * Created on July 9, 2000, 5:00 AM
 */
 
package org.jbundle.base.message.core.tree;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.jbundle.thin.base.message.BaseMessageFilter;


/** 
 * A NameValue is a vame/value node.
 * @author  Administrator
 * @version 1.0.0
 */
public class NameValue extends Object
{
    /**
     * The name or value for this node.
     */
    protected String m_strName = null;
    /**
     * The name or value for this node.
     */
    protected Object m_objValue = null;
    /**
     * Child nodes.
     */
    protected Map<String,NameValue> m_mapNameValue = null;
    /**
     * The (optional) filter or filter list.
     * This can either be a BaseMessageFilter or a Map of filters.
     */
    protected Object m_filter = null;

    /**
      * Creates new NameValue.
     */
    public NameValue()
    {
        super();
    }
    /**
     * Creates new NameValue.
     * @param strName The name of this node.
     * @param objValue The value for this node.
     */
    public NameValue(String strName, Object objValue)
    {
        this();
        this.init(strName, objValue);
    }
    /**
     * Creates new NameValue.
     * @param strName The name of this node.
     * @param objValue The value for this node.
     */
    public void init(String strName, Object objValue)
    {
        m_strName = strName;
        m_objValue = objValue;
    }
    /**
     * Free this node (and any others in this tree).
     */
    public void free()
    {
        if (m_mapNameValue != null)
        {
            Iterator iterator = m_mapNameValue.values().iterator();
            while (iterator.hasNext())
            {
                ((NameValue)iterator.next()).free();
            }
        }
        m_mapNameValue = null;
        if (m_filter != null)
        {   // Should be empty
        }
        m_filter = null;
        m_strName = null;
        m_objValue = null;
    }
    /**
     * Is this node empty.
     * @return true if it is empty.
     */
    public boolean isEmpty()
    {
        if (m_filter != null)
            return false;   // Not empty
        if (m_mapNameValue != null)
            if (m_mapNameValue.size() != 0)
                return false;   // Not empty;
        return true;
    }
    /**
     * Are these object equal?
     * @param obj The (node) to compare.
     * @return True if the node is equal to this one.
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof NameValue)
        {   // Always
            if (((NameValue)obj).getName().equalsIgnoreCase(this.getName()))
                if (((NameValue)obj).getValue().equals(this.getValue()))
                    return true;
        }
        return super.equals(obj);
    }
    /**
     * Get the name.
     * @return This node's name.
     */
    public String getName()
    {
        return m_strName;
    }
    /**
     * Get the value.
     * @return This node's value.
     */
    public Object getValue()
    {
        return m_objValue;
    }
    /**
     * Get the key for this node.
     * @return The key.
     */
    public String getKey()
    {
        return this.getKey(this.getName(), this.getValue());
    }
    /**
     * Create a key for the name/value pair.
     */
    public String getKey(String strName, Object objValue)
    {
        return strName + '@' + objValue.toString();
    }
    /**
     * Get the next Lower nodes.
     * @param bAddIfNotFound If true, set up a new value map if not set up yet.
     * @return The value map.
     */
    public Map<String,NameValue> getValueMap(boolean bAddIfNotFound)
    {
        if (m_mapNameValue == null)
            if (bAddIfNotFound)
                m_mapNameValue = new Hashtable<String,NameValue>();
        return m_mapNameValue;
    }
    /**
     * Add this node to my value map.
     * @param node The node to add.
     */
    public void addNameValueNode(NameValue node)
    {
        this.getValueMap(true).put(node.getKey(), node);
    }
    /**
     * Add this node to my value map.
     * @param node The node to add.
     */
    public boolean removeNameValueNode(NameValue node)
    {
        Map<String,NameValue> map = this.getValueMap(false);
        if (map == null)
            return false;   // Error
        return (map.remove(node.getKey()) == node);
    }
    /**
     * Get my child value node (that matches this value).
     * @param objValue The value to find/match.
     * @param bAddIfNotFound Add this value if it was not found.
     * @return Return this child value node.
     */
    public NameValue getNameValueNode(String strName, Object objValue, boolean bAddIfNotFound)
    {
        Map<String,NameValue> map = this.getValueMap(bAddIfNotFound);
        if (map == null)
            return null;
        NameValue value = (NameValue)map.get(this.getKey(strName, objValue));
        if (value == null)
            if (bAddIfNotFound)
                this.addNameValueNode(value = new NameValue(strName, objValue));
        return value;
    }
    /**
     * Set the owner of this value.
     */
    public void addThisMessageFilter(BaseMessageFilter filter)
    {
        if (m_filter == null)
            m_filter = filter;
        else
        {
            if (m_filter instanceof BaseMessageFilter)
            {
                BaseMessageFilter filterFirst = (BaseMessageFilter)m_filter;
                m_filter = new Hashtable<String,Object>();
                ((Map)m_filter).put(filterFirst.getFilterID(), filterFirst);
            }
            ((Map)m_filter).put(filter.getFilterID(), filter);
        }
    }
    /**
     * Remove this filter from the filter list.
     * @param filter The filter to remove.
     */
    public boolean removeThisMessageFilter(BaseMessageFilter filter)
    {
        if (m_filter instanceof BaseMessageFilter)
        {
            if (m_filter != filter) // Should be this one.
                return false;   // Error
            m_filter = null;
            return true;    // Success
        }
        else
            return (((Map)m_filter).remove(filter.getFilterID()) != null);
    }
    /**
     * Get the list of filters for this leaf node.
     */
    public Iterator getFilterIterator()
    {
        if (m_filter instanceof Map)
            return ((Map)m_filter).values().iterator();
        else if (m_filter != null)
            return new OneFilterIterator();
        else
            return null;    // Okay
    }
    /**
     * A special iterator if there is only one filter (and it is m_filter).
     */
    class OneFilterIterator extends Object
        implements Iterator
    {
        /**
         * Keep track of the first time.
         */
        protected boolean bFirstTime = true;
        
        /**
         * Is there another filter?
         * @return true if there is.
         */
        public boolean hasNext()
        {
            if (bFirstTime)
                if (m_filter instanceof BaseMessageFilter)  // Always
                    return true;
            return false;
        }
        /**
         * Get the next filter?
         * @return the filter if there is one.
         */
        public Object next()
        {
            if (bFirstTime)
            {
                bFirstTime = false;
                if (m_filter instanceof BaseMessageFilter)  // Always
                    return m_filter;
            }
            return null;
        }
        /**
         * Remove the filter (not implemented).
         */
        public void remove()
        {
        }
    }
}
