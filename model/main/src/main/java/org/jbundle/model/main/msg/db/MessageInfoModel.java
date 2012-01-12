/**
 * @(#)MessageInfoModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

public interface MessageInfoModel extends org.jbundle.model.db.Rec
{
    public static final String MESSAGE_INFO_TYPE_ID = "MessageInfoTypeID";
    public static final String CONTACT_TYPE_ID = "ContactTypeID";

    public static final String MESSAGE_INFO_FILE = "MessageInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageInfo";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageInfo";

}
