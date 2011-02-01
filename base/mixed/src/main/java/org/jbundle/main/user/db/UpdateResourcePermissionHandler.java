/**
 *  @(#)UpdateResourcePermissionHandler.
 *  Copyright © 2010 tourapp.com. All rights reserved.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;

/**
 *  UpdateResourcePermissionHandler - .
 */
public class UpdateResourcePermissionHandler extends FileListener
{
    /**
     * Default constructor.
     */
    public UpdateResourcePermissionHandler()
    {
        super();
    }
    /**
     * UpdateResourcePermissionHandler Method.
     */
    public UpdateResourcePermissionHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        super.init(record);
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
            if (this.getOwner().getField(UserResource.kResourceClass).isModified())
                this.updateResourcePermissions();
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }
    /**
     * UpdateResourcePermissions Method.
     */
    public void updateResourcePermissions()
    {
        UserPermission recUserPermission = new UserPermission(Utility.getRecordOwner(this.getOwner()));
        
        recUserPermission.addListener(new SubFileFilter(this.getOwner()));
        
        try {
            while (recUserPermission.hasNext())
            {
                recUserPermission.next();
                recUserPermission.edit();
                recUserPermission.getField(UserPermission.kUserResourceID).setModified(true);   // Fake a mod, so the group permissions will be updated
                recUserPermission.set();
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
        
        recUserPermission.free();
    }

}
