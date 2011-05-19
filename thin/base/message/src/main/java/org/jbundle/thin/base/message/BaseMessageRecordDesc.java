/*
 * HotelRateRequestOut.java
 *
 * Created on September 26, 2003, 12:41 AM
 */

package org.jbundle.thin.base.message;

import java.util.HashMap;
import java.util.Map;

import org.jbundle.model.PropertyOwner;
import org.jbundle.model.db.Convert;
import org.jbundle.model.db.Rec;
import org.jbundle.model.util.Constant;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * This is the base message for sending and receiving requests.
 * Data in this object are stored in the native java object type.
 * Data can either be extracted as the Raw object, an External object, or
 * a string:
 * Raw Object: Native java object.
 * External Object: Externally recognizable data (such as Hotel Name rather than HotelID).
 * String: External Object converted to ASCII (The conversion is specified in the DataDesc).
 * Typically, you override the rawToExternal and externlToRaw to do your conversion (none by default).
 * Also: XML: Typically External to String conversion (with tags) except for items such as dates.
 * @author  don
 */
public class BaseMessageRecordDesc extends MessageDataDesc
{
    /**
     * Returns a string representation of the object.
     * @return  a string representation of the object.
     */
    public String toString()
    {
        String string = super.toString() + " {";
        for (String key : m_messageDataDescChildren.keySet())
        {
            if (string.charAt(string.length() - 1) != '{')
                string = string + ",";
            string = string + m_messageDataDescChildren.get(key).toString();
        }
        string = string + "}";
        return string;
    }
    private static final long serialVersionUID = 1L;

    protected Map<String,MessageDataDesc> m_messageDataDescChildren = null;

    public static final boolean NON_UNIQUE_NODE = false;
    public static final boolean UNIQUE_NODE = true;
    
    protected boolean m_bNodeType = UNIQUE_NODE;

    public static final int RESET_INDEX = 0;
    public static final int END_OF_NODES = -1;
    protected int m_iNodeIndex = 0;
    
