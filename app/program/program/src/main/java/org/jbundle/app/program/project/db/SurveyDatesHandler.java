/**
 * @(#)SurveyDatesHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.db;

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
import org.jbundle.main.db.*;
import org.jbundle.app.program.project.screen.*;

/**
 *  SurveyDatesHandler - .
 */
public class SurveyDatesHandler extends FileListener
{
    /**
     * Default constructor.
     */
    public SurveyDatesHandler()
    {
        super();
    }
    /**
     * SurveyDatesHandler Method.
     */
    public SurveyDatesHandler(Record record)
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
                || (this.getOwner().getField(ProjectTask.kStartDateTime).isModified())
                || (this.getOwner().getField(ProjectTask.kEndDateTime).isModified()))
            {
                Record recParent = null;
                if (!this.getOwner().getField(ProjectTask.kParentProjectTaskID).isNull())
                    recParent = ((ReferenceField)this.getOwner().getField(ProjectTask.kParentProjectTaskID)).getReference();
                if (recParent != null)
                    if ((recParent.getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (recParent.getEditMode() == DBConstants.EDIT_CURRENT))
                {
                    boolean bSurveyNeeded = true;
                    if (iChangeType == DBConstants.AFTER_ADD_TYPE)
                        if ((!this.getOwner().getField(ProjectTask.kStartDateTime).isNull())
                            && (this.getOwner().getField(ProjectTask.kStartDateTime).equals(recParent.getField(ProjectTask.kEndDateTime))))
                        bSurveyNeeded = !((ProjectTask)recParent).linkLastPredecessor(this.getOwner(), bDisplayOption);
                    if (bSurveyNeeded)
                        ((ProjectTask)recParent).surveyDates(bDisplayOption);
                }
            }
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }

}
