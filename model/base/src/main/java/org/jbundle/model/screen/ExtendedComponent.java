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
 * A few other methods to handle the fancy components.
 * @author  Administrator
 * @version 1.0.0
 */
public interface ExtendedComponent extends FieldComponent
{
    /**
     * Add this icon to the list of icons alternating for this label.
     * @param icon The icon to add.
     * @param iIndex The index for this icon.
     */
    public void addIcon(Object icon, int iIndex);

}
