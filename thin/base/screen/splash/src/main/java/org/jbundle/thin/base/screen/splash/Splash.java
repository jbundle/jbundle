/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.splash;

/*
 * @(#)Splash.java  2.2  2005-04-03
 *
 * Thanks to:
 * Copyright © 2012-2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is in the public domain.
 */

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.jbundle.util.osgi.finder.ClassServiceUtility;

/**
 * A Splash program.
 * This program displays a 
 * NOTE: Be careful not to reference or import any other classes from here.
 */
public class Splash extends JPanel 
{
	private static final long serialVersionUID = 1L;

    public static final String DEFAULT_SPLASH = "org/jbundle/thin/base/screen/splash/Splash.gif";   // In same package as this
    public static final String ROOT_PACKAGE = "org.jbundle.";  // Package prefix
    public static final String SPLASH = "splash";
    public static final String MAIN = "splash.main";

    /**
     * The current instance of the splash window.
     */
    private static Splash instance;
    
    /**
     * The splash image.
     */
    private Image image;
    
    /**
     * Set first time.
     */
    private boolean paintCalled = false;
    
    /**
     * Display splash, and launch app.
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        String splashImage = Splash.getParam(args, SPLASH);
        if ((splashImage == null) || (splashImage.length() == 0))
            splashImage = DEFAULT_SPLASH;
        URL url = Splash.class.getResource(splashImage);
        if (url == null)
            if (ClassServiceUtility.getClassService().getClassFinder(null) != null)
                url = ClassServiceUtility.getClassService().getClassFinder(null).findResourceURL(splashImage, null);
        
        Container container = null;
        Splash.splash(container, url);
        String main = Splash.getParam(args, MAIN);
        if ((main == null) || (main.length() == 0))
            main = ROOT_PACKAGE + "Thin";
        else if (main.charAt(0) == '.')
            main = ROOT_PACKAGE + main.substring(1);
        Splash.invokeMain(main, args);
        Splash.disposeSplash();
    }
    /**
     * Parse the param line and add it to this properties object.
     * (ie., key=value).
     * @properties The properties object to add this params to.
     * @param strParam param line in the format param=value
     */
    public static String getParam(String[] args, String strParam)
    {
        if ((args != null) && (strParam != null))
        {
            for (int i = 0; i < args.length; i++)
            {
                if (args[i] != null)
                {
                    int iIndex = args[i].indexOf('=');
                    if (iIndex != -1)
                    {
                        if (strParam.equalsIgnoreCase(args[i].substring(0, iIndex)))
                            return args[i].substring(iIndex + 1, args[i].length());
                    }
                }
            }
        }
        return null;
    }

    public Splash()
    {
        super();
    }
    /**
     * Creates a new instance.
     * @param parent the parent of the window.
     * @param image the splash image.
     */
    private Splash(Container container, Image image) 
    {
        super();
        
        if (container instanceof JApplet)
            ((JApplet)container).getContentPane().add(this);
        else if (container instanceof JWindow)
            ((JWindow)container).getContentPane().add(this);
        else
            container.add(this);
        
        this.image = image;

        // Load the image
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie){}
        
        // Center the window on the screen
        int imgWidth = image.getWidth(this);
        int imgHeight = image.getHeight(this);

        container.setSize(imgWidth, imgHeight);
        this.setSize(imgWidth, imgHeight);
    }
    
    /**
     * Updates the display area of the window.
     */
    public void update(Graphics g)
    {
        paint(g);
    }
    /**
     * Paints the image on the window.
     */
    public void paint(Graphics g)
    {
        g.drawImage(image, 0, 0, this);
        
        // Notify method splash that the window has been painted.
        if (! paintCalled) {
            paintCalled = true;
            synchronized (this)
            {
                notifyAll();
            }
        }
    }
    
    /**
     * Open's a splash window using the specified image.
     * @param image The splash image.
     */
    public static Container splash(Container container, Image image)
    {
        if (instance == null && image != null) {
            Window frame = null;
            int heading = 0;
            if (container == null)
            {
                frame = new JWindow();
                container = new JApplet();  // Just to be consistent
                ((JWindow)frame).getContentPane().add(container);
            }
            else
            {
                frame = new JFrame();
                ((JFrame)frame).getContentPane().add(container);
                if (container instanceof JApplet)
                    heading = 50;   // TODO There must be a way to get this
            }                

            // Create the splash image
            instance = new Splash(container, image);
            
            Dimension dimension = container.getSize();
            frame.setSize(dimension.width, dimension.height + heading);
            Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(
            (screenDim.width - dimension.width) / 2,
            (screenDim.height - dimension.height) / 2
            );

            // Show the window.
            frame.setVisible(true);
            
            // Wait until its paint method has been called at least once.
            if (! EventQueue.isDispatchThread() 
            && Runtime.getRuntime().availableProcessors() == 1) {
                synchronized (instance) {
                    while (! instance.paintCalled) {
                        try { instance.wait(); } catch (InterruptedException e) {}
                    }
                }
            }
        }
        return container;
    }
    /**
     * Open's a splash window using the specified image.
     * @param imageURL The url of the splash image.
     */
    public static Container splash(Container container, URL imageURL) {
        if (imageURL != null) {
            return splash(container, Toolkit.getDefaultToolkit().createImage(imageURL));
        }
        return null;
    }
    
    /**
     * Closes the splash window.
     */
    public static void disposeSplash() {
        if (instance != null) {
            Container container = instance;
            while ((container = container.getParent()) != null)
            {
                if (container instanceof Window)
                    ((Window)container).dispose();
            }
            instance = null;
        }
    }
    
    /**
     * Invokes the main method of the provided class name.
     * @param args the command line arguments
     */
    public static void invokeMain(String className, String[] args) {
        try {
            Class<?> clazz = null;
            if (ClassServiceUtility.getClassService().getClassFinder(null) != null)
                clazz = ClassServiceUtility.getClassService().getClassFinder(null).findClass(className, null);
            if (clazz == null)
                clazz = Class.forName(className);

            Method method = clazz.getMethod("main", PARAMS);
            Object[] objArgs = new Object[] {args};
            method.invoke(null, objArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static final Class<?>[] PARAMS = new Class[] {String[].class};
}
