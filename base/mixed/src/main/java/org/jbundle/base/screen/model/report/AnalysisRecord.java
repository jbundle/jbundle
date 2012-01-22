/*
 *  @(#)AnalysisRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

import org.jbundle.base.db.EmptyKey;
import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.field.EmptyField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.thin.base.db.Constants;


/**
 *  AnalysisRecord - Summary test file.
 */
public class AnalysisRecord extends Record
{
	private static final long serialVersionUID = 1L;

    public static final int kID = kRecordLastField + 1;
    public static final int kAnalysisRecordLastField = kID;
    public static final int kAnalysisRecordFields = kID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kAnalysisRecordLastKey = kIDKey;
    public static final int kAnalysisRecordKeys = kIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public AnalysisRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public AnalysisRecord(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    public static final String kAnalysisRecordFile = "AnalysisRecord";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return Record.formatTableNames(kAnalysisRecordFile, bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "test";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.UNSHAREABLE_MEMORY;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kID)
            field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kAnalysisRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == kIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (keyArea == null)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kAnalysisRecordLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
