/**
 * @(#)Registration.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.resource.db;

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
import org.jbundle.app.program.resource.screen.*;
import org.jbundle.model.app.program.resource.db.*;

/**
 *  Registration - Temporary database to emulate a system key registry.
 */
public class Registration extends VirtualRecord
     implements RegistrationModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kResourceID = kVirtualRecordLastField + 1;
    public static final int kCode = kResourceID + 1;
    public static final int kLanguage = kCode + 1;
    public static final int kLocale = kLanguage + 1;
    public static final int kKeyValue = kLocale + 1;
    public static final int kObjectValue = kKeyValue + 1;
    public static final int kRegistrationLastField = kObjectValue;
    public static final int kRegistrationFields = kObjectValue - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kResourceIDKey = kIDKey + 1;
    public static final int kCodeKey = kResourceIDKey + 1;
    public static final int kRegistrationLastKey = kCodeKey;
    public static final int kRegistrationKeys = kCodeKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public Registration()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Registration(RecordOwner screen)
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

    public static final String kRegistrationFile = "Registration";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kRegistrationFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Registration";
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
        return DBConstants.LOCAL | DBConstants.USER_DATA;
    }
    /**
     * Make a default screen.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = BaseScreen.makeNewScreen(REGISTRATION_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = BaseScreen.makeNewScreen(REGISTRATION_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        if (iFieldSeq == kResourceID)
        {
            field = new ResourceField(this, "ResourceID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kCode)
            field = new StringField(this, "Code", 40, null, null);
        if (iFieldSeq == kLanguage)
        {
            field = new StringField(this, "Language", 2, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kLocale)
        {
            field = new StringField(this, "Locale", 2, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kKeyValue)
            field = new StringField(this, "KeyValue", 80, null, null);
        if (iFieldSeq == kObjectValue)
            field = new MemoField(this, "ObjectValue", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kRegistrationLastField)
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
        if (iKeyArea == kResourceIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ResourceID");
            keyArea.addKeyField(kResourceID, DBConstants.ASCENDING);
            keyArea.addKeyField(kLanguage, DBConstants.ASCENDING);
            keyArea.addKeyField(kLocale, DBConstants.ASCENDING);
            keyArea.addKeyField(kKeyValue, DBConstants.ASCENDING);
        }
        if (iKeyArea == kCodeKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "Code");
            keyArea.addKeyField(kResourceID, DBConstants.ASCENDING);
            keyArea.addKeyField(kCode, DBConstants.ASCENDING);
            keyArea.addKeyField(kLanguage, DBConstants.ASCENDING);
            keyArea.addKeyField(kLocale, DBConstants.ASCENDING);
            keyArea.addKeyField(kKeyValue, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kRegistrationLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kRegistrationLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
