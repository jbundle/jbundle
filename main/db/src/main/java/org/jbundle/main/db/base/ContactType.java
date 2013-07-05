/**
 * @(#)ContactType.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db.base;

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
import org.jbundle.model.main.db.base.*;

/**
 *  ContactType - Contact type.
 */
public class ContactType extends VirtualRecord
     implements ContactTypeModel
{
    private static final long serialVersionUID = 1L;

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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(CONTACT_TYPE_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            field = new StringField(this, DESCRIPTION, 30, null, null);
        if (iFieldSeq == 4)
            field = new StringField(this, CODE, 15, null, null);
        if (iFieldSeq == 5)
            field = new ShortField(this, LEVEL, Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)1));
        if (iFieldSeq == 6)
            field = new ContactTypeField(this, PARENT_CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new ImageField(this, ICON, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new StringField(this, RECORD_CLASS, 128, null, null);
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
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, DESCRIPTION_KEY);
            keyArea.addKeyField(DESCRIPTION, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, CODE_KEY);
            keyArea.addKeyField(CODE, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
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
        this.setKeyArea(ContactType.CODE_KEY);
        this.getField(ContactType.CODE).setString(strType);
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
            this.setKeyArea(ContactType.ID_KEY);
            this.getCounterField().setString(strContactTypeID);
            try   {
                if (this.seek(null))
                    strContactTypeID = this.getField(ContactType.CODE).toString();
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
            this.setKeyArea(ContactType.CODE_KEY);
            this.getField(ContactType.CODE).setString(strRecordName);
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
        String strRecordClass = this.getField(ContactType.RECORD_CLASS).toString();
        return Record.makeRecordFromClassName(strRecordClass, recordOwner, true, false);
    }

}
