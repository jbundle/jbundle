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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.main.msg.screen.*;
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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(MESSAGE_LOG_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            if (screen == null) // ? I don't NOW what else to do?
                screen = Record.makeNewScreen(MESSAGE_LOG_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        }
        else if ((iDocMode & MessageLog.SOURCE_SCREEN_MODE) == MessageLog.SOURCE_SCREEN_MODE)
        {
            String strReferenceClass = this.getProperty(TrxMessageHeader.REFERENCE_CLASS);
            String strReferenceID = this.getField(MessageLog.REFERENCE_ID).toString();
            if (strReferenceID == null)
                strReferenceID = this.getProperty(TrxMessageHeader.REFERENCE_ID);
            if ((strReferenceClass != null) && (strReferenceID != null))
            {
                Record record = Record.makeRecordFromClassName(strReferenceClass, this.findRecordOwner());
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
            field = new MessageInfoTypeField(this, MESSAGE_INFO_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new MessageTypeField(this, MESSAGE_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new MessageStatusField(this, MESSAGE_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 6)
            field = new MessageTransportField(this, MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new MessageProcessInfoField(this, MESSAGE_PROCESS_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new ContactTypeField(this, CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 9)
            field = new ContactField(this, CONTACT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, DESCRIPTION, 60, null, null);
        if (iFieldSeq == 11)
            field = new MessageLog_MessageTime(this, MESSAGE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 12)
            field = new IntegerField(this, TIMEOUT_SECONDS, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 13)
            field = new DateTimeField(this, TIMEOUT_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 14)
            field = new UserField(this, USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 15)
            field = new StringField(this, REFERENCE_TYPE, 60, null, null);
        if (iFieldSeq == 16)
            field = new ReferenceField(this, REFERENCE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 17)
            field = new MessageLogField(this, RESPONSE_MESSAGE_LOG_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 18)
            field = new PropertiesField(this, MESSAGE_HEADER_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 19)
            field = new PropertiesField(this, MESSAGE_INFO_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 20)
            field = new PropertiesField(this, MESSAGE_TRANSPORT_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 21)
            field = new StringField(this, MESSAGE_CLASS_NAME, 128, null, null);
        if (iFieldSeq == 22)
            field = new StringField(this, MESSAGE_HEADER_CLASS_NAME, 128, null, null);
        if (iFieldSeq == 23)
            field = new StringField(this, MESSAGE_DATA_CLASS_NAME, 128, null, null);
        if (iFieldSeq == 24)
            field = new StringField(this, EXTERNAL_MESSAGE_CLASS_NAME, 128, null, null);
        if (iFieldSeq == 25)
            field = new StringField(this, MESSAGE_QUEUE_NAME, 60, null, null);
        if (iFieldSeq == 26)
            field = new StringField(this, MESSAGE_QUEUE_TYPE, 60, null, null);
        if (iFieldSeq == 27)
            field = new StringField(this, MESSAGE_DATA_TYPE, 30, null, null);
        if (iFieldSeq == 28)
            field = new XmlField(this, XML_MESSAGE_DATA, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 29)
            field = new MemoField(this, MESSAGE_DATA, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 30)
            field = new StringField(this, ERROR_TEXT, 127, null, null);
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
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ID");
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ReferenceID");
            keyArea.addKeyField(REFERENCE_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(MESSAGE_TIME, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ContactTypeID");
            keyArea.addKeyField(CONTACT_TYPE_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(CONTACT_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(MESSAGE_TIME, DBConstants.ASCENDING);
        }
        if (iKeyArea == 3)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "MessageTime");
            keyArea.addKeyField(MESSAGE_TIME, DBConstants.ASCENDING);
        }
        if (iKeyArea == 4)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Timeout");
            keyArea.addKeyField(MESSAGE_STATUS_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(TIMEOUT_TIME, DBConstants.ASCENDING);
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
        Map<String,Object> properties = ((PropertiesField)this.getField(MessageLog.MESSAGE_HEADER_PROPERTIES)).getProperties();
        BaseMessageHeader messageHeader = null;
        String strMessageHeaderClassName = this.getField(MessageLog.MESSAGE_HEADER_CLASS_NAME).toString();
        String strQueueName = this.getField(MessageLog.MESSAGE_QUEUE_NAME).toString();
        String strQueueType = this.getField(MessageLog.MESSAGE_QUEUE_TYPE).toString();
        Object source = null;
        messageHeader = BaseMessageHeader.createMessageHeader(strMessageHeaderClassName, strQueueName, strQueueType, source, properties);
        if (messageHeader == null)
            messageHeader = new TrxMessageHeader(null, properties);
        if (messageHeader instanceof TrxMessageHeader)
        {
            properties = ((PropertiesField)this.getField(MessageLog.MESSAGE_INFO_PROPERTIES)).getProperties();
            ((TrxMessageHeader)messageHeader).setMessageInfoMap(properties);
            properties = ((PropertiesField)this.getField(MessageLog.MESSAGE_TRANSPORT_PROPERTIES)).getProperties();
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
        String strMessageDataClassName = this.getField(MessageLog.MESSAGE_DATA_CLASS_NAME).toString();
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
        String strMessageClassName = this.getField(MessageLog.MESSAGE_CLASS_NAME).toString();
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
        String strXMLData = this.getField(MessageLog.XML_MESSAGE_DATA).getString();
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
            strProperty = ((PropertiesField)this.getField(MessageLog.MESSAGE_HEADER_PROPERTIES)).getProperty(strKey);
        if (strProperty == null)
            strProperty = ((PropertiesField)this.getField(MessageLog.MESSAGE_INFO_PROPERTIES)).getProperty(strKey);
        if (strProperty == null)
            strProperty = ((PropertiesField)this.getField(MessageLog.MESSAGE_TRANSPORT_PROPERTIES)).getProperty(strKey);
        return strProperty;
    }
    /**
     * Get the data string from the message data from this (XPath) key.
     */
    public String getMessageProperty(String strKey)
    {
        // todo(don) fix this! ((PropertiesField)this.getField(MessageLog.MESSAGE_PROPERTIES)).getProperty(strKey);
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
            this.getField(MessageLog.ID).setString(ID);
            this.setKeyArea(MessageLog.ID_KEY);
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
