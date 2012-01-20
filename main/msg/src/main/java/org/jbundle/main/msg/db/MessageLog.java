/**
 * @(#)MessageLog.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.message.trx.message.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.trx.transport.screen.*;
import org.jbundle.model.message.*;
import org.jbundle.main.db.base.*;
import org.jbundle.main.user.db.*;
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageLog - Message log display.
 */
public class MessageLog extends VirtualRecord
     implements MessageLogModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kMessageInfoTypeID = kVirtualRecordLastField + 1;
    public static final int kMessageTypeID = kMessageInfoTypeID + 1;
    public static final int kMessageStatusID = kMessageTypeID + 1;
    public static final int kMessageTransportID = kMessageStatusID + 1;
    public static final int kMessageProcessInfoID = kMessageTransportID + 1;
    public static final int kContactTypeID = kMessageProcessInfoID + 1;
    public static final int kContactID = kContactTypeID + 1;
    public static final int kDescription = kContactID + 1;
    public static final int kMessageTime = kDescription + 1;
    public static final int kTimeoutSeconds = kMessageTime + 1;
    public static final int kTimeoutTime = kTimeoutSeconds + 1;
    public static final int kUserID = kTimeoutTime + 1;
    public static final int kReferenceType = kUserID + 1;
    public static final int kReferenceID = kReferenceType + 1;
    public static final int kResponseMessageLogID = kReferenceID + 1;
    public static final int kMessageHeaderProperties = kResponseMessageLogID + 1;
    public static final int kMessageInfoProperties = kMessageHeaderProperties + 1;
    public static final int kMessageTransportProperties = kMessageInfoProperties + 1;
    public static final int kMessageClassName = kMessageTransportProperties + 1;
    public static final int kMessageHeaderClassName = kMessageClassName + 1;
    public static final int kMessageDataClassName = kMessageHeaderClassName + 1;
    public static final int kExternalMessageClassName = kMessageDataClassName + 1;
    public static final int kMessageQueueName = kExternalMessageClassName + 1;
    public static final int kMessageQueueType = kMessageQueueName + 1;
    public static final int kMessageDataType = kMessageQueueType + 1;
    public static final int kXMLMessageData = kMessageDataType + 1;
    public static final int kMessageData = kXMLMessageData + 1;
    public static final int kErrorText = kMessageData + 1;
    public static final int kMessageLogLastField = kErrorText;
    public static final int kMessageLogFields = kErrorText - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kReferenceIDKey = kIDKey + 1;
    public static final int kContactTypeIDKey = kReferenceIDKey + 1;
    public static final int kMessageTimeKey = kContactTypeIDKey + 1;
    public static final int kTimeoutKey = kMessageTimeKey + 1;
    public static final int kMessageLogLastKey = kTimeoutKey;
    public static final int kMessageLogKeys = kTimeoutKey - DBConstants.MAIN_KEY_FIELD + 1;
    public static final int MESSAGE_SCREEN_MODE = ScreenConstants.LAST_MODE * 2;
    public static final int SOURCE_SCREEN_MODE = ScreenConstants.LAST_MODE * 4;
    /**
     * Default constructor.
     */
    public MessageLog()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageLog(RecordOwner screen)
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

    public static final String kMessageLogFile = "MessageLog";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kMessageLogFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Message";
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
        return DBConstants.REMOTE | DBConstants.USER_DATA | DBConstants.SERVER_REWRITES | DBConstants.DONT_LOG_TRX;
    }
    /**
     * MakeScreen Method.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & MessageLog.MESSAGE_SCREEN_MODE) == MessageLog.MESSAGE_SCREEN_MODE)
        {
            if ((this.getEditMode() == DBConstants.EDIT_ADD) || (this.getEditMode() == DBConstants.EDIT_NONE))
            {
                String strObjectID = parentScreen.getProperty(DBConstants.OBJECT_ID);
                if ((strObjectID != null) && (strObjectID.length() > 0))
                {
                    try {
                        this.setHandle(strObjectID, DBConstants.BOOKMARK_HANDLE);
                    } catch (DBException ex)    {
                        ex.printStackTrace();
                    }
                }
            }
            if ((this.getEditMode() == DBConstants.EDIT_CURRENT) || (this.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
            {
                String strScreenClass = this.getProperty(ScreenMessageTransport.SCREEN_SCREEN);
                if (strScreenClass == null)
                    strScreenClass = this.getProperty(DBParams.SCREEN);
                if (strScreenClass != null)
                {
                    parentScreen.setProperty(TrxMessageHeader.LOG_TRX_ID, this.getProperty(TrxMessageHeader.LOG_TRX_ID));
                    screen = Record.makeNewScreen(strScreenClass, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, null, true);
                }
            }
            if (screen == null) // ? I don't know what else to do?
                screen = Record.makeNewScreen(MESSAGE_LOG_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        }
        else if ((iDocMode & MessageLog.SOURCE_SCREEN_MODE) == MessageLog.SOURCE_SCREEN_MODE)
        {
            String strReferenceClass = this.getProperty(TrxMessageHeader.REFERENCE_CLASS);
            String strReferenceID = this.getField(MessageLog.kReferenceID).toString();
            if (strReferenceID == null)
                strReferenceID = this.getProperty(TrxMessageHeader.REFERENCE_ID);
            if ((strReferenceClass != null) && (strReferenceID != null))
            {
                Record record = Record.makeRecordFromClassName(strReferenceClass, Utility.getRecordOwner(this));
                if (record != null)
                {
                    try {
                        record.addNew();
                        record.getCounterField().setString(strReferenceID);
                        if (record.seek(null))
                        {
                            iDocMode = ScreenConstants.MAINT_MODE;
                            screen = record.makeScreen(itsLocation, parentScreen, iDocMode, properties);
                        }
                    } catch (DBException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(MESSAGE_LOG_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else
            screen = Record.makeNewScreen(MESSAGE_LOG_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        return screen;
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
        if (iFieldSeq == kMessageInfoTypeID)
            field = new MessageInfoTypeField(this, "MessageInfoTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageTypeID)
            field = new MessageTypeField(this, "MessageTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageStatusID)
            field = new MessageStatusField(this, "MessageStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageTransportID)
            field = new MessageTransportField(this, "MessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageProcessInfoID)
            field = new MessageProcessInfoField(this, "MessageProcessInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kContactTypeID)
            field = new ContactTypeField(this, "ContactTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kContactID)
            field = new ContactField(this, "ContactID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 60, null, null);
        if (iFieldSeq == kMessageTime)
            field = new MessageLog_MessageTime(this, "MessageTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTimeoutSeconds)
            field = new IntegerField(this, "TimeoutSeconds", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTimeoutTime)
            field = new DateTimeField(this, "TimeoutTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kUserID)
            field = new UserField(this, "UserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kReferenceType)
            field = new StringField(this, "ReferenceType", 60, null, null);
        if (iFieldSeq == kReferenceID)
            field = new ReferenceField(this, "ReferenceID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kResponseMessageLogID)
            field = new MessageLogField(this, "ResponseMessageLogID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageHeaderProperties)
            field = new PropertiesField(this, "MessageHeaderProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageInfoProperties)
            field = new PropertiesField(this, "MessageInfoProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageTransportProperties)
            field = new PropertiesField(this, "MessageTransportProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageClassName)
            field = new StringField(this, "MessageClassName", 128, null, null);
        if (iFieldSeq == kMessageHeaderClassName)
            field = new StringField(this, "MessageHeaderClassName", 128, null, null);
        if (iFieldSeq == kMessageDataClassName)
            field = new StringField(this, "MessageDataClassName", 128, null, null);
        if (iFieldSeq == kExternalMessageClassName)
            field = new StringField(this, "ExternalMessageClassName", 128, null, null);
        if (iFieldSeq == kMessageQueueName)
            field = new StringField(this, "MessageQueueName", 60, null, null);
        if (iFieldSeq == kMessageQueueType)
            field = new StringField(this, "MessageQueueType", 60, null, null);
        if (iFieldSeq == kMessageDataType)
            field = new StringField(this, "MessageDataType", 30, null, null);
        if (iFieldSeq == kXMLMessageData)
            field = new XmlField(this, "XMLMessageData", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageData)
            field = new MemoField(this, "MessageData", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kErrorText)
            field = new StringField(this, "ErrorText", 127, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageLogLastField)
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
        if (iKeyArea == kReferenceIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ReferenceID");
            keyArea.addKeyField(kReferenceID, DBConstants.ASCENDING);
            keyArea.addKeyField(kMessageTime, DBConstants.ASCENDING);
        }
        if (iKeyArea == kContactTypeIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ContactTypeID");
            keyArea.addKeyField(kContactTypeID, DBConstants.ASCENDING);
            keyArea.addKeyField(kContactID, DBConstants.ASCENDING);
            keyArea.addKeyField(kMessageTime, DBConstants.ASCENDING);
        }
        if (iKeyArea == kMessageTimeKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "MessageTime");
            keyArea.addKeyField(kMessageTime, DBConstants.ASCENDING);
        }
        if (iKeyArea == kTimeoutKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Timeout");
            keyArea.addKeyField(kMessageStatusID, DBConstants.ASCENDING);
            keyArea.addKeyField(kTimeoutTime, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kMessageLogLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kMessageLogLastKey)
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
        this.addListener(new CalcTimeoutTimeHandler(null));
        this.addListener(new NotifyTimeoutProcessHandler(null));
        this.addListener(new NoDeleteModifyHandler(true, false));   // Can't delete these
    }
    /**
     * Convert the command to the screen document type.
     * @param strCommand The command text.
     * @param The standard document type (MAINT/DISPLAY/SELECT/MENU/etc).
     */
    public int commandToDocType(String strCommand)
    {
        if (MessageLog.MESSAGE_SCREEN.equalsIgnoreCase(strCommand))
            return MessageLog.MESSAGE_SCREEN_MODE;
        if (MessageLog.SOURCE_SCREEN.equalsIgnoreCase(strCommand))
            return MessageLog.SOURCE_SCREEN_MODE;
        return super.commandToDocType(strCommand);
    }
    /**
     * Create the original message header for this transaction ID.
     * @param strTrxID The transaction ID of the original transaction.
     * @return A message header with the params of this trx (or null if nonexistent).
     */
    public BaseMessageHeader createMessageHeader(String strTrxID)
    {
        if (this.getMessageLog(strTrxID) != null)
            return this.createMessageHeader();
        return null;
    }
    /**
     * CreateMessageHeader Method.
     */
    public BaseMessageHeader createMessageHeader()
    {
        Map<String,Object> properties = ((PropertiesField)this.getField(MessageLog.kMessageHeaderProperties)).getProperties();
        BaseMessageHeader messageHeader = null;
        String strMessageHeaderClassName = this.getField(MessageLog.kMessageHeaderClassName).toString();
        String strQueueName = this.getField(MessageLog.kMessageQueueName).toString();
        String strQueueType = this.getField(MessageLog.kMessageQueueType).toString();
        Object source = null;
        messageHeader = BaseMessageHeader.createMessageHeader(strMessageHeaderClassName, strQueueName, strQueueType, source, properties);
        if (messageHeader == null)
            messageHeader = new TrxMessageHeader(null, properties);
        if (messageHeader instanceof TrxMessageHeader)
        {
            properties = ((PropertiesField)this.getField(MessageLog.kMessageInfoProperties)).getProperties();
            ((TrxMessageHeader)messageHeader).setMessageInfoMap(properties);
            properties = ((PropertiesField)this.getField(MessageLog.kMessageTransportProperties)).getProperties();
            ((TrxMessageHeader)messageHeader).setMessageTransportMap(properties);
            ((TrxMessageHeader)messageHeader).put(TrxMessageHeader.LOG_TRX_ID, this.getCounterField().toString());  // Should be there
        }
        return messageHeader;
    }
    /**
     * CreateMessageData Method.
     */
    public BaseMessageRecordDesc createMessageData()
    {
        MessageRecordDesc messageData = null;
        String strMessageDataClassName = this.getField(MessageLog.kMessageDataClassName).toString();
        return MessageRecordDesc.createMessageRecordDesc(strMessageDataClassName, null, null);
    }
    /**
     * CreateMessage Method.
     */
    public Message createMessage(String strTrxID)
    {
        if (this.getMessageLog(strTrxID) != null)
        {
            BaseMessageHeader messageHeader = this.createMessageHeader();
            MessageRecordDesc messageData = (MessageRecordDesc)this.createMessageData();
            return this.createMessage(messageHeader, messageData);
        }
        return null;
    }
    /**
     * CreateMessage Method.
     */
    public Message createMessage(BaseMessageHeader messageHeader, MessageRecordDesc messageDataDesc)
    {
        BaseMessage message = null;
        String strMessageClassName = this.getField(MessageLog.kMessageClassName).toString();
        message = BaseMessage.createMessage(strMessageClassName, messageHeader, null);
        if (messageDataDesc != null)
            message.addMessageDataDesc(messageDataDesc);
        if (messageHeader instanceof TrxMessageHeader)
        {
            if (message == null)
                message = BaseMessage.createMessage((TrxMessageHeader)messageHeader);
            if (message == null)
                message = new TreeMessage((TrxMessageHeader)messageHeader, null);
        }
        String strXMLData = this.getField(MessageLog.kXMLMessageData).getString();
        message.setXML(strXMLData);
        return message;
    }
    /**
     * Get this property from this record.
     */
    public String getProperty(String strKey)
    {
        String strProperty = this.getMessageProperty(strKey);
        if (strProperty == null)
            strProperty = ((PropertiesField)this.getField(MessageLog.kMessageHeaderProperties)).getProperty(strKey);
        if (strProperty == null)
            strProperty = ((PropertiesField)this.getField(MessageLog.kMessageInfoProperties)).getProperty(strKey);
        if (strProperty == null)
            strProperty = ((PropertiesField)this.getField(MessageLog.kMessageTransportProperties)).getProperty(strKey);
        return strProperty;
    }
    /**
     * Get the data string from the message data from this (XPath) key.
     */
    public String getMessageProperty(String strKey)
    {
        // todo(don) fix this! ((PropertiesField)this.getField(MessageLog.kMessageProperties)).getProperty(strKey);
        return null;
    }
    /**
     * Get this record.
     */
    public MessageLogModel getMessageLog(String ID)
    {
        int iOldOrder = this.getDefaultOrder();
        try {
            this.addNew();
            this.getField(MessageLog.kID).setString(ID);
            this.setKeyArea(MessageLog.kIDKey);
            if (this.seek(null))
                return this;
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            this.setKeyArea(iOldOrder);
        }
        return null;
    }

}
