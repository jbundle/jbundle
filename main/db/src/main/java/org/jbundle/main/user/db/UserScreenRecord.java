/**
 * @(#)UserScreenRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

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

/**
 *  UserScreenRecord - .
 */
public class UserScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String NAME_SORT = "NameSort";
    public static final String USER_GROUP_ID = "UserGroupID";
    public static final String HEADER_TYPE = "HeaderType";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String SAVEUSER = "saveuser";
    public static final String STATUS_LINE = "StatusLine";
    public static final String CURRENT_PASSWORD = "CurrentPassword";
    public static final String NEW_PASSWORD_1 = "NewPassword1";
    public static final String NEW_PASSWORD_2 = "NewPassword2";
    public static final String TERMS = "Terms";
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

    public static final String USER_SCREEN_RECORD_FILE = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new StringField(this, NAME_SORT, 10, null, null);
        if (iFieldSeq == 1)
            field = new UserGroupFilter(this, USER_GROUP_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 2)
            field = new StringField(this, HEADER_TYPE, 30, null, null);
        if (iFieldSeq == 3)
            field = new StringField(this, USER, 50, null, null);
        if (iFieldSeq == 4)
            field = new PasswordField(this, PASSWORD, 80, null, null);
        if (iFieldSeq == 5)
            field = new BooleanField(this, SAVEUSER, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(true));
        if (iFieldSeq == 6)
            field = new StringField(this, STATUS_LINE, 60, null, null);
        if (iFieldSeq == 7)
        {
            field = new PasswordField(this, CURRENT_PASSWORD, 80, null, null);
            field.setMinimumLength(6);
        }
        if (iFieldSeq == 8)
        {
            field = new PasswordField(this, NEW_PASSWORD_1, 80, null, null);
            field.setMinimumLength(6);
        }
        if (iFieldSeq == 9)
        {
            field = new PasswordField(this, NEW_PASSWORD_2, 80, null, null);
            field.setMinimumLength(6);
        }
        if (iFieldSeq == 10)
            field = new HtmlField(this, TERMS, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
