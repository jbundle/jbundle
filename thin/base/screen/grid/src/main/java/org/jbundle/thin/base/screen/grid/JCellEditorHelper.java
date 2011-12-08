/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.grid;

import java.util.EventObject;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

/**
 * An editor helper passes table comands on to the table listeners.
 * The code in this class is taken mostly from  DefaultCellEditor.
 * @author  Don Corley don@tourgeek.com.
 * @version 1.0.0
 */
public class JCellEditorHelper extends Object
{
	private static final long serialVersionUID = 1L;

	/**
     * The listener list.
     */
    protected EventListenerList listenerList = new EventListenerList();
    /**
     * Only one <code>ChangeEvent</code> is needed per button
     * instance since the
     * event's only state is the source property.  The source of events
     * generated is always "this".
     */
    protected transient ChangeEvent changeEvent;
    /**
     * Is enabled?
     */
    protected boolean m_bIsEnabled = true;

    /**
     * Creates new JCellEditorHelper.
     */
    public JCellEditorHelper()
    {
        super();
    }
    /**
     * Creates new JCellEditorHelper.
     */
    public JCellEditorHelper(Object obj)
    {
        this();
    }
    /**
     * Is this cell editable.
     * From the TableCellEditor interface.
     * @return true So you can click the button.
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        return m_bIsEnabled;
    }
    /**
     * Is this cell editable.
     * From the TableCellEditor interface.
     * @return true So you can click the button.
     */
    public void setEnabled(boolean bIsEnabled)
    {
        m_bIsEnabled = bIsEnabled;
    }
    /**
     * Stop cell editing.
     * From the TableCellEditor interface.
     * @return true.
     */
    public boolean stopCellEditing()
    {
        fireEditingStopped();
        return true;
    }
    /**
     * Cancel editing.
     * From the TableCellEditor interface.
     */
    public void cancelCellEditing()
    {
        fireEditingCanceled();
    }
    /**
     * Add a cell editor listener.
     */
    public void addCellEditorListener(CellEditorListener l)
    {
        listenerList.add(CellEditorListener.class, l);
    }
    /**
     * Remove a cell editor listener.
     * The CellEditorHelper handles this.
     * From the CellEditor interface.
     */
    public void removeCellEditorListener(CellEditorListener l)
    {
    listenerList.remove(CellEditorListener.class, l);
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingStopped() {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==CellEditorListener.class) {
        // Lazily create the event:
        if (changeEvent == null)
            changeEvent = new ChangeEvent(this);
        ((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
        }        
    }
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingCanceled() {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==CellEditorListener.class) {
        // Lazily create the event:
        if (changeEvent == null)
            changeEvent = new ChangeEvent(this);
        ((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
        }        
    }
    }
}
