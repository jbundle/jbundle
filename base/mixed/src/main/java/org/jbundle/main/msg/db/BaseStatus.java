/**
 *  @(#)BaseStatus.
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
import org.jbundle.thin.base.message.*;
import javax.swing.*;

/**
 *  BaseStatus - This is a base record class that contains the status for a value.
The possible status values are: 
0 - NULL_STATUS - Status field is null
1 - NO_STATUS - Status is unknown
2 - LOOKUP_REQUIRED
3 - LOOKUP_SCHEDULED
4 - VALID - Info valid
5 - NOT_VALID
The t.
 */
public class BaseStatus extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kDescription = kVirtualRecordLastField + 1;
    public static final int kIcon = kDescription + 1;
    public static final int kBaseStatusLastField = kIcon;
    public static final int kBaseStatusFields = kIcon - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kDescriptionKey = kIDKey + 1;
    public static final int kBaseStatusLastKey = kDescriptionKey;
    public static final int kBaseStatusKeys = kDescriptionKey - DBConstants.MAIN_KEY_FIELD + 1;
    public static final int NULL_STATUS = 0;
    public static final int NO_STATUS = 1;
    public static final int PROPOSAL = 2;
    public static final int ACCEPTED = 3;
    public static final int CANCELED = 4;
    public static final int VALID = MessageDataDesc.VALID;
    public static final int OKAY = BaseStatus.VALID;
    public static final int NOT_USED = VALID+1;
    public static final int REQUEST_SENT = NOT_USED+1;
    public static final int ERROR = MessageDataDesc.ERROR;
    public static final int DATA_REQUIRED = MessageDataDesc.DATA_REQUIRED;
    public static final int MANUAL_REQUEST_REQUIRED = DATA_REQUIRED+1;
    public static final int MANUAL_REQUEST_SENT = MANUAL_REQUEST_REQUIRED+1;
    public static final int NOT_VALID = MessageDataDesc.NOT_VALID;
    public static final int DATA_VALID = MessageDataDesc.DATA_VALID;
    /**
     * Default constructor.
     */
    public BaseStatus()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseStatus(RecordOwner screen)
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

    public static final String kBaseStatusFile = "BaseStatus";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kBaseStatusFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 20, null, null);
        if (iFieldSeq == kIcon)
            field = new ImageField(this, "Icon", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kBaseStatusLastField)
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
        if (keyArea == null) if (iKeyArea < kBaseStatusLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kBaseStatusLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Is this status waiting for an event to occur?.
     */
    public static boolean isWaiting(int iStatusID)
    {
        switch (iStatusID)
        {
            case REQUEST_SENT:
            case DATA_REQUIRED:
                return true;
            case NULL_STATUS:
            case NO_STATUS:
            case PROPOSAL:
            case ACCEPTED:
            case CANCELED:
            case OKAY:
            case NOT_USED:
            case ERROR:
            case MANUAL_REQUEST_REQUIRED:
            case MANUAL_REQUEST_SENT:
            case NOT_VALID:
            case DATA_VALID:
            default:
                return false;        
        }
    }

}
