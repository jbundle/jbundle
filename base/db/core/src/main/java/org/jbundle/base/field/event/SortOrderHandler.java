/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;


import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.KeyField;
import org.jbundle.base.db.QueryRecord;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;


/**
 * This class is a very flexible behavior for redisplaying a grid
 * record on a grid screen in different orders. This is commonly added
 * to a field which is tied to the header descriptions on a grid screen.
 * When a button is pressed, the field is changed to the index of the
 * button which triggers the field changed method of this class.
 * This class maintains the map of indexes to grid orders. The class
 * then changes the record order and requeries the record.
 * For example, you might code a vendor grid screen display as:
 * <pre>
 * QueryKeyHandler keyBehavior = new QueryKeyHandler(this);
 * keyBehavior.setGridTable(Vendor.kNameSortKey, recVendor, 0);
 * keyBehavior.setGridTable(Vendor.kCodeKey, recVendor, 1);
 * recScreenRecord.getField(VendorScreenRecord.kVendorKey).addListener(keyBehavior);
 * </pre>
 * Then, index 0 if name order, index 1 is code order.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SortOrderHandler extends FieldReSelectHandler
{
    /**
     * Maximum key areas to handle.
     */
    private final int MAX_ARRAY_SIZE = 12;
    /**
     * Key Areas.
     */
    protected int m_iKeyAreaArray[] = null;
    /**
     * The next open row in the key area area.
     */
    protected int m_iNextArrayIndex = 0;
    /**
     * The grid record.
     */
    protected Record m_recGrid = null;
    /**
     * Try to create the sort order if it isn't found.
     */
    protected boolean m_bCreateSortOrder = false;

    /**
     * Constructor.
     */
    public SortOrderHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param gridScreen The grid screen you will be requering.
     */
    public SortOrderHandler(GridScreen gridScreen)
    {
        this();
        this.init(null, gridScreen, (Record)null, false);
    }
    /**
     * Constructor.
     * @param gridScreen The grid screen you will be requering.
     */
    public SortOrderHandler(GridScreen gridScreen, boolean bCreateSortOrder)
    {
        this();
        this.init(null, gridScreen, (Record)null, bCreateSortOrder);
    }
    /**
     * Constructor - Doesn't require a gridRecord, also doesn't repaint screen on change (for reports).
     * @param recGrid The grid record.
     */
    public SortOrderHandler(Record recGrid)
    {
        this();
        this.init(null, null, recGrid, false);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param gridScreen The grid screen you will be requering.
     * @param recGrid The grid record.
     */
    public void init(BaseField field, GridScreen gridScreen, Record recGrid, boolean bCreateSortOrder)
    {
        m_recGrid = recGrid;
        m_bCreateSortOrder = bCreateSortOrder;
        super.init(field, gridScreen, null);
        m_iKeyAreaArray = new int[MAX_ARRAY_SIZE];
        for (int i = 0; i < MAX_ARRAY_SIZE; i++)
            m_iKeyAreaArray[i] = -1;
        m_iNextArrayIndex = 0;
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
        throw new CloneNotSupportedException(); // For now
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
        {
            this.setupGridOrder();      // Set the initial grid order
            if (m_gridScreen != null)
            {
                m_gridScreen.setConverter(this.getOwner());   // The grid screen will automatically change this field to match the key order.
                this.getOwner().addComponent(m_gridScreen);
            }
        }
    }
    /**
     * Default call; gridTable=mainRecord, index=next.
     * @param iKeyArea The key area to set to the next key.
     */
    public void setGridTable(int iKeyArea)
    {
        this.setGridTable(iKeyArea, null, -1);
    }
    /**
     * Set an index to a key area.
     * @param iKeyArea The key area to set to this index.
     * @param gridTable The record to set.
     * @param index The index to relate to this keyarea/gridtable combo.
     */
    public void setGridTable(int iKeyArea, Record gridTable, int index)
    {
        if (index == -1)
            index = m_iNextArrayIndex;  // Next available
        m_iNextArrayIndex = Math.max(m_iNextArrayIndex, index+1);
        if (gridTable == null) if (m_gridScreen != null)
            gridTable = m_gridScreen.getMainRecord();
        if (gridTable != null) if (m_gridScreen != null) if (gridTable != m_gridScreen.getMainRecord())
        {
            if (m_gridScreen.getMainRecord() instanceof QueryRecord)
                iKeyArea = ((QueryRecord)m_gridScreen.getMainRecord()).setGridFile(gridTable, iKeyArea);    // Sets the key order and gridtable (even if only one file)
        }
        m_iKeyAreaArray[index] = iKeyArea;
    }
    /**
     * Set the grid order to the value in this field.
     * @return an error code.
     */
    public int setupGridOrder()
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        boolean bOrder = DBConstants.ASCENDING;
        int iKeyOrder = (int)((NumberField)this.getOwner()).getValue();
        if (iKeyOrder == 0)
            return DBConstants.KEY_NOT_FOUND;
        if (iKeyOrder < 0)
        {
            bOrder = DBConstants.DESCENDING;
            iKeyOrder = -iKeyOrder;
        }
        iKeyOrder--;    // 0 Based
        if (iKeyOrder < m_iNextArrayIndex)
            iKeyOrder = m_iKeyAreaArray[iKeyOrder];   // Get key order from internal array
        else
        { // They chose a column that was was not specified... Get the desc and try to sort it by the field
            if (m_gridScreen != null)
            {
                int iColumn = iKeyOrder + 1 + m_gridScreen.getNavCount(); // grid column
                ScreenField sField = m_gridScreen.getSField(iColumn);
                if (sField.getConverter() != null)
                    if (sField.getConverter().getField() != null)
                {
                    Record record = m_gridScreen.getMainRecord();
                    iKeyOrder = -1;     // No obvious sort order
                    for (int iKeyArea = 0; iKeyArea < record.getKeyAreaCount(); iKeyArea++)
                    {
                        KeyArea keyArea = record.getKeyArea(iKeyArea);
                        if (keyArea.getField(0) == sField.getConverter().getField())
                        {   // Is this field the first field of this key?
                            iKeyOrder = iKeyArea; // Yes, use this order
                            break;
                        }
                    }
                    if (iKeyOrder == -1)
                        if (m_bCreateSortOrder)
                        {   // Create a key to sort on
                            BaseField field = (BaseField)sField.getConverter().getField();
                            KeyArea keyArea = new KeyArea(record, DBConstants.NOT_UNIQUE, field.getFieldName() + "tempKey");
                            new KeyField(keyArea, field, DBConstants.ASCENDING);
                            iKeyOrder = record.getKeyAreaCount() - 1;
                        }
                }
            }
        }
        if (iKeyOrder < 0)
            return DBConstants.KEY_NOT_FOUND;
        KeyArea keyArea = null;
        if (m_recGrid == null)
            m_recGrid = m_gridScreen.getMainRecord();
        keyArea = m_recGrid.setKeyArea(iKeyOrder);
        if (keyArea == null)
            iErrorCode = DBConstants.KEY_NOT_FOUND;
        else
        {
            for (int i = 0; i < keyArea.getKeyFields(); i++)
            {
                KeyField keyField = keyArea.getKeyField(i);
                keyField.setKeyOrder(bOrder);
            }
        }
        
        return iErrorCode;
    }
    /**
     * The Field has Changed.
     * Change the key order to match this field's value.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = this.setupGridOrder();
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        return super.fieldChanged(bDisplayOption, iMoveMode);
    }
    /**
     * User pressed a header button; change the key value to match (and reorder).
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getFirstIndex() == e.getLastIndex())
            if (e.getFirstIndex() > 0)  // Column 0 is the form button
                this.clickColumn(e.getFirstIndex());
        JTable control = (JTable)m_gridScreen.getScreenFieldView().getControl();
        control.clearSelection(); // Column selections not allowed
    }
    /**
     * User pressed a header button; change the key value to match (and reorder).
     */
    public void clickColumn(int iIndex)
    {
        this.getOwner().setValue(iIndex - m_gridScreen.getNavCount());  // Set the grid value (will call this listener if change)
    }
    /**
     * Automatically create the sort order if it doesn't exist.
     */
    public void setCreateSortOrder(boolean bCreateSortOrder)
    {
        m_bCreateSortOrder = bCreateSortOrder;
    }
}
