/**
 * @(#)MessageControl.
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
import org.jbundle.model.main.msg.db.*;

/**
 *  MessageControl - Message control information.
 */
public class MessageControl extends ControlRecord
     implements MessageControlModel
{
    private static final long serialVersionUID = 1L;

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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(MESSAGE_CONTROL_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new StringField(this, WEB_SERVICES_SERVER, 128, null, "/ws");
        if (iFieldSeq == 5)
            field = new MessageVersionField(this, DEFAULT_VERSION_ID, 20, null, null);
        if (iFieldSeq == 6)
            field = new URLField(this, BASE_NAMESPACE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new URLField(this, BASE_SCHEMA_LOCATION, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new MessageTransportField(this, WEB_MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
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
