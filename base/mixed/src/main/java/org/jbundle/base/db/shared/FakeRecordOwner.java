/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.shared;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.thread.BaseRecordOwner;
import org.jbundle.model.App;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.db.DatabaseOwner;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.ThinApplication;


/**
 * Access multiple tables as if they were overrides of the base record's table.
 * This is typically used to access multiple tables from the same overriding class,
 * such as a Dog and Cat class from an Animal class.
 */
public class FakeRecordOwner extends BaseRecordOwner
{
    /**
     *
     */
    protected BaseTable m_sharedTable = null;
    
    /**
     *
     */
    public BaseTable getSharedTable()
    {
        return m_sharedTable;
    }

    /**
     * Initialization.
     */
    public FakeRecordOwner()
    {
        super();
    }
    /**
     * Initialization.
     */
    public FakeRecordOwner(SharedBaseRecordTable sharedTable, RecordOwnerParent parent, FieldList recordMain, Map<String, Object> properties)
    {
        this();
        this.init(sharedTable, parent, recordMain, properties);
    }
    /**
     * Initialize the RecordOwner.
     * @param parentSessionObject Parent that created this session object.
     * @param record Main record for this session (opt).
     * @param objectID ObjectID of the object that this SessionObject represents (usually a URL or bookmark).
     */
    public void init(SharedBaseRecordTable sharedTable, RecordOwnerParent parent, FieldList recordMain, Map<String, Object> properties)
    {
        m_sharedTable = sharedTable;
        super.init(parent, recordMain, properties);
    }
    /**
     * Free this record owner.
     */
    public void free()
    {
        if (m_databaseFake != null)
            m_databaseFake.free();
        m_databaseFake = null;
        if (m_taskFake != null)
            m_taskFake.free();
        m_taskFake = null;
        m_sharedTable = null;
        super.free();
    }
    /**
     * Get the database owner for this recordowner.
     * Typically, the Environment is returned.
     * If you are using transactions, then the recordowner is returned, as the recordowner
     * needs private database connections to track transactions.
     * Just remember, if you are managing transactions, you need to call commit or your trxs are toast.
     * Also, you have to set the AUTO_COMMIT to false, before you init your records, so the database
     * object will be attached to the recordowner rather than the environment.
     * @return The database owner.
     */
    public DatabaseOwner getDatabaseOwner()
    {
        return this;    // FAKE Record out.
    }
    /**
     * Given the name, either get the open database, or open a new one.
     * @param strDBName The name of the database.
     * @param iDatabaseType The type of database/table.
     * @return The database (new or current).
     */
    protected BaseDatabase m_databaseFake = null;
    public BaseDatabase getDatabase(String strDBName, int iDatabaseType, Map<String, Object> properties)
    {
        if (m_databaseFake == null)
            m_databaseFake = new FakeDatabase(this, null, 0);
        return m_databaseFake;
    }
    /**
     * Set the fake db.
     * @param database
     */
    public void setFakeDatabase(BaseDatabase database)
    {
        m_databaseFake = database;
    }
    /**
     * Add this database to my database list.<br />
     * Do not call these directly, used in database init.
     * @param database The database to add.
     */
    public void addDatabase(BaseDatabase database)
    {
        // Don't do override.
    }
    /**
     * Remove this database from my database list.
     * Do not call these directly, used in database free.
     * @param database The database to free.
     * @return true if successful.
     */
    public boolean removeDatabase(BaseDatabase database)
    {
        return true;    // Don't do override.
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        Task task = super.getTask();
        if (task == null)
            task = m_taskFake;
        if (task == null)
            task = m_taskFake = new FakeTask(new FakeApplication(null, null, null), null, null);
        return task;
    }
    protected FakeTask m_taskFake = null;
    class FakeApplication extends ThinApplication
    {
        /**
         * Default constructor.
         */
        public FakeApplication()
        {
            super();
        }
        /**
         * Constructor.
         * Pass in the possible initial parameters.
         * @param env Environment is ignored in the thin context.
         * @param strURL The application parameters as a URL.
         * @param args The application parameters as an initial arg list.
         * @param applet The application parameters coming from an applet.
         */
        public FakeApplication(Object env, Map<String,Object> properties, Object applet)
        {
            this();
            this.init(env, properties, applet); // The one and only
        }
        /**
         * Initialize the Application.
         * @param env Environment is ignored in the thin context.
         * @param strURL The application parameters as a URL.
         * @param args The application parameters as an initial arg list.
         * @param applet The application parameters coming from an applet.
         */
        public void init(Object env, Map<String,Object> properties, Object applet)
        {
            if (properties == null)
                properties = new Hashtable<String,Object>();
            properties.put(Params.USER_ID, DBConstants.ANON_USER_ID);
            super.init(env, properties, applet);
        }
    }
    /**
     *
     */
    class FakeTask extends org.jbundle.thin.base.thread.AutoTask
    {
        /**
         * Constructor.
         */
        public FakeTask()
        {
            super();
        }
        /**
         * Constructor.
         * @param application The parent application.
         * @param strParams The task properties.
         */
        public FakeTask(App application, String strParams, Map<String,Object> properties)
        {
            this();
            this.init(application, strParams, properties);
        }
        /**
         * Constructor.
         * @param application The parent application.
         * @param strParams The task properties.
         */
        public void init(App application, String strParams, Map<String, Object> properties)
        {
            super.init(application, strParams, properties);
        }
        /**
         *
         */
        public void free()
        {
            App application = this.getApplication();
            super.free();
            application.free();
        }
    }
}
