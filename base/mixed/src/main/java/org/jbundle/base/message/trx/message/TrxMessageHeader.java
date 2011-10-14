/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message;

/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MessageConstants;


/** 
 * A Record message is a message explaining the changes to this record.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class TrxMessageHeader extends BaseMessageHeader
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * The param to supply the destination (The URL part of the destination).
     */
    public static final String DESTINATION_PARAM = "destinationAddress";
    /**
     * The param to supply the destination type (the extension part of the URL).
     */
    public static final String DESTINATION_MESSAGE_PARAM = "destinationMessage";
    /**
     * The param to supply the source URL.
     */
    public static final String SOURCE_PARAM = "sourceAddress";
    /**
     * The param to supply the source URL.
     */
    public static final String SOURCE_MESSAGE_PARAM = "sourceMessage";
    /**
     * The reply to URL param.
     */
    public static final String REPLY_TO_PARAM = "replyTo";
    /**
     * The param to supply the WSDL File path or URL.
     */
    public static final String WSDL_PATH = "wsdlPath";
    /**
     * The reply to registry ID.
     */
    public static final String REGISTRY_ID = "registryID";
    /**
     * The filename or URL string to do the XSLT conversion.
     */
    public static final String XSLT_DOCUMENT = "XSLTDocument";
    /**
     * The code of this message(info).
     */
    public static final String MESSAGE_CODE = "messageCode";
    /**
     * The code of this message(info).
     */
    public static final String MESSAGE_MARSHALLER_CLASS = "marshallerClass";
    /**
     * The code of this message(info).
     */
    public static final String MESSAGE_RESPONSE_MARSHALLER_CLASS = "responseMarshallerClass";
    /**
     * The code of this message(info).
     */
    public static final String EXTERNAL_MESSAGE_CLASS = "externalMessageClassName";
    /**
     * The class of the processor that sends this message.
     */
    public static final String MESSAGE_PROCESSOR_CLASS = DBParams.PROCESS;  //"messageProcessor";
    /**
     * The ID of the message(info) response.
     */
    public static final String MESSAGE_RESPONSE_ID = "responseMessageID";
    /**
     * An alternate processor to use on return (typically for returned e-mails).
     */
    public static final String MESSAGE_RESPONSE_CODE = "responseMessageCode";
    /**
     * An alternate processor to use on return (typically for returned e-mails).
     */
    public static final String MESSAGE_RESPONSE_CLASS = "responseMessageProcessor";
    /**
     * The class or ID of the message(error) response.
     */
    public static final String MESSAGE_ERROR_PROCESSOR = "messageErrorProcessor";
    /**
     * The transaction ID of the message log.
     */
    public static final String LOG_TRX_ID = "trxID";
    /**
     * The transaction ID of the message log of the message I'm replying to.
     */
    public static final String ORIG_LOG_TRX_ID = "trxIDOrig";
    /**
     * The transaction ID of the message log of the message I'm replying to.
     */
    public static final String MESSAGE_VERSION = "messageVersion";
    /**
     * The transaction ID of the message log of the message I'm replying to.
     */
    public static final String MESSAGE_VERSION_ID = "messageVersionID";
    /**
     * The transaction ID of the message log of the message I'm replying to.
     */
    public static final String SCHEMA_LOCATION = "schemaLocation";
    /**
     * MessageInfoType.
     */
    public static final String MESSAGE_INFO_TYPE = "messageInfoType";
    /**
     * MessageRequestType.
     */
    public static final String MESSAGE_REQUEST_TYPE = "messageRequestType";
    /**
     * MessageProcessType.
     */
    public static final String MESSAGE_PROCESS_TYPE = "messageProcessType";
    /**
     * Message description.
     */
    public static final String DESCRIPTION = "description";
    /**
     * The contact type (vendor/profile/etc).
     */
    public static final String CONTACT_TYPE = DBParams.CONTACT_TYPE;
    /**
     * The contact id.
     */
    public static final String CONTACT_ID = DBParams.CONTACT_ID;
    /**
     * The contact id.
     */
    public static final String CONTACT_USER_ID = "contactUserID";
    /**
     * The contact id.
     */
    public static final String CONTACT_USER = "contactUser";
    /**
     * User.
     */
    public static final String USER_ID = DBParams.USER_ID;
    /**
    *
    */
    public static final String REFERENCE_ID = DBConstants.OBJECT_ID;
    /**
    *
    */
    public static final String REFERENCE_TYPE = DBParams.RECORD;
    /**
    *
    */
    public static final String REFERENCE_CLASS = "referenceClass";
    /**
    * The message processor for this message.
    */
    public static final String MESSAGE_PROCESS_INFO_ID = "messageProcessInfoID";
    /**
    * The error message for this message. If this is not null, this message is in error.
    */
    public static final String MESSAGE_ERROR = "errorMessage";
    /**
    * Timeout in seconds.
    */
    public static final String MESSAGE_TIMEOUT = "messageTimeoutSeconds";

    /**
     * The header properties for this message.
     * Such as destination, transport type, message name.
     */
    protected Map<String,Object> m_mapMessageHeader = null;
    /**
     * The message info properties for this message.
     * Such as Message name, processor.
     */
    protected Map<String,Object> m_mapMessageInfo = null;
    /**
     * The transport properties for this message.
     * Such as passwords, hosts, etc.
     */
    protected Map<String,Object> m_mapMessageTransport = null;

    /**
     * Creates new RecordMessage.
     */
    public TrxMessageHeader()
    {
        super();
    }
    /**
     * Constructor.
     * This constructor is for the generic transaction processor.
     * @param strQueueType Type of queue - remote or local.
     * @param source usually the object sending or listening for the message, to reduce echos.
     */
    public TrxMessageHeader(Object source, Map<String,Object> properties)
    {
        this();
        this.init(MessageConstants.TRX_SEND_QUEUE, MessageConstants.INTERNET_QUEUE, source, properties);
    }
    /**
     * Constructor.
     * @param strQueueName Name of the queue.
     * @param strQueueType Type of queue - remote or local.
     * @param source usually the object sending or listening for the message, to reduce echos.
     */
    public TrxMessageHeader(String strQueueName, Object source, Map<String,Object> properties)
    {
        this();
        this.init(strQueueName, MessageConstants.INTERNET_QUEUE, source, properties);
    }
    /**
     * Constructor.
     * @param strQueueName Name of the queue.
     * @param strQueueType Type of queue - remote or local.
     * @param source usually the object sending or listening for the message, to reduce echos.
     */
    public void init(String strQueueName, String strQueueType, Object source, Map<String,Object> properties)
    {
        m_mapMessageHeader = properties;
        m_mapMessageTransport = null;
        m_mapMessageInfo = null;
        super.init(strQueueName, MessageConstants.INTERNET_QUEUE, source, properties);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Return the state of this object as a properties object.
     * @param strKey The key to return.
     * @return The properties.
     */
    public Object get(String strKey)
    {
        Object objData = super.get(strKey);
        if (objData == null)
            if (m_mapMessageHeader != null)
                objData = m_mapMessageHeader.get(strKey);
        if (objData == null)
            if (m_mapMessageInfo != null)
                objData = m_mapMessageInfo.get(strKey);
        if (objData == null)
            if (m_mapMessageTransport != null)
                objData = m_mapMessageTransport.get(strKey);
        return objData;
    }
    /**
     * Return the state of this object as a properties object.
     * @param strKey The key to return.
     * @return The properties.
     */
    public void put(String strKey, Object objData)
    {
        if (m_mapMessageHeader == null)
            m_mapMessageHeader = new HashMap<String,Object>();
        objData = m_mapMessageHeader.put(strKey, objData);
    }
    /**
     * Return the state of this object as a properties object.
     * @param strKey The key to return.
     * @return The properties.
     */
    public void putAll(TrxMessageHeader trxHeaderToMerge)
    {
        if (m_mapMessageHeader == null)
            m_mapMessageHeader = new HashMap<String,Object>();
        if (trxHeaderToMerge.getMessageHeaderMap() != null)
            this.getMessageHeaderMap().putAll(trxHeaderToMerge.getMessageHeaderMap());
        if (m_mapMessageInfo == null)
            m_mapMessageInfo = new HashMap<String,Object>();
        if (trxHeaderToMerge.getMessageInfoMap() != null)
            this.getMessageInfoMap().putAll(trxHeaderToMerge.getMessageInfoMap());
        if (m_mapMessageTransport == null)
            m_mapMessageTransport = new HashMap<String,Object>();
        if (trxHeaderToMerge.getMessageTransportMap() != null)
            this.getMessageTransportMap().putAll(trxHeaderToMerge.getMessageTransportMap());
    }
    /**
     * Return the properties object.
     * @return The properties.
     */
    public Map<String,Object> getMessageHeaderMap()
    {
        return m_mapMessageHeader;
    }
    /**
     * Return the properties object.
     * @return The properties.
     */
    public void setMessageHeaderMap(Map<String,Object> map)
    {
        m_mapMessageHeader = map;
    }
    /**
     * Return the properties object.
     * @return The properties.
     */
    public Map<String,Object> getMessageInfoMap()
    {
        return m_mapMessageInfo;
    }
    /**
     * Return the properties object.
     * @return The properties.
     */
    public void setMessageInfoMap(Map<String,Object> map)
    {
        m_mapMessageInfo = map;
    }
    /**
     * Return the properties object.
     * @return The properties.
     */
    public Map<String,Object> getMessageTransportMap()
    {
        return m_mapMessageTransport;
    }
    /**
     * Return the properties object.
     * @return The properties.
     */
    public void setMessageTransportMap(Map<String,Object> map)
    {
        m_mapMessageTransport = map;
    }
    /**
     * Return the state of this object as a properties object.
     * @return The properties.
     */
    public Map<String,Object> getProperties()
    {
        Map<String,Object> properties = super.getProperties();
        if (m_mapMessageHeader != null)
            properties.putAll(m_mapMessageHeader);
        if (m_mapMessageInfo != null)
            properties.putAll(m_mapMessageInfo);
        if (m_mapMessageTransport != null)
            properties.putAll(m_mapMessageTransport);
        return properties;
    }
    /**
     * Given this message header, set up a reply header.
     * Note: Swap the source and destimation, set the message to the reply message,
     * move the registry ID here.
     * @return The new reply header.
     */
    public TrxMessageHeader createReplyHeader()
    {
        Map<String,Object> mapInHeader = this.getMessageHeaderMap();
        Map<String,Object> mapInInfo = this.getMessageInfoMap();
        Map<String,Object> mapReplyHeader = new HashMap<String,Object>();
        Map<String,Object> mapReplyInfo = new HashMap<String,Object>();
        this.moveMapInfo(mapReplyHeader, mapInHeader, TrxMessageHeader.DESTINATION_PARAM, TrxMessageHeader.SOURCE_PARAM);
        this.moveMapInfo(mapReplyHeader, mapInHeader, TrxMessageHeader.SOURCE_PARAM, TrxMessageHeader.DESTINATION_PARAM);

        this.moveMapInfo(mapReplyHeader, mapInHeader, TrxMessageHeader.ORIG_LOG_TRX_ID, TrxMessageHeader.LOG_TRX_ID);
        
        this.moveMapInfo(mapReplyHeader, mapInHeader, TrxMessageHeader.REGISTRY_ID, TrxMessageHeader.REGISTRY_ID);

        this.moveMapInfo(mapReplyInfo, mapInInfo, TrxMessageHeader.MESSAGE_CODE, TrxMessageHeader.MESSAGE_RESPONSE_ID);
        this.moveMapInfo(mapReplyInfo, mapInInfo, TrxMessageHeader.MESSAGE_CODE, TrxMessageHeader.MESSAGE_RESPONSE_CODE);
        this.moveMapInfo(mapReplyInfo, mapInHeader, TrxMessageHeader.MESSAGE_CODE, TrxMessageHeader.MESSAGE_RESPONSE_CODE);
        this.moveMapInfo(mapReplyInfo, mapInInfo, TrxMessageHeader.EXTERNAL_MESSAGE_CLASS, TrxMessageHeader.MESSAGE_RESPONSE_CLASS);
        this.moveMapInfo(mapReplyInfo, mapInInfo, TrxMessageHeader.MESSAGE_MARSHALLER_CLASS, TrxMessageHeader.MESSAGE_RESPONSE_MARSHALLER_CLASS);
        
        this.moveMapInfo(mapReplyInfo, mapInInfo, TrxMessageHeader.MESSAGE_VERSION, TrxMessageHeader.MESSAGE_VERSION);
        this.moveMapInfo(mapReplyInfo, mapInInfo, TrxMessageHeader.MESSAGE_VERSION_ID, TrxMessageHeader.MESSAGE_VERSION_ID);
        this.moveMapInfo(mapReplyInfo, mapInInfo, TrxMessageHeader.SCHEMA_LOCATION, TrxMessageHeader.SCHEMA_LOCATION);

        TrxMessageHeader trxMessageHeader = new TrxMessageHeader(null, mapReplyHeader);
        trxMessageHeader.setMessageInfoMap(mapReplyInfo);
        return trxMessageHeader;
    }
    /**
     * Move this param from the header map to the reply map (if non null).
     */
    public void moveMapInfo(Map<String,Object> mapReplyHeader, Map<String,Object> mapInHeader, String strReplyParam, String strInParam)
    {
        if (mapInHeader != null)
            if (mapInHeader.get(strInParam) != null)
                if (mapReplyHeader != null)
                    mapReplyHeader.put(strReplyParam, mapInHeader.get(strInParam));
    }
    /**
     * Output this object's state
     */
    public String toString()
    {
        String string = super.toString();
        if (m_mapMessageHeader != null)
            string = string + m_mapMessageHeader.toString();
        if (m_mapMessageInfo != null)
            string = string + m_mapMessageInfo.toString();
        if (m_mapMessageTransport != null)
            string = string + m_mapMessageTransport.toString();
        return string;
    }
    /**
     * Get the message header data as a XML String.
     * @return
     */
    public StringBuffer addXML(StringBuffer sbXML)
    {
        if (sbXML == null)
            sbXML = new StringBuffer();
        Util.addStartTag(sbXML, BaseMessage.HEADER_TAG).append(DBConstants.RETURN);
        Map<String,Object> map = this.getProperties();
        Util.addXMLMap(sbXML, map);
        Util.addEndTag(sbXML, BaseMessage.HEADER_TAG).append(DBConstants.RETURN);
        return sbXML;
    }
    /**
     * Add the task properties that I will need to start up a process somewhere else with this same environment.
     * @param task
     */
    public void addTaskProperties(Task task)
    {
    	if (task == null)
    		return;
        this.put(TrxMessageHeader.USER_ID, task.getProperty(DBParams.USER_ID));
//?         BaseDatabase.addDBProperties(m_mapMessageHeader, task, null);	// Note: m_mapMessageHeader is guaranteed not null by previous call.
    }
}
