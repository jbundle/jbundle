/**
 * @(#)AnimalModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.test.vet.db;

import org.jbundle.model.db.*;

public interface AnimalModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String NAME = "Name";
    public static final String COLOR = "Color";
    public static final String WEIGHT = "Weight";
    public static final String VET = "Vet";

    public static final String NAME_KEY = "Name";

    public static final String VET_KEY = "Vet";
    public static final String ANIMAL_GRID_SCREEN_CLASS = "org.jbundle.app.test.vet.screen.AnimalGridScreen";

    public static final String ANIMAL_FILE = "Animal";
    public static final String THIN_CLASS = "org.jbundle.thin.app.test.vet.db.Animal";
    public static final String THICK_CLASS = "org.jbundle.app.test.vet.db.Animal";

}
