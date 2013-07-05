/**
 * @(#)Person.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db;

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
import org.jbundle.base.message.core.trx.*;
import org.jbundle.model.main.msg.db.*;
import org.jbundle.main.user.db.*;
import org.jbundle.model.main.db.*;

/**
 *  Person - List of People's Names, Addr.
 */
public class Person extends VirtualRecord
     implements PersonModel
{
    private static final long serialVersionUID = 1L;

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

    public static final String PERSON_FILE = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
        {
            field = new CounterField(this, ID, 8, null, null);
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
            field = new StringField(this, CODE, 16, null, null);
        if (iFieldSeq == 4)
            field = new StringField(this, NAME, 30, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, ADDRESS_LINE_1, 40, null, null);
        if (iFieldSeq == 6)
            field = new StringField(this, ADDRESS_LINE_2, 40, null, null);
        if (iFieldSeq == 7)
            field = new StringField(this, CITY_OR_TOWN, 15, null, null);
        if (iFieldSeq == 8)
            field = new StringField(this, STATE_OR_REGION, 15, null, null);
        if (iFieldSeq == 9)
            field = new StringField(this, POSTAL_CODE, 10, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, COUNTRY, 15, null, null);
        if (iFieldSeq == 11)
            field = new PhoneField(this, TEL, 24, null, null);
        if (iFieldSeq == 12)
            field = new FaxField(this, FAX, 24, null, null);
        if (iFieldSeq == 13)
            field = new EMailField(this, EMAIL, 40, null, null);
        if (iFieldSeq == 14)
            field = new URLField(this, WEB, 60, null, null);
        if (iFieldSeq == 15)
            field = new Person_DateEntered(this, DATE_ENTERED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 16)
            field = new DateField(this, DATE_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 17)
            field = new ReferenceField(this, CHANGED_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 18)
            field = new MemoField(this, COMMENTS, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 19)
            field = new UserField(this, USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 20)
            field = new StringField(this, PASSWORD, 16, null, null);
        if (iFieldSeq == 21)
            field = new StringField(this, NAME_SORT, 6, null, null);
        if (iFieldSeq == 22)
            field = new StringField(this, POSTAL_CODE_SORT, 5, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.addListener(new DateChangedHandler((DateTimeField)this.getField(Person.DATE_CHANGED)));
        this.addListener(new SetUserIDHandler(Person.CHANGED_ID, false));
        
        this.getField(Person.NAME).addListener(new CopyLastHandler(this.getField(Person.NAME_SORT)));    // Only if dest is null (ie., company name is null)
        this.getField(Person.NAME_SORT).addListener(new FieldToUpperHandler(null));
        
        this.getField(Person.POSTAL_CODE).addListener(new CopyFieldHandler(this.getField(Person.POSTAL_CODE_SORT)));
    }
    /**
     * Add the destination information of this person to the message.
     */
    public TrxMessageHeader addDestInfo(TrxMessageHeader trxMessageHeader)
    {
        String strMessageTransport = (String)trxMessageHeader.get(MessageTransportModel.SEND_MESSAGE_BY_PARAM);
        if ((MessageTransportModel.EMAIL.equalsIgnoreCase(strMessageTransport))
            && (trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM) == null))
                trxMessageHeader.put(TrxMessageHeader.DESTINATION_PARAM, this.getField(Person.EMAIL).toString());
        else if ((MessageTransportModel.FAX.equalsIgnoreCase(strMessageTransport))
            && (trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM) == null))
                trxMessageHeader.put(TrxMessageHeader.DESTINATION_PARAM, this.getField(Person.FAX).toString());
        else if ((MessageTransportModel.MAIL.equalsIgnoreCase(strMessageTransport))
            && (trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM) == null))
        {
            String strMail = this.getField(Person.NAME).toString();
            strMail += '\n' + this.getField(Person.ADDRESS_LINE_1).toString();
            if (!this.getField(Person.ADDRESS_LINE_2).isNull())
                strMail += '\n' + this.getField(Person.ADDRESS_LINE_2).toString();
            strMail += '\n' + this.getField(Person.CITY_OR_TOWN).toString();
            if (!this.getField(Person.STATE_OR_REGION).isNull())
                strMail += ", " + this.getField(Person.STATE_OR_REGION).toString();
            if (!this.getField(Person.POSTAL_CODE).isNull())
                strMail += ' ' + this.getField(Person.POSTAL_CODE).toString();
            if (!this.getField(Person.COUNTRY).isNull())
                strMail += ' ' + this.getField(Person.COUNTRY).toString();
            trxMessageHeader.put(TrxMessageHeader.DESTINATION_PARAM, strMail);
        }
        Map<String,Object> mapInfo = trxMessageHeader.getMessageInfoMap();
        if (mapInfo == null)
            trxMessageHeader.setMessageInfoMap(mapInfo = new HashMap<String,Object>());
        
        mapInfo.put(TrxMessageHeader.CONTACT_TYPE, this.getTableNames(false));
        mapInfo.put(TrxMessageHeader.CONTACT_ID, this.getField(Person.ID).toString());
        if (!this.getField(Person.USER_ID).isNull())
        {
            mapInfo.put(TrxMessageHeader.CONTACT_USER_ID, this.getField(Person.USER_ID).toString());
            Record recUserInfo = ((ReferenceField)this.getField(Person.USER_ID)).getReference();
            if (recUserInfo != null)
                if ((recUserInfo.getEditMode() == DBConstants.EDIT_CURRENT) || (recUserInfo.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    if (!recUserInfo.getField(UserInfo.USER_NAME).isNull())
                        mapInfo.put(TrxMessageHeader.CONTACT_USER, recUserInfo.getField(UserInfo.USER_NAME).toString());
        }
        
        return trxMessageHeader;
    }
    /**
     * Get the default display field name
     * @return The field name to display in popups, grids, etc.
     */
    public String getDefaultDisplayFieldName()
    {
        return Person.NAME;
    }

}
