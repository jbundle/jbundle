/*
 * Params.java
 *
 * Created on March 6, 2000, 11:43 PM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.Action;

/** 
 * A ParamDispatcher is a utility for distributing a property change event to many property change listeners.
 * This model is also a property listener and dispacher. When a property event is passed here,
 * this object passes the property event to all the listeners.
 * This object also stores all properties in a hashtable to be retrieved by calling get(key).
 * NOTE: You must notify this object via firePropertyChange(xxx) for this to notify
 * all the listeners (.set(xx, yy) will only change the properties.
 * Also, only sends object if it changed from last time.
 * @author  Administrator
 * @version 1.0.0
 */
public class ParamDispatcher extends Hashtable<String,Object>
    implements PropertyChangeListener, Action
{
	private static final long serialVersionUID = 1L;

	/**
     * If this is non-null, only pass on these params.
     */
    protected transient String[] m_rgstrValidParams = null;
    /**
     * For synchronization.
     */
    protected transient String m_strCurrentProperty = null;
    /**
     * Propery change support.
     */
    protected transient java.beans.PropertyChangeSupport propertyChange = new java.beans.PropertyChangeSupport(this);

    /**
     * Creates new ParamDispatcher.
     */
    public ParamDispatcher()
    {
        super();
    }
    /**
     * Creates new ParamDispatcher.
     * @param rgstrValidParams If this is non-null, only pass on these params.
     */
    public ParamDispatcher(String[] rgstrValidParams)
    {
        this();
        this.init(rgstrValidParams);
    }
    /**
     * Creates new ParamDispatcher.
     * @param rgstrValidParams If this is non-null, only pass on these params.
     */
    public void init(String[] rgstrValidParams)
    {
        m_rgstrValidParams = rgstrValidParams;
    }
    /**
     * This method gets called when a bound property is changed.
     * Propogate the event to all listeners.
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        String strProperty = evt.getPropertyName();
        if (this.isValidProperty(strProperty))
        {
            Object objCurrentValue = this.get(strProperty);
            if (evt.getNewValue() != objCurrentValue)
            {
                m_strCurrentProperty = strProperty;   // Eliminate the chance of echos
                if (evt.getNewValue() != null)
                    this.put(strProperty, evt.getNewValue());
                else
                    this.remove(strProperty);
//x                this.firePropertyChange(strProperty, evt.getOldValue(), evt.getNewValue());     // Propogate the property change
                if (propertyChange != null)
                    propertyChange.firePropertyChange(evt);
                m_strCurrentProperty = null;
            }
        }
    }
    /**
     * Get the current parameters for this screen.
     * Convert the params to strings and place them in a properties object.
     */
    public Map<String,Object> getProperties()
    {
        Map<String,Object> properties = new Hashtable<String,Object>();
        ParamDispatcher params = this;
        for (Enumeration<String> e = params.keys() ; e.hasMoreElements() ;)
        {
            String strKey = e.nextElement();
            String strValue = params.get(strKey).toString();
            properties.put(strKey, strValue);
        }
        return properties;
    }
    /**
     * This method gets called when a bound property is changed.
     * Propagate the event to all listeners.
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public boolean isValidProperty(String strProperty)
    {
        if (m_rgstrValidParams != null)
        {
            for (int i = 0; i < m_rgstrValidParams.length; i++)
            {
                if (m_rgstrValidParams[i].equalsIgnoreCase(strProperty))
                { // Valid property
                    if (m_strCurrentProperty != strProperty)
                    {
                        return true;    // Valid property
                    }
                }
            }
        }
        return false;   // Not a valid property
    }
    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     * @param listener The propery change listener to add to my listeners.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener)
    {
        propertyChange.addPropertyChangeListener(listener);
    }
    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     * @param propertyName The property name.
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if (propertyChange != null)
            propertyChange.firePropertyChange(propertyName, oldValue, newValue);
    }
    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     * @param listener The propery change listener to remove from my listeners.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener)
    {
        propertyChange.removePropertyChangeListener(listener);
    }
	@Override
	public Object getValue(String key) {
		return this.get(key);
	}
	@Override
	public boolean isEnabled() {
		return true;
	}
	@Override
	public void putValue(String key, Object value) {
		this.put(key, value);
	}
	@Override
	public void setEnabled(boolean b) {
		// Not supported here
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// Dispatcher doesn't do anything, the added listeners do.
	}
}
