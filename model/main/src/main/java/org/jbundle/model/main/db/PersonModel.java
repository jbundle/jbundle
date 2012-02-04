/**
 * @(#)PersonModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.db.*;

public interface PersonModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CODE = "Code";
    public static final String NAME = "Name";
    public static final String ADDRESS_LINE_1 = "AddressLine1";
    public static final String ADDRESS_LINE_2 = "AddressLine2";
    public static final String CITY_OR_TOWN = "CityOrTown";
    public static final String STATE_OR_REGION = "StateOrRegion";
    public static final String POSTAL_CODE = "PostalCode";
    public static final String COUNTRY = "Country";
    public static final String TEL = "Tel";
    public static final String FAX = "Fax";
    public static final String EMAIL = "Email";
    public static final String WEB = "Web";
    public static final String DATE_ENTERED = "DateEntered";
    public static final String DATE_CHANGED = "DateChanged";
    public static final String CHANGED_ID = "ChangedID";
    public static final String COMMENTS = "Comments";
    public static final String USER_ID = "UserID";
    public static final String PASSWORD = "Password";
    public static final String NAME_SORT = "NameSort";
    public static final String POSTAL_CODE_SORT = "PostalCodeSort";

    public static final String CODE_KEY = "Code";

    public static final String PERSON_FILE = "Person";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.Person";
    public static final String THICK_CLASS = "org.jbundle.main.db.Person";

}
