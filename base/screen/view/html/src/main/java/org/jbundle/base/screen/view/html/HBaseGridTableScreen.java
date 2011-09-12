/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.net.URLDecoder;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;


/**
 * The window for displaying several records at once.
 */
public class HBaseGridTableScreen extends HBaseGridScreen
{
    /**
     * Constructor.
     */
    public HBaseGridTableScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HBaseGridTableScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
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
     * Move the HTML input to the screen record fields.
     * @param strSuffix value to add to the end of the field name before retrieving the param.
     * @exception DBException File exception.
     * @return bParamsFound True if params were found and moved.
     */
    public int moveControlInput(String strSuffix)
        throws DBException
    {
        int iDefaultParamsFound = DBConstants.NO_PARAMS_FOUND;
        if ((((GridScreen)this.getScreenField()).getEditing())
            && (this.getTask() instanceof ServletTask))
        {
            // First, go through all the params and find out which rows have been submitted.
            Record record = this.getMainRecord();
            Record recCurrent = null;
            ServletTask task = (ServletTask)this.getTask();
            Map<String,Object> ht = task.getRequestProperties(task.getServletRequest(), false);
            for (String strParamName : ht.keySet())
            {
                try   {
                    strParamName = URLDecoder.decode(strParamName, DBConstants.URL_ENCODING);
                } catch (java.io.UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                int iAtPosition = strParamName.indexOf('@');
                if (iAtPosition != -1)
                {
//                    String strFieldName = strParamName.substring(0, iAtPosition);
                    String strObjectID = strParamName.substring(iAtPosition + 1);
                    if (ht.get(strObjectID) == null)
                        ht.put(strObjectID, strObjectID);   // Add this unique row
                }
            }
            // Now go through each record; read the record, update the fields, and update the record (if changed).
            for (String strObjectID : ht.keySet())
            {
                int iRawDBType = (record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK);
                if ((iRawDBType == DBConstants.LOCAL)
                        || (iRawDBType == DBConstants.REMOTE)
                            || (iRawDBType == DBConstants.TABLE))
                    recCurrent = record.setHandle(strObjectID, DBConstants.OBJECT_ID_HANDLE);
                else
                {
                    recCurrent = record.setHandle(strObjectID, DBConstants.BOOKMARK_HANDLE);    // Non-persistent
                }
                if (recCurrent != null)
                    if (recCurrent.getEditMode() == DBConstants.EDIT_CURRENT)
                {
                    recCurrent.edit();      // HACK - Change to lock on mod
                    iDefaultParamsFound = super.moveControlInput('@' + strObjectID);
                    if (recCurrent.isModified())
                    {
                        recCurrent.set();
                    }
                }
            }
        }
        int iParamsFound = super.moveControlInput(strSuffix);
        if (iParamsFound == DBConstants.NORMAL_RETURN)
            iDefaultParamsFound = DBConstants.NORMAL_RETURN;
        return iDefaultParamsFound;
    }
}
