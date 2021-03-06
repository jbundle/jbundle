/*
 * 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.model;


/**
 * @author don
 *
 */
public interface ThinPhysicalTable {
    /**
     * Bump the use count.
     * This doesn't have to be synchronized because getPTable in PDatabase is.
     * @param pTableOwner The table owner to add.
     * @return The new use count.
     */
    public int addPTableOwner(ThinPhysicalTableOwner pTableOwner);

}
