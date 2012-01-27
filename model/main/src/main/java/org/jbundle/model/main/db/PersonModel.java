/**
 * @(#)PersonModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.db.*;

public interface PersonModel extends Rec
{

    public static final String PERSON_FILE = "Person";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.Person";
    public static final String THICK_CLASS = "org.jbundle.main.db.Person";

}