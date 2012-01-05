/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FtpOut extends Object {

    public FtpOut() {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        FtpOut pgm = new FtpOut();
        pgm.run();
    }
    
    public void run()
    {
        try {
            
            URL url = new URL("ftp://download:donwpp@www.donandann.com/backup/temp2.txt");
            URLConnection urlc = url.openConnection();
            OutputStream os = urlc.getOutputStream(); // To upload
            
            for (byte b = 1; b < 22 ; b++)
            {
                os.write(b);
                System.out.println("Data: " + b);
            }
            
            os.close();
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
