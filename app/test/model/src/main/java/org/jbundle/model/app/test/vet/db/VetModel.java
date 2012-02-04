/**
 * @(#)VetModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.test.vet.db;

import org.jbundle.model.db.*;

public interface VetModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String NAME = "Name";
    public static final String FAVORITE_ANIMAL = "FavoriteAnimal";

    public static final String NAME_KEY = "Name";

    public static final String VET_FILE = "Vet";
    public static final String THIN_CLASS = "org.jbundle.thin.app.test.vet.db.Vet";
    public static final String THICK_CLASS = "org.jbundle.app.test.vet.db.Vet";

}
