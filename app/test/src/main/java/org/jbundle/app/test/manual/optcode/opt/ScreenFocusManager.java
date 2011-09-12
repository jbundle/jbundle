/*
 * @(#)ScreenFocusManager.java  1.6 98/01/30
 * 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.optcode.opt;

/**
 * Default screen focus manager implementation
 *
 * @version 1.0.0
 * @author Arnaud Weber
 */
import java.awt.Component;
import java.awt.Container;

import javax.swing.DefaultFocusManager;

public class ScreenFocusManager extends DefaultFocusManager
{

    /** Return true if <code>a</code> should be before <code>b</code> in the
     * "tab" order. Override this method if you want to change the automatic
     * "tab" order. 
     * The default implementation will order tab to give a left to right, top
     * down order. Override this method if another order is required.
     */
    public boolean compareTabOrder(Component a,Component b) {
        Container parent = a.getParent();
        if ((parent == null) || (b.getParent() != parent))
            return super.compareTabOrder(a, b);
        int iOrderA = this.getOrderInParent(a);
        int iOrderB = this.getOrderInParent(b);
        return (iOrderA < iOrderB);
    }
    private int getOrderInParent(Component component)
    {
        Component[] rgiComponents = component.getParent().getComponents();
        for (int i = 0; i < rgiComponents.length; i++)
        {
            if (component == rgiComponents[i])
                return i;
        }
        return -1;  // Never
    }
}
