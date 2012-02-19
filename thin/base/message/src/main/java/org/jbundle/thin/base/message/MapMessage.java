/*
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jbundle.model.util.Constant;
import org.jbundle.model.util.Util;
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
public class MapMessage extends BaseMessage
    implements Serializable
{
	private static final long serialVersionUID = 1L;


    /**
     * Creates new BaseMessage.
     */
    public MapMessage()
    {
        super();
    }
    /**
     * Constructor.
     * @param messageHeader The message header which contains information such as destination and type.
     * @param data This properties object is a default way to pass messages.
     */
    public MapMessage(BaseMessageHeader messageHeader, Object data)
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
        super.setData((Map)data);  // Just being careful
    }
    /**
     * Get the raw data object for this message.
     * @return
     */
    public Object getData()
    {
        return (Map)m_data;
    }
    /**
     * Get the native class type of this class of data.
     */
    public Class<?> getNativeClassType(Class<?> rawClassType)
    {
        return rawClassType;
    }
    /**
     * Get the raw data for this (xpath) key.
     * @param strParam  The xpath key.
     * @return The raw data at this location in the message.
     */
    public Object getNative(String key)
    {
        return this.getMap().get(key);
    }
    /**
     * Put the raw value for this param in the (xpath) map.
     * @param strParam The xpath key.
     * @param objValue The raw data to set this location in the message.
     */
    public void putNative(String key, Object value)
    {
        this.getMap().put(key, value);
    }
    /**
     * Convenience method to cast data to a map.
     * and create a map if one doesn't exist.
     * @return The data map.
     */
    private Map<String,Object> getMap()
    {
        if (m_data == null)
            m_data = new HashMap<String,Object>();
        return (Map<String, Object>)m_data;
    }
    /**
     * Get a Iterator of the keys in this message.
     * @return An iterator of the keys in this message.
     */
    public Iterator<String> keySet()
    {
        return this.getMap().keySet().iterator();
    }
    /**
     * Get the message data as a XML String.
     * @return The XML String.
     */
    public String getXML(boolean bIncludeHeader)
    {
        StringBuffer sbXML = new StringBuffer();
        String rootTag = ROOT_TAG;
        if (this.getMessageDataDesc(null) != null)
        	if (this.getMessageDataDesc(null).getKey() != null)
        		rootTag = this.getMessageDataDesc(null).getKey();
        Util.addStartTag(sbXML, rootTag).append(Constant.RETURN);
        Util.addXMLMap(sbXML, this.getMap());
        if (bIncludeHeader)
            if (this.getMessageHeader() != null)
                this.getMessageHeader().addXML(sbXML);
        Util.addEndTag(sbXML, rootTag);
        return sbXML.toString();
    }
    /**
     * Set the message data as a DOM Hierarchy.
     * @return True if successful.
     */
    public boolean setDOM(Node nodeXML)
    {
        if (nodeXML.getNodeType() == Node.DOCUMENT_NODE)
            if (nodeXML.getChildNodes().getLength() == 1)
            {
                String rootTag = ROOT_TAG;
                if (this.getMessageDataDesc(null) != null)
                	if (this.getMessageDataDesc(null).getKey() != null)
                		rootTag = this.getMessageDataDesc(null).getKey();
                if (rootTag.equals(nodeXML.getChildNodes().item(0).getNodeName()))
                    nodeXML = nodeXML.getChildNodes().item(0);  // Pass root node.
            }
        Map<String,Object> map = new HashMap<String,Object>();
        MapMessage.convertDOMtoMap(nodeXML, map, false);
        for (String key : map.keySet())
        {
        	this.putString(key, map.get(key).toString());
        }
        return true;    // Success
    }
    /**
     * Set the message data as a DOM Hierarchy.
     * @return True if successful.
     */
    public static boolean convertDOMtoMap(Node nodeXML, Map<String,Object> map, boolean bProcessChildren)
    {
        NodeList nodeList = nodeXML.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            String strKey = node.getNodeName();
            short sNodeType = node.getNodeType();
            if (sNodeType == Node.ELEMENT_NODE)
            {
                String strValue = Constant.BLANK;
                if (node.getChildNodes() == null)
                    strValue = null;
                else if (node.getChildNodes().getLength() == 0)
                    strValue = null;
                else if ((node.getChildNodes().getLength() == 1) && (node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE))
                    strValue =  node.getChildNodes().item(0).getNodeValue();
                else if (bProcessChildren)
                	MapMessage.convertDOMtoMap(node, map, bProcessChildren);
                if ((strValue != Constant.BLANK) && (strValue != null))
                    map.put(strKey, strValue);
                //?else
                //?    Util.getLogger().warning("--------Error - Create Non-text map node? ------");
            }
            else // if (sNodeType == Node.TEXT_NODE)
            {
                // ???
            }
        }
        return true;    // Success
    }
}
