/**
 * @(#)FieldData.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.screen.*;
import org.jbundle.model.app.program.db.*;

/**
 *  FieldData - Field Information File Class.
 */
public class FieldData extends VirtualRecord
     implements FieldDataModel
{
    private static final long serialVersionUID = 1L;

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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(FIELD_DATA_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(FIELD_DATA_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(FIELD_DATA_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        if (iFieldSeq == 0)
        {
            field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
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
        {
            field = new StringField(this, FIELD_NAME, 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == 4)
            field = new StringField(this, FIELD_CLASS, 40, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, BASE_FIELD_NAME, 40, null, null);
        if (iFieldSeq == 6)
            field = new StringField(this, DEPENDENT_FIELD_NAME, 40, null, null);
        if (iFieldSeq == 7)
            field = new ShortField(this, MINIMUM_LENGTH, 4, null, null);
        if (iFieldSeq == 8)
            field = new ShortField(this, MAXIMUM_LENGTH, 5, null, null);
        if (iFieldSeq == 9)
            field = new StringField(this, DEFAULT_VALUE, 50, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, INITIAL_VALUE, 50, null, null);
        if (iFieldSeq == 11)
            field = new StringField(this, FIELD_DESCRIPTION, 100, null, null);
        if (iFieldSeq == 12)
            field = new StringField(this, FIELD_DESC_VERTICAL, 14, null, null);
        if (iFieldSeq == 13)
            field = new StringField(this, FIELD_TYPE, 1, null, null);
        if (iFieldSeq == 14)
            field = new ShortField(this, FIELD_DIMENSION, 3, null, null);
        if (iFieldSeq == 15)
            field = new StringField(this, FIELD_FILE_NAME, 40, null, null);
        if (iFieldSeq == 16)
            field = new ShortField(this, FIELD_SEQ_NO, 4, null, null);
        if (iFieldSeq == 17)
            field = new BooleanField(this, FIELD_NOT_NULL, 1, null, null);
        if (iFieldSeq == 18)
        {
            field = new StringField(this, DATA_CLASS, 20, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 19)
            field = new BooleanField(this, HIDDEN, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 20)
        {
            field = new IncludeScopeField(this, INCLUDE_SCOPE, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0x001));
            field.addListener(new InitOnceFieldHandler(null));
        }
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
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ID");
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "FieldFileName");
            keyArea.addKeyField(FIELD_FILE_NAME, DBConstants.ASCENDING);
            keyArea.addKeyField(FIELD_SEQ_NO, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "FieldName");
            keyArea.addKeyField(FIELD_FILE_NAME, DBConstants.ASCENDING);
            keyArea.addKeyField(FIELD_NAME, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
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
