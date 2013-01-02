/**
 * @(#)UpdateChildrenHandler.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.db;

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
import org.jbundle.app.program.project.screen.*;

/**
 *  UpdateChildrenHandler - .
 */
public class UpdateChildrenHandler extends FileListener
{
    /**
     * Default constructor.
     */
    public UpdateChildrenHandler()
    {
        super();
    }
    /**
     * UpdateChildrenHandler Method.
     */
    public UpdateChildrenHandler(Record record)
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
        if ((iChangeType == DBConstants.AFTER_ADD_TYPE)
            || (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
            || (iChangeType == DBConstants.AFTER_DELETE_TYPE))
        {
            if ((iChangeType == DBConstants.AFTER_DELETE_TYPE)
                || (this.getOwner().getField(ProjectTask.START_DATE_TIME).isModified())
                || (this.getOwner().getField(ProjectTask.END_DATE_TIME).isModified()))
            {
                if (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
                    ((ProjectTask)this.getOwner()).updateChildren(bDisplayOption);
            }
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }

}
