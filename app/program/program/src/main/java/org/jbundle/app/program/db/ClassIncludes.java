/**
 * @(#)ClassIncludes.
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
 *  ClassIncludes - .
 */
public class ClassIncludes extends VirtualRecord
     implements org.jbundle.model.app.program.db.ClassIncludesModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kClassInfoID = kVirtualRecordLastField + 1;
    public static final int kClassIncludesClass = kClassInfoID + 1;
    public static final int kClassIncludesLastField = kClassIncludesClass;
    public static final int kClassIncludesFields = kClassIncludesClass - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kClassInfoIDKey = kIDKey + 1;
    public static final int kClassIncludesLastKey = kClassInfoIDKey;
    public static final int kClassIncludesKeys = kClassInfoIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public ClassIncludes()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ClassIncludes(RecordOwner screen)
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

    public static final String kClassIncludesFile = "ClassIncludes";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kClassIncludesFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) != 0)
            screen = new ClassIncludesScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new ClassIncludesGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        if (iFieldSeq == kClassInfoID)
            field = new ReferenceField(this, "ClassInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassIncludesClass)
            field = new StringField(this, "ClassIncludesClass", 128, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kClassIncludesLastField)
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
        if (iKeyArea == kClassInfoIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ClassInfoID");
            keyArea.addKeyField(kClassInfoID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kClassIncludesLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kClassIncludesLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
