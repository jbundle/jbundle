/**
 * @(#)CalcTimeoutTimeHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db;

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
import org.jbundle.base.message.trx.message.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.trx.transport.screen.*;
import org.jbundle.model.message.*;
import org.jbundle.main.db.base.*;

/**
 *  CalcTimeoutTimeHandler - Calc the timeout time for this log entry.
 */
public class CalcTimeoutTimeHandler extends FileListener
{
    /**
     * Default constructor.
     */
    public CalcTimeoutTimeHandler()
    {
        super();
    }
    /**
     * CalcTimeoutTimeHandler Method.
     */
    public CalcTimeoutTimeHandler(Record record)
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
        switch (iChangeType)
        {
            case DBConstants.ADD_TYPE:
            case DBConstants.UPDATE_TYPE:
                if (this.getOwner().getField(MessageLog.kLastChanged) != null)  // Always
                    if (this.getOwner().getField(MessageLog.kTimeoutSeconds).getValue() > 0)
                        if (this.getOwner().getField(MessageLog.kTimeoutTime).getValue() == 0)  // Don't change timeout after it was set
                    {
                        if (MessageStatus.SENT.equalsIgnoreCase(((ReferenceField)this.getOwner().getField(MessageLog.kMessageStatusID)).getReference().getField(MessageStatus.kCode).toString()))
                        {
                            Calendar cal = ((DateTimeField)this.getOwner().getField(MessageLog.kLastChanged)).getCalendar();
                            int iSeconds = (int)this.getOwner().getField(MessageLog.kTimeoutSeconds).getValue();
                            cal.add(Calendar.SECOND, iSeconds);
                            ((DateTimeField)this.getOwner().getField(MessageLog.kTimeoutTime)).setCalendar(cal, bDisplayOption, DBConstants.SCREEN_MOVE);
                        }
                        else
                        {   // Clear the timeout
                            ((DateTimeField)this.getOwner().getField(MessageLog.kTimeoutTime)).setData(null, bDisplayOption, DBConstants.SCREEN_MOVE);                    
                        }
                    }
                break;
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }

}
