/**
 * @(#)MessagesModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.db.*;

public interface MessagesModel extends Rec
{

    //public static final String ID = ID;
    public static final String DESCRIPTION = "Description";
    public static final String EMAIL = "Email";
    public static final String USER_ID_FIELD = "UserIDField";

    public static final String MESSAGES_FILE = "Messages";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.Messages";
    public static final String THICK_CLASS = "org.jbundle.main.db.Messages";

}
