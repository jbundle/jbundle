/**
 * @(#)AnalysisLog.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.app.program.db.*;

/**
 *  AnalysisLog - Analyze class create/delete.
 */
public class AnalysisLog extends VirtualRecord
     implements AnalysisLogModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public AnalysisLog()
    {
        super();
    }
    /**
     * Constructor.
     */
    public AnalysisLog(RecordOwner screen)
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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(ANALYSIS_LOG_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "program";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.REMOTE | DBConstants.USER_DATA;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
            field = new IntegerField(this, SYSTEM_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new IntegerField(this, OBJECT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, CLASS_NAME, 255, null, null);
        if (iFieldSeq == 6)
            field = new StringField(this, DATABASE_NAME, 255, null, null);
        if (iFieldSeq == 7)
            field = new AnalysisLog_InitTime(this, INIT_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new DateTimeField(this, FREE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 9)
            field = new StringField(this, RECORD_OWNER, 255, null, null);
        if (iFieldSeq == 10)
            field = new MemoField(this, STACK_TRACE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, ID_KEY);
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, OBJECT_ID_KEY);
            keyArea.addKeyField(SYSTEM_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(OBJECT_ID, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * Log that this record has been added.
     * Call this from the end of record.init
     * @param record the record that is being added.
     */
    public void logAddRecord(Rec record, int iSystemID)
    {
        try {
            this.getTable().setProperty(DBParams.SUPRESSREMOTEDBMESSAGES, DBConstants.TRUE);
            this.getTable().getDatabase().setProperty(DBParams.MESSAGES_TO_REMOTE, DBConstants.FALSE);
        
            this.addNew();
            this.getField(AnalysisLog.SYSTEM_ID).setValue(iSystemID);
            this.getField(AnalysisLog.OBJECT_ID).setValue(Debug.getObjectID(record, false));
            this.getField(AnalysisLog.CLASS_NAME).setString(Debug.getClassName(record));
            this.getField(AnalysisLog.DATABASE_NAME).setString(record.getDatabaseName());
            ((DateTimeField)this.getField(AnalysisLog.INIT_TIME)).setValue(DateTimeField.currentTime());
            this.getField(AnalysisLog.RECORD_OWNER).setString(Debug.getClassName(((Record)record).getRecordOwner()));
            this.getField(AnalysisLog.STACK_TRACE).setString(Debug.getStackTrace());
            this.add();
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Log that this record has been freed.
     * Call this from the end of record.free
     * @param record the record that is being added.
     */
    public void logRemoveRecord(Rec record, int iSystemID)
    {
        try {
            this.getTable().setProperty(DBParams.SUPRESSREMOTEDBMESSAGES, DBConstants.TRUE);
            this.getTable().getDatabase().setProperty(DBParams.MESSAGES_TO_REMOTE, DBConstants.FALSE);
        
            this.addNew();
            this.getField(AnalysisLog.SYSTEM_ID).setValue(iSystemID);
            this.getField(AnalysisLog.OBJECT_ID).setValue(Debug.getObjectID(record, true));
            this.setKeyArea(AnalysisLog.OBJECT_ID_KEY);
            if (this.seek(null))
            {
                this.edit();
                ((DateTimeField)this.getField(AnalysisLog.FREE_TIME)).setValue(DateTimeField.currentTime());
                if (this.getField(AnalysisLog.RECORD_OWNER).isNull())
                    this.getField(AnalysisLog.RECORD_OWNER).setString(Debug.getClassName(((Record)record).getRecordOwner()));
                this.set();
            }
            else
            {
            // Ignore for now    System.exit(1);
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }

}
