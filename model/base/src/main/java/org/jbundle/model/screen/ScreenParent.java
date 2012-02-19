/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.screen;

import org.jbundle.model.RecordOwnerModel;
import org.jbundle.model.db.Rec;


/**
 * FieldComponent.java
 *
 * Created on November 9, 2000, 2:31 AM
 */

/** 
 * ScreenComponent is a simple interface that allows a screen control
 * to get it's connection to the field information.
 * @author  Administrator
 * @version 1.0.0
 */
public interface ScreenParent extends ComponentParent, RecordOwnerModel
{
    /**
     * Remember, the init method is in RecordOwnerModel.
     */
    
    /**
     * When this query closes, this screen should close also.
     * @param record The record that is dependent on this screen.
     */
    public void setDependentQuery(Rec record);
    /**
     * Find the sub-screen that uses this grid query and set for selection.
     * When you select a new record here, you read the same record in the SelectQuery.
     * (Override to do something).
     * @param selectTable The record which is synced on record change.
     * @param bUpdateOnSelect Do I update the current record if a selection occurs.
     * @return True if successful.
     */
    public boolean setSelectQuery(Rec selectTable, boolean bUpdateOnSelect);
    /**
     *  If there is a header record, return it, otherwise, return the main record.
     *  The header record is the (optional) main record on gridscreens and is sometimes used
     *  to enter data in a sub-record when a header is required.
     * @return The header record.
     */
    public Rec getHeaderRecord();
}
