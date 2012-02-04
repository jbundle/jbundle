/**
 * @(#)BaseStatus.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db.base;

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
import org.jbundle.thin.base.message.*;
import javax.swing.*;
import org.jbundle.model.main.db.base.*;

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
     implements BaseStatusModel
{
    private static final long serialVersionUID = 1L;

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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(BASE_STATUS_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            field = new StringField(this, DESCRIPTION, 20, null, null);
        if (iFieldSeq == 4)
            field = new ImageField(this, ICON, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Description");
            keyArea.addKeyField(DESCRIPTION, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
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
