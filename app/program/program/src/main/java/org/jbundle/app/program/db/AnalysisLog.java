/**
 * @(#)AnalysisLog.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
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

    //public static final int kID = kID;
    public static final int kSystemID = kVirtualRecordLastField + 1;
    public static final int kObjectID = kSystemID + 1;
    public static final int kClassName = kObjectID + 1;
    public static final int kDatabaseName = kClassName + 1;
    public static final int kInitTime = kDatabaseName + 1;
    public static final int kFreeTime = kInitTime + 1;
    public static final int kRecordOwner = kFreeTime + 1;
    public static final int kStackTrace = kRecordOwner + 1;
    public static final int kAnalysisLogLastField = kStackTrace;
    public static final int kAnalysisLogFields = kStackTrace - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kObjectIDKey = kIDKey + 1;
    public static final int kAnalysisLogLastKey = kObjectIDKey;
    public static final int kAnalysisLogKeys = kObjectIDKey - DBConstants.MAIN_KEY_FIELD + 1;
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

    public static final String kAnalysisLogFile = "AnalysisLog";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kAnalysisLogFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kSystemID)
            field = new IntegerField(this, "SystemID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kObjectID)
            field = new IntegerField(this, "ObjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassName)
            field = new StringField(this, "ClassName", 255, null, null);
        if (iFieldSeq == kDatabaseName)
            field = new StringField(this, "DatabaseName", 255, null, null);
        if (iFieldSeq == kInitTime)
            field = new AnalysisLog_InitTime(this, "InitTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kFreeTime)
            field = new DateTimeField(this, "FreeTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kRecordOwner)
            field = new StringField(this, "RecordOwner", 255, null, null);
        if (iFieldSeq == kStackTrace)
            field = new MemoField(this, "StackTrace", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kAnalysisLogLastField)
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
        if (iKeyArea == kObjectIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ObjectID");
            keyArea.addKeyField(kSystemID, DBConstants.ASCENDING);
            keyArea.addKeyField(kObjectID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kAnalysisLogLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kAnalysisLogLastKey)
                keyArea = new EmptyKey(this);
        }
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
            this.getField(AnalysisLog.kSystemID).setValue(iSystemID);
            this.getField(AnalysisLog.kObjectID).setValue(Debug.getObjectID(record, false));
            this.getField(AnalysisLog.kClassName).setString(Debug.getClassName(record));
            this.getField(AnalysisLog.kDatabaseName).setString(record.getDatabaseName());
            ((DateTimeField)this.getField(AnalysisLog.kInitTime)).setValue(DateTimeField.currentTime());
            this.getField(AnalysisLog.kRecordOwner).setString(Debug.getClassName(((Record)record).getRecordOwner()));
            this.getField(AnalysisLog.kStackTrace).setString(Debug.getStackTrace());
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
            this.getField(AnalysisLog.kSystemID).setValue(iSystemID);
            this.getField(AnalysisLog.kObjectID).setValue(Debug.getObjectID(record, true));
            this.setKeyArea(AnalysisLog.kObjectIDKey);
            if (this.seek(null))
            {
                this.edit();
                ((DateTimeField)this.getField(AnalysisLog.kFreeTime)).setValue(DateTimeField.currentTime());
                if (this.getField(AnalysisLog.kRecordOwner).isNull())
                    this.getField(AnalysisLog.kRecordOwner).setString(Debug.getClassName(((Record)record).getRecordOwner()));
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
