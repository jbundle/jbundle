/**
 * @(#)MessageTransport.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.trx.transport.*;
import org.jbundle.util.osgi.finder.*;
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageTransport - Message transports.
 */
public class MessageTransport extends VirtualRecord
     implements MessageTransportModel
{
    private static final long serialVersionUID = 1L;

    public static final String INITIAL_MESSAGE_DATA_STATUS = MessageDataDesc.DATA_STATUS;
    /**
     * Default constructor.
     */
    public MessageTransport()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageTransport(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(MESSAGE_TRANSPORT_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.LOCAL | DBConstants.USER_DATA;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
            field = new StringField(this, DESCRIPTION, 30, null, null);
        if (iFieldSeq == 4)
            field = new StringField(this, CODE, 10, null, null);
        if (iFieldSeq == 5)
            field = new PasswordPropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 6)
            field = new MessageTransportTypeField(this, MESSAGE_TRANSPORT_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, ID_KEY);
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, DESCRIPTION_KEY);
            keyArea.addKeyField(DESCRIPTION, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, CODE_KEY);
            keyArea.addKeyField(CODE, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
                
        ((PasswordPropertiesField)this.getField(MessageTransport.PROPERTIES)).addPasswordProperty(org.jbundle.base.message.trx.transport.email.MessageReceivingPopClientProcess.POP3_PASSWORD);
        ((PasswordPropertiesField)this.getField(MessageTransport.PROPERTIES)).addPasswordProperty(org.jbundle.base.message.trx.transport.email.EmailMessageTransport.SMTP_PASSWORD);
    }
    /**
     * Add the properties to this message (transportinfo).
     * NOTE: The properties are added ONLY if they don't currently exist in the transport map.
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader)
    {
        Map<String,Object> mapHeaderMessageTransport = trxMessageHeader.getMessageTransportMap();
        
        Map<String,Object> propMessageTransport = ((PropertiesField)this.getField(MessageTransport.PROPERTIES)).loadProperties();
        String strSendBy = this.getField(MessageTransport.CODE).toString();
        if ((strSendBy != null) && (strSendBy.length() > 0))
            propMessageTransport.put(MessageTransport.SEND_MESSAGE_BY_PARAM, strSendBy);
        String strTransType = this.getField(MessageTransport.MESSAGE_TRANSPORT_TYPE).toString();
        if ((strTransType != null) && (strTransType.length() > 0))
            propMessageTransport.put(MessageTransport.TRANSPORT_TYPE_PARAM, strTransType);
        propMessageTransport.put(MessageTransport.TRANSPORT_ID_PARAM, this.getField(MessageTransport.ID).toString());
        if (mapHeaderMessageTransport != null)
            Utility.putAllIfNew(mapHeaderMessageTransport, propMessageTransport);
        else
            mapHeaderMessageTransport = propMessageTransport;
        trxMessageHeader.setMessageTransportMap(mapHeaderMessageTransport);
        
        return trxMessageHeader;
    }
    /**
     * GetMessageTransport Method.
     */
    public MessageTransport getMessageTransport(String strMessageTransport)
    {
        if ((strMessageTransport == null) || (strMessageTransport.length() == 0))
            return null;
        if (Utility.isNumeric(strMessageTransport))
        {
            this.setKeyArea(MessageTransport.ID_KEY);
            this.getField(MessageTransport.ID).setString(strMessageTransport);  
        }
        else
        {
            this.setKeyArea(MessageTransport.CODE_KEY);
            this.getField(MessageTransport.CODE).setString(strMessageTransport);
        }
        try {
            if (this.seek(null))
                return this;
        } catch (DBException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Is this transport a direct type?.
     */
    public boolean isDirectTransport()
    {
        return MessageTransport.isDirectTransport(this.getField(MessageTransport.CODE).toString());
    }
    /**
     * Is this transport code a direct type?.
     */
    public static boolean isDirectTransport(String strTransportCode)
    {
        if ((MessageTransport.DIRECT.equalsIgnoreCase(strTransportCode))
            || (MessageTransport.SERVER.equalsIgnoreCase(strTransportCode))
            || (MessageTransport.CLIENT.equalsIgnoreCase(strTransportCode)))
                return true;    // These are the direct types
        return false;    // The others are not direct
    }
    /**
     * Get the message transport for this type
     * @param messageTransportType
     * @returns The concrete BaseMessageTransport implementation.
     */
    public Object createMessageTransport(String messageTransportType, Task task)
    {
        MessageTransport messageTransport = this.getMessageTransport(messageTransportType);
        String className = null;
        if (messageTransport != null)
            className = ((PropertiesField)messageTransport.getField(MessageTransport.PROPERTIES)).getProperty("className");
        if (className == null)
        {
            String packageName = BaseMessageTransport.class.getPackage().getName();
            className = packageName + '.' + messageTransportType.toLowerCase() + '.' + messageTransportType + "MessageTransport";
        }
        BaseMessageTransport transport = (BaseMessageTransport)ClassServiceUtility.getClassService().makeObjectFromClassName(className);
        if (transport != null)
            transport.init(task, null, null);
        return transport;

    }

}
