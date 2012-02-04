/**
 * @(#)ContactTypeModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db.base;

import org.jbundle.model.db.*;

public interface ContactTypeModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final String LEVEL = "Level";
    public static final String PARENT_CONTACT_TYPE_ID = "ParentContactTypeID";
    public static final String ICON = "Icon";
    public static final String RECORD_CLASS = "RecordClass";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String CODE_KEY = "Code";

    public static final String CONTACT_TYPE_FILE = "ContactType";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.base.ContactType";
    public static final String THICK_CLASS = "org.jbundle.main.db.base.ContactType";
    /**
     * GetContactTypeFromID Method.
     */
    public String getContactTypeFromID(String strContactTypeID);

}
