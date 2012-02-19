/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.screen;



/**
 * FieldComponent.java
 *
 * Created on November 9, 2000, 2:31 AM
 */

/** 
 * ScreenComponent is a simple interface that allows a screen control
 * to get it's connection to the field information.
 * @author  Administrator
 * @version 1.0.0
 */
public interface GridScreenParent extends ScreenParent
{
    /**
     * Requery the recordset.
     */
    public void reSelectRecords();
    /**
     * Get the navigation button count.
     * @return The nav button count.
     */
    public int getNavCount();
    /**
     * Allow appends to this grid?
     * @param bAppending If true, allow appending to the record.
     */
    public void setAppending(boolean bAppending);
}
