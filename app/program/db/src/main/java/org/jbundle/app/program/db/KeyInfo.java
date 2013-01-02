/**
 * @(#)KeyInfo.
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
 *  KeyInfo - Record Key Information.
 */
public class KeyInfo extends VirtualRecord
     implements KeyInfoModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public KeyInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public KeyInfo(RecordOwner screen)
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
        return (m_tableName == null) ? Record.formatTableNames(KEY_INFO_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Key";
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
            screen = Record.makeNewScreen(KEY_INFO_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(KEY_INFO_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
            field = new StringField(this, KEY_FILENAME, 40, null, null);
        if (iFieldSeq == 4)
            field = new ShortField(this, KEY_NUMBER, 2, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, KEY_FIELD_1, 40, null, null);
        if (iFieldSeq == 6)
            field = new StringField(this, KEY_FIELD_2, 40, null, null);
        if (iFieldSeq == 7)
            field = new StringField(this, KEY_FIELD_3, 40, null, null);
        if (iFieldSeq == 8)
            field = new StringField(this, KEY_FIELD_4, 40, null, null);
        if (iFieldSeq == 9)
            field = new StringField(this, KEY_FIELD_5, 40, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, KEY_FIELD_6, 40, null, null);
        if (iFieldSeq == 11)
            field = new StringField(this, KEY_FIELD_7, 40, null, null);
        if (iFieldSeq == 12)
            field = new StringField(this, KEY_FIELD_8, 40, null, null);
        if (iFieldSeq == 13)
            field = new StringField(this, KEY_FIELD_9, 40, null, null);
        if (iFieldSeq == 14)
            field = new StringField(this, KEY_NAME, 40, null, null);
        if (iFieldSeq == 15)
            field = new KeyTypeField(this, KEY_TYPE, 1, null, "KeyTypeField.UNIQUE");
        if (iFieldSeq == 16)
        {
            field = new IncludeScopeField(this, INCLUDE_SCOPE, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0x004));
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
            keyArea = this.makeIndex(DBConstants.UNIQUE, ID_KEY);
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, KEY_FILENAME_KEY);
            keyArea.addKeyField(KEY_FILENAME, DBConstants.ASCENDING);
            keyArea.addKeyField(KEY_NUMBER, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }

}
