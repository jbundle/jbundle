package org.jbundle.thin.base.db.mem;

import org.jbundle.model.DBException;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;

/**
 * The BufferFieldTable is the base table with a BaseBuffer as the DataSource.
 * This is mostly useful for dealing with memory tables (since they require BaseBuffers).
 */
public class BufferFieldTable extends FieldTable
{
    /**
     * Constructor.
     */
    public BufferFieldTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The record to handle.
     */
    public BufferFieldTable(FieldList record)
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
        this.setDataSource(new VectorBuffer(null));
    }
    /**
     * Move this Field's data to the record area (Override this for non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the data from.
     * @exception DBException File exception.
     */
    public void fieldToData(Field field)
    {
        ((BaseBuffer)this.getDataSource()).addNextField((FieldInfo)field);
    }
    /**
     * Move the data source buffer to all the fields.
     *  <br /><pre>
     *  Make sure you do the following steps:
     *  1) Move the data fields to the correct record data fields, with mode Constants.READ_MOVE.
     *  2) Set the data source or set to null, so I can cache the source if necessary.
     *  3) Save the objectID if it is not an Integer type, so I can serialize the source of this object.
     *  </pre>
     * Note: This is synchronized because VectorBuffer is not thread safe.
     * @exception Exception File exception.
     * @return Any error encountered moving the data.
     */
    public synchronized int dataToFields(Rec record) throws DBException
    {
        if (this.getDataSource() == null)
            throw new DBException(Constants.INVALID_RECORD);
        ((BaseBuffer)this.getDataSource()).resetPosition();
        return super.dataToFields(record);
    }
    /**
     * Move the output buffer to this field (Override this to use non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the current data to.
     * @return The error code (From field.setData()).
     */
    public int dataToField(Field field) throws DBException
    {
        return ((BaseBuffer)this.getDataSource()).getNextField((FieldInfo)field, Constants.DISPLAY, Constants.READ_MOVE);
    } 
}
