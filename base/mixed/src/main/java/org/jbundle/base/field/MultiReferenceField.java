package org.jbundle.base.field;

/**
 * This field holds the object reference and the table for a multi-table record.
 */
import org.jbundle.base.db.Record;

public class MultiReferenceField extends ReferenceField
{
	private static final long serialVersionUID = 1L;

    /**
     * ReferenceField constructor comment.
     */
    public MultiReferenceField()
    {
        super();
    }
    /**
     * ReferenceField constructor comment.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public MultiReferenceField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize this object.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }
}
