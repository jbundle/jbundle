/**
 * @(#)MessageDetailModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.main.db.*;

public interface MessageDetailModel extends PropertiesRecordModel
{
    public static final String MESSAGE_DETAIL_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageDetailScreen";
    public static final String MESSAGE_DETAIL_GRID_SCREEN_CLASS = "org.jbundle.main.msg.screen.MessageDetailGridScreen";

    public static final String MESSAGE_DETAIL_FILE = "MessageDetail";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageDetail";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageDetail";

}
