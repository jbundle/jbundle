/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FtpDirIn extends Object {

    public FtpDirIn() {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        FtpDirIn pgm = new FtpDirIn();
        pgm.run();
    }
    
    public void run()
    {
        try {
            URL url = new URL("ftp://download:donwpp@www.donandann.com/backup/");
            URLConnection urlc = url.openConnection();
            InputStream is = urlc.getInputStream(); // To download
//            OutputStream os = urlc.getOutputStream(); // To upload

            int data = 0;
            
            byte[] b = new byte[100];
            int loc = 0;
            while (data != -1)
            {
                data = is.read();
                if (loc < b.length)
                    b[loc++] = (byte)data;
                System.out.println("data: " + data);
            }
            System.out.println("end: " + new String(b));
            
            is.close();
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
