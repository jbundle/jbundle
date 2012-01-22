/**
 * @(#)MessageVersion.
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
 *  MessageVersion - Message version.
 */
public class MessageVersion extends VirtualRecord
     implements MessageVersionModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kDescription = kVirtualRecordLastField + 1;
    public static final int kCode = kDescription + 1;
    public static final int kNamespace = kCode + 1;
    public static final int kSchemaLocation = kNamespace + 1;
    public static final int kNumericVersion = kSchemaLocation + 1;
    public static final int kVersionID = kNumericVersion + 1;
    public static final int kProperties = kVersionID + 1;
    public static final int kMessageVersionLastField = kProperties;
    public static final int kMessageVersionFields = kProperties - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kCodeKey = kIDKey + 1;
    public static final int kDescriptionKey = kCodeKey + 1;
    public static final int kMessageVersionLastKey = kDescriptionKey;
    public static final int kMessageVersionKeys = kDescriptionKey - DBConstants.MAIN_KEY_FIELD + 1;
    public static final int NO_VERSION = 0;
    public static final String VERSION_ID = "messageVersionID"; //TrxMessageHeader.MESSAGE_VERSION_ID;
    /**
     * Default constructor.
     */
    public MessageVersion()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageVersion(RecordOwner screen)
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

    public static final String kMessageVersionFile = "MessageVersion";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kMessageVersionFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.TABLE | DBConstants.SHARED_DATA;
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
            field = new StringField(this, "Code", 20, null, null);
        if (iFieldSeq == kNamespace)
            field = new StringField(this, "Namespace", 128, null, null);
        if (iFieldSeq == kSchemaLocation)
            field = new StringField(this, "SchemaLocation", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kNumericVersion)
            field = new StringField(this, "NumericVersion", 20, null, null);
        if (iFieldSeq == kVersionID)
            field = new StringField(this, "VersionID", 20, null, null);
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageVersionLastField)
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
        if (iKeyArea == kCodeKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Code");
            keyArea.addKeyField(kCode, DBConstants.ASCENDING);
        }
        if (iKeyArea == kDescriptionKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Description");
            keyArea.addKeyField(kDescription, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kMessageVersionLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kMessageVersionLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Add the version properties to this message.
     * NOTE: The properties are added ONLY if the don't currently exist in the info properties.
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader, MessageControl recMessageControl)
    {
        Map<String,Object> mapHeaderMessageInfo = trxMessageHeader.getMessageInfoMap();
        
        Map<String,Object> propMessageTransportInfo = ((PropertiesField)this.getField(MessageVersion.kProperties)).loadProperties();
        if (mapHeaderMessageInfo != null)
            Utility.putAllIfNew(mapHeaderMessageInfo, propMessageTransportInfo);
        else
            mapHeaderMessageInfo = propMessageTransportInfo;
        if (mapHeaderMessageInfo == null)
            mapHeaderMessageInfo = new HashMap<String,Object>();
        if (!this.getField(MessageVersion.kCode).isNull())
            mapHeaderMessageInfo.put(VERSION, this.getField(MessageVersion.kCode).toString());
        if (!this.getField(MessageVersion.kID).isNull())
            mapHeaderMessageInfo.put(VERSION_ID, this.getField(MessageVersion.kID).toString());
        if (recMessageControl != null)
            if ((recMessageControl.getEditMode() == DBConstants.EDIT_CURRENT) || (recMessageControl.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                if (mapHeaderMessageInfo.get(TrxMessageHeader.SCHEMA_LOCATION) != null)
            {
                String strSchemaLocation = (String)mapHeaderMessageInfo.get(TrxMessageHeader.SCHEMA_LOCATION);
                if (strSchemaLocation.indexOf('/') == -1)
                {   // Need to get the full location
                    if (strSchemaLocation.indexOf('.') == -1)
                        strSchemaLocation = strSchemaLocation + ".xsd";
                strSchemaLocation = recMessageControl.getSchemaLocation(this, strSchemaLocation);
                mapHeaderMessageInfo.put(TrxMessageHeader.SCHEMA_LOCATION, strSchemaLocation);
                }
            }
        trxMessageHeader.setMessageInfoMap(mapHeaderMessageInfo);
        
        return trxMessageHeader;
    }

}
