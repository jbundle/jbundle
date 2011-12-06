/**
 * @(#)CalendarModelSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.calendar.remote;

import org.jbundle.base.db.Record;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.opt.TableModelSession;
import org.jbundle.main.calendar.db.CalendarEntry;
import org.jbundle.thin.base.remote.RemoteException;

/**
 *  CalendarModelSession - .
 */
public class CalendarModelSession extends TableModelSession
{
    /**
     * Default constructor.
     */
    public CalendarModelSession() throws RemoteException
    {
        super();
    }
    /**
     * CalendarModelSession Method.
     */
    public CalendarModelSession(BaseSession parentSessionObject, Record record, Object objectID) throws RemoteException
    {
        this();
        this.init(parentSessionObject, record, objectID);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseSession parentSessionObject, Record record, Object objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Override this to open the main file for this session.
     */
    public Record openMainRecord()
    {
        return new CalendarEntry(this);
    }

}
