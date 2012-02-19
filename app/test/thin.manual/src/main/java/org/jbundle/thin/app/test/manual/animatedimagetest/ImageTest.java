/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.animatedimagetest;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;

public class ImageTest extends Applet
{
    private static final long serialVersionUID = 1L;

      private Image m_image=null;
      
      public void init()
      {
        m_image=getImage(getDocumentBase(), "javacup.gif");
      }

      public void paint(Graphics g)
      {
        g.drawImage(m_image,0,0,this); 
      }

      public boolean imageUpdate( Image img, int flags, int x, int y, 
        int w, int h ) 
      {
                 System.out.println("Image update: flags="+flags+
          " x="+x+" y="+y+" w="+w+" h="+h);
        repaint();
        return true;
      }


}
