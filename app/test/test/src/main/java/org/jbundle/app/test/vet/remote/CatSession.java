/**
 * @(#)CatSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.remote;

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
import org.jbundle.base.remote.db.*;
import org.jbundle.base.remote.*;
import org.jbundle.thin.base.remote.*;
import org.jbundle.app.test.vet.db.*;

/**
 *  CatSession - .
 */
public class CatSession extends Session
{
    /**
     * Default constructor.
     */
    public CatSession() throws RemoteException
    {
        super();
    }
    /**
     * CatSession Method.
     */
    public CatSession(BaseSession parentSessionObject, Record record, Map<String, Object> objectID) throws RemoteException
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
     * Override this to open the main file for this session.
     */
    public Record openMainRecord()
    {
        return new Cat(this);
    }
    /**
     * Add behaviors to this session.
     */
    public void addListeners()
    {
        super.addListeners();
        Vet recVet = (Vet)this.getRecord(Vet.kVetFile);
        
        this.getMainRecord().setKeyArea(Cat.kVetKey);
        this.getMainRecord().addListener(new SubFileFilter(recVet));
    }

}
