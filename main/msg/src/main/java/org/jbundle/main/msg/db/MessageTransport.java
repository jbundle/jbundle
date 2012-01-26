/**
 * @(#)MessageTransport.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.message.trx.message.*;
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

    //public static final int kID = kID;
    public static final int kDescription = kVirtualRecordLastField + 1;
    public static final int kCode = kDescription + 1;
    public static final int kProperties = kCode + 1;
    public static final int kMessageTransportType = kProperties + 1;
    public static final int kMessageTransportLastField = kMessageTransportType;
    public static final int kMessageTransportFields = kMessageTransportType - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kDescriptionKey = kIDKey + 1;
    public static final int kCodeKey = kDescriptionKey + 1;
    public static final int kMessageTransportLastKey = kCodeKey;
    public static final int kMessageTransportKeys = kCodeKey - DBConstants.MAIN_KEY_FIELD + 1;
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

    public static final String kMessageTransportFile = "MessageTransport";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kMessageTransportFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 30, null, null);
        if (iFieldSeq == kCode)
            field = new StringField(this, "Code", 10, null, null);
        if (iFieldSeq == kProperties)
            field = new PasswordPropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageTransportType)
            field = new MessageTransportTypeField(this, "MessageTransportType", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageTransportLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == kIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kDescriptionKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Description");
            keyArea.addKeyField(kDescription, DBConstants.ASCENDING);
        }
        if (iKeyArea == kCodeKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Code");
            keyArea.addKeyField(kCode, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kMessageTransportLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kMessageTransportLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
                
        ((PasswordPropertiesField)this.getField(kProperties)).addPasswordProperty(org.jbundle.base.message.trx.transport.email.MessageReceivingPopClientProcess.POP3_PASSWORD);
        ((PasswordPropertiesField)this.getField(kProperties)).addPasswordProperty(org.jbundle.base.message.trx.transport.email.EmailMessageTransport.SMTP_PASSWORD);
    }
    /**
     * Add the properties to this message (transportinfo).
     * NOTE: The properties are added ONLY if they don't currently exist in the transport map.
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader)
    {
        Map<String,Object> mapHeaderMessageTransport = trxMessageHeader.getMessageTransportMap();
        
        Map<String,Object> propMessageTransport = ((PropertiesField)this.getField(MessageTransport.kProperties)).loadProperties();
        String strSendBy = this.getField(MessageTransport.kCode).toString();
        if ((strSendBy != null) && (strSendBy.length() > 0))
            propMessageTransport.put(MessageTransport.SEND_MESSAGE_BY_PARAM, strSendBy);
        String strTransType = this.getField(MessageTransport.kMessageTransportType).toString();
        if ((strTransType != null) && (strTransType.length() > 0))
            propMessageTransport.put(MessageTransport.TRANSPORT_TYPE_PARAM, strTransType);
        propMessageTransport.put(MessageTransport.TRANSPORT_ID_PARAM, this.getField(MessageTransport.kID).toString());
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
            this.setKeyArea(MessageTransport.kIDKey);
            this.getField(MessageTransport.kID).setString(strMessageTransport);   
        }
        else
        {
            this.setKeyArea(MessageTransport.kCodeKey);
            this.getField(MessageTransport.kCode).setString(strMessageTransport);
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
        return MessageTransport.isDirectTransport(this.getField(MessageTransport.kCode).toString());
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
            className = ((PropertiesField)messageTransport.getField(MessageTransport.kProperties)).getProperty("className");
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
