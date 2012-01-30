/**
 * @(#)MessageControl.
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
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageControl - Message control information.
 */
public class MessageControl extends ControlRecord
     implements MessageControlModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kProperties = kControlRecordLastField + 1;
    public static final int kWebServicesServer = kProperties + 1;
    public static final int kDefaultVersionID = kWebServicesServer + 1;
    public static final int kBaseNamespace = kDefaultVersionID + 1;
    public static final int kBaseSchemaLocation = kBaseNamespace + 1;
    public static final int kWebMessageTransportID = kBaseSchemaLocation + 1;
    public static final int kMessageControlLastField = kWebMessageTransportID;
    public static final int kMessageControlFields = kWebMessageTransportID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kMessageControlLastKey = kIDKey;
    public static final int kMessageControlKeys = kIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public MessageControl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageControl(RecordOwner screen)
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

    public static final String kMessageControlFile = "MessageControl";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kMessageControlFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.LOCAL | DBConstants.USER_DATA;
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
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kWebServicesServer)
            field = new StringField(this, "WebServicesServer", 128, null, "/ws");
        if (iFieldSeq == kDefaultVersionID)
            field = new MessageVersionField(this, "DefaultVersionID", 20, null, null);
        if (iFieldSeq == kBaseNamespace)
            field = new URLField(this, "BaseNamespace", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kBaseSchemaLocation)
            field = new URLField(this, "BaseSchemaLocation", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kWebMessageTransportID)
            field = new MessageTransportField(this, "WebMessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMessageControlLastField)
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
        if (keyArea == null) if (iKeyArea < kMessageControlLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kMessageControlLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addListeners()
    {
        super.addListeners();
        Map<String,Object> map = Utility.arrayToMap(DESCRIPTIONS);
        ((PropertiesField)this.getField(MessageControl.PROPERTIES)).setMapKeyDescriptions(map);
    }
    /**
     * GetDefaultNamespace Method.
     */
    public String getDefaultNamespace()
    {
        return this.getNamespaceFromVersion(null);
    }
    /**
     * GetNamespaceFromVersion Method.
     */
    public String getNamespaceFromVersion(String version)
    {
        MessageVersion recMessageVersion = this.getMessageVersion(version);
        String namespace = this.getField(MessageControl.BASE_NAMESPACE).toString();
        if (namespace == null)
            namespace = DBConstants.BLANK;
        if ((recMessageVersion != null) && (!recMessageVersion.getField(MessageVersion.NAMESPACE).isNull()))
                namespace += recMessageVersion.getField(MessageVersion.NAMESPACE).toString();
        return namespace;
    }
    /**
     * GetSchemaLocation Method.
     */
    public String getSchemaLocation(String version, String schemaLocation)
    {
        if (schemaLocation != null)
            if ((schemaLocation.startsWith("http")) || (schemaLocation.startsWith("/")))
                return schemaLocation;
        MessageVersion recMessageVersion = this.getMessageVersion(version);
        return this.getSchemaLocation(recMessageVersion, schemaLocation);
    }
    /**
     * GetSchemaLocation Method.
     */
    public String getSchemaLocation(MessageVersion recMessageVersion, String schemaLocation)
    {
        String location = this.getField(MessageControl.BASE_SCHEMA_LOCATION).toString();
        if (location == null)
            location = DBConstants.BLANK;
        else if (!location.endsWith("/"))
            location += "/";
        if ((recMessageVersion != null) && (!recMessageVersion.getField(MessageVersion.SCHEMA_LOCATION).isNull()))
            location = location + recMessageVersion.getField(MessageVersion.SCHEMA_LOCATION).toString();
        else
            location = location + recMessageVersion.getField(MessageVersion.CODE).toString();
        if (location != null)
        {
            if (!location.endsWith("/"))
                location += "/";
            return location + schemaLocation;
        }
        return schemaLocation;
    }
    /**
     * GetMessageVersion Method.
     */
    public MessageVersion getMessageVersion()
    {
        return (MessageVersion)((ReferenceField)this.getField(MessageControl.DEFAULT_VERSION_ID)).getReferenceRecord();
    }
    /**
     * GetMessageVersion Method.
     */
    public MessageVersion getMessageVersion(String version)
    {
        MessageVersion recMessageVersion = this.getMessageVersion();
        recMessageVersion.setKeyArea(MessageVersion.CODE_KEY);
        version = MessageControl.fixVersion(version);
        recMessageVersion.getField(MessageVersion.CODE).setString(version);
        try {
            if (version != null)
                if (recMessageVersion.seek(DBConstants.EQUALS))
            {
                return recMessageVersion;
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        return (MessageVersion)((ReferenceField)this.getField(MessageControl.DEFAULT_VERSION_ID)).getReference();
    }
    /**
     * GetNumericVersionFromVersion Method.
     */
    public String getNumericVersionFromVersion(String version)
    {
        MessageVersion recMessageVersion = this.getMessageVersion(version);
        String numericVersion = DBConstants.BLANK;
        if ((recMessageVersion != null) && (!recMessageVersion.getField(MessageVersion.NAMESPACE).isNull()))
            numericVersion = recMessageVersion.getField(MessageVersion.NUMERIC_VERSION).toString();
        if ((numericVersion == null) || (numericVersion.length() == 0))
            numericVersion = "1.000";
        return numericVersion;
    }
    /**
     * GetIdFromVersion Method.
     */
    public String getIdFromVersion(String version)
    {
        MessageVersion recMessageVersion = this.getMessageVersion(version);
        String idVersion = DBConstants.BLANK;
        if ((idVersion != null) && (!recMessageVersion.getField(MessageVersion.NAMESPACE).isNull()))
            idVersion = recMessageVersion.getField(MessageVersion.VERSION_ID).toString();
        if ((idVersion == null) || (idVersion.length() == 0))
            idVersion = "OTA" + version;
        return idVersion;
    }
    /**
     * FixVersion Method.
     */
    public static String fixVersion(String version)
    {
        if (version != null)
            if (version.length() > 0)
        {
            version = version.toUpperCase();
            if (Character.isLetter(version.charAt(0)))
                version = version.substring(1, version.length()) + version.substring(0, 1); // Move letter to start
        }
        return version;
    }
    /**
     * GetVersionFromSchemaLocation Method.
     */
    public String getVersionFromSchemaLocation(String schemaLocation)
    {
        MessageVersion recMessageVersion = this.getMessageVersion();
        recMessageVersion.close();
        try {
            while (recMessageVersion.hasNext())
            {
                recMessageVersion.next();
                String messageSchemaLocation = this.getSchemaLocation(recMessageVersion, DBConstants.BLANK);
                if (schemaLocation.startsWith(messageSchemaLocation))
                    return recMessageVersion.getField(MessageVersion.CODE).toString();
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        return null;
    }

}
