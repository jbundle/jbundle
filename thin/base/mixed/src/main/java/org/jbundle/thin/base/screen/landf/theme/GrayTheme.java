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
public class GrayTheme extends CustomTheme {
    
    /**
     * Creates a new instance of GrayTheme
     * Note: MUST only have default constructor!
     */
    public GrayTheme()
    {
        super();
        
        primaryColor1 = new ColorUIResource(66, 33, 66);
        primaryColor2 = new ColorUIResource(90, 86, 99);
        primaryColor3 = new ColorUIResource(99, 99, 99);

        secondaryColor1 = new ColorUIResource(0, 0, 0);
        secondaryColor2 = new ColorUIResource(51, 51, 51);
        secondaryColor3 = new ColorUIResource(102, 102, 102);

        blackColor = new ColorUIResource(222, 222, 222);
        whiteColor = new ColorUIResource(0, 0, 0);
    }

    public String getName()
    {
        return "Gray"; 
    }
}
