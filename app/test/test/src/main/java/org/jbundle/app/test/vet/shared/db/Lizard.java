/**
 * @(#)Lizard.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.shared.db;

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
import org.jbundle.base.db.shared.*;
import org.jbundle.app.test.vet.shared.screen.*;
import org.jbundle.app.test.vet.db.*;
import org.jbundle.model.app.test.vet.shared.db.*;

/**
 *  Lizard - .
 */
public class Lizard extends Reptile
     implements LizardModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kReptileTypeID = kReptileTypeID;
    //public static final int kExtra = kExtra;
    //public static final int kSpecial = kSpecial;
    //public static final int kName = kName;
    //public static final int kVetID = kVetID;
    //public static final int kWeight = kWeight;
    //public static final int kClearance = kClearance;
    public static final int kLizardLastField = kReptileLastField;
    public static final int kLizardFields = kReptileLastField - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kVetIDKey = kIDKey + 1;
    public static final int kNameKey = kVetIDKey + 1;
    public static final int kLizardLastKey = kNameKey;
    public static final int kLizardKeys = kNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public Lizard()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Lizard(RecordOwner screen)
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

    public static final String kLizardFile = "Reptile";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kLizardFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "test";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.REMOTE | DBConstants.SHARED_TABLE | DBConstants.USER_DATA;
    }
    /**
     * Make a default screen.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = BaseScreen.makeNewScreen(LIZARD_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = BaseScreen.makeNewScreen(LIZARD_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        //if (iFieldSeq == kReptileTypeID)
        //{
        //  field = new ReptileTypeField(this, "ReptileTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.addListener(new InitOnceFieldHandler(null));
        //}
        //if (iFieldSeq == kExtra)
        //  field = new StringField(this, "Extra", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kSpecial)
        //  field = new StringField(this, "Special", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kName)
        //  field = new StringField(this, "Name", 30, null, null);
        //if (iFieldSeq == kVetID)
        //  field = new VetField(this, "VetID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kWeight)
        //  field = new ShortField(this, "Weight", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClearance)
            field = new ShortField(this, "Clearance", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kLizardLastField)
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
        if (iKeyArea == kVetIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "VetID");
            keyArea.addKeyField(kVetID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kNameKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Name");
            keyArea.addKeyField(kName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kLizardLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kLizardLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.addListener(new SharedFileHandler(Reptile.kReptileTypeID, ReptileTypeField.LIZARD));
    }

}
