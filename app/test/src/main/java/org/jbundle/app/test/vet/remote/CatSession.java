/**
 * @(#)CatSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.remote;

import org.jbundle.app.test.vet.db.Cat;
import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.db.Session;
import org.jbundle.thin.base.remote.RemoteException;

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
    public CatSession(BaseSession parentSessionObject, Record record, Object objectID) throws RemoteException
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
