/*
 * BlueTheme.java
 *
 * Created on August 23, 2005, 5:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.landf.theme;

import javax.swing.plaf.ColorUIResource;

/**
 *
 * @author don
 */
public class BlueTheme extends CustomTheme {
    
    /**
     * Creates a new instance of BlueTheme
     * Note: MUST only have default constructor!
     */
    public BlueTheme()
    {
        super();
        this.setPrimaryColor(new ColorUIResource(159, 235, 235));
    }

    public String getName()
    {
        return "Blue";
    }

}
