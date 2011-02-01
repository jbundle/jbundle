/**
 *  @(#)UserLog.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.main.user.db;

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
import org.jbundle.main.msg.db.*;

/**
 *  UserLog - User log.
 */
public class UserLog extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kUserID = kVirtualRecordLastField + 1;
    public static final int kLogTime = kUserID + 1;
    public static final int kMessage = kLogTime + 1;
    public static final int kUserLogTypeID = kMessage + 1;
    public static final int kUserLogLastField = kUserLogTypeID;
    public static final int kUserLogFields = kUserLogTypeID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kUserIDKey = kIDKey + 1;
    public static final int kUserLogLastKey = kUserIDKey;
    public static final int kUserLogKeys = kUserIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public UserLog()
    {
        super();
    }
    /**
     * Constructor.
     */
    public UserLog(RecordOwner screen)
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

    public static final String kUserLogFile = "UserLog";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kUserLogFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.REMOTE | DBConstants.USER_DATA | DBConstants.SERVER_REWRITES | DBConstants.DONT_LOG_TRX;
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
        if (iFieldSeq == kUserID)
            field = new UserField(this, "UserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kLogTime)
            field = new UserLog_LogTime(this, "LogTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMessage)
            field = new StringField(this, "Message", 127, null, null);
        if (iFieldSeq == kUserLogTypeID)
            field = new MessageTypeField(this, "UserLogTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kUserLogLastField)
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
        if (iKeyArea == kUserIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "UserID");
            keyArea.addKeyField(kUserID, DBConstants.ASCENDING);
            keyArea.addKeyField(kLogTime, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kUserLogLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kUserLogLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Add this log entry.
     */
    public void log(int iUserID, int iUserLogTypeID, String strMessage)
    {
        try {
            this.addNew();
            this.getField(UserLog.kUserID).setValue(iUserID);
            this.getField(UserLog.kUserLogTypeID).setValue(iUserLogTypeID);
            this.getField(UserLog.kMessage).setString(strMessage);
            this.add();
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }

}
