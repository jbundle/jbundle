/**
 * @(#)ContactTypeModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db.base;

public interface ContactTypeModel extends org.jbundle.model.db.Rec
{
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final String CODE_KEY = "Code";

    public static final String CONTACT_TYPE_FILE = "ContactType";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.base.ContactType";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.base.ContactType";
    /**
     * GetContactTypeFromID Method.
     */
    public String getContactTypeFromID(String strContactTypeID);

}
