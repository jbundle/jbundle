package org.jbundle.base.field.event;

/**
 * @(#)MergeTableHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.event.FileRemoveBOnCloseHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.util.DBConstants;

/**
 * This listener is used to filter a sub-table from a merge table view.
 * This listener is added to a checkmark. When the checkmark is turned to
 * true, this table is added back; false this table is removed.
 * Note that is inherits from FieldReSelectHandler, so any change will
 * requery the gridscreen.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MergeTableHandler extends FieldReSelectHandler
{
    /**
     * The parent merge record.
     */
    protected Record m_mergeRecord = null;
    /**
     * The sub-table to add and remove from the parent merge record.
     */
    protected Record m_subRecord = null;
    /**
     * The (optional) grid screen to requery on change.
     */
    protected BaseScreen m_gridScreen = null;

    /**
     * Constructor.
     */
    public MergeTableHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param mergeTable The parent merge record.
     * @param subTable The sub-record to add and remove from the parent merge record.
     * @param gridScreen The (optional) grid screen to requery on change.
     */
    public MergeTableHandler(Record mergeTable, Record subTable, BaseScreen gridScreen)
    {
        this();
        this.init(null, mergeTable, subTable, gridScreen);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param mergeTable The parent merge record.
     * @param subTable The sub-record to add and remove from the parent merge record.
     * @param gridScreen The (optional) grid screen to requery on change.
     */
    public void init(BaseField field, Record mergeTable, Record subTable, BaseScreen gridScreen)
    {
        super.init(field);
        m_gridScreen = gridScreen;
        m_mergeRecord = mergeTable;
        m_subRecord = subTable;
        if (subTable != null)
        {   // Remove this listener when the file closes
            FileListener listener = new FileRemoveBOnCloseHandler(this);    // If this closes first, this will remove FileListener
            subTable.addListener(listener);   // Remove this if you close the file first
        }
    }
    /**
     * Creates a new object of the same class as this object.
     * @param field The field to add the new cloned behavior to.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone(BaseField field) throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException(); // Not supported!
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null) if (this.getOwner().getState() == false)
            m_mergeRecord.getTable().removeTable(m_subRecord.getTable()); // If this is off initially, take the table out of the loop
    }
    /**
     * The Field has Changed.
     * If this field is true, add the table back to the grid query and requery the grid table.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        boolean flag = this.getOwner().getState();
        if (flag)
            m_mergeRecord.getTable().addTable(m_subRecord.getTable());
        else
            m_mergeRecord.getTable().removeTable(m_subRecord.getTable());
        m_mergeRecord.close();  // Must requery on Add, should close on delete
        if (m_gridScreen == null)
            return DBConstants.NORMAL_RETURN;
        else
            return super.fieldChanged(bDisplayOption, iMoveMode);
    }
}
