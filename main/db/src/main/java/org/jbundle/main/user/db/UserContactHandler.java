/**
 * @(#)UserContactHandler.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

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
import org.jbundle.main.db.*;
import org.jbundle.model.main.db.base.*;
import org.jbundle.main.db.base.*;

/**
 *  UserContactHandler - Make sure the user info from the contact is synced with the contact from the userinfo file.
 */
public class UserContactHandler extends FieldListener
{
    protected ContactType m_recContactType = null;
    /**
     * Default constructor.
     */
    public UserContactHandler()
    {
        super();
    }
    /**
     * UserContactHandler Method.
     */
    public UserContactHandler(BaseField field)
    {
        this();
        this.init(field);
    }
    /**
     * Init Method.
     */
    public void init(BaseField field)
    {
        super.init(field);
        this.setRespondsToMode(DBConstants.INIT_MOVE, false);
        this.setRespondsToMode(DBConstants.READ_MOVE, false);
    }
    /**
     * Free the resources.
     */
    public void free()
    {
        if (m_recContactType != null)
            m_recContactType.free();
        m_recContactType = null;
        super.free();
    }
    /**
     * FieldChanged Method.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        UserField field = (UserField)this.getOwner();
        Person recPerson = (Person)field.getRecord();
        if (field.isNull())
        {
            
        }
        else
        {
            ContactTypeModel recContactType = (ContactTypeModel)this.getContactType(recPerson);
            if (recContactType != null)
            {   // Always
                UserInfo recUserInfo = (UserInfo)field.getReference();
                if (recUserInfo != null)
                    if ((recUserInfo.getEditMode() == DBConstants.EDIT_CURRENT) || (recUserInfo.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    {
                        recUserInfo.addPropertyListeners();
                        int iOldOpenMode = recUserInfo.setOpenMode(recUserInfo.getOpenMode() & ~DBConstants.OPEN_READ_ONLY);
                        try {
                            recUserInfo.edit();
                            recUserInfo.getField(UserInfo.CONTACT_TYPE_ID).moveFieldToThis(((Record)recContactType).getCounterField());
                            recUserInfo.getField(UserInfo.CONTACT_ID).moveFieldToThis(recPerson.getCounterField());
                            recUserInfo.set();
                        } catch (DBException ex) {
                            ex.printStackTrace();
                        } finally {
                            recUserInfo.setOpenMode(iOldOpenMode);
                        }
                    }
            }
        }
        return super.fieldChanged(bDisplayOption, iMoveMode);
    }
    /**
     * GetContactType Method.
     */
    public ContactType getContactType(Person recPerson)
    {
        if (m_recContactType == null)
        {
            m_recContactType = (ContactType)Record.makeRecordFromClassName(ContactTypeModel.CONTACT_TYPE_FILE, this.getOwner().getRecord().findRecordOwner());
            if (((Record)m_recContactType).getRecordOwner() != null)
                ((Record)m_recContactType).getRecordOwner().removeRecord((Record)m_recContactType);
        }
        return m_recContactType.getContactType(recPerson);
    }

}
