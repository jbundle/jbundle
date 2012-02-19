/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

import java.awt.Container;

import org.jbundle.thin.base.db.Params;


/**
 * Thin Applet is a concrete implemention of BaseApplet.
 * You can start ThinApplet from the command line and it
 * will run using the initial arguments screen=xxx.
 */
public class ThinApplet extends BaseApplet
{
	private static final long serialVersionUID = 1L;

	/**
     * The initial default screen.
     */
    public static final String INITIAL_SCREEN = ".thin.base.screen.menu.JRemoteMenuScreen";

    /**
     * Default constructor.
     */
    public ThinApplet()
    {
        super();
    }
    /**
     * BaseApplet Class Constructor.
     * This is rarely used, except to make a new window.
     * @param args Arguments in standalone pass-in format.
     */
    public ThinApplet(String[] args)
    {
        this();
        this.init(args);
    }
    /**
     * For Stand-alone initialization.
     * In the overriding app must do one of the following:
     * <pre>
     *  BaseApplet.main(args);  // call this
     *  SApplet applet = new SApplet();     // Create your applet object
     *  applet.init();
     *  applet.start();                         // This calls init(args)
     *      (or)
     *  BaseApplet.main(args);  // call this
     *  SApplet applet = new SApplet(args);     // Create your applet object - this calls init(args)
     *      (or)
     *  SApplet applet = new SApplet();
     *  BaseApplet.main(args);  // call this
     *  </pre>
     * @param args Arguments in standalone pass-in format.
     */
    public static void main(String[] args)
    {
        BaseApplet applet = new ThinApplet();
        BaseApplet.main(args);
        new JBaseFrame("Applet", applet);
    }
    /**
     * The getAppletInfo() method returns a string describing the applet's
     * author, copyright date, or miscellaneous information.
     */
    public String getAppletInfo()
    {
        return "Name: Thin Applet\r\n" +
               "Author: Don Corley\r\n" +
               "E-Mail: don@tourgeek.com";
    }
    /**
     * Add any applet sub-panel(s) now.
     * Usually, you override this, although for a simple screen, just pass a screen=class param.
     * @param parent The parent to add the new screen to.
     */
    public boolean addSubPanels(Container parent)
    {
        String strScreen = this.getProperty(Params.SCREEN);
        if (strScreen == null)
            this.setProperty(Params.SCREEN, INITIAL_SCREEN);
        boolean success = super.addSubPanels(parent);
        if (success)
        	if (strScreen == null)
        		this.getProperties().remove(Params.SCREEN);
        return success;
    }
}
