/**
 * NameValue.java
 *
 * Created on July 9, 2000, 5:00 AM
 */
 
package org.jbundle.base.message.core.tree;

import java.util.Iterator;

import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MessageConstants;

/** 
 *
 * @author  Administrator
 * @version 1.0.0
 */
public class Registry extends NameValue
{
    /**
     * The receiver that owns this registry..
     */
    protected TreeMessageFilterList m_filterList = null;
    /**
     * The name (and value) of this NameValue node
     */
    public final static String ROOT = "root";

    /**
      * Creates new NameValue registry.
      */
    public Registry()
    {
        super();
    }
    /**
     * Creates new NameValue Registry.
     * @param receiver The receiver that owns this registry.
     */
    public Registry(TreeMessageFilterList filterList)
    {
        this();
        this.init(filterList);
    }
    /**
     * Creates new NameValue registry.
     * @param receiver The receiver that owns this registry.
     * @param strName The name of the registry (always root).
     */
    public void init(TreeMessageFilterList filterList)
    {
        m_filterList = filterList;
        super.init(ROOT, ROOT);
    }
    /**
     * Free this registry (and the entire tree).
     */
    public void free()
    {
        super.free();
        m_filterList = null;
    }
    /**
     * Add this filter to the end of the name/value chain.
     * @param filter The filter to add.
     */
    public void addMessageFilter(BaseMessageFilter filter)
    {
        this.getNameValueLeaf(filter, true).addThisMessageFilter(filter);
    }
    /**
     * Remove this filter from the end of the name/value chain.
     * @param filter The filter to remove.
     */
    public boolean removeMessageFilter(BaseMessageFilter filter)
    {
        NameValue value = this.getNameValueLeaf(filter, false);
        if (value == null)
        {
            System.out.println("Registry/80 - Error Leaf not found");
            return false;   // Error
        }
        boolean bSuccess = value.removeThisMessageFilter(filter);
        this.cleanNameValueTree(filter);
        return bSuccess;
    }
    /**
     * Remove any enpty nodes from the end of the name/value chain.
     * @param filter The filter to clean up.
     */
    public void cleanNameValueTree(BaseMessageFilter filter)
    {
        NameValue[] rgNodes = this.getNameValueArray(filter);
        for (int i = rgNodes.length - 1; i > 0; i--)
        {       // From the last node to the one after the root node.
            NameValue node = rgNodes[i];
            if (!node.isEmpty())
                return;     // Okay, don't need to check any other nodes up the tree.
            // It is empty, remove it from the parent node and free it.
            NameValue nodeParent = rgNodes[i - 1];
            if (!nodeParent.removeNameValueNode(node))
                System.out.println("Error: tree node not removed");
            node.free();
        }
    }
    /**
     * Get the value nodes in this name/value chain.
     * @param header The message header that contains the name/value tree.
     * @oaram bAddIfNotFound Add the value if it is not found.
     * @return The node at the end of the name/value tree.
     */
    public NameValue[] getNameValueArray(BaseMessageHeader header)
    {
        Object[][] mxString = header.getNameValueTree();
        NameValue[] rgNodes = new NameValue[10];
        rgNodes[0] = this;
        int i = 0;
        if (mxString != null)
        {
            for (; i < mxString.length; i++)
            {
                rgNodes[i + 1] = rgNodes[i].getNameValueNode((String)mxString[i][MessageConstants.NAME], mxString[i][MessageConstants.VALUE], false);
                if (rgNodes[i + 1] == null)
                    break;
            }
        }
        NameValue[] rgNodesNew = new NameValue[i + 1];
        for (int j = 0; j <= i; j++)
        {
            rgNodesNew[j] = rgNodes[j];
        }
        return rgNodesNew;
    }
    /**
     * Get the value node at the end of this name/value chain.
     * @param header The message header that contains the name/value tree.
     * @oaram bAddIfNotFound Add the value if it is not found.
     * @return The node at the end of the name/value tree.
     */
    public NameValue getNameValueLeaf(BaseMessageHeader header, boolean bAddIfNotFound)
    {
        Object[][] mxString = header.getNameValueTree();
        NameValue node = this;
        if (mxString != null)
        {
            for (int i = 0; i < mxString.length; i++)
            {
                node = node.getNameValueNode((String)mxString[i][MessageConstants.NAME], mxString[i][MessageConstants.VALUE], bAddIfNotFound);
                if (node == null)
                    return null;
            }
        }
        return node;
    }
    /**
     * Get the list of filters for this message header.
     * Override this to implement another (tree?) filter.
     * @param messageHeader The message header to get the list for.
     * @return The list of filters.
     */
    public Iterator getFilterList(BaseMessageHeader messageHeader)
    {
        Iterator iterator = null;
        Object[][] mxString = messageHeader.getNameValueTree();
        NameValue node = this;
        int iIndex = -1;
        while (node != null)
        {
            Iterator nodeIterator = node.getFilterIterator();
            if (nodeIterator != null)
            {
                if (iterator == null)
                    iterator = nodeIterator;
                else
                {
                    if (!(iterator instanceof MultiIterator))
                        iterator = new MultiIterator(iterator);
                    ((MultiIterator)iterator).addIterator(nodeIterator);
                }
            }
            iIndex++;
            if ((mxString != null) && (iIndex < mxString.length))
                node = node.getNameValueNode((String)mxString[iIndex][MessageConstants.NAME], mxString[iIndex][MessageConstants.VALUE], false);
            else
                node = null;    // done
        }
        if (iterator == null)
            return gEmptyIterator;
        return iterator;
    }
    /**
     * Create a special iterator that will iterate through several iterators
     */
    class MultiIterator extends Object
        implements Iterator
    {
        /**
         * My list of iterators.
         */
        protected Iterator[] m_rgIterator = null;
        /**
         * Current index on the iterator.
         */
        protected int m_iIndex = 0;
        
        /**
         * Constructor.
         */
        public MultiIterator()
        {
            super();
        }
        /**
         * Constructor.
         */
        public MultiIterator(Iterator iterator)
        {
            this();
            this.init(iterator);
        }
        /**
         * Constructor.
         */
        public void init(Iterator iterator)
        {
            m_rgIterator = new Iterator[10];
            m_iIndex = -1;
            
            this.addIterator(iterator);
        }
        /**
         * Add an iterator to the list.
         */
        public void addIterator(Iterator iterator)
        {
            m_iIndex++;
            m_rgIterator[m_iIndex] = iterator;
        }
        /**
         * Is there another filter?
         * @return true if there is.
         */
        public boolean hasNext()
        {
            if (m_iIndex < 0)
                return false;
            boolean bHasNext = m_rgIterator[m_iIndex].hasNext();
            if (!bHasNext)
            {
                m_iIndex--;
                return this.hasNext();
            }
            return bHasNext;
        }
        /**
         * Get the next filter?
         * @return the filter if there is one.
         */
        public Object next()
        {
            if (m_iIndex < 0)
                return null;
            Object obj = m_rgIterator[m_iIndex].next();
            if (obj == null)
            {
                m_iIndex--;
                return this.next();
            }
            return obj;
        }
        /**
         * Remove the filter (not implemented).
         */
        public void remove()
        {
        }
    }
    /**
     *
     */
    public static EmptyIterator gEmptyIterator = new EmptyIterator();
    /**
     * A special iterator that is empty.
     */
    public static class EmptyIterator
        implements Iterator
    {
        /**
         * Is there another filter?
         * @return true if there is.
         */
        public boolean hasNext()
        {
            return false;
        }
        /**
         * Get the next filter?
         * @return the filter if there is one.
         */
        public Object next()
        {
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
