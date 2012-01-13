/**
 * @(#)ProcessTypeModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface ProcessTypeModel extends Rec
{
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final String UPDATE = "UPDATE";
    public static final String INFO = "INFO";

    public static final String PROCESS_TYPE_FILE = "ProcessType";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.ProcessType";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.ProcessType";

}
