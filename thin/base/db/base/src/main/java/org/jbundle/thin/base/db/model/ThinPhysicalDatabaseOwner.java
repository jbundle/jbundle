/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.model;

import org.jbundle.thin.base.db.mem.base.PDatabase;

public interface ThinPhysicalDatabaseOwner {
    /**
     * Set the ptable that I am an owner of.
     * @param pDatabase
     */
    public void setPDatabase(PDatabase pDatabase);

}
