/**
 * @(#)ClassFields.
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
import org.jbundle.model.app.program.db.*;

/**
 *  ClassFields - Class Field Detail.
 */
public class ClassFields extends VirtualRecord
     implements ClassFieldsModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kClassInfoClassName = kVirtualRecordLastField + 1;
    public static final int kClassInfoID = kClassInfoClassName + 1;
    public static final int kClassFieldClass = kClassInfoID + 1;
    public static final int kClassFieldSequence = kClassFieldClass + 1;
    public static final int kClassFieldName = kClassFieldSequence + 1;
    public static final int kClassFieldDesc = kClassFieldName + 1;
    public static final int kClassFieldProtect = kClassFieldDesc + 1;
    public static final int kClassFieldInitial = kClassFieldProtect + 1;
    public static final int kClassFieldInitialValue = kClassFieldInitial + 1;
    public static final int kClassFieldsType = kClassFieldInitialValue + 1;
    public static final int kIncludeScope = kClassFieldsType + 1;
    public static final int kClassFieldsLastField = kIncludeScope;
    public static final int kClassFieldsFields = kIncludeScope - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kClassInfoClassNameKey = kIDKey + 1;
    public static final int kClassFieldsLastKey = kClassInfoClassNameKey;
    public static final int kClassFieldsKeys = kClassInfoClassNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public ClassFields()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ClassFields(RecordOwner screen)
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

    public static final String kClassFieldsFile = "ClassFields";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kClassFieldsFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            screen = new ClassFieldsScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new ClassFieldsGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kClassInfoClassName)
            field = new StringField(this, "ClassInfoClassName", 40, null, null);
        if (iFieldSeq == kClassInfoID)
            field = new ReferenceField(this, "ClassInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassFieldClass)
            field = new StringField(this, "ClassFieldClass", 60, null, null);
        if (iFieldSeq == kClassFieldSequence)
            field = new IntegerField(this, "ClassFieldSequence", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0));
        if (iFieldSeq == kClassFieldName)
        {
            field = new StringField(this, "ClassFieldName", 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kClassFieldDesc)
            field = new StringField(this, "ClassFieldDesc", 30, null, null);
        if (iFieldSeq == kClassFieldProtect)
        {
            field = new StringField(this, "ClassFieldProtect", 30, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kClassFieldInitial)
            field = new MemoField(this, "ClassFieldInitial", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassFieldInitialValue)
            field = new MemoField(this, "ClassFieldInitialValue", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassFieldsType)
        {
            field = new ClassFieldsTypeField(this, "ClassFieldsType", Constants.DEFAULT_FIELD_LENGTH, null, "ClassFieldsTypeField.NATIVE_FIELD");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kIncludeScope)
        {
            field = new IncludeScopeField(this, "IncludeScope", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0x001));
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kClassFieldsLastField)
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
        if (iKeyArea == kClassInfoClassNameKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ClassInfoClassName");
            keyArea.addKeyField(kClassInfoClassName, DBConstants.ASCENDING);
            keyArea.addKeyField(kClassFieldSequence, DBConstants.ASCENDING);
            keyArea.addKeyField(kClassFieldName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kClassFieldsLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kClassFieldsLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
