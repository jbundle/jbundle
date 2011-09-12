/**
 * @(#)QueueName.
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
import org.jbundle.thin.base.message.*;

/**
 *  QueueName - Message queues.
 */
public class QueueName extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kCode = kVirtualRecordLastField + 1;
    public static final int kName = kCode + 1;
    public static final int kExternalQueueName = kName + 1;
    public static final int kQueueType = kExternalQueueName + 1;
    public static final int kPrivateQueue = kQueueType + 1;
    public static final int kReverseQueueNameID = kPrivateQueue + 1;
    public static final int kQueueNameLastField = kReverseQueueNameID;
    public static final int kQueueNameFields = kReverseQueueNameID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kNameKey = kIDKey + 1;
    public static final int kCodeKey = kNameKey + 1;
    public static final int kExternalQueueNameKey = kCodeKey + 1;
    public static final int kQueueNameLastKey = kExternalQueueNameKey;
    public static final int kQueueNameKeys = kExternalQueueNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public QueueName()
    {
        super();
    }
    /**
     * Constructor.
     */
    public QueueName(RecordOwner screen)
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

    public static final String kQueueNameFile = "QueueName";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kQueueNameFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Queue";
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
        return DBConstants.TABLE;
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
        if (iFieldSeq == kName)
        {
            field = new StringField(this, "Name", 30, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kExternalQueueName)
            field = new StringField(this, "ExternalQueueName", 60, null, null);
        if (iFieldSeq == kQueueType)
            field = new QueueTypeField(this, "QueueType", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kPrivateQueue)
            field = new BooleanField(this, "PrivateQueue", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kReverseQueueNameID)
            field = new QueueNameField(this, "ReverseQueueNameID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kQueueNameLastField)
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
        if (iKeyArea == kNameKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Name");
            keyArea.addKeyField(kName, DBConstants.ASCENDING);
        }
        if (iKeyArea == kCodeKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Code");
            keyArea.addKeyField(kCode, DBConstants.ASCENDING);
        }
        if (iKeyArea == kExternalQueueNameKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ExternalQueueName");
            keyArea.addKeyField(kExternalQueueName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kQueueNameLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kQueueNameLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
