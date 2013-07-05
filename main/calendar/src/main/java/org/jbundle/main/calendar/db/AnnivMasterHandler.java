/**
 * @(#)AnnivMasterHandler.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.calendar.db;

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
import org.jbundle.main.calendar.screen.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.model.message.*;

/**
 *  AnnivMasterHandler - Update the appointment detail when this file is updated.
 */
public class AnnivMasterHandler extends FileListener
{
    protected Anniversary m_recAnniversary = null;
    protected CalendarControl m_recCalendarControl = null;
    /**
     * Default constructor.
     */
    public AnnivMasterHandler()
    {
        super();
    }
    /**
     * AnnivMasterHandler Method.
     */
    public AnnivMasterHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        m_recAnniversary = null;
        m_recCalendarControl = null;
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
        AnnivMaster recAnnivMaster = (AnnivMaster)this.getOwner();
        if (iChangeType == DBConstants.AFTER_ADD_TYPE)
        {
            Object bookmark = recAnnivMaster.getLastModified(DBConstants.BOOKMARK_HANDLE);
            try {
                recAnnivMaster.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                
                Calendar calStart = ((DateTimeField)this.getCalendarControl().getField(CalendarControl.START_ANNIV_DATE)).getCalendar();
                Calendar calEnd = ((DateTimeField)this.getCalendarControl().getField(CalendarControl.END_ANNIV_DATE)).getCalendar();
        
                recAnnivMaster.addAppointments(this.getAnniversary(), calStart, calEnd);
        
                recAnnivMaster.addNew();
            } catch (DBException ex) {
                ex.printStackTrace();
            }
        }
        if (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
        {
            Calendar calStart = ((DateTimeField)this.getCalendarControl().getField(CalendarControl.START_ANNIV_DATE)).getCalendar();
            Calendar calEnd = ((DateTimeField)this.getCalendarControl().getField(CalendarControl.END_ANNIV_DATE)).getCalendar();
        
            recAnnivMaster.removeAppointments(this.getAnniversary());
            recAnnivMaster.addAppointments(this.getAnniversary(), calStart, calEnd);
        }
        if (iChangeType == DBConstants.AFTER_DELETE_TYPE)
        {
            recAnnivMaster.removeAppointments(this.getAnniversary());
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }
    /**
     * GetAnniversary Method.
     */
    public Anniversary getAnniversary()
    {
        if (m_recAnniversary == null)
            m_recAnniversary = new Anniversary(this.getOwner().getRecordOwner());
        return m_recAnniversary;
    }
    /**
     * GetCalendarControl Method.
     */
    public CalendarControl getCalendarControl()
    {
        if (m_recCalendarControl == null)
            m_recCalendarControl = new CalendarControl(this.getOwner().getRecordOwner());
        return m_recCalendarControl;
    }

}
