/**
 * @(#)AnniversaryTableSession.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.calendar.remote;

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
import org.jbundle.base.remote.db.*;
import org.jbundle.thin.base.remote.*;
import org.jbundle.main.calendar.db.*;
import org.jbundle.base.remote.*;

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
    public AnniversaryTableSession(BaseSession parentSessionObject, Record record, Map<String, Object> objectID) throws RemoteException
    {
        this();
        this.init(parentSessionObject, record, objectID);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseSession parentSessionObject, Record record, Map<String, Object> objectID)
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
