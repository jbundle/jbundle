/**
 * @(#)MainControlModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.db.*;

public interface MainControlModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String PACKAGE_NAME = "PackageName";

    public static final String MAIN_CONTROL_FILE = "MainControl";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.MainControl";
    public static final String THICK_CLASS = "org.jbundle.main.db.MainControl";

}
