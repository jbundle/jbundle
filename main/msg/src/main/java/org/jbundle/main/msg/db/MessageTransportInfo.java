/**
 * @(#)MessageTransportInfo.
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
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageTransportInfo - Message Process Transport Detail.
 */
public class MessageTransportInfo extends VirtualRecord
     implements MessageTransportInfoModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kCode = kVirtualRecordLastField + 1;
    public static final int kMessageProcessInfoID = kCode + 1;
    public static final int kMessageTransportID = kMessageProcessInfoID + 1;
    public static final int kMessageVersionID = kMessageTransportID + 1;
    public static final int kActive = kMessageVersionID + 1;
    public static final int kDefaultTransport = kActive + 1;
    public static final int kProperties = kDefaultTransport + 1;
    public static final int kMessageTransportInfoLastField = kProperties;
    public static final int kMessageTransportInfoFields = kProperties - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kMessageProcessInfoIDKey = kIDKey + 1;
    public static final int kMessageTransportInfoLastKey = kMessageProcessInfoIDKey;
    public static final int kMessageTransportInfoKeys = kMessageProcessInfoIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public MessageTransportInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageTransportInfo(RecordOwner screen)
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

    public static final String kMessageTransportInfoFile = "MessageTransportInfo";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kMessageTransportInfoFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            screen = Record.makeNewScreen(MESSAGE_TRANSPORT_INFO_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(MESSAGE_TRANSPORT_INFO_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        if (iFieldSeq == kCode)
            field = new StringField(this, "Code", 30, null, null);
        if (iFieldSeq == kMessageProcessInfoID)
        {
            field = new MessageProcessInfoField(this, "MessageProcessInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kMessageTransportID)
            field = new MessageTransportField(this, "MessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessageVersionID)
            field = new MessageVersionField(this, "MessageVersionID", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(MessageVersion.NO_VERSION));
        if (iFieldSeq == kActive)
            field = new BooleanField(this, "Active", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(true));
        if (iFieldSeq == kDefaultTransport)
            field = new BooleanField(this, "DefaultTransport", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageTransportInfoLastField)
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
        if (iKeyArea == kMessageProcessInfoIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "MessageProcessInfoID");
            keyArea.addKeyField(kMessageProcessInfoID, DBConstants.ASCENDING);
            keyArea.addKeyField(kMessageTransportID, DBConstants.ASCENDING);
            keyArea.addKeyField(kMessageVersionID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kMessageTransportInfoLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kMessageTransportInfoLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Add the properties to this message (info).
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader)
    {
        Map<String,Object> mapHeaderMessageInfo = trxMessageHeader.getMessageInfoMap();
        
        Map<String,Object> propMessageTransportInfo = ((PropertiesField)this.getField(MessageTransportInfo.PROPERTIES)).loadProperties();
        if (mapHeaderMessageInfo != null)
            mapHeaderMessageInfo.putAll(propMessageTransportInfo);
        else
            mapHeaderMessageInfo = propMessageTransportInfo;
        trxMessageHeader.setMessageInfoMap(mapHeaderMessageInfo);
        
        return trxMessageHeader;
    }

}
