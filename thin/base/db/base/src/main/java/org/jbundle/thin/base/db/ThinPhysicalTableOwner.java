package org.jbundle.thin.base.db;

import org.jbundle.thin.base.db.mem.base.PTable;

public interface ThinPhysicalTableOwner {
    /**
     * Set the ptable that I am an owner of.
     * @param pTable
     */
    public void setPTable(PTable pTable);

}
