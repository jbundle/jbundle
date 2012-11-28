/**
 * @(#)MessageProcessInfo.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.trx.processor.*;
import org.jbundle.base.message.trx.transport.local.*;
import java.net.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.base.message.core.trx.internal.*;
import org.jbundle.model.message.*;
import org.jbundle.main.db.base.*;
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageProcessInfo - Message process information.
 */
public class MessageProcessInfo extends VirtualRecord
     implements MessageProcessInfoModel
{
    private static final long serialVersionUID = 1L;

    public static final int TRANSPORT_DETAIL_MODE = (ScreenConstants.LAST_MODE * 2) | ScreenConstants.DETAIL_MODE;
    protected MessageDetail m_recMessageDetail = null;
    protected MessageTransport m_recMessageTransport = null;
    protected MessageTransportInfo m_recMessageTransportInfo = null;
    protected MessageControl m_recMessageControl = null;
    /**
     * Default constructor.
     */
    public MessageProcessInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageProcessInfo(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        m_recMessageDetail = null;
        m_recMessageTransport = null;
        m_recMessageTransportInfo = null;
        m_recMessageControl = null;
        super.init(screen);
    }
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(MESSAGE_PROCESS_INFO_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.REMOTE | DBConstants.USER_DATA;
    }
    /**
     * MakeScreen Method.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & TRANSPORT_DETAIL_MODE) == TRANSPORT_DETAIL_MODE)
            screen = Record.makeNewScreen(MessageTransportInfoModel.MESSAGE_TRANSPORT_INFO_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(MESSAGE_PROCESS_INFO_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = Record.makeNewScreen(MESSAGE_PROCESS_INFO_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
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
            field = new StringField(this, CODE, 30, null, null);
        if (iFieldSeq == 4)
            field = new StringField(this, DESCRIPTION, 50, null, null);
        if (iFieldSeq == 5)
            field = new QueueNameField(this, QUEUE_NAME_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 6)
            field = new MessageProcessInfoField(this, REPLY_MESSAGE_PROCESS_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new MessageProcessInfoField(this, LOCAL_MESSAGE_PROCESS_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new MessageInfoField(this, MESSAGE_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 9)
            field = new MessageTypeField(this, MESSAGE_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new ProcessTypeField(this, PROCESS_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 11)
            field = new StringField(this, PROCESSOR_CLASS, 127, null, null);
        if (iFieldSeq == 12)
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 13)
            field = new MessageTransportSelect(this, DEFAULT_MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 14)
            field = new BaseStatusSelect(this, INITIAL_MESSAGE_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, MESSAGE_INFO_ID_KEY);
            keyArea.addKeyField(MESSAGE_INFO_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(MESSAGE_TYPE_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(PROCESS_TYPE_ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, DESCRIPTION_KEY);
            keyArea.addKeyField(DESCRIPTION, DBConstants.ASCENDING);
        }
        if (iKeyArea == 3)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, CODE_KEY);
            keyArea.addKeyField(CODE, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * Free Method.
     */
    public void free()
    {
        m_recMessageDetail = null;
        m_recMessageTransport = null;
        m_recMessageTransportInfo = null;
        m_recMessageControl = null;
        super.free();
    }
    /**
     * Convert the command to the screen document type.
     * @param strCommand The command text.
     * @param The standard document type (MAINT/DISPLAY/SELECT/MENU/etc).
     */
    public int commandToDocType(String strCommand)
    {
        if (TRANSPORT_DETAIL_SCREEN.equalsIgnoreCase(strCommand))
            return TRANSPORT_DETAIL_MODE;
        return super.commandToDocType(strCommand);
    }
    /**
     * Get the (internal) Queue name for this message process.
     */
    public String getQueueName(boolean bDefaultIfNone)
    {
        String strName = null;
        Record recQueueName = ((ReferenceField)this.getField(MessageProcessInfo.QUEUE_NAME_ID)).getReference();
        if ((recQueueName != null) && (recQueueName.getEditMode() == DBConstants.EDIT_CURRENT))
            strName = recQueueName.getField(QueueName.CODE).toString();
        if ((strName == null) || (strName.length() == 0))
            if (bDefaultIfNone)
        {
            int iMessageInfoType = MessageInfoType.REQUEST_ID;
            Record recMessageInfo = ((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference();
            if ((recMessageInfo != null) && (recMessageInfo.getEditMode() == DBConstants.EDIT_CURRENT))
                iMessageInfoType = (int)recMessageInfo.getField(MessageInfo.MESSAGE_INFO_TYPE_ID).getValue();
            if (iMessageInfoType == MessageInfoType.REQUEST_ID)
                strName = MessageConstants.TRX_SEND_QUEUE;
            else
                strName = MessageConstants.TRX_RECEIVE_QUEUE;
        }
        return strName;
    }
    /**
     * Get the queue type for this message process.
     */
    public String getQueueType(boolean bDefaultIfNone)
    {
        String strQueueType = null;
        Record recQueueName = ((ReferenceField)this.getField(MessageProcessInfo.QUEUE_NAME_ID)).getReference();
        if (recQueueName != null)
            if (recQueueName.getEditMode() == DBConstants.EDIT_CURRENT)
                strQueueType = recQueueName.getField(QueueName.QUEUE_TYPE).toString();
        if ((strQueueType == null) || (strQueueType.length() == 0))
            if (bDefaultIfNone)
                strQueueType = MessageConstants.DEFAULT_QUEUE;
        return strQueueType;
    }
    /**
     * GetMessageProcessInfo Method.
     */
    public MessageProcessInfoModel getMessageProcessInfo(String strMessageKey)
    {
        int iOldKeyArea = this.getDefaultOrder();
        try {
            if (Utility.isNumeric(strMessageKey))
            {
                this.getField(MessageProcessInfo.ID).setString(strMessageKey);
                this.setKeyArea(MessageProcessInfo.ID_KEY);
                if (this.seek(null))
                    return this;
            }
            this.getField(MessageProcessInfo.CODE).setString(strMessageKey);
            this.setKeyArea(MessageProcessInfo.CODE_KEY);
            if (this.seek(null))
                return this;
            MessageInfo recMessageInfo = (MessageInfo)((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReferenceRecord();
            recMessageInfo.addNew();
            recMessageInfo.getField(MessageInfo.CODE).setString(strMessageKey);
            recMessageInfo.setKeyArea(MessageInfo.CODE_KEY);
            if (recMessageInfo.seek(null))
            {
                if (MessageInfoType.REQUEST.equalsIgnoreCase(((ReferenceField)recMessageInfo.getField(MessageInfo.MESSAGE_INFO_TYPE_ID)).getReference().getField(MessageInfoType.CODE).toString()))
                {   // Must be a request!
                    this.getField(MessageProcessInfo.MESSAGE_INFO_ID).moveFieldToThis(recMessageInfo.getField(MessageInfo.ID));
                    this.getField(MessageProcessInfo.MESSAGE_TYPE_ID).setValue(((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_TYPE_ID)).getIDFromCode(MessageType.MESSAGE_IN));
                    this.getField(MessageProcessInfo.PROCESS_TYPE_ID).setValue(((ReferenceField)this.getField(MessageProcessInfo.PROCESS_TYPE_ID)).getIDFromCode(ProcessType.INFO));
                    this.setKeyArea(MessageProcessInfo.MESSAGE_INFO_ID_KEY);
                    if (this.seek(null))
                        return this;
                    this.getField(MessageProcessInfo.PROCESS_TYPE_ID).setValue(((ReferenceField)this.getField(MessageProcessInfo.PROCESS_TYPE_ID)).getIDFromCode(ProcessType.UPDATE));    // Should not be, but check anyway
                    if (this.seek(null))
                        return this;
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            this.setKeyArea(iOldKeyArea);   // Set this back
        }
        return null;
    }
    /**
     * GetMessageProcessInfo Method.
     */
    public MessageProcessInfoModel getMessageProcessInfo(String strMessageInfoType, String strContactType, String strRequestType, String strMessageProcessType, String strProcessType)
    {
        int iOldKeyArea = this.getDefaultOrder();
        try {
            Record recMessageInfo = ((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReferenceRecord();
            int iMessageTypeID = ((ReferenceField)recMessageInfo.getField(MessageInfo.MESSAGE_INFO_TYPE_ID)).getIDFromCode(strMessageInfoType);
            if (iMessageTypeID <= 0)
                return null;
            recMessageInfo.getField(MessageInfo.MESSAGE_INFO_TYPE_ID).setValue(iMessageTypeID);
            int iContactTypeID = ((ReferenceField)recMessageInfo.getField(MessageInfo.CONTACT_TYPE_ID)).getIDFromCode(strContactType);
            if (iContactTypeID <= 0)
                return null;
            recMessageInfo.getField(MessageInfo.CONTACT_TYPE_ID).setValue(iContactTypeID);
            int iRequestTypeID = ((ReferenceField)recMessageInfo.getField(MessageInfo.REQUEST_TYPE_ID)).getIDFromCode(strRequestType);
            if (iRequestTypeID <= 0)
                return null;
            recMessageInfo.getField(MessageInfo.REQUEST_TYPE_ID).setValue(iRequestTypeID);
            recMessageInfo.setKeyArea(MessageInfo.MESSAGE_INFO_TYPE_ID_KEY);
            if (recMessageInfo.seek(DBConstants.EQUALS))
            {
                this.setKeyArea(MessageProcessInfo.MESSAGE_INFO_ID_KEY);
                this.getField(MessageProcessInfo.MESSAGE_INFO_ID).moveFieldToThis((BaseField)recMessageInfo.getCounterField());
                int iMessageProcessTypeID = ((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_TYPE_ID)).getIDFromCode(strMessageProcessType);
                if (iMessageProcessTypeID <= 0)
                    return null;
                this.getField(MessageProcessInfo.MESSAGE_TYPE_ID).setValue(iMessageProcessTypeID);
                int iProcessTypeID = ((ReferenceField)this.getField(MessageProcessInfo.PROCESS_TYPE_ID)).getIDFromCode(strProcessType);
                if (iProcessTypeID <= 0)
                    return null;
                this.getField(MessageProcessInfo.PROCESS_TYPE_ID).setValue(iProcessTypeID);
                
                if (this.seek(null))
                    return this;
            }
            // Try the parent contact type
            Record recContactType = ((ReferenceField)recMessageInfo.getField(MessageInfo.CONTACT_TYPE_ID)).getReferenceRecord();
            if (recContactType != null)
            {
                recContactType = ((ReferenceField)recContactType.getField(ContactType.PARENT_CONTACT_TYPE_ID)).getReference();
                if (recContactType != null)
                    if ((recContactType.getEditMode() == DBConstants.EDIT_CURRENT) || (recContactType.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                {
                    strContactType = recContactType.getField(ContactType.CODE).toString();
                    if (strContactType != null)
                        return this.getMessageProcessInfo(strMessageInfoType, strContactType, strRequestType, strMessageProcessType, strProcessType);
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            this.setKeyArea(iOldKeyArea);   // Set this back
        }
        return null;
    }
    /**
     * SetupMessageHeaderFromCode Method.
     */
    public boolean setupMessageHeaderFromCode(Message trxMessage, String strMessageCode, String strVersion)
    {
        TrxMessageHeader trxMessageHeader = (TrxMessageHeader)((BaseMessage)trxMessage).getMessageHeader();
        if ((trxMessageHeader == null) && (strMessageCode == null))
            return false;
        if (trxMessageHeader == null)
        {
            trxMessageHeader = new TrxMessageHeader(null, null);
            ((BaseMessage)trxMessage).setMessageHeader(trxMessageHeader);
        }
        if (strMessageCode == null)
            strMessageCode = (String)trxMessageHeader.get(TrxMessageHeader.MESSAGE_CODE);
        Utility.getLogger().info("Message code: " + strMessageCode);
        if (strMessageCode == null)
            return false;   // Message not found
        MessageProcessInfo recMessageProcessInfo = (MessageProcessInfo)this.getMessageProcessInfo(strMessageCode);
        if (recMessageProcessInfo == null)
            return false;   // Message not found
        MessageInfo recMessageInfo = (MessageInfo)((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference();
        if (recMessageInfo == null)
            return false;    // Impossible
        trxMessageHeader = recMessageInfo.addMessageProperties(trxMessageHeader);
        trxMessageHeader = this.addMessageProperties(trxMessageHeader);
        
        trxMessageHeader = this.addTransportProperties(trxMessageHeader, strVersion);
        return true;
    }
    /**
     * From this valid record and target record, create the proper message.
     * This must be valid and the recTarget must be valid.
     */
    public TrxMessageHeader createProcessMessageHeader(MessageDetailTarget recMessageDetailTarget, String strMessageTransport)
    {
        if ((this.getEditMode() != DBConstants.EDIT_CURRENT) && (this.getEditMode() != DBConstants.EDIT_IN_PROGRESS))
            return null;    // Must have a valid record
        TrxMessageHeader trxMessageHeader = new TrxMessageHeader(null, null);
        // Add properties from this process info
        MessageInfo recMessageInfo = (MessageInfo)((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference();
        if (recMessageInfo == null)
            return null;    // Impossible
        trxMessageHeader = recMessageInfo.addMessageProperties(trxMessageHeader);
        trxMessageHeader = this.addMessageProperties(trxMessageHeader);
        
        if (recMessageDetailTarget != null)
        {
            if (m_recMessageDetail == null)
            {
                RecordOwner recordOwner = this.findRecordOwner();
                m_recMessageDetail = new MessageDetail(recordOwner);
                recordOwner.removeRecord(m_recMessageDetail);
                this.addListener(new FreeOnFreeHandler(m_recMessageDetail));
            }
            Vector<MessageDetailTarget> vMessageDetailTarget = new Vector<MessageDetailTarget>();
            while (recMessageDetailTarget != null)
            {
                vMessageDetailTarget.addElement(recMessageDetailTarget);
                recMessageDetailTarget = recMessageDetailTarget.getNextMessageDetailTarget();
            }
            for (int i = vMessageDetailTarget.size() - 1; i >= 0; i--)
            {
                recMessageDetailTarget = vMessageDetailTarget.get(i);
                trxMessageHeader = recMessageDetailTarget.addMessageProperties(trxMessageHeader);
            }
            MessageTransport recMessageTransport = null;
            if ((strMessageTransport == null) || (strMessageTransport.length() == 0))
                strMessageTransport = (String)trxMessageHeader.get(MessageTransport.SEND_MESSAGE_BY_PARAM);
            if ((strMessageTransport != null) && (strMessageTransport.length() != 0))
                recMessageTransport = this.getMessageTransport(strMessageTransport);
            if (recMessageTransport == null)
            { // Still no transport, see if there is a default transport in the MessageDetail
                for (int i = 0; i < vMessageDetailTarget.size(); i++)
                {
                    recMessageDetailTarget = vMessageDetailTarget.get(i);
                    recMessageTransport = m_recMessageDetail.getDefaultMessageTransport(recMessageDetailTarget, this);
                    if (recMessageTransport != null)
                        break;
                }
            }
            if (recMessageTransport == null)
            { // See if there is a default transport specified in this record
                if (!this.getField(MessageProcessInfo.DEFAULT_MESSAGE_TRANSPORT_ID).isNull())
                    recMessageTransport = (MessageTransport)((ReferenceField)this.getField(MessageProcessInfo.DEFAULT_MESSAGE_TRANSPORT_ID)).getReference();
            }
            if (recMessageTransport == null)
            { // See if there is a default transport in the MessageDetailTarget chain.
                for (int i = 0; i < vMessageDetailTarget.size(); i++)
                {
                    recMessageDetailTarget = vMessageDetailTarget.get(i);
                    recMessageTransport = recMessageDetailTarget.getMessageTransport(trxMessageHeader);
                    if (recMessageTransport != null)
                        if ((recMessageTransport.getEditMode() == DBConstants.EDIT_CURRENT) || (recMessageTransport.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                            break;
                }
            }
            if ((recMessageTransport == null)
                || ((recMessageTransport.getEditMode() != DBConstants.EDIT_CURRENT) && (recMessageTransport.getEditMode() != DBConstants.EDIT_IN_PROGRESS)))
                    recMessageTransport = this.getMessageTransport(MessageTransport.DIRECT);
            trxMessageHeader = recMessageTransport.addMessageProperties(trxMessageHeader);
            for (int i = vMessageDetailTarget.size() - 1; i >= 0; i--)
            {
                recMessageDetailTarget = vMessageDetailTarget.get(i);
                trxMessageHeader = m_recMessageDetail.addMessageProperties(trxMessageHeader, recMessageDetailTarget, this, recMessageTransport);
            }
            for (int i = vMessageDetailTarget.size() - 1; i >= 0; i--)
            {
                recMessageDetailTarget = vMessageDetailTarget.get(i);
                trxMessageHeader = recMessageDetailTarget.addDestInfo(trxMessageHeader);
            }
        }
        else
        { // If there is no target detail, at least try to get the transport
            MessageTransport recMessageTransport = null;
            if ((strMessageTransport == null) || (strMessageTransport.length() == 0))
                strMessageTransport = (String)trxMessageHeader.get(MessageTransport.SEND_MESSAGE_BY_PARAM);
            if ((strMessageTransport != null) && (strMessageTransport.length() != 0))
                recMessageTransport = this.getMessageTransport(strMessageTransport);
            if (recMessageTransport != null) if (true)
                trxMessageHeader = recMessageTransport.addMessageProperties(trxMessageHeader);          
        }
        trxMessageHeader = this.addTransportProperties(trxMessageHeader, null);
        if (trxMessageHeader.get(MessageConstants.QUEUE_NAME) != null)
            trxMessageHeader.setQueueName((String)trxMessageHeader.get(MessageConstants.QUEUE_NAME));
        if (trxMessageHeader.get(MessageConstants.QUEUE_TYPE) != null)
            trxMessageHeader.setQueueType((String)trxMessageHeader.get(MessageConstants.QUEUE_TYPE));
        
        return trxMessageHeader;
    }
    /**
     * GetMessageTransport Method.
     */
    public MessageTransport getMessageTransport(String strMessageTransport)
    {
        if (m_recMessageTransport == null)
        {
            RecordOwner recordOwner = this.findRecordOwner();
            m_recMessageTransport = new MessageTransport(recordOwner);
            if (recordOwner != null)
                recordOwner.removeRecord(m_recMessageTransport);    // Set it is not on the recordowner's list
            this.addListener(new FreeOnFreeHandler(m_recMessageTransport));
        }
        return m_recMessageTransport.getMessageTransport(strMessageTransport);
    }
    /**
     * GetMessageControl Method.
     */
    public MessageControl getMessageControl()
    {
        if (m_recMessageControl == null)
            {
            RecordOwner recordOwner = this.findRecordOwner();
            m_recMessageControl = new MessageControl(recordOwner);
            if (recordOwner != null)
                recordOwner.removeRecord(m_recMessageControl);    // Set it is not on the recordowner's list
            this.addListener(new FreeOnFreeHandler(m_recMessageControl));
        }
        return m_recMessageControl;
    }
    /**
     * AddMessageProperties Method.
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader)
    {
        Map<String,Object> mapHeaderMessageInfo = trxMessageHeader.getMessageInfoMap();
        
        Map<String,Object> propMessageProcessInfo = ((PropertiesField)this.getField(MessageProcessInfo.PROPERTIES)).loadProperties();
        String strQueueName = this.getQueueName(false);
        if (propMessageProcessInfo.get(MessageConstants.QUEUE_NAME) == null)
            if (strQueueName != null)
                propMessageProcessInfo.put(MessageConstants.QUEUE_NAME, strQueueName);
        String strQueueType = this.getQueueType(false);
        if (propMessageProcessInfo.get(MessageConstants.QUEUE_TYPE) == null)
            if (strQueueType != null)
                propMessageProcessInfo.put(MessageConstants.QUEUE_TYPE, strQueueType);
        if (!this.getField(MessageProcessInfo.REPLY_MESSAGE_PROCESS_INFO_ID).isNull())
            propMessageProcessInfo.put(TrxMessageHeader.MESSAGE_RESPONSE_ID, this.getField(MessageProcessInfo.REPLY_MESSAGE_PROCESS_INFO_ID).toString());
        if (!this.getField(MessageProcessInfo.LOCAL_MESSAGE_PROCESS_INFO_ID).isNull())
            propMessageProcessInfo.put(LocalMessageTransport.LOCAL_PROCESSOR, this.getField(MessageProcessInfo.LOCAL_MESSAGE_PROCESS_INFO_ID).toString());
        if (!this.getField(MessageProcessInfo.MESSAGE_TYPE_ID).isNull())
        {
            MessageType recMessageType = (MessageType)((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_TYPE_ID)).getReference();
            if (recMessageType != null)
                propMessageProcessInfo.put(TrxMessageHeader.MESSAGE_PROCESS_TYPE, recMessageType.getField(MessageType.CODE).toString());
        }
        
        if (!this.getField(MessageProcessInfo.PROCESSOR_CLASS).isNull())
            propMessageProcessInfo.put(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS, this.getField(MessageProcessInfo.PROCESSOR_CLASS).toString());
        if (mapHeaderMessageInfo != null)
            mapHeaderMessageInfo.putAll(propMessageProcessInfo);
        else
            mapHeaderMessageInfo = propMessageProcessInfo;
        if (mapHeaderMessageInfo.get(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS) == null)
            this.setDefaultMessageProcessor(mapHeaderMessageInfo);
        trxMessageHeader.setMessageInfoMap(mapHeaderMessageInfo);
        
        trxMessageHeader.put(TrxMessageHeader.MESSAGE_PROCESS_INFO_ID, this.getCounterField().toString());
        
        String strDescription = this.getField(MessageProcessInfo.DESCRIPTION).toString();
        if (((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference() != null)
            if (!((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference().getField(MessageInfo.DESCRIPTION).isNull())
                strDescription = ((ReferenceField)this.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference().getField(MessageInfo.DESCRIPTION).toString();
        trxMessageHeader.put(TrxMessageHeader.DESCRIPTION, strDescription);
        
        return trxMessageHeader;
    }
    /**
     * AddTransportProperties Method.
     */
    public TrxMessageHeader addTransportProperties(TrxMessageHeader trxMessageHeader, String strVersion)
    {
        if (trxMessageHeader.get(MessageTransport.TRANSPORT_ID_PARAM) != null)
        {
            RecordOwner recordOwner = this.findRecordOwner();
            if (m_recMessageTransport == null)
            {
                m_recMessageTransport = new MessageTransport(recordOwner);
                if (recordOwner != null)
                    recordOwner.removeRecord(m_recMessageTransport);
                this.addListener(new FreeOnFreeHandler(m_recMessageTransport));
            }
            if (m_recMessageTransportInfo == null)
            {
                m_recMessageTransportInfo = new MessageTransportInfo(recordOwner);
                if (recordOwner != null)
                    recordOwner.removeRecord(m_recMessageTransportInfo);
                this.addListener(new FreeOnFreeHandler(m_recMessageTransportInfo));
            }
            SubFileFilter subFileFilter = null;
            try {
                if (m_recMessageTransport.setHandle(trxMessageHeader.get(MessageTransport.TRANSPORT_ID_PARAM), DBConstants.BOOKMARK_HANDLE) != null)
                {
                    Map<String,Object> propMessageTransport = ((PropertiesField)m_recMessageTransport.getField(MessageTransport.PROPERTIES)).loadProperties();
                    String strInitialMessageStatusID = null;
                    if (!this.getField(MessageProcessInfo.INITIAL_MESSAGE_STATUS_ID).isNull())
                        strInitialMessageStatusID = this.getField(MessageProcessInfo.INITIAL_MESSAGE_STATUS_ID).toString();
                    if (strInitialMessageStatusID != null)
                    {
                        if (propMessageTransport == null)
                            propMessageTransport = new Hashtable<String,Object>();
                        propMessageTransport.put(MessageTransport.INITIAL_MESSAGE_DATA_STATUS, strInitialMessageStatusID);
                    }
                    if (propMessageTransport != null)
                    {
                        Map<String,Object> propHeaderTransport = trxMessageHeader.getMessageTransportMap();
                        if (propHeaderTransport != null)
                            propHeaderTransport.putAll(propMessageTransport);
                        else
                            propHeaderTransport = propMessageTransport;
                        trxMessageHeader.setMessageTransportMap(propHeaderTransport);
                    }
                    m_recMessageTransportInfo.setKeyArea(MessageTransportInfo.MESSAGE_PROCESS_INFO_ID_KEY);
                    int iMessageVersionID = 0;
                    if (trxMessageHeader.getProperties() != null)
                        if (strVersion == null)
                    {
                        if (trxMessageHeader.getProperties().get(MessageVersion.VERSION_ID) != null)
                        {
                            try {
                                iMessageVersionID = Integer.parseInt(trxMessageHeader.getProperties().get(MessageVersion.VERSION_ID).toString());
                            } catch (NumberFormatException ex) {
                                // Ignore
                            }
                        }
                        else
                            strVersion = (String)trxMessageHeader.getProperties().get(MessageVersion.VERSION);
                    }
                    MessageVersion recMessageVersion = this.getMessageControl().getMessageVersion();
                    if (iMessageVersionID != 0)
                    {
                        recMessageVersion.getField(MessageVersion.ID).setValue(iMessageVersionID);
                        recMessageVersion.setKeyArea(MessageVersion.ID_KEY);
                        if (!recMessageVersion.seek(DBConstants.EQUALS))
                            iMessageVersionID = 0;  // Never
                    }
                    if (iMessageVersionID == 0)
                    {
                        recMessageVersion = this.getMessageControl().getMessageVersion(strVersion);
                        iMessageVersionID = (int)recMessageVersion.getField(MessageVersion.ID).getValue();
                        if (strVersion == null)
                        {
                            int iMessageVersionIDDefault = 0;
                            int iMessageVersionIDBestGuess = iMessageVersionID;
                            boolean bDefaultExists = false;
                            m_recMessageTransportInfo.addListener(subFileFilter = new SubFileFilter(this.getField(MessageProcessInfo.ID), MessageTransportInfo.MESSAGE_PROCESS_INFO_ID, m_recMessageTransport.getField(MessageTransport.ID), MessageTransportInfo.MESSAGE_TRANSPORT_ID, null, null));
                            while  (m_recMessageTransportInfo.hasNext())
                            {
                                m_recMessageTransportInfo.next();
                                if (m_recMessageTransportInfo.getField(MessageTransportInfo.ACTIVE).getState())
                                    iMessageVersionIDBestGuess = (int)m_recMessageTransportInfo.getField(MessageTransportInfo.MESSAGE_VERSION_ID).getValue();
                                else if (m_recMessageTransportInfo.getField(MessageTransportInfo.MESSAGE_VERSION_ID).getValue() == 0)
                                    iMessageVersionIDBestGuess = 0;    // If not active with no version, best guess is no version
                                if (m_recMessageTransportInfo.getField(MessageTransportInfo.DEFAULT_TRANSPORT).getState())  // Default is always the best guess
                                    iMessageVersionIDDefault = (int)m_recMessageTransportInfo.getField(MessageTransportInfo.MESSAGE_VERSION_ID).getValue();
                                if (m_recMessageTransportInfo.getField(MessageTransportInfo.MESSAGE_VERSION_ID).getValue() == iMessageVersionID)  // Default is always the best guess
                                    bDefaultExists = true;
                            }
                            subFileFilter.free();
                            subFileFilter = null;
                            if (iMessageVersionIDDefault != 0)
                                iMessageVersionID = iMessageVersionIDDefault;   // If there is a default, always use it
                            else if (!bDefaultExists)
                                iMessageVersionID = iMessageVersionIDBestGuess; // else, If the default doesn't exist, use the best guess
                            recMessageVersion.getField(MessageVersion.ID).setValue(iMessageVersionID);
                            recMessageVersion.setKeyArea(MessageVersion.ID_KEY);
                            if (!recMessageVersion.seek(DBConstants.EQUALS))
                                iMessageVersionID = 0;  // Never
                        }
                    }
                    m_recMessageTransportInfo.getField(MessageTransportInfo.MESSAGE_PROCESS_INFO_ID).moveFieldToThis(this.getField(MessageProcessInfo.ID));
                    m_recMessageTransportInfo.getField(MessageTransportInfo.MESSAGE_TRANSPORT_ID).moveFieldToThis(m_recMessageTransport.getField(MessageTransport.ID));
                    m_recMessageTransportInfo.getField(MessageTransportInfo.MESSAGE_VERSION_ID).setValue(iMessageVersionID);
                    if (m_recMessageTransportInfo.seek(DBConstants.EQUALS))
                    {
                        trxMessageHeader = recMessageVersion.addMessageProperties(trxMessageHeader, this.getMessageControl());
                        trxMessageHeader = m_recMessageTransportInfo.addMessageProperties(trxMessageHeader);
                    }
                }
            } catch (DBException ex) {
                ex.printStackTrace();
            } finally {
                if (subFileFilter != null)
                    subFileFilter.free();
            }
        }
        return trxMessageHeader;
    }
    /**
     * et the default processor if one is not specified.
     */
    public void setDefaultMessageProcessor(Map<String,Object> mapHeaderMessageInfo)
    {
        if (mapHeaderMessageInfo.get(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS) == null)
        {
            String strMessageInfoTypeCode = (String)mapHeaderMessageInfo.get(TrxMessageHeader.MESSAGE_INFO_TYPE);
            String strMessageProcessTypeCode = (String)mapHeaderMessageInfo.get(TrxMessageHeader.MESSAGE_PROCESS_TYPE);
            if ((strMessageInfoTypeCode != null) && (strMessageProcessTypeCode != null))
            {
                String strProcessorClass = null;
                if ((MessageInfoType.REQUEST.equals(strMessageInfoTypeCode)) && (MessageType.MESSAGE_OUT.equals(strMessageProcessTypeCode)))
                    strProcessorClass = BaseMessageOutProcessor.class.getName();
                else if ((MessageInfoType.REQUEST.equals(strMessageInfoTypeCode)) && (MessageType.MESSAGE_IN.equals(strMessageProcessTypeCode)))
                    strProcessorClass = BaseMessageInProcessor.class.getName();
                else if ((MessageInfoType.REPLY.equals(strMessageInfoTypeCode)) && (MessageType.MESSAGE_OUT.equals(strMessageProcessTypeCode)))
                    strProcessorClass = BaseMessageReplyOutProcessor.class.getName();
                if ((MessageInfoType.REPLY.equals(strMessageInfoTypeCode)) && (MessageType.MESSAGE_IN.equals(strMessageProcessTypeCode)))
                    strProcessorClass = BaseMessageReplyInProcessor.class.getName();
                if (strProcessorClass != null)
                    mapHeaderMessageInfo.put(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS, strProcessorClass);
            }
        }
    }
    /**
     * Create the response message for this message.
     * @return the response message (or null if none).
     */
    public Message createReplyMessage(Message message)
    {
        Object objResponseID = ((BaseMessage)message).getMessageHeader().get(TrxMessageHeader.MESSAGE_RESPONSE_ID);
        if (objResponseID == null)
            return null;    // TODO (don) FIX this - return an error.
        MessageProcessInfo recMessageProcessInfo = (MessageProcessInfo)this.getMessageProcessInfo(objResponseID.toString());
        MessageInfo recMessageInfo = (MessageInfo)((ReferenceField)recMessageProcessInfo.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference();
        BaseMessage replyMessage = new TreeMessage(null, null);
        MessageRecordDesc messageRecordDesc = recMessageInfo.createNewMessage(replyMessage, null);
        return replyMessage;
    }
    /**
     * Using this URL, create a message and send it to the recepient
     * Note: If the MessageProcessInfo record is current, uses this information for sending the message
     * @param strMessageTransport The (optional) transport code or ID
     * @param recMessageTargetDetail The (optional) Message target (will use this for resolving transport and destination information)
     * @return An error code.
     */
    public int createAndSendURLMessage(String strMessageTransport, MessageDetailTarget recMessageDetailTarget, String strURL, Map<String,Object> properties)
    {
        String strBaseURL = strURL;
        URL url = this.getTask().getApplication().getResourceURL(strURL, null);
        strURL = url.toString();
        if ((this.getRecordOwner() != null)
            && (this.getRecordOwner().getProperty(DBParams.BASE_URL) != null))
                strBaseURL = this.getRecordOwner().getProperty(DBParams.BASE_URL);
        else if (strURL.indexOf(strBaseURL) > 0)   // Make sure the xsl processor creates all the links with a full URL (since emails, etc probably need full URLs)
            strBaseURL = strURL.substring(0, strURL.indexOf(strBaseURL));
        else
            strBaseURL = null;
        if (strBaseURL != null)
            strURL = Utility.addURLParam(strURL, DBParams.BASE_URL, strBaseURL);
        
        // Now, transfer the URL data to the file or this string.
        String strMessage = Utility.transferURLStream(strURL, null);
        
        if ((strMessage != null) && (strMessage.length() > 0))
        {   // Note: properties include SEND_BY and DESTINATION
            if (properties == null)
                properties = new Hashtable<String, Object>();
            Utility.parseArgs(properties, strURL);
            
            if (strMessage != null)
                if (strMessage.startsWith("%PDF"))
                    if (properties.get("content-type") == null)
                        properties.put("content-type", "application/pdf");
        
            BaseMessage message = null;
            // First see if the use specifies a specific message
            TrxMessageHeader trxMessageHeader = null;
            MessageProcessInfo recMessageProcessInfo = this;
            if (recMessageProcessInfo.getEditMode() != DBConstants.EDIT_CURRENT)
            {   // If not, try to look up the correct message
                String strMessageInfoType = MessageInfoType.REQUEST;
                String strContactType = null; 
                if (recMessageDetailTarget instanceof Record) // Usually
                    strContactType = ((Record)recMessageDetailTarget).getTableNames(false);
                String strRequestType = RequestType.MANUAL;
                String strMessageProcessType = MessageType.MESSAGE_OUT;
                String strProcessType = ProcessType.INFO;
                recMessageProcessInfo = (MessageProcessInfo)this.getMessageProcessInfo(strMessageInfoType, strContactType, strRequestType, strMessageProcessType, strProcessType);
            }
            if (recMessageProcessInfo != null)
                if (recMessageProcessInfo.getEditMode() == DBConstants.EDIT_CURRENT)
            {
                trxMessageHeader = recMessageProcessInfo.createProcessMessageHeader(recMessageDetailTarget, strMessageTransport);
                if (trxMessageHeader.getMessageHeaderMap() == null)
                    trxMessageHeader.setMessageHeaderMap(properties);
                else
                    trxMessageHeader.getMessageHeaderMap().putAll(properties); // These params override read params
                message = BaseMessage.createMessage(trxMessageHeader);
            }
            if (message == null)
            {   // If all else fails, just create a manual message.
                if (trxMessageHeader == null)
                    trxMessageHeader = new TrxMessageHeader(this, properties);
                message = new ManualMessage(trxMessageHeader, strMessage);
            }
            else
                message.put(ManualMessage.MESSAGE_PARAM, strMessage);  // The physical message
            if (trxMessageHeader.get(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS) == null)
                trxMessageHeader.put(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS, BaseMessageOutProcessor.class.getName());   // Default processor
            
            if ((trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM) == null) && (message.get(TrxMessageHeader.DESTINATION_PARAM) == null))
                return this.getTask().setLastError(this.getTask().getString("No destination address in message"));
        
            MessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
            if (messageManager != null)
                messageManager.sendMessage(message);
        }
        return DBConstants.NORMAL_RETURN;
    }

}
