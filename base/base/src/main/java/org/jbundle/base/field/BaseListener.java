/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * BaseListener - Base BaseListener.
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.event.MultDepHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * BaseListener - Base BaseListener class.
 * <p>Note: Typically, you do not call listeners with the owner as the
 * first param as specified in the
 * init() method. This is because behaviors are usually connected
 * and disconnected from their owners.
 * <p>
 * Do your initialization and destruction in the setOwner() method! (init
 * when the owner is != null else destroy)
 * <pre>
 * For Example:
 * xyz = new XYZBehavior(null);
 * obj.addListener(xyz);    // Add functionality
 * obj.removeListener(xyz);   // Remove functionality
 * </pre>
 */
public class BaseListener extends Object
{
    /**
     * My owner.
     */
    protected ListenerOwner m_owner = null;
    /**
     * The next listener in my listener chain.
     */
    protected BaseListener m_nextListener = null;
    /**
     * Is this listener currently enabled?
     */
    protected boolean m_bEnabled = true;
    /**
     * This listener is dependent on me, If I'm freed, the dependent listener is also freed.
     * If you have more than one listener dependent on this, use the MultiDepHandler.
     */
    protected BaseListener m_dependentListener = null;
    /**
     * Enable state is dependent on this listener.
     * Note: This doesn't have to be managed since the dependent listener HAS to be in the same record.
     */
    protected BaseListener m_dependentStateListener = null;

