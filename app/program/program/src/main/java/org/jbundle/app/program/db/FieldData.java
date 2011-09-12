/**
 * @(#)FieldData.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.app.program.screen.*;

/**
 *  FieldData - Field Information File Class.
 */
public class FieldData extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kFieldName = kVirtualRecordLastField + 1;
    public static final int kFieldClass = kFieldName + 1;
    public static final int kBaseFieldName = kFieldClass + 1;
    public static final int kDependentFieldName = kBaseFieldName + 1;
    public static final int kMinimumLength = kDependentFieldName + 1;
    public static final int kMaximumLength = kMinimumLength + 1;
    public static final int kDefaultValue = kMaximumLength + 1;
    public static final int kInitialValue = kDefaultValue + 1;
    public static final int kFieldDescription = kInitialValue + 1;
    public static final int kFieldDescVertical = kFieldDescription + 1;
    public static final int kFieldType = kFieldDescVertical + 1;
    public static final int kFieldDimension = kFieldType + 1;
    public static final int kFieldFileName = kFieldDimension + 1;
    public static final int kFieldSeqNo = kFieldFileName + 1;
    public static final int kFieldNotNull = kFieldSeqNo + 1;
    public static final int kDataClass = kFieldNotNull + 1;
    public static final int kThinInclude = kDataClass + 1;
    public static final int kThickInclude = kThinInclude + 1;
    public static final int kHidden = kThickInclude + 1;
    public static final int kFieldDataLastField = kHidden;
    public static final int kFieldDataFields = kHidden - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kFieldFileNameKey = kIDKey + 1;
    public static final int kFieldNameKey = kFieldFileNameKey + 1;
    public static final int kFieldDataLastKey = kFieldNameKey;
    public static final int kFieldDataKeys = kFieldNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public FieldData()
    {
        super();
    }
    /**
     * Constructor.
     */
    public FieldData(RecordOwner screen)
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

    public static final String kFieldDataFile = "FieldData";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kFieldDataFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Field";
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
        return DBConstants.REMOTE | DBConstants.SHARED_DATA | DBConstants.HIERARCHICAL;
    }
    /**
     * Make a default screen.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) != 0)
            screen = new FieldDataScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new FieldDataGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kID)
        {
            field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == kFieldName)
        {
            field = new StringField(this, "FieldName", 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kFieldClass)
            field = new StringField(this, "FieldClass", 40, null, null);
        if (iFieldSeq == kBaseFieldName)
            field = new StringField(this, "BaseFieldName", 40, null, null);
        if (iFieldSeq == kDependentFieldName)
            field = new StringField(this, "DependentFieldName", 40, null, null);
        if (iFieldSeq == kMinimumLength)
            field = new ShortField(this, "MinimumLength", 4, null, null);
        if (iFieldSeq == kMaximumLength)
            field = new ShortField(this, "MaximumLength", 5, null, null);
        if (iFieldSeq == kDefaultValue)
            field = new StringField(this, "DefaultValue", 50, null, null);
        if (iFieldSeq == kInitialValue)
            field = new StringField(this, "InitialValue", 50, null, null);
        if (iFieldSeq == kFieldDescription)
            field = new StringField(this, "FieldDescription", 100, null, null);
        if (iFieldSeq == kFieldDescVertical)
            field = new StringField(this, "FieldDescVertical", 14, null, null);
        if (iFieldSeq == kFieldType)
            field = new StringField(this, "FieldType", 1, null, null);
        if (iFieldSeq == kFieldDimension)
            field = new ShortField(this, "FieldDimension", 3, null, null);
        if (iFieldSeq == kFieldFileName)
            field = new StringField(this, "FieldFileName", 40, null, null);
        if (iFieldSeq == kFieldSeqNo)
            field = new ShortField(this, "FieldSeqNo", 4, null, null);
        if (iFieldSeq == kFieldNotNull)
            field = new BooleanField(this, "FieldNotNull", 1, null, null);
        if (iFieldSeq == kDataClass)
        {
            field = new StringField(this, "DataClass", 20, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kThinInclude)
            field = new BooleanField(this, "ThinInclude", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kThickInclude)
            field = new BooleanField(this, "ThickInclude", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kHidden)
            field = new BooleanField(this, "Hidden", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kFieldDataLastField)
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
        if (iKeyArea == kFieldFileNameKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "FieldFileName");
            keyArea.addKeyField(kFieldFileName, DBConstants.ASCENDING);
            keyArea.addKeyField(kFieldSeqNo, DBConstants.ASCENDING);
        }
        if (iKeyArea == kFieldNameKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "FieldName");
            keyArea.addKeyField(kFieldFileName, DBConstants.ASCENDING);
            keyArea.addKeyField(kFieldName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kFieldDataLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kFieldDataLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        this.addListener(new SetDataClass(null));
    }

}
