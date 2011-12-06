/**
 * @(#)AnniversaryTableSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.calendar.remote;

import org.jbundle.base.db.Record;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.db.TableSession;
import org.jbundle.base.util.Debug;
import org.jbundle.main.calendar.db.AnnivMaster;
import org.jbundle.thin.base.remote.RemoteException;

/**
 *  AnniversaryTableSession - .
 */
public class AnniversaryTableSession extends TableSession
{
    /**
     * Default constructor.
     */
    public AnniversaryTableSession() throws RemoteException
    {
        super();
    }
    /**
     * AnniversaryTableSession Method.
     */
    public AnniversaryTableSession(BaseSession parentSessionObject, Record record, Object objectID) throws RemoteException
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
     * Add behaviors to this session.
     */
    public void addListeners()
    {
        super.addListeners();
        Debug.pl("AnniversaryTableSession/68 + Add Listeners here");
        // +++ Add a behavior to scan the newly added anniversary and add the actual entries.
    }
    /**
     * Override this to open the main file for this session.
     */
    public Record openMainRecord()
    {
        return new AnnivMaster(this);
    }

}
