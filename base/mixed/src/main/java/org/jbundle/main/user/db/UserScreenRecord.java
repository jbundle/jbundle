/**
 *  @(#)UserScreenRecord.
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

/**
 *  UserScreenRecord - .
 */
public class UserScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kNameSort = kScreenRecordLastField + 1;
    public static final int kUserGroupID = kNameSort + 1;
    public static final int kHeaderType = kUserGroupID + 1;
    public static final int kuser = kHeaderType + 1;
    public static final int kpassword = kuser + 1;
    public static final int ksaveuser = kpassword + 1;
    public static final int kStatusLine = ksaveuser + 1;
    public static final int kCurrentPassword = kStatusLine + 1;
    public static final int kNewPassword1 = kCurrentPassword + 1;
    public static final int kNewPassword2 = kNewPassword1 + 1;
    public static final int kTerms = kNewPassword2 + 1;
    public static final int kUserScreenRecordLastField = kTerms;
    public static final int kUserScreenRecordFields = kTerms - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public UserScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public UserScreenRecord(RecordOwner screen)
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

    public static final String kUserScreenRecordFile = null;    // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kNameSort)
            field = new StringField(this, "NameSort", 10, null, null);
        if (iFieldSeq == kUserGroupID)
            field = new UserGroupFilter(this, "UserGroupID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kHeaderType)
            field = new StringField(this, "HeaderType", 30, null, null);
        if (iFieldSeq == kuser)
            field = new StringField(this, "user", 50, null, null);
        if (iFieldSeq == kpassword)
            field = new PasswordField(this, "password", 80, null, null);
        if (iFieldSeq == ksaveuser)
            field = new BooleanField(this, "saveuser", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(true));
        if (iFieldSeq == kStatusLine)
            field = new StringField(this, "StatusLine", 60, null, null);
        if (iFieldSeq == kCurrentPassword)
        {
            field = new PasswordField(this, "CurrentPassword", 80, null, null);
            field.setMinimumLength(6);
        }
        if (iFieldSeq == kNewPassword1)
        {
            field = new PasswordField(this, "NewPassword1", 80, null, null);
            field.setMinimumLength(6);
        }
        if (iFieldSeq == kNewPassword2)
        {
            field = new PasswordField(this, "NewPassword2", 80, null, null);
            field.setMinimumLength(6);
        }
        if (iFieldSeq == kTerms)
            field = new HtmlField(this, "Terms", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kUserScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
