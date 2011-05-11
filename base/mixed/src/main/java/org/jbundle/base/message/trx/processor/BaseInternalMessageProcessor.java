package org.jbundle.base.message.trx.processor;

import java.util.Map;
import java.util.Properties;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.base.message.trx.transport.soap.SOAPMessageTransport;
import org.jbundle.base.util.Utility;
import org.jbundle.main.msg.db.MessageTransport;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.util.osgi.finder.ClassServiceImpl;


/**
 * This is the base class to process an external message.
 * This class handles two distinct but related functions:
 * <br>- It converts a message to an external format and sends it, returning a reply.
 * <br>- It converts an incoming external message and processes it, typically sending a reply.
 */
public abstract class BaseInternalMessageProcessor extends BaseMessageProcessor
{
    /**
     * Default constructor.
     */
    public BaseInternalMessageProcessor()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public BaseInternalMessageProcessor(RecordOwnerParent taskParent, Record recordMain, Properties properties)
    {
        this();
        this.init(taskParent, recordMain, properties);    // The one and only
    }
    /**
     * Initializes the MessageProcessor.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free all the resources belonging to this class.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the message transport.
     * For example, jaxm.
     * <br/>The message transport should have the classname:
     * com.xxx.base.message.trx.transport.<type(lowercase)>.<type>MessageTransport.
     * return The message transport.
     */
    public BaseMessageTransport getMessageTransport(BaseMessage internalTrxMessage)
    {
        TrxMessageHeader messageHeader = (TrxMessageHeader)internalTrxMessage.getMessageHeader();
        BaseMessageTransport transport = null;
        Map<String,Object> propMessageTransport = null;
        String strMessageType = (String)messageHeader.get(MessageTransport.SEND_MESSAGE_BY_PARAM);
        if (strMessageType != null)
        {
            String strClassName = null;
            Record recMessageTransport = this.getRecord(MessageTransport.kMessageTransportFile);
            if (recMessageTransport == null)
                recMessageTransport = new MessageTransport(this);
            recMessageTransport.setKeyArea(MessageTransport.kCodeKey);
            recMessageTransport.getField(MessageTransport.kCode).setString(strMessageType);
            try {
                if (recMessageTransport.seek(null))
                {
                    PropertiesField fldProperty = (PropertiesField)recMessageTransport.getField(MessageTransport.kProperties);
                    strClassName = fldProperty.getProperty(MessageTransport.TRANSPORT_CLASS_NAME_PARAM);
                    Map<String,Object> mapToMerge = messageHeader.getMessageTransportMap();
                    propMessageTransport = fldProperty.loadProperties();
                    if (mapToMerge != null)
                        propMessageTransport.putAll(mapToMerge);
                    messageHeader.setMessageTransportMap(propMessageTransport);
                }
            } catch (DBException ex) {
                ex.printStackTrace();
            }
            
            String strBasePackage = SOAPMessageTransport.class.getName();
            strBasePackage = strBasePackage.substring(0, strBasePackage.lastIndexOf('.'));    // Get rid of class name
            strBasePackage = strBasePackage.substring(0, strBasePackage.lastIndexOf('.'));    // Get rid of last package name
            if ((strClassName == null) || (strClassName.length() == 0))
            {
                strClassName = strBasePackage + '.' + strMessageType.toLowerCase();
                strClassName = strClassName + '.' + strMessageType + "MessageTransport";
            }
            transport = (BaseMessageTransport)ClassServiceImpl.getClassService().makeObjectFromClassName(strClassName);
        }
        if (transport == null)
        {
            Utility.getLogger().warning("No transport specified on message");
            transport = new SOAPMessageTransport();         // Default = SOAP
        }
        transport.init(this.getTask(), null, propMessageTransport);
        return transport;
    }
}
