/**
 * @(#)ReptileModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.test.vet.shared.db;

import org.jbundle.model.db.*;

public interface ReptileModel extends Rec
{

    //public static final String ID = ID;
    public static final String REPTILE_TYPE_ID = "ReptileTypeID";
    public static final String EXTRA = "Extra";
    public static final String SPECIAL = "Special";
    public static final String NAME = "Name";
    public static final String VET_ID = "VetID";
    public static final String WEIGHT = "Weight";
    public static final String CLEARANCE = "Clearance";

    public static final String VET_ID_KEY = "VetID";

    public static final String NAME_KEY = "Name";

    public static final String REPTILE_FILE = "Reptile";
    public static final String THIN_CLASS = "org.jbundle.thin.app.test.vet.shared.db.Reptile";
    public static final String THICK_CLASS = "org.jbundle.app.test.vet.shared.db.Reptile";

}
