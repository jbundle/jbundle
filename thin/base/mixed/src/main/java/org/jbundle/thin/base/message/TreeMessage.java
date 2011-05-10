/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
package org.jbundle.thin.base.message;

import java.io.Serializable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;

import org.jbundle.thin.base.db.Constant;
import org.jbundle.thin.base.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/** 
 * A BaseMessage is the core message class.
 * As a default, it contains a Header and Properties.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class TreeMessage extends BaseMessage
    implements Serializable
{
	private static final long serialVersionUID = 1L;

    /**
     * Creates new BaseMessage.
     */
    public TreeMessage()
    {
        super();
    }
    /**
     * Constructor.
     * @param messageHeader The message header which contains information such as destination and type.
     * @param data This properties object is a default way to pass messages.
     */
    public TreeMessage(BaseMessageHeader messageHeader, Object data)
    {
        this();
        this.init(messageHeader, data);
    }
    /**
     * Constructor.
     * @param messageHeader The message header which contains information such as destination and type.
     * @param data This properties object is a default way to pass messages.
     */
    public void init(BaseMessageHeader messageHeader, Object data)
    {
        super.init(messageHeader, data);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set the raw data object.
     * @param data
     */
    public void setData(Object data)
    {
        super.setData((Node)data);  // Just being careful
    }
    /**
     * Get the raw data object for this message.
     * @return
     */
    public Object getData()
    {
        return (Node)m_data;
    }
    /**
     * Get the native class type of this class of data.
     */
     public Class<?> getNativeClassType(Class<?> rawClassType)
     {
         return String.class;
     }
    /**
     * Get the raw data for this (xpath) key.
     * @param strParam  The xpath key.
     * @return The raw data at this location in the message.
     */
    public Object getNative(String key)
    {
        Node node = this.getNode(null, key, CreateMode.DONT_CREATE, true);
        if (node != null)
        {            
            Object object = node.getNodeValue();
            if (object instanceof String)   // Always
                if (((String)object).length() > 0)  // Only return if non-null
                    return object;
        }
        return null;
    }
    /**
     * Put the raw value for this param in the (xpath) map.
     * @param strParam The xpath key.
     * @param objValue The raw data to set this location in the message.
     */
    public void putNative(String key, Object value)
    {
        String strValue = null;
        if (value != null)
            strValue = value.toString();
        CreateMode createMode = CreateMode.DONT_CREATE;
        if (value != null)
        {
            createMode = CreateMode.CREATE_IF_NOT_FOUND;
            if (Util.isCData(strValue))
                createMode = CreateMode.CREATE_CDATA_NODE;
        }
        Node node = this.getNode(null, key, createMode, true);
        if (node != null)
        {
            if (strValue != null)
                node.setNodeValue(strValue);
            else
                node.getParentNode().removeChild(node);
        }
    }
    /**
     * Get a Iterator of the keys in this message.
     * @return An iterator of the keys in this message.
     */
    public Iterator<String> keySet()
    {
        Node node = getNode(true);
        NodeList nodeList = node.getChildNodes();
        return new NodeIterator(nodeList);
    }
    class NodeIterator
        implements Iterator<String>
    {
        protected NodeList m_nodeList = null;
        protected int m_iCurrentItem = 0;
        
        public NodeIterator(NodeList nodeList)
        {
            m_nodeList = nodeList;
            m_iCurrentItem = 0;
        }
        public boolean hasNext()
        {
            if ((m_nodeList == null)
                || (m_iCurrentItem >= m_nodeList.getLength()))
                    return false;
            return true;
        }
        public String next()
        {
            Node node = m_nodeList.item(m_iCurrentItem);
            String nodeName = node.getNodeName();
            // Bump the counter
            m_iCurrentItem++;
            while (m_iCurrentItem < m_nodeList.getLength())
            {
                if (m_nodeList.item(m_iCurrentItem).getNodeType() == Node.ELEMENT_NODE)
                    break;
                m_iCurrentItem++;
            }
            return nodeName;
        }
        public void remove()
        {
        }
    }
    /**
     * Get the number of data nodes at this path in the data.
     * @param strKey The xpath key to the data.
     * @return The number of data nodes at this location.
     */
    public int getNodeCount(String strKey)
    {
        int iCount = 0;
        Node node = this.getNode(null, strKey, CreateMode.DONT_CREATE, false);
        if (node != null)
        {
            String key = strKey;
            if (key.lastIndexOf('/') != -1)
                key = key.substring(key.lastIndexOf('/') + 1);
            Node nodeStart = node.getParentNode();
            NodeList nodeList = ((Element)nodeStart).getElementsByTagName(key);
            if (nodeList != null)
            {
                for (int i = 0; i < nodeList.getLength(); i++)
                {
                    if (nodeList.item(i).getParentNode() == nodeStart)
                        iCount++;    // Get the last child.
                }
            }
        }
        return iCount;   // Override this
    }
    /**
     * Create this new (empty) node in the message.
     * @param strKey The location of the node to create.
     */
    public Object createNewNode(String strKey)
    {
        Node node = this.getNode(null, strKey, CreateMode.CREATE_NEW_NODE, false);
        return node;
    }
    enum CreateMode
    {
        DONT_CREATE,
        CREATE_IF_NOT_FOUND,
        CREATE_NEW_NODE,
        CREATE_CDATA_NODE,
    }
    /**
     * Convenience method to cast data to a map.
     * and create a map if one doesn't exist.
     * @return The data map.
     */
    private Node getNode(Node nodeStart, String key, CreateMode createMode, boolean bReturnTextNode)
    {
        if (key.indexOf('/') != -1)
        {
            String strKeyParent = key.substring(0, key.indexOf('/'));
            CreateMode createModeParent = createMode;
            if (createModeParent == CreateMode.CREATE_NEW_NODE)
                createModeParent = CreateMode.CREATE_IF_NOT_FOUND;  // Only create a duplicate node at the leaf level.
            nodeStart = this.getNode(nodeStart, strKeyParent, createModeParent, false);
            if (nodeStart == null)
                return null;
            key = key.substring(key.indexOf('/') + 1);
            return this.getNode(nodeStart, key, createMode, bReturnTextNode);
        }
        if (nodeStart == null)
            nodeStart = this.getNode(true);

        int iIndex = Integer.MAX_VALUE;
        if (key.indexOf('[') != -1)
        {
            iIndex = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
            key = key.substring(0, key.indexOf('['));
        }
        key = Util.fixDOMElementName(key);
        Node node = null;
        NodeList nodeList = ((Element)nodeStart).getElementsByTagName(key);
        if (nodeList != null)
        {
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                if (nodeList.item(i).getParentNode() == nodeStart)
                {
                    node = nodeList.item(i);    // Get the last child.
                    if (iIndex != Integer.MAX_VALUE)
                    {
                        if (--iIndex == 0)
                            break;  // This is the correct node
                        node = null;    // If you are looking for a specific node, and it doesn't exist, return null.
                    }
                }
            }
        }
        if (((node == null) && (createMode != CreateMode.DONT_CREATE))
                || (createMode == CreateMode.CREATE_NEW_NODE))
        {
            node = ((Document)m_data).createElement(key);
            if (bReturnTextNode)
            {
                if (createMode != CreateMode.CREATE_CDATA_NODE)
                    node.appendChild(((Document)m_data).createTextNode(Constant.BLANK));
                else
                    node.appendChild(((Document)m_data).createCDATASection(Constant.BLANK));
            }
            nodeStart.appendChild(node);
        }
        if (node != null)
            if (bReturnTextNode)
                node = node.getFirstChild();
        return node;    // Return new node or null.
    }
    /**
     * Convenience method to cast data to a map.
     * and create a map if one doesn't exist.
     * @return The data map.
     */
    private Node getNode(boolean bDataRoot)
    {
        if (m_data == null)
        {
            DocumentBuilder db = Util.getDocumentBuilder();
            synchronized (db)
            {
                m_data = (Node)db.newDocument();
                String rootTag = ROOT_TAG;
                if (this.getMessageDataDesc(null) != null)
                	if (this.getMessageDataDesc(null).getKey() != null)
                		rootTag = this.getMessageDataDesc(null).getKey();
                ((Document)m_data).appendChild(((Document)m_data).createElement(rootTag));
            }
        }
        if (bDataRoot)
            return ((Document)m_data).getDocumentElement();
        return (Node)m_data;
    }
    /**
     * Get the message data as a XML String.
     * @return The XML String.
     */
    public String getXML(boolean bIncludeHeader)
    {
        String strOut = Util.convertDOMToXML(this.getNode(false));
        if (bIncludeHeader)
        {
            String strHeaderXML = this.getMessageHeader().addXML(null).toString();
            int iStartOfRootTag = strOut.lastIndexOf("</");
            if (iStartOfRootTag != -1)
                strOut = strOut.substring(0, iStartOfRootTag) + strHeaderXML + strOut.substring(iStartOfRootTag);
        }
        return strOut;
    }
    /**
     * Set the message data as a DOM Hierarchy.
     * @return True if successful.
     */
    public boolean setDOM(Node nodeXML)
    {
        m_data = nodeXML;
        return true;    // Success
    }
    /**
     * Set the message data as a DOM Hierarchy.
     * @return True if successful.
     */
    public Node getDOM()
    {
        return (Node)m_data;
    }
}
