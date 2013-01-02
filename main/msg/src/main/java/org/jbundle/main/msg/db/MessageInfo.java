/**
 * @(#)MessageInfo.
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
import org.jbundle.util.osgi.finder.*;
import org.jbundle.model.main.db.*;
import org.jbundle.main.db.base.*;
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageInfo - Message information.
 */
public class MessageInfo extends VirtualRecord
     implements MessageInfoModel
{
    private static final long serialVersionUID = 1L;

    public static final int PROCESS_DETAIL_MODE = (ScreenConstants.LAST_MODE * 4) | ScreenConstants.DETAIL_MODE;
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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(MESSAGE_INFO_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        if ((iDocMode & ScreenConstants.DOC_MODE_MASK) == PROCESS_DETAIL_MODE)
            screen = Record.makeNewScreen(MessageProcessInfo.MESSAGE_PROCESS_INFO_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(MESSAGE_INFO_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = Record.makeNewScreen(MESSAGE_INFO_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MENU_MODE) != 0)
            screen = Record.makeNewScreen(MenusModel.MENU_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
            field = new StringField(this, DESCRIPTION, 50, null, null);
        if (iFieldSeq == 4)
            field = new StringField(this, CODE, 30, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, MESSAGE_CLASS, 127, null, null);
        if (iFieldSeq == 6)
            field = new PropertiesField(this, MESSAGE_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
        {
            field = new MessageInfoTypeField(this, MESSAGE_INFO_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(1));
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 8)
            field = new MessageInfoField(this, REVERSE_MESSAGE_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 9)
            field = new ContactTypeField(this, CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new RequestTypeField(this, REQUEST_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        if (iKeyArea == 3)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, MESSAGE_INFO_TYPE_ID_KEY);
            keyArea.addKeyField(MESSAGE_INFO_TYPE_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(CONTACT_TYPE_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(REQUEST_TYPE_ID, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
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
        BaseField fldProperties = this.getField(MessageInfo.MESSAGE_PROPERTIES);
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
        Map<String, Object> propMessageInfo = ((PropertiesField)this.getField(MessageInfo.MESSAGE_PROPERTIES)).loadProperties();
        propMessageInfo.put(TrxMessageHeader.INTERNAL_MESSAGE_CLASS, this.getField(MessageInfo.MESSAGE_CLASS).toString());
        
        MessageInfoType recMessageInfoType = (MessageInfoType)((ReferenceField)this.getField(MessageInfo.MESSAGE_INFO_TYPE_ID)).getReference();
        if (recMessageInfoType != null)
            propMessageInfo.put(TrxMessageHeader.MESSAGE_INFO_TYPE, recMessageInfoType.getField(MessageInfoType.CODE).toString());
        String schemaLocation = (String)propMessageInfo.get(SCHEMA_LOCATION);
        if (schemaLocation == null)
            schemaLocation = this.getField(MessageInfo.CODE).toString();
        if (schemaLocation != null)
            propMessageInfo.put(SCHEMA_LOCATION, schemaLocation);
        
        Record recRequestType = ((ReferenceField)this.getField(MessageInfo.REQUEST_TYPE_ID)).getReference();
        if ((recRequestType != null) && (recRequestType.getEditMode() == DBConstants.EDIT_CURRENT))
            propMessageInfo.put(TrxMessageHeader.MESSAGE_REQUEST_TYPE, recRequestType.getField(RequestType.CODE).toString());
        
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
        String strClassName = this.getField(MessageInfo.MESSAGE_CLASS).toString();
        messageData = (MessageRecordDesc)ClassServiceUtility.getClassService().makeObjectFromClassName(strClassName);
        if (messageData != null)
               messageData.init(message, strKey);
        return messageData;
    }

}
