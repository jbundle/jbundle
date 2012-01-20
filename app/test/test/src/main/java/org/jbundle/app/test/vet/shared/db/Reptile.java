/**
 * @(#)Reptile.
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
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.test.vet.shared.screen.*;
import org.jbundle.app.test.vet.db.*;
import org.jbundle.model.app.test.vet.shared.db.*;

/**
 *  Reptile - .
 */
public class Reptile extends VirtualRecord
     implements ReptileModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kReptileTypeID = kVirtualRecordLastField + 1;
    public static final int kExtra = kReptileTypeID + 1;
    public static final int kSpecial = kExtra + 1;
    public static final int kName = kSpecial + 1;
    public static final int kVetID = kName + 1;
    public static final int kWeight = kVetID + 1;
    public static final int kClearance = kWeight + 1;
    public static final int kReptileLastField = kClearance;
    public static final int kReptileFields = kClearance - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kVetIDKey = kIDKey + 1;
    public static final int kNameKey = kVetIDKey + 1;
    public static final int kReptileLastKey = kNameKey;
    public static final int kReptileKeys = kNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public Reptile()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Reptile(RecordOwner screen)
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

    public static final String kReptileFile = "Reptile";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kReptileFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.REMOTE | DBConstants.BASE_TABLE_CLASS | DBConstants.SHARED_TABLE | DBConstants.USER_DATA;
    }
    /**
     * MakeScreen Method.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
        {
            Object objObjectID = null;
            try {
                objObjectID = this.getHandle(DBConstants.OBJECT_ID_HANDLE);
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
            String strObjectID = null;
            if (objObjectID != null)
                parentScreen.setProperty(DBConstants.STRING_OBJECT_ID_HANDLE, objObjectID.toString());
            if (this.getField(Reptile.kReptileTypeID).getValue() == ReptileTypeField.SNAKE)
                screen = new SnakeScreen(null, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
            else //if (this.getField(Reptile.kReptileTypeID).getValue() == ReptileTypeField.LIZARD)
                screen = new LizardScreen(null, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        }
        else //if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new ReptileGridScreen(this, (ScreenLocation)itsLocation, (BasePanel)parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        if (iFieldSeq == kReptileTypeID)
        {
            field = new ReptileTypeField(this, "ReptileTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kExtra)
            field = new StringField(this, "Extra", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kSpecial)
            field = new StringField(this, "Special", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kName)
            field = new StringField(this, "Name", 30, null, null);
        if (iFieldSeq == kVetID)
            field = new VetField(this, "VetID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kWeight)
            field = new ShortField(this, "Weight", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClearance)
            field = new ShortField(this, "Clearance", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kReptileLastField)
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
        if (keyArea == null) if (iKeyArea < kReptileLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kReptileLastKey)
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
    }
    /**
     * Get the record type from the field that specifies the record type.
     * (Override this).
     * @return The record type (as an object).
     */
    public BaseField getSharedRecordTypeKey()
    {
        return this.getField(Reptile.kReptileTypeID);
    }
    /**
     * MakeSharedRecord Method.
     */
    public Record makeSharedRecord(Object objKey, RecordOwner recordOwner)
    {
        if (objKey instanceof Integer)
        {
            int iReptileType = ((Integer)objKey).intValue();
            if (iReptileType == ReptileTypeField.LIZARD)
                return new Lizard(recordOwner);
            if (iReptileType == ReptileTypeField.SNAKE)
                return new Snake(recordOwner);
        }
        return null;
    }

}
