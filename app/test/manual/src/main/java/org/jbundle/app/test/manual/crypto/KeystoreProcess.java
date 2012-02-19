/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class KeystoreProcess {

    public KeystoreProcess()
    {
        
    }
    public static final void main(String[] args)
    {
        KeystoreProcess pwd = new KeystoreProcess();
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
                java.security.KeyStore.PasswordProtection pwd = new KeyStore.PasswordProtection(password);
                KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)ks.getEntry("tourgeek", pwd);
                
                Certificate cert = pkEntry.getCertificate();
                System.out.println("cert: " + cert);
                System.out.println("public key: " + cert.getPublicKey());

                PrivateKey myPrivateKey = pkEntry.getPrivateKey();
                System.out.println("key: " + myPrivateKey); 
                PublicKey publicKey = cert.getPublicKey();
                
                Cipher desCipher;

                // Create the cipher 
                desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

                // Initialize the cipher for encryption
                desCipher.init(Cipher.ENCRYPT_MODE, publicKey);

                // Our cleartext
                byte[] cleartext = "This is just an example".getBytes();

                // Encrypt the cleartext
                byte[] ciphertext = desCipher.doFinal(cleartext);

                desCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                // Initialize the same cipher for decryption
                desCipher.init(Cipher.DECRYPT_MODE, myPrivateKey);

                // Decrypt the ciphertext
                byte[] cleartext1 = desCipher.doFinal(ciphertext);
                
                System.out.println("result: " + new String(cleartext1));
            } catch (InvalidKeyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KeyStoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (BadPaddingException e) {
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
