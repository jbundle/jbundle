/**
 * @(#)CatModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.test.vet.db;

import org.jbundle.model.app.test.vet.db.*;

public interface CatModel extends AnimalModel
{
    public static final String CAT_VET_SCREEN_CLASS = "org.jbundle.app.test.vet.screen.CatVetScreen";
    public static final String CAT_VET_GRID_SCREEN_CLASS = "org.jbundle.app.test.vet.screen.CatVetGridScreen";

    public static final String CAT_FILE = "Cat";
    public static final String THIN_CLASS = "org.jbundle.thin.app.test.vet.db.Cat";
    public static final String THICK_CLASS = "org.jbundle.app.test.vet.db.Cat";

}
