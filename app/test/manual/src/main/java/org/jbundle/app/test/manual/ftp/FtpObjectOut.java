/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.ftp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FtpObjectOut extends Object {

    public FtpObjectOut() {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        FtpObjectOut pgm = new FtpObjectOut();
        pgm.run();
    }
    
    public void run()
    {
        try {
            
            URL url = new URL("ftp://download:donwpp@www.donandann.com/backup/temp5.txt");
            URLConnection urlc = url.openConnection();
            OutputStream os = urlc.getOutputStream(); // To upload
            ObjectOutputStream writer = new ObjectOutputStream(os);            
            
            Object string = "test";
            for (byte b = 1; b < 5 ; b++)
            {
                writer.writeObject(string);
                System.out.println("Data: " + b);
            }
            
            writer.close();
//            os.close();
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
