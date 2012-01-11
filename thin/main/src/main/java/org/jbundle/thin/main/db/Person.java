/**
 * @(#)Person.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class Person extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.main.db.PersonModel
{

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
