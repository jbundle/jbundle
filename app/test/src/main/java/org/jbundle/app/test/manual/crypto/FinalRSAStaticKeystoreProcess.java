/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Test RSA public key encryption. 1. Reads public and private key from keystore
 * which is created by executing this command: keytool -genkey -keyalg RSA
 * -keysize 1024 -alias tourgeekrsa -keystore
 * /usr/local/java/jdk/jre/lib/security/keystore
 * 
 * @author don
 * 
 */
public class FinalRSAStaticKeystoreProcess {

    public FinalRSAStaticKeystoreProcess() {

    }

    public static final void main(String[] args) {
        FinalRSAStaticKeystoreProcess pwd = new FinalRSAStaticKeystoreProcess();
        pwd.store();
    }

    public void store() {
        try {
            // ----------Public key Encryption---------------
            RSAPublicKeySpec dhSkipParamSpec = new RSAPublicKeySpec(publicKeyModulus, publicKeyBase);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(dhSkipParamSpec);

            // Create the cipher
            Cipher desCipher = Cipher.getInstance("RSA");

            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // Our cleartext
            byte[] cleartext = "This is just an example".getBytes();

            // Encrypt the cleartext
            byte[] ciphertext = desCipher.doFinal(cleartext);

            
            // ----------Private key Decryption---------------
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            // get user password and file input stream
            System.out.print("password: ");
            System.out.flush();
            char[] password = /* getPassword(); */readPasswd(System.in);

            java.io.FileInputStream fis = null;
            try {
                fis = new java.io.FileInputStream("/usr/local/java/jdk/jre/lib/security/keystore");
                ks.load(fis, password);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }

            // get my private key
            java.security.KeyStore.PasswordProtection pwd = new KeyStore.PasswordProtection(password);
            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("rsa", pwd);
            PrivateKey privateKey = pkEntry.getPrivateKey();

            desCipher = Cipher.getInstance("RSA");
            // Initialize the same cipher for decryption
            desCipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] cleartext1 = desCipher.doFinal(ciphertext);

            System.out.println("result: " + new String(cleartext1));
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads user password from given input stream.
     */
    public char[] readPasswd(InputStream in) throws IOException {
        char[] lineBuffer;
        char[] buf;

        buf = lineBuffer = new char[128];

        int room = buf.length;
        int offset = 0;
        int c;

        loop: while (true) {
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
                    ((PushbackInputStream) in).unread(c2);
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

    private static final BigInteger publicKeyModulus = new BigInteger(
            "90242639829853749672390237687730602469835068946957879215819757950415403219474497386056794085513035666391540835472767141106791638089730690793760981933262309119993395230010972486643432150940900905678345312917068395181911103105877082010079638574407308225389170936611620606089865440154690951722618401112109539323");

    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger publicKeyBase = BigInteger.valueOf(65537);

}
