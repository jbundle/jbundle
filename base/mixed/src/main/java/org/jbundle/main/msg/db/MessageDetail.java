/**
 *  @(#)MessageDetail.
 *  Copyright © 2010 tourapp.com. All rights reserved.
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
import org.jbundle.main.db.*;
import org.jbundle.main.msg.screen.*;
import org.jbundle.base.message.trx.message.*;
import org.jbundle.main.msg.db.base.*;

/**
 *  MessageDetail - Message detail.
 */
public class MessageDetail extends PropertiesRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kProperties = kProperties;
    public static final int kContactTypeID = kPropertiesRecordLastField + 1;
    public static final int kPersonID = kContactTypeID + 1;
    public static final int kMessageTransportID = kPersonID + 1;
    public static final int kMessageProcessInfoID = kMessageTransportID + 1;
    public static final int kDestinationSite = kMessageProcessInfoID + 1;
    public static final int kDestinationPath = kDestinationSite + 1;
    public static final int kReturnSite = kDestinationPath + 1;
    public static final int kReturnPath = kReturnSite + 1;
    public static final int kXSLTDocument = kReturnPath + 1;
    public static final int kDefaultMessageVersionID = kXSLTDocument + 1;
    public static final int kDefaultMessageTransportID = kDefaultMessageVersionID + 1;
    public static final int kInitialManualTransportStatusID = kDefaultMessageTransportID + 1;
    public static final int kMessageDetailLastField = kInitialManualTransportStatusID;
    public static final int kMessageDetailFields = kInitialManualTransportStatusID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kContactTypeIDKey = kIDKey + 1;
    public static final int kMessageDetailLastKey = kContactTypeIDKey;
    public static final int kMessageDetailKeys = kContactTypeIDKey - DBConstants.MAIN_KEY_FIELD + 1;
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

    public static final String kMessageDetailFile = "MessageDetail";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kMessageDetailFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) != 0)
            screen = new MessageDetailScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new MessageDetailGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        if (iFieldSeq == kContactTypeID)
            field = new ContactTypeField(this, "ContactTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kPersonID)
            field = new ReferenceField(this, "PersonID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageTransportID)
        {
            field = new MessageTransportField(this, "MessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kMessageProcessInfoID)
            field = new MessageProcessInfoField(this, "MessageProcessInfoID", 60, null, null);
        //if (iFieldSeq == kProperties)
        //  field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kDestinationSite)
        {
            field = new StringField(this, "DestinationSite", 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kDestinationPath)
        {
            field = new StringField(this, "DestinationPath", 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kReturnSite)
        {
            field = new StringField(this, "ReturnSite", 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kReturnPath)
        {
            field = new StringField(this, "ReturnPath", 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kXSLTDocument)
        {
            field = new StringField(this, "XSLTDocument", 127, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kDefaultMessageVersionID)
        {
            field = new MessageVersionField(this, "DefaultMessageVersionID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kDefaultMessageTransportID)
            field = new MessageTransportSelect(this, "DefaultMessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kInitialManualTransportStatusID)
        {
            field = new BaseStatusSelect(this, "InitialManualTransportStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageDetailLastField)
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
        if (iKeyArea == kContactTypeIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ContactTypeID");
            keyArea.addKeyField(kContactTypeID, DBConstants.ASCENDING);
            keyArea.addKeyField(kPersonID, DBConstants.ASCENDING);
            keyArea.addKeyField(kMessageProcessInfoID, DBConstants.ASCENDING);
            keyArea.addKeyField(kMessageTransportID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kMessageDetailLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kMessageDetailLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * AddPropertyListeners Method.
     */
    public void addPropertyListeners()
    {
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.kDestinationSite), TrxMessageHeader.DESTINATION_PARAM);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.kDestinationPath), TrxMessageHeader.DESTINATION_MESSAGE_PARAM);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.kReturnSite), TrxMessageHeader.SOURCE_PARAM);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.kReturnPath), TrxMessageHeader.SOURCE_MESSAGE_PARAM);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.kXSLTDocument), TrxMessageHeader.XSLT_DOCUMENT);
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.kInitialManualTransportStatusID), MessageTransport.INITIAL_MESSAGE_DATA_STATUS); 
        this.addPropertiesFieldBehavior(this.getField(MessageDetail.kDefaultMessageVersionID), TrxMessageHeader.MESSAGE_VERSION_ID);
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
            ContactType recContactType = (ContactType)((ReferenceField)this.getField(MessageDetail.kContactTypeID)).getReferenceRecord(null);
            recContactType = recContactType.getContactType((Record)recMessageDetailTarget);
            if (recContactType == null)
                return trxMessageHeader;    // Just being careful
        
            this.setKeyArea(MessageDetail.kContactTypeIDKey);
            this.getField(MessageDetail.kContactTypeID).moveFieldToThis((BaseField)recContactType.getCounterField());
            this.getField(MessageDetail.kPersonID).moveFieldToThis((BaseField)((Record)recMessageDetailTarget).getCounterField());
            this.getField(MessageDetail.kMessageProcessInfoID).moveFieldToThis((BaseField)recMessageProcessInfo.getCounterField());
            this.getField(MessageDetail.kMessageTransportID).moveFieldToThis((BaseField)recMessageTransport.getCounterField());
            if (this.seek(null))
            {
                Map<String,Object> propHeader = ((PropertiesField)this.getField(MessageDetail.kProperties)).loadProperties();
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
            ContactType recContactType = (ContactType)((ReferenceField)this.getField(MessageDetail.kContactTypeID)).getReferenceRecord(null);
            recContactType = recContactType.getContactType((Record)recMessageDetailTarget);
            if (recContactType == null)
                return null;    // Just being careful
        
            this.setKeyArea(MessageDetail.kContactTypeIDKey);
            this.getField(MessageDetail.kContactTypeID).moveFieldToThis((BaseField)recContactType.getCounterField());
            this.getField(MessageDetail.kPersonID).moveFieldToThis((BaseField)((Record)recMessageDetailTarget).getCounterField());
            this.getField(MessageDetail.kMessageProcessInfoID).moveFieldToThis((BaseField)recMessageProcessInfo.getCounterField());
            this.getField(MessageDetail.kMessageTransportID).setValue(0);
            if (this.seek(">="))
            {
                if (this.getField(MessageDetail.kContactTypeID).equals((BaseField)recContactType.getCounterField()))
                    if (this.getField(MessageDetail.kPersonID).equals((BaseField)((Record)recMessageDetailTarget).getCounterField()))
                        if (this.getField(MessageDetail.kMessageProcessInfoID).equals((BaseField)recMessageProcessInfo.getCounterField()))
                            if (!this.getField(MessageDetail.kDefaultMessageTransportID).isNull())
                                return (MessageTransport)((ReferenceField)this.getField(MessageDetail.kDefaultMessageTransportID)).getReference();
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return null;
    }

}
