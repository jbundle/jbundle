/**
 * @(#)MessageVersion.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageVersion - Message version.
 */
public class MessageVersion extends VirtualRecord
     implements MessageVersionModel
{
    private static final long serialVersionUID = 1L;

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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(MESSAGE_VERSION_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            field = new StringField(this, DESCRIPTION, 30, null, null);
        if (iFieldSeq == 4)
            field = new StringField(this, CODE, 20, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, NAMESPACE, 128, null, null);
        if (iFieldSeq == 6)
            field = new StringField(this, SCHEMA_LOCATION, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new StringField(this, NUMERIC_VERSION, 20, null, null);
        if (iFieldSeq == 8)
            field = new StringField(this, VERSION_ID, 20, null, null);
        if (iFieldSeq == 9)
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Code");
            keyArea.addKeyField(CODE, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Description");
            keyArea.addKeyField(DESCRIPTION, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * Add the version properties to this message.
     * NOTE: The properties are added ONLY if the don't currently exist in the info properties.
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader, MessageControl recMessageControl)
    {
        Map<String,Object> mapHeaderMessageInfo = trxMessageHeader.getMessageInfoMap();
        
        Map<String,Object> propMessageTransportInfo = ((PropertiesField)this.getField(MessageVersion.PROPERTIES)).loadProperties();
        if (mapHeaderMessageInfo != null)
            Utility.putAllIfNew(mapHeaderMessageInfo, propMessageTransportInfo);
        else
            mapHeaderMessageInfo = propMessageTransportInfo;
        if (mapHeaderMessageInfo == null)
            mapHeaderMessageInfo = new HashMap<String,Object>();
        if (!this.getField(MessageVersion.CODE).isNull())
            mapHeaderMessageInfo.put(VERSION, this.getField(MessageVersion.CODE).toString());
        if (!this.getField(MessageVersion.ID).isNull())
            mapHeaderMessageInfo.put(VERSION_ID, this.getField(MessageVersion.ID).toString());
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
