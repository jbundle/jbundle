/**
 * @(#)QueueNameModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface QueueNameModel extends Rec
{

    public static final String QUEUE_NAME_FILE = "QueueName";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.QueueName";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.QueueName";

}
