/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
package org.jbundle.thin.base.message;

import java.io.Serializable;
import java.util.Iterator;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/** 
 * A BaseMessage is the core message class.
 * As a default, it contains a Header and Properties.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public abstract class BaseMessage extends Object
    implements Serializable, MessageDataParent, Cloneable
{
    private static final long serialVersionUID = 1L;

    /**
     * The root XML tag for standardized internal -> XML conversions.
     */
    public static final String ROOT_TAG = "root";
    /**
     * The root XML tag for standardized internal -> XML conversions.
     */
    public static final String HEADER_TAG = "header";

    /**
     * The message header which contains information such as destination and type.
     */
    protected BaseMessageHeader m_messageHeader = null;
    /**
     * The raw data (Typically a Map<String,Object>).
     */
    protected Object m_data = null;
    /**
     * If true, this message has been handled and should be ignored.
     */
    protected transient boolean m_bIsConsumed = false;
    /**
     * If true, this message has been processed by the client and should not be echoed back up.
     */
    protected boolean m_bIsProcessedByClient = false;
    /**
     * Don't send this message to this client as it has already been handled there.
     */
    protected transient Object m_objProcessedByClientSession = null;
    /**
     * If true, this message has been processed by the server and should not be sent back down.
     */
    protected transient boolean m_bIsProcessedByServer = false;
    /**
     * The data dictionary for this message.
     */
    protected transient MessageDataDesc m_messageDataDesc = null;
    /**
     * The (optional) External version of this message.
     */
    protected transient ExternalMessage m_externalMessage = null;

    /**
     * Creates new BaseMessage.
     */
    public BaseMessage()
    {
        super();
    }
    /**
     * Constructor.
     * @param messageHeader The message header which contains information such as destination and type.
     * @param data This properties object is a default way to pass messages.
     */
    public BaseMessage(BaseMessageHeader messageHeader, Object data)
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
        m_messageDataDesc = null;
        m_externalMessage = null;
        
        m_messageHeader = messageHeader;
        this.setData(data);
        m_bIsConsumed = false;
    }
    /**
     * Free this object.
     */
    public void free()
    {
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseMessage message = (BaseMessage)super.clone();   // Note: The data is not deep cloned, since BaseMessageReceiver is the only place clone is called.
        BaseMessageHeader messageHeader = message.getMessageHeader();
        if (messageHeader != null)
            message.setMessageHeader((BaseMessageHeader)messageHeader.clone()); // This has to be a new object
        return message;
    }
    /**
     * Set the raw data object.
     * @param data
     */
    public void setData(Object data)
    {
        m_data = data;
    }
    /**
     * Get the raw data object for this message.
     * @return
     */
    public Object getData()
    {
        return m_data;
    }
    /**
     * Get a Iterator of the keys in this message.
     * @return An iterator of the keys in this message.
     */
    public abstract Iterator<String> keySet();
    /**
    * Get the native class type of this class of data.
    */
    public abstract Class<?> getNativeClassType(Class<?> rawClassType);
    /**
     * Get the raw data for this (xpath) key.
     * @param strParam  The xpath key.
     * @return The raw data at this location in the message.
     */
    protected abstract Object getNative(String strParam);
    /**
     * Put the raw value for this param in the (xpath) map.
     * @param strParam The xpath key.
     * @param objValue The raw data to set this location in the message.
     */
    public abstract void putNative(String strParam, Object objValue);
    /**
     * Get the number of data nodes at this path in the data.
     * @param strKey The xpath key to the data.
     * @return The number of data nodes at this location.
     */
    public int getNodeCount(String strKey)
    {
        return 1;   // Override this
    }
    /**
     * Do any of the conversion that has to be done for all message types.
     * Typically overidden to return correctly converted data.
     * @param trxMessage The raw internal trx message.
     * @return The new Internal message with additional/refined data.
     */
    public Object get(String strKey)
    {
        if (this.getMessageFieldDesc(strKey) != null)
            return this.getMessageFieldDesc(strKey).get();
        return this.getNative(strKey);
    }
    /**
     * Convert this external data format to the raw object and put it in the map.
     * Typically overidden to return correctly converted data.
     */
    public void put(String strKey, Object strValue)
    {
        if (this.getMessageFieldDesc(strKey) != null)
            this.getMessageFieldDesc(strKey).put(strValue);
        else
            this.putNative(strKey, strValue);
    }
    /**
     * Do any of the conversion that has to be done for all message types.
     * Typically overidden to return correctly converted data.
     * @param trxMessage The raw internal trx message.
     * @return The new Internal message with additional/refined data.
     */
    public String getString(String strKey)
    {
        if (this.getMessageFieldDesc(strKey) != null)
            return this.getMessageFieldDesc(strKey).getString();
        Object data = this.getNative(strKey);
        if (data != null)
            return data.toString();
        return null;
    }
    /**
     * Convert this external data format to the raw object and put it in the map.
     * Typically overidden to return correctly converted data.
     */
    public void putString(String strKey, String strValue)
    {
        if (this.getMessageFieldDesc(strKey) != null)
            this.getMessageFieldDesc(strKey).putString(strValue);
        else
            this.putNative(strKey, strValue);
    }
    /**
     * Create this new (empty) node in the message.
     * @param strKey The location of the node to create.
     */
    public Object createNewNode(String strKey)
    {
        return null;    // Override this!
    }
    /**
     * Set the message data as a XML String.
     * @return
     */
    public boolean setXML(String strXML)
    {
        Document doc = Util.convertXMLToDOM(strXML);
        return this.setDOM(doc);
    }
    /**
     * Set the message data as a DOM Hierarchy.
     * @return True if successful.
     */
    public boolean setDOM(Node nodeXML)
    {
        return false;    // Success
    }
    /**
     * Get the message data as a XML String.
     * @return The XML String.
     */
    public String getXML(boolean bIncludeHeader)
    {
        return null;    // Override this
    }
    /**
     * Set the message data as a DOM Hierarchy.
     * @return True if successful.
     */
    public Node getDOM()
    {
        return null;    // Override this
    }
    /**
     * Get the message header.
     * @return The message header.
     */
    public BaseMessageHeader getMessageHeader()
    {
        return m_messageHeader;   // Override this
    }
    /**
     * Get the message header.
     * @return The message header.
     */
    public void setMessageHeader(BaseMessageHeader messageHeader)
    {
        m_messageHeader = messageHeader;   // Override this
    }
    /**
     * Has this message been consumed?
     * @return true if it has already been handled.
     */
    public boolean isConsumed()
    {
        return m_bIsConsumed;
    }
    /**
     * Consume this message so no other listeners get it.
     */
    public void consume()
    {
        m_bIsConsumed = true;
    }
    /**
     * Consume this message so no other listeners get it.
     */
    public void setConsumed(boolean bIsConsumed)
    {
        m_bIsConsumed = bIsConsumed;
    }
    /**
     * Was this message already distributed in the client session?
     * @return If true, the message was processed by the client.
     */
    public boolean isProcessedByClient()
    {
        return m_bIsProcessedByClient;
    }
    /**
     * Was this message already distributed in the client session?
     * @return If true, the message was processed by the client.
     */
    public void setProcessedByClient(boolean bIsProcessedByClient)
    {
        m_bIsProcessedByClient = bIsProcessedByClient;
    }
    /**
     * Was this message already distributed in the client session?
     * @return If true, the message was processed by the client.
     */
    public boolean isProcessedByServer()
    {
        return m_bIsProcessedByServer;
    }
    /**
     * Was this message already distributed in the client session?
     * @return If true, the message was processed by the client.
     */
    public void setProcessedByServer(boolean bIsProcessedByServer)
    {
        m_bIsProcessedByServer = bIsProcessedByServer;
    }
    /**
     * Set the client session that has already handled this message, so I don't have an echo.
     * @param objProcessedByClientSession The client task session that has already handled this message.
     */
    public void setProcessedByClientSession(Object objProcessedByClientSession)
    {
        m_objProcessedByClientSession = objProcessedByClientSession;
    }
    /**
     * Set the client session that has already handled this message, so I don't have an echo.
     * @param objProcessedByClientSession The client task session that has already handled this message.
     */
    public Object getProcessedByClientSession()
    {
        return m_objProcessedByClientSession;
    }
    /**
     * Get the parent message for this data.
     * @return The message.
     */
    public BaseMessage getMessage()
    {
        return this;
    }
    /**
     * 
     */
    public BaseMessage convertToThinMessage()
    {
        return this;
    }
    /**
     * Get the message description for this (xpath) key.
     * @return The message data desc.
     */
    public MessageDataDesc getMessageDataDesc(String strKey)
    {
        if (m_messageDataDesc == null)
            return null;
        return m_messageDataDesc.getMessageDataDesc(strKey);
    }
    /**
     * Get the message description for this (xpath) key.
     * @return The message data desc.
     */
    public MessageFieldDesc getMessageFieldDesc(String strKey)
    {
        if (m_messageDataDesc == null)
            return null;
        return m_messageDataDesc.getMessageFieldDesc(strKey);
    }
    /**
     * Add a child message data desc.
     */
    public void addMessageDataDesc(MessageDataDesc messageDataDesc)
    {
        if (messageDataDesc instanceof MessageFieldDesc)
        {
            if (m_messageDataDesc == null)
                m_messageDataDesc = new MessageRecordDesc(this, Constants.BLANK);
            m_messageDataDesc.addMessageDataDesc(messageDataDesc);
        }
        else
        {
            m_messageDataDesc = messageDataDesc;
            if (m_messageDataDesc != null)
                m_messageDataDesc.setMessageDataParent(this);
        }
    }
    /**
     * Get the external version of this message.
     * @return The external version of this message.
     */
    public ExternalMessage getExternalMessage()
    {
        return m_externalMessage;
    }
    /**
     * Get the external version of this message.
     * @param Set the external version of this message.
     */
    public void setExternalMessage(ExternalMessage externalMessage)
    {
        m_externalMessage = externalMessage;
    }
    /**
     * Convert the external form to the internal message form.
     * NOTE: Be VERY careful as the trxMessageHeader is used to get the message class
     * AND also set to the message header for the new message.
     * @param externalMessage The received message to be converted to internal form.
     * @return The internal message.
     */
    public static BaseMessage createMessage(BaseMessageHeader messageHeader)
    {
        return BaseMessage.createMessage(null, messageHeader, null);
    }
    /**
     * Setup this message given this internal data structure.
     * @param data The data in an intermediate format.
     */
    public static BaseMessage createMessage(String strMessageClassName, BaseMessageHeader messageHeader, Object data)
    {
       BaseMessage message = null;
       
       if ((strMessageClassName == null)
           && (messageHeader != null))
       {
           strMessageClassName = (String)messageHeader.get(BaseMessageHeader.INTERNAL_MESSAGE_CLASS);
           if ((strMessageClassName == null) || (strMessageClassName.length() == 0))
           {
//?               String strRequestType = (String)messageHeader.get(TrxMessageHeader.MESSAGE_REQUEST_TYPE);
//?               if ((strRequestType == null) || (strRequestType.length() == 0) || (RequestType.MANUAL.equalsIgnoreCase(strRequestType)))
//?                   strMessageClass = ManualMessage.class.getName();
           }
       }
       if ((strMessageClassName != null) && (strMessageClassName.length() > 0))
       {
           strMessageClassName = Util.getFullClassName(strMessageClassName);
           Object obj = (BaseMessage)Util.makeObjectFromClassName(strMessageClassName);
           if (obj instanceof BaseMessage)
           {
               message = (BaseMessage)obj;
               message.init(messageHeader, null);
           }
           else if  (obj instanceof MessageRecordDesc)
           {
               message = new TreeMessage(messageHeader, null);
               ((MessageRecordDesc)obj).init(message, null);
           }
       }
       return message;
   }
    /**
     * Setup this message given this internal data structure.
     * @param data The data in an intermediate format.
     */
     public void createMessageDataDesc(String strMessageDataClassName)
     {
         if (strMessageDataClassName == null)
         {
             if (this.getMessageHeader() != null)
                 strMessageDataClassName = (String)this.getMessageHeader().get(BaseMessageHeader.INTERNAL_MESSAGE_CLASS);
         }
         MessageRecordDesc messageRecordDesc = (MessageRecordDesc)Util.makeObjectFromClassName(strMessageDataClassName);
         if (messageRecordDesc != null)
                 messageRecordDesc.init(this, null);
     }
     public void createMessageDataDesc()
     {
         this.createMessageDataDesc(null);
     }
   /**
    * Output this object's state
    */
   public String toString()
   {
       return this.getXML(true);
   }
}
