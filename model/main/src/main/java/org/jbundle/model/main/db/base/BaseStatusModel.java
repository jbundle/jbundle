/**
 * @(#)BaseStatusModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db.base;

import org.jbundle.model.db.*;

public interface BaseStatusModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String ICON = "Icon";

    public static final String DESCRIPTION_KEY = "Description";
    public static final int NULL_STATUS = 0;
    public static final int NO_STATUS = 1;
    public static final int PROPOSAL = 2;
    public static final int ACCEPTED = 3;
    public static final int CANCELED = 4;
    public static final int VALID = 5; // MessageDataDesc.VALID;
    public static final int OKAY = VALID;
    public static final int NOT_USED = VALID+1;
    public static final int REQUEST_SENT = NOT_USED+1;
    public static final int ERROR = 8; //MessageDataDesc.ERROR;
    public static final int DATA_REQUIRED = 9; //MessageDataDesc.DATA_REQUIRED;
    public static final int MANUAL_REQUEST_REQUIRED = DATA_REQUIRED+1;
    public static final int MANUAL_REQUEST_SENT = MANUAL_REQUEST_REQUIRED+1;
    public static final int NOT_VALID = 12; //MessageDataDesc.NOT_VALID;
    public static final int DATA_VALID = 13; //MessageDataDesc.DATA_VALID;

    public static final String BASE_STATUS_FILE = "BaseStatus";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.base.BaseStatus";
    public static final String THICK_CLASS = "org.jbundle.main.db.base.BaseStatus";

}