    /**
     * Constructor.
     */
    public BaseListener()
    {
        super();
    }
    /**
     * Constructor.
     * @param owner My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public BaseListener(ListenerOwner owner)
    {
        this();
        this.init(owner);
    }
    /**
     * Constructor.
     * @param owner My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(ListenerOwner owner)
    {
        m_owner = owner;
        m_nextListener = null;
        m_bEnabled = true;
        m_dependentListener = null;
        m_dependentStateListener = null;
        if (owner != null)
            owner.addListener(this);
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        m_dependentStateListener = null;    // This doesn't have to be managed since the dependent listener HAS to be in the same record.
        this.removeListener(false);   // remove it (But don't free it - duh)
        if (m_nextListener != null)
        {   // Never - just being careful
            m_nextListener.free();
            m_nextListener = null;
        }
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null;
    }
    /**
     * Set the field or file that owns this listener.
     * Note: There is no getOwner() method here... This is because
     * the specific listeners return the correct owner class on getOwner().
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        m_owner = owner;
    }
    /**
     * Get this listener's owner.
     * Usually, you call the getOwner() method of your listener.
     * @return My owner.
     */
    public ListenerOwner getListenerOwner()
    {
        return m_owner;
    }
    /**
     * Add a listener to the end of the chain.
     * @param listener The listener to add.
     */
    public void doAddListener(BaseListener listener)
    {
        if (m_nextListener != null)
            m_nextListener.doAddListener(listener);
        else
            m_nextListener = listener;
    }
    /**
     * Remove this listener from the chain.
     * This just calls the owner's removelistener(true) method which should do the right stuff.
     * @param bFreeFlag If true, free this listener.
     */
    public void removeListener(boolean bFreeFlag)
    {
        // Overidden in FieldListener and FileListener
        if (m_dependentListener != null)
        {
            BaseListener dependentListener = m_dependentListener;
            this.setDependentListener(null);    // In case you want to delete me!
            dependentListener.removeListener(true);   // free it (and re-chain)
        }
        if (m_owner != null)
            m_owner.removeListener(this, bFreeFlag); // remove it (and re-chain)
        else if (bFreeFlag)
            this.free();    // If there is no owner, make sure you free.
    }
    /**
     * Remove a specific listener from the chain.
     * @param listener The listener to remove.
     * @param bFreeFlag If true, free the listener.
     */
    public void removeListener(BaseListener listener, boolean bFreeFlag)
    {
        if (m_nextListener != null)
        {
            if (m_nextListener == listener)
            {
                m_nextListener = listener.getNextListener();
                listener.unlink(bFreeFlag);   // remove theBehavior from the linked list
            }
            else
                m_nextListener.removeListener(listener, bFreeFlag);
        } // Remember to free the listener after removing it!
    }
    /**
     * Unlink this listener from the owner and next listener.
     * This is called in the remove listener method.
     * @param bDeleteFlag If true, free this object.
     */
    public void unlink(boolean bDeleteFlag)
    {
        this.setNextListener(null);
        this.setOwner(null);
        if (bDeleteFlag)
            this.free();
    }
    /**
     * Get the dependent listener (of null if none).
     * @return The dependent behavior.
     */
    public BaseListener getDependentListener()
    {
        return m_dependentListener;
    }
    /**
     * Is this listener enabled?
     * This is different from isEnabled in that isEnabled determines if the listener
     * is enabled taking in account the environment (ie., Am I enabled in the SLAVE space?)
     * @return true if enabled.
     */
    public boolean isEnabled()
    {
        return this.isEnabledListener();
    }
    /**
     * Is this listener enabled flag set?
     * @return true if enabled.
     */
    public boolean isEnabledListener()
    {
        if (m_dependentStateListener != null)
            return m_dependentStateListener.isEnabledListener();
        return m_bEnabled;
    }
    /**
     * Enable/Disable this listener.
     * @param flag If true, enable this listener.
     * @returns old state.
     */
    public boolean setEnabledListener(boolean flag)
    {
        boolean bOldState = m_bEnabled;
        m_bEnabled = flag;
        return bOldState;
    }
    /**
     * Get the next listener in the listener chain.
     * @return The next listener or null if this is the end of the chain.
     */
    public BaseListener getNextListener()
    {
        return m_nextListener;
    }
    /**
     * Get then next enabled listener in the chain.
     * @return The next enabled listener (of null if none).
     */
    public BaseListener getNextEnabledListener()
    {
        if (m_nextListener == null)
            return null;
        if (m_nextListener.isEnabled())
            return m_nextListener;
        else
            return m_nextListener.getNextEnabledListener();
    }
    /**
     * Set a dependent listener.
     * This listener is dependent on me, If I'm freed, the dependent listener is also freed.
     * If there is already a dependent listener, then a three (etc) way dependency is set up.
     * @param dependentListener The dependentListener to set.
     */
    public void setDependentListener(BaseListener dependentListener)
    {   // Set up the bi-dependecy
        BaseListener oldDependentListener = m_dependentListener;
        m_dependentListener = dependentListener;
        if (oldDependentListener != null)
        {
            if (oldDependentListener.getDependentListener() == this)
                oldDependentListener.setDependentListener(null);    // No longer dependent on listener!
        }
        if (m_dependentListener != null)
        {
            if (m_dependentListener.getDependentListener() == null)
                m_dependentListener.setDependentListener(this);   // Dependent on each other!
            else if (m_dependentListener.getDependentListener() != this)
            {
                MultDepHandler multDepBehavior = new MultDepHandler(null);
                m_dependentListener = null;
                oldDependentListener = dependentListener.getDependentListener();
                dependentListener.setDependentListener(null);
                multDepBehavior.setDependentListener(dependentListener);    // Add this to the mult-dependent list
                multDepBehavior.setDependentListener(oldDependentListener);   // Add this to the mult-dependent list
                multDepBehavior.setDependentListener(this);   // Add this to the mult-dependent list
                m_dependentListener = multDepBehavior;
            }
        }
    }
    /**
     * Set the next listener in the listener chain.
     * Note: You can pass the full class name, or the short class name or (preferably) the class.
     * @param listener The name of the class I'm looking for.
     * @return The old "next" listener.
     */
    public BaseListener setNextListener(BaseListener listener)
    {
        BaseListener listenerTemp = m_nextListener;
        m_nextListener = listener;
        return listenerTemp;
    }
    /**
     * Get the listener with this identifier.
     * @return The first listener of this class or null if no match.
     */
    public BaseListener getListener(Object strBehaviorClass)
    {
        return this.getListener(strBehaviorClass, true);   // By default need exact match
    }
    /**
     * Get the listener with this class identifier.
     * Note: You can pass the full class name, or the short class name or (preferably) the class.
     * @param bExactMatch Only returns classes that are an exact match... if false, return classes that are an instanceof the class
     * @param strBehaviorClass The name of the class I'm looking for.
     * @return The first listener of this class or null if no match.
     */
    public BaseListener getListener(Object strBehaviorClass, boolean bExactMatch)
    {
        BaseListener listener = m_nextListener;
        if (listener == null)
            return null;
        if (!bExactMatch)
        {
            try {
                if (strBehaviorClass instanceof String)
                    strBehaviorClass = Class.forName((String)strBehaviorClass);
                if (((Class<?>)strBehaviorClass).isAssignableFrom(listener.getClass()))
                    return listener;
                else
                    return listener.getListener(strBehaviorClass, bExactMatch);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        if (strBehaviorClass instanceof String)
        {
            String strClass = listener.getClass().getName();
            if (((String)strBehaviorClass).indexOf('.') == -1)
            {   // If identifier is not fully qualified, strip the package from the class
                if (strClass.lastIndexOf('.') != -1)
                    strClass = strClass.substring(strClass.lastIndexOf('.') + 1);
            }
            if (strClass.equalsIgnoreCase((String)strBehaviorClass))
                return listener;
        }
        else if (listener.getClass().equals(strBehaviorClass))
            return listener;
        return listener.getListener(strBehaviorClass, bExactMatch);
    }
    /**
     * Enable state is dependent on this listener.
     * NOTE: See the method overidding this in FileListener and FieldListener 
     * which checks that the dependent listener HAS to be in the same record.
     * @param dependentStateListener The listener to get the enabled state from.
     */
    public void setDependentStateListener(BaseListener dependentStateListener)
    {
        m_dependentStateListener = dependentStateListener;
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * Note: This is a FileListener method, but I put it down here so this message will be propagated.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {   // Return an error to stop the change
        int iErrorCode = DBConstants.NORMAL_RETURN;
        if (iChangeType == DBConstants.BEFORE_FREE_TYPE)
        {   // This is the only type that I would pass on (So dependent knows it will be freed)
            BaseListener dependentListener = this.getDependentListener();
            if (dependentListener != null)
                if (dependentListener.isEnabled())
            {
                boolean bEnabled = dependentListener.setEnabledListener(false);
                iErrorCode = dependentListener.doRecordChange(field, iChangeType, bDisplayOption);
                dependentListener.setEnabledListener(bEnabled);
            }
        }
        return iErrorCode;
    }
}
