/**
 * @(#)Company.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class Company extends org.jbundle.thin.main.db.Person
{

    public Company()
    {
        super();
    }
    public Company(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }

}
