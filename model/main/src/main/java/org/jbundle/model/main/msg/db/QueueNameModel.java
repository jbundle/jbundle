/**
 * @(#)QueueNameModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface QueueNameModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CODE = "Code";
    public static final String NAME = "Name";
    public static final String EXTERNAL_QUEUE_NAME = "ExternalQueueName";
    public static final String QUEUE_TYPE = "QueueType";
    public static final String PRIVATE_QUEUE = "PrivateQueue";
    public static final String REVERSE_QUEUE_NAME_ID = "ReverseQueueNameID";

    public static final String NAME_KEY = "Name";

    public static final String CODE_KEY = "Code";

    public static final String EXTERNAL_QUEUE_NAME_KEY = "ExternalQueueName";

    public static final String QUEUE_NAME_FILE = "QueueName";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.QueueName";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.QueueName";

}
