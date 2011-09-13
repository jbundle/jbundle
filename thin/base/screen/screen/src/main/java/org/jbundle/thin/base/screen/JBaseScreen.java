/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import org.jbundle.model.DBException;
import org.jbundle.model.Freeable;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;


/**
 * A Base screen which contains a record.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public class JBaseScreen extends JBasePanel
{
	private static final long serialVersionUID = 1L;

	/**
     * Other records.
     */
    protected Vector<FieldList> m_vFieldListList = null;

    /**
     *  JBaseScreen Class Constructor.
     */
    public JBaseScreen()
    {
        super();
    }
    /**
     * JBaseScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JBaseScreen(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object record)
    {
        super.init(parent, record);
        if (record instanceof FieldList)
            this.addFieldList((FieldList)record, 0);
        else
            this.addFieldList(this.buildFieldList(), 0);
        if (this.getFieldList() != null)
        {
            boolean bAddCache = true;
            if (this instanceof JScreen)
                bAddCache = false;
            if (this.getFieldList().getTable() == null)
                this.getBaseApplet().linkNewRemoteTable(this.getFieldList(0), bAddCache);
            if (this.getFieldList().getTable() != null)
                if (this.getFieldList().getEditMode() == Constants.EDIT_NONE)
            {
                try   {
                    this.getFieldList().getTable().addNew();
                } catch (DBException ex)    {
                    ex.printStackTrace(); // Never.
                }
            }
        }
    }
    /**
     * Free the resources held by this object.
     * Besides freeing all the sub-screens, this method disconnects all of my
     * fields from their controls.
     */
    public void free()
    {
        super.free();   // Free the sub-screens first!

        if (m_vFieldListList != null)
        {
            for (int i = m_vFieldListList.size() - 1; i >= 0; i--)
            {   // Step 1 - Disconnect the controls from the fields
                FieldList fieldList = this.getFieldList(i);
                if (fieldList != null)
                {
                    this.disconnectControls(fieldList);
                    if (fieldList.getOwner() == this)
                        fieldList.free();
                }
            }
            if (m_vFieldListList != null)
                m_vFieldListList.clear();   // Note JBaseField.free() frees all the field lists
            m_vFieldListList = null;
        }
    }
    /**
     * Return the FieldList describing the screen's fields.
     * @return The fieldlist for this screen.
     */
    public FieldList getFieldList()
    {
        return this.getFieldList(0);
    }
    /**
     * Return the FieldList describing the screen's fields.
     * Note: This is used so you can override this method to use more that one field list.
     * @return The fieldlist for this screen (null if you are past the end of the list).
     */
    public FieldList getFieldList(int iIndex)
    {
        if (m_vFieldListList == null)
            return null;
        if (iIndex >= m_vFieldListList.size())
            return null;
        return (FieldList)m_vFieldListList.get(iIndex);
    }
    /**
     * Return the FieldList describing the screen's fields.
     * Note: This is used so you can override this method to use more that one field list.
     * @return The fieldlist for this screen (null if you are past the end of the list).
     */
    public FieldList getFieldList(String strFileName)
    {
        if (m_vFieldListList == null)
            return null;
        for (int i = 0; ; i++)
        {   // Step 1 - Disconnect the controls from the fields
            FieldList fieldInList = this.getFieldList(i);
            if (fieldInList == null)
                break;
            if (strFileName.equalsIgnoreCase(fieldInList.getTableNames(false)))
                return fieldInList; // Found, already there.
        }
        return null;
    }
    /**
     * Add another fieldlist.
     * @param fieldList The field list to add.
     */
    public void addFieldList(FieldList fieldList)
    {
        this.addFieldList(fieldList, -1);
    }
    /**
     * Add another fieldlist.
     * @param fieldList The field list to add.
     */
    public void addFieldList(FieldList fieldList, int iLocation)
    {
        if (fieldList == null)
            return;
        if (m_vFieldListList == null)
            m_vFieldListList = new Vector<FieldList>();
        for (int i = 0; ; i++)
        {   // Step 1 - Disconnect the controls from the fields
            FieldList fieldInList = this.getFieldList(i);
            if (fieldInList == null)
                break;
            if (fieldList == fieldInList)
                return; // Found, already there.
        }
        if (iLocation == -1)
            m_vFieldListList.add(fieldList);
        else
            m_vFieldListList.add(iLocation, fieldList);
    }
    /**
     * Add another fieldlist.
     * @param fieldList The field list to remove (pass null to remove them all).
     */
    public void removeFieldList(FieldList fieldList)
    {
        if (m_vFieldListList == null)
            return;
        for (int i = 0; ; i++)
        {   // Step 1 - Disconnect the controls from the fields
            FieldList fieldInList = this.getFieldList(i);
            if (fieldInList == null)
                break;
            if ((fieldList == null)
                || (fieldList == fieldInList))
                    m_vFieldListList.remove(i); // Found, remove.
        }
    }
    /**
     * Build the list of fields that make up the screen.
     * Override this to create a new record.
     * @return The fieldlist for this screen.
     */
    public FieldList buildFieldList()
    {
        return null;    // If overriding class didn't set this
    }
    /**
     * Go through all the fields in this record and remove all their components.
     * Free the component if they are freeable.
     * @param fieldList The record to remove the field component references from.
     */
    public void disconnectControls(FieldList fieldList)
    {
        if (fieldList == null)
        {       // Disconnect controls from all fieldlists
            for (int i = 0; ; i++)
            {   // Step 1 - Disconnect the controls from the fields
                fieldList = this.getFieldList(i);
                if (fieldList == null)
                    break;
                this.disconnectControls(fieldList);
            }
        }
        else
        {   // Step 1 - Disconnect the controls from the fields
            for (int iFieldSeq = 0; iFieldSeq < fieldList.getFieldCount(); iFieldSeq++)
            {
                FieldInfo field = fieldList.getField(iFieldSeq);
                Component component = null;
                int iIndex = 0;
                while ((component = (Component)field.getComponent(iIndex)) != null)
                {
                    if ((component.getParent() == null)
                        || (this.isAncestorOf(component)))
                    {
                        if (component instanceof Freeable)
                            ((Freeable)component).free();
                        field.removeComponent(component); // No need to change the index
                    }
                    else
                        iIndex++; // Skip this one
                }
            }
        }
    }
    /**
     * Clear all the fields to their default values.
     * Override this.
     */
    public void resetFields()
    {
    }
    /**
     * Move the controls to the data record(s).
     * Override this.
     */
    public void controlsToFields()
    {
    }
    /**
     * Move the the data record(s) to the screen controls.
     * Override this.
     */
    public void fieldsToControls()
    {
    }
    /**
     * Clicked on a button, notify the screen.
     * @param evt The event.
     */
    public void actionPerformed(ActionEvent evt)
    {
        super.actionPerformed(evt);
    }
    /**
     * Process this action.
     * This class calls controltofields on submit and resetfields on reset.
     * @param strAction The message or command to propagate.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (strAction == Constants.SUBMIT)
        {
            this.controlsToFields();    // Move screen data to record
        }
        else if (strAction == Constants.RESET)
        {
            this.resetFields();
        }
        return super.doAction(strAction, iOptions);
    }
    /**
     * Get this field (or return null if this field doesn't belong on the screen).
     * This is the method to use to filter the items to display on the screen.
     * @param The index of this field in the record.
     * @return The fieldinfo object.
     */
    public Converter getFieldForScreen(int iIndex)
    {
        if (this.getFieldList() != null)
        {
            FieldInfo fieldInfo = this.getFieldList().getField(iIndex);
            if (fieldInfo != null)
            {
            	if (fieldInfo.isHidden())
            		return SKIP_THIS_FIELD;
                return fieldInfo.getFieldConverter();
            }
        }
        return null;
    }
    public static final FieldInfo SKIP_THIS_FIELD = new FieldInfo(null, null, -1, null, null);
}
