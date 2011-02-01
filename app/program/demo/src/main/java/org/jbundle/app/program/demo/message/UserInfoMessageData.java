/**
 *  @(#)UserInfoMessageData.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.app.program.demo.message;

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
import org.jbundle.main.user.db.*;

/**
 *  UserInfoMessageData - .
 */
public class UserInfoMessageData extends MessageRecordDesc
{
    /**
     * Default constructor.
     */
    public UserInfoMessageData()
    {
        super();
    }
    /**
     * UserInfoMessageData Method.
     */
    public UserInfoMessageData(MessageDataParent messageDataParent, String strKey)
    {
        this();
        this.init(messageDataParent, strKey);
    }
    /**
     * Init Method.
     */
    public void init(MessageDataParent messageDataParent, String strKey)
    {
        if (strKey == null)
            strKey = UserInfo.kUserInfoFile;
        super.init(messageDataParent, strKey);
    }
    /**
     * Setup sub-Message Data.
     */
    public void setupMessageDataDesc()
    {
        super.setupMessageDataDesc();
        
        this.addMessageFieldDesc(UserInfo.USER_NAME, String.class, MessageFieldDesc.REQUIRED, null);
        this.addMessageFieldDesc(UserInfo.PASSWORD, String.class, MessageFieldDesc.REQUIRED, null);
    }
    /**
     * Move the map values to the correct record fields.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int getRawRecordData(FieldList record)
    {
        int iErrorCode = super.getRawRecordData(record);
        this.getRawFieldData(record.getField(UserInfo.USER_NAME));
        this.getRawFieldData(record.getField(UserInfo.PASSWORD));
        return iErrorCode;
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     */
    public int putRawRecordData(FieldList record)
    {
        int iErrorCode = super.putRawRecordData(record);
        this.putRawFieldData(record.getField(UserInfo.USER_NAME));
        this.putRawFieldData(record.getField(UserInfo.PASSWORD));
        return iErrorCode;
    }

}
