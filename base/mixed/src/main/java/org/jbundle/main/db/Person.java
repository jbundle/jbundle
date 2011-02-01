/**
 *  @(#)Person.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.main.db;

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
import org.jbundle.base.message.trx.message.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.main.user.db.*;

/**
 *  Person - List of People's Names, Addr.
 */
public class Person extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kCode = kVirtualRecordLastField + 1;
    public static final int kName = kCode + 1;
    public static final int kAddressLine1 = kName + 1;
    public static final int kAddressLine2 = kAddressLine1 + 1;
    public static final int kCityOrTown = kAddressLine2 + 1;
    public static final int kStateOrRegion = kCityOrTown + 1;
    public static final int kPostalCode = kStateOrRegion + 1;
    public static final int kCountry = kPostalCode + 1;
    public static final int kTel = kCountry + 1;
    public static final int kFax = kTel + 1;
    public static final int kEmail = kFax + 1;
    public static final int kWeb = kEmail + 1;
    public static final int kDateEntered = kWeb + 1;
    public static final int kDateChanged = kDateEntered + 1;
    public static final int kChangedID = kDateChanged + 1;
    public static final int kComments = kChangedID + 1;
    public static final int kUserID = kComments + 1;
    public static final int kPassword = kUserID + 1;
    public static final int kNameSort = kPassword + 1;
    public static final int kPostalCodeSort = kNameSort + 1;
    public static final int kPersonLastField = kPostalCodeSort;
    public static final int kPersonFields = kPostalCodeSort - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kCodeKey = kIDKey + 1;
    public static final int kPersonLastKey = kCodeKey;
    public static final int kPersonKeys = kCodeKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public Person()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Person(RecordOwner screen)
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

    public static final String kPersonFile = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kID)
        {
            field = new CounterField(this, "ID", 8, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == kCode)
            field = new StringField(this, "Code", 16, null, null);
        if (iFieldSeq == kName)
            field = new StringField(this, "Name", 30, null, null);
        if (iFieldSeq == kAddressLine1)
            field = new StringField(this, "AddressLine1", 40, null, null);
        if (iFieldSeq == kAddressLine2)
            field = new StringField(this, "AddressLine2", 40, null, null);
        if (iFieldSeq == kCityOrTown)
            field = new StringField(this, "CityOrTown", 15, null, null);
        if (iFieldSeq == kStateOrRegion)
            field = new StringField(this, "StateOrRegion", 15, null, null);
        if (iFieldSeq == kPostalCode)
            field = new StringField(this, "PostalCode", 10, null, null);
        if (iFieldSeq == kCountry)
            field = new StringField(this, "Country", 15, null, null);
        if (iFieldSeq == kTel)
            field = new PhoneField(this, "Tel", 24, null, null);
        if (iFieldSeq == kFax)
            field = new FaxField(this, "Fax", 24, null, null);
        if (iFieldSeq == kEmail)
            field = new EMailField(this, "Email", 40, null, null);
        if (iFieldSeq == kWeb)
            field = new URLField(this, "Web", 60, null, null);
        if (iFieldSeq == kDateEntered)
            field = new Person_DateEntered(this, "DateEntered", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kDateChanged)
            field = new DateField(this, "DateChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kChangedID)
            field = new ReferenceField(this, "ChangedID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kComments)
            field = new MemoField(this, "Comments", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kUserID)
            field = new UserField(this, "UserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kPassword)
            field = new StringField(this, "Password", 16, null, null);
        if (iFieldSeq == kNameSort)
            field = new StringField(this, "NameSort", 6, null, null);
        if (iFieldSeq == kPostalCodeSort)
            field = new StringField(this, "PostalCodeSort", 5, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kPersonLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.addListener(new DateChangedHandler(Person.kDateChanged));
        this.addListener(new SetUserIDHandler(Person.kChangedID, false));
        
        this.getField(Person.kName).addListener(new CopyLastHandler(Person.kNameSort));    // Only if dest is null (ie., company name is null)
        this.getField(Person.kNameSort).addListener(new FieldToUpperHandler(null));
        
        this.getField(Person.kPostalCode).addListener(new CopyFieldHandler(Person.kPostalCodeSort));
    }
    /**
     * Add the destination information of this person to the message.
     */
    public TrxMessageHeader addDestInfo(TrxMessageHeader trxMessageHeader)
    {
        String strMessageTransport = (String)trxMessageHeader.get(MessageTransport.SEND_MESSAGE_BY_PARAM);
        if ((MessageTransport.EMAIL.equalsIgnoreCase(strMessageTransport))
            && (trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM) == null))
                trxMessageHeader.put(TrxMessageHeader.DESTINATION_PARAM, this.getField(Person.kEmail).toString());
        else if ((MessageTransport.FAX.equalsIgnoreCase(strMessageTransport))
            && (trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM) == null))
                trxMessageHeader.put(TrxMessageHeader.DESTINATION_PARAM, this.getField(Person.kFax).toString());
        else if ((MessageTransport.MAIL.equalsIgnoreCase(strMessageTransport))
            && (trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM) == null))
        {
            String strMail = this.getField(Person.kName).toString();
            strMail += '\n' + this.getField(Person.kAddressLine1).toString();
            if (!this.getField(Person.kAddressLine2).isNull())
                strMail += '\n' + this.getField(Person.kAddressLine2).toString();
            strMail += '\n' + this.getField(Person.kCityOrTown).toString();
            if (!this.getField(Person.kStateOrRegion).isNull())
                strMail += ", " + this.getField(Person.kStateOrRegion).toString();
            if (!this.getField(Person.kPostalCode).isNull())
                strMail += ' ' + this.getField(Person.kPostalCode).toString();
            if (!this.getField(Person.kCountry).isNull())
                strMail += ' ' + this.getField(Person.kCountry).toString();
            trxMessageHeader.put(TrxMessageHeader.DESTINATION_PARAM, strMail);
        }
        Map<String,Object> mapInfo = trxMessageHeader.getMessageInfoMap();
        if (mapInfo == null)
            trxMessageHeader.setMessageInfoMap(mapInfo = new HashMap<String,Object>());
        
        mapInfo.put(TrxMessageHeader.CONTACT_TYPE, this.getTableNames(false));
        mapInfo.put(TrxMessageHeader.CONTACT_ID, this.getField(Person.kID).toString());
        if (!this.getField(Person.kUserID).isNull())
        {
            mapInfo.put(TrxMessageHeader.CONTACT_USER_ID, this.getField(Person.kUserID).toString());
            Record recUserInfo = ((ReferenceField)this.getField(Person.kUserID)).getReference();
            if (recUserInfo != null)
                if ((recUserInfo.getEditMode() == DBConstants.EDIT_CURRENT) || (recUserInfo.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    if (!recUserInfo.getField(UserInfo.kUserName).isNull())
                        mapInfo.put(TrxMessageHeader.CONTACT_USER, recUserInfo.getField(UserInfo.kUserName).toString());
        }
        
        return trxMessageHeader;
    }
    /**
     * Get the default display field for this record (for popups and lookups).
     * @return The sequence of the field that should be displayed.
     */
    public int getDefaultDisplayFieldSeq()
    {
        return Person.kName;
    }

}
