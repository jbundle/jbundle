/**
 * @(#)UpdateGroupPermissionHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;

/**
 *  UpdateGroupPermissionHandler - .
 */
public class UpdateGroupPermissionHandler extends FileListener
{
    protected int m_iOldGroupID = -1;
    protected Record m_recUserPermission = null;
    /**
     * Default constructor.
     */
    public UpdateGroupPermissionHandler()
    {
        super();
    }
    /**
     * UpdateGroupPermissionHandler Method.
     */
    public UpdateGroupPermissionHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        m_recUserPermission = null;
        super.init(record);
    }
    /**
     * Free Method.
     */
    public void free()
    {
        if (m_recUserPermission != null)
            m_recUserPermission.free();
        m_recUserPermission = null;
        super.free();
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        super.doNewRecord(bDisplayOption);
        this.setOldGroupID(-1);
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        super.doValidRecord(bDisplayOption);
        this.setOldGroupID((int)this.getOwner().getField(UserPermission.kUserGroupID).getValue());
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * ADD_TYPE - Before a write.
     * UPDATE_TYPE - Before an update.
     * DELETE_TYPE - Before a delete.
     * AFTER_UPDATE_TYPE - After a write or update.
     * LOCK_TYPE - Before a lock.
     * SELECT_TYPE - After a select.
     * DESELECT_TYPE - After a deselect.
     * MOVE_NEXT_TYPE - After a move.
     * AFTER_REQUERY_TYPE - Record opened.
     * SELECT_EOF_TYPE - EOF Hit.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {
        if ((iChangeType == DBConstants.AFTER_UPDATE_TYPE)
            || (iChangeType == DBConstants.AFTER_ADD_TYPE)
            || (iChangeType == DBConstants.AFTER_DELETE_TYPE))
        {
            int iGroupID = (int)this.getOwner().getField(UserPermission.kUserGroupID).getValue();
            if (m_iOldGroupID != -1)
                if (iGroupID != m_iOldGroupID)
                    this.updateGroupPermission(m_iOldGroupID);
            this.updateGroupPermission(iGroupID);
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }
    /**
     * UpdateGroupPermission Method.
     */
    public void updateGroupPermission(int iGroupID)
    {
        if (m_recUserPermission == null)
            m_recUserPermission = new UserPermission(this.getOwner().findRecordOwner());
        Record m_recUserGroup = ((ReferenceField)m_recUserPermission.getField(UserPermission.kUserGroupID)).getReferenceRecord();
        m_recUserGroup.setOpenMode(m_recUserGroup.getOpenMode() & ~DBConstants.OPEN_READ_ONLY); // Read and write
        if (m_recUserPermission.getListener(SubFileFilter.class) == null)
            m_recUserPermission.addListener(new SubFileFilter(m_recUserGroup));
        try {
            m_recUserGroup.addNew();
            m_recUserGroup.getCounterField().setValue(iGroupID);
            if (m_recUserGroup.seek(null))
            {
                m_recUserGroup.edit();
                
                StringBuffer sb = new StringBuffer();
                m_recUserPermission.close();
                while (m_recUserPermission.hasNext())
                {
                    m_recUserPermission.next();
                    
                    Record recUserResource = ((ReferenceField)m_recUserPermission.getField(UserPermission.kUserResourceID)).getReference();
                    String strResource = recUserResource.getField(UserResource.kResourceClass).toString();
                    StringTokenizer tokenizer = new StringTokenizer(strResource, "\n\t ,");
                    while (tokenizer.hasMoreTokens())
                    {
                        String strClass = tokenizer.nextToken();
                        int startThin = strClass.indexOf(Constants.THIN_SUBPACKAGE, 0);
                        if (startThin != -1)    // Remove the ".thin" reference
                            strClass = strClass.substring(0, startThin) + strClass.substring(startThin + Constants.THIN_SUBPACKAGE.length());
                        if (strClass.length() > 0)
                        {
                            sb.append(strClass).append('\t');
                            sb.append(m_recUserPermission.getField(UserPermission.kAccessLevel).toString()).append('\t');
                            sb.append(m_recUserPermission.getField(UserPermission.kLoginLevel).toString()).append("\t\n");
                        }
                    }
                }
                
                m_recUserGroup.getField(UserGroup.kAccessMap).setString(sb.toString());
                m_recUserGroup.set();
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }
    /**
     * SetOldGroupID Method.
     */
    public void setOldGroupID(int iOldGroupID)
    {
        m_iOldGroupID = iOldGroupID;
    }

}
