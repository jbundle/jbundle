/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)FieldScratchHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;

/**
 * This listener makes sure the value is added to this field on merge (instead of replacing it).
 * @version 1.0.0
 * @author    Don Corley
 */
public class MergeDataAddHandler extends FieldDataScratchHandler
{
    /**
     * Constructor.
     */
    public MergeDataAddHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public MergeDataAddHandler(BaseField field)
    {
        this();
        this.init(field, false);    // Do NOT change data on refresh!
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field, boolean bChangeDataOnRefresh)
    {
        super.init(field, bChangeDataOnRefresh);
    }
    /**
     * Free this listener (and the field copy).
     */
    public void free()
    {
        super.free();
    }
    /**
     * Merge my changed data back into field that I just restored from disk.
     * @param objData The value this field held before I refreshed from disk.
     * @return The setData error code.
     */
    public int doMergeData(Object objData)
    {
        double dOrigData = ((Double)this.getOriginalData()).doubleValue();
        double dMergeData = ((Double)objData).doubleValue();
        double dCurrentData = this.getOwner().getValue();
        double dMergeAmount = dMergeData - dOrigData;
        double dNewAmount = dCurrentData + dMergeAmount;
        objData = new Double(dNewAmount);
        this.setOriginalData(new Double(dCurrentData));  // Just in case I come through here again

        return super.doMergeData(objData);
    }
}
