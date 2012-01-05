/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FtpIn extends Object {

    public FtpIn() {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        FtpIn pgm = new FtpIn();
        pgm.run();
    }
    
    public void run()
    {
        try {
            URL url = new URL("ftp://download:donwpp@www.donandann.com/backup/temp.txt");
            URLConnection urlc = url.openConnection();
            InputStream is = urlc.getInputStream(); // To download
//            OutputStream os = urlc.getOutputStream(); // To upload

            int data = 0;
            
            while (data != -1)
            {
                data = is.read();
                System.out.println("data: " + data);
            }
            
            is.close();
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
