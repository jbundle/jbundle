/**
 * @(#)Company.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.main.db.*;

public class Company extends Person
    implements CompanyModel
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
