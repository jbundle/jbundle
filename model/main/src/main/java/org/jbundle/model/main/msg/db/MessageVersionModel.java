/**
 * @(#)MessageVersionModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface MessageVersionModel extends Rec
{

    //public static final String ID = ID;
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final String NAMESPACE = "Namespace";
    public static final String SCHEMA_LOCATION = "SchemaLocation";
    public static final String NUMERIC_VERSION = "NumericVersion";
    public static final String VERSION_ID = "VersionID";
    public static final String PROPERTIES = "Properties";

    public static final String CODE_KEY = "Code";

    public static final String DESCRIPTION_KEY = "Description";
    public static final String VERSION = "messageVersion"; //TrxMessageHeader.MESSAGE_VERSION;

    public static final String MESSAGE_VERSION_FILE = "MessageVersion";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageVersion";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageVersion";

}
