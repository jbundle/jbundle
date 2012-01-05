/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.crypto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;

public class Keystore {

    public Keystore()
    {
        
    }
    public static final void main(String[] args)
    {
        Keystore pwd = new Keystore();
        pwd.store();
    }
    
    public void store()
    {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            // get user password and file input stream
            
            char[] password = /*getPassword();*/readPasswd(System.in);

            java.io.FileInputStream fis = null;
            try {
                    fis = new java.io.FileInputStream("/usr/local/java/jdk/jre/lib/security/keystore");
                    ks.load(fis, password);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
            
            // get my private key
//            KeyStore.ProtectionParameter = new KeyStore.PasswordProtection(password);
            Enumeration<String> x = ks.aliases();
            String s = "";
            for (String z = x.nextElement(); x.hasMoreElements(); z = x.nextElement())
            {
                s = s + "z";
            }
            System.out.println("Aliases: " + s);
            System.out.println("size: " + ks.size()); 
            
            java.security.KeyStore.PasswordProtection pwd = new KeyStore.PasswordProtection(password);
            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)ks.getEntry("tourgeek", pwd);
            
            Certificate cert = pkEntry.getCertificate();
            System.out.println("cert: " + cert);
            System.out.println("public key: " + cert.getPublicKey());

            PrivateKey myPrivateKey = pkEntry.getPrivateKey();
             System.out.println("key: " + myPrivateKey); 
            System.out.println("algo: " + myPrivateKey.getAlgorithm()); 
            System.out.println("enc: " + myPrivateKey.getEncoded()); 
            System.out.println("format: " + myPrivateKey.getFormat()); 


        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }
    /**
     * Reads user password from given input stream.
     */
    public char[] readPasswd(InputStream in) throws IOException {
        char[] lineBuffer;
        char[] buf;
        int i;

        buf = lineBuffer = new char[128];

        int room = buf.length;
        int offset = 0;
        int c;

loop:   while (true) {
            switch (c = in.read()) {
              case -1: 
              case '\n':
                break loop;

              case '\r':
                int c2 = in.read();
                if ((c2 != '\n') && (c2 != -1)) {
                    if (!(in instanceof PushbackInputStream)) {
                        in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream)in).unread(c2);
                } else 
                    break loop;

              default:
                if (--room < 0) {
                    buf = new char[offset + 128];
                    room = buf.length - offset - 1;
                    System.arraycopy(lineBuffer, 0, buf, 0, offset);
                    Arrays.fill(lineBuffer, ' ');
                    lineBuffer = buf;
                }
                buf[offset++] = (char) c;
                break;
            }
        }

        if (offset == 0) {
            return null;
        }

        char[] ret = new char[offset];
        System.arraycopy(buf, 0, ret, 0, offset);
        Arrays.fill(buf, ' ');

        return ret;
    }
  
}
