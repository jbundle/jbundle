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
 * GridComponent is a simple interface that allows a screen control
 * to display a grid.
 * @author  Administrator
 * @version 1.0.0
 */
public interface GridComponent extends ScreenComponent
{
    /**
     * Requery the recordset.
     */
    public void reSelectRecords();
}
