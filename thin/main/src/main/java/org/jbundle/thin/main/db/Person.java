/**
 * @(#)Person.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.db.*;

public class Person extends FieldList
    implements PersonModel
{

    //public static final String ID = ID;

    public Person()
    {
        super();
    }
    public Person(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }

}
