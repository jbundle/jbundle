/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.splash;

/*
 * @(#)Splash.java  2.2  2005-04-03
 *
 * Copyright © 2012-2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is in the public domain.
 */

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JWindow;

/**
 * A Splash program.
 * This program displays a 
 * NOTE: Be careful not to reference or import any other class from here.
 *
 */
public class Splash extends JWindow 
{
	private static final long serialVersionUID = 1L;

    public static final String DEFAULT_SPLASH = "/com/tourapp/thin/base/screen/splash.jpg";   // In same package as this
    public static final String ROOT_PACKAGE = "org.jbundle.";  // Package prefix
    public static final String SPLASH = "splash";
    public static final String MAIN = "applet";

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
        Splash.splash(Splash.class.getResource(splashImage));
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
        super((JFrame)null);
    }
    /**
     * Creates a new instance.
     * @param parent the parent of the window.
     * @param image the splash image.
     */
    private Splash(JFrame parent, Image image) 
    {
        super(parent);
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
        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
        (screenDim.width - imgWidth) / 2,
        (screenDim.height - imgHeight) / 2
        );
        
        // This mouse listener listens for mouse clicks and disposes the splash window.
        MouseAdapter disposeOnClick = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Note: To avoid that method splash hangs, we
                // must set paintCalled to true and call notifyAll.
                // This is necessary because the mouse click may
                // occur before the contents of the window
                // has been painted.
                synchronized(Splash.this) {
                    Splash.this.paintCalled = true;
                    Splash.this.notifyAll();
                }
                dispose();
            }
        };
        addMouseListener(disposeOnClick);
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
    public static void splash(Image image)
    {
        if (instance == null && image != null) {
            JFrame f = new JFrame();

            // Create the splash image
            instance = new Splash(f, image);
            
            // Show the window.
            instance.setVisible(true);
            
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
    }
    /**
     * Open's a splash window using the specified image.
     * @param imageURL The url of the splash image.
     */
    public static void splash(URL imageURL) {
        if (imageURL != null) {
            splash(Toolkit.getDefaultToolkit().createImage(imageURL));
        }
    }
    
    /**
     * Closes the splash window.
     */
    public static void disposeSplash() {
        if (instance != null) {
            instance.getOwner().dispose();
            instance = null;
        }
    }
    
    /**
     * Invokes the main method of the provided class name.
     * @param args the command line arguments
     */
    public static void invokeMain(String className, String[] args) {
        try {
            Class.forName(className).getMethod("main", new Class[] {String[].class}).invoke(null, new Object[] {args});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
