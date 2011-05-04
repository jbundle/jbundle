package org.jbundle.base.screen.view.data;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.netutil.NetUtility;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBParams;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.net.NDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabaseOwner;
import org.jbundle.thin.base.db.model.ThinPhysicalTableOwner;


/**
 * DTableAccessScreen serializes an entire file and sends it down
 * the pipe. The data is directy usable as a MTable by thin and thick clients.
 * <br/>Be careful not to request files that are too large.
 */
public class DTableAccessScreen extends DDataAccessScreen
    implements ThinPhysicalDatabaseOwner, ThinPhysicalTableOwner
{

    /**
     * Constructor.
     */
    public DTableAccessScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The DataAccessScreen model.
     * @param bEditableControl If true, this view is editable.
     */
    public DTableAccessScreen(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The DataAccessScreen model.
     * @param bEditableControl If true, this view is editable.
     */
    public void init(ScreenField model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Process an HTML get or post.
     * @param req The servlet request.
     * @param res The servlet response object.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void sendData(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
//?     res.setContentType("text/html");
//x        OutputStream out = res.getOutputStream();
        String strRecordClass = this.getProperty(NDatabase.RECORD_CLASS);
        String strTableName = this.getProperty(NDatabase.TABLE_NAME);
        String strLanguage = this.getProperty(DBParams.LANGUAGE);
        NetUtility.getNetTable(strRecordClass, strTableName, strLanguage, this, this, (RecordOwner)this.getScreenField(), res.getOutputStream());
    }
    /**
     * Set the ptable that I am an owner of.
     * @param pTable
     */
    public void setPTable(PTable pTable)
    {
    }
    /**
     * Set the pdatabase that I am an owner of.
     * @param pTable
     */
    public void setPDatabase(PDatabase pDatabase)
    {
    }
}
