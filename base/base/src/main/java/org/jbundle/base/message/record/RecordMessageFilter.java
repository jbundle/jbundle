/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.record;

/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
import java.io.Serializable;
import java.util.Map;

import org.jbundle.base.db.Record;


/** 
 * A Record message is a message explaining the changes to this record.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class RecordMessageFilter extends BaseRecordMessageFilter
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * Creates new RecordMessage.
     */
    public RecordMessageFilter()
    {
        super();
    }
    /**
     * Constructor.
     */
    public RecordMessageFilter(Record record, Object source)
    {
        this();
        this.init(record, source);
    }
    /**
     * Constructor.
     */
    public void init(Record record, Object source)
    {
        super.init(record, NO_BOOKMARK, source);

        if (record != null)
             record.addListener(new SyncRecordMessageFilterHandler(this, true));
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the name/value pairs in an ordered tree.
     * Note: Replace this with a DOM tree when it is available in the basic SDK.
     * @return A matrix with the name, type, etc.
     */
    public Object[][] createNameValueTree(Object mxString[][], Map<String, Object> properties)
    {
        mxString = super.createNameValueTree(mxString, properties);
        if (properties != null)
            mxString = this.addNameValue(mxString, BOOKMARK, properties.get(BOOKMARK));
        return mxString;
    }
}
