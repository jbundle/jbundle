/*
 * HotelRateRequestOut.java
 *
 * Created on September 26, 2003, 12:41 AM
 */

package org.jbundle.thin.base.message;

import java.io.Serializable;
import java.util.Map;

import org.jbundle.model.PropertyOwner;
import org.jbundle.thin.base.db.Constant;
import org.jbundle.thin.base.db.FieldList;


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
public class MessageDataDesc extends Object
    implements MessageDataParent, Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_DATA_CLASS_NAME = "messageDataClassName";
    public static final String DATA_STATUS = "dataStatus";

    protected MessageDataParent m_messageDataParent = null;
    
    protected String m_strKey = null;
    
    public static final int VALID = 5;
    public static final int ERROR = 8;
    public static final int DATA_REQUIRED = 9;
    public static final int NOT_VALID = 12;
    public static final int DATA_VALID = 13;

    /**
     * Creates a new instance of MessageDataDesc
     */
    public MessageDataDesc()
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public MessageDataDesc(MessageDataParent messageDataParent, String strKey)
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
        m_messageDataParent = messageDataParent;
        m_strKey = strKey;
        if (messageDataParent != null)
            messageDataParent.addMessageDataDesc(this);
    }
    /**
     * Get this item's key.
     */
    public String getKey()
    {
        return m_strKey;
    }
    /**
     * Get this item's key.
     */
    public String getFullKey(String strSubKey)
    {
        String strReturnKey = strSubKey;
        if (strReturnKey == null)
            strReturnKey = Constant.BLANK;
        if (this.getMessageDataParent() instanceof MessageDataDesc)
        {    // Don't add for top level or above
            if (strReturnKey.length() > 0)
                strReturnKey = '/' + strReturnKey;
            strReturnKey = this.getKey() + strReturnKey;
            strReturnKey = ((MessageDataDesc)this.getMessageDataParent()).getFullKey(strReturnKey);
        }
        return strReturnKey;
    }
    /**
     * Get this item's key.
     */
    public void setKey(String strKey)
    {
        m_strKey = strKey;
    }
    /**
     * 
     * @return
     */
    public MessageDataParent getMessageDataParent()
    {
        return m_messageDataParent;
    }
    /**
     * 
     * @return
     */
    public void setMessageDataParent(MessageDataParent messageDataParent)
    {
        m_messageDataParent = messageDataParent;
    }
    /**
     * 
     * @return
     */
    public BaseMessage getMessage()
    {
        if (m_messageDataParent == null)
            return null;
        return m_messageDataParent.getMessage();
    }
    /**
     * Get the message description for this (xpath) key.
     * @return The message data desc.
     */
    public MessageDataDesc getMessageDataDesc(String strKey)
    {
        return this;    // Override this!
    }
    /**
     * Get the message description for this (xpath) key.
     * @return The message data desc.
     */
    public MessageFieldDesc getMessageFieldDesc(String strKey)
    {
        return null;    // Override this!
    }
    /**
     * Add a child message data desc.
     */
    public void addMessageDataDesc(MessageDataDesc messageDataDesc)
    {
        // Override this in MessageRecordDesc!
    }
    /**
     * Move the correct fields from ALL the detail records to the map.
     * Typically, you override this and loop through the records in the table.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     * @return Error code
     */
    public int handlePutRawRecordData(FieldList record)
    {
        return this.putRawRecordData(record);
    }
    /**
     * Move the correct fields from THIS record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     * @return Error code
     */
    public int putRawRecordData(FieldList record)
    {
    	return Constant.NORMAL_RETURN;		// Override this
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int handleGetRawRecordData(FieldList record)
    {
        return this.getRawRecordData(record);
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int getRawRecordData(FieldList record)
    {
        int iMessageStatus = Constant.NORMAL_RETURN;        // Override this
        return iMessageStatus;
    }
    /**
     * Move the data from this properyowner to this message.
     */
    public void putRawProperties(PropertyOwner propertyOwner)
    {
        // Override this
    }
    /**
     * Initialize the fields in this record to prepare for this message.
     * Also, do any other preparation needed before sending this message.
     * @param record The record to initialize
     * @return An error code if there were any problems.
     */
    public int initForMessage(FieldList record)
    {
        int iErrorCode = Constant.NORMAL_RETURN;   // Override this
        return iErrorCode;
    }
    /**
     * Check to make sure all the data is present to attempt a cost lookup.
     * Note: You are NOT returning the status, you are returning the status of the params,
     * The calling program will change the status if required.
     * @return DATA_REQUIRED if all the data is not present, DATA_VALID if the data is OKAY.
     */
    public int checkRequestParams(FieldList record)
    {
        int iMessageStatus = DATA_VALID;    // Override this
        return iMessageStatus;
    }
    /**
     * Get a unique key that describes the data in this record.
     * You can use this key to see if any of the data has changed since the message was last sent.
     */
    public StringBuffer getMessageKey(StringBuffer sb)
    {
        return sb;    // Override this
    }
    /**
     * Move the pertinenent information from the request to this reply message.
     * Override this to be more specific.
     * Add some code like: this.put(messageRequest.get());
     */
    public void moveRequestInfoToReply(BaseMessage messageRequest)
    {
        // Override this
    }
    /**
     * Get the property names and classes that are part of the standard message payload.
     * @param mapPropertyNames
     * @return TODO
     * @return
     */
    public Map<String, Class<?>> getPayloadPropertyNames(Map<String,Class<?>> mapPropertyNames)
    {
        return mapPropertyNames;    // Override this
    }
    /**
     * Returns a string representation of the object.
     * @return  a string representation of the object.
     */
    public String toString()
    {
        return m_strKey;
    }
}