    /**
     * Creates a new instance of HotelRateRequestOut
     */
    public BaseMessageRecordDesc()
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public BaseMessageRecordDesc(MessageDataParent messageDataParent, String strKey)
    {
        this();
        this.init(messageDataParent, strKey);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(MessageDataParent messageDataParent, String strKey)
    {
        super.init(messageDataParent, strKey);
        
        this.setupMessageDataDesc();
    }
    /**
     * Setup all the data descriptions.
     */
    public void setupMessageDataDesc()
    {
        // Override this with lines like this:
//        this.addMessageDataDesc(Product.PRODUCT_ID_PARAM, Integer.class, REQUIRED, "Y");
    }
    /**
    *
    */
//+   public static final MessageDataDesc DEFAULT_DATA_DESC = new MessageDataDesc(DBConstants.BLANK, String.class, null, null, false, null);
    /**
     * Add the data description for this param.
     */
    public MessageFieldDesc addMessageFieldDesc(String strParam, Class<?> classRawObject, boolean bRequired, Object objRawDefault)
    {
        return new MessageFieldDesc(this, strParam, classRawObject, bRequired, objRawDefault);
    }
    /**
     * Add the data description for this param.
     */
    public MessageFieldDesc addMessageFieldDesc(String strParam, Class<?> classRawObject, boolean bRequired, int iKeyInformation, Object objRawDefault)
    {
        return new MessageFieldDesc(this, strParam, classRawObject, bRequired, iKeyInformation, objRawDefault);
    }
    /**
     * Add a child message data desc.
     */
    public void addMessageDataDesc(MessageDataDesc messageDataDesc)
    {
        if (m_messageDataDescChildren == null)
            m_messageDataDescChildren = new HashMap<String,MessageDataDesc>();
        if (messageDataDesc != null)
        {
            m_messageDataDescChildren.put(messageDataDesc.getKey(), messageDataDesc);
            messageDataDesc.setMessageDataParent(this);
        }
    }
    /**
     * Get the data description for this param.
     */
    public MessageDataDesc getMessageDataDesc(String strParam)
    {
        if (strParam == null)
            return this;
        if (strParam.equals(this.getKey()))
            return this;
        if (m_messageDataDescChildren == null)
        {
            m_messageDataDescChildren = new HashMap<String,MessageDataDesc>();
            this.setupMessageDataDesc();
        }
        return (MessageDataDesc)m_messageDataDescChildren.get(strParam);
    }
    /**
     *
     */
    public void removeMessageDataDesc(String strParam)
    {
        if (m_messageDataDescChildren != null)
            m_messageDataDescChildren.remove(strParam);
    }
    /**
     * Get the message description for this (xpath) key.
     * @return The message data desc.
     */
    public MessageFieldDesc getMessageFieldDesc(String strKey)
    {
        if (strKey.lastIndexOf('/') != -1)
        {
            String strParentKey = strKey.substring(0, strKey.lastIndexOf('/'));
            String strFieldKey = strKey.substring(strKey.lastIndexOf('/') + 1);
            MessageDataDesc messageRecordDesc = this.getMessageFieldDesc(strParentKey);
            if (messageRecordDesc == null)
                return null;
            return messageRecordDesc.getMessageFieldDesc(strFieldKey);
        }
        if (this.getMessageDataDesc(strKey) instanceof MessageFieldDesc)
            return (MessageFieldDesc)this.getMessageDataDesc(strKey);
        return null;
    }
    /**
     * Get the node type.
     * @return
     */
    public boolean getNodeType()
    {
        return m_bNodeType;
    }
    /**
     * Set the node type.
     * @param bNodeType
     */
    public void setNodeType(boolean bNodeType)
    {
        m_bNodeType = bNodeType;
    }
    /**
     * Does this message only include a single booking detail item?.
     */
    public boolean isSingleDetail(Rec record)
    {
        boolean bSingleDetail = true;
        
        if (Boolean.toString(false).equals(this.getMessage().getMessageHeader().get(BaseMessageHeader.SINGLE_DETAIL_PARAM)))
            return false;
        
        return bSingleDetail;
    }
    /**
     * Move the correct fields from ALL the detail records to the map.
     * Typically, you override this and loop through the records in the table.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     */
    public int handlePutRawRecordData(Rec record)
    {
        int iErrorCode = Constant.NORMAL_RETURN;
            
        iErrorCode = super.handlePutRawRecordData(record);
        
        if (iErrorCode == Constant.NORMAL_RETURN)
        {
            if (m_messageDataDescChildren != null)
            {
                for (String strKey : m_messageDataDescChildren.keySet())
                {
                    iErrorCode = m_messageDataDescChildren.get(strKey).handlePutRawRecordData(record);
                    if (iErrorCode != Constant.NORMAL_RETURN)
                        break;
                }
            }
        }

        return iErrorCode;
    }
    /**
     * Move the correct fields from this current record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     */
    public int putRawRecordData(Rec record)
    {
        if (this.getNodeType() == BaseMessageRecordDesc.NON_UNIQUE_NODE)
            this.getMessage().createNewNode(this.getFullKey(null));
        return super.putRawRecordData(record);
    }
    /**
     * This utility sets this param to the field's raw data.
     * @param record The record to get the data from.
     */
    public int putRawFieldData(Convert field)
    {
        String strKey = this.getFullKey(field.getFieldName());
        Class<?> classData = String.class;
        if (field.getField() != null)
            classData = this.getMessage().getNativeClassType(field.getField().getDataClass());
        Object objValue = field.getData();
        try {
            objValue = Converter.convertObjectToDatatype(objValue, classData, null);  // I do this just to be careful.
        } catch (Exception ex) {
            objValue = null;
        }
        this.getMessage().putNative(strKey, objValue);
        return Constant.NORMAL_RETURN;
    }
    /**
     * Move the map values to the correct record fields.
     * @param record The record to move the data to.
     * @return The error code.
     */
    public int handleGetRawRecordData(Rec record)
    {
        int iErrorCode = Constant.NORMAL_RETURN;
            
        iErrorCode = super.handleGetRawRecordData(record);
        
        if (iErrorCode == Constant.NORMAL_RETURN)
        {
            if (m_messageDataDescChildren != null)
            {
                for (String strKey : m_messageDataDescChildren.keySet())
                {
                    iErrorCode = m_messageDataDescChildren.get(strKey).handleGetRawRecordData(record);
                    if (iErrorCode != Constant.NORMAL_RETURN)
                        break;
                }
            }
        }

        return iErrorCode;
    }
    /**
     * Move the map values to the correct record fields.
     * @param record The record to move the data to.
     * @return The error code.
     */
    public int getRawRecordData(Rec record)
    {
        return super.getRawRecordData(record);    // Override this
    }
    /**
     * This utility sets this field to the param's raw data.
     */
    public int getRawFieldData(Convert field)
    {
        String strKey = this.getFullKey(field.getFieldName());
        Class<?> classData = field.getField().getDataClass();
        Object objValue = this.getMessage().getNative(strKey);
        try {
            objValue = Converter.convertObjectToDatatype(objValue, classData, null);  // I do this just to be careful.
        } catch (Exception ex) {
            objValue = null;
        }
        return field.setData(objValue);
    }
    /**
     * Position to this node in the tree.
     * @param iNodeIndex The node to position to.
     * @param record The record I am moving data to. If this is null, don't position/setup the data.
     * @return An error code.
     */
    public Rec setNodeIndex(int iNodeIndex, Rec record)
    {
        if (END_OF_NODES == iNodeIndex)
            iNodeIndex = 0;
        m_iNodeIndex = iNodeIndex;
        return record;
    }
    /**
     * Position to this node in the tree.
     * @param iNodeIndex The node to position to.
     * @param record The record I am moving data to. If this is null, don't position/setup the data.
     * @return An error code.
     */
    public Rec setDataIndex(int iNodeIndex, Rec record)
    {
        if (END_OF_NODES == iNodeIndex)
            iNodeIndex = 0;
        m_iNodeIndex = iNodeIndex;
        return record;
    }
    /**
     * Get the current node index.
     * @return
     */
    public int getNodeIndex()
    {
        return m_iNodeIndex;
    }
    /**
     * Get this item's key.
     */
    public String getKey()
    {
        String strReturnKey = super.getKey();
        if (m_iNodeIndex > 0) if (strReturnKey != null)
            strReturnKey = strReturnKey + '[' + Integer.toString(m_iNodeIndex) + ']';
        return strReturnKey;
    }
    /**
     * Move the data from this propertyowner to this message.
     */
    public void putRawProperties(PropertyOwner propertyOwner)
    {
        if (m_messageDataDescChildren != null)
        {
            for (String strKey : m_messageDataDescChildren.keySet())
            {
                m_messageDataDescChildren.get(strKey).putRawProperties(propertyOwner);
            }
        }
    }
    /**
     * Convenience method - Put the value for this param in the map.
     * If it is not the correct object type, convert it first.
     */
    public void put(String strKey, Object objValue)
    {
        if (this.getMessageFieldDesc(strKey) != null)
            this.getMessageFieldDesc(strKey).put(objValue);
        else if (this.getMessage() != null)
            this.getMessage().putNative(this.getFullKey(strKey), objValue);
    }
    /**
     * Convenience method - Get the data at this key.
     * @param trxMessage The raw internal trx message.
     * @return The new Internal message with additional/refined data.
     */
    public Object get(String strKey)
    {
        Object data = null;
        if (this.getMessageFieldDesc(strKey) != null)
            data = this.getMessageFieldDesc(strKey).get();
        else if (this.getMessage() != null)
            data = this.getMessage().getNative(this.getFullKey(strKey));
        return data;
    }
    /**
     * Setup this message given this internal data structure.
     * @param data The data in an intermediate format.
     */
    public static BaseMessageRecordDesc createMessageRecordDesc(String strMessageDataClassName, MessageDataParent messageDataParent, String strKey)
    {
        BaseMessageRecordDesc messageData = (BaseMessageRecordDesc)ClassServiceUtility.getClassService().makeObjectFromClassName(strMessageDataClassName);
        if (messageData != null)
        	messageData.init(messageDataParent, strKey);
        return messageData;
    }
    /**
     * Initialize the fields in this record to prepare for this message.
     * Also, do any other preparation needed before sending this message.
     * @param record The record to initialize
     * @return An error code if there were any problems.
     */
    public int initForMessage(Rec record)
    {
        int iErrorCode = Constant.NORMAL_RETURN;
        if (m_messageDataDescChildren != null)
        {
            for (String strKey : m_messageDataDescChildren.keySet())
            {
                MessageDataDesc messageDataDesc = m_messageDataDescChildren.get(strKey);
                iErrorCode = messageDataDesc.initForMessage(record);
                if (iErrorCode != Constant.NORMAL_RETURN)
                    break;
            }
        }
        return iErrorCode;
    }
    /**
     * Check to make sure all the data is present to attempt a cost lookup.
     * Note: You are NOT returning the status, you are returning the status of the params,
     * The calling program will change the status if required.
     * @return DATA_REQUIRED if all the data is not present, DATA_VALID if the data is OKAY.
     */
    public int checkRequestParams(Rec record)
    {
        int iMessageStatus = DATA_VALID;
        if (m_messageDataDescChildren != null)
        {
            for (String strKey : m_messageDataDescChildren.keySet())
            {
                MessageDataDesc messageDataDesc = m_messageDataDescChildren.get(strKey);
                iMessageStatus = messageDataDesc.checkRequestParams(record);
                if (iMessageStatus != DATA_VALID)
                    break;
            }
        }
        return iMessageStatus;    // Override this
    }
    /**
     * Get a unique key that describes the data in this record.
     * You can use this key to see if any of the data has changed since the message was last sent.
     */
    public StringBuffer getMessageKey(StringBuffer sb)
    {
        if (sb == null)
            sb = new StringBuffer();
        if (m_messageDataDescChildren != null)
        {
            for (String strKey : m_messageDataDescChildren.keySet())
            {
                MessageDataDesc messageDataDesc = m_messageDataDescChildren.get(strKey);
                messageDataDesc.getMessageKey(sb);
            }
        }
        return sb;
    }
    /**
     * Move the pertinenent information from the request to this reply message.
     * Override this to actually move information.
     */
    public void moveRequestInfoToReply(BaseMessage messageRequest)
    {
        if (m_messageDataDescChildren != null)
        {
            for (String strKey : m_messageDataDescChildren.keySet())
            {
                MessageDataDesc messageDataDesc = m_messageDataDescChildren.get(strKey);
                MessageDataDesc requestMessageDataDesc = messageRequest.getMessageDataDesc(messageDataDesc.getFullKey(null));
                if (requestMessageDataDesc != null)
                    messageDataDesc.moveRequestInfoToReply(messageRequest);
            }
        }
    }
    /**
     * Get the property names and classes that are part of the standard message payload.
     * @param mapPropertyNames
     * @return
     */
    public Map<String, Class<?>> getPayloadPropertyNames(Map<String,Class<?>> mapPropertyNames)
    {
        mapPropertyNames= super.getPayloadPropertyNames(mapPropertyNames);
        if (m_messageDataDescChildren != null)
        {
            for (String strKey : m_messageDataDescChildren.keySet())
            {
                MessageDataDesc messageDataDesc = m_messageDataDescChildren.get(strKey);
                mapPropertyNames = messageDataDesc.getPayloadPropertyNames(mapPropertyNames);
            }
        }
        return mapPropertyNames;
    }
}
