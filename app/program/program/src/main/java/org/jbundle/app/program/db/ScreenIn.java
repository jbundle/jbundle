/**
 * @(#)ScreenIn.
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
 *  ScreenIn - Screen In File.
 */
public class ScreenIn extends VirtualRecord
     implements ScreenInModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public ScreenIn()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ScreenIn(RecordOwner screen)
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
        return (m_tableName == null) ? Record.formatTableNames(SCREEN_IN_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Screen";
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
            screen = Record.makeNewScreen(SCREEN_IN_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(SCREEN_IN_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
            field = new StringField(this, SCREEN_IN_PROG_NAME, 40, null, null);
        if (iFieldSeq == 4)
            field = new ShortField(this, SCREEN_OUT_NUMBER, 2, null, null);
        if (iFieldSeq == 5)
            field = new ShortField(this, SCREEN_ITEM_NUMBER, 4, null, null);
        if (iFieldSeq == 6)
        {
            field = new StringField(this, SCREEN_FILE_NAME, 40, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 7)
            field = new StringField(this, SCREEN_FIELD_NAME, 40, null, null);
        if (iFieldSeq == 8)
            field = new ShortField(this, SCREEN_ROW, 8, null, null);
        if (iFieldSeq == 9)
            field = new ShortField(this, SCREEN_COL, 4, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, SCREEN_GROUP, 4, null, null);
        if (iFieldSeq == 11)
            field = new ShortField(this, SCREEN_PHYSICAL_NUM, 4, null, null);
        if (iFieldSeq == 12)
            field = new ScreenLocField(this, SCREEN_LOCATION, 20, null, null);
        if (iFieldSeq == 13)
            field = new FieldDescField(this, SCREEN_FIELD_DESC, 30, null, null);
        if (iFieldSeq == 14)
            field = new MemoField(this, SCREEN_TEXT, 9999, null, null);
        if (iFieldSeq == 15)
            field = new ScreenAnchorField(this, SCREEN_ANCHOR, 20, null, null);
        if (iFieldSeq == 16)
            field = new ControlTypeField(this, SCREEN_CONTROL_TYPE, 20, null, null);
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
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ScreenInProgName");
            keyArea.addKeyField(SCREEN_IN_PROG_NAME, DBConstants.ASCENDING);
            keyArea.addKeyField(SCREEN_ITEM_NUMBER, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }

}
