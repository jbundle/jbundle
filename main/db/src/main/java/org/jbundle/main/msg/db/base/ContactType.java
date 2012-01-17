/**
 * @(#)ContactType.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db.base;

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
import org.jbundle.main.db.*;
import org.jbundle.model.db.*;
import org.jbundle.model.main.msg.db.base.*;

/**
 *  ContactType - Contact type.
 */
public class ContactType extends VirtualRecord
     implements ContactTypeModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kDescription = kVirtualRecordLastField + 1;
    public static final int kCode = kDescription + 1;
    public static final int kLevel = kCode + 1;
    public static final int kParentContactTypeID = kLevel + 1;
    public static final int kIcon = kParentContactTypeID + 1;
    public static final int kRecordClass = kIcon + 1;
    public static final int kContactTypeLastField = kRecordClass;
    public static final int kContactTypeFields = kRecordClass - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kDescriptionKey = kIDKey + 1;
    public static final int kCodeKey = kDescriptionKey + 1;
    public static final int kContactTypeLastKey = kCodeKey;
    public static final int kContactTypeKeys = kCodeKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public ContactType()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ContactType(RecordOwner screen)
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

    public static final String kContactTypeFile = "ContactType";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kContactTypeFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.TABLE | DBConstants.SHARED_DATA | DBConstants.LOCALIZABLE;
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
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 30, null, null);
        if (iFieldSeq == kCode)
            field = new StringField(this, "Code", 15, null, null);
        if (iFieldSeq == kLevel)
            field = new ShortField(this, "Level", Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)1));
        if (iFieldSeq == kParentContactTypeID)
            field = new ContactTypeField(this, "ParentContactTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIcon)
            field = new ImageField(this, "Icon", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kRecordClass)
            field = new StringField(this, "RecordClass", 128, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kContactTypeLastField)
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
        if (iKeyArea == kDescriptionKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Description");
            keyArea.addKeyField(kDescription, DBConstants.ASCENDING);
        }
        if (iKeyArea == kCodeKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Code");
            keyArea.addKeyField(kCode, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kContactTypeLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kContactTypeLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Get the contact type for this record.
     * (The code is the record table name).
     * @param The record.
     * @return The Contact Type record (or null if not found).
     */
    public ContactType getContactType(Record record)
    {
        String strType = record.getTableNames(false);
        this.setKeyArea(ContactType.kCodeKey);
        this.getField(ContactType.kCode).setString(strType);
        try {
            if (this.seek(null))
            {   // Success
                return this;
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return null;    // Not found
    }
    /**
     * GetContactTypeFromID Method.
     */
    public String getContactTypeFromID(String strContactTypeID)
    {
        if (Utility.isNumeric(strContactTypeID))
        {
            int iOldKeyArea = this.getDefaultOrder();
            this.setKeyArea(ContactType.kIDKey);
            this.getCounterField().setString(strContactTypeID);
            try   {
                if (this.seek(null))
                    strContactTypeID = this.getField(ContactType.kCode).toString();
            } catch (DBException ex)    {
                ex.printStackTrace();
            } finally {
                this.setKeyArea(iOldKeyArea);
            }
        }  
        return strContactTypeID;
    }
    /**
     * MakeRecordFromRecordName Method.
     */
    public Record makeRecordFromRecordName(String strRecordName, RecordOwner recordOwner)
    {
        int iOldKeyArea = this.getDefaultOrder();
        try {
            this.addNew();
            this.setKeyArea(ContactType.kCodeKey);
            this.getField(ContactType.kCode).setString(strRecordName);
            if (this.seek(DBConstants.EQUALS))
                return this.makeContactRecord(recordOwner);
        } catch (DBException ex) {
            this.setKeyArea(iOldKeyArea);
        }
        return null;
    }
    /**
     * MakeContactRecord Method.
     */
    public Record makeContactRecord(RecordOwner recordOwner)
    {
        String strRecordClass = this.getField(ContactType.kRecordClass).toString();
        return Record.makeRecordFromClassName(strRecordClass, recordOwner, true, false);
    }

}
