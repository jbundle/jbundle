/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.thin.base.db.Converter;


/**
 * Enable target field on valid and disable on new.
 */
public class EnableOnPhysicalHandler extends EnableOnValidHandler
{

    /**
     * Constructor.
     */
    public EnableOnPhysicalHandler()
    {
        super();
    }
    /**
     * This constructor enable/disables ALL non-unique key fields.
     * @param bEnbleOnValid Enable/disable the fields on valid.
     * @param bEnableOnNew Enable/disable the fields on new.
     */
    public EnableOnPhysicalHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param field Target field.
     * @param iFieldSeq Target field.
     * @param bEnbleOnValid Enable/disable the fields on valid.
     * @param bEnableOnNew Enable/disable the fields on new.
     * @param flagField If this flag is true, do the opposite enable/disable.
     */
    public void init(Record record)
    {
        super.init(record, null, null, true, true, null);
    }
    /**
     * Called when a valid record is read from the table/query.
     * Enables or disables the target field(s).
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption) // Init this field override for other value
    {
    	Record record = this.getOwner();
    	BaseDatabase database = record.getTable().getDatabase();
    	m_bEnableOnValid = true;

    	int counter = (int)record.getCounterField().getValue();
    	String startingID = database.getProperty(BaseDatabase.STARTING_ID);
    	String endingID = database.getProperty(BaseDatabase.ENDING_ID);
    	if (startingID != null)
    		if (counter < Integer.parseInt(Converter.stripNonNumber(startingID)))
    			m_bEnableOnValid = false;
    	if (endingID != null)
    		if (counter > Integer.parseInt(Converter.stripNonNumber(endingID)))
    			m_bEnableOnValid = false;
//    	System.out.println("start, end: " + startingID + "  " + endingID);
        super.doValidRecord(bDisplayOption);
    }
}
