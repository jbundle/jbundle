/**
 * @(#)MessageInfo.
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
import org.jbundle.base.message.trx.message.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.main.msg.screen.*;
import org.jbundle.util.osgi.finder.*;
import org.jbundle.main.msg.db.base.*;
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageInfo - Message information.
 */
public class MessageInfo extends VirtualRecord
     implements MessageInfoModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kDescription = kVirtualRecordLastField + 1;
    public static final int kCode = kDescription + 1;
    public static final int kMessageClass = kCode + 1;
    public static final int kMessageProperties = kMessageClass + 1;
    public static final int kMessageInfoTypeID = kMessageProperties + 1;
    public static final int kReverseMessageInfoID = kMessageInfoTypeID + 1;
    public static final int kContactTypeID = kReverseMessageInfoID + 1;
    public static final int kRequestTypeID = kContactTypeID + 1;
    public static final int kMessageInfoLastField = kRequestTypeID;
    public static final int kMessageInfoFields = kRequestTypeID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kDescriptionKey = kIDKey + 1;
    public static final int kCodeKey = kDescriptionKey + 1;
    public static final int kMessageInfoTypeIDKey = kCodeKey + 1;
    public static final int kMessageInfoLastKey = kMessageInfoTypeIDKey;
    public static final int kMessageInfoKeys = kMessageInfoTypeIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    public static final int PROCESS_DETAIL_MODE = ScreenConstants.LAST_MODE * 4;
    public static final int TRANSPORT_DETAIL_MODE = ScreenConstants.LAST_MODE * 2;
    public static final String ELEMENT = "ota.element";
    public static final String SCHEMA_LOCATION = TrxMessageHeader.SCHEMA_LOCATION;
    public static final String NAMESPACE = "namespace";
    /**
     * Default constructor.
     */
    public MessageInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageInfo(RecordOwner screen)
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

    public static final String kMessageInfoFile = "MessageInfo";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kMessageInfoFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = new MessageInfoScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new MessageInfoGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & MessageInfo.PROCESS_DETAIL_MODE) == MessageInfo.PROCESS_DETAIL_MODE)
            screen = new MessageProcessInfoGridScreen(this, null, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 50, null, null);
        if (iFieldSeq == kCode)
            field = new StringField(this, "Code", 30, null, null);
        if (iFieldSeq == kMessageClass)
            field = new StringField(this, "MessageClass", 127, null, null);
        if (iFieldSeq == kMessageProperties)
            field = new PropertiesField(this, "MessageProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageInfoTypeID)
        {
            field = new MessageInfoTypeField(this, "MessageInfoTypeID", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(1));
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kReverseMessageInfoID)
            field = new MessageInfoField(this, "ReverseMessageInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kContactTypeID)
            field = new ContactTypeField(this, "ContactTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kRequestTypeID)
            field = new RequestTypeField(this, "RequestTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageInfoLastField)
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
        if (iKeyArea == kMessageInfoTypeIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "MessageInfoTypeID");
            keyArea.addKeyField(kMessageInfoTypeID, DBConstants.ASCENDING);
            keyArea.addKeyField(kContactTypeID, DBConstants.ASCENDING);
            keyArea.addKeyField(kRequestTypeID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kMessageInfoLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kMessageInfoLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Convert the command to the screen document type.
     * @param strCommand The command text.
     * @param The standard document type (MAINT/DISPLAY/SELECT/MENU/etc).
     */
    public int commandToDocType(String strCommand)
    {
        if (MessageInfo.PROCESS_DETAIL_SCREEN.equalsIgnoreCase(strCommand))
            return MessageInfo.PROCESS_DETAIL_MODE;
        return super.commandToDocType(strCommand);
    }
    /**
     * AddPropertiesFieldBehavior Method.
     */
    public void addPropertiesFieldBehavior(BaseField fldDisplay, String strProperty)
    {
        BaseField fldProperties = this.getField(MessageInfo.kMessageProperties);
        FieldListener listener = new CopyConvertersHandler(new PropertiesConverter(fldProperties, strProperty));
        listener.setRespondsToMode(DBConstants.INIT_MOVE, false);
        listener.setRespondsToMode(DBConstants.READ_MOVE, false);
        fldDisplay.addListener(listener);
        listener = new CopyConvertersHandler(fldDisplay, new PropertiesConverter(fldProperties, strProperty));
        listener.setRespondsToMode(DBConstants.SCREEN_MOVE, false);
        fldProperties.addListener(listener);
    }
    /**
     * add the properties to this message (info).
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader)
    {
        Map<String, Object> mapHeaderMessageInfo = trxMessageHeader.getMessageInfoMap();
        Map<String, Object> propMessageInfo = ((PropertiesField)this.getField(MessageInfo.kMessageProperties)).loadProperties();
        propMessageInfo.put(TrxMessageHeader.INTERNAL_MESSAGE_CLASS, this.getField(MessageInfo.kMessageClass).toString());
        
        MessageInfoType recMessageInfoType = (MessageInfoType)((ReferenceField)this.getField(MessageInfo.kMessageInfoTypeID)).getReference();
        if (recMessageInfoType != null)
            propMessageInfo.put(TrxMessageHeader.MESSAGE_INFO_TYPE, recMessageInfoType.getField(MessageInfoType.kCode).toString());
        String schemaLocation = (String)propMessageInfo.get(SCHEMA_LOCATION);
        if (schemaLocation == null)
            schemaLocation = this.getField(MessageInfo.kCode).toString();
        if (schemaLocation != null)
            propMessageInfo.put(SCHEMA_LOCATION, schemaLocation);
        
        Record recRequestType = ((ReferenceField)this.getField(MessageInfo.kRequestTypeID)).getReference();
        if ((recRequestType != null) && (recRequestType.getEditMode() == DBConstants.EDIT_CURRENT))
            propMessageInfo.put(TrxMessageHeader.MESSAGE_REQUEST_TYPE, recRequestType.getField(RequestType.kCode).toString());
        
        if (mapHeaderMessageInfo != null)
            mapHeaderMessageInfo.putAll(propMessageInfo);
        else
            mapHeaderMessageInfo = propMessageInfo;
        trxMessageHeader.setMessageInfoMap(mapHeaderMessageInfo);
        
        return trxMessageHeader;
    }
    /**
     * Create the message that this record describes
     * (in the classname field)
     * @returns The message or null if error.
     */
    public MessageRecordDesc createNewMessage(BaseMessage message, String strKey)
    {
        MessageRecordDesc messageData = null;
        String strClassName = this.getField(MessageInfo.kMessageClass).toString();
        messageData = (MessageRecordDesc)ClassServiceUtility.getClassService().makeObjectFromClassName(strClassName);
        if (messageData != null)
               messageData.init(message, strKey);
        return messageData;
    }

}
