/**
 * @(#)ClassResource.
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
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.screen.*;
import org.jbundle.model.app.program.db.*;

/**
 *  ClassResource - Resource detail for "Resource" class types..
 */
public class ClassResource extends VirtualRecord
     implements ClassResourceModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kClassName = kVirtualRecordLastField + 1;
    public static final int kSequenceNo = kClassName + 1;
    public static final int kKeyName = kSequenceNo + 1;
    public static final int kValueName = kKeyName + 1;
    public static final int kClassResourceLastField = kValueName;
    public static final int kClassResourceFields = kValueName - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kClassNameKey = kIDKey + 1;
    public static final int kClassResourceLastKey = kClassNameKey;
    public static final int kClassResourceKeys = kClassNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public ClassResource()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ClassResource(RecordOwner screen)
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

    public static final String kClassResourceFile = "ClassResource";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kClassResourceFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Class Resource";
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
     * Make a default screen.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(CLASS_RESOURCE_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(CLASS_RESOURCE_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        if (iFieldSeq == kClassName)
            field = new StringField(this, "ClassName", 40, null, null);
        if (iFieldSeq == kSequenceNo)
            field = new IntegerField(this, "SequenceNo", 4, null, null);
        if (iFieldSeq == kKeyName)
            field = new StringField(this, "KeyName", 40, null, null);
        if (iFieldSeq == kValueName)
            field = new StringField(this, "ValueName", 255, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kClassResourceLastField)
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
        if (iKeyArea == kClassNameKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ClassName");
            keyArea.addKeyField(kClassName, DBConstants.ASCENDING);
            keyArea.addKeyField(kSequenceNo, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kClassResourceLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kClassResourceLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
