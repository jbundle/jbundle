/**
 * @(#)MessageDetail.
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
import org.jbundle.main.db.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.main.db.base.*;
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageDetail - Message detail.
 */
public class MessageDetail extends PropertiesRecord
     implements MessageDetailModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public MessageDetail()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageDetail(RecordOwner screen)
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
        return (m_tableName == null) ? Record.formatTableNames(MESSAGE_DETAIL_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Message detail specifications";
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
     * Make a default screen.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(MESSAGE_DETAIL_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(MESSAGE_DETAIL_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        //if (iFieldSeq == 3)
        //  field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new ContactTypeField(this, CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new ReferenceField(this, PERSON_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 6)
        {
            field = new MessageTransportField(this, MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 7)
            field = new MessageProcessInfoField(this, MESSAGE_PROCESS_INFO_ID, 60, null, null);
        if (iFieldSeq == 8)
        {
            field = new StringField(this, DESTINATION_SITE, 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 9)
        {
            field = new StringField(this, DESTINATION_PATH, 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 10)
        {
            field = new StringField(this, RETURN_SITE, 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 11)
        {
            field = new StringField(this, RETURN_PATH, 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 12)
        {
            field = new StringField(this, XSLT_DOCUMENT, 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 13)
        {
            field = new MessageVersionField(this, DEFAULT_MESSAGE_VERSION_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 14)
            field = new MessageTransportSelect(this, DEFAULT_MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 15)
        {
            field = new BaseStatusSelect(this, INITIAL_MANUAL_TRANSPORT_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
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
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ContactTypeID");
            keyArea.addKeyField(CONTACT_TYPE_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(PERSON_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(MESSAGE_PROCESS_INFO_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(MESSAGE_TRANSPORT_ID, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * AddPropertyListeners Method.
     */
    public void addPropertyListeners()
    {
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.DESTINATION_SITE), TrxMessageHeader.DESTINATION_PARAM);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.DESTINATION_PATH), TrxMessageHeader.DESTINATION_MESSAGE_PARAM);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.RETURN_SITE), TrxMessageHeader.SOURCE_PARAM);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.RETURN_PATH), TrxMessageHeader.SOURCE_MESSAGE_PARAM);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.XSLT_DOCUMENT), TrxMessageHeader.XSLT_DOCUMENT);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.INITIAL_MANUAL_TRANSPORT_STATUS_ID), MessageTransport.INITIAL_MESSAGE_DATA_STATUS); 
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.DEFAULT_MESSAGE_VERSION_ID), TrxMessageHeader.MESSAGE_VERSION_ID);
    }
    /**
     * Get the message properties for this vendor.
     * @param strMessageName The message name.
     * @return A map with the message properties.
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader, MessageDetailTarget recMessageDetailTarget, MessageProcessInfo recMessageProcessInfo, MessageTransport recMessageTransport)
    {
        try {
            if (trxMessageHeader == null)
                trxMessageHeader = new TrxMessageHeader(null, null);
            ContactType recContactType = (ContactType)((ReferenceField)this.getField(MessageDetail.CONTACT_TYPE_ID)).getReferenceRecord(null);
            recContactType = (ContactType)recContactType.getContactType((Record)recMessageDetailTarget);
            if (recContactType == null)
                return trxMessageHeader;    // Just being careful
        
            this.setKeyArea(MessageDetail.CONTACT_TYPE_ID_KEY);
            this.getField(MessageDetail.CONTACT_TYPE_ID).moveFieldToThis((BaseField)recContactType.getCounterField());
            this.getField(MessageDetail.PERSON_ID).moveFieldToThis((BaseField)((Record)recMessageDetailTarget).getCounterField());
            this.getField(MessageDetail.MESSAGE_PROCESS_INFO_ID).moveFieldToThis((BaseField)recMessageProcessInfo.getCounterField());
            this.getField(MessageDetail.MESSAGE_TRANSPORT_ID).moveFieldToThis((BaseField)recMessageTransport.getCounterField());
            if (this.seek(null))
            {
                Map<String,Object> propHeader = ((PropertiesField)this.getField(MessageDetail.PROPERTIES)).loadProperties();
                if (propHeader == null)
                    propHeader = new HashMap<String,Object>(); // Never return null.
                Map<String,Object> map = trxMessageHeader.getMessageHeaderMap();
                if (map != null)
                    map.putAll(propHeader);
                else
                    map = propHeader;
                trxMessageHeader.setMessageHeaderMap(map);
                if ((recMessageTransport != null)
                    && ((recMessageTransport.getEditMode() == DBConstants.EDIT_CURRENT) || (recMessageTransport.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
                {
                    trxMessageHeader = recMessageTransport.addMessageProperties(trxMessageHeader);
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        // No need to free the two files as they are linked to the fields in this record
        
        return trxMessageHeader;
    }
    /**
     * Get the default message transport for this target/process.
     */
    public MessageTransport getDefaultMessageTransport(MessageDetailTarget recMessageDetailTarget, MessageProcessInfo recMessageProcessInfo)
    {
        try {
            ContactType recContactType = (ContactType)((ReferenceField)this.getField(MessageDetail.CONTACT_TYPE_ID)).getReferenceRecord(null);
            recContactType = (ContactType)recContactType.getContactType((Record)recMessageDetailTarget);
            if (recContactType == null)
                return null;    // Just being careful
        
            this.setKeyArea(MessageDetail.CONTACT_TYPE_ID_KEY);
            this.getField(MessageDetail.CONTACT_TYPE_ID).moveFieldToThis((BaseField)recContactType.getCounterField());
            this.getField(MessageDetail.PERSON_ID).moveFieldToThis((BaseField)((Record)recMessageDetailTarget).getCounterField());
            this.getField(MessageDetail.MESSAGE_PROCESS_INFO_ID).moveFieldToThis((BaseField)recMessageProcessInfo.getCounterField());
            this.getField(MessageDetail.MESSAGE_TRANSPORT_ID).setValue(0);
            if (this.seek(">="))
            {
                if (this.getField(MessageDetail.CONTACT_TYPE_ID).equals((BaseField)recContactType.getCounterField()))
                    if (this.getField(MessageDetail.PERSON_ID).equals((BaseField)((Record)recMessageDetailTarget).getCounterField()))
                        if (this.getField(MessageDetail.MESSAGE_PROCESS_INFO_ID).equals((BaseField)recMessageProcessInfo.getCounterField()))
                            if (!this.getField(MessageDetail.DEFAULT_MESSAGE_TRANSPORT_ID).isNull())
                                return (MessageTransport)((ReferenceField)this.getField(MessageDetail.DEFAULT_MESSAGE_TRANSPORT_ID)).getReference();
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return null;
    }

}
