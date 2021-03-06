/**
 * @(#)RequestTypeModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface RequestTypeModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CODE_KEY = "Code";
    public static final String MANUAL = "MANUAL";
    public static final String INFORMATION = "InfoStatusID";
    public static final String AVAILABILITY = "InventoryStatusID";
    public static final String PRICE = "CostStatusID";
    public static final String BOOKING = "ProductStatusID";
    public static final String ERROR = "Error";

    public static final String REQUEST_TYPE_FILE = "RequestType";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.RequestType";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.RequestType";

}
