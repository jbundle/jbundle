/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.client;

import java.util.Vector;

import org.jbundle.model.DBException;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;


/**
 * The VectorFieldTable is the base table with a Vector as the DataSource.
 * This is mostly useful for dealing with remote calls (since they require Vectors).
 * This table maintains a datasource which is a vector containing all the current
 * record's native field data. The data can be moved to a table for sent/received
 * over the line for overriding classes.
 */
public class VectorFieldTable extends FieldTable
{
    protected int m_iCurrentField = -1;
    
    /**
     * Constructor.
     */
    public VectorFieldTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The record to handle.
     */
    public VectorFieldTable(FieldList record)
    {
        this();
        this.init(record);
    }
    /**
     * Setup a new data source.
     * Creates a new data source and sets it to the data source method variable.
     */
    public void setupNewDataSource()
    {
        this.setDataSource(new Vector<Object>());
    }
    /**
     * Move this Field's data to the record area (Override this for non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the data from.
     * @exception DBException File exception.
     */
    public void fieldToData(Field field)
    {
        ((Vector)this.getDataSource()).addElement(field.getData());
    }
    /**
     * Move the data source buffer to all the fields.
     *  <p /><pre>
     *  Make sure you do the following steps:
     *  1) Move the data fields to the correct record data fields, with mode Constants.READ_MOVE.
     *  2) Set the data source or set to null, so I can cache the source if necessary.
     *  3) Save the objectID if it is not an Integer type, so I can serialize the source of this object.
     *  </pre>
     * @exception DBException File exception.
     * @return Any error encountered moving the data.
     */
    public int dataToFields(Rec record) throws DBException
    {
        m_iCurrentField = 0;
        return super.dataToFields(record);
    }
    /**
     * Move the output buffer to this field (Override this to use non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the data from.
     * @exception DBException File exception.
     */
    public int dataToField(Field field) throws DBException
    {
        int iErrorCode = field.setData(((Vector)this.getDataSource()).elementAt(m_iCurrentField), Constants.DISPLAY, Constants.READ_MOVE);
        m_iCurrentField++;
        return iErrorCode;
    } 
}
