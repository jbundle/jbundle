/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle;

/**
 * This is a convience class, so users don't have to remember the path to SApplet.
 */
public class Main extends org.jbundle.base.screen.control.swing.SApplet
{
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public Main()
    {
        super();
    }
    /**
     * Start this program.
     * server=rmiserver
     * codebase=webserver
     */
    public static void main(String args[])
    {
        org.jbundle.base.screen.control.swing.SApplet.main(args);
    }

}
